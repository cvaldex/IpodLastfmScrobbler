package cl.cvaldex.scrobbler.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


//import org.lastpod.TrackItem;
import de.umass.lastfm.scrobble.ScrobbleData;

public class CSVParser {
	public static final int DEFAULT_DURATION = 4000;
	public static List<ScrobbleData> fileParser(String absoluteFilePath) throws Exception{
		File file = new File(absoluteFilePath);
		
		if(!file.exists() || !file.canRead()){
			throw new Exception("Error reading file: " + absoluteFilePath);
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		
		String line = null;
		List<ScrobbleData> trackList = new ArrayList<ScrobbleData>();
		int currentLine = 1;
		
		while((line = reader.readLine()) != null){
			//saltar las lineas que empiezan por # ya que son comentarios
			if(!line.startsWith("#")){
				try{
					//quitar el tab inicial que reemplaza al # cuando es línea válida
					trackList.add(parseLine(line.trim()));
				}
				catch(Exception e){
					System.out.println("Error parsing line " + currentLine + ": " + line);
				}
			}
			currentLine++;
		}
		
		reader.close();

		return trackList;
	}
	
	private static ScrobbleData parseLine(String line) throws Exception{
		
		
		String [] fields = line.split("\t");
		if(fields.length < 4){
			throw new Exception("Campos mínimos para scrobbling son Artista, Album, Nombre Track, Largo y Playcount");
		}

		ScrobbleData scrobble = new ScrobbleData();
		
		scrobble.setArtist(fields[0]);
		scrobble.setAlbum(fields[1]);
		scrobble.setTrack(fields[2]);
		scrobble.setPlayCount(Integer.parseInt(fields[3]));
		scrobble.setDuration(DEFAULT_DURATION);

		return scrobble;
	}
}
