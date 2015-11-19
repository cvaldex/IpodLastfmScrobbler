package cl.cvaldex.scrobbler.main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import net.roarsoftware.lastfm.scrobble.ResponseStatus;
import net.roarsoftware.lastfm.scrobble.Scrobbler;

import org.lastpod.TrackItem;

public class BasicScrobbler {
	public static final String KEY = "159b8689e2323359d9db3bd544da7210"; //clave generada por last.fm
	public static final String SECRET = "0e94bbfb5b25497a2a4955248bb85fbe"; // valor secreto generado por last.fm
	public static final long TIME_DELAY = 117;

//	public static void main(String[] args) throws Exception{
	public static void scrobble(Collection<TrackItem> tracks , String userName , String userPassword , long delay , int scrobblingLogAmount , int voidScrobbles) throws IOException, InterruptedException{
		Scrobbler scrobbler = Scrobbler.newScrobbler("tst", "1.0", userName);
		ResponseStatus status = scrobbler.handshake(userPassword);
		
		if(status.ok()){
			System.out.println("Sesión establecida correctamente con usuario: " + userName);
		}
		else{
			System.out.println("Error al establecer la sesión.-");
			System.exit(1);
		}
		
		long playCount = 0;
		int totalScrobblings = 0;
		long startTime = 0;
		
		long initTime = System.currentTimeMillis();
		long averageTimeScrobbling = 0;

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		System.out.println("Inicio proceso: " + formatter.format(new Date()) + " - Delay envio: " + delay);
		
		//int floorCounter = 329;
		
		for (TrackItem data : tracks) {
			playCount = data.getPlaycount();
			
			//procesar cada track con la cantidad de veces que fue reproducido
			while(playCount > 0){
				//setear el startTime "fresco"
				startTime = (System.currentTimeMillis() / 1000) - TIME_DELAY;

				data.setStartTime(startTime);
				
				if(voidScrobbles < totalScrobblings || voidScrobbles == 0){
					status = scrobbler.submit(data);
				}
				
				if(!status.ok()){
					System.out.println("Error: " + status.getMessage() + " " + "\n" + data.toString());
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
