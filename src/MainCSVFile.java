import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import cl.cvaldex.scrobbler.main.BasicScrobbler;
import cl.cvaldex.scrobbler.parser.CSVParser;
import de.umass.lastfm.scrobble.ScrobbleData;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MainCSVFile {
	//private final Logger log = LogManager.getLogger(this.getClass().getName());
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
		Logger log = LogManager.getLogger(MainCSVFile.class.getName());
		if(args.length < 7){
			log.error("Error en la ejecución: MainCVSFile filePath user password sendDelay sendLog voidScrobbles useProxy");
			System.exit(1);
		}

		String filePath = args[0];
		String lastfmUser = args[1];
		String lastfmPassword = args[2];
		boolean scrobble = Boolean.valueOf(args[3]);
		long delay = Long.parseLong(args[4]);
		int scrobblingLogAmount = Integer.parseInt(args[5]);
		int voidScrobbles = Integer.parseInt(args[6]);
		
		boolean useProxy = Boolean.valueOf(args[7]);
		
		if(useProxy){
			if(args.length < 9){
				log.error("Error en la ejecución: Faltan proxy.host y proxy.port");
				System.exit(1);
			}
			String proxyHost = args[8];
			String proxyPort = args[9];
			
			log.debug("Proxy Host: " + proxyHost);
			log.debug("Proxy Port: " + proxyPort);
			
			//setear proxy
			System.setProperty("http.proxyHost", proxyHost);
	        System.setProperty("http.proxyPort", proxyPort);
	        System.setProperty("https.proxyHost", proxyHost);
	        System.setProperty("https.proxyPort", proxyPort);
		}

		System.out.println("Iniciando el proceso");
		
		log.debug("File path: " + filePath);
		log.debug("Lastfm user: " + lastfmUser);
		log.debug("Scrobbling delay: " + delay);
		log.debug("Scrobbling log amount: " + scrobblingLogAmount);
		log.debug("Scrobbling to void: " + voidScrobbles);
		log.debug("Use Proxy: " + useProxy);
		
		List<ScrobbleData> tracks = (List<ScrobbleData>) CSVParser.fileParser(filePath);
		
		printScrobbles(tracks);
		
		System.out.println("Total Scrobbles: " + countScrobbles(tracks));
		
		//ejecutar el scrobbler
		if(scrobble){
			BasicScrobbler.scrobble(tracks, lastfmUser, lastfmPassword, delay, scrobblingLogAmount, voidScrobbles);
		}
	}
	
	static int countScrobbles(List<ScrobbleData> tracks){
		int counter = 0;
		
		for(ScrobbleData track: tracks){
			counter += track.getPlayCount();
		}

		return counter;
	}
	
	static void printScrobbles(List<ScrobbleData> tracks){
		for(ScrobbleData track: tracks){
			if(track.getPlayCount() > 0){
				System.out.println(track.getArtist() + " - " + track.getAlbum() + " - " + track.getTrack() + " - " + formatDate(new Date(track.getTimestamp() * 1000)));
			}
		}
		
	}
	
	static String formatDate(Date date){
		DateFormat df =  DateFormat.getDateInstance(DateFormat.FULL);
		
		return df.format(date);
	}

}
