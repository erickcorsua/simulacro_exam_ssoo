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

import ssoo.telemetría.panel.*;

public class ServidorTelemetria {

   
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		final int CAPACIDAD_COLA = 100;
	    final ColaTrabajos COLA_TRABAJOS = new ColaTrabajos(CAPACIDAD_COLA);//le pongo 20 porque no se cuanto ponerle
		final Logger nuestro_logger = new Logger();
	    
	    PanelVisualizador.getPanel().registrarColaTrabajos(COLA_TRABAJOS);
	    		
	    
		Thread hiloRecepcionThread = new Thread(new HiloRecepcion(COLA_TRABAJOS));
		hiloRecepcionThread.start();
		
		 // decidir número de analizadores según núcleos disponibles
        int cores = Runtime.getRuntime().availableProcessors();
        int nAnalizadores = Math.max(1, cores); // usa 'cores' (o cores-1 si prefieres reservar uno)
        int nhilo_analisis;

        
        if(nAnalizadores > 8) {
        	
        	nhilo_analisis = 8;
        	
        }
        else {
        	
        	nhilo_analisis = nAnalizadores;
        	
        }
        
        //java -cp p2-telemetria.jar ssoo.telemetría.estación.simulador.SimuladorEstación 
        //vil1 10 12 4 900 300 5
        
        // arrancar hilos analizadores
        for (int i = 0; i < nhilo_analisis; i++) {
        	
            Thread analizadorThread = new Thread(new HiloAnalizador(COLA_TRABAJOS, nuestro_logger), "Analizador-" + i);
            analizadorThread.start();
            
        }

        System.out.println("Servidor iniciado: cores=" + cores + ", analizadores=" + nAnalizadores);
		
        Thread hilologger = new Thread(new HiloLogger(nuestro_logger));
        hilologger.start();
        
        
	}
} 
