package br.jus.tse.silver_screen;

public class Client {

    void showMoviesNormal(){
        long starting_time = System.currentTimeMillis();
        FilmSessions sessions = new FilmSessions();
        String jsonSessions = sessions.retrieveMovieSessions();
        long ending_time = System.currentTimeMillis();
        System.out.println(jsonSessions);
        System.out.println("                 *************************** Tempo de execução:" + (ending_time-starting_time));

    }

    public void showMoviesWithProxies() {
        long starting_time = System.currentTimeMillis();
        MovieSessions sessions = new FilmSessionsProxy();
        String jsonSessions = sessions.retrieveMovieSessions();
        long ending_time = System.currentTimeMillis();
        System.out.println(jsonSessions);
        System.out.println("                 *************************** Tempo de execução:" + (ending_time-starting_time));
    }
}
