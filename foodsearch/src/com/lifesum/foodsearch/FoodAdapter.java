package com.lifesum.foodsearch;

import java.util.ArrayList;

import org.json.JSONObject;

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
	ArrayList<FoodORM> list;
	Context context;
	public FoodAdapter(Context ctx) {
		if(ctx==null){
			Log.e("AdapterError", "this adapter must be instantiated with a context");
			return;
		}
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
			convertView=inflater.inflate(R.layout.layout_food_info, null);
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
