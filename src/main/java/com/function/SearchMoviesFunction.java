package com.function;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SearchMoviesFunction {

    @FunctionName("searchMovies")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("search") String search,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        try {
            List<Movie> movies = searchMovies(search);

            return request.createResponseBuilder(HttpStatus.OK).body(movies).build();
        } catch (SQLException e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error searching for movies: " + e.getMessage()).build();
        }
    }

    private List<Movie> searchMovies(String search) throws SQLException {
        // String jdbcUrl = System.getenv("DB_URL");
        // String username = System.getenv("DB_USERNAME");
        // String password = System.getenv("DB_PASSWORD");

        String jdbcUrl = "jdbc:sqlserver://####.windows.net:1433;database=####";
        String username = "####";
        String password = "####";


        
        String selectSql = "SELECT * FROM Movies_valeri";
        if (search != null && !search.isEmpty()) {
            selectSql += " WHERE title LIKE ?";
        }

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(selectSql)) {

            if (search != null && !search.isEmpty()) {
                pstmt.setString(1, "%" + search + "%");
            }

            try (ResultSet resultSet = pstmt.executeQuery()) {
                List<Movie> movies = new ArrayList<>();
                while (resultSet.next()) {
                    Movie movie = new Movie(
                            resultSet.getString("title"),
                            resultSet.getInt("year"),
                            resultSet.getString("genre"),
                            resultSet.getString("director"),
                            resultSet.getString("actors"),
                            resultSet.getString("description"),
                            resultSet.getDouble("average_rating"));

                    movies.add(movie);
                }
                return movies;
            }
        }
    }
}
