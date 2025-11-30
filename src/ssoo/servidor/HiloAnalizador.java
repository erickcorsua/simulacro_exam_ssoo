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

import ssoo.telemetría.Analizador;
import ssoo.telemetría.Telemetría;

public class HiloAnalizador implements Runnable{

	
	// =================================================
	// =================atributos=======================
	// =================================================

	private ColaTrabajos cola;
	private Analizador analizador;
	
	private Logger logger;
	
	// =================================================
	// =================constructor=====================
	// =================================================

	public HiloAnalizador(ColaTrabajos cola, Logger logger){
		
		if(cola == null) throw new IllegalArgumentException("la cola no puede ser nula");
		this.cola = cola;
		this.analizador = new Analizador();
		this.logger = logger;
		
	}
	
	// =================================================
	// =================metodos=========================
	// =================================================
	
	public void run(){
		
		Thread Hilo_analizador_Actual = Thread.currentThread();
		
		try {
			
			while(!Thread.currentThread().isInterrupted()){
				
				//extraer trabajo de la cola
				Trabajo trabajo_de_la_cola = cola.tomar();
				
				
				//procesar el trabajo
				Telemetría recibidaTelemetría = trabajo_de_la_cola.getTelemetriaOriginal();
				Telemetría salida;
				
				try {
					long t0 = System.currentTimeMillis();
					salida = analizador.analizar(recibidaTelemetría);
					long t1 = System.currentTimeMillis();
					
					int tiempo_transcurrido = (int)(t1-t0);
					
					String mensaje_para_logger = "Telemetría " + recibidaTelemetría.toString()+" procesada en "+tiempo_transcurrido +" ms";
					System.out.println("----------------------------------------->" + mensaje_para_logger);
					//Telemetría "telemetria-94" procesada en 5821 ms 
					logger.registar(mensaje_para_logger);
					
				} catch (Exception e) {
					// Si falla el análisis, usamos la entrada como resultado de fallback
					
                    System.err.println("[" + Hilo_analizador_Actual.getName() + "] Error analizando, usando original: " + e.getMessage());
                    salida = recibidaTelemetría;
				}
				
				//avisar al productor (HiloPeticion) mediante completar()				
                try {
                    
                	trabajo_de_la_cola.completar(salida);
                    
                } catch (Throwable e) {
                    // completar no debería fallar, pero lo registramos por si acaso
                    System.err.println("[" + Hilo_analizador_Actual.getName() + "] Error al completar trabajo id=" + trabajo_de_la_cola.getId() + ": " + e.getMessage());
                }

                System.out.println("[" + Hilo_analizador_Actual.getName() + "] Trabajo procesado id=" + trabajo_de_la_cola.getId());
			}
			
		}catch (InterruptedException e) {
            // Restaurar flag y salir
            Thread.currentThread().interrupt();
        }
        System.out.println("[" + Hilo_analizador_Actual.getName() + "] HiloAnalizador terminado.");
	 
	}

}
