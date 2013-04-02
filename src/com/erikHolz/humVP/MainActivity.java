package com.erikHolz.humVP;

	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	/*	Author:				Erik Holzwirth										  */
	/*	Classname:			MainActivity										  */
	/*	Last Time Edited:	30.03.2013 16:30									  */
	/*	Methods:			void 	getInput									  */
	/*						void 	saveSettings								  */
	/*						void	saveLog										  */
	/*						class	FetchData									  */
	/*						void	buildLayout									  */
	/*	Description:		MainActivity which the user sees, starts all other	  */
	/*						functions and stuff									  */
	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */	

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity {

	/* 
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * create some global needed fields
	 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	         
	ProgressDialog dialog;
	
    String	SETT_fieldToBeSearched 	= "Klasse";
    String 	valueSet 				= "0";
    
	String[] argumentsToday			= new String[4];
	String[] argumentsTomorrow		= new String[4];
	String[] argumentsMenu			= new String[4];
    String[] lastUpdate;
	
    Tab tabMenu;
	Tab tabToday;
	Tab tabTomorrow;
	
    TextView headerUpdate;
	
    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * initialise global fields
     * start refreshing of data if needed
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	   	
        ActionBar bar = getSupportActionBar();
        
        // hide app icon
        bar.setLogo(new ColorDrawable(Color.TRANSPARENT));        
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
    	setContentView(R.layout.activity_main);
    	
    	// (dummy) arguments for fragments
    	argumentsToday[0] 		= "<--Refresh-->";
    	argumentsToday[1] 		= "2";
    	argumentsToday[2] 		= "<--Refresh-->";
    	argumentsToday[3] 		= "<--Refresh-->";
    	
    	argumentsTomorrow[0] 	= "<--Refresh-->";
    	argumentsTomorrow[1] 	= "2";
    	argumentsTomorrow[2] 	= "<--Refresh-->";
    	argumentsTomorrow[3] 	= "<--Refresh-->";
    	
    	argumentsMenu[0] 		= "<--Refresh-->";
    	argumentsMenu[1] 		= "2";
    	argumentsMenu[2] 		= "<--Refresh-->";
    	argumentsMenu[3] 		= "<--Refresh-->";
    	          
    	// setup tabs
        tabToday 		= bar.newTab().setText("Heute");
        tabTomorrow 	= bar.newTab().setText("Morgen");
        tabMenu			= bar.newTab().setText("Essen");
  
        tabToday.setTabListener(new TabFragmentListener<TabFragmentToday>(this, "today", TabFragmentToday.class));
        tabTomorrow.setTabListener(new TabFragmentListener<TabFragmentTomorrow>(this, "tomorrow", TabFragmentTomorrow.class));
        tabMenu.setTabListener(new TabFragmentListener<TabFragmentMenu>(this, "menu", TabFragmentMenu.class));

        bar.addTab(tabToday);
        bar.addTab(tabTomorrow);
        bar.addTab(tabMenu);
                            
	    // initialise dialog for download	       
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Suche aktuelle Daten...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setProgress(0);
        dialog.setMax(100);
        
        // initialise header for showing recent update
        headerUpdate = (TextView) findViewById(R.id.headerUpdate);
        
		// check if user has already declared his class
        startupRefresh();
	
	
    }
         
    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * handles menu creation
     * gives menu items their actions
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
		return true;
    }
        
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {    	
        switch (item.getItemId()) {
        	case R.id.menu_refresh:
        		FetchData refresh = new FetchData();
        		refresh.execute();        		
                return true;
	
        	case R.id.menu_submenu_category_1:
        		if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
        		SETT_fieldToBeSearched = "Klasse";
            	return true;
            	
        	case R.id.menu_submenu_category_2:
        		if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
        		SETT_fieldToBeSearched = "Vertretung";
            	return true;
            	
        	case R.id.menu_submenu_category_3:
        		if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
        		SETT_fieldToBeSearched = "Ausfall";
            	return true;
                
            case R.id.menu_settings:
            	getInput();
            	return true;
            	
            case R.id.menu_exit:
            	// go to homescreen
            	Intent intent = new Intent(Intent.ACTION_MAIN);
            	intent.addCategory(Intent.CATEGORY_HOME);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	startActivity(intent);
            	return true;
        }
		return false;
    }
    
    /* 
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * ask the user to give his data which will be searched in the database
     * save the input the User gives
     * add the date when refresh was executed last time
     * log some data in background
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */
       
    public void getInput() {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Klasse bzw. Lehrer?");
    	alert.setMessage("Bitte geben Sie ihre Klasse bzw den Namen des gewünschten Lehrers an!" + "\n" + "(z.B. '7/1' oder 'Herr Köthe')");

    	// used as field for user input
    	final EditText input = new EditText(this);
    	alert.setView(input);

    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	
    		public void onClick(DialogInterface dialog, int whichButton) {
			
	    		// catch empty inputs
	    		if(input.getText().toString().equals("")) 
					getInput();
	    		
	    		// save user input if it is not empty
				else {
					valueSet = input.getText().toString();
			
					try {
						saveUserValue(input.getText().toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					if (checkForUpdate()) { 
						FetchData refresh = new FetchData();
						refresh.execute();
					} 			
					else {
						headerUpdate.setText("Zuletzt aktualisiert: " + lastUpdate[0] + ":" + lastUpdate[1]);
						refreshFragments();
					}

				}
    		}
    		
    	});

    	alert.show();
    }

    public void saveUserValue(String text) throws IOException {
		BufferedWriter output = new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory().getPath() + "/humVP_SettingsFile.txt"))); 
		
		output.write(text);
		output.close();
    }
    
    public void saveUpdateState(String time) throws IOException {
		BufferedWriter output = new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory().getPath() + "/humVP_SettingsFile.txt")));
		
		output.write(valueSet);		
		output.newLine();
		output.write(time);
		output.close();
		
		lastUpdate = time.split("_");
    }

    public void saveLog() throws IOException {
    	Date logDate = new Date();
    	BufferedWriter 	output = new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory().getPath() + "/humVP_LogFile.txt"), true)); 
		
    	output.write(logDate + "_" + valueSet + "\n");
		output.close();
    }
    
    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * check when the user updated his data last time
     * returns true if it was on the day before, else false
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */ 
    
    public boolean checkForUpdate() {
    	GregorianCalendar date = new GregorianCalendar();    	
    	Calendar.getInstance();
    	date.setTime(new Date());
    			
		String filename 		= "";
		filename	    		+= date.get(Calendar.YEAR);
		
   		if (date.get(Calendar.MONTH) + 1 < 10) 	filename	+= " 0" + (date.get(Calendar.MONTH) + 1);
		else									filename	+= " " 	+ (date.get(Calendar.MONTH) + 1);
			
		if (date.get(Calendar.DATE) < 10)     	filename 	+= " 0" + date.get(Calendar.DATE);
		else									filename	+= " "  + date.get(Calendar.DATE);
		
		if(!(filename.equals(lastUpdate[3]))) 
			return true;
		
		else 
			return false;
		
    }
    
    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * checks if app was already started sometime and if settings were created
     * if not it will force the user to give his data
     * if last update was the day before it refreshs the data
     * else it loads last data
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */
    
    public void startupRefresh() {
    	
    	BufferedReader input;
		int i 					= 0;
    	String[] loadedData 	= new String[2];

		try {
			input = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getPath() + "/humVP_SettingsFile.txt"));
			String line = null;
			
			while ((line = input.readLine()) != null) {
				loadedData[i] = line;			
				i++;
			}
			
			valueSet 	= loadedData[0];
			lastUpdate 	= loadedData[1].split("_");
				
			if (valueSet == null) 
				getInput();
			
			else {
				if (checkForUpdate()) { 
					FetchData refresh = new FetchData();
					refresh.execute();
				}
				else {
					headerUpdate.setText("Zuletzt aktualisiert: " + lastUpdate[0] + ":" + lastUpdate[1]);
					refreshFragments();
				}
			}
			
		    input.close();

			} catch (FileNotFoundException e) {
				getInput();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
    }
    
    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * function can be used by the fragments to get the arguments for displaying 
     * data
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */
   
    public String[] getFragmentArguments(int fragmentId) {
    	String[] arguments = new String[4];
    	
    	switch (fragmentId) {
    	case 0:
    		for (int intI = 0; intI < 4; intI++)
    			arguments[intI] = argumentsToday[intI];
    		break;
    	case 1:
    		for (int intI = 0; intI < 4; intI++)
    			arguments[intI] = argumentsTomorrow[intI];
    		break;
    	case 2:
    		for (int intI = 0; intI < 4; intI++)
    			arguments[intI] = argumentsMenu[intI];
    		break;
    	}
		
    	return arguments;
    }
    
    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * programmatically change the current tab to force fragments to refresh their
     * views
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */
    
    public void refreshFragments() {
    	ActionBar bar = getSupportActionBar();
    	
    	bar.selectTab(tabTomorrow);
    	bar.selectTab(tabMenu);
    	bar.selectTab(tabToday);
    }
    
    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * gets linkname
     * downloads file(s)
     * starts converter
     * controlls progress dialog
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */
    
    public class FetchData extends AsyncTask<Void, Integer, String> {	    
            	
        boolean boolConnection 			= false;
        boolean boolFileExistsMenu		= false;
        boolean boolFileExistsToday 	= false;
        boolean boolFileExistsTomorrow	= false;
    	boolean boolIsN 				= false;
       
		int fileAmount = 0;
        int intLoopCount;
        int	intTime;
	    
        String[] fileDestinations		= new String[4];
		String[] possibleLinkname 		= new String[17];
        String[] validLinknames  		= new String[4];

        protected void onPreExecute() {
            dialog.show();
        }
    	
    	@Override
	    protected String doInBackground(Void... params) { 
    		
    		// ---------------------------------------------------------------------
    		// get possible linknames
    		// ---------------------------------------------------------------------
    		
    		// get dates for today and if needed next 16 days (in case of holidays)
    		Calendar[] date  	= new GregorianCalendar[17];
    		for (intLoopCount = 0; intLoopCount < 17; intLoopCount++) {
    			date[intLoopCount] = new GregorianCalendar();
    			Calendar.getInstance();
   				date[intLoopCount].setTime(new Date());
    		}
    				
    		for (intLoopCount = 0; intLoopCount < 17; intLoopCount++) 
    			date[intLoopCount].add (Calendar.DAY_OF_YEAR, intLoopCount);	
    		
    			
    		// get (string) linknames according to dates
    		for (intLoopCount = 0; intLoopCount < 17; intLoopCount++) {	
    			possibleLinkname[intLoopCount] 	= "";
    			possibleLinkname[intLoopCount] 	+= date[intLoopCount].get(Calendar.YEAR);
    			
    			// January = 0 --> +1
    			if (date[intLoopCount].get(Calendar.MONTH) + 1 < 10) 
    				possibleLinkname[intLoopCount] 	+= "%200" 	+ (date[intLoopCount].get(Calendar.MONTH) + 1);
    			else											 	
    				possibleLinkname[intLoopCount] 	+= "%20"  	+ (date[intLoopCount].get(Calendar.MONTH) + 1);
    			
    			if (date[intLoopCount].get(Calendar.DATE) < 10)      
    				possibleLinkname[intLoopCount] 	+= "%200" 	+ date[intLoopCount].get(Calendar.DATE);
    			else									     		 
    				possibleLinkname[intLoopCount]		+= "%20"  	+ date[intLoopCount].get(Calendar.DATE);
    		}
    		
            publishProgress((int) 3);
 		    		
    		// ----------------------------------------------------------------------------
    		// get valid linknames
    		// ----------------------------------------------------------------------------
    		
    		intLoopCount = 0;
    		
	    	validLinknames[0] = "";
	    	validLinknames[1] = "";
	    	validLinknames[2] = "";
	    	validLinknames[3] = "";
	    	
	    	// get today
	    	
	    	try {
	    		// get first valid URL
	    		
	            int intCheckTomorrow = 0;
	    			            
	            for (intLoopCount = 0; intLoopCount < possibleLinkname.length; intLoopCount++) {
	    			
	            	URL checkURL = new URL ("http://humgym.net/vertretungsplan.html?file=tl_files/Vertretungsplaene/" 
	            															+ possibleLinkname[intLoopCount] + ".pdf");
			    	URL checkURLN = new URL ("http://humgym.net/vertretungsplan.html?file=tl_files/Vertretungsplaene/" 
	            														+ possibleLinkname[intLoopCount] + "%20N.pdf"); 
			    	
			    	if(intCheckTomorrow == 0) {
		    			try {
		    				URLConnection urlConnection = checkURL.openConnection();
		    				if (urlConnection.getContentType().equalsIgnoreCase("application/pdf")) {
		    			    	validLinknames[0]  = possibleLinkname[intLoopCount];
		    					
		    			    	// check for N
		    			    	URLConnection urlConnectionN = checkURLN.openConnection();
		    	   				if (urlConnectionN.getContentType().equalsIgnoreCase("application/pdf")) {
		    	    				validLinknames[1] = possibleLinkname[intLoopCount] + "%20N";
		    	    			}
		    					intCheckTomorrow = 1;
		    				}
		    			}	 
		    			catch (NullPointerException e) {
		    			}
			    	}
			    	
			    	else if(intCheckTomorrow == 1) {
		    			try {
		    				URLConnection urlConnection = checkURL.openConnection();
		    				if (urlConnection.getContentType().equalsIgnoreCase("application/pdf")) {
		    			    	validLinknames[2]  = possibleLinkname[intLoopCount];
		    			    	
		    			    	// check for N
		    					URLConnection urlConnectionN = checkURLN.openConnection();
		    	   				if (urlConnectionN.getContentType().equalsIgnoreCase("application/pdf")) {
		    	    				validLinknames[3] = possibleLinkname[intLoopCount] + "%20N";
		    	    			}
		    					break;
		    				}
		    			}	 
		    			catch (NullPointerException e) {
		    			}	
			    	}
			    
			    	publishProgress((int) dialog.getProgress() + 1 );
			    	
	    		}
	    			    		
	    	} 
	    	catch (Exception e) {
	    	}
	    	
            publishProgress((int) 20);
            
    		// ----------------------------------------------------------------------------
    		// download newest file
    		// ----------------------------------------------------------------------------
            
            // first downloads 
	    	
	    	if (!(validLinknames[0].matches(""))) boolConnection = true;
	    	
	    	if (boolConnection) {
	    		
	    		int progressBuffer = 1;
	    		
	    		for (intLoopCount = 0; intLoopCount < 4; intLoopCount++) 
	    			if(!(validLinknames[intLoopCount].matches(""))) 
	    				progressBuffer++;
	    		
	    		try {	
		    		for (int i = 0; i < 4; i++) {
		    			fileDestinations[i] = "";
		    			if (!(validLinknames[i].equals(""))) {
			    		
			    			// open connection to URL
			    			URL url = new URL("http://humgym.net/vertretungsplan.html?file=tl_files/Vertretungsplaene/" + validLinknames[i] + ".pdf");
			    			
			    			// fileLength for progressBar
			    			URLConnection connection = url.openConnection();
			                connection.connect();
			    			int fileLength = connection.getContentLength();

			    			// download the file
			    			InputStream input = new BufferedInputStream(url.openStream());
			    			OutputStream output = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath(), validLinknames[i].replace("%20", " ") + ".pdf"));
			         
			    			// write data byte by byte into new file
			    			byte data[] = new byte[1024];
			    			int count;
			                long downloaded = 0;

			    			while ((count = input.read(data)) != -1) {
			                    downloaded += count;
			                    publishProgress( (int) ((downloaded * 30 / progressBuffer / fileLength)) + 20 * (i + 1) );
			    				output.write(data, 0, count);
			    			}
			    		
			    			// close all opened streams
			    			output.flush();
			    			output.close();
			    			input.close();
			    			
			    			fileDestinations[i] = validLinknames[i].replace("%20", " ");
			    			
			    			if(!(validLinknames[0].equals(""))) boolFileExistsToday 	= true;
			    			if(!(validLinknames[2].equals(""))) boolFileExistsTomorrow 	= true; 

			    			
		    			}
		    		}
		    	} catch(Exception e) {
		    		e.printStackTrace();
		    	}
	    		
	    	}
	    	
	    	// download menu
	    	
        	try {	    		
        		publishProgress((int) 50);
        		// open connection to URL
        		URL url = new URL("http://www.lift-nordhausen.de/index.php/speiseplan.html");
    			
        		// fileLength for progressBar
    			// getContentLength() returns wrong value
    			int fileLength = 0;
        		InputStream inputFileLength = new BufferedInputStream(url.openStream());

	 			// download the file
        		InputStream input = new BufferedInputStream(url.openStream());
        		OutputStream output = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath(), "menu.html"));
	 		         
        		// write data byte by byte into new file
        		byte data[] = new byte[1024];	 
        		int downloaded = 0;
        		int count;
	
        		while ((count = inputFileLength.read(data)) != -1) {
        			fileLength += count;
        		}
        		
        		inputFileLength.close();

        		
        		while ((count = input.read(data)) != -1) {
        			output.write(data, 0, count);
        			downloaded += count;
        			publishProgress(50 + (int) (downloaded * 10 / fileLength));
        		}
	 			    			
        		// close all opened streams
        		output.flush();
        		output.close();
        		input.close();
        	} catch (Exception e) {
	    		e.printStackTrace();
        	}
	    	
    		// ----------------------------------------------------------------------------
    		// convert data into txt and readable txt
    		// ----------------------------------------------------------------------------
	    	
        	// convert main data
        	
			if (boolFileExistsToday || boolFileExistsTomorrow) {
				for (int intI = 0; intI < 4; intI++) 
					if(!(fileDestinations[intI].equals(""))) 
						fileAmount++;
				
				for (int intI = 0; intI < 4; intI++) {
					if(!(fileDestinations[intI].matches(""))) {

						String[] splittedLine = fileDestinations[intI].split(" ");
						// 2013 03 17 N --> 4 parts in filename
						if (splittedLine.length > 3) 
							boolIsN = true;
						Converter converter = new Converter(fileDestinations[intI]);
						
						try {
							converter.parsePDF();
							publishProgress(dialog.getProgress() + 30 / fileAmount);		
							converter.cleanTXT();
							converter.finish();
							
							if(boolIsN) {
								converter.merge();
								boolIsN = false;
							}
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			// convert menu
			
			publishProgress((int) 90);
			Converter converter = new Converter("-->menu<--");
 	 		
        	try {
        		converter.cleanMenu();
        		publishProgress((int) 100);
        		boolFileExistsMenu = true;
        	} catch (IOException e) {
        		e.printStackTrace();
        	}				
							
			return "";
	    		    		    		    	
	    }
    	
        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            dialog.setProgress(progress[0]);
        }
    	
    	@Override
        protected void onPostExecute(String result) {
    		
    		String update;
    		
    		// get update date
    		Calendar updateTime = new GregorianCalendar();
    		Calendar.getInstance();
    		updateTime.setTime(new Date());
    		
      		if (updateTime.get(Calendar.MINUTE) < 10) 	update = String.valueOf(updateTime.get(Calendar.HOUR_OF_DAY)) + "_0" + String.valueOf(updateTime.get(Calendar.MINUTE)) + "_";
    		else										update = String.valueOf(updateTime.get(Calendar.HOUR_OF_DAY)) + "_" + String.valueOf(updateTime.get(Calendar.MINUTE)) + "_";
    		
       		// today
    		if(boolFileExistsToday) {
    			argumentsToday[0] = Environment.getExternalStorageDirectory().getPath() + "/" + fileDestinations[0];
    			update 			 += Environment.getExternalStorageDirectory().getPath() + "/" + fileDestinations[0] + "_";
    			argumentsToday[1] = "0";
    			argumentsToday[2] = SETT_fieldToBeSearched;
    			argumentsToday[3] = valueSet;
    		}
    		else {
    			argumentsToday[0] = "<--Error-->";
    			update 			 += " " + "_";
    			argumentsToday[1] = "1";
    			argumentsToday[2] = "<--Error-->";
    			argumentsToday[3] = "<--Error-->";
    		}
    	   			
    		// tomorrow
    		if(boolFileExistsTomorrow) {
    			argumentsTomorrow[0] = Environment.getExternalStorageDirectory().getPath() + "/" + fileDestinations[2];
    			update 			 	+= Environment.getExternalStorageDirectory().getPath() + "/" + fileDestinations[2];
    			argumentsTomorrow[1] = "0";
    			argumentsTomorrow[2] = SETT_fieldToBeSearched;
    			argumentsTomorrow[3] = valueSet;
    		}
    		else {
    			argumentsTomorrow[0] = "<--Error-->";
    			update 				+= " ";
    			argumentsTomorrow[1] = "1";
    			argumentsTomorrow[2] = "<--Error-->";
    			argumentsTomorrow[3] = "<--Error-->";   
    		}
    		   		
    		// menu
    		if(boolFileExistsMenu){
    			argumentsMenu[0] = "<--Menu-->";
    			argumentsMenu[1] = "0";
    			argumentsMenu[2] = "<--Menu-->";
    			argumentsMenu[3] = "<--Menu-->";
    		}
    		else {
    			argumentsMenu[0] = "<--Error-->";
    			argumentsMenu[1] = "1";
    			argumentsMenu[2] = "<--Error-->";
    			argumentsMenu[3] = "<--Error-->";
    		}
    		
    		// forces reload
    		ActionBar bar = getSupportActionBar();
    		bar.selectTab(tabTomorrow);
    		bar.selectTab(tabMenu);
    		bar.selectTab(tabToday);
    		
    		try {
				saveLog();
				saveUpdateState(update);
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
			headerUpdate.setText("Zuletzt aktualisiert: " + lastUpdate[0] + ":" + lastUpdate[1]);
    		
    		// finally close the dialog
    		dialog.dismiss();
    		
    		return;
    	}
    	
	}	

}