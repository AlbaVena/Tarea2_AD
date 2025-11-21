package principal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controlador.EspectaculosService;
import controlador.PropertiesService;
import controlador.UsuariosService;
import entidades.Artista;
import entidades.Coordinador;
import entidades.Credenciales;
import entidades.Especialidad;
import entidades.Espectaculo;
import entidades.Numero;
import entidades.Perfil;
import entidades.Persona;
import entidades.ProgramProperties;
import entidadesDAO.PersonaDAO;

public class Principal {

	static Scanner leer = new Scanner(System.in);

	// TODO eliminar usuariosservice
	static UsuariosService usuariosService = null;
	static EspectaculosService espectaculosService = null;
	static PropertiesService propertiesService = null;

	static PersonaDAO PDAO = null;

	static Map<String, String> paises = null;

	public static void main(String[] args) {

		// Comenzamos configurando el programa

		propertiesService = new PropertiesService();
		usuariosService = new UsuariosService();
		espectaculosService = new EspectaculosService();
		paises = cargarPaises();

		PDAO = new PersonaDAO();

		System.out.println("**Bienvenido al Circo**");

		// MENU INVITADO
		/**
		 * 1. ver espectaculos 2. Log IN 3. Salir
		 */
		int opcion = -1;
		Boolean valid = false;
		do {
			System.out.println("Menu Invitado");
			System.out
					.println("Elige una opcion: \n\t1. Ver espectaculos\n\t2. "
							+ "Log IN\n\t3. Salir");

			do {
				try {

					opcion = leer.nextInt();
					leer.nextLine();
					valid = true;
				} catch (Exception e) {
					System.out.println("debes introducir un numero");
					leer.nextLine();
				}
			} while (!valid);

			switch (opcion) {
			case 1:
				espectaculosService.cargarEspectaculos();
				espectaculosService.mostrarEspectaculos();
				break;
			case 2:
				String usuario, password;
				Persona usuarioLogueado = null;

				do {
					System.out.println("Introduce tu nombre de usuario");
					usuario = leer.nextLine();
				} while (usuario == null);

				do {
					System.out.println("Introduce tu contraseña");
					password = leer.nextLine();
				} while (password == null);

				Persona usuarioIntento = usuariosService.login(usuario,
						password);

				if (usuarioIntento != null) {
					switch (usuarioIntento.getPerfil()) {
					case ARTISTA:
						menuArtista();
						break;
					case COORDINACION:
						menuCoordinacion();
						break;
					case ADMIN:
						menuAdmin();
						break;
					default:
						System.out.println("Perfil no reconocido");
					}

				} else
					System.out.println("usuario o contraseña incorrecto");

				break;
			case 3:
				System.out.println("Cerrando el programa...");
				break;
			default:
				System.out.println("No has introducido una opcion valida."
						+ " Por favor intentalo de nuevo.");

			}
		} while (opcion != 3);

	}

	/**
	 * METODOS:
	 */

	private static Map<String, String> cargarPaises() {
		Map<String, String> paises = new HashMap<String, String>();

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document documento = builder.parse(ProgramProperties.paises);
			documento.getDocumentElement().normalize();

			NodeList listaPaises = documento.getElementsByTagName("pais"); // en
																			// la
																			// lista
																			// los
																			// elementos
																			// con
																			// etiqueta
																			// "pais"
			for (int i = 0; i < listaPaises.getLength(); i++) {
				Node nodo = listaPaises.item(i); // me devuelve el nodo en
													// posicion i

				if (nodo.getNodeType() == Node.ELEMENT_NODE) { // devuelve un
																// entero. solo
																// los elementos
																// tienen
																// etiquetas
																// hijo
					Element elemento = (Element) nodo; // lo covertimos a
														// element para usar los
														// metodos <PAIS>

					String id = getNodo("id", elemento);
					String nombre = getNodo("nombre", elemento);

					paises.put(id, nombre);

				}

			}
		}

		catch (Exception e) {
			System.out.println(
					"Ha ocurrido algun problema al leer el archivo XML.");
		}

		return paises;
	}

	private static String getNodo(String etiqueta, Element elem) { // "etiqueta"
																	// concreta
		NodeList nodo = elem.getElementsByTagName(etiqueta).item(0)
				.getChildNodes(); // busca todas las qtiquetas hijas
									// con el nombre de la etiqueta
		// devuelve los nodos hijos
		Node valorNodo = nodo.item(0); // primer hijo ID
		return valorNodo.getNodeValue(); // el nodo de TEXTO (valor real) NOMBRE
	}

	private static boolean comprobarEspectaculoRepetido(String espNombre) {
		for (Espectaculo e : espectaculosService.getEspectaculos()) {
			if (e.getNombre().equals(espNombre))
				return true;
		}
		return false;
	}

	public static Espectaculo crearEspectaculo() {
		String nombre = null, nombrePrueba = null;
		Boolean fechas = false;
		LocalDate fechaIni = null, fechaFin = null;
		Set<Numero> numeros = new HashSet<>();
		long numCoor = -1;
		Espectaculo nuevoEspectaculo;

		if (usuariosService.getSesion()
				.getPerfilActual() == Perfil.COORDINACION) {
			do {
				System.out.println("introduce el nombre del espectaculo"
						+ "\n(debe tener un maximo de 25 caracteres)");
				nombrePrueba = leer.nextLine();
				if (nombrePrueba.length() <= 25
						&& !comprobarEspectaculoRepetido(nombrePrueba)) {
					nombre = nombrePrueba;
				} else {
					System.out.println(
							"ese nombre es demasiado largo o ya existe.");
					nombre = null;
				}
			} while (nombre == null);
			try {
				do {
					System.out.println(
							"introduce la fecha de inicio (formato yyyy-mm-dd)");
					String fecha1 = leer.nextLine();
					fechaIni = LocalDate.parse(fecha1);

					System.out.println(
							"introduce la fecha de fin\n(recuerda que no puede pasar mas de 1 año entre fechas)");
					String fecha2 = leer.nextLine();
					fechaFin = LocalDate.parse(fecha2);

					if (fechaFin.isBefore(fechaIni)) {
						System.out.println(
								"La fecha final no puede ser anterior a la inicial.");
						fechas = false;
					} else if (fechaFin.isAfter(fechaIni.plusYears(1))) {
						System.out.println(
								"El periodo no puede ser superior a 1 año.");
						fechas = false;
					} else {
						fechas = true;
					}
				} while (fechas == false);
			} catch (DateTimeParseException e) {
				System.out.println(
						"fFormato de fecha incorrecto. Usa yyyy-mm-dd.");
			}
			Numero numero1 = new Numero("noche magica", 15);
			Numero numero2 = new Numero("sonidos del aire", 12);
			Numero numero3 = new Numero("baile del fuego", 18);
			numeros.add(numero1);
			numeros.add(numero2);
			numeros.add(numero3);
			long id = espectaculosService.getEspectaculos().size() + 1;
			Coordinador coordinadorActual = (Coordinador) usuariosService
					.getSesion().getUsuActual();
			nuevoEspectaculo = new Espectaculo(id, nombre, fechaIni, fechaFin,
					numeros, coordinadorActual);
			System.out.println("Espectaculo creado con exito.");
		} else {
			do {
				System.out.println("introduce el nombre del espectaculo"
						+ "\n(debe tener un maximo de 25 caracteres)");
				nombrePrueba = leer.nextLine();
				if (nombrePrueba.length() <= 25
						&& !comprobarEspectaculoRepetido(nombrePrueba)) {
					nombre = nombrePrueba;
				} else {
					System.out.println(
							"ese nombre es demasiado largo o ya existe.");
					nombre = null;
				}
			} while (nombre == null);
			do {
				try {
					System.out.println(
							"introduce la fecha de inicio (formato yyyy-mm-dd)");
					String fecha1 = leer.nextLine();
					fechaIni = LocalDate.parse(fecha1);

					System.out.println(
							"introduce la fecha de fin\n(recuerda que no puede pasar mas de 1 año entre fechas)");
					String fecha2 = leer.nextLine();
					fechaFin = LocalDate.parse(fecha2);

					if (fechaFin.isBefore(fechaIni)) {
						System.out.println(
								"La fecha final no puede ser anterior a la inicial.");
						fechas = false;
					} else if (fechaFin.isAfter(fechaIni.plusYears(1))) {
						System.out.println(
								"El periodo no puede ser superior a 1 año.");
						fechas = false;
					} else {
						fechas = true;
					}
				} catch (DateTimeParseException e) {
					System.out.println(
							"Formato de fecha incorrecto. Usa yyyy-mm-dd.");
				}
			} while (fechas == false);

			Numero numero1 = new Numero("noche magica", 15);
			Numero numero2 = new Numero("sonidos del aire", 12);
			Numero numero3 = new Numero("baile del fuego", 18);
			numeros.add(numero1);
			numeros.add(numero2);
			numeros.add(numero3);
			long idEspectaculo = espectaculosService.getEspectaculos().size()
					+ 1;

			System.out.println(
					"Elige un coordinador de los siguientes escribiendo su numero:");
			Boolean elegido = false;
			Coordinador coordinadorElegido = null;
			Boolean coordinadorEncontrado = false;
			do {
				for (Persona c : usuariosService.getCredencialesSistema()) {
					if (c.getPerfil() == Perfil.COORDINACION) {
						System.out.println(c.getId() + " - " + c.getNombre());
					}
				}
				Boolean v = false;
				do {
					try {
						numCoor = leer.nextLong();
						leer.nextLine();
						v = true;
					} catch (Exception e) {
						System.out.println("debes introducir un numero");
						leer.nextLine();
					}
				} while (!v);

				for (Persona c : usuariosService.getCredencialesSistema()) {
					if (c.getPerfil() == Perfil.COORDINACION
							&& c.getId() == numCoor) {
						if (c instanceof Coordinador) {
							coordinadorElegido = new Coordinador(c.getId(),
									c.getEmail(), nombre, c.getNacionalidad(),
									c.getCredenciales());
							coordinadorEncontrado = true;
							elegido = true;
							break;
						}
					}

				}
				if (!coordinadorEncontrado) {
					System.out.println("no se ha encontrado ese coordinador.");
				}
				System.out.println("Espectaculo creado.");
			} while (!elegido);
			nuevoEspectaculo = new Espectaculo(idEspectaculo, nombre, fechaIni,
					fechaFin, numeros, coordinadorElegido);
		}

		return nuevoEspectaculo;
	}

	// MENU COORDINACION
	/**
	 * 1. ver espectaculos 2. gestionar espectaculos 2.1 crear-modificar
	 * espectaculo 2.2 crear-modificar numero 2.3 asignar artistas 3. Log OUT
	 */
	public static void menuCoordinacion() {
		int opcion = -1;

		System.out.println("Menu Coordinacion");
		do {
			System.out.println(
					"Menu COORDINACION\nElige una opcion: \n\t1. Ver espectaculos\n\t.2 "
							+ "Crear o Modificar espectaculos\n\t3. Log OUT\n\t4. Salir al meu anterior");
			Boolean v = false;
			do {
				try {
					opcion = leer.nextInt();
					leer.nextLine();
					v = true;
				} catch (Exception e) {
					System.out.println("debes introducir un numero");
					leer.nextLine();
				}
			} while (!v);
			;
			switch (opcion) {
			case 1:
				espectaculosService.mostrarEspectaculos();
				break;
			case 2:
				espectaculosService.guardarEspectaculo(crearEspectaculo());
				break;
			case 3:
				usuariosService.logOut();
				break;
			case 4:
				usuariosService.logOut();
				System.out.println("Saliendo al menu anterior...");
				break;
			default:
				System.out.println("No has introducido una opcion valida."
						+ " Por favor intentalo de nuevo.");
			}

		} while (opcion != 4);
	}

	// MENU ARTISTA
	/**
	 * 1. ver tu ficha 2. ver espectaculos 3. Log OUT
	 */
	public static void menuArtista() {
		int opcion = -1;

		System.out.println("Menu Artista");
		do {
			System.out
					.println("Elige una opcion: \n\t1. Ver tu ficha\n\t2. Ver "
							+ "espectaculos\n\t3. Log OUT");
			Boolean v = false;
			do {
				try {
					opcion = leer.nextInt();
					leer.nextLine();
					v = true;
				} catch (Exception e) {
					System.out.println("debes introducir un numero");
					leer.nextLine();
				}
			} while (!v);

			switch (opcion) {
			case 1:
				usuariosService.mostrarFicha();

				break;
			case 2:
				espectaculosService.mostrarEspectaculos();
				break;
			case 3:
				usuariosService.logOut();

				break;
			default:
				System.out.println("No has introducido una opcion valida."
						+ " Por favor intentalo de nuevo.");
			}

		} while (opcion != 3);
	}

	// MENU ADMIN
	/**
	 * 1. ver espectaculos 2. gestionar espectaculos 2.1 crear-modificar
	 * espectaculo 2.2 crear-modificar numero 2.3 asignar artistas 3. gestionar
	 * personas y credenciales 3.1 registrar persona 3.2 asignar perfil y
	 * credenciales 3.3 gestionar datos artista-coordinador 4. Log OUT
	 */
	public static void menuAdmin() {
		int opcion = -1;

		System.out.println("Menu Administrador");
		do {
			System.out.println("Elige una opcion: \n\t1. Ver espectaculos"
					+ "\n\t2. Gestionar espectaculos"
					+ "\n\t3. Gestionar personas y credenciales"
					+ "\n\t4. Log OUT" + "\n\t5. Salir al menu anterior");

			Boolean validado = false;
			do {
				try {
					opcion = leer.nextInt();
					leer.nextLine();
					validado = true;
				} catch (Exception e) {
					System.out.println("debes introducir un numero");
					leer.nextLine();
				}
			} while (!validado);

			switch (opcion) {
			case 1:
				espectaculosService.mostrarEspectaculos();

				break;
			case 2:
				gestionarEscpectaculos();
				break;

			case 3:
				gestionarPersonas();
				break;
			case 4:
				usuariosService.logOut();
				break;
			case 5:
				usuariosService.logOut();
				System.out.println("Saliendo al menu anterior...");
				break;
			default:
				System.out.println("No has introducido una opcion valida."
						+ " Por favor intentalo de nuevo.");
			}
		} while (opcion < 4);
	}

	/**
	 * MENU gestion personas
	 */

	public static void gestionarPersonas() {
		int opcion2 = -1;
		do {
			System.out.println("Que deseas hacer?");
			System.out.println("\t1. Registrar persona\n\t2. "
					+ "Gestionar datos artista o coordinador\n\t3. Salir al menu anterior");

			Boolean validado = false;
			do {
				try {
					opcion2 = leer.nextInt();
					leer.nextLine();
					validado = true;
				} catch (Exception e) {
					System.out.println("debes introducir un numero");
					leer.nextLine();
				}
			} while (!validado);

			switch (opcion2) {
			case 1:

				usuariosService.crearPersona(registrarPersona());
				break;
			case 2:

				modificarPersona();

				break;
			case 3:
				System.out.println("Saliendo al menu anterior...");
				break;

			default:
				System.out.println("no has introducido una opcion valida.");
			}

		} while (opcion2 != 3);
	}

	public static void gestionarEscpectaculos() {
		int opcion2 = -1;
		do {
			System.out.println("Que deseas hacer?");
			System.out.println("\t1. Crear o modificar un espectaculo\n\t2. "
					+ "Crear o modificar un numero\n\t3. "
					+ "Asignar artistas\n\t4. Salir al menu anterior");
			Boolean v = false;
			do {
				try {
					opcion2 = leer.nextInt();
					leer.nextLine();
					v = true;
				} catch (Exception e) {
					System.out.println("debes introducir un numero");
					leer.nextLine();
				}
			} while (!v);

			switch (opcion2) {

			case 1:
				espectaculosService.guardarEspectaculo(crearEspectaculo());

				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				System.out.println("Saliendo al menu anterior...");
				break;
			default:
				System.out.println("no has introducido una opcion valida.");
			}
		} while (opcion2 != 4);
	}

	// ESTE METODO QUEDA EN VISTA PORQUE ESTA COMPUESTO POR system.out.println
	// PRINCIPALMENTE
	/**
	 * pide todos los datos que componen una persona separados por categorias
	 * 1.datos personales 2.datos profesionales 3.datos de credenciales
	 * 
	 * @return Persona completa
	 */
	// registrar persona nueva
	public static Persona registrarPersona() {
		Persona resultadoLogin = null;
		String email, nombre, nacionalidad;
		String nombreUsuario = null, passUsuario = null;
		Perfil perfilUsu = null;
		Boolean senior = false;
		String apodo = null;
		LocalDate fecha = null;
		Set<Especialidad> especialidadesUsu = new HashSet<>();

		/**
		 * DATOS PERSONALES
		 */
		System.out.println("introduce un email");
		email = leer.nextLine();
		if (!usuariosService.comprobarEmail(email)) {
			System.out.println("Ese email ya esta registrado");
			return null;
		}
		System.out.println("introduce el nombre completo de la persona");
		nombre = leer.nextLine();

		boolean nac = false;
		do {
			System.out.println("introduce el id del pais elegido");
			for (Entry<String, String> entrada : paises.entrySet()) {
				System.out.println(entrada);
			}
			nacionalidad = leer.nextLine().toUpperCase();
			if (paises.containsKey(nacionalidad)) {
				nacionalidad = paises.get(nacionalidad);
				nac = true;
			} else {
				System.out.println("Ese pais no se encuentra");
			}

		} while (!nac);

		/*
		 * DATOS PROFESIONALES
		 */
		int num = -1;
		boolean validado = false;
		do {
			System.out.println("El usuario es Coordinador (1) o Artista (2)?");

			Boolean valido = false;
			do {
				try {
					num = leer.nextInt();
					leer.nextLine();
					valido = true;
				} catch (Exception e) {
					System.out.println("debes introducir un numero");
					leer.nextLine();
				}
			} while (!valido);
			switch (num) {
			case 1:
				perfilUsu = Perfil.COORDINACION;
				int num2 = 0;
				System.out.println("El coordinador es senior? (1- si , 2- no)");
				Boolean valido1 = false;
				do {
					try {
						num2 = leer.nextInt();
						leer.nextLine();
						valido1 = true;
					} catch (Exception e) {
						System.out.println("debes introducir un numero");
						leer.nextLine();
					}
				} while (!valido1);

				switch (num2) {
				case 1:
					senior = true;
					System.out.println(
							"desde que fecha es senior? (formato yyyy-mm-dd)");
					fecha = LocalDate.parse(leer.nextLine());
					break;
				case 2:
					senior = false;
					System.out.println("informacion senior guardada.");
					break;
				default:
					System.out.println("no has elegido una opcion valida");
					break;
				}
				break;

			case 2:

				perfilUsu = Perfil.ARTISTA;
				System.out.println("el artista tiene apodo? (1-si , 2-no)");
				Boolean valido3 = false;
				int num3 = -1;
				do {
					try {
						num3 = leer.nextInt();
						leer.nextLine();
						valido3 = true;
					} catch (Exception e) {
						System.out.println("debes introducir un numero");
						leer.nextLine();
					}
				} while (!valido3);

				switch (num3) {
				case 1:
					System.out.println("cual es su apodo?");
					apodo = leer.nextLine().trim().toLowerCase();
					break;
				case 2:
					apodo = null;
					break;
				default:
					System.out.println("no has elegido una opcion valida");
					break;
				}
				int i = 1;
				System.out.println(
						"indica los numeros de sus especialidades separados por comas: ");
				for (Especialidad e : Especialidad.values()) {
					System.out.println(i + "-" + e);
					i++;
				}
				String[] seleccion = leer.nextLine().split(",");

				for (String s : seleccion) {
					Boolean b = false;
					do {
						try {
							int elegida = Integer.parseInt(s.trim());
							b = true;
							switch (elegida) {
							case 1 ->
								especialidadesUsu.add(Especialidad.ACROBACIA);
							case 2 -> especialidadesUsu.add(Especialidad.HUMOR);
							case 3 -> especialidadesUsu.add(Especialidad.MAGIA);
							case 4 -> especialidadesUsu
									.add(Especialidad.EQUILIBRISMO);
							case 5 ->
								especialidadesUsu.add(Especialidad.MALABARISMO);
							default -> System.out.println(
									"Has introducido una opcion invalida");
							}
						} catch (NumberFormatException e) {
							System.out.println("debes introducir numeros");
							leer.nextLine();
						}
					} while (!b);

				}

				break;
			default:
				System.out.println("La opcion elegida no es valida");
				break;
			}
			validado = true;
		} while (!validado);

		/**
		 * DATOS DE CREDENCIALES
		 */

		do {
			System.out.println(
					"introduce el nombre de usuario (ten en cuenta que "
							+ "no admitira letras con tildes o dieresis, ni espacios en blanco)");
			String cadena = leer.nextLine().trim();

			if (cadena.matches("^[a-zA-Z_-]{3,}$")) {
				nombreUsuario = cadena.toLowerCase();
				if (cadena.equals("admin")) {
					System.out.println("Ese nombre de usuario está reservado.");
					nombreUsuario = null;
				}
			} else
				System.out.println("ese nombre de usuario no es valido");
		} while (nombreUsuario == null);

		do {
			System.out
					.println("por ultimo introduce una contraseña valida (debe"
							+ " tener mas de 2 caracteres, y ningun espacio en blanco");
			String pass = leer.nextLine();
			if (pass.matches("^[^|\\s]{3,}$")) {
				passUsuario = pass;
			} else
				System.out.println("contraseña no valida");
		} while (passUsuario == null);

		Credenciales credenciales = new Credenciales(nombreUsuario, passUsuario,
				perfilUsu);

		if (perfilUsu == Perfil.ARTISTA) {
			resultadoLogin = new Artista(-1, email, nombre, nacionalidad,
					credenciales, -1, apodo, especialidadesUsu, null);
		} else if (perfilUsu == Perfil.COORDINACION) {
			resultadoLogin = new Coordinador(-1, email, nombre, nacionalidad,
					credenciales, -1, senior, fecha, null);
		}

		return resultadoLogin;
	}

	public static String modificarPersona() {

		String resultado = "No se ha modificado ningun dato.";
		int opcion = -1, cambio = -1;
		;
		String nuevoNombre, nuevoEmail, nuevaNac;

		for (Persona p : usuariosService.getCredencialesSistema()) {
			System.out.println("ID: " + p.getId() + " - " + p.getNombre() + ", "
					+ p.getPerfil());
		}

		System.out.println(
				"Indica que usuario se va a modificar (escribe su id)");
		opcion = leer.nextInt();
		leer.nextLine();

		for (Persona p : PDAO.getPersonas()) {
			if (p.getId() == opcion) {
				System.out.println(
						"¿Que quieres cambiar? \n\t1. nombre\\n\\t2. email\\n\\t3. nacionalidad");
				cambio = leer.nextInt();
				leer.nextLine();

				switch (cambio) {
				case 1:
					System.out.println("Introduce el nuevo nombre completo");
					nuevoNombre = leer.nextLine();
					p.setNombre(resultado);
					resultado = "Se ha modificado el nombre de ID " + p.getId();
					break;
				case 2:
					System.out.println("Introduce el nuevo email)");
					nuevoEmail = leer.nextLine();
					if (!usuariosService.comprobarEmail(nuevoEmail)) {
						System.out.println("Ese email ya esta registrado");
						break;
					} else {
						p.setEmail(nuevoEmail);
						resultado = "Se ha modificado el email de ID "
								+ p.getId();
					}
					break;
				case 3:
					boolean nac = false;
					do {
						System.out.println(
								"introduce el id del pais al que cambia");
						for (Entry<String, String> entrada : paises
								.entrySet()) {
							System.out.println(entrada);
						}
						nuevaNac = leer.nextLine().toUpperCase();
						if (paises.containsKey(nuevaNac)) {
							nuevaNac = paises.get(nuevaNac);
							nac = true;
							p.setNacionalidad(nuevaNac);
						} else {
							System.out.println("Ese pais no se encuentra");
						}
					} while (!nac);
					resultado = "Se ha modificado la nacionalidad de ID "
							+ p.getId();
					break;
				default:
					System.out.println("Esa no fue una opcion valida");
				}

			}
		}

		return resultado;

	}

	public static String modificarPersonaActualizado() {
		String resultado = null;
		System.out.println("Indica el id de la persona que va a modificar");
		ArrayList<Persona> personas = PDAO.getPersonas();
		String dato = null;
		for (Persona p : personas) {
			System.out.println("ID: " + p.getId() + ", " + p.getNombre() + " - "
					+ p.getPerfil());
		}

		long idElegido = leer.nextLong();
		leer.nextLine();

		if (!personas.contains(idElegido)) {
			return "No ha elegido un id valido";
		}

		Persona personaAModificar = PDAO.getPersonaId(idElegido);
		if (personaAModificar == null) {
			return "no se encontro la persona";
		}

		// modificacion
		// EMAIL
		boolean modificado = false;
		System.out.println(
				"\t--Datos de " + personaAModificar.getNombre() + "--");
		System.out.println("Email actual: [" + personaAModificar.getEmail()
				+ "] Introduce el nuevo dato o deja vacio para continuar.");
		dato = leer.nextLine().trim();
		if (!dato.isEmpty()) {
			if (usuariosService.comprobarEmail(dato)) {
				personaAModificar.setEmail(dato);
				modificado = true;
				dato = null;
				System.out.println("Datos actualizados.");
			} else {
				System.out.println("Ese email ya esta registrado");
			}
		}

		// NOMBRE
		System.out.println("Nombre actual: [ " + personaAModificar.getNombre()
				+ " ] Introduce el nuevo dato o deja vacio para continuar.");
		dato = leer.nextLine().trim();
		if (!dato.isEmpty()) {
			personaAModificar.setNombre(dato);
			modificado = true;
			dato = null;
			System.out.println("Datos actualizados.");
		}

		// nacionalidad
		String nuevaNac = null;
		boolean nacEncontrada = false;
		System.out.println(
				"Nacionalidad actual: [ " + personaAModificar.getNacionalidad()
						+ " ] Quieres cambiarla? (s/n)");
		dato = leer.nextLine().trim();
		if (dato.equalsIgnoreCase("s")) {
			do {
				System.out.println("Introduce el ID del pais elegido:");

				for (Entry<String, String> entrada : paises.entrySet()) {
					System.out.println("ID: " + entrada.getKey() + " - "
							+ entrada.getValue());
				}
				String pais = leer.nextLine().toUpperCase().trim();
				if (paises.containsKey(pais)) {
					nuevaNac = paises.get(pais);
					nacEncontrada = true;
				} else {
					System.out.println("No se encuentra ese pais");
				}

			} while (nacEncontrada == false);

			personaAModificar.setNacionalidad(nuevaNac);
			modificado = true;
			dato = null;
			System.out.println("Datos actualizados.");

			// datos artista

			if (personaAModificar instanceof Artista) {

				Artista artistaAModificar = (Artista) personaAModificar;

				// apodo
				System.out.println("Apodo actual: [ "
						+ artistaAModificar.getApodo()
						+ " ] Introduce el nuevo dato o deja vacio para continuar.?");
				dato = leer.nextLine().trim();
				if (!dato.isEmpty()) {
					artistaAModificar.setApodo(dato);
					modificado = true;
					dato = null;
					System.out.println("Datos actualizados.");
				}

				// Especialidades
				System.out.println("Especialidades del artista: "
						+ artistaAModificar.getEspecialidades());
				System.out
						.println("Quieres actualizar la lista COMPLETA? (s/n)");

				dato = leer.nextLine().trim();
				if (dato.equalsIgnoreCase("s")) {

					Set<Especialidad> nuevasEspec = new HashSet<>();
					int num = 1;

					System.out.println(
							"Indica el conjunto de las especialidades separadas por comas (ej: 1,3,4)");
					for (Especialidad e : Especialidad.values()) {
						System.out.println(num + "-" + e);
						num++;
					}
					String[] seleccion = leer.nextLine().split(",");

					for (String s : seleccion) {

						int elegida = Integer.parseInt(s.trim());
						Especialidad especialidadAgregada = null;

						switch (elegida) {
						case 1:
							especialidadAgregada = Especialidad.ACROBACIA;
							break;
						case 2:
							especialidadAgregada = Especialidad.HUMOR;
							break;
						case 3:
							especialidadAgregada = Especialidad.MAGIA;
							break;
						case 4:
							especialidadAgregada = Especialidad.EQUILIBRISMO;
							break;
						case 5:
							especialidadAgregada = Especialidad.MALABARISMO;
							break;
						default:
							System.out.println("Opción inválida");
						}
						if (especialidadAgregada != null) {
							nuevasEspec.add(especialidadAgregada);

						}
					}
					if (!nuevasEspec.isEmpty()) {
						artistaAModificar.setEspecialidades(nuevasEspec);
						modificado = true;
						dato = null;
						System.out.println("Datos actualizados.");
					}
					// TODO enviar a la BD
					PDAO.modificarArtista(artistaAModificar);

				}
			}

			// datos coordinador
			else if (personaAModificar instanceof Coordinador) {

				Coordinador coordinadorAModificar = (Coordinador) personaAModificar;
				if (coordinadorAModificar.isSenior()) {

					//senior y fecha
					System.out.println(coordinadorAModificar.getNombre()
							+ " es Coordinador Senior desde "
							+ coordinadorAModificar.getFechasenior()
							+ ". Esto no se puede modificar.");

				} else {
					System.out.println("Quieres hacer Senior a "
							+ coordinadorAModificar.getNombre() + " ? (s/n)");
					dato = leer.nextLine().trim();
					if (dato.equalsIgnoreCase("s")) {
						boolean fechaEncontrada = false;
						do {
							System.out.println(
									"Introduce la fecha desde que es Senior (formato yyyy-mm-dd)");
							try {
							coordinadorAModificar.setFechasenior(LocalDate.parse(leer.nextLine()));
							coordinadorAModificar.setSenior(true);
							System.out.println(coordinadorAModificar.getNombre()+" es Coordinador Senior desde el "+coordinadorAModificar.getFechasenior());
							modificado = true;
							fechaEncontrada = true;
							} catch (java.time.format.DateTimeParseException e) {
								System.out.println("Formato de fecha incorrecto.");
							}
							
						} while (fechaEncontrada == false);
					}
					dato = null;

				}

			}
		}

		return resultado;
	}

}
