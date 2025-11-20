package entidadesDAO;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import com.mysql.jdbc.Statement;

import entidades.Artista;
import entidades.Coordinador;
import entidades.Credenciales;
import entidades.Perfil;
import entidades.Persona;
import factorias.DAOFactoryJDBC;

public class PersonaDAO {

	private DAOFactoryJDBC DAOF; // CONEXION

	/**
	 * INSERTAR
	 */
	private final String INSERTARUSUARIOPS = "INSERT INTO personas (email, nombre, nacionalidad) VALUES (?, ?, ?)";
	private final String INSERTARARTISTAPS = "INSERT INTO artistas (apodo, id_persona) VALUES (?, ?)";
	private final String INSERTARCOORDINADORPS = "INSERT INTO coordinadores (senior, fechasenior, id_persona) VALUES (?, ?, ?)";
	private final String INSERTARCREDENCIALESPS = "INSERT INTO credenciales (nombre, password, id_persona) VALUES (?, ?, ?)";

	/**
	 * SELECT
	 */
	private final String SELECTPERSONASsql = "SELECT "
			+ " p.id_persona, p.nombre AS nombre_persona, p.email, p.nacionalidad, "
			+ " c.nombre AS nombre_usuario, c.password, c.perfil, " + " a.id_artista, a.apodo, "
			+ " co.id_coordinador, co.senior, co.fechasenior " + "FROM personas p "
			+ "LEFT JOIN credenciales c ON p.id_persona = c.id_persona "
			+ "LEFT JOIN artistas a ON p.id_persona = a.id_persona "
			+ "LEFT JOIN coordinadores co ON p.id_persona = co.id_persona";

	private final String SELECTPERSONA_ID = SELECTPERSONASsql + " WHERE p.id_persona = ?";

	/**
	 * MODIFICAR
	 */
	private final String MODIFICARARTISTAPS = "";
	private final String MODIFICARCOORDINADORPS = "";

	/**
	 * ELIMINAR
	 */
	private final String ELIMINARUSUARIO = "";

	/**
	 * CONSTRUCTOR
	 */
	public PersonaDAO() {
		DAOF = DAOFactoryJDBC.getDAOFactory();
	}

	public long insertarUsuario(Persona persona) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		long resultado = -1;

		// desactivar el autocommit

		try {
			// ***** Obtener la conexion ( y de paso los ID)****
			ps = DAOF.getConexion().prepareStatement(INSERTARUSUARIOPS, Statement.RETURN_GENERATED_KEYS);

			// añadir los campos en el mismo orden
			ps.setString(1, persona.getEmail());
			ps.setString(2, persona.getNombre());
			ps.setString(3, persona.getNacionalidad());

			// actualizar cambios
			int filas = ps.executeUpdate();

			if (filas == 0) {
				throw new SQLException("No se inserto nada");
			}

			// guardamos las claves ID autogeneradas
			rs = ps.getGeneratedKeys();

			if (rs.next()) {
				resultado = rs.getInt(1);
			} else {
				throw new SQLException("No se pudo obtener el ID");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					System.err.println("Error al cerrar la consulta");
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					System.err.println("Error al cerrar la conexion");
				}
			}
		}

		return resultado;
	}

	public long insertarArtista(Artista artista) {

		try {
			DAOF.getConexion().setAutoCommit(false); // Desactivar el autocommit
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		long resultado = -1;

		long idPersonaGenerado = insertarUsuario(artista); // devuelve el id_personaa

		if (idPersonaGenerado <= 0) {

			System.err.println("Fallo al obtener el id_persona");
			return -1;
		}

		try {
			ps = DAOF.getConexion().prepareStatement(INSERTARARTISTAPS, Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, artista.getApodo());
			ps.setLong(2, idPersonaGenerado);

			int filas = ps.executeUpdate();

			if (filas == 0) {
				throw new SQLException("No se inserto nada");

			}

			rs = ps.getGeneratedKeys();

			if (rs.next()) {
				resultado = rs.getLong(1);
				insertarCredenciales(artista.getCredenciales(), idPersonaGenerado);

				// TODO aqui tengo que insertar las especialidades
				// insertarEspecialidades(idArtistaGenerado, artista.getEspecialidades());

				DAOF.getConexion().commit();

			} else {
				throw new SQLException("No se pudo obtener el ID");
			}

		} catch (SQLException e) {
			try {

				DAOF.getConexion().rollback();
				System.err.println("Rollback.");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					System.err.println("Error al cerrar la consulta");
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					System.err.println("Error al cerrar la conexion");
				}
			}
		}

		return resultado;
	}

	public long insertarCoordinador(Coordinador coordinador) {

		try {
			DAOF.getConexion().setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		long resultado = -1;
		long idPersonaGenerado = insertarUsuario(coordinador);

		if (idPersonaGenerado <= 0) {

			System.err.println("Fallo al obtener el id_persona");
			return -1;
		}

		try {
			ps = DAOF.getConexion().prepareStatement(INSERTARCOORDINADORPS, Statement.RETURN_GENERATED_KEYS);

			ps.setBoolean(1, coordinador.isSenior());

			Date sqlDate = Date.valueOf(coordinador.getFechasenior());

			ps.setDate(2, sqlDate);
			ps.setLong(3, idPersonaGenerado);

			int filas = ps.executeUpdate();

			if (filas == 0) {
				throw new SQLException("No se inserto nada");
			}

			rs = ps.getGeneratedKeys();

			if (rs.next()) {
				resultado = rs.getLong(1);
				insertarCredenciales(coordinador.getCredenciales(), idPersonaGenerado);

				DAOF.getConexion().commit();

				// TODO aqui tengo que insertar las especialidades
				// insertarEspecialidades(idArtistaGenerado, artista.getEspecialidades());

			} else {
				throw new SQLException("No se pudo obtener el ID");
			}

		} catch (SQLException e) {

			try {

				DAOF.getConexion().rollback();
				System.err.println("Rollback.");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					System.err.println("Error al cerrar la consulta");
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					System.err.println("Error al cerrar la conexion");
				}
			}
		}

		return resultado;
	}

	public long insertarCredenciales(Credenciales credenciales, long idPersona) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		long resultado = -1;

		try {
			ps = DAOF.getConexion().prepareStatement(INSERTARCREDENCIALESPS);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			ps.setString(1, credenciales.getNombre());
			ps.setString(2, credenciales.getPassword());
			ps.setLong(3, idPersona);

			int filas = ps.executeUpdate();

			if (filas == 0) {
				throw new SQLException("No se inserto nada");

			}

			rs = ps.getGeneratedKeys();

			if (rs.next()) {
				resultado = rs.getLong(1);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					System.err.println("Error al cerrar la consulta");
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					System.err.println("Error al cerrar la conexion");
				}
			}
		}

		return resultado;
	}

	public void modificarArtista() {
		// TODO
	}

	public void modificarCoordinador() {
		// TODO
	}

	/**
	 * devuelve todas las filas, y contruye un artista o coordinador segun su perfil
	 * @return arraylist de personas completas
	 */
	public ArrayList<Persona> getPersonas() {
		ArrayList<Persona> personas = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DAOF.getConexion().prepareStatement(SELECTPERSONASsql);
			rs = ps.executeQuery();

			while (rs.next()) {
				// de persona
				long idPersona = rs.getLong("id_persona");
				String email = rs.getString("email");
				String nombre = rs.getString("nombre_persona");
				String nacionalidad = rs.getString("nacionalidad");

				// de credenciales
				Credenciales credenciales = new Credenciales(rs.getString("nombre_usuario"), rs.getString("password"),
						Perfil.valueOf(rs.getString("perfil")));

				if (credenciales.getPerfil() == Perfil.ARTISTA) {
					long idArtista = rs.getLong("id_artista");
					String apodo = rs.getString("apodo");

					Artista nuevoArtista = new Artista(idPersona, email, nombre, nacionalidad, credenciales, idArtista,
							apodo, null, null);
					personas.add(nuevoArtista);

				}

				else if (credenciales.getPerfil() == Perfil.COORDINACION) {
					long idCoordinador = rs.getLong("id_coordinador");
					boolean senior = rs.getBoolean("senior");
					java.sql.Date fecha = rs.getDate("fechasenior");
					LocalDate fechaLocal = null;

					if (fecha != null) {
						fechaLocal = fecha.toLocalDate();
					}

					Coordinador nuevoCoordinador = new Coordinador(idPersona, email, nombre, nacionalidad, credenciales,
							idCoordinador, senior, fechaLocal, null);

					personas.add(nuevoCoordinador);
				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					System.err.println("Error al cerrar la consulta");
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					System.err.println("Error al cerrar la conexion");
				}
			}
		}

		return personas;
	}

	/**
	 * busca una persona por su id_persona
	 * construye un artista o coordinador segun su perfil
	 * @param idPersona
	 * @return persona tipo Artista o Coordinador
	 */
	public Persona getPersonaId(long idPersona) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Persona persona = null;

		try {
			ps = DAOF.getConexion().prepareStatement(SELECTPERSONA_ID);

			ps.setLong(1, idPersona);
			rs = ps.executeQuery();

			if (rs.next()) {
				String email = rs.getString("email");
				String nombre = rs.getString("nombre");
				String nacionalidad = rs.getString("nacionalidad");

				Credenciales credenciales = new Credenciales(rs.getString("nombre_usuario"), rs.getString("password"),
						Perfil.valueOf(rs.getString("perfil")));

				if (credenciales.getPerfil() == Perfil.ARTISTA) {
					long idArtista = rs.getLong("id_artista");
					String apodo = rs.getString("apodo");
					persona = new Artista(idPersona, email, nombre, nacionalidad, credenciales, idArtista, apodo, null,
							null);
				} else if (credenciales.getPerfil() == Perfil.COORDINACION) {
					long idCoordinador = rs.getLong("id_coordinador");
					boolean senior = rs.getBoolean("senior");
					java.sql.Date fecha = rs.getDate("fechasenior");
					LocalDate fechaLocal = null;

					if (fecha != null) {
						fechaLocal = fecha.toLocalDate();
					}
					
					//TODO cuando tenga los espectaculos tendran que ir aqui

					persona = new Coordinador(idPersona, email, nombre, nacionalidad, credenciales, idCoordinador,
							senior, fechaLocal, null);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					System.err.println("Error al cerrar la consulta");
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					System.err.println("Error al cerrar la conexion");
				}
			}
		}

		return persona;
	}

}
