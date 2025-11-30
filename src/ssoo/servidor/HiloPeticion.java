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

import ssoo.telemetría.*;
import ssoo.telemetría.Telemetría;
import ssoo.telemetría.estación.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Hilo que atiende una petición:
 *  - crea un Trabajo por cada telemetría del encargo,
 *  - encola los trabajos en la ColaTrabajos pasada en el constructor.
 *
 *
 * Actualmente solo encolamos; la recogida de resultados la harás más adelante.
 * 
 */

public class HiloPeticion implements Runnable {
    
	// =================================================
	// =================atributos=======================
	// =================================================

	private final Petición peticion;
	private final ColaTrabajos cola_de_trabajos;
    
    
	// =================================================
	// =================constructor=====================
	// =================================================

    public HiloPeticion(Petición peticion, ColaTrabajos colaTrabajos_global) {
    	if (peticion == null) throw new IllegalArgumentException("peticion null");                 //por mera cortesia, un poco de control de errores
        if (colaTrabajos_global == null) throw new IllegalArgumentException("colaTrabajos null");  //por mera cortesia, un poco de control de errores
        this.peticion = peticion;
        this.cola_de_trabajos = colaTrabajos_global;
       }
    
	// =================================================
	// =================metodos=========================   
	// =================================================

    //(solo tenemos metodo run)
    
    @Override
    public void run() {
        // P1. Mostrar en pantalla el nombre de la estación y los datos del encargo, basicamente para que sepas que esta pasando
        //===============================================================================================================================================    	
        Encargo encargo       = peticion.getEncargo();                                //encargo que recibo de la perticion
        Estación estacion     = peticion.getEstación();                               //estacion que recibo de la peticion
        List<Telemetría> telemetriasList = encargo.getTelemetrías();                  //lista de telemetrias que recibo del encargo que me ha pasado la peticion recibida    
        
        System.out.println("--------------------------------------------------");
        System.out.println("[" + Thread.currentThread().getName() +"] Empiezo");
        System.out.println("[" + Thread.currentThread().getName() +"] He recibido una peticion: Nombre de estación:" + estacion.getNombre());
        System.out.println("[" + Thread.currentThread().getName() +"] Telemetrías de encargo: " + telemetriasList.size());
        
        //===============================================================================================================================================
        
        // P2. Esperar 5 segundos
        try {
            Thread.sleep(5000); //esperamos 5 segundos  --------------------> simulacion de que esta realizando algo
            
        } catch (InterruptedException e) {
            System.err.println("Hilo de petición interrumpido.");
        }

        //************************Esto es lo nuevo de la fase 2 ***************************************
        
        List<Trabajo> trabajos = new ArrayList<>(); //una lista de trabajos donde annadiremos los trabajos que generemos por cada trabajo
        
        //bucle para encolar los trabajos de la lista de telemetrias
        for(int i = 0; i < telemetriasList.size(); i++) {  //ajusto el bucle con el tamaño de la lista de telemetrias que tengo
        	
        	Telemetría tele = telemetriasList.get(i);      //me declaro un obejeto telemetria auxiliar para cargar en el un objeto telemetria ya creado que esta en la lista
        	
        	Trabajo trabajo = new Trabajo(tele, encargo.getTítulo(), i);  //creo u nobjeto trabajo y al constructor le paso la telemetria y el titulo del encargo, junto con un id
        	trabajos.add(trabajo);                                        //añadimos el trabajo creado a una lista
        	
        	//intentamos encolar, la clase de cola trabajos, nos hace esto de forma Thread-safe, asi que no nos preocupamos de los cerrojos aqui
        	try {
        		
        		cola_de_trabajos.encolar(trabajo);
        		System.out.println("[" + Thread.currentThread().getName() + "] Encolado Trabajo id=" + trabajo.getId() + "telemetria= " + (tele != null ? tele.getNombre() : "null" ));
        		
				
			} catch (InterruptedException e) { //si no tenemos existo encolando
				// TODO: handle exception
				
				Thread.currentThread().interrupt();
				System.out.println("Interrumpido al encolar trabajo. Saliendo ...");
				return; // sale
			}        	
        }
        
        //*********************************************************************************************                
        
     // P3. Mensaje de despedida y terminar
        if(telemetriasList.isEmpty()) {
        	System.out.println("[" + Thread.currentThread().getName() + "] Encolo trabajo pero sin telemetrías.");
        }
        
        System.out.println("--------------------------------------------------");
        
        
        
        //tendremos que esperar, a que el hilo analizador acabe de hacer su analisis
        //para ello usaremos el metodo await de la clase trabajo, que espera la señal que nos da el hilo analizador
        //al acabar su analisis  
          
          List<Telemetría> tele_analizadas = new ArrayList<>();            //vamos a crear una lista con las telemetrias analizadas
          
          for (Trabajo trabajo : trabajos) { 
              try {
                  // metodo existente en clase Trabajo (espera hasta que el analizador llame completar)
                  Telemetría resultado = trabajo.awaitreulstadoTelemetria(); //esperamos a que el hilo analizador nos de la señal de que podemos obtener la telemetria analizada
                  
                  tele_analizadas.add(resultado);
                  System.out.println("[" + Thread.currentThread().getName() + "] Recibido resultado trabajo id=" + trabajo.getId());
                  
              } catch (InterruptedException e) {//por si algo falla
                  // restaurar flag y detener espera; generamos informe con lo que tengamos
                  Thread.currentThread().interrupt();
                  System.err.println("Interrumpido mientras esperaba resultado. Generando informe parcial.");
                  break;
              }
          }
        
          
          //crear los informes
          
          Índice indiceÍndice = new Índice(tele_analizadas);
          
          Informe informe = null;
          
          try {
        	  
              informe = new Informe("informe-"+encargo.getTítulo(), indiceÍndice , tele_analizadas);
              
          } catch (Throwable t) {
        	  
              System.err.println("No se pudo crear Informe: " + t.getMessage());
              
          }

          peticion.getEstación().enviar(informe);

          if (informe != null) {
              System.out.println("[" + Thread.currentThread().getName() + "] Informe creado: " + informe.getTítulo()
                      + " (telemetrías: " + informe.getCantidadTotal() + ")");
              // getPDF() devuelve una cadena ficticia con el PDF; lo mostramos por consola en la práctica
              System.out.println("[" + Thread.currentThread().getName() + "] PDF informe (simulado):\n" + informe.getPDF());
          } else {
              System.err.println("[" + Thread.currentThread().getName() + "] Informe no creado (error).");
          }

          // P6. Liberar referencias para GC
          for (Trabajo trabajo : trabajos) {
              trabajo.clearResultado();
          }
          trabajos.clear();

          System.out.println("[" + Thread.currentThread().getName() + "] Fin HiloPeticion.");
          System.out.println("--------------------------------------------------");
  
          
    }//run
    
}//class


        