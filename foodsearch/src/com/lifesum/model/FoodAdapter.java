package com.lifesum.model;

import java.util.ArrayList;

import org.json.JSONObject;

import com.lifesum.foodsearch.R;
import com.lifesum.foodsearch.R.layout;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * this class is a baseapadater that can be used in diferents
 * views of android, like Gallery, ListView, spinner, gridiew
 *
 */
public class FoodAdapter extends BaseAdapter{
	public ArrayList<FoodORM> list;
	Context context;
	int type=0;
	View display;
	
	/**
	 * contructor
	 * this is the builder of this classs
	 * @param ctx the context to create views
	 * @param tp 1: for row display, 0 for block display
	 * */
	public FoodAdapter(Context ctx,int tp) {
		if(ctx==null){
			Log.e("AdapterError", "this adapter must be instantiated with a context");
			return;
		}
		type=tp;
		context=ctx;
		list=new ArrayList<FoodORM>();
	}
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return list.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int index) {
		if(index<=list.size())return list.get(index);
		return null;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int index) {
		return 0;
		/*
		 * we dont need an id of the object for now
		 * */
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int index, View convertView, ViewGroup root) {
		if(convertView==null){
			LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView=inflater.inflate(type==1?R.layout.layout_food_info_row:R.layout.layout_food_info, null);
		}
		FoodORM food=list.get(index);
		food.setOnView(convertView);
		return convertView;
	}
	
	/**
	 * this method add information to the adapter 
	 * */
	public void addInfo(JSONObject obj, boolean save){
		FoodORM item=new FoodORM(obj);
		list.add(item);
		if(save){
			DBHelper database=DBHelper.getInstance(context);
			database.saveFood(item);
		}
	}
	/**
	 * this method add information to the adapter 
	 * */
	public void addInfo(FoodORM obj){
		list.add(obj);
	}
}
