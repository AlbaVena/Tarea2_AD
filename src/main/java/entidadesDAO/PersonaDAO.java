package entidadesDAO;

import java.sql.Connection;
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
import entidades.Especialidad;
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
	private final String INSERTARARTISTAESPECIALIDADPS = "INSERT INTO artista_especialidad (id_artista, id_especialidad) VALUES (?, ?)";

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
	private final String SELECTESPECIALIDAD_ID = "SELECT id_especialidad FROM especialidades WHERE nombre = ?";

	/**
	 * MODIFICAR
	 */
	private final String MODIFICARPERSONAPS = "UPDATE personas SET email = ?, nombre = ?, nacionalidad = ? WHERE id_persona = ?";
	private final String MODIFICARARTISTAPS = "UPDATE artistas SET apodo = ? WHERE id_persona = ?";
	private final String MODIFICARCOORDINADORPS = "";

	/**
	 * ELIMINAR
	 */
	private final String ELIMINARUSUARIO = "";
	private final String ELIMINARESPECIALIDADES = "DELETE FROM artista_especialidad WHERE id_artista = ?";

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

	public String modificarPersona(Persona persona) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String resultado = null;

		try {
			ps = DAOF.getConexion().prepareStatement(MODIFICARPERSONAPS);

			ps.setString(1, persona.getEmail());
			ps.setString(2, persona.getNombre());
			ps.setString(3, persona.getNacionalidad());
			ps.setLong(4, persona.getId());

			int filas = ps.executeUpdate();

			if (filas > 0) {
				return "Datos de persona actualizados.";
			} else {
				return "No se encontro el ID " + persona.getId();
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

	public void modificarArtista(Artista artista) {
		Connection conexion = null;
		PreparedStatement psArt = null;
		PreparedStatement psDelEspec = null;
		PreparedStatement psInserEspec = null;
		ResultSet rs = null;

		try {
			conexion = DAOF.getConexion();
			conexion.setAutoCommit(false); // COMO en insertarArtista

			// actualizar apodo
			try {
				psArt = conexion.prepareStatement(MODIFICARARTISTAPS);
				psArt.setString(1, artista.getApodo());
				psArt.setLong(2, artista.getId());

				// Y actualizar
				psArt.executeUpdate();

				// eliminar las especialidades antiguas Y actualizar
				psDelEspec = conexion.prepareStatement(ELIMINARESPECIALIDADES);
				psDelEspec.setLong(1, artista.getId());
				psDelEspec.executeUpdate();

				for (Especialidad e : artista.getEspecialidades()) {

					// insertar nuevas especialidades
					int idEspecialidad = (int) getEspecialidadesID(e.name());

					if (idEspecialidad == -1) {
						throw new SQLException("Especialidad no encontrada. Se cancela la transacción.");
					}

					// insertar nueva relacion
					psInserEspec = conexion.prepareStatement(INSERTARARTISTAESPECIALIDADPS);
					psInserEspec.setLong(1, artista.getIdArt());
					psInserEspec.setInt(2, idEspecialidad);

					psInserEspec.executeUpdate();

					// como se abre dentro del bucle, tambien hay que cerrarlo por si acaso
					if (psInserEspec != null) {
						psInserEspec.close();
						psInserEspec = null;
					}

					// si todo ha ido bien AHORA commit
					conexion.commit();

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("No se ha podido modificar apodo de artista");
				try {
					if (conexion != null) {
						conexion.rollback();
						System.err.println("Rollback.");
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.err.println("Error en el roolback");
				}

			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						System.err.println("Error al cerrar la consulta");
					}
				}
				if (psArt != null) { psArt.close(); }
				if (psDelEspec != null) { psDelEspec.close(); }
				
				if (conexion != null) {
					conexion.setAutoCommit(true);
					conexion.close();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void modificarCoordinador() {
		// TODO
	}

	/**
	 * devuelve todas las filas, y contruye un artista o coordinador segun su perfil
	 * 
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
	 * busca una persona por su id_persona construye un artista o coordinador segun
	 * su perfil
	 * 
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

					// TODO cuando tenga los espectaculos tendran que ir aqui

					persona = new Coordinador(idPersona, email, nombre, nacionalidad, credenciales, idCoordinador,
							senior, fechaLocal, null);
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

		return persona;
	}

	public int getEspecialidadesID(String nombreEs) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		int resultado = -1;

		try {
			ps = DAOF.getConexion().prepareStatement(SELECTESPECIALIDAD_ID);
			ps.setString(1, nombreEs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("id_especialidad");
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

}
