package cl.cvaldex.scrobbler.parser;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import de.umass.lastfm.scrobble.ScrobbleData;

import com.google.api.services.sheets.v4.Sheets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GoogleSheetsParser{
    /** Application name. */
    private static final String APPLICATION_NAME = "GSheets Scrobbler";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/sheets.googleapis.scrobbler");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/sheets.googleapis.com-java-quickstart
     */
    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);
    
    private static String secretsFilePath = null;
    private String spreadsheetId = null;
    private String range = null;
    public static final int DEFAULT_DURATION = 4000;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
    	File secretsFile = new File(secretsFilePath);
        InputStream in = new FileInputStream(secretsFile);
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Collection<ScrobbleData> parse() throws Exception {
    	if(secretsFilePath == null || secretsFilePath.trim().length() == 0){
    		throw new Exception("secretsFilePath cannot be null or empty");
    	}
    	
    	if(spreadsheetId == null || spreadsheetId.trim().length() == 0){
    		throw new Exception("spreadsheetId cannot be null or empty");
    	}
    	
    	if(range == null || range.trim().length() == 0){
    		throw new Exception("Cell range cannot be null or empty");
    	}
    	
    	Collection<ScrobbleData> trackList = new ArrayList<ScrobbleData>();
        // Build a new authorized API client service.
        Sheets service = getSheetsService();

        // Prints the names and majors of students in a sample spreadsheet:
        //String spreadsheetId = "1OmKzJm5HCxdsEXXCrJCeTmOIFWIUst0f9jw54lLulUM";
        ValueRange response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.size() == 0) {
            System.out.println("No data found.");
        } else {
        	ScrobbleData scrobble = null;
        	for (List row : values) {
        		if(! row.get(0).toString().startsWith("#")){
	        		scrobble = new ScrobbleData();
	        		scrobble.setArtist(row.get(1).toString());
	        		scrobble.setAlbum(row.get(2).toString());
	        		scrobble.setTrack(row.get(3).toString());
	        		scrobble.setPlayCount(Integer.parseInt(row.get(4).toString()));
	        		scrobble.setDuration(DEFAULT_DURATION);
	        		
	        		trackList.add(scrobble);
        		}
        	}
        }
        
        return trackList;
    }

	public String getSecretsFilePath() {
		return secretsFilePath;
	}

	public void setSecretsFilePath(String secretsFilePath) {
		this.secretsFilePath = secretsFilePath;
	}

	public String getSheetID() {
		return spreadsheetId;
	}

	public void setSheetID(String sheetID) {
		this.spreadsheetId = sheetID;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}
}
