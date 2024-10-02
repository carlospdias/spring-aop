package br.jus.tse.silver_screen;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {


        System.out.println( "Hello World!" );

        Client client = new Client();
        //client.showMoviesNormal();

        System.out.println("******************************************************************");
        client.showMoviesWithProxies();

    }
}
