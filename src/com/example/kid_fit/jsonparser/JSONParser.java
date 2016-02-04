package com.example.kid_fit.jsonparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

@SuppressWarnings("deprecation")
public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	static HttpResponse httpResponse = null;

	// constructor
	public JSONParser() {

	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, String method,
			List<? extends NameValuePair> params) {

		// Making HTTP request
		try {
			
			// check for request method
			if(method == "POST"){
				// request method is POST
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
				
			}else if(method == "GET"){
				// request method is GET
				try {
			        HttpParams params2 = new BasicHttpParams();
			        HttpConnectionParams.setConnectionTimeout(params2, 10000);
			        HttpConnectionParams.setSoTimeout(params2, 10000);
			        HttpProtocolParams.setVersion(params2, HttpVersion.HTTP_1_1);
			        HttpProtocolParams.setContentCharset(params2, HTTP.UTF_8);
			        HttpProtocolParams.setUseExpectContinue(params2, true);
			        // defaultHttpClient
			        DefaultHttpClient httpClient = new DefaultHttpClient(params2);
			        HttpGet httpPost = new HttpGet( url);
			        httpResponse = httpClient.execute( httpPost);
			        HttpEntity httpEntity = httpResponse.getEntity();
			        is = httpEntity.getContent();           
			    } catch (UnsupportedEncodingException ee) {
			        Log.i("UnsupportedEncodingException...", is.toString());
			    } catch (ClientProtocolException e) {
			        Log.i("ClientProtocolException...", is.toString());
			    } catch (IOException e) {
			        Log.i("IOException...", is.toString());
			    }
			}			
			

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8192);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}
}

