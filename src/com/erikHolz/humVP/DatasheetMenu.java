package com.erikHolz.humVP;

/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*	Author:				Erik Holzwirth										*/
/*	Classname:			DatasheetMenu										*/
/*	Last Time Edited:	21.03.2013 18:34									*/
/*	Methods:			void 	initialize									*/
/*						void 	readFile									*/
/*	Description:		contains all information read from the created txt	*/
/*						file, used like a table								*/
/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.os.Environment;

public class DatasheetMenu {

	String[] tag 	= new String[7];
	String[] menuA 	= new String[7];
	String[] menuB	= new String[7];
	
	// ----------------------------------------------------------------------------
	// keywords for replacing numbers with day names
	// ----------------------------------------------------------------------------

	private static final String[] dayNames 	= 	{ 		"Montag",
														"Dienstag",
														"Mittwoch",
														"Donnerstag",
														"Freitag",				//  5
														"Samstag",
														"Sonntag",
													};

	// ----------------------------------------------------------------------------
	// read data from created txt file
	// ----------------------------------------------------------------------------

	void readFile() {
		try
		{			
			BufferedReader 	input 	= new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getPath() + "/" + "essenBuffer.txt"));
			String line = null;
			
			while ((line = input.readLine()) != null) {
				String[] splittedLine = line.split("_");
				this.initialize(splittedLine[0], splittedLine[1], splittedLine[2]);
			}
	
			input.close();
		}
		catch( IOException e )
		{
		}	
	}
	
	// ----------------------------------------------------------------------------
	// give every "row" its values
	// ----------------------------------------------------------------------------

	void initialize(String otag, String omenua,String omenub) {
		// Counter for day replacement;
		
		tag[Integer.parseInt(otag) - 1]			= dayNames[Integer.parseInt(otag) - 1];
		menuA[Integer.parseInt(otag) - 1] 		= omenua;
		menuB[Integer.parseInt(otag) - 1] 		= omenub;
	}
	
}
