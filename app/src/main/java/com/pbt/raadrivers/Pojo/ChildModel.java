package com.pbt.raadrivers.Pojo;

public class ChildModel {
    String title;
    boolean isSelected;
    int resource = -1;

    public ChildModel(String title){
        this.title = title;
    }

    public ChildModel(String title, boolean isSelected){
        this.title = title;
        this.isSelected = isSelected;
    }

    public ChildModel(String profile, int training) {
        this.title = profile;
        this.resource = training;

    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
