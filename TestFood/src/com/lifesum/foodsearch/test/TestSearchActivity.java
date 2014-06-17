package com.lifesum.foodsearch.test;

import android.app.SearchManager;
import android.content.Intent;

import com.lifesum.foodsearch.SearchActivity;


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
}
