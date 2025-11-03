package entidades;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

public class Espectaculo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String nombre;
	private LocalDate fechaini;
	private LocalDate fechafin;
	private Set <Numero> numeros;
	private Coordinador encargadoCoor;
	
	public Espectaculo(long id, String nombre, LocalDate fechaini, LocalDate fechafin,Set<Numero> numeros, Coordinador encargadoCoor ) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.fechaini = fechaini;
		this.fechafin = fechafin;
		this.numeros = numeros;
		this.setEncargadoCoor(encargadoCoor);
		
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
	public LocalDate getFechaini() {
		return fechaini;
	}
	public void setFechaini(LocalDate fechaini) {
		this.fechaini = fechaini;
	}
	public LocalDate getFechafin() {
		return fechafin;
	}
	public void setFechafin(LocalDate fechafin) {
		this.fechafin = fechafin;
	}
	public Set<Numero> getNumeros() {
		return numeros;
	}
	public void setNumeros(Set<Numero> numeros) {
		this.numeros = numeros;
	}
	@Override
	public String toString() {
		return "Espectaculo id:" + id + ",nombre: "+ nombre + ", desde " +fechaini + ", hasta " + fechafin;
	}
	public Coordinador getEncargadoCoor() {
		return encargadoCoor;
	}
	public void setEncargadoCoor(Coordinador encargadoCoor) {
		this.encargadoCoor = encargadoCoor;
	}
	
	
	

}
