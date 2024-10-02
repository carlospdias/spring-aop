package br.jus.tse.silver_screen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class FilmSessionsProxy implements MovieSessions {
    public static final int MAX_CACHE_IN_HOURS = 3;
    private String FILE_NAME_CACHE="D:/tmp/gru/cache-movies.json";

    private boolean isvalidCache(){
        Path path = Paths.get(FILE_NAME_CACHE);
        if (!path.toFile().isFile()){
            return false;
        }
        BasicFileAttributes attr;
        try {
            attr = Files.readAttributes(path, BasicFileAttributes.class);
            attr.lastModifiedTime();

            LocalDateTime ultimaModificacao = LocalDateTime.ofInstant(attr.lastModifiedTime().toInstant(), ZoneId.systemDefault());
            long hoursPassed = ChronoUnit.HOURS.between(ultimaModificacao, LocalDateTime.now());
            return  MAX_CACHE_IN_HOURS > hoursPassed;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    private String getCache(){
        Path path = Paths.get(FILE_NAME_CACHE);
        try {
            String content = Files.readString(path, Charset.defaultCharset());
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void updateCache(String json){
        Path path = Paths.get(FILE_NAME_CACHE);
        try {
            Files.writeString(path, json);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String retrieveMovieSessions() {
        if(!isvalidCache()){
          FilmSessions filmSessions = new FilmSessions();
          String result =  filmSessions.retrieveMovieSessions();
          updateCache(result);
        }
        return getCache();
    }

    public static void main(String[] args) {
        FilmSessionsProxy f = new FilmSessionsProxy();
        long starting_time = System.currentTimeMillis();
        f.retrieveMovieSessions();
        long ending_time = System.currentTimeMillis();

        System.out.println("                 *************************** Tempo de execução:" + (ending_time-starting_time));
    }
}
