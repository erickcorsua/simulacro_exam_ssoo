package ssoo.servidor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Logger {

	// =============atributos=========
	private final BlockingQueue<String> cola;
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition condi_10_msg = lock.newCondition();

	private boolean puedo_escribir = false;

	// =========constructor===========
	public Logger() {
		cola = new ArrayBlockingQueue<String>(15);
	}

	// Nota: en tu código original el método se llamó "registar" (sin 'r' extra).
	// Lo dejamos igual porque HiloAnalizador llama a logger.registar(...).
	public void registar(String mensaje_tiempo) {

		lock.lock();
		try {
			// Primero añadimos el mensaje al queue (bloqueante si está lleno).
			try {
				cola.put(mensaje_tiempo);
			} catch (InterruptedException e) {
				// Restablecer interrupción y salir
				Thread.currentThread().interrupt();
				return;
			}

			// Si ahora hay al menos 10 mensajes notificamos al HiloLogger
			if (cola.size() >= 10) {
				puedo_escribir = true;
				condi_10_msg.signal();
			}

		} finally {
			lock.unlock();
		}
	}

	// Devuelve un bloque con 10 mensajes (separados por '\n'), o null si se interrumpe.
	public String escribirlog() {
		lock.lock();
		try {
			// Esperar hasta que haya al menos 10 mensajes
			while (!puedo_escribir && cola.size() < 10) {
				try {
					condi_10_msg.await();
				} catch (InterruptedException e) {
					// Restaurar flag y devolver null para que el hilo logger termine.
					Thread.currentThread().interrupt();
					return null;
				}
			}

			// Tomamos exactamente 10 mensajes
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 10; i++) {
				try {
					String mensajetomado = cola.take();
					if (sb.length() > 0) sb.append("\n");
					sb.append(mensajetomado);
				} catch (InterruptedException e) {
					// Restaurar flag y devolver lo que tengamos (o null).
					Thread.currentThread().interrupt();
					return null;
				}
			}

			// Reiniciamos el flag para futuras escrituras
			puedo_escribir = false;

			return sb.toString();

		} finally {
			lock.unlock();
		}
	}
}