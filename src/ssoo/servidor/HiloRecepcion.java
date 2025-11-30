/*********************************************************
 *
 * ALUMNOS QUE HAN REALIZADO ESTA PRÁCTICA:
 *
 * GRUPO: so-l08d-16
 *
 * ALUMNO 1
 *   Nombre: Javier Jaen Naharro
 *   Correo: j.jaen@alumnos.upm.es
 *
 * ALUMNO 2
 *   Nombre: Erick Correa Suarez
 *   Correo: e.csuarez@alumnos.upm.es
 *
 *********************************************************/

package ssoo.servidor;

import java.io.IOException;

import ssoo.telemetría.estación.*;

public class HiloRecepcion implements Runnable {
	
	// =================================================
	// =================atributos=======================
	// =================================================

	private ColaTrabajos cola_trabajos_global;
	
	// =================================================
	// =================constructor=====================
	// =================================================

	public HiloRecepcion(ColaTrabajos cola_trabajos_global) {
		
		this.cola_trabajos_global = cola_trabajos_global;
		
	}
	
	// =================================================
	// =================metodos=========================
	// =================================================

	@Override
	public void run() {
		
		try {
			
			Receptor receptor = new Receptor();
			
			//no hace falta un constructor, recibe el que le da java por defecto, que es invisible.
		
			System.out.println("["+ Thread.currentThread().getName() +"]"+"Receptor activo. Esperando peticiones ...");
			
			while(true) {
				
				// Solo usar las peticiones recibidas, ¡no instancies! porque no se puede, es protected
				//Aqui lo que haces es cargar en la peticion recibida en un objeto de tipo peticion,
				//pero no creas un nuevo objeto
				Petición peticion = receptor.recibirPetición();
				
				if(peticion != null) { // si ha llegado la peticion tiramos el hilo de peticion
					
					System.out.println("["+ Thread.currentThread().getName() +"]"+"Encargo recibido");
					
					Thread hiloPeticionThread = new Thread(new HiloPeticion(peticion, cola_trabajos_global));
					hiloPeticionThread.start();
					
				} 
			}
			
		} catch (IOException e) {
			
			System.out.println("Error al iniciar el receptor o durante la recepcion:" + e.getMessage());
			e.printStackTrace();
		}
	
	}
	
}

