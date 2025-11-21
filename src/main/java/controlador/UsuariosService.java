package controlador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import entidades.Artista;
import entidades.Coordinador;
import entidades.Persona;
import entidades.ProgramProperties;
import entidades.Sesion;
import entidadesDAO.PersonaDAO;

public class UsuariosService {

	Sesion actual = new Sesion();

	static PersonaDAO PDAO = null;

	/**
	 * 
	 * @return
	 */
	public Sesion getSesion() {
		return actual;
	}

	public void setSesion(Sesion actual) {
		this.actual = actual;
	}

	public ArrayList<Persona> getCredencialesSistema() {
		return PDAO.getPersonas();
	}

	/*
	 * constructor
	 */
	public UsuariosService() {
		PDAO = new PersonaDAO();
	}

	public Persona login(String nombreUsuario, String password) {
		Persona usuarioLogueado = null;

		if (nombreUsuario.equals(ProgramProperties.usuarioAdmin) && password.equals(ProgramProperties.passwordAdmin)) {
			usuarioLogueado = new Persona(ProgramProperties.usuarioAdmin, ProgramProperties.passwordAdmin);
		} else {
			for (Persona p : getCredencialesSistema()) {
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

	public Persona getPersona(Long id) {
		return PDAO.getPersonaId(id);
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

		PersonaDAO pdao = new PersonaDAO();
		long idUsuario = pdao.insertarUsuario(nueva);

		if (idUsuario > -1) {
			nueva.setId(idUsuario);
			
			if (nueva instanceof Artista) {
				Artista artista = (Artista) nueva;
				System.out.println(pdao.insertarArtista(artista)); // FUNCIONA
																	// TODO lo mismo para coordinador,
																	// y luego para credenciales
			} else if (nueva instanceof Coordinador) {
				Coordinador coordinador = (Coordinador) nueva;
				System.out.println(pdao.insertarCoordinador(coordinador));
			}
		}

	}

	public void persistirCredenciales() {
		try {
			FileWriter writer = new FileWriter(ProgramProperties.credenciales);
			String contenido = "";
			for (Persona p : getCredencialesSistema()) {
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
		for (Persona p : getCredencialesSistema()) {
			if (p.getEmail() == email) {
				System.out.println("Ese email ya está registrado en el sistema");
				return false;
			}
		}
		return valido;
	}

	public void modificarArtista(Artista artista) {
		PDAO.modificarArtista(artista);
	}

	public Boolean comprobarNombreUsuario(String nombreUsuario) {
		Boolean valido = true;
		for (Persona p : getCredencialesSistema()) {
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
