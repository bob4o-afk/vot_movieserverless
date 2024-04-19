package com.function;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class AddMovieFunction {

    @FunctionName("addMovie")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("title") String title,
            @BindingName("year") int year,
            @BindingName("genre") String genre,
            @BindingName("director") String director,
            @BindingName("actors") String actors,
            @BindingName("description") String description,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        if (title == null || genre == null || description == null || director == null || actors == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass all movie information as query parameters.").build();
        }

        try {
            Movie movie = new Movie(title, year, genre, director, actors, description);
            insertMovieIntoDatabase(movie);

            return request.createResponseBuilder(HttpStatus.OK).body("Movie added successfully.").build();
        } catch (SQLException e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inserting movie into database: " + e.getMessage()).build();
        }
    }

    private void insertMovieIntoDatabase(Movie movie) throws SQLException {
        String jdbcUrl = "jdbc:sqlserver://####.windows.net:1433;database=####";
        String username = "####";
        String password = "####";


        // SQL statement to insert a new movie
        String sql = "INSERT INTO Movies_valeri (title, year, genre, director, actors, description) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, movie.getTitle());
            pstmt.setInt(2, movie.getYear());
            pstmt.setString(3, movie.getGenre());
            pstmt.setString(4, movie.getDirector());
            pstmt.setString(5, movie.getActors());
            pstmt.setString(6, movie.getDescription());

            pstmt.executeUpdate();
        }
    }
}
