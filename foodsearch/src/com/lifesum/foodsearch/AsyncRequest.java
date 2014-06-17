package com.lifesum.foodsearch;



import com.lifesum.foodsearch.RESTClient.RequestMethod;

import android.os.AsyncTask;

/**
 * this class is a AsyncTask from android to execute Restclient in background
 *
 */
public class AsyncRequest extends AsyncTask<RequestMethod ,String,RESTClient>{
	
	String url;
	RESTCallback callback;
	Exception error=null;
	RESTClient request;
	/**
	 * this is the builder of the class
	 * @param url the endpoint to request
	 * @param restCallback the function to receive the respone 
	 */
	public AsyncRequest(String url, RESTCallback restCallback) {
		request=new RESTClient(url);
		this.callback=restCallback;
	}
	/**
	 * set a param in a restclient
	 * */
	public void AddParam(String name, String value) {
		request.AddParam(name,value);
	}
	/**
	 * set a header in restclient
	 * */
	public void AddHeader(String name, String value) {
		request.AddHeader(name, value);
	}
	/**
	 * set content-type in restclient
	 * */
	public void setContentType(String content){
		request.setContentType(content);
	}
	/**
	 * set the time outof the restclient
	 * */
	public void setTimeOut(int time){
		request.setRequestTimeOut(time);
	}
	public void setPost(String post){
		request.post=post;
	}
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected RESTClient doInBackground(RequestMethod... method) {
		if(callback!=null)callback.initRequest();
		try{
			request.Execute(method[0]);	
		}catch(Exception e){
			error=e;
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(RESTClient req) {
		if(callback!=null){
			if(request!=null)callback.receiveResponse(request.getResponse(),error);
			else callback.receiveResponse(null,error);
		}
	}
	public interface RESTCallback{
		public void receiveResponse(String response,Exception e);
		public void initRequest();
	}
}
