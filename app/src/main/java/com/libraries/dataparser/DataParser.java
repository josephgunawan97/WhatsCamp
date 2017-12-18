package com.libraries.dataparser;

import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraries.ssl.MGHTTPClient;
import com.models.DataResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DataParser  {
	
	public static InputStream retrieveStream(String url) {
        try {
            HttpClient httpClient = MGHTTPClient.getNewHttpClient();
            HttpPost httpPost = new HttpPost(url);
//            DefaultHttpClient  httpClient = new DefaultHttpClient();
//            HttpPost httpPost = MGHTTPClient.getNonHttpsPost(url);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            final int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("Status Code", "Error " + statusCode + " for URL " + url); 
                return null;
             }
            HttpEntity getResponseEntity = httpResponse.getEntity();
            InputStream stream = getResponseEntity.getContent();
           return stream;
        } 
        catch (IOException e) {
        	
           Log.w("DataParser Error", "Error for URL " + url, e);
        }
        return null;
     }
	
	public JsonNode getJsonRootNode(String url)	{
		InputStream source = retrieveStream(url);
		if(source == null)
			return null;
		
		JsonFactory f = new JsonFactory();
        f.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        
        ObjectMapper mapper = new ObjectMapper(f);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		JsonNode rootNode = null;
		try  {
			rootNode = mapper.readTree(source);
		} 
		catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rootNode;
	}

	public static DataResponse getJSONFromUrlWithPostRequest(String url, List<NameValuePair> params) {
		
		try {
			HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            if(params != null)
            	httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            
            HttpResponse httpResponse = httpClient.execute(httpPost);
            final int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("Status Code", "Error " + statusCode + " for URL " + url); 
                return null;
			}
            
            HttpEntity getResponseEntity = httpResponse.getEntity();
            InputStream source = getResponseEntity.getContent();
            JsonFactory f = new JsonFactory();
            f.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
            ObjectMapper mapper = new ObjectMapper(f);
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//            mapper.enableDefaultTyping(DefaultTyping.JAVA_LANG_OBJECT, As.WRAPPER_OBJECT);
//            mapper.configure(DeserializationFeature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//            mapper.registerSubtypes(User.class);
//            mapper.registerSubtypes(Status.class);
            
            DataResponse data = new DataResponse();
    		try  {
//    			JsonNode rootNode = mapper.readTree(source);
//    			JsonNode status = rootNode.get("status");
//    			JsonNode userInfo = rootNode.get("user_info");
//    			data.setStatus(createStatus(status));
//    			data.setUser_info(createUser(userInfo));
    			data = mapper.readValue(source, DataResponse.class);
    			return data;
    		} 
    		catch (JsonParseException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} 
    		catch (JsonMappingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} 
    		catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
		catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } 
		catch (ClientProtocolException e) {
            e.printStackTrace();
        } 
		catch (IOException e) {
            e.printStackTrace();
        }
		return null;
    }

	public static DataResponse uploadFileWithParams(String url, ArrayList<NameValuePair> params, File file) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			FileBody fileBody = null;
			if(file != null)
				fileBody = new FileBody(file);

			if(fileBody != null)
				reqEntity.addPart("uploaded_file", fileBody);

			if(params != null) {
				Charset charset = Charset.forName("UTF-8");
				for(NameValuePair pair : params) {
					StringBody stringB = new StringBody(pair.getValue(), charset);
					reqEntity.addPart(pair.getName(), stringB);
				}
			}

			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity getResponseEntity = response.getEntity();
			InputStream source = getResponseEntity.getContent();


			JsonFactory f = new JsonFactory();
			f.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);

			ObjectMapper mapper = new ObjectMapper(f);
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			DataResponse data = null;
			try  {
				data = mapper.readValue(source, DataResponse.class);
				return data;
			}
			catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
