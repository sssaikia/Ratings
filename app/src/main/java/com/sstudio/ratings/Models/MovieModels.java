package com.sstudio.ratings.Models;

/**
 * Created by Alan on 11/21/2017.
 */
public class MovieModels {

    private String imageURL;
    private String name;
    private String imdbID;
    private String year;

    public MovieModels(String name,String imageURL,String imdbID,String year){
        this.name=name;
        this.imageURL=imageURL;
        this.imdbID=imdbID;
        this.year=year;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getName(){
        return name;
    }

    public void setName(String s){
        name = s;
    }

    public String getImageURL(){
        return imageURL;
    }

    public void setImageURL(String s){
        imageURL = s;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setRating(String imdbID) {
        this.imdbID = imdbID;
    }
}