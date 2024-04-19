package com.function;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class AddRatingFunction {

    @FunctionName("addRating")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("movie_id") int movie_id,
            @BindingName("opinion") String opinion,
            @BindingName("rating") int rating,
            @BindingName("author") String author,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        if (opinion == null || author == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass all rating information as query parameters.").build();
        }

        if (rating < 1 || rating > 10) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Rating should be between 1 and 10.").build();
        }

        if (movie_id < 0){
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Movie ID should be a positive int.").build();
        }

        try {
            Rating ratingObj = new Rating(movie_id, author, opinion, rating);
            insertRatingIntoDatabase(ratingObj);

            return request.createResponseBuilder(HttpStatus.OK).body("Rating added successfully.").build();
        } catch (SQLException e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inserting rating into database: " + e.getMessage()).build();
        }
    }

    private void insertRatingIntoDatabase(Rating rating) throws SQLException {
        String jdbcUrl = "jdbc:sqlserver://####.windows.net:1433;database=####";
        String username = "####";
        String password = "####";

        // SQL statement to insert a new rating
        String sql = "INSERT INTO Ratings_valeri (movie_id, author, opinion, rating) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rating.getMovieId());
            pstmt.setString(2, rating.getAuthor());
            pstmt.setString(3, rating.getOpinion());
            pstmt.setInt(4, rating.getRating());

            pstmt.executeUpdate();
        }
    }
}

