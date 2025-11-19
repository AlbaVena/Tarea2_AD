package entidades;

import java.io.Serializable;
import java.util.Set;

public class Numero implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private int orden;
	private String nombre;
	private int duracion;
	private Espectaculo espectaculo;
	private Set <Artista> artistas;
	
	public Numero(long id, int orden, String nombre, int duracion, Espectaculo espectaculo, Set<Artista> artistas) {
		this.id = id;
		this.orden = orden;
		this.nombre = nombre;
		this.duracion = duracion;
		this.espectaculo = espectaculo;
		this.artistas = artistas;
	}

	public Numero (String nombre, int duracion) {
		this.nombre = nombre;
		this.duracion = duracion;
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public Espectaculo getEspectaculo() {
		return espectaculo;
	}

	public void setEspectaculo(Espectaculo espectaculo) {
		this.espectaculo = espectaculo;
	}

	public Set<Artista> getArtistas() {
		return artistas;
	}

	public void setArtistas(Set<Artista> artistas) {
		this.artistas = artistas;
	}

	@Override
	public String toString() {
		return "Numero id:" + id + ", nombre=" + nombre
				+ ", duracion=" + duracion + ", espectaculo=" + espectaculo
				+ ", artistas=" + artistas + "]";
	}
	
	

}
