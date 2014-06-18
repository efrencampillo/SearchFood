package com.lifesum.model;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * this class helps to save information
 * 
 */
public class DBHelper  extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "food";
	private static final int DATABASE_VERSION = 1;
	private static DBHelper mthis;
	
	private static final String CREATE_TABLE_FOOD ="create table "
			+ "food_table(id integer primary key, title text, " 
			+ "fat REAL, carb REAL, protein REAL, calories integer);";
	
	public DBHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mthis=this;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_FOOD);	
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
		db.execSQL("DROP TABLE IF EXISTS food_table");
		onCreate(db);
	}
	public static DBHelper getInstance(Context context){
		if(mthis==null)new DBHelper(context);
		return mthis;
	}
	/**
	 * this method returns a list of food finding in title the text send
	 * */
	public ArrayList<FoodORM> findFood(String txt){
		ArrayList<FoodORM> list=new ArrayList<FoodORM>();
		if(txt==null||txt.length()<1)return list;
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor c=db.rawQuery("select * from food_table where title like '%"+txt+"%'",null);
		if(c.moveToFirst()){
			do{
				FoodORM  fd=new FoodORM(c);
				list.add(fd);
			}while(c.moveToNext());
		}
		return list;
	}
	
	/**
	 * this method save a single food in the database
	 * */
	public void saveFood(FoodORM food){
		if(food==null)return;
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues values = new ContentValues();	
		try{
			values.put("id",food.id);
			values.put("title",food.title);
			values.put("fat",food.fat);
			values.put("carb",food.carbohydrates);
			values.put("protein",food.protein);
			values.put("calories",food.calories);
			db.insert("food_table", null, values);
		}catch(Exception e){
		  e.printStackTrace();			  
		}
	}
	
}
