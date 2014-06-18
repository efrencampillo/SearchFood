package com.lifesum.model;

import org.json.JSONObject;

import com.lifesum.foodsearch.R;
import com.lifesum.foodsearch.R.id;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * this is a custom object relational model
 * used to paser each object form the server response
 *
 *reference guideline about creating model:
 *http://developer.android.com/training/articles/perf-tips.html#GettersSetters
 */
public class FoodORM {
	public int categoryid=0;
	public double fiber=0;
	public String headimage="";
	public int pcsingram=0;
	public String brand="";
	public double unsaturatedfat=0;
	public double fat=0;
	public int servingcategory=0;
	public int typeofmeasurement= 1;
	public double protein= 0;
	public int defaultserving= 0;
	public int mlingram=0;
	public long id=0;
	public double saturatedfat=0;
	public String category= "";
	public boolean verified= false;
	public String title="";
	public String pcstext="";
	public double sodium=0;
	public double carbohydrates=0;
	public int showonlysametype= 0;
	public int calories=0;
	public int serving_version= 0;
	public double sugar=0;
	public int measurementid=0;
	public double cholesterol= 0;
	public int gramsperserving= 0;
	public int showmeasurement= 2;
	public int potassium= 0;
	
	/**
	 * this is a builder from DB to FoodORM
	 * */
	public FoodORM(Cursor c){
		try{
			id=c.getInt(0);
			title=c.getString(1);
			fat=c.getDouble(2);
			carbohydrates=c.getDouble(3);
			protein=c.getDouble(4);
			calories=c.getInt(5);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * this mehod creates the FoodORM from json
	 * */
	public FoodORM(JSONObject info){
		try{
			if(info.has("categoryid")){
				categoryid=info.getInt("categoryid");
			}
			if(info.has("fiber")){
				fiber=info.getDouble("fiber");
			}
			if(info.has("headimage")){
				headimage=info.getString("headimage");
			}
			if(info.has("pcsingram")){
				pcsingram=info.getInt("pcsingram");
			}
			if(info.has("brand")){
				brand=info.getString("brand");
			}
			if(info.has("unsaturatedfat")){
				unsaturatedfat=info.getInt("unsaturatedfat");
			}
			if(info.has("fat")){
				fat=info.getDouble("fat");
			}
			if(info.has("servingcategory")){
				servingcategory=info.getInt("servingcategory");
			}
			if(info.has("typeofmeasurement")){
				typeofmeasurement=info.getInt("typeofmeasurement");
			}
			if(info.has("protein")){
				protein=info.getDouble("protein");
			}
			if(info.has("defaultserving")){
				defaultserving=info.getInt("defaultserving");
			}
			if(info.has("mlingram")){
				mlingram=info.getInt("mlingram");
			}
			if(info.has("id")){
				id=info.getInt("id");
			}
			if(info.has("saturatedfat")){
				saturatedfat=info.getDouble("saturatedfat");
			}
			if(info.has("category")){
				category=info.getString("category");
			}
			if(info.has("verified")){
				verified=info.getBoolean("verified");
			}
			if(info.has("title")){
				title=info.getString("title");
			}
			if(info.has("pcstext")){
				pcstext=info.getString("pcstext");
			}
			if(info.has("sodium")){
				sodium=info.getDouble("sodium");
			}
			if(info.has("carbohydrates")){
				carbohydrates=info.getDouble("carbohydrates");
			}
			if(info.has("showonlysametype")){
				showonlysametype=info.getInt("showonlysametype");
			}
			if(info.has("calories")){
				calories=info.getInt("calories");
			}
			if(info.has("serving_version")){
				serving_version=info.getInt("serving_version");
			}
			if(info.has("sugar")){
				sugar=info.getDouble("sugar");
			}
			if(info.has("measurementid")){
				measurementid=info.getInt("measurementid");
			}
			if(info.has("cholesterol")){
				cholesterol=info.getDouble("cholesterol");
			}
			if(info.has("gramsperserving")){
				gramsperserving=info.getInt("gramsperserving");
			}
			if(info.has("showmeasurement")){
				showmeasurement=info.getInt("showmeasurement");
			}
			if(info.has("potassium")){
				potassium=info.getInt("potassium");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * this method set the infor about this object in the view given
	 * */
	public void setOnView(View view){
		TextView txt_title=(TextView)view.findViewById(R.id.txt_title);
		if(txt_title!=null){
			txt_title.setText(""+title);
		}
		ImageView img=(ImageView)view.findViewById(R.id.img_food);
		if(img!=null&&headimage!=null&&headimage.length()>0){
			ImageLoader il=ImageLoader.getInstance();
			il.displayImage(headimage,img);
			///NOTE:it seems like all information returned not contains images
		}
		TextView txt_fat=(TextView)view.findViewById(R.id.value_fat);
		if(txt_fat!=null){
			txt_fat.setText(""+fat);
		}
		TextView txt_carb=(TextView)view.findViewById(R.id.value_carb);
		if(txt_carb!=null){
			txt_carb.setText(""+carbohydrates);
		}
		TextView txt_protein=(TextView)view.findViewById(R.id.value_protein);
		if(txt_protein!=null){
			txt_protein.setText(""+protein);
		}
		TextView txt_cal=(TextView)view.findViewById(R.id.value_calories);
		if(txt_cal!=null){
			txt_cal.setText(""+calories);
		}
		
	}
}
