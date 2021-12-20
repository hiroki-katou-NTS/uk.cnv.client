package nts.uk.client.exi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;

import nts.arc.time.GeneralDateTime;

public class LogManager {

	private static final String logfile;

	static {
		logfile = ExiClientProperty.getProperty(ExiClientProperty.LOG_FILE_PATH);
	}

	public static void init() {
		File file = new File(logfile);
		if(file.exists()) return;
		
		try {
			Files.createFile(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void out(String str, Object ... args) {
		out(String.format(str, args));
	}

	public static void out(String str) {
		system_out(str);
		outputLog(str);
	}

	public static void err(String str, Object ... args) {
		err(String.format(str, args));
	}

	public static void err(String str) {
		system_error(str);
		outputLog(str);
	}

	public static void err(Exception e) {
		system_error(e.toString());
		outputLog(e.toString());
	}
	
	private static void system_out(String str) {
		System.out.println("[" + GeneralDateTime.now().toString() + "]: " + str);
	}
	private static void system_error(String str) {
		System.err.println("[" + GeneralDateTime.now().toString() + "]: " + str);
	}

	private static void outputLog(String str) {
		String outputStr = "[" +GeneralDateTime.now().toString() + "]: " + str;
		try (FileOutputStream fos = new FileOutputStream(logfile, true);
				OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);) {

			bw.write(outputStr);
			bw.newLine();
			bw.flush();
		}
		catch(Exception e) {
			system_out(e.getMessage());
			e.printStackTrace();
		}
	}
}
