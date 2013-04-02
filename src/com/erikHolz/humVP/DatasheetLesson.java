package com.erikHolz.humVP;

	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	/*	Author:				Erik Holzwirth										*/
	/*	Classname:			DatasheetLesson										*/
	/*	Last Time Edited:	03.03.2013 14:10									*/
	/*	Methods:			void 	initialize									*/
	/*						void 	readFile									*/
	/*						int[]	searchByClass								*/
	/*	Description:		contains all information read from the created txt	*/
	/*						file, used like a table								*/
	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatasheetLesson {

	String[] klasse 	= new String[120];
	String[] stunde 	= new String[120];
	String[] fach		= new String[120];
	String[] raum		= new String[120];
	String[] ausfall	= new String[120];
	String[] vertretung	= new String[120];
	int		 id 		= 0;
	
	// ----------------------------------------------------------------------------
	// keywords for replacing short tags with long names
	// ----------------------------------------------------------------------------

	private static final String[] shortNames 	= 	{ 	"ma",
														"ph",
														"ch",
														"bi",
														"as",					//  5
														"if",
														"de",
														"en",
														"fr",
														"la",					// 10
														"ru",
														"sn",
														"ge",
														"sk",
														"wr",					// 15
														"et",
														"mu",
														"ku",
														"sp",
														"mnt"
													};
	
	private static final String[] longNames 	=	{	"Mathematik",
														"Physik",
														"Chemie",
														"Biologie",
														"Astronomie",			//  5
														"Informatik",
														"Deutsch",
														"Englisch",
														"Französisch",
														"Latein",				// 10
														"Russisch",
														"Spanisch",
														"Geschichte",
														"Sozialkunde",
														"Wirtschaft",			// 15
														"Ethik",
														"Musik",
														"Kunst",
														"Sport",
														"MNT",
													};

	// ----------------------------------------------------------------------------
	// give every "row" its values
	// ----------------------------------------------------------------------------

	void initialize(String oklasse, String ostunde,String ofach, String oraum, String oausfall, String overtretung) {
		// Counter for class replacement;
		int intI = 0;
		
		id++;
		klasse[id] 		= oklasse;
		stunde[id] 		= ostunde;

		while(!(ofach.equals(shortNames[intI])) && intI < 19) intI++;
		
		fach[id] 		= longNames[intI];
		raum[id] 		= "Raum " + oraum;
		ausfall[id] 	= oausfall;
		vertretung[id] 	= overtretung;
	}
	
	// ----------------------------------------------------------------------------
	// read data from created txt file
	// ----------------------------------------------------------------------------

	void readFile(String filename) {
		try
		{			
			BufferedReader 	input 	= new BufferedReader(new FileReader(filename + ".txt"));
			String line = null;
			
			while ((line = input.readLine()) != null) {
				String[] splittedLine = line.split("_");
				this.initialize(splittedLine[0], splittedLine[1], splittedLine[2], splittedLine[3], splittedLine[4], splittedLine[5]);
			}
	
			input.close();
			
			// get recent date
			Calendar date  	= new GregorianCalendar();
    		Calendar.getInstance();
   			date.setTime(new Date());
    		date.add(Calendar.DAY_OF_YEAR, -1);
    		
   			// get filename of yesterday's file
    		String filenameYesterday 		= "";
    		filenameYesterday	    		+= date.get(Calendar.YEAR);
    		
       		if (date.get(Calendar.MONTH) + 1 < 10) 	filenameYesterday	+= " 0" + (date.get(Calendar.MONTH) + 1);
    		else									filenameYesterday	+= " " 	+ (date.get(Calendar.MONTH) + 1);
    			
    		if (date.get(Calendar.DATE) < 10)     	filenameYesterday 	+= " 0" + date.get(Calendar.DATE);
    		else									filenameYesterday	+= " "  + date.get(Calendar.DATE);
			
			File f = new File(filenameYesterday + ".txt");
			if(f.exists()) f.delete();
		}
		catch( IOException e )
		{
			
		}	
	}
	
	// ----------------------------------------------------------------------------
	// find values value in field field 
	// ----------------------------------------------------------------------------

	int[] searchByCategory(String field, String value) {
		int[] 	entryPos = new int[8];
		int[] 	entryPosSorted = new int[8];

		int 	entryCount = 0;
		int 	intI = 0;
		int 	intJ = 0;
		
		if(field.equals("Klasse")) {
			for(intI = 1; intI <= id; intI++) {
				if (klasse[intI].equals(value)) {
					entryPos[intJ] = intI;
					intJ++;
				}
			}
		}
		
		else if(field.equals("Vertretung")) {
			for(intI = 1; intI <= id; intI++) {
				if (vertretung[intI].equals(value)) {
					entryPos[intJ] = intI;
					intJ++;
				}
			}
		}
		
		else if(field.equals("Ausfall")) {
			for(intI = 1; intI <= id; intI++) {
				if (ausfall[intI].equals(value)) {
					entryPos[intJ] = intI;
					intJ++;
				}
			}
		}
		
		// sort & clean results
		
		for (intI = 0; intI < 8; intI++) {
			if(entryPos[intI] != 0) 
				entryCount++;
		}
		
		for (intI = 0; intI < entryCount; intI++) {
			for (intJ = 1; intJ < 9; intJ++) {
				String str = String.valueOf(intJ);
				if (stunde[entryPos[intI]].equals(str)) {
					entryPosSorted[intJ - 1] = entryPos[intI]; 
				}
			}
		}
	
		entryCount = 0;
		intJ = 0;
		
		for (intI = 0; intI < 8; intI++) {
			if(entryPosSorted[intI] != 0) 
				entryCount++;
		}
		
		int[] 	entryPosClean = new int[entryCount];
		
		for (intI = 0; intI < 8; intI++) {
			if(entryPosSorted[intI] != 0 && intJ < entryCount) {
				entryPosClean[intJ] = entryPosSorted[intI];
				intJ++;
			}
		}
				
		return entryPosClean;
	}
	
	// ----------------------------------------------------------------------------
	// end of functions
	// ----------------------------------------------------------------------------
	
}

