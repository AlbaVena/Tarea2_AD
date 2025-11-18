package entidadesDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import entidades.Artista;
import entidades.Persona;
import factorias.DAOFactoryJDBC;

public class PersonaDAO {

	private DAOFactoryJDBC DAOF;
	private final String INSERTARUSUARIOPS = "insert into personas (email, nombre, nacionalidad) values (?, ?, ?)";
	private final String INSERTARARTISTAPS = "insert into artistas (apodo, id_persona) values (?, ?)";

	public PersonaDAO() {
		DAOF = DAOFactoryJDBC.getDAOFactory();
	}

	public long insertarUsuario(Persona persona) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		long resultado = -1;
		try {
			// ***** Obtener la conexion ( y de paso los ID)****
			ps = DAOF.getConexion().prepareStatement(INSERTARUSUARIOPS,
					Statement.RETURN_GENERATED_KEYS);

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
		PreparedStatement ps = null;
		ResultSet rs = null;
		long resultado = -1;
		long idPersonaGenerado = insertarUsuario(artista);

		if (idPersonaGenerado <= 0) {
			System.err.println("Fallo al obtener el id_persona");
			return -1;
		}

		try {
			ps = DAOF.getConexion().prepareStatement(INSERTARARTISTAPS,
					Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, artista.getApodo());
			ps.setLong(2, idPersonaGenerado);

			int filas = ps.executeUpdate();

			if (filas == 0) {
				throw new SQLException("No se inserto nada");
			}
			
			
			rs = ps.getGeneratedKeys();

			if (rs.next()) {
				resultado = rs.getLong(1);
				
				//TODO aqui tengo que insertar las especialidades
				//insertarEspecialidades(idArtistaGenerado, artista.getEspecialidades());
				
			}
		else {
			throw new SQLException("No se pudo obtener el ID");
		}
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
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
