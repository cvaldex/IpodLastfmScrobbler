package cl.cvaldex.scrobbler.dto;

import net.roarsoftware.lastfm.scrobble.Source;
import net.roarsoftware.lastfm.scrobble.SubmissionData;

public class CustomSubmissionData extends SubmissionData {
	private int playCount;
	
	public CustomSubmissionData(String artist, String track, String album,
			int length, int tracknumber, Source source, long startTime , int playCount) {
		
		super(artist, track, album, length, tracknumber, source, startTime);
		
		this.playCount = playCount;
	}

	public int getPlayCount() {
		return playCount;
	}
	
	

}
