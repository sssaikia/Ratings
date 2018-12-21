
package com.sstudio.ratings.Models.com.moviemeter;

import java.util.List;

public class Moviemeter {

    private List<Datum> data = null;
    private About about;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public About getAbout() {
        return about;
    }

    public void setAbout(About about) {
        this.about = about;
    }

}
