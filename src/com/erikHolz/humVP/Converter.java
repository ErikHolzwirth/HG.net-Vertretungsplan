package com.erikHolz.humVP;

	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	/*	Author:				Erik Holzwirth										*/
	/*	Classname:			Converter											*/
	/*	Last Time Edited:	21.03.2013 20:40									*/
	/*	Methods:			void 	parsePDF									*/
	/*						void 	cleanTXT									*/
	/*						void	finish										*/
	/*						void	cleanMenu									*/
	/*	Description:		converts the pdf into a txt and cleans / formats	*/
	/*						the txt file										*/
	/*						!!!!! 				USES iText 				!!!!!	*/
	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.os.Environment;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

public class Converter {
	
	String fileName = "";
	String fileDest = "";
	
	// ----------------------------------------------------------------------------
	// create new object, giving the filename (both pdf input & txt output) as
	// parameter
	// ----------------------------------------------------------------------------

	public Converter(String pFilename) {
		fileDest = Environment.getExternalStorageDirectory().getPath() + "/" + pFilename;
		fileName = pFilename;
	}
	
	// ----------------------------------------------------------------------------
	// converts the PDF into a txt
	// ----------------------------------------------------------------------------

	// ----------------------------------------------------------------------------
	// main function, read/parse PDF file to txt
	// iText Example from: 
	// http://svn.code.sf.net/p/itext/code/book/src/part4/chapter15/ExtractPageContentSorted1.java
	// ----------------------------------------------------------------------------

	public void parsePDF() throws IOException {
		PdfReader reader 				= new PdfReader(fileDest + ".pdf");
		PdfReaderContentParser parser 	= new PdfReaderContentParser(reader);
		PrintWriter out 				= new PrintWriter(new FileOutputStream(fileDest + "__.txt"));
		
		TextExtractionStrategy strategy;
		for (int intI = 1; intI <= reader.getNumberOfPages(); intI++) {
			strategy = parser.processContent(intI, new LocationTextExtractionStrategy());
			out.println(strategy.getResultantText());
		}
		
		out.flush();
		out.close();
		reader.close();
		
		File f = new File(fileDest + ".pdf");
		if(f.exists()) f.delete();
	}
	
	// ----------------------------------------------------------------------------
	// cleans the TXT from not need information
	// ----------------------------------------------------------------------------
	
	// ----------------------------------------------------------------------------
	// first step of cleaning the txt file to make it readable for the program
	// removes unimportant information
	// will keep string relations, "Herr" + "Name" in one String[] "Herr Name"
	// ----------------------------------------------------------------------------

	public void cleanTXT() {
	
		try
		{			
			// skip read line
			boolean boolSkip 	= false;
			
			// end of file found using not need lines
			boolean boolEnd  	= false;
			
			// create objects to write new file and another one to read old file
			BufferedReader 	input 	= new BufferedReader(new FileReader			(fileDest + "__.txt"));
			BufferedWriter 	output 	= new BufferedWriter(new FileWriter(new File(fileDest +  "_.txt"), false)); 
	
			// buffer for read in lines
			String line = null;
			
			// buffer for reading signal signs like '*'
			char[] charBuffer = null;
		
			// read (& write) until end of file (= NULL)
			while ((line = input.readLine()) != null && !boolEnd) {
			
				// counting variable, later used
				Integer intJ = 0;
				Integer intK = 0;
				Integer intL = 0;
				
				// split line to edit it
				String[] splittedLine = line.split(" ");
				
				// line can be the buffer again since splittedLine contains all needed Strings 
				line = "";

				for(int intI = 0; intI < splittedLine.length; intI++) {
					// just checks first field, because skipping sign will only be there
					charBuffer = splittedLine[0].toCharArray();
				
					// intI = splittedLine.length lets the loop counter jump to the last position
					
					// skip headers
					if (splittedLine[intI].equals("Vertretungsplan")) {
						intI = splittedLine.length;
						boolSkip = true;
					} 
					else if (splittedLine[intI].equals("Klasse/Block")) {
						intI = splittedLine.length;
						boolSkip = true;
					}
					else if (splittedLine[intI].equals("Nachtrag")) {
						intI = splittedLine.length;
						boolSkip = true;
					}
					else if (splittedLine[intI].equals("Klasse")) {
						intI = splittedLine.length;
						boolSkip = true;
					}
				
					// stop writing at this lines
					else if (splittedLine[intI].equals("Aufsicht:")) {
						intI = splittedLine.length;
						boolEnd = true;
					}
					else if (splittedLine[intI].equals("Aufsichten:")) {
						intI = splittedLine.length;
						boolEnd = true;
					}
					else if (splittedLine[intI].equals("Aufsicht")) {
						intI = splittedLine.length;
						boolEnd = true;
					}	
					else if (charBuffer[0] == '*') {
						intI = splittedLine.length;
						boolEnd = true;
					}
			
					// catches class tag --> e.g. splittedLine: [0] = "5" [1] = "/" [2] = "1" 
					else if (intI <= 2) line += splittedLine[intI];
				
					// replace " " as separator with "_"
					else {

						// tag for a teacher --> read name after "Herr" || "Frau" with space
						if ( splittedLine[intI].equals("Herr") || splittedLine[intI].equals("Frau") ) {
							line += "_" + splittedLine[intI];
						
							// search next teacher name ("Ausfall" --> "Vertretung")
							if (intJ == 0 && intK == 0) {
							
								// search next mentioning of "Herr" or "Frau"
								for(intJ = intI + 1; intJ < splittedLine.length; intJ++)
									if ( splittedLine[intJ].equals("Herr") || splittedLine[intJ].equals("Frau") ) break;
								for(intK = intI + 1; intK < splittedLine.length; intK++)
									if ( splittedLine[intK].equals("Ausfall") ) break;	
							
								intI++;
								
								// especially for catching double names like "Herr Dr. Klose"
								// if intJ < intK there will be "Vertretung"
								if (intJ < intK) {	
									while(intI < intJ){
										line += " " + splittedLine[intI];
										intI++;
									}
								}
							
								// if intJ > intK there will be "Ausfall"
								else if (intJ > intK) {
									while(intI < intK ){
										line += " " + splittedLine[intI];
										intI++;
									}
								}
							
								intI--;
							
							// else simply add line	
							} 
							else {
								intI++;
								for (intL = intI; intL < splittedLine.length; intL++) 
									line += " " + splittedLine[intL];
								intI = intL;
							}
						} 
						else line += "_" + splittedLine[intI];
					}
				}
			
				output.write(line);
				
				// only add newLine if a line was written and it is not end of file
				if (boolSkip == false && boolEnd == false) {
					output.newLine();
				}
				else boolSkip = false;
			}
					
			// close streams;
			input.close();
			output.close();

			File f = new File(fileDest + "__.txt");
			if(f.exists()) f.delete();

		}
		catch( IOException e )
		{
			e.printStackTrace();
		}	
	}
	
	// ----------------------------------------------------------------------------
	// last changes to the txt file
	// ----------------------------------------------------------------------------

	// ----------------------------------------------------------------------------
	// last modifications to the txt file
	// 5/1,2,3 will be separated into 3 lines "5/1", "5/2", "5/3" ...
	// ----------------------------------------------------------------------------

	public void finish() {
	
		try
		{			
			// create object to write new file and another one to read old file
			BufferedReader 	input 	= new BufferedReader(new FileReader(fileDest + "_.txt"));
			BufferedWriter 	output 	= new BufferedWriter(new FileWriter(new File(fileDest + ".txt"), false)); 
	
			// buffer for read in lines
			String line = null;
			
			// read (& write) until end of file (= NULL)
			while ((line = input.readLine()) != null) {
				// split line to edit it
				String[] splittedLine = line.split("_");

				// Counter
				int intI = 0;
			
				// line will be the buffer again
				line = "";
			
				// check for multiple classes in one row --> 5/1,2,3
				if (splittedLine[0].length() > 4) {
					// split "5/1,2,3" to "5" and "1,2,3"
					String[] buffer1 = splittedLine[0].split("/");
					// split "1,2,3" to "1" and "2" and "3"
					String[] buffer2 = buffer1[1].split(",");
				
				
					for(intI = 0; intI < buffer2.length; intI++) {
						line += buffer1[0] + "/" + buffer2[intI] + "_" + splittedLine[1] + "_" + splittedLine [2] + "_" + splittedLine [3] + "_" + splittedLine [4] + "_" + splittedLine [5];
						output.write(line);
						output.newLine();
						line = "";
					}
				} 
				
				// check for letters in classnames like 11/A
				else {
					String[] buffer1 = splittedLine[0].split("/");
				
					if (buffer1[1].matches("[A-Z]")){
						for(intI = 1; intI < 4; intI++) {
							line += buffer1[0] + "/" + intI + "_" + splittedLine[1] + "_" + splittedLine [2] + "_" + splittedLine [3] + "_" + splittedLine [4] + "_" + splittedLine [5];
							output.write(line);
							output.newLine();
							line = "";
						}
					}
					else {
						line += splittedLine[0] + "_" + splittedLine[1] + "_" + splittedLine [2] + "_" + splittedLine [3] + "_" + splittedLine [4] + "_" + splittedLine [5];
						output.write(line);
						output.newLine();
					}
				}
			
			}
			
			// close streams
			input.close();
			output.close();
			
			File f = new File(fileDest + "_.txt");
			if(f.exists()) f.delete();
		
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}	
	}

	// ----------------------------------------------------------------------------
	// merge regular file and N file to one file
	// ----------------------------------------------------------------------------

	public void merge() {
		
		try
		{	
			String[] filenameSplitted = fileName.split(" ");
			String mainFile			= filenameSplitted[0] + " " + filenameSplitted[1] + " " + filenameSplitted[2];
			
			BufferedReader 	input 	= new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getPath() + "/" + mainFile + ".txt"));
			BufferedReader 	inputN 	= new BufferedReader(new FileReader(fileDest + ".txt"));
			BufferedWriter 	outputB = new BufferedWriter(new FileWriter(new File(fileDest + "_b.txt"), true)); 
	
			// buffer for read in lines
			String line = null;
		
			// read (& write) until end of file (= NULL)
			while ((line = input.readLine()) != null) {
				outputB.write(line);
				outputB.newLine();	
			}
			while ((line = inputN.readLine()) != null) {
				outputB.write(line);
				outputB.newLine();	
			}
			
			// close streams
			input.close();
			inputN.close();
			outputB.close();
			
			File f1 = new File(Environment.getExternalStorageDirectory().getPath() + "/" + mainFile + ".txt");
			if(f1.exists()) f1.delete();
			File f2 = new File(fileDest + ".txt");
			if(f2.exists()) f2.delete();
			
			File oldFile = new File(fileDest + "_b.txt"); 

			oldFile.renameTo(new File(Environment.getExternalStorageDirectory().getPath() + "/" + mainFile + ".txt"));
		
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}	
	}

	// ----------------------------------------------------------------------------
	// clean the downloaded html file and make it readable
	// ----------------------------------------------------------------------------
	
	public void cleanMenu() throws IOException {
		BufferedReader 	 inStep1 	= new BufferedReader(new FileReader (Environment.getExternalStorageDirectory().getPath() + "/" + "menu.html"));
		BufferedWriter 	outStep1 	= new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory().getPath() + "/" + "essenBufferStep1.txt"), false)); 

		String line = null;
		String[] lineSplitted;
		String[] buffer = null;

		// get rid of both header and footer

		while ((line = inStep1.readLine()) != null) {
			lineSplitted = line.split("ce_speiseplan_day_label");
			if (!(line.equals(lineSplitted[0])))	
				for (int intI = 0; intI < lineSplitted.length - 1; intI++) {
					if (intI + 1 == lineSplitted.length) {
						buffer = lineSplitted[intI].split("/Speiseplan - Startseite_files/lift_footer_verlauf.png");
						outStep1.write(buffer[0] + "\n");
					} else 
						outStep1.write(lineSplitted[intI + 1] + "\n");
					}	
		}
			
		 inStep1.close();
		outStep1.close();
		
		// kill those annoying <html> </tags>
		
		BufferedReader 	 inStep2 	= new BufferedReader(new FileReader (Environment.getExternalStorageDirectory().getPath() + "/" + "essenBufferStep1.txt"));
		BufferedWriter 	outStep2 	= new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory().getPath() + "/" + "essenBufferStep2.txt"), false)); 
		
		while ((line = inStep2.readLine()) != null) {
			lineSplitted = line.split(">");
			for (int intJ = 0; intJ < lineSplitted.length; intJ++)
				outStep2.write(lineSplitted[intJ] + "\n");
		}
		
		 inStep2.close();
		outStep2.close();
		
		BufferedReader 	 inStep3 	= new BufferedReader(new FileReader (Environment.getExternalStorageDirectory().getPath() + "/" + "essenBufferStep2.txt"));
		BufferedWriter 	outStep3 	= new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory().getPath() + "/" + "essenBufferStep3.txt"), false)); 
		
		while ((line = inStep3.readLine()) != null) {
			lineSplitted 	= line.split("</strong");
			if(!(line.equals("</span")))
				buffer 			= line.split("</span");
			else buffer[0] 		= line;
			if (!(line.equals(lineSplitted[0])) || !(line.equals(buffer[0])))	
				outStep3.write(lineSplitted[0] + "\n");
		}
		
		 inStep3.close();
		outStep3.close();
		
		BufferedReader 	 inStep4 	= new BufferedReader(new FileReader (Environment.getExternalStorageDirectory().getPath() + "/" + "essenBufferStep3.txt"));
		BufferedWriter 	outStep4 	= new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory().getPath() + "/" + "essenBufferStep4.txt"), false)); 
		
		while ((line = inStep4.readLine()) != null) {
			lineSplitted 	= line.split("</span");
			if (!(lineSplitted[0].equals("1")))
				outStep4.write(lineSplitted[0] + "\n");
		}
		
		 inStep4.close();
		outStep4.close();
			
		// some last layouting to make easier to read
		
		BufferedReader 	 inStep5	= new BufferedReader(new FileReader (Environment.getExternalStorageDirectory().getPath() + "/" + "essenBufferStep4.txt"));
		BufferedWriter 	outStep5 	= new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory().getPath() + "/" + "essenBuffer.txt"), false)); 
		
		int intMergeCount = 0;
		
		while ((line = inStep5.readLine()) != null) {
			if (line.equals("MONTAG")) 		{ line = "1"; intMergeCount = 3; }
			if (line.equals("DIENSTAG")) 	{ line = "2"; intMergeCount = 3; }
			if (line.equals("MITTWOCH")) 	{ line = "3"; intMergeCount = 3; }
			if (line.equals("DONNERSTAG")) 	{ line = "4"; intMergeCount = 3; }
			if (line.equals("FREITAG")) 	{ line = "5"; intMergeCount = 3; }
			if (line.equals("SAMSTAG")) 	{ line = "6"; intMergeCount = 3; }
			if (line.equals("SONNTAG")) 	{ line = "7"; intMergeCount = 3; }
			
			if (intMergeCount > 0) {
				intMergeCount -= 1;
				if 	(intMergeCount == 0) outStep5.write(line + "\n");
				else					 outStep5.write(line + "_");
			}

		}
		
		 inStep5.close();
		outStep5.close();
		
		// delete buffer files
		
		File f = null;
		
		f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "menu.html");
		if(f.exists()) f.delete();
		f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "essenBufferStep1.txt");
		if(f.exists()) f.delete();
		f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "essenBufferStep2.txt");
		if(f.exists()) f.delete();
		f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "essenBufferStep3.txt");
		if(f.exists()) f.delete();
		f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "essenBufferStep4.txt");
		if(f.exists()) f.delete();
		

		
	}
	
	// ----------------------------------------------------------------------------
	// end of functions
	// ----------------------------------------------------------------------------
	
}