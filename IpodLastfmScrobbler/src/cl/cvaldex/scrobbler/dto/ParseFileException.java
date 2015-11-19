package cl.cvaldex.scrobbler.dto;

import java.io.IOException;

public class ParseFileException extends Exception {

	public ParseFileException(String message, IOException ioe) {
		super(message , ioe);
	}
	
}
