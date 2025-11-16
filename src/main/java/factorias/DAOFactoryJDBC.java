package factorias;

import java.sql.Connection;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import entidades.ProgramProperties;

public class DAOFactoryJDBC {

	private Connection conexion;
	
	
	
	public Connection getConexion() {
		return conexion;
	}

	private static DAOFactoryJDBC f;
	
	private DAOFactoryJDBC() {
		
		MysqlDataSource m = new MysqlDataSource();
		
		try {
			m.setUrl(ProgramProperties.url);
			m.setPassword(ProgramProperties.dbpass);
			m.setUser(ProgramProperties.dbuser);
			
			conexion = m.getConnection();
			
		} catch (Exception e) {
			System.err.println("No se pudo crear la conexion");
		}
			
	}
	
	public static DAOFactoryJDBC getDAOFactory () {
		if (f == null) {
			f = new DAOFactoryJDBC();
		}
		return f;
	}
	
	
	
	
}
