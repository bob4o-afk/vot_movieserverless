package com.function;

public class Rating {
    private int movie_id;
    private String opinion;
    private int rating;
    private String author;

    // Constructor
    public Rating(int movie_id, String author, String opinion, int rating) {
        this.movie_id = movie_id;
        this.opinion = opinion;
        this.rating = rating;
        this.author = author;
    }

    // Getters

    public int getMovieId(){
        return movie_id;
    }

    public String getOpinion() {
        return opinion;
    }

    public int getRating() {
        return rating;
    }

    public String getAuthor() {
        return author;
    }

    // Setters
    public void setTitle(int movie_id) {
        this.movie_id = movie_id;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
