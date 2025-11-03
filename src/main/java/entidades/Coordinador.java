package entidades;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

public class Coordinador extends Persona implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long idCoord;
	private boolean senior = false;
	private LocalDate fechasenior = null;
	private Set <Espectaculo> espectaculos;
	
	public Coordinador (String linea) {
		super();
		String[] propiedades = linea.split("\\|");
		this.id = Long.parseLong(propiedades[0]);
		this.credenciales = new Credenciales(propiedades[1], propiedades[2]);
		this.email = propiedades[3];
		this.nombre = propiedades[4];
		this.nacionalidad = propiedades[5];
			this.perfil=Perfil.COORDINACION;
	}

	public Coordinador(long id, String email, String nombre, String nacionalidad, Credenciales credenciales) {
		super(id, email, nombre, nacionalidad, credenciales, Perfil.COORDINACION);
	}

	public Coordinador(long id, String email, String nombre, String nacionalidad, Credenciales credenciales,
			long idCoord, boolean senior, LocalDate fechasenior, Set <Espectaculo> espectaculos) {
		super(id, email, nombre, nacionalidad, credenciales, Perfil.COORDINACION);
		this.idCoord = idCoord;
		this.senior = senior;
		this.fechasenior = fechasenior;
		this.espectaculos = espectaculos;
	}

	public long getIdCoord() {
		return idCoord;
	}

	public void setIdCoord(long idCoord) {
		this.idCoord = idCoord;
	}

	public boolean isSenior() {
		return senior;
	}

	public void setSenior(boolean senior) {
		this.senior = senior;
	}

	public LocalDate getFechasenior() {
		return fechasenior;
	}

	public void setFechasenior(LocalDate fechasenior) {
		this.fechasenior = fechasenior;
	}

	public Set <Espectaculo> getEspectaculos() {
		return espectaculos;
	}

	public void setEspectaculos(Set<Espectaculo> espectaculos) {
		this.espectaculos = espectaculos;
	}

	@Override
	public String toString() {
		return "Coordinador [idCoord=" + idCoord + ", senior=" + senior
				+ ", fechasenior=" + fechasenior + ", espectaculos="
				+ espectaculos + "]";
	}
	
	

	
	
}
