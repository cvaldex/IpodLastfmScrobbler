package cl.cvaldex.scrobbler.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.lastpod.TrackItem;

public class CSVParser {
	public static List<TrackItem> fileParser(String absoluteFilePath) throws Exception{
		File file = new File(absoluteFilePath);
		
		if(!file.exists() || !file.canRead()){
			throw new Exception("Error reading file: " + absoluteFilePath);
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		
		String line = null;
		List<TrackItem> trackList = new ArrayList<TrackItem>();
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
	
	private static TrackItem parseLine(String line) throws Exception{
		TrackItem trackItem = new TrackItem();
		
		String [] fields = line.split("\t");
		if(fields.length < 4){
			throw new Exception("Campos mínimos para scrobbling son Artista, Album, Nombre Track, Largo y Playcount");
		}
		
		trackItem.setArtist(fields[0]);
		trackItem.setAlbum(fields[1]);
		trackItem.setTrack(fields[2]);
		trackItem.setPlaycount(Long.parseLong(fields[3]));
		
		//Valores por defecto
		trackItem.setTrackid(0);
		trackItem.setLastplayed(System.currentTimeMillis());
		trackItem.setLength(4000);
		
		return trackItem;
	}
}
