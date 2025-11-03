package entidades;

import java.io.Serializable;

public class Credenciales implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String nombre;
	private String password;
	private Perfil perfil = Perfil.INVITADO;
	
	public Credenciales(long id, String nombre, String password, Perfil perfil) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.password = password;
		this.perfil = perfil;
	}
	public Credenciales(String nombre, String password, Perfil perfil) {
		super();
		this.nombre = nombre;
		this.password = password;
		this.perfil = perfil;
	}
	
	public Credenciales(String nombreAdministrador, String passwordAdministrador) {
		super();
		this.id = -1;
		this.nombre = nombreAdministrador;
		this.password = passwordAdministrador;
		this.perfil = Perfil.ADMIN;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	
	

}
