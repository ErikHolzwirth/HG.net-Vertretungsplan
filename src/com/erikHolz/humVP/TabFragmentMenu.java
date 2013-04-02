package com.erikHolz.humVP;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class TabFragmentMenu extends TabFragment {
	public View fragmentView;
		
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_tabs, container, false);
        String arguments[] = ((MainActivity) getActivity()).getFragmentArguments(2);
        buildLayout(Integer.parseInt(arguments[1]));
        return fragmentView;       
    }
    
    
	public void buildLayout(int mode) {
        
		Integer intLoopCount;
		        
		ExpandableListView listView = (ExpandableListView) fragmentView.findViewById(R.id.expandableListView);
		ExpandableListAdapter adapter = new ExpandableListAdapter(getActivity(), new ArrayList<String>(), new ArrayList<ArrayList<OutLessons>>());
			
		if(mode == 0) {
			DatasheetMenu datasheetMenu = new DatasheetMenu();
			datasheetMenu.readFile();
			int intWrittenCount = 0;
					
	        for (intLoopCount = 0; intLoopCount < 7; intLoopCount++) {       		
	        	if(datasheetMenu.menuA[intLoopCount] != null) {
		        	adapter.addItem(new OutLessonElements(datasheetMenu.menuA[intLoopCount], datasheetMenu.tag[intLoopCount]));
		        	adapter.addItem(new OutLessonElements(datasheetMenu.menuB[intLoopCount], datasheetMenu.tag[intLoopCount]));
		        	intWrittenCount++;
	        	}
	        }	
        	
	        if(intWrittenCount == 0)
	        	adapter.addItem(new OutLessonElements("Ein interner Fehler ist aufgetreten", "Hinweis"));
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
