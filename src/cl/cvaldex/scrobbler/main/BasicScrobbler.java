package cl.cvaldex.scrobbler.main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Session;
import de.umass.lastfm.Track;
import de.umass.lastfm.scrobble.ScrobbleData;
import de.umass.lastfm.scrobble.ScrobbleResult;

//import net.roarsoftware.lastfm.scrobble.ResponseStatus;
//import net.roarsoftware.lastfm.scrobble.Scrobbler;

//import org.lastpod.TrackItem;

public class BasicScrobbler {
	public static final String API_KEY = "159b8689e2323359d9db3bd544da7210"; //clave generada por last.fm
	public static final String SECRET = "0e94bbfb5b25497a2a4955248bb85fbe"; // valor secreto generado por last.fm

	public static void scrobble(Collection<ScrobbleData> tracks , String userName , String userPassword , long delay , int scrobblingLogAmount , int voidScrobbles) throws IOException, InterruptedException{
		Session session = Authenticator.getMobileSession(userName, userPassword, API_KEY, SECRET);

		long playCount = 1;
		int totalScrobblings = 0;
		long startTime = 0;

		long initTime = System.currentTimeMillis();
		long averageTimeScrobbling = 0;

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		System.out.println("Inicio proceso: " + formatter.format(new Date()) + " - Delay envio: " + delay);
		ScrobbleResult result = null;
		//int floorCounter = 329;
		int numberOfTracks = tracks.size();
		long timeStamp = 1609434000;//System.currentTimeMillis() / 1000;

		System.out.println("Timestamp: " + 1609434000);


		for (ScrobbleData data : tracks) {
			playCount = data.getPlayCount();
			//playCount = 1; /**TO-DO Corregir esta aberración **/
			//procesar cada track con la cantidad de veces que fue reproducido
			/**TO-DO corregir esto del playcount **/
			while(playCount > 0){
				if(voidScrobbles < totalScrobblings || voidScrobbles == 0){
					//startTime = timeStamp - (numberOfTracks * 10);
					numberOfTracks --;
					//data.setTimestamp((int)startTime);
					result = Track.scrobble(data , session);
				}

				if(!result.isSuccessful()){
					System.out.println("Error: " + result.getErrorMessage() + " " + "\n" + data.toString());
				}

				playCount--;
				totalScrobblings++;

				if((totalScrobblings % scrobblingLogAmount) == 0){
					System.out.println("Scrobbling enviados: " + totalScrobblings);
				}

				//System.out.println("Init: " + System.currentTimeMillis());
				Thread.sleep(delay); //esperar un tiempo antes de enviar el siguiente scrobbling
				//System.out.println("Out: " + System.currentTimeMillis());
			}
		}

		averageTimeScrobbling = (System.currentTimeMillis() - initTime) / totalScrobblings;
		System.out.println("Fin proceso: " + formatter.format(new Date()));
		System.out.println("Scrobblings totales = " + totalScrobblings + "\t Promedio por Scrobbling: " + averageTimeScrobbling);
	}
}
