package principal;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import entidades.Persona;
import entidades.Artista;
import entidades.Coordinador;
import entidades.Credenciales;
import entidades.Especialidad;
import entidades.Espectaculo;
import entidades.Numero;
import entidades.Perfil;
import entidades.ProgramProperties;
import entidades.Sesion;

public class Principal {

	static Scanner leer = new Scanner(System.in);

	static ArrayList<Persona> credencialesSistema = null;
	static ArrayList<Espectaculo> espectaculos = null;
	static Map<String, String> paises = null;
	static Sesion actual = new Sesion();

	public static void main(String[] args) {

		// Comenzamos configurando el programa

		cargarProperties();
		credencialesSistema = cargarCredenciales();
		espectaculos = cargarEspectaculos();
		paises = cargarPaises();

		System.out.println("**Bienvenido al Circo**");

		// MENU INVITADO
		/**
		 * 1. ver espectaculos 2. Log IN 3. Salir
		 */
		int opcion = -1;
		Boolean v = false;
		do {
			mostrarMenuSesion(actual);
			System.out.println("Elige una opcion: \n\t1. Ver espectaculos\n\t2. " + "Log IN\n\t3. Salir");

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
				cargarEspectaculos();
				mostrarEspectaculos();
				break;
			case 2:
				Persona usuarioIntento = login(credencialesSistema);
				if (usuarioIntento != null) {
					actual = new Sesion(usuarioIntento);
					switch (actual.getPerfilActual()) {
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
				System.out.println("No has introducido una opcion valida." + " Por favor intentalo de nuevo.");

			}
		} while (opcion != 3);

	}

	/**
	 * METODOS:
	 */

	/**
	 * Muestra el perfil de sesion activo.
	 * 
	 * @param actual
	 */
	public static void mostrarMenuSesion(Sesion actual) {
		System.out.println("Menu " + actual.getPerfilActual() + ":");
	}

	private static void cargarProperties() {
		Properties p = new Properties();
		try (InputStream input = Principal.class.getClassLoader().getResourceAsStream("application.properties")) {
			p.load(input);

			ProgramProperties.usuarioAdmin = p.getProperty("usuarioAdmin");
			ProgramProperties.passwordAdmin = p.getProperty("passwordAdmin");
			ProgramProperties.credenciales = p.getProperty("credenciales");
			ProgramProperties.espectaculos = p.getProperty("espectaculos");
			ProgramProperties.paises = p.getProperty("paises");

		} catch (FileNotFoundException e) {
			System.out.println("No pude encontrar el fichero de properties");
		} catch (IOException e) {
			System.out.println("Hubo problemas al leer el fichero de properties");
		}
	}

	private static Map<String, String> cargarPaises() {
		Map<String, String> paises = new HashMap<String, String>();

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
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
			System.out.println("Ha ocurrido algun problema al leer el archivo XML.");
		}

		return paises;
	}

	private static String getNodo(String etiqueta, Element elem) { // "etiqueta"
																	// concreta
		NodeList nodo = elem.getElementsByTagName(etiqueta).item(0).getChildNodes(); // busca todas las qtiquetas hijas
																						// con el nombre de la etiqueta
		// devuelve los nodos hijos
		Node valorNodo = nodo.item(0); // primer hijo ID
		return valorNodo.getNodeValue(); // el nodo de TEXTO (valor real) NOMBRE
	}

	private static boolean comprobarEspectaculoRepetido(String espNombre) {
		for (Espectaculo e : espectaculos) {
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

		if (actual.getPerfilActual() == Perfil.COORDINACION) {
			do {
				System.out.println("introduce el nombre del espectaculo" + "\n(debe tener un maximo de 25 caracteres)");
				nombrePrueba = leer.nextLine();
				if (nombrePrueba.length() <= 25 && !comprobarEspectaculoRepetido(nombrePrueba)) {
					nombre = nombrePrueba;
				} else {
					System.out.println("ese nombre es demasiado largo o ya existe.");
					nombre = null;
				}
			} while (nombre == null);
			try {
				do {
					System.out.println("introduce la fecha de inicio (formato yyyy-mm-dd)");
					String fecha1 = leer.nextLine();
					fechaIni = LocalDate.parse(fecha1);

					System.out.println(
							"introduce la fecha de fin\n(recuerda que no puede pasar mas de 1 año entre fechas)");
					String fecha2 = leer.nextLine();
					fechaFin = LocalDate.parse(fecha2);

					if (fechaFin.isBefore(fechaIni)) {
						System.out.println("La fecha final no puede ser anterior a la inicial.");
						fechas = false;
					} else if (fechaFin.isAfter(fechaIni.plusYears(1))) {
						System.out.println("El periodo no puede ser superior a 1 año.");
						fechas = false;
					} else {
						fechas = true;
					}
				} while (fechas == false);
			} catch (DateTimeParseException e) {
				System.out.println("fFormato de fecha incorrecto. Usa yyyy-mm-dd.");
			}
			Numero numero1 = new Numero("noche magica", 15.00);
			Numero numero2 = new Numero("sonidos del aire", 12.5);
			Numero numero3 = new Numero("baile del fuego", 18.5);
			numeros.add(numero1);
			numeros.add(numero2);
			numeros.add(numero3);
			long id = espectaculos.size() + 1;
			Coordinador coordinadorActual = (Coordinador) actual.getUsuActual();
			nuevoEspectaculo = new Espectaculo(id, nombre, fechaIni, fechaFin, numeros, coordinadorActual);
			System.out.println("Espectaculo creado con exito.");
		} else {
			do {
				System.out.println("introduce el nombre del espectaculo" + "\n(debe tener un maximo de 25 caracteres)");
				nombrePrueba = leer.nextLine();
				if (nombrePrueba.length() <= 25 && !comprobarEspectaculoRepetido(nombrePrueba)) {
					nombre = nombrePrueba;
				} else {
					System.out.println("ese nombre es demasiado largo o ya existe.");
					nombre = null;
				}
			} while (nombre == null);
			do {
				try {
					System.out.println("introduce la fecha de inicio (formato yyyy-mm-dd)");
					String fecha1 = leer.nextLine();
					fechaIni = LocalDate.parse(fecha1);

					System.out.println(
							"introduce la fecha de fin\n(recuerda que no puede pasar mas de 1 año entre fechas)");
					String fecha2 = leer.nextLine();
					fechaFin = LocalDate.parse(fecha2);

					if (fechaFin.isBefore(fechaIni)) {
						System.out.println("La fecha final no puede ser anterior a la inicial.");
						fechas = false;
					} else if (fechaFin.isAfter(fechaIni.plusYears(1))) {
						System.out.println("El periodo no puede ser superior a 1 año.");
						fechas = false;
					} else {
						fechas = true;
					}
				} catch (DateTimeParseException e) {
					System.out.println("Formato de fecha incorrecto. Usa yyyy-mm-dd.");
				}
			} while (fechas == false);

			Numero numero1 = new Numero("noche magica", 15.00);
			Numero numero2 = new Numero("sonidos del aire", 12.5);
			Numero numero3 = new Numero("baile del fuego", 18.5);
			numeros.add(numero1);
			numeros.add(numero2);
			numeros.add(numero3);
			long idEspectaculo = espectaculos.size() + 1;

			System.out.println("Elige un coordinador de los siguientes escribiendo su numero:");
			Boolean elegido = false;
			Coordinador coordinadorElegido = null;
			Boolean coordinadorEncontrado = false;
			do {
				for (Persona c : credencialesSistema) {
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

				for (Persona c : credencialesSistema) {
					if (c.getPerfil() == Perfil.COORDINACION && c.getId() == numCoor) {
						if (c instanceof Coordinador) {
							coordinadorElegido = new Coordinador(c.getId(), c.getEmail(), nombre, c.getNacionalidad(),
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
			nuevoEspectaculo = new Espectaculo(idEspectaculo, nombre, fechaIni, fechaFin, numeros, coordinadorElegido);
		}

		return nuevoEspectaculo;
	}

	private static ArrayList<Espectaculo> cargarEspectaculos() {
		ArrayList<Espectaculo> espectaculos = new ArrayList<Espectaculo>();
		File archivo = new File(ProgramProperties.espectaculos);
		if (!archivo.exists()) {
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(ProgramProperties.espectaculos, true))) {
				oos.writeObject(espectaculos);
				oos.close();
			} catch (FileNotFoundException e) {
				System.out.println("archivo no encontrado.");
			} catch (IOException e) {
				System.out.println("Error de escritura del archivo");
			}
		} else {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ProgramProperties.espectaculos))) {

				espectaculos = (ArrayList<Espectaculo>) ois.readObject();
				ois.close();
			} catch (FileNotFoundException e) {
				System.out.println("archivo espectaculos no encontrado");
			} catch (IOException e) {
				System.out.println("error de lectura del archivo al cargar");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("error de conversion de tipos.");
			}

		}

		return espectaculos;
	}

	public static void guardarEspectaculo(Espectaculo aGuardar) {

		ArrayList<Espectaculo> espectaculos = new ArrayList<Espectaculo>();
		espectaculos = cargarEspectaculos();

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ProgramProperties.espectaculos))) {
			espectaculos.add(aGuardar);
			oos.writeObject(espectaculos);
			System.out.println("archivo modificado.");

		} catch (FileNotFoundException e) {
			System.out.println("no se pudo encontrar el archivo de espectaculos");
		} catch (IOException e) {
			System.out.println("error al escribir el archivo de espectaculos");
			e.printStackTrace();
		}
	}

	private static ArrayList<Persona> cargarCredenciales() {
		ArrayList<Persona> personas = new ArrayList<>();
		// leer el fichero de credenciales
		ArrayList<String> lineas = leerFichero(ProgramProperties.credenciales);

		for (String linea : lineas) {
			if (linea.contains("coordinacion")) {
				personas.add(new Coordinador(linea));
			} else if (linea.contains("artista")) {
				personas.add(new Artista(linea));
			}
		}
		return personas;
	}

	private static ArrayList<String> leerFichero(String ruta) {
		ArrayList<String> lineas = new ArrayList<>();
		File archivo = new File(ruta);
		try {

			if (!archivo.exists()) {
				FileWriter writer = new FileWriter(archivo);
				writer.write("");
				writer.close();
			} else {

				BufferedReader reader = new BufferedReader(new FileReader(ruta));
				String linea;
				while ((linea = reader.readLine()) != null) {
					lineas.add(linea);
				}
				reader.close();

			}
		} catch (IOException e) {
			System.out.println("No se ha podido cargar el fichero: " + ruta);
		}

		return lineas;
	}

	private static Persona login(ArrayList<Persona> credenciales) {
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

		if (usuario.equals(ProgramProperties.usuarioAdmin) && password.equals(ProgramProperties.passwordAdmin)) {
			usuarioLogueado = new Persona(ProgramProperties.usuarioAdmin, ProgramProperties.passwordAdmin);
		} else {
			for (Persona p : credenciales) {
				if (p.getCredenciales().getNombre().equals(usuario)
						&& p.getCredenciales().getPassword().equals(password)) {
					usuarioLogueado = p;
				}
			}
		}
		return usuarioLogueado;
	}

	public static void logOut() {
		System.out.println("Has cerrado la sesion");
		actual.setUsuActual(new Persona());
	}

	// MENUS
	// MENU COORDINACION
	/**
	 * 1. ver espectaculos 2. gestionar espectaculos 2.1 crear-modificar espectaculo
	 * 2.2 crear-modificar numero 2.3 asignar artistas 3. Log OUT
	 */
	public static void menuCoordinacion() {
		int opcion = -1;
		mostrarMenuSesion(actual);
		do {
			System.out.println("Menu COORDINACION\nElige una opcion: \n\t1. Ver espectaculos\n\t.2 "
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
				mostrarEspectaculos();
				break;
			case 2:
				guardarEspectaculo(crearEspectaculo());
				break;
			case 3:
				logOut();
				break;
			case 4:
				logOut();
				System.out.println("Saliendo al menu anterior...");
				break;
			default:
				System.out.println("No has introducido una opcion valida." + " Por favor intentalo de nuevo.");
			}

		} while (opcion != 4);
	}

	// MENU ARTISTA
	/**
	 * 1. ver tu ficha 2. ver espectaculos 3. Log OUT
	 */
	public static void menuArtista() {
		int opcion = -1;
		mostrarMenuSesion(actual);
		do {
			System.out.println("Elige una opcion: \n\t1. Ver tu ficha\n\t2. Ver " + "espectaculos\n\t3. Log OUT");
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
				System.out.println("--Ficha del artista--\nNombre: " + actual.getUsuActual().getNombre() + "\nID: "
						+ actual.getUsuActual().getId());

				break;
			case 2:
				mostrarEspectaculos();
				break;
			case 3:
				logOut();

				break;
			default:
				System.out.println("No has introducido una opcion valida." + " Por favor intentalo de nuevo.");
			}

		} while (opcion != 3);
	}

	// MENU ADMIN
	/**
	 * 1. ver espectaculos 2. gestionar espectaculos 2.1 crear-modificar espectaculo
	 * 2.2 crear-modificar numero 2.3 asignar artistas 3. gestionar personas y
	 * credenciales 3.1 registrar persona 3.2 asignar perfil y credenciales 3.3
	 * gestionar datos artista-coordinador 4. Log OUT
	 */
	public static void menuAdmin() {
		int opcion = -1;

		mostrarMenuSesion(actual);
		do {
			System.out.println("Elige una opcion: \n\t1. Ver espectaculos" + "\n\t2. Gestionar espectaculos"
					+ "\n\t3. Gestionar personas y credenciales" + "\n\t4. Log OUT" + "\n\t5. Salir al menu anterior");

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
				mostrarEspectaculos();

				break;
			case 2:
				gestionarEscpectaculos();
				break;

			case 3:
				gestionarPersonas();
				break;
			case 4:
				logOut();
				break;
			case 5:
				logOut();
				System.out.println("Saliendo al menu anterior...");
				break;
			default:
				System.out.println("No has introducido una opcion valida." + " Por favor intentalo de nuevo.");
			}
		} while (opcion != 5);
	}

	public static void gestionarPersonas() {
		int opcion2 = -1;
		Persona nueva = null;
		do {
			System.out.println("Que deseas hacer?");
			System.out.println("\t1. Registrar persona\n\t2. " + "Gestionar datos artista o coordinador\n\t3. Salir al menu anterior");

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

				do {
					nueva = registrarPersona();
					if (nueva != null) {
						nueva.setId(credencialesSistema.size() + 1);
						credencialesSistema.add(nueva);
						persistirCredenciales();
						System.out.println("Usuario registrado con éxito");
					}
				} while (nueva == null);
				break;
			case 2:

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
			System.out.println("\t1. Crear o modificar un espectaculo\n\t2. " + "Crear o modificar un numero\n\t3. "
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
				guardarEspectaculo(crearEspectaculo());

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
		if (!comprobarEmail(email)) {
			System.out.println("Ese email ya esta registrado");
			return null;
		}
		System.out.println("introduce el nombre de la persona");
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
			
		}
		while (!nac);

		/*
		 * DATOS PROFESIONALES
		 */
		int num = -1;
		boolean validado = false;
		do {
			System.out.println("El usuario es Coordinador (1) o Artista (2)?");

			Boolean v = false;
			do {
				try {
					num = leer.nextInt();
					leer.nextLine();
					v = true;
				} catch (Exception e) {
					System.out.println("debes introducir un numero");
					leer.nextLine();
				}
			} while (!v);
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
					System.out.println("desde que fecha es senior? (formato yyyy-mm-dd)");
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
				Boolean vvv = false;
				int num3 = -1;
				do {
					try {
						num3 = leer.nextInt();
						leer.nextLine();
						vvv = true;
					} catch (Exception e) {
						System.out.println("debes introducir un numero");
						leer.nextLine();
					}
				} while (!vvv);

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
				System.out.println("indica los numeros de sus especialidades separados por comas: ");
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
							case 1 -> especialidadesUsu.add(Especialidad.ACROBACIA);
							case 2 -> especialidadesUsu.add(Especialidad.HUMOR);
							case 3 -> especialidadesUsu.add(Especialidad.MAGIA);
							case 4 -> especialidadesUsu.add(Especialidad.EQUILIBRISMO);
							case 5 -> especialidadesUsu.add(Especialidad.MALABARISMO);
							default -> System.out.println("Has introducido una opcion invalida");
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
			validado =true;
		} while (!validado);

		/**
		 * DATOS DE CREDENCIALES
		 */

		do {
			System.out.println("introduce el nombre de usuario (ten en cuenta que "
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
			System.out.println("por ultimo introduce una contraseña valida (debe"
					+ " tener mas de 2 caracteres, y ningun espacio en blanco");
			String pass = leer.nextLine();
			if (pass.matches("^[^|\\s]{3,}$")) {
				passUsuario = pass;
			} else
				System.out.println("contraseña no valida");
		} while (passUsuario == null);
		Credenciales credenciales = new Credenciales(nombreUsuario, passUsuario, perfilUsu);

		return resultadoLogin = new Persona(-1, email, nombreUsuario, nacionalidad, credenciales, perfilUsu);
	}

	public static void persistirCredenciales() {
		try {
			FileWriter writer = new FileWriter(ProgramProperties.credenciales);
			String contenido = "";
			for (Persona p : credencialesSistema) {
				contenido += p.toFicheroCredenciales()+"\n";
			}
			writer.write(contenido);
			writer.close();
		} catch (IOException e) {
			System.out.println("error al escribir el archivo");
		}
	}

	public static Boolean comprobarEmail(String email) {
		Boolean valido = true;
		for (Persona p : credencialesSistema) {
			if (p.getEmail() == email) {
				System.out.println("Ese email ya está registrado en el sistema");
				return false;
			}
		}
		return valido;
	}

	public static Boolean comprobarNombreUsuario(String nombreUsuario) {
		Boolean valido = true;
		for (Persona p : credencialesSistema) {
			if (p.getCredenciales().getNombre() == nombreUsuario) {
				System.out.println("Ese nombre ya existe");
				return false;
			}
		}
		// Si no hemos fallado en ningún validador, construimos la Persona
		// resultadoLogin = new Persona(..);
		return valido;
	}

	public static void mostrarEspectaculos() {
		ArrayList<Espectaculo> listaEspectaculos = new ArrayList<>();
		if (espectaculos == null || espectaculos.isEmpty()) {
			System.out.println("No hay espectáculos disponibles.");
			return;
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ProgramProperties.espectaculos))) {
			for (Espectaculo e : espectaculos) {
				listaEspectaculos.add(e);
			}
		} catch (FileNotFoundException e1) {
			System.out.println("Archivo de Espectaculos no encontrado");
			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println("Error de lectura o escritura del archivo Espectaculos");
			e1.printStackTrace();
		}
		for (Espectaculo e : listaEspectaculos) {
			System.out.println(e);
		}
	}

}
