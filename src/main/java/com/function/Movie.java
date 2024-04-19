package com.function;

public class Movie {
    private String title;
    private int year;
    private String genre;
    private String description;
    private String director;
    private String actors;
    private double average_rating;

    public Movie(String title, int year, String genre, String director, String actors, String description, Double average_rating) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.director = director;
        this.actors = actors;
        this.description = description;
        this.average_rating = average_rating;
    }

    public Movie(String title, int year, String genre, String director, String actors, String description) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.director = director;
        this.actors = actors;
        this.description = description;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public String getDirector() {
        return director;
    }

    public String getActors() {
        return actors;
    }

    public double getAverageRating(){
        return average_rating;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseYear(int year) {
        this.year = year;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public void setAverageRating(double average_rating) {
        this.average_rating = average_rating;
    }
}
