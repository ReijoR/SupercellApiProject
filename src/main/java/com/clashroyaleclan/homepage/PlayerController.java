package com.clashroyaleclan.homepage;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PlayerController {

    private static final String API_URL = "https://api.clashroyale.com/v1/players/%s";

    @Value("${api.key}")
    private String API_KEY;

    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);

    @GetMapping("/players")
    public String getPlayer(@RequestParam String playerTag, Model model) throws IOException, InterruptedException {
    String encodedPlayerTag = URLEncoder.encode(playerTag, StandardCharsets.UTF_8);
    String apiUrl = String.format(API_URL, encodedPlayerTag);
    log.info("apiUrl: {}", apiUrl);

    // Set up the API request
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Authorization", "Bearer " + API_KEY)
            .header("Accept", "application/json")
            .build();

    // Send the API request and deserialize the response
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(response.body());

    // Create a Player object and set its properties
    Player player = new Player();
    player.setName(rootNode.get("name").asText());
    player.setTrophies(rootNode.get("trophies").asInt());
    player.setTag(rootNode.get("tag").asText());
    player.setExpLevel(rootNode.get("expLevel").asInt());
    player.setBestTrophies(rootNode.get("bestTrophies").asInt());
    player.setWins(rootNode.get("wins").asInt());
    player.setLosses(rootNode.get("losses").asInt());

    // Set the clan name if the player is in a clan
    if (rootNode.has("clan")) {
        JsonNode clanNode = rootNode.get("clan");
        player.setClanName(clanNode.get("name").asText());
        player.setClanTag(clanNode.get("tag").asText());
    }

    // Add the Player object to the model
    model.addAttribute("player", player);

    // Return the name of the view to be rendered
    return "player";
    }


}


