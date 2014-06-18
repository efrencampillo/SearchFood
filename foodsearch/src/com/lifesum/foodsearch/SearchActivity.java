package com.lifesum.foodsearch;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SearchView;
import android.widget.Toast;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lifesum.foodsearch.R;
import com.lifesum.model.DBHelper;
import com.lifesum.model.FoodAdapter;
import com.lifesum.model.FoodORM;
import com.lifesum.net.AsyncRequest;
import com.lifesum.net.FoodConstant;
import com.lifesum.net.AsyncRequest.RESTCallback;
import com.lifesum.net.RESTClient.RequestMethod;
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
	MenuSelect mMenu;
	boolean saveInfo=false;
	
	///ImageLoader configuration
	ImageLoader imageLoader = ImageLoader.getInstance(); 
	ImageLoaderConfiguration config;
	DisplayImageOptions options;
	
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	MenuSelect mainMenu;
	
	static FoodAdapter adapterblock;
	static FoodAdapter adapterrow;
	static MenuItem searchItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		database=DBHelper.getInstance(this);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		adapterrow=new FoodAdapter(this,1);
		adapterblock=new FoodAdapter(this,0);
		if (savedInstanceState == null) {
			mFragment=new PlaceholderFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.container, mFragment).commit();
			mMenu=new MenuSelect();
			getFragmentManager().beginTransaction()
			.add(R.id.menu, mMenu).commit();
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
		mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.drawable.menu_icon,  
                R.string.drawer_open, R.string.drawer_close ){
		            public void onDrawerClosed(View view) {
		                super.onDrawerClosed(view);
		            }
		            public void onDrawerOpened(View drawerView) {
		                super.onDrawerOpened(drawerView);
		            }
		        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        showToast(getString(R.string.welcome));
	}
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
	    searchItem=menu.findItem(R.id.action_search);
	    searchItem.expandActionView();
	    SearchView searchView = (SearchView)searchItem.getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(false);
	    searchView.requestFocus();
//	    //set the view to disable save by default
//	    MenuItem item=menu.findItem(R.id.save);
//	    item.setChecked(false);
//	    saveInfo=false;
	    return super.onCreateOptionsMenu(menu);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		if (mDrawerToggle.onOptionsItemSelected(item))return true;
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing an adapter and a listview
	 * to see the information saved or the downloaded.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		ListView listfood;
		Gallery gallery;
		GridView grid;
		ProgressBar loadingbar;
		public PlaceholderFragment() { }

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_search,
					container, false);
			listfood=(ListView)rootView.findViewById(R.id.list_food);
			gallery=(Gallery)rootView.findViewById(R.id.gallery_food);
			grid=(GridView)rootView.findViewById(R.id.grid_food);
			listfood.setAdapter(adapterrow);
			gallery.setAdapter(adapterrow);
			grid.setAdapter(adapterrow);
			loadingbar=(ProgressBar)rootView.findViewById(R.id.loading_bar);
			OnItemClickListener clicklistener=new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View view,
						int index, long id) {
					FoodORM food=(FoodORM)adapter.getAdapter().getItem(index);
					((SearchActivity)getActivity()).searchOnGoogle(food);
				}
			};
			listfood.setOnItemClickListener(clicklistener);
			gallery.setOnItemClickListener(clicklistener);
			grid.setOnItemClickListener(clicklistener);
			return rootView;
		}
		public void setDisplay(int checkedId){
			switch(checkedId){
			case R.id.display_row:
				listfood.setAdapter(adapterrow);
				gallery.setAdapter(adapterrow);
				grid.setAdapter(adapterrow);
				break;
			case R.id.display_block:
				listfood.setAdapter(adapterblock);
				gallery.setAdapter(adapterblock);
				grid.setAdapter(adapterblock);
				break;
			}
		}
		public void setView(int checkedId){
			Log.i("setedView","index:"+checkedId);
			listfood.setVisibility(View.GONE);
			gallery.setVisibility(View.GONE);
			grid.setVisibility(View.GONE);
			switch(checkedId){
			case R.id.listview:
				listfood.setVisibility(View.VISIBLE);
				break;
			case R.id.gallery:
				gallery.setVisibility(View.VISIBLE);
				break;
			case R.id.gridview:
				grid.setVisibility(View.VISIBLE);
				break;
			}
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
    				if(searchItem!=null)searchItem.collapseActionView();
    				adapterrow.list.clear();
    				adapterblock.list.clear();
    				mDrawerLayout.closeDrawers();
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
							adapterrow.addInfo(foodinfo,saveInfo);
							adapterblock.addInfo(foodinfo,false);//we only need to save once
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							mFragment.loadingbar.setVisibility(View.GONE);
							adapterrow.notifyDataSetChanged();
							adapterblock.notifyDataSetChanged();
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
			adapterblock.addInfo(food);
			adapterrow.addInfo(food);
		}
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				adapterrow.notifyDataSetChanged();
				adapterblock.notifyDataSetChanged();
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
	public static class MenuSelect extends Fragment{
		/* (non-Javadoc)
		 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.menu_fragment,
					container, false);
			RadioGroup display_selector=(RadioGroup)rootView.findViewById(R.id.choose_display);
			display_selector.setOnCheckedChangeListener(new OnCheckedChangeListener() {				
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					((SearchActivity)getActivity()).mFragment.setDisplay(checkedId);
					((SearchActivity)getActivity()).mDrawerLayout.closeDrawers();
				}
			});
			RadioGroup view_selector=(RadioGroup)rootView.findViewById(R.id.choose_view);
			view_selector.setOnCheckedChangeListener(new OnCheckedChangeListener() {				
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					((SearchActivity)getActivity()).mFragment.setView(checkedId);
					((SearchActivity)getActivity()).mDrawerLayout.closeDrawers();
				}
			});
			CheckBox save=(CheckBox)rootView.findViewById(R.id.save);
			save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean ischecked) {
					((SearchActivity)getActivity()).saveInfo=ischecked;					
				}
			});
			return rootView;
		}
	}
	/**
	 * this method start a browser to find more information about food selected
	 * */
	public void searchOnGoogle(FoodORM food){
		if(food==null||food.title==null)return;
		if(isInternetConected()){
			Uri uri=Uri.parse("http://www.google.com/#q="+food.title);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}else{
			showToast(getString(R.string.no_internet));
		}
	}
}
