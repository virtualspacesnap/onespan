package com.oss.java.springdemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
public class OssService {
	private final String API_KEY = "vikramapi";
	private final String API_URL = "https://sandbox.esignlive.com/api";
	private final String filePath = "classpath:Test PDF.pdf";
	private final String signer1Email = "sendvjs@gmail.com";
	private final String signer2Email = "vsaha@oldmutual.com";

	public Map<String, String> createPackage() throws IOException {
		String requestURL = API_URL + "/packages";
		String charset = "UTF-8";
		File uploadFile1 = ResourceUtils.getFile(filePath);
		String boundary = "InfBound1x$y$tems";
		String CRLF = "\r\n"; // Line separator used in multipart/form-data.
		String jsonContent = "{\r\n" + 
				"   \"roles\":[\r\n" + 
				"      {\r\n" + 
				"         \"id\":\"Role1\",\r\n" + 
				"         \"signers\":[\r\n" + 
				"            {\r\n" + 
				"               \"email\":\""+signer1Email+"\",\r\n" + 
				"               \"firstName\":\"1.firstname\",\r\n" + 
				"               \"lastName\":\"1.lastname\",\r\n" + 
				"               \"company\":\"OneSpan Sign\"\r\n" + 
				"            }\r\n" + 
				"         ]\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"         \"id\":\"Role2\",\r\n" + 
				"         \"signers\":[\r\n" + 
				"            {\r\n" + 
				"               \"email\":\""+signer2Email+"\",\r\n" + 
				"               \"firstName\":\"2.firstname\",\r\n" + 
				"               \"lastName\":\"2.lastname\",\r\n" + 
				"               \"company\":\"OneSpan Sign\"\r\n" + 
				"            }\r\n" + 
				"         ]\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"documents\":[\r\n" + 
				"      {\r\n" + 
				"         \"approvals\":[\r\n" + 
				"            {\r\n" + 
				"               \"role\":\"Role1\",\r\n" + 
				"               \"fields\":[\r\n" + 
				"                  {\r\n" + 
				"                     \"page\":0,\r\n" + 
				"                     \"top\":100,\r\n" + 
				"                     \"subtype\":\"FULLNAME\",\r\n" + 
				"                     \"height\":50,\r\n" + 
				"                     \"left\":100,\r\n" + 
				"                     \"width\":200,\r\n" + 
				"                     \"type\":\"SIGNATURE\"\r\n" + 
				"                  }\r\n" + 
				"               ]\r\n" + 
				"            },\r\n" + 
				"            {\r\n" + 
				"               \"role\":\"Role2\",\r\n" + 
				"               \"fields\":[\r\n" + 
				"                  {\r\n" + 
				"                     \"page\":0,\r\n" + 
				"                     \"top\":300,\r\n" + 
				"                     \"subtype\":\"FULLNAME\",\r\n" + 
				"                     \"height\":50,\r\n" + 
				"                     \"left\":100,\r\n" + 
				"                     \"width\":200,\r\n" + 
				"                     \"type\":\"SIGNATURE\"\r\n" + 
				"                  }\r\n" + 
				"               ]\r\n" + 
				"            }\r\n" + 
				"         ],\r\n" + 
				"         \"name\":\"Test Document\"\r\n" + 
				"      }\r\n" + 
				"   ],\r\n" + 
				"   \"name\":\"Example Package\",\r\n" + 
				"   \"type\":\"PACKAGE\",\r\n" + 
				"   \"language\":\"en\",\r\n" + 
				"   \"emailMessage\":\"\",\r\n" + 
				"   \"description\":\"New Package\",\r\n" + 
				"   \"autocomplete\":true,\r\n" + 
				"   \"status\":\"SENT\"\r\n" + 
				"}";

		HttpsURLConnection connection = null;
		URL url = new URL(requestURL);
		connection = (HttpsURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		connection.setRequestProperty("Authorization", "Basic " + API_KEY);
		connection.setRequestProperty("Accept", "application/json; esl-api-version=11.0");

		OutputStream output = connection.getOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(output), true);

		try {

			// Add pdf file.
			writer.append("--" + boundary).append(CRLF);
			writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"2" + uploadFile1.getName() + "\"")
					.append(CRLF);
			writer.append("Content-Type: application/pdf").append(CRLF);
			writer.append(CRLF).flush();
			Files.copy(uploadFile1.toPath(), output);
			output.flush();
			writer.append(CRLF).flush();

			// add json payload
			writer.append("--" + boundary).append(CRLF);
			writer.append("Content-Disposition: form-data; name=\"payload\"").append(CRLF);
			writer.append("Content-Type: application/json; charset=" + charset).append(CRLF);
			writer.append(CRLF).append(jsonContent).append(CRLF).flush();

			// End of multipart/form-data.
			writer.append("--" + boundary + "--").append(CRLF).flush();
		} catch (Exception ex) {
			System.err.println(ex);
		}

		// get and write out response code
		int responseCode = ((HttpURLConnection) connection).getResponseCode();
		System.out.println(responseCode);

		if (responseCode == 200) {

			// get and write out response
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());

			JSONObject json = new JSONObject(response.toString());
			String packageId = json.getString("id");
			return new HashMap<String, String>() {
				{
					put("packageId", packageId);
				}
			};

		} else {

			// get and write out response
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
			throw new RuntimeException(response.toString());

		}

	}

	private String createSession(String packageId, String signerEmail) throws IOException {
		String base_url = API_URL.substring(0, API_URL.lastIndexOf("/api"));
		String url = API_URL + "/authenticationTokens/signer/multiUse";
		URL sourceClient = new URL(url);
		HttpURLConnection sourceConn = (HttpURLConnection) sourceClient.openConnection();
		sourceConn.setRequestProperty("Authorization", "Basic " + API_KEY);
		sourceConn.setRequestProperty("Content-Type", "application/json; esl-api-version=11.21");
		sourceConn.setRequestProperty("Accept", "application/json; esl-api-version=11.21");
		sourceConn.setRequestMethod("POST");
		sourceConn.setDoOutput(true);
		sourceConn.setDoInput(true);

		String payloadJSON = "{\"packageId\": \"" + packageId + "\",\"signerId\": \""+ signerEmail + "\"}";
		OutputStream os = sourceConn.getOutputStream();
		os.write(payloadJSON.toString().getBytes());
		os.flush();
		os.close();

		int sourceResponseCode = ((HttpURLConnection) sourceConn).getResponseCode();
		System.out.println(payloadJSON);
		System.out.println(url + " : " + sourceResponseCode);

		Reader ir = sourceResponseCode == 200 ? new InputStreamReader(sourceConn.getInputStream())
				: new InputStreamReader(sourceConn.getErrorStream());
		BufferedReader in = new BufferedReader(ir);
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		in.close();
		sourceConn.disconnect();

		if (sourceResponseCode == 200) {
			try {
				JSONObject jsonObject = new JSONObject(response.toString());
				String sessionToken = jsonObject.getString("value");
				String signingUrl = base_url + "/access?sessionToken=" + sessionToken;
				return signingUrl;
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public Map<String, String> createSession1(String packageId) throws IOException {
		String signingUrl = createSession(packageId, signer1Email);

		return new HashMap<String, String>() {
			{
				put("signingUrl", signingUrl);
			}
		};
	}

	public Map<String, String> createSession2(String packageId) throws IOException {
		String signingUrl = createSession(packageId, signer2Email);

		return new HashMap<String, String>() {
			{
				put("signingUrl", signingUrl);
			}
		};
	}
}
