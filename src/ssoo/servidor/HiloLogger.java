package ssoo.servidor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HiloLogger implements Runnable {

	private final File mi_txt;
	private final Logger nuestro_logger;

	public HiloLogger(Logger logger) {
		this.nuestro_logger = logger;
		// Usar fichero relativo en el directorio de ejecución para evitar rutas hardcodeadas
		this.mi_txt = new File("logs.txt");
	}

	@Override
	public void run() {
		// Abrimos el writer en modo append y lo cerramos al terminar.
		try (FileWriter fw = new FileWriter(mi_txt, true);
			 BufferedWriter bw = new BufferedWriter(fw)) {

			while (!Thread.currentThread().isInterrupted()) {
				// escribirlog devuelve un bloque con 10 mensajes o null si se interrumpe
				String txt = nuestro_logger.escribirlog();
				if (txt == null) {
					// hilo interrumpido o error, salimos
					break;
				}

				try {
					// Escribimos exactamente lo devuelto (ya contiene '\n' entre mensajes)
					bw.write(txt);
					bw.newLine(); // añadir línea final entre bloques
					bw.flush();
				} catch (IOException e) {
					System.err.println("HiloLogger: error al escribir en fichero: " + e.getMessage());
					// opcional: reintentar, esperar un poco, o terminar
				}
			}

		} catch (IOException e) {
			System.err.println("HiloLogger: no puedo abrir/crear el fichero logs.txt: " + e.getMessage());
		}

		System.out.println("[HiloLogger] terminado.");
	}
}