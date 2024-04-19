package com.function;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Optional;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;

public class FindThumbnailFunction {
    @FunctionName("getMovieImage")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("movieName") String movieName,
            final ExecutionContext context) {

        context.getLogger().info("Java HTTP trigger processed a request.");

        try {
            // Retrieve storage account from connection-string.
            String connectStr = "########";
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectStr);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Retrieve reference to a previously created container.
            CloudBlobContainer container = blobClient.getContainerReference("movies-tumbnails");

            // Get the IMDb ID for the movie
            String imdbId = getIMDbId(movieName);
            context.getLogger().info("IMDb ID: " + imdbId);
            if (imdbId.isEmpty()) {
                context.getLogger().info("No IMDb ID found for the specified movie.");
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("No IMDb ID found for the specified movie.").build();
            }

            String imageUrl = getMovieImageByIMDbId(imdbId);
            imageUrl = imageUrl.replace("\\/", "/");
            context.getLogger().info("Movie Image URL: " + imageUrl);
            if (imageUrl.isEmpty()) {
                context.getLogger().info("No image found for the specified movie.");
                return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("No image found for the specified movie.").build();
            }

            // Get image data from the URL
            byte[] imageBytes = getImageData(imageUrl);
            context.getLogger().info(Arrays.toString(imageBytes));

            // Upload the image data to Azure Blob Storage
            CloudBlockBlob blob = container.getBlockBlobReference(movieName + ".jpeg");
            blob.uploadFromByteArray(imageBytes, 0, imageBytes.length);

            context.getLogger().info("Image uploaded successfully.");
            return request.createResponseBuilder(HttpStatus.OK).body("Image uploaded successfully.").build();
        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the request.").build();
        }
    }

    private static String getIMDbId(String movieName) throws IOException, InterruptedException {
        // Send request to get IMDb ID
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://mdblist.p.rapidapi.com/?s=" + movieName))
                .header("X-RapidAPI-Key", "118f11f20bmsh28dfcf70c48fd72p1dc891jsn38a9110a00fd")
                .header("X-RapidAPI-Host", "mdblist.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        // Parse response to get IMDb ID
        if (response.statusCode() == 200) {
            String responseBody = response.body();

            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray searchResults = jsonObject.getAsJsonArray("search");
            if (searchResults.size() > 0) {
                JsonObject firstResult = searchResults.get(0).getAsJsonObject();
                String imdbId = firstResult.get("imdbid").getAsString();
                return imdbId;
            } else {
                return "";
            }

        } else {
            return "";
        }
    }

    private static String getMovieImageByIMDbId(String imdbId) throws IOException, InterruptedException {
        // Send request to get movie image by IMDb ID
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://movies-tv-shows-database.p.rapidapi.com/?movieid=" + imdbId))
                .header("Type", "get-movies-images-by-imdb")
                .header("X-RapidAPI-Key", "118f11f20bmsh28dfcf70c48fd72p1dc891jsn38a9110a00fd")
                .header("X-RapidAPI-Host", "movies-tv-shows-database.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        // Parse response to get movie image URL
        if (response.statusCode() == 200) {
            String responseBody = response.body();
            int imageUrlStartIndex = responseBody.indexOf("poster") + 9;
            int imageUrlEndIndex = responseBody.indexOf("\"", imageUrlStartIndex);
            String imageUrl = responseBody.substring(imageUrlStartIndex, imageUrlEndIndex);
            return imageUrl;
        } else {
            return "";
        }
    }

    private static byte[] getImageData(String imageUrl) throws IOException {
        // Fetch image data from the URL
        try (InputStream in = new URL(imageUrl).openStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            return out.toByteArray();
        }
    }
}
