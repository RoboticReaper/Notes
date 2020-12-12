package com.hoversfw.notes;

public class RecyclerviewItem {
    private String title;
    private String description;

    public RecyclerviewItem( String mtitle, String mdescription){
        title=mtitle;
        description=mdescription;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }
}
