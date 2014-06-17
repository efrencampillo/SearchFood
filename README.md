SearchFood
==========
this proyect is a test to consume the web service to find food.


	To set this proyect correctly in a IDE, you need to add the reference to the
Android support v7-appcompat library, this library is provided by google and can 
be download from the android SDK Manager.

	This repo contains 2 projects, the activity required, and a android test 
application to run the test of the App. the test only contains the methods created 
that are not part of android sdk.

	To compile Right the required project, you must used the API Level 19 and the 
test project must be in the same work space, as the support-v7-appcompat library.


	SearchFood project contains a custom ORM to parse the json information that can
create the object from a Cursor result of SQLite database query, contains too an 
image loader library to display images, but services seems that is not sending urls
from food's pictures. To call the web services contains a Custom RestClient class, 
is a class developed by me and include an AsynRequest class that is a AsyncTask 
extendend from android,this class make the work in background.

Important:
remember to change the reference of the appcompatv7 library in the 
\foodsearch\project.properties file. in eclipse IDE you can change it on project 
properties, android section-> libs.

about the extra points,
ui/ux design:
	I added the splash page with a custom transition between activities.
Android guidelines: 
	using the libraries that they provide and the elements that they share to 
	use in the apps.
caching:
	no implemented, only offline finder trough SQLite
multi-add items: 
	not implemented
different ways of listing data:
	the information is organized by BaseAdapter a class that is easy to change 
	to anothers views, as gallery or gridview.