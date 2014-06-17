package com.lifesum.foodsearch;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lifesum.foodsearch.AsyncRequest.RESTCallback;
import com.lifesum.foodsearch.R;
import com.lifesum.foodsearch.RESTClient.RequestMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

/**
 * this is the search activity required
 * */
public class SearchActivity extends ActionBarActivity {
	
	DBHelper database;
	PlaceholderFragment mFragment;
	boolean saveInfo=false;
	
	///ImageLoader configuration
	ImageLoader imageLoader = ImageLoader.getInstance(); 
	ImageLoaderConfiguration config;
	DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		database=DBHelper.getInstance(this);
		
		if (savedInstanceState == null) {
			mFragment=new PlaceholderFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.container, mFragment).commit();
		}
		handleIntent(getIntent());
		
		///thisn part initialize the imageloader
		Drawable draw=getResources().getDrawable(R.drawable.ic_launcher);
		options= new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.ic_launcher)
			.showImageOnLoading(draw)
		    .showImageForEmptyUri(draw)
		    .showImageOnFail(draw)
		    .cacheOnDisc(true)
		    .build();
		config = new ImageLoaderConfiguration.Builder(this)
		    .memoryCacheExtraOptions(320, 440) // default = device screen dimensions
		    .threadPoolSize(5) // 
		    .threadPriority(Thread.MAX_PRIORITY) // max vel to download
		    .memoryCacheSizePercentage(25) // default
		    .discCacheSize(20 * 1024 * 1024)
		    .discCacheFileCount(100)
		    .imageDownloader(new BaseImageDownloader(this)) // default
		    .defaultDisplayImageOptions(options) // default
		    .build();
		imageLoader.init(config);
	}
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
		super.onNewIntent(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    //set the view to disable save by default
	    MenuItem item=menu.findItem(R.id.save);
	    item.setChecked(false);
	    saveInfo=false;
	    return super.onCreateOptionsMenu(menu);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		///if save option is selected we need to change falgs to save info
		if (id == R.id.save) {
			if(item.isChecked()){
				item.setChecked(false);
				saveInfo=false;
			}else {
				item.setChecked(true);
				saveInfo=true;
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing an adapter and a listview
	 * to see the information saved or the downloaded.
	 */
	public static class PlaceholderFragment extends Fragment {
		FoodAdapter adapter;
		ListView listfood;
		ProgressBar loadingbar;
		public PlaceholderFragment() { }

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_search,
					container, false);
			adapter=new FoodAdapter(getActivity());
			listfood=(ListView)rootView.findViewById(R.id.list_food);
			listfood.setAdapter(adapter);
			loadingbar=(ProgressBar)rootView.findViewById(R.id.loading_bar);
			return rootView;
		}
		
	}
	/**
	 * handle intent
	 * this method receives an intent, and validate if contains the search
	 * flag, if this contains, will validate if there is any conection and then
	 * start a new request to get the info 
	 * */
	public void handleIntent(Intent intent) {
		//this part means that user wants to search
		
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        	//this part clear the list
        	runOnUiThread(new Runnable(){
    			@Override
    			public void run() {
    				mFragment.adapter.list.clear();
    			}
    		});
        	//
        	String query = intent.getStringExtra(SearchManager.QUERY);
        	if(query==null||query.length()<1)return;
        	if(isInternetConected()){
	            AsyncRequest req=new AsyncRequest(FoodConstant.ServerUrl+"search/query", new RESTCallback() {				
					@Override
					public void receiveResponse(String response, Exception error) {		
						if(error!=null){
							showToast(getString(R.string.conexion_error));
						}else{
							parseResult(response);
						}	
						mFragment.loadingbar.setVisibility(View.GONE);
					}			
					@Override
					public void initRequest() {
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								mFragment.loadingbar.setVisibility(View.VISIBLE);
							}});	
					}
				});
	            req.AddHeader("Authorization", FoodConstant.Auth_token);
	            req.AddParam("type", "food");
	           
	            if(query!=null&&query.length()>0){
	            	req.AddParam("search", "\""+query+"\"");
	            	req.execute(RequestMethod.GET);
	            }
        	}else{
        		showToast(getString(R.string.offline));
            	findOffline(query);
            }
        }
    }
	/**
	 * this method parse the result in a new thread to show 
	 * information and no lock interface
	 * */
	public void parseResult(final String response){
		if(response!=null&&response.length()>0){
			new Thread(new Runnable() {				
				@Override
				public void run() {
					try{
						JSONObject result=new JSONObject(response);
						JSONObject jsonresponse=result.getJSONObject("response");
						JSONArray list=jsonresponse.getJSONArray("list");
						int length=list.length();
						for(int i=0;i<length;i++){
							JSONObject foodinfo=list.getJSONObject(i);
							mFragment.adapter.addInfo(foodinfo,saveInfo);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							mFragment.adapter.notifyDataSetChanged();
						}});
				}
			}).start();
		}	
	}
	/**
	 * method to show a toast
	 * */
	public void showToast(String msj){
		if(msj==null)return;
		Toast.makeText(this, msj, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * this methos find on the local DB the food
	 * */
	public void findOffline(String txt){
		if(txt==null||txt.length()<1)return;
		DBHelper db=DBHelper.getInstance(this);
		ArrayList<FoodORM> list=db.findFood(txt);
		for(FoodORM food:list){
			mFragment.adapter.addInfo(food);
		}
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				mFragment.adapter.notifyDataSetChanged();
			}});
		
	}
	/**
	 * this method validates if is there a network conenction
	 * */
	public boolean isInternetConected(){
		ConnectivityManager conMgr = (ConnectivityManager) this
	            .getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (conMgr.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED
	    	||conMgr.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED
	        || conMgr.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING
	        || conMgr.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING) {
	        return true;
	    } 
	    return false;
	}
}
