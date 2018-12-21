
package com.sstudio.ratings.Models.trailerurl;

import java.util.List;

public class Metadata {

    private List<String> languages = null;
    private String aspRetio;
    private List<String> filmingLocations = null;
    private List<String> alsoKnownAs = null;
    private List<String> countries = null;
    private String gross;
    private List<String> soundMix = null;
    private String budget;

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getAspRetio() {
        return aspRetio;
    }

    public void setAspRetio(String aspRetio) {
        this.aspRetio = aspRetio;
    }

    public List<String> getFilmingLocations() {
        return filmingLocations;
    }

    public void setFilmingLocations(List<String> filmingLocations) {
        this.filmingLocations = filmingLocations;
    }

    public List<String> getAlsoKnownAs() {
        return alsoKnownAs;
    }

    public void setAlsoKnownAs(List<String> alsoKnownAs) {
        this.alsoKnownAs = alsoKnownAs;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public String getGross() {
        return gross;
    }

    public void setGross(String gross) {
        this.gross = gross;
    }

    public List<String> getSoundMix() {
        return soundMix;
    }

    public void setSoundMix(List<String> soundMix) {
        this.soundMix = soundMix;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

}
