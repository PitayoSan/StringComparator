import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JOptionPane;

public class CompararString {
	
	private String patron;
	private String texto;
	private File path; 
	private String[][] kmpTable;
	private int[] kmpTableIndex;
	private int coincidencias;

	public void ingresaTexto() {
		this.patron = this.texto = "";
		this.coincidencias = 0;
		do {
			this.patron = JOptionPane.showInputDialog("Ingresa el patron a buscar: \nDebe contener al menos una letra o simbolo");
			if(this.patron == null) {
				System.exit(0);
			}
		} while(this.patron.equals(""));
		
		System.out.println("Patrón ingresado: " + this.patron);
		
		System.out.println("Creando tabla...");
		System.out.println();
		
		this.hacerTabla(this.patron);
		this.imprimirKMP();
		
		int opcion = JOptionPane.showConfirmDialog(null,"Elige YES si deseas ingresar texto plano, o NO si deseas"
				+ " ingresar la dirección a un archivo de texto");
		if( opcion == 0) {
			do {
				this.texto = JOptionPane.showInputDialog("Ingresa el texto en el cual se va a buscar: \nDebe contener al menos una letra o simbolo");
				if(this.texto == null) {
					System.exit(0);
				}
			} while(this.texto.equals(""));
			System.out.println("Texto ingresado: " + this.texto);
			System.out.println();
			System.out.println("Comenzando busqueda");
			System.out.println();
			this.fuerzaBruta(this.texto, this.patron);
			System.out.println("Busqueda finalizada");
			if(this.coincidencias == 0) {
				System.out.println("No se encontraron coincidencias");
			}
		} else if(opcion == 1) {
			String possiblePath = "";
			do {
				possiblePath = JOptionPane.showInputDialog("Ingresa el path del archivo");
				if(possiblePath == null) {
					System.exit(0);
				}
			} while(possiblePath.equals(""));
			this.path = new File(possiblePath);
			
			System.out.println("Path ingresado: " + this.path.getPath());
			System.out.println();
			System.out.println("Comenzando busqueda");
			System.out.println();
			this.searchFiles(this.path);
			System.out.println("Busqueda finalizada");
			if(this.coincidencias == 0) {
				System.out.println("No se encontraron coincidencias");
			}
		} else if(opcion == 2) {
			System.exit(0);
		}
	}
	
	private void searchFiles(File file) {

		try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
		    String line = null;
		    int lineas = 1;
		    while ((line = reader.readLine()) != null) {
				this.fuerzaBruta(line, this.patron, lineas);
				lineas++;
		    }
		} catch (IOException x) {
			System.out.println("El archivo " + file.getPath() + " no se pudo leer");
		}
	}
	
	private void fuerzaBruta(String strTexto, String strPatron) {
		int busquedaActual = 0;
		char[] texto = strTexto.toCharArray();
		char[] patron = strPatron.toCharArray();
		int endPos = texto.length - patron.length+1;
		OUTER:
		for(int pos = 0; pos < endPos; pos++) {
			for(int i = 0; i < patron.length; i++) {
				if(texto[pos + i] != patron[busquedaActual]) {
					pos += (busquedaActual == 0) ? busquedaActual : busquedaActual - 1;
					busquedaActual = this.kmpTableIndex[busquedaActual];
					continue OUTER;
				}
				busquedaActual++;
			}
			System.out.println("Coincidencia en la posición: " + pos);
			this.coincidencias++;
			pos += busquedaActual - 1;
			busquedaActual = 0;
		}
	}
	
	private void fuerzaBruta(String strTexto, String strPatron, int linea) {
		int busquedaActual = 0;
		char[] texto = strTexto.toCharArray();
		char[] patron = strPatron.toCharArray();
		int endPos = texto.length - patron.length+1;
		OUTER:
		for(int pos = 0; pos < endPos; pos++) {
			for(int i = 0; i < patron.length; i++) {
				if(texto[pos + i] != patron[busquedaActual]) {
					pos += (busquedaActual == 0) ? busquedaActual : busquedaActual - 1;
					busquedaActual = this.kmpTableIndex[busquedaActual];
					continue OUTER;
				}
				busquedaActual++;
			}
			System.out.println("Coincidencia en la posición: " + pos + " en la linea " + linea);
			this.coincidencias++;
			pos += busquedaActual - 1;
			busquedaActual = 0;
		}
	}
	
	private void hacerTabla(String patron) {
		this.kmpTable = new String[3][patron.length()+1];
		this.kmpTableIndex = new int[patron.length()+1];
		for(int charAt = 0; charAt < patron.length()+1; charAt++) {
			this.kmpTable[0][charAt] = patron.substring(0, charAt);
			this.kmpTableIndex[charAt] = this.coincidenCaracteres(this.kmpTable[0][charAt]);
			this.kmpTable[1][charAt] = patron.substring(0, this.kmpTableIndex[charAt]);
		}
	}
	
	private int coincidenCaracteres(String patron) {
		int coincidencias = 0;
		int largo = patron.length();
		int prefijoPosible = largo-1;
		int index = largo-2;
		while(index >= 0) {
				if(coincidencias > 0 && patron.charAt(index) != patron.charAt(prefijoPosible)) {
					index += coincidencias-1;
					coincidencias = 0;
					prefijoPosible = largo-1;
				}
				char h1 = patron.charAt(index);
				char h2 = patron.charAt(prefijoPosible);
				if(h1 == h2) {
					coincidencias++;
					prefijoPosible--;
				}
				index--;
		}
		return coincidencias;
	}
	
	private void imprimirKMP() {
		System.out.println("Imprimiendo tabla: ");
		System.out.println();
		for(int i = 0; i < this.kmpTableIndex.length; i++) {
			System.out.print(i + "	");
			System.out.print(this.kmpTable[0][i] + "		");
			System.out.print(this.kmpTable[1][i] + "		");
			System.out.print(this.kmpTableIndex[i]);
			System.out.println();
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		CompararString cs = new CompararString();
		cs.ingresaTexto();
	}
}
