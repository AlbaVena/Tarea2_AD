package entidades;

import java.util.List;
import java.util.Set;

public class Artista extends Persona {
	private static final long serialVersionUID = 1L;
	private long idArt;
	private String apodo = null;
	private Set<Especialidad> especialidades;
	private List<Numero> numeros;

	public Artista(long id, String email, String nombre, String nacionalidad,
			Credenciales credenciales) {
		super(id, email, nombre, nacionalidad, credenciales, Perfil.ARTISTA);
	}
	public Artista (String linea) {
		super();
		String[] propiedades = linea.split("\\|");
		this.id = Long.parseLong(propiedades[0]);
		this.credenciales = new Credenciales(propiedades[1], propiedades[2]);
		this.email = propiedades[3];
		this.nombre = propiedades[4];
		this.nacionalidad = propiedades[5];
		this.perfil=Perfil.ARTISTA;
}

	public Artista(long id, String email, String nombre, String nacionalidad,
			Credenciales credenciales, long idArt, String apodo,
			Set<Especialidad> especialidades, List<Numero> numeros) {
		super(id, email, nombre, nacionalidad, credenciales, Perfil.ARTISTA);
		this.idArt = idArt;
		this.apodo = apodo;
		this.especialidades = especialidades;
		this.numeros = numeros;
	}

	public long getIdArt() {
		return idArt;
	}

	public void setIdArt(long idArt) {
		this.idArt = idArt;
	}

	public String getApodo() {
		return apodo;
	}

	public void setApodo(String apodo) {
		this.apodo = apodo;
	}

	public Set<Especialidad> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(Set<Especialidad> especialidades) {
		this.especialidades = especialidades;
	}

	public List<Numero> getNumeros() {
		return numeros;
	}

	public void setNumeros(List<Numero> numeros) {
		this.numeros = numeros;
	}

	//Metodos:
	public void mostrarFicha() {
		if (apodo != null) {
			System.out.println("Artista: "+nombre+"\nID: "+idArt+"\nApodo: "
		+apodo+
					"Especialidades:");
			for (Especialidad especialidad : especialidades) {
				System.out.println(especialidad);
			}
		}
		else {
			System.out.println("Artista: "+nombre+"\nEspecialidades:\n");
			for (Especialidad especialidad : especialidades) {
				System.out.println(especialidad);
			}
	}
}
}
