package controlador;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import entidades.ProgramProperties;
import principal.Principal;

public class PropertiesService {
	
	public PropertiesService() {
		cargarProperties();
	}
	
	
	
	private void cargarProperties() {
		Properties p = new Properties();
		try (InputStream input = Principal.class.getClassLoader().getResourceAsStream("application.properties")) {
			p.load(input);

			ProgramProperties.usuarioAdmin = p.getProperty("usuarioAdmin");
			ProgramProperties.passwordAdmin = p.getProperty("passwordAdmin");
			ProgramProperties.credenciales = p.getProperty("credenciales");
			ProgramProperties.espectaculos = p.getProperty("espectaculos");
			ProgramProperties.paises = p.getProperty("paises");

		} catch (FileNotFoundException e) {
			System.out.println("No pude encontrar el fichero de properties");
		} catch (IOException e) {
			System.out.println("Hubo problemas al leer el fichero de properties");
		}
	}
}
