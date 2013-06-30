package com.erikHolz.humVP;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class TabFragmentToday extends SherlockFragment {
	public View fragmentView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_tabs, container, false);
        String arguments[] = ((MainActivity) getActivity()).getFragmentArguments(0);
        buildLayout(arguments[0],Integer.parseInt(arguments[1]),arguments[2], arguments[3]);
        return fragmentView;          
    }
	
	public void buildLayout(String filename, int mode, String field, String value) {
		
		Integer intLoopCount;
		
		ExpandableListView listView = (ExpandableListView) fragmentView.findViewById(R.id.expandableListView);
		ExpandableListAdapter adapter = new ExpandableListAdapter(getActivity(), new ArrayList<String>(), new ArrayList<ArrayList<OutLessons>>());
		
		if(mode == 0) {
			DatasheetLesson datasheetLesson = new DatasheetLesson();
			datasheetLesson.readFile(filename);
		
			int[] 	entryPos 	= datasheetLesson.searchByCategory(field, value);
			int		entryCount	= entryPos.length;
			
	        if (entryCount > 0)
	        	for (intLoopCount = 0; intLoopCount < entryCount; intLoopCount++) {       		
	        		adapter.addItem(new OutLessonElements(datasheetLesson.raum[entryPos[intLoopCount]], datasheetLesson.stunde[entryPos[intLoopCount]] + ". STUNDE"));
	        		adapter.addItem(new OutLessonElements(datasheetLesson.fach[entryPos[intLoopCount]], datasheetLesson.stunde[entryPos[intLoopCount]] + ". STUNDE"));
	        		adapter.addItem(new OutLessonElements(datasheetLesson.ausfall[entryPos[intLoopCount]], datasheetLesson.stunde[entryPos[intLoopCount]] + ". STUNDE"));
	        		adapter.addItem(new OutLessonElements(datasheetLesson.vertretung[entryPos[intLoopCount]], datasheetLesson.stunde[entryPos[intLoopCount]] + ". STUNDE"));
	        	}
	        
	        else 
	       		adapter.addItem(new OutLessonElements("Zur Zeit keine Einträge!", "Keine Vertretung"));	            		        
		}
		
		else if(mode == 1) {
			adapter.addItem(new OutLessonElements("Überpüfen Sie ihre Internetverbindung!", "Hinweis"));
	    	adapter.addItem(new OutLessonElements("Existiert bereits ein Vertretungsplan?", "Hinweis"));
		}
		
		else if(mode == 2) {
			adapter.addItem(new OutLessonElements("Bitte Aktualisieren sie die Daten!", "Hinweis"));
		}
		
		
		listView.setAdapter(adapter);
        
	}

}
