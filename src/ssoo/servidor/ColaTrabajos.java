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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class ColaTrabajos implements ssoo.telemetría.Numerable{

	// =================================================
	// =================atributos=======================
	// =================================================

	private final BlockingQueue<Trabajo> queue;
	private final int capacidad;

	// =================================================
	// =================constructor=====================
	// =================================================

	public ColaTrabajos(int capacidad) { //aqui se supone que debe ser el num. medio de telemetrias que llegan por encargo *10

		if (capacidad <= 0) {

			throw new IllegalArgumentException("la capacidad no puede ser menor que 0");

		}

		this.capacidad = capacidad;
		this.queue = new ArrayBlockingQueue<Trabajo>(capacidad);

	}

	// =================================================
	// =================metodos=========================
	// =================================================

	public int numTrabajos() {
		
		return this.queue.size();
				
	}
	
	
	// encola bloqueante
	// encolar
	public void encolar(Trabajo trabajo_para_encolar) throws InterruptedException {

		if (trabajo_para_encolar == null) {

			throw new IllegalArgumentException("trabajo es null");

		}
		queue.put(trabajo_para_encolar);

	}

	// ----------------
	// encola NO bloqueante
	// offer

	public boolean offer(Trabajo trabajo_para_encolar) throws InterruptedException {
		
		if (trabajo_para_encolar == null) {

			throw new IllegalArgumentException("trabajo es null");

		}
		return queue.offer(trabajo_para_encolar);

	}

	// ----------------
	// pillar cosas de la cola
	// tomar
	
	public Trabajo tomar() throws InterruptedException {
		
		return queue.take();		
		
	}

	//algunos getters
	
	public Trabajo poll() {
		
		return queue.poll();
		
	}
	
	//-----------------
	public int size() {
		
		return queue.size();
		
	}
	
	//-----------------
	
	public int capacidad() {
		
		return capacidad;
		
	}

}
