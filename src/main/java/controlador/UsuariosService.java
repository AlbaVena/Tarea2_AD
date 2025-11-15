package controlador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import entidades.Artista;
import entidades.Coordinador;
import entidades.Credenciales;
import entidades.Especialidad;
import entidades.Perfil;
import entidades.Persona;
import entidades.ProgramProperties;
import entidades.Sesion;
import principal.Principal;

public class UsuariosService {

	Sesion actual = new Sesion();
	
	
	public Sesion getSesion() {
		return actual;
	}

	public void setSesion(Sesion actual) {
		this.actual = actual;
	}
	
	

	public ArrayList<Persona> getCredencialesSistema() {
		return credencialesSistema;
	}

	public void setCredencialesSistema(ArrayList<Persona> credencialesSistema) {
		this.credencialesSistema = credencialesSistema;
	}



	ArrayList<Persona> credencialesSistema = null;

	public UsuariosService() {
		credencialesSistema = cargarCredenciales();
	}

	private ArrayList<String> leerFichero(String ruta) {
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

	private ArrayList<Persona> cargarCredenciales() {
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

	private void cargarProperties() {
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

	public Persona login(String nombreUsuario, String password) {
		Persona usuarioLogueado = null;

		if (nombreUsuario.equals(ProgramProperties.usuarioAdmin) && password.equals(ProgramProperties.passwordAdmin)) {
			usuarioLogueado = new Persona(ProgramProperties.usuarioAdmin, ProgramProperties.passwordAdmin);
		} else {
			for (Persona p : credencialesSistema) {
				if (p.getCredenciales().getNombre().equals(nombreUsuario)
						&& p.getCredenciales().getPassword().equals(password)) {
					usuarioLogueado = p;
				}
			}
		}
		if (usuarioLogueado != null) {
			actual = new Sesion(usuarioLogueado);
		}
		return usuarioLogueado;
	}

	public void logOut() {
		actual.setUsuActual(new Persona());
	}

	public String mostrarFicha() {
		String ficha = "--Ficha del artista--\nNombre: " + actual.getUsuActual().getNombre() + "\nID: "
				+ actual.getUsuActual().getId();
		return ficha;
	}

	public void crearPersona(Persona nueva) {
		nueva.setId(credencialesSistema.size() + 1);
		credencialesSistema.add(nueva);
		persistirCredenciales();

	}

	public void persistirCredenciales() {
		try {
			FileWriter writer = new FileWriter(ProgramProperties.credenciales);
			String contenido = "";
			for (Persona p : credencialesSistema) {
				contenido += p.toFicheroCredenciales() + "\n";
			}
			writer.write(contenido);
			writer.close();
		} catch (IOException e) {
			System.err.println("Error al escribir el archivo");
		}
	}

	public Boolean comprobarEmail(String email) {
		Boolean valido = true;
		for (Persona p : credencialesSistema) {
			if (p.getEmail() == email) {
				System.out.println("Ese email ya está registrado en el sistema");
				return false;
			}
		}
		return valido;
	}

	public Boolean comprobarNombreUsuario(String nombreUsuario) {
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

}
