import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.lastpod.TrackItem;
import cl.cvaldex.scrobbler.main.BasicScrobbler;
import cl.cvaldex.scrobbler.parser.CSVParser;

public class MainCSVFile {

	/**
	 * @param args
	 * 0 = ruta del archivo a leer, en formato CSV
	 * 1 = usuario Last.fm
	 * 2 = password usuario Last.fm
	 * 3 = tiempo de espera en ele envío de los tracks
	 * 4 = cantidad de registros antes de escribir el log
	 * 5 = cantidad de scrobbles a desechar antes de comenzar a enviar
	 * @throws Exception 
	 */

	public static void main(String[] args) throws Exception {
		if(args.length < 6){
			System.out.println("Error en la ejecución: MainCVSFile filePath user password sendDelay sendLog voidScrobbles");
			System.exit(1);
		}
		
		String filePath = args[0];
		String lastfmUser = args[1];
		String lastfmPassword = args[2];
		boolean scrobble = Boolean.valueOf(args[3]);
		long delay = Long.parseLong(args[4]);
		int scrobblingLogAmount = Integer.parseInt(args[5]);
		int voidScrobbles = Integer.parseInt(args[6]);

		System.out.println("File path: " + filePath);
		System.out.println("Lastfm user: " + lastfmUser);
		System.out.println("Scrobbling delay: " + delay);
		System.out.println("Scrobbling log amount: " + scrobblingLogAmount);
		System.out.println("Scrobbling to void: " + voidScrobbles);
		
		//TrackItemParser tip = new ItunesDbParser(filePath , false , null , false);
		List<TrackItem> tracks = (List<TrackItem>) CSVParser.fileParser(filePath);
		
		printScrobbles(tracks);
		
		System.out.println("Total Scrobbles: " + countScrobbles(tracks));
		
		//ejecutar el scrobbler
		if(scrobble){
			BasicScrobbler.scrobble(tracks, lastfmUser, lastfmPassword, delay, scrobblingLogAmount, voidScrobbles);
		}
	}
	
	static int countScrobbles(List<TrackItem> tracks){
		int counter = 0;
		
		for(TrackItem track: tracks){
			counter += track.getPlaycount();
		}
		
		return counter;
	}
	
	static int printScrobbles(List<TrackItem> tracks){
		int counter = 0;
		
		for(TrackItem track: tracks){
			if(track.getPlaycount() > 0){
				System.out.println(track.getArtist() + " - " + track.getAlbum() + " - " + track.getTrack() + " - " + track.getPlaycount() + " - " + formatDate(new Date(track.getLastplayed() * 1000)));
			}
		}
		
		return counter;
	}
	
	static String formatDate(Date date){
		/*int counter = 0;
		
		for(TrackItem track: tracks){
			if(track.getPlaycount() > 0){
				System.out.println(track.getArtist() + " - " + track.getAlbum() + " - " + track.getTrack() + " - " + track.getPlaycount() + " - " + new Date(track.getLastplayed() * 1000));
			}
		}
		
		return counter;
		*/
		//String formatedDate = null;
		
		DateFormat df =  DateFormat.getDateInstance(DateFormat.FULL);
		
		
		return df.format(date);
	}

}
