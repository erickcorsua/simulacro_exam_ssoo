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

import ssoo.telemetría.Telemetría;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Trabajo simple que representa la tarea de analizar una única telemetría.
 * 
 * - El productor (HiloPeticion) crea el Trabajo y lo encola.
 * - El consumidor (HiloAnalizador) procesa la telemetría y llama completar(resultado).
 * - El productor puede esperar al resultado con awaitResultado().
 *
 *
 *Seguimos las directrices de la organizacion del codigo, lo que buscamos 
 *es encapsular el lock para que no este disperso, sino que ordenado 
 *y centralizado en una clase, lo que hacemos es hacer un lock para asegurarno que entre
 *el hilo analizador y el hilo peticion no haya problemas de sincronizacion, sobre todo que el 
 *hilo peticion pueda bloquearse y liberar cpu mientras el hilo analizador acaba su tarea
 *
 * Sincronización: ReentrantLock + Condition (await / signal).
 */

public class Trabajo {
	
//=================================================    
//=================atributos=====================
//=================================================	
	private static long ID = 0; //un id
	
	private final long id;
    private final Telemetría telemetriaOriginal; //no es relevante, se podria quitar, (para depuracion)
    private final int indiceEnLista;             //lo mismo, no es relevante pero esta ahi para depurar 
    private final String tituloEncargo;          //puesto ahi para depuracion
    
    // sincronización POR INSTANCIA (NO STATIC) muy muy relevante
    
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition resultadoReady = lock.newCondition();
    
    // resultado protegido por 'lock' y flags privados  (muy muy relevante)
    
    private Telemetría resultadoTelemetria = null;
    private boolean completado = false;
    
 //=================================================    
 //=================constructor=====================
 //=================================================
	
	public Trabajo(Telemetría telemetria_original, String titulo_Encargo, int indice) {
		
		if( telemetria_original == null) {
			throw new IllegalArgumentException("telemetria nula");
		}
		this.id = incrementar_id();
		this.telemetriaOriginal = telemetria_original;
		this.indiceEnLista = indice;
		this.tituloEncargo = titulo_Encargo;
		
	}
	
//=================================================    
//=================metodos=========================
//=================================================
	
	//------------------privados----------------
	
	private static synchronized long incrementar_id() {
		return ID++;
	}
	
	//-----------------publicos-----------------
	
    //getters sencillitos

    public long getId() {
        return id;
    }

    public Telemetría getTelemetriaOriginal() {
        return telemetriaOriginal;
    }

    public String getTituloEncargo() {
        return tituloEncargo;
    }

    public int getIndiceEnLista() {
        return indiceEnLista;
    }

	//------------------------------------------
	
    /**
     * Espera hasta que el resultado esté disponible y lo devuelve.
     *
     *Parecido a lo que tenemos en las diapositivas de organizacion del codigo
     *este seria un metodo por decirlo asi que lo implementaria un consumidor, espera 
     *a que se complete el producto que en este caso es el resultadoTelemetria
     *
     * @return telemetría analizada
     * @throws InterruptedException si el hilo es interrumpido mientras espera
     */
    
	public Telemetría awaitreulstadoTelemetria() throws InterruptedException {
		
		lock.lock();
		try {
			
			while(!completado){
				
				resultadoReady.await(); //bloquea el hilo y pasa a otro hilo hasta que le llegue esa señal
				
			}
			return resultadoTelemetria;
		} finally {
			lock.unlock();
		}
	}
	
	/**
     * Método que debe llamar el HiloAnalizador cuando termina el procesamiento. 
     * Señala a cualquier espera en curso.
     *
     * @param resultado telemetría analizada (no null)
     * @throws IllegalStateException si ya estaba completado
     */
	
    public void completar(Telemetría Telemetria_resul_de_Thanalizador) {
    	if(Telemetria_resul_de_Thanalizador == null) {
    		
    		throw new IllegalArgumentException("La Telemetria no puede ser nula");
    		
    	}
    	
    	lock.lock();
    	try {
    		
    		if (completado) {
                // ya estaba completado: ignoramos (política idempotente)
                return;
            }
    		
    		this.resultadoTelemetria = Telemetria_resul_de_Thanalizador;
    		this.completado = true;
    		resultadoReady.signal(); //manda la señal de que esta ya el resultado, 
			
		} finally {
			
			lock.unlock();
	       
		}
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
	//cosas para depurar
    
    /** Consulta no bloqueante (usa lock internamente para visibilidad) */
    public boolean isCompletado() {
        lock.lock();
        try {
            return completado;
        } finally {
            lock.unlock();
        }
    }

    /** Libera la referencia al resultado para ayudar al GC (úsalo con cuidado). */
    public void clearResultado() {
        lock.lock();
        try {
            resultadoTelemetria = null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        String tname = "n/a";
        try {
            if (telemetriaOriginal != null) tname = telemetriaOriginal.getNombre();
        } catch (Throwable ignore) { }
        String titulo = (tituloEncargo != null) ? tituloEncargo : "sin-encargo";
        return "Trabajo{id=" + id + ", encargo=" + titulo +
                ", indice=" + indiceEnLista + ", telemetria=" + tname +
                ", completado=" + completado + "}";
    }
}
    

