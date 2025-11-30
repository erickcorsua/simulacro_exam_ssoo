package ssoo.servidor;

import java.io.File;
import java.io.FileWriter;

public class HiloLogger implements Runnable{
	
	
	private File mi_txt;
	private FileWriter escritorFileWriter;
	private Logger nuestro_logger;
	
	
	public HiloLogger(Logger logger) {
		
		nuestro_logger = logger;
		
		try {
			
			mi_txt = new File("D:\\java_projects\\java_proyects_jdk_eclip_25\\simulacro_exam\\el_esperado.txt");
			escritorFileWriter = new FileWriter(mi_txt);
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print("xxxxxxxxxxxxxxxxx no puedo crear el earchivo xxxxxxxxxxxxxxxxxxxxx");
		}
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			
			String txt = nuestro_logger.escribirlog(); 
			escritorFileWriter.append(txt);
			
		} catch (Exception e) {
			// TODO: handle exception
			
			System.out.print("xxxxxxxxxxxxxxxxx no puedo escribir xxxxxxxxxxxxxxxxxxxxx");
		}
		
	}

}
