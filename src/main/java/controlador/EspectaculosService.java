package controlador;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import entidades.Espectaculo;
import entidades.ProgramProperties;

public class EspectaculosService {

	public EspectaculosService() {
		espectaculos = cargarEspectaculos();
	}
	
	private ArrayList<Espectaculo> espectaculos = null;

	public ArrayList<Espectaculo> getEspectaculos() {
		return espectaculos;
	}

	private void setEspectaculos(ArrayList<Espectaculo> espectaculos) {
		this.espectaculos = espectaculos;
	}
	
	/*
	 * 
	 */
	public ArrayList<Espectaculo> cargarEspectaculos() {
		ArrayList<Espectaculo> espectaculos = new ArrayList<Espectaculo>();
		File archivo = new File(ProgramProperties.espectaculos);
		if (!archivo.exists()) {
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(ProgramProperties.espectaculos, true))) {
				oos.writeObject(espectaculos);
				oos.close();
			} catch (FileNotFoundException e) {
				System.out.println("archivo no encontrado.");
			} catch (IOException e) {
				System.out.println("Error de escritura del archivo");
			}
		} else {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ProgramProperties.espectaculos))) {

				espectaculos = (ArrayList<Espectaculo>) ois.readObject();
				ois.close();
			} catch (FileNotFoundException e) {
				System.out.println("archivo espectaculos no encontrado");
			} catch (IOException e) {
				System.out.println("error de lectura del archivo al cargar");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("error de conversion de tipos.");
			}

		}

		return espectaculos;
	}
	

	public void mostrarEspectaculos() {
		ArrayList<Espectaculo> listaEspectaculos = new ArrayList<>();
		if (espectaculos == null || espectaculos.isEmpty()) {
			System.out.println("No hay espectáculos disponibles.");
			return;
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ProgramProperties.espectaculos))) {
			for (Espectaculo e : espectaculos) {
				listaEspectaculos.add(e);
			}
		} catch (FileNotFoundException e1) {
			System.out.println("Archivo de Espectaculos no encontrado");
			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println("Error de lectura o escritura del archivo Espectaculos");
			e1.printStackTrace();
		}
		for (Espectaculo e : listaEspectaculos) {
			System.out.println(e);
		}
	}
	
	public void guardarEspectaculo(Espectaculo aGuardar) {

		ArrayList<Espectaculo> espectaculos = new ArrayList<Espectaculo>();
		espectaculos = cargarEspectaculos();

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ProgramProperties.espectaculos))) {
			espectaculos.add(aGuardar);
			oos.writeObject(espectaculos);
			System.out.println("archivo modificado.");

		} catch (FileNotFoundException e) {
			System.out.println("no se pudo encontrar el archivo de espectaculos");
		} catch (IOException e) {
			System.out.println("error al escribir el archivo de espectaculos");
			e.printStackTrace();
		}
	}
	
}
