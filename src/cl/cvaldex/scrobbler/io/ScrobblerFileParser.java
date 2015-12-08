package cl.cvaldex.scrobbler.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import cl.cvaldex.scrobbler.dto.CustomSubmissionData;
import cl.cvaldex.scrobbler.dto.ParseFileException;

import net.roarsoftware.lastfm.scrobble.Source;
import net.roarsoftware.lastfm.scrobble.SubmissionData;

public class ScrobblerFileParser {
	/**
	 * Método que lee desde un archivo de datos los campos necesarios para generar un scrobbling en la API de last.fm
	 * @param filePath archivo del que se debe extraer la información de scrobbling
	 * @return Collection con objetos con la informacion a enviar a la API de last.fm
	 * @throws IOException en caso de problemas con la lectura del archivo
	 */
	public static Collection<CustomSubmissionData> readFromFile(String filePath) throws ParseFileException{
		Collection<CustomSubmissionData> scrobblings = new ArrayList<CustomSubmissionData>();
		String line = null;
		BufferedReader reader = null;
		long readedLines = 0;
		int totalScrobblings = 0;
		
		try{
			reader = new BufferedReader(new FileReader(filePath));
			Scanner scanner = null;
			CustomSubmissionData submissionData = null;
			String artist = null;
			String track = null;
			String album = null;
			int length = 0;
			int trackNumber = 0;
			int playCount = 0;
			long startTime = 0;
						
			while((line = reader.readLine()) != null){
				readedLines++;				
				if(line.trim().length() == 0){
					continue;
				}
				
				scanner = new Scanner(line).useDelimiter("\t");
				
				artist = scanner.next().trim();
				album  = scanner.next().trim();
				track  = scanner.next().trim();
				trackNumber = scanner.nextInt();
				
				//calcular en segundos
				length = scanner.nextInt();
				length = Math.round(length / 1000);
				
				//startTime = 
				//System.out.println("-------> " + scanner.next());
				playCount = scanner.nextInt();

				//setear el startTime
				//startTime = (System.currentTimeMillis() / 1000) - 117;
				
				submissionData = new CustomSubmissionData(artist , track , album , length , trackNumber , Source.USER , startTime , playCount);
				
				scrobblings.add(submissionData);

				//solo por log
				totalScrobblings += playCount;
			}
		}
		catch(IOException ioe){
			throw new ParseFileException("Error al parsear el archivo " + filePath , ioe);
		}
		finally{
			System.out.println("Lineas parseadas:     " + readedLines);		
			System.out.println("Scrobblings a enviar: " + totalScrobblings);
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
				
		return scrobblings;
	}
}
