package com.erikHolz.humVP;

public class OutLessons {

	    private String name;
	    private String group;

	    public OutLessons(String name) {
	        this.name 	= name;
	    }

	    public void setGroup(String group) {
	        this.group = group;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }
	    
	    public String getName() {
	        return name;
	    }
	    
	    public String getGroup() {
	        return group;
	    }
}