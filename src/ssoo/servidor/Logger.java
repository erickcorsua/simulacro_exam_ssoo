package ssoo.servidor;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Logger {

	// =============atributos=========

	private BlockingQueue<String> cola;
	
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition condi_10_msg = lock.newCondition();
	
	
	
	private boolean puedo_escribir =false;
	private String msg_construido = new String();

	// =========constructor===========

	public Logger() {

		// de momento ya veremos que pongo aqui
		cola = new ArrayBlockingQueue<String>(15);

	}

	public void registar(String mensaje_tiempo) {
		
		lock.lock();
		try {
			
			if (cola.size() == 10) {

				condi_10_msg.signal();
				puedo_escribir = true;

			} else {

				try {

					cola.put(mensaje_tiempo);

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			
		} finally {
			// TODO: handle finally clause
			lock.unlock();
		}
	}
	
	
	public String escribirlog() {
		
		lock.lock();
		try {
			
			while (!puedo_escribir) {
				
				try {
					
					condi_10_msg.await();
					
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
						
			for (int i = 0; i < 10; i++) {
				
				try {
					String mensajetomado = cola.take();
					msg_construido = msg_construido.concat("\n"+mensajetomado);
					
				} catch (Exception e) {
					// TODO: handle exception
	
					
				}
				
			}
			return msg_construido;
			
		} finally {
			// TODO: handle finally clause
			lock.unlock();
		}		
	}
}
