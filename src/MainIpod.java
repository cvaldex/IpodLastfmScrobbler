import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.lastpod.TrackItem;
import org.lastpod.parser.ItunesDbParser;
import org.lastpod.parser.TrackItemParser;

import cl.cvaldex.scrobbler.main.BasicScrobbler;
import de.umass.lastfm.scrobble.ScrobbleData;


public class MainIpod {

	/**
	 * @param args
	 * 0 = ruta a la base de datos de itunes
	 * 1 = usuario Last.fm
	 * 2 = password usuario Last.fm
	 * 3 = tiempo de espera en ele env�o de los tracks
	 * 4 = cantidad de registros antes de escribir el log
	 * 5 = cantidad de scrobbles a desechar antes de comenzar a enviar
	 * @throws InterruptedException 
	 * @throws IOException 
	 */

	public static void main(String[] args) throws IOException, InterruptedException {
		if(args.length < 8){
			System.out.println("Error en la ejecución: Main itunesDBPath user password sendDelay sendLog voidScrobbles random ramdomLength");
			System.exit(1);
		}
		
		String itunesPath = args[0];
		String lastfmUser = args[1];
		String lastfmPassword = args[2];
		boolean scrobble = Boolean.valueOf(args[3]);
		long delay = Long.parseLong(args[4]);
		int scrobblingLogAmount = Integer.parseInt(args[5]);
		int voidScrobbles = Integer.parseInt(args[6]);
		
		boolean random = Boolean.valueOf(args[7]);
		int randomLength = Integer.parseInt(args[8]);

		System.out.println("itunesDB path: " + itunesPath);
		System.out.println("Lastfm user: " + lastfmUser);
		System.out.println("Scrobbling delay: " + delay);
		System.out.println("Scrobbling log amount: " + scrobblingLogAmount);
		System.out.println("Scrobbling to void: " + voidScrobbles);
		System.out.println("Random?: " + random);
		
		if(random){
			System.out.println("Random Length: " + randomLength);
		}
		
		boolean useProxy = Boolean.valueOf(args[9]);
		
		if(useProxy){
			if(args.length < 11){
				System.out.println("Error en la ejecución: Faltan proxy.host y proxy.port");
				System.exit(1);
			}
			String proxyHost = args[10];
			String proxyPort = args[11];
			
			System.out.println("Proxy Host: " + proxyHost);
			System.out.println("Proxy Port: " + proxyPort);
			
			//setear proxy
			System.setProperty("http.proxyHost", proxyHost);
	        System.setProperty("http.proxyPort", proxyPort);
	        System.setProperty("https.proxyHost", proxyHost);
	        System.setProperty("https.proxyPort", proxyPort);
		}
		
		
		
		TrackItemParser tip = new ItunesDbParser(itunesPath , false , null , false);
		List<ScrobbleData> tracks = tip.parse();
			
		System.out.println("Total Scrobbles: " + countScrobbles(tracks));
		
		//ejecutar el scrobbler
		if(scrobble){
			if(random){
				//seleccionar elementos al azar desde la colecci�n
				tracks = getRandomElementsFromList(tracks, randomLength);
				System.out.println("Sub Total Scrobbles: " + countScrobbles(tracks));
			}
			
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
		DateFormat df =  DateFormat.getDateInstance(DateFormat.FULL);
		
		return df.format(date);
	}
	
	//@SuppressWarnings("unchecked")
	static List<ScrobbleData> getRandomElementsFromList(List<ScrobbleData> list , int length){
		List<ScrobbleData> finalList = new ArrayList<ScrobbleData>(length);
		int counter = 0;
		int maxValue = list.size() - 1;
		int currentIndex = 0;
		Random generator = new Random();
		ScrobbleData tmpItem;
		
		while(counter < length){ 
			currentIndex = generator.nextInt(maxValue);
			tmpItem = (ScrobbleData) list.get(currentIndex);
			finalList.add(tmpItem);
			
			counter += tmpItem.getPlayCount();
		}
		
		return finalList;
	}

}
