package entidadesDAO;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import entidades.Persona;
import factorias.DAOFactoryJDBC;

public class PersonaDAO {

	private DAOFactoryJDBC DAOF;
	private final String INSERTARUSUARIOPS =  "insert into personas (email, nombre, nacionalidad) values (?, ?, ?)";
	
	
	public PersonaDAO() {
		DAOF = DAOFactoryJDBC.getDAOFactory();
	}
	
	public int insertarUsuario(Persona persona) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		int resultado = -1;
		try {
			//Obtener la conexion ( y de paso los ID)****
			ps = DAOF.getConexion().prepareStatement(INSERTARUSUARIOPS, Statement.RETURN_GENERATED_KEYS);
			
			//añadir los campos en el mismo orden
			ps.setString(1, persona.getEmail());
			ps.setString(2, persona.getNombre());
			ps.setString(3, persona.getNacionalidad());
			
			//actualizar cambios
			int filas = ps.executeUpdate();
			
			if (filas == 0) {
				throw new SQLException("No se inserto nada");
			}
			
			//guardamos las claves ID autogeneradas
			rs = ps.getGeneratedKeys();
			
			
			if (rs.next()) {
				resultado = rs.getInt(1);
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
