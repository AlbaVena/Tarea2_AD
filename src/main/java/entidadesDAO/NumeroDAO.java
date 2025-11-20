
package entidadesDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import entidades.Numero;
import factorias.DAOFactoryJDBC;

public class NumeroDAO {

	private DAOFactoryJDBC DAOF; // CONEXION

	private final String INSERTARNUMEROPS = "INSERT INTO numeros (nombre, duracion, id_espectaculo) VALUES (?, ?, ?)";
	private final String INSERTARARTISTA_NUMEROSPS = "INSERT INTO artistas_numeros (id_artista, id_numero) VAUES (?, ?)";

	public int insertarNumero(Numero numero) {
		int resultado = -1;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DAOF.getConexion().prepareStatement(INSERTARNUMEROPS, Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, numero.getNombre());
			ps.setInt(2, numero.getDuracion());
			ps.setLong(3, numero.getEspectaculo().getId());

			int filas = ps.executeUpdate();

			if (filas == 0) {
				throw new SQLException("No se inserto nada");
			}

			rs = ps.getGeneratedKeys();

			if (rs.next()) {
				resultado = rs.getInt(1);
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

	public boolean asignarNumeroArtista(long idArtista, int idNumero) {
		boolean exito = false;
		PreparedStatement ps = null;

		try {
			ps = DAOF.getConexion().prepareStatement(INSERTARARTISTA_NUMEROSPS);

			ps.setLong(1, idArtista);
			ps.setInt(2, idNumero);

			int filas = ps.executeUpdate();

			if (filas > 0) {
				exito = true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					System.err.println("Error al cerrar la conexion");
				}
			}
		}

		return exito;

	}

}
