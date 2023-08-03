package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HelloController {

    // FXML elements
    @FXML
    private Label welcomeText;

    @FXML
    private ListView<String> countryListView;

    @FXML
    private ListView<String> moreInfoListView;

    @FXML
    private TextField searchBar;

    @FXML
    private ImageView flagImageView;

    private List<String> countriesData;

    public void initialize() {
        // Initialize the list views and data
        countryListView.getItems().clear();
        countriesData = new ArrayList<>();
        fetchDataFromAPI();
    }

    @FXML
    private void onBackButtonClick() {
        // Clear all views when the back button is clicked
        searchBar.clear();
        countryListView.getItems().clear();
        moreInfoListView.getItems().clear();
        flagImageView.setImage(null);
    }

    @FXML
    protected void onHelloButtonClick() {
        // Set a welcome message when the hello button is clicked
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onInfoButtonClick() {
        // Fetch country details when the info button is clicked
        String selectedCountry = countryListView.getSelectionModel().getSelectedItem();
        fetchCountryDetails(selectedCountry);
    }

    @FXML
    protected void onSearchBarKeyReleased() {
        // Filter country list based on search text
        String searchText = searchBar.getText().toLowerCase();
        countryListView.getItems().clear();

        for (String country : countriesData) {
            if (country.toLowerCase().contains(searchText)) {
                countryListView.getItems().add(country);
            }
        }
    }

    // Helper method to display JSON data recursively
    private void displayJsonData(String keyPrefix, JsonNode node, StringBuilder stringBuilder) {
        if (node.isValueNode()) {
            // Append the key-value pair to the string builder
            String value = node.asText();
            if (value.startsWith("http://") || value.startsWith("https://")) {
                stringBuilder.append(keyPrefix).append(": ").append("<a href=\"").append(value).append("\">").append(value).append("</a>").append("\n");
            } else {
                stringBuilder.append(keyPrefix).append(": ").append(value).append("\n");
            }
        } else {
            // Recursively traverse object and array nodes
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                JsonNode value = entry.getValue();

                if (value.isObject() || value.isArray()) {
                    displayJsonData(keyPrefix.isEmpty() ? key + ": " : keyPrefix + key + ": ", value, stringBuilder);
                } else {
                    // Append the key-value pair to the string builder
                    String fieldValue = value.asText();
                    if (fieldValue.startsWith("http://") || fieldValue.startsWith("https://")) {
                        stringBuilder.append(keyPrefix).append(key).append(": ").append("<a href=\"").append(fieldValue).append("\">").append(fieldValue).append("</a>").append("\n");
                    } else {
                        stringBuilder.append(keyPrefix).append(key).append(": ").append(fieldValue).append("\n");
                    }
                }
            }
        }
    }

    // Helper method to display detailed country information
    private void displayCountryInfo(JsonNode countryNode) {
        StringBuilder countryDetails = new StringBuilder();
        String flagImageUrl = countryNode.at("/flags/png").asText();

        // Iterate through all fields in the JSON response and append their keys and values
        Iterator<Map.Entry<String, JsonNode>> fields = countryNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            JsonNode value = entry.getValue();

            String formattedKey = key.replace(">", "").trim();
            if (value.isValueNode()) {
                // Append the key-value pair to the string builder
                String formattedValue = value.asText();
                countryDetails.append(formattedKey).append(": ").append(formattedValue).append("\n");
            } else {
                // Recursively display nested JSON data
                displayJsonData(formattedKey + " > ", value, countryDetails);
            }
        }

        // Add the flag image URL as a clickable link
        countryDetails.append("Flag Image: ").append(flagImageUrl).append("\n");

        // Set the detailed information ListView with the formatted string
        moreInfoListView.getItems().clear();
        moreInfoListView.getItems().add(countryDetails.toString());

        // Load and display the flag image
        Image flagImage = new Image(flagImageUrl);
        flagImageView.setImage(flagImage);
    }

    // Helper method to fetch country details from the API
    private void fetchCountryDetails(String countryName) {
        String apiUrl = "https://restcountries.com/v3.1/name/" + countryName;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseJson = objectMapper.readTree(conn.getInputStream());

                // If the response is an array, assume the first element contains the relevant country data
                if (responseJson.isArray() && responseJson.size() > 0) {
                    JsonNode countryNode = responseJson.get(0);
                    displayCountryInfo(countryNode);
                } else {
                    System.out.println("Failed to fetch country details. Country not found.");
                }
            } else {
                System.out.println("Failed to fetch country details. Response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to fetch data from the API and populate the countryListView
    private void fetchDataFromAPI() {
        String apiUrl = "https://restcountries.com/v3.1/all";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseJson = objectMapper.readTree(conn.getInputStream());

                // Extract country names from the API response and populate the countriesData list
                for (JsonNode countryNode : responseJson) {
                    String countryName = countryNode.at("/name/common").asText();
                    countriesData.add(countryName);
                }

                // Add country names to the ListView
                countryListView.getItems().addAll(countriesData);
            } else {
                System.out.println("Failed to fetch data. Response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
