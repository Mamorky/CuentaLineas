package dam.psp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.omg.CORBA.PUBLIC_MEMBER;

class Hilo extends Thread{

	ProcessBuilder pBuilder;
	String[] valores;
	
	public Hilo(ProcessBuilder pBuilder) {
		this.pBuilder = pBuilder;
	}
	
	@Override
	public void run() {
		try {
			Process process = pBuilder.start();	
			BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
			
			String Linea = "";
			
			Linea = bReader.readLine();
			
			if(Linea == null) {
				return;
			}
			
			valores = Linea.split("\\s+");
			
			System.out.println("Lineas ->"+valores[valores.length-4]+" en fichero -> "+valores[valores.length-1]);
			System.out.println("Palabras ->"+valores[valores.length-3]+" en fichero -> "+valores[valores.length-1]);
			System.out.println("Caracteres ->"+valores[valores.length-2]+" en fichero -> "+valores[valores.length-1]);
			
			CuentaLineas.lineasTotales += Integer.valueOf(valores[valores.length-4]);
			
			bReader.close();
			
			if(process!=null)
				process.destroy();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

public class CuentaLineas {
	
	public static int lineasTotales = 0;
	public static Object mutex = new Object();
	
	public static void main(String[] args) {
		String[] ficheros = new String[args.length];
		ProcessBuilder[] processBuilders = new ProcessBuilder[ficheros.length];
		Hilo[] hilos = new Hilo[ficheros.length];
		
		for (int i = 0; i < args.length; i++) {
			ficheros[i] = args[i].toString();
			processBuilders[i] = new ProcessBuilder("wc",ficheros[i].toString());
			Hilo hilo = new Hilo(processBuilders[i]);
			hilos[i] = hilo;
		}
		
		for (int i = 0; i < hilos.length; i++) {
			hilos[i].start();
		}
		
		for (int i = 0; i < hilos.length; i++) {
			try {
				hilos[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("En total hay "+lineasTotales+" lineas");
	}
}
