package entidades;

import java.io.Serializable;

public class Persona implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected long id;
	protected String email;
	protected String nombre;
	protected String nacionalidad;
	protected Credenciales credenciales;
	protected Perfil perfil;
	
	public Persona() {
		this.perfil = Perfil.INVITADO;
	}
	
	

	public Persona(long id, String email, String nombre, String nacionalidad, Credenciales credenciales,
			Perfil perfil) {
		super();
		this.id = id;
		this.email = email;
		this.nombre = nombre;
		this.nacionalidad = nacionalidad;
		this.credenciales = credenciales;
		this.perfil = perfil;
	}

	// Constructor de administrador
	public Persona(String usuarioAdministrador, String contraseñaAdministrador) {
		super();
		this.credenciales = new Credenciales(usuarioAdministrador, contraseñaAdministrador);
		this.perfil = Perfil.ADMIN;
	}

	public Persona(String linea) {
		super();
		String[] propiedades = linea.split("\\|");
		this.id = Long.parseLong(propiedades[0]);
		this.credenciales = new Credenciales(propiedades[1], propiedades[2]);
		this.email = propiedades[3];
		this.nombre = propiedades[4];
		this.nacionalidad = propiedades[5];
		switch (propiedades[6].toLowerCase().trim()) {
		case "artista":
			this.perfil = Perfil.ARTISTA;
			break;
		case "coordinacion":
			this.perfil = Perfil.COORDINACION;
			break;
		default:
			this.perfil = Perfil.INVITADO;
			break;
		}

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public Credenciales getCredenciales() {
		return credenciales;
	}

	public void setCredenciales(Credenciales credenciales) {
		this.credenciales = credenciales;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	
	public String toFicheroCredenciales () {
		 
		return id+"|"+credenciales.getNombre()+"|"+credenciales.getPassword()+
				"|"+email+"|"+nombre+"|"+nacionalidad+"|"+perfil;
	}



	@Override
	public String toString() {
		return "Persona [id=" + id + ", email=" + email + ", nombre=" + nombre
				+ ", nacionalidad=" + nacionalidad + ", credenciales="
				+ credenciales + ", perfil=" + perfil + "]";
	}

	
	

}
