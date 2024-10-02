package br.jus.tse.silver_screen;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class FilmSessions implements MovieSessions{

    private String parseMovieSessions(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);

        JsonNode movies = node.get(0).get("movies");
        ObjectMapper mapperCreator = new ObjectMapper();
        ArrayNode arrayNode = mapperCreator.createArrayNode();

        for (JsonNode nodeJson : movies){
            ObjectNode movieNode = mapper.createObjectNode();
            movieNode.put("titulo", nodeJson.get("title"));
            movieNode.put("duracao", nodeJson.get("duration"));
            movieNode.put("censura", nodeJson.get("contentRating"));
            arrayNode.add(movieNode);

            nodeJson.get("sessionTypes").forEach((nodeJ)->{
                ArrayNode arraySessionNode = mapperCreator.createArrayNode();
                nodeJ.get("sessions").forEach((nodeSession)->{
                    ObjectNode roomSessionNode = mapperCreator.createObjectNode();
                    roomSessionNode.put("Horario", nodeSession.get("date").get("hour"));
                    arraySessionNode.add(roomSessionNode);
                });
                movieNode.set("sessoes", arraySessionNode);
            });

        }
        String jsonArrayAsString = mapperCreator.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);

        return jsonArrayAsString;
    }
    public String retrieveMovieSessions(){
        String formattedDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String uri="https://api-content.ingresso.com/v0/sessions/city/113/theater/381/partnership/home/groupBy/sessionType?date=".concat(formattedDate);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:130.0) Gecko/20100101 Firefox/130.0")
                .setHeader("Accept","application/json, text/plain, */*")
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();
            return parseMovieSessions(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    public static void main(String[] args) {
        FilmSessions f = new FilmSessions();
        long starting_time = System.currentTimeMillis();
        f.retrieveMovieSessions();
        long ending_time = System.currentTimeMillis();
        System.out.println("                 *************************** Tempo de execução:" + (ending_time-starting_time));

    }
}
