package entidades;

public class Sesion {
	private Persona usuActual;
	private Perfil perfilActual = Perfil.INVITADO;
	
	public Sesion(Persona usuActual) {
		super();
		this.usuActual = usuActual;
		if (usuActual != null) {
			perfilActual = usuActual.getPerfil();
		}
	}
	
	public Sesion() {
		super();
	}

	public Sesion( Perfil perfilActual) {
		super();
		this.perfilActual = perfilActual;

	}

	public Persona getUsuActual() {
		return usuActual;
	}

	public void setUsuActual(Persona usuActual) {
		this.usuActual = usuActual;
	}

	public Perfil getPerfilActual() {
		return perfilActual;
	}

	public void setPerfilActual(Perfil perfilActual) {
		this.perfilActual = perfilActual;
	}



	
}
