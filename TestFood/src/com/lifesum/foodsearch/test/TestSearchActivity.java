package com.lifesum.foodsearch.test;

import org.json.JSONObject;

import android.app.SearchManager;
import android.content.Intent;
import android.view.Menu;

import com.lifesum.foodsearch.SearchActivity;
import com.lifesum.model.FoodORM;


/**
 * this is the test class
 *
 */
public class TestSearchActivity extends android.test.ActivityInstrumentationTestCase2<SearchActivity> {
	SearchActivity app;
	/**
	 * @param name
	 */
	public TestSearchActivity() {
		super(SearchActivity.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	protected static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	protected static void tearDownAfterClass() throws Exception {
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		app=getActivity();
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	/**
	 * Test method for handle intent
	 */
	public final void testHandleIntent() {
		Intent i=new Intent();
		app.handleIntent(i);
		i.setAction(Intent.ACTION_SEARCH);
		app.handleIntent(i);
		i.putExtra(SearchManager.QUERY, "cola");
		app.handleIntent(i);
		
	}
	public final void testParseResult(){
		app.parseResult(null);
		app.parseResult("");
		app.parseResult("{}");
		app.parseResult("{\"response\": {\"list\": [{\"categoryid\": 45, \"fiber\": 0.200000002980232, \"headimage\": \"\", \"pcsingram\": 66.0, \"brand\": \"\", \"unsaturatedfat\": 0.0, \"fat\": 1.79999995231628, \"servingcategory\": 0, \"typeofmeasurement\": 1, \"protein\": 0.200000002980232, \"defaultserving\": 0, \"mlingram\": 1.0, \"id\": 957831, \"saturatedfat\": 1.60000002384186,\"category\":\"Ice Cream\", \"verified\": false, \"title\": \"Cola jet\", \"pcstext\": \"Unidad\", \"sodium\": 9.99999974737875e-06, \"carbohydrates\": 25.3999996185303, \"showonlysametype\": 0, \"calories\": 119, \"serving_version\": 4, \"sugar\": 20.8999996185303, \"measurementid\": 3, \"cholesterol\": 0.0, \"gramsperserving\": 0.0, \"showmeasurement\": 2, \"potassium\": 0.0}]}}");
	}
	public final void testShowToast(){
		app.showToast(null);
		app.showToast("");
		app.showToast("valid message");
	}
	public final void testFindOffline(){
		app.findOffline(null);
		app.findOffline("");
		app.findOffline("cola");
	}
	public final void testIsInternetConected(){
		app.isInternetConected();
	}
	public final void testSearchOnGoogle(){
		FoodORM food=null;
		app.searchOnGoogle(food);
		food=new FoodORM(new JSONObject());
		app.searchOnGoogle(food);
		food.title="cola";
		app.searchOnGoogle(food);
	}
	
}
