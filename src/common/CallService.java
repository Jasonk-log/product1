package common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class CallService {

	public String getJSON(String address) {
		
		String result = null;
		
		try {

			//address = "http://result:8080/";

			URL url = new URL(address);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				sb.append(line);
				sb.append(System.getProperty("line.separator"));
			}
			rd.close();
			result = sb.toString();
		
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return result;
		
	}
		
}
