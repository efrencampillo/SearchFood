
package com.lifesum.net;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.util.ArrayList;

import org.apache.http.conn.ssl.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.*;
import org.apache.http.protocol.HTTP;


import android.os.Build;
import android.util.Log;
import android.webkit.MimeTypeMap;

/**
 * this class is a restclient made for android of my own library
 * */
public class RESTClient {
	
	public static final String JSON = "application/json";
	public static final String URLENCODED = "application/x-www-form-urlencoded";
	
	private int RequestTimeOut=30000;
	
	public static  enum RequestMethod {
		GET,POST,PUT,DELETE
	}

	private ArrayList<NameValuePair> params;
	private ArrayList<NameValuePair> headers;
	public String post;
	private String url;

	private int responseCode;
	private String message;
	private String response;
	private String contentType;

/**
 * default methods to manage the restclient
 * */
	public String getResponse() {
		return response;
	}
	public String getErrorMessage() {
		return message;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public RESTClient(String url) {
		this.url = url;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
		contentType= JSON;
	}
	
	public void AddParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}
	
	public void AddHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}
	
	public RESTClient setContentType(String content){
		contentType=content;
		return this;
	}
	
	public String getUrl() {
		return url;
	}
	public RESTClient setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public RESTClient setPost (String pPost){
		this.post = pPost;
		return this;
	}
	
	/**
	 * this set the miliseccond time out default
	 * */
	public void setRequestTimeOut(int milliseconds){
		if(milliseconds<1000){
			Log.d("requestTimeOut", "value very low for internet request it keeps in 30 seconds");
		}else{
			RequestTimeOut=milliseconds;
		}
	}
	
	
	
	 String lineEnd = "\r\n";
     String twoHyphens = "--";
     String boundary =  "*****";
     /**
 	 * upload multipart
 	 * this method receive the file to be uploaded
 	 * */
	public String uploadMultiPart(File file, String NameParamImage)throws Exception{
		disableSSLCertificateChecking();
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		HttpsURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;      
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 5*1024*1024;
        try{
            FileInputStream fileInputStream = new FileInputStream(file);
            URL endpoint = new URL(url);
            conn = (HttpsURLConnection) endpoint.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            /*
              //this is a exapmle how to put basic authentication in http
              	String auth=UserProfile.getEmail()+":"+UserProfile.getPassword();
				byte[] data = auth.getBytes("UTF-8");
				auth=Base64.encodeToString(data, Base64.DEFAULT);
				conn.setRequestProperty("Authorization", "basic " +auth);
			 */
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            conn.setRequestProperty("User-Agent", "Android "+Build.MODEL+"/" + Build.VERSION.RELEASE);
            dos = new DataOutputStream( conn.getOutputStream() );
            String post="";
            for (NameValuePair p : params)post+=writeMultipartParam(p);
            post+=twoHyphens + boundary + lineEnd;
            post+="Content-Disposition: form-data; name=\""+NameParamImage+"\"; filename=\""+file.getName()+"\"" + lineEnd;
            String mimetype=getMimeType(file.getName());
            if(mimetype==null)mimetype="image/jpeg";
            post+="Content-Type: "+mimetype+lineEnd;
            post+="Content-Transfer-Encoding: binary"+lineEnd+lineEnd;
           
            dos.writeBytes(post);
           
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0){
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            post+=lineEnd;
            post+=twoHyphens + boundary + twoHyphens;
            Log.i("posting",""+post);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens);
            fileInputStream.close();
            dos.flush();
            dos.close();
            conn.connect();
        }
        catch (MalformedURLException ex){
            Log.e("Debug", "error: " + ex.getMessage(), ex);
        }
        catch (IOException ioe){
            Log.e("Debug", "error: " + ioe.getMessage(), ioe);
        }
        responseCode=conn.getResponseCode();
        if(responseCode!=201){
        	return null;        	
        }
        try {
        	String response_data="";
            inStream = new DataInputStream (conn.getInputStream());
            String str;
            while (( str = inStream.readLine()) != null){
                response_data+=str;
            }
            inStream.close();
            return response_data;
        }
        catch (IOException ioex){
            Log.e("Debug", "error: " + ioex.getMessage(), ioex);
        }
       return null;
	}
	private String writeMultipartParam(NameValuePair nameValue){
		String result="";
		result+=twoHyphens + boundary + lineEnd;
		result+="Content-Disposition: form-data; name=\""+nameValue.getName()+"\"" +lineEnd+lineEnd;
		result+=nameValue.getValue()+lineEnd;
		return result;
	}
	public static String getMimeType(String url)
	{
	    String type = null;
	    String extension = MimeTypeMap.getFileExtensionFromUrl(url);
	    if (extension != null) {
	        MimeTypeMap mime = MimeTypeMap.getSingleton();
	        type = mime.getMimeTypeFromExtension(extension);
	    }
	    return type;
	}
	/**
	 * this method utoacepts all certificates in httpsurlconections
	 * */
	 private static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() { return null;}
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException { }
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException { }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
/**
 * exceute the post defined to the url defined
 * 
 * */
	public void Execute(RequestMethod method) throws Exception {
		//Debug.log("Exceute","request:"+method.toString()+" in "+url);
		switch (method) {
		case GET: {
			// add parameters
			String combinedParams = "";
			if (!params.isEmpty()) {
				combinedParams += "?";
				for (NameValuePair p : params) {
					String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
					if (combinedParams.length() > 1) {
						combinedParams += "&" + paramString;
					} else {
						combinedParams += paramString;
					}
				}
			}
			HttpGet request = new HttpGet(url + combinedParams);
			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}
			executeRequest(request, url);
			break;
		}
		case PUT:
			HttpPut requestput = new HttpPut(url);
			executeRequest(requestput, url);
			break;
		case POST: {			
			HttpPost request = new HttpPost(url);
			request.setHeader("Content-type",contentType);
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
				Log.d("header", ""+h);
			}		
			if (!params.isEmpty()||post!=null) {
				if(contentType.equals(JSON)){
					ByteArrayEntity databody=new ByteArrayEntity(post.getBytes("UTF8"));
					Log.d("jsonData", ""+databody);
					request.setEntity(databody);
				}else {
					Log.d("urlEncode", "params:"+params);
					request.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
				}
				
			}		
			executeRequest(request, url);
			break;
		}
		case DELETE:
			HttpDelete request = new HttpDelete(url);	
			executeRequest(request, url);
			break;
		}
	}
/**
 * executing request from post in execute restclient
 * 
 * */
	private void executeRequest(HttpUriRequest request, String url) throws Exception{
		HttpClient client = getNewHttpClient();//; new DefaultHttpClient(httpParams);
		HttpResponse httpResponse;
		try {
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				response = convertStreamToString(instream);
				instream.close();
			}
		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			Log.d("RestClientExecption","protocol error at:"+this.url);
			e.printStackTrace();
			throw new Exception();		
		} catch (IOException e) {
			Log.d("RestClientExecption","I/O error at:"+this.url);
			client.getConnectionManager().shutdown();
			e.printStackTrace();
			throw new Exception();	
		}
	}
/**
 * 
 * convert the Stream from the request in a String readeable to the rest client
 * 
 * */
	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	/**
	 * 
	 * this private method obtains the httpclient that support https
	 * and set the timeout in 30 secconds
	 * 
	 * 
	 * */
	public HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);
	        MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); 
	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	        HttpConnectionParams.setConnectionTimeout(params, RequestTimeOut);
	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));
	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	private class MySSLSocketFactory extends SSLSocketFactory {
	    SSLContext sslContext = SSLContext.getInstance("TLS");
	    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	        super(truststore);
	        TrustManager tm = new X509TrustManager() {
	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException { }
	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException { }
	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };
	        sslContext.init(null, new TrustManager[] { tm }, null);
	    }
	    @Override
	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
	        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	    }
	    @Override
	    public Socket createSocket() throws IOException {
	        return sslContext.getSocketFactory().createSocket();
	    }
	}
	
}
