package com.function;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CalculateAverageRatingFunction {

    @FunctionName("calculateAverageRating")
    public void run(
            @TimerTrigger(name = "timerInfo", schedule = "0 30 11 * * *") String timerInfo,
            final ExecutionContext context) {
        context.getLogger().info("Java Timer trigger function executed at: " + java.time.LocalDateTime.now());

        try {
            updateAverageRatings();
        } catch (SQLException e) {
            context.getLogger().warning("Error updating average ratings: " + e.getMessage());
        }
    }

    private void updateAverageRatings() throws SQLException {
        String jdbcUrl = "jdbc:sqlserver://####.windows.net:1433;database=####";
        String username = "####";
        String password = "####";

        
        String selectSql = "SELECT movie_id, AVG(Ratings_valeri) AS average_rating FROM Ratings_valeri GROUP BY movie_id";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(selectSql);
             ResultSet resultSet = pstmt.executeQuery()) {

            while (resultSet.next()) {
                int movieId = resultSet.getInt("movie_id");
                double averageRating = resultSet.getDouble("average_rating");

                updateMovieWithAverageRating(conn, movieId, averageRating);
            }
        }
    }

    private void updateMovieWithAverageRating(Connection conn, int movieId, double averageRating) throws SQLException {
        String updateSql = "UPDATE Movies_valeri SET average_rating = ? WHERE movie_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setDouble(1, averageRating);
            pstmt.setInt(2, movieId);

            pstmt.executeUpdate();
        }
    }

}
