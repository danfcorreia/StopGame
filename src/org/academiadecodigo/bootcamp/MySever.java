package org.academiadecodigo.bootcamp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MySever {
    ////////////// PORT 7777 //////////////////////
    ServerSocket serverSocket;
    AcceptedClients acceptedClients = new AcceptedClients();
    ArrayList<Client> clients = new ArrayList<>();
    static int maxPlayers = 3; // MAX 11 MIN 3
    boolean isServerReady = false;
    static int port = 7777;

    public static void main(String[] args) {

        MySever server = new MySever();

        port = Integer.parseInt(args[0]);
        maxPlayers = Integer.parseInt(args[1]);
        
        if(maxPlayers < 3){
            maxPlayers = 3;
        }

        server.createServerSocket(port);

        Thread accept = new Thread(server.acceptedClients);
        accept.start();

        Game game = new Game(server.clients, maxPlayers);
        Thread gameThread = new Thread(game);
        while (!server.isServerReady){
           System.out.println("waiting for the game to start");
        }
        System.out.println("game starting");
        gameThread.start();
    }

    public static int getMaxPlayers() {
        return maxPlayers;
    }

    private class AcceptedClients implements Runnable{

        @Override
        public void run() {
            while(!hasMaxPlayers()){
                try{
                    Socket currentClientSocket = serverSocket.accept();
                    clients.add(new Client(currentClientSocket));

                    Thread clientThread = new Thread(clients.get(clients.size()-1));//ADDING CLIENTS
                    clientThread.start();

                } catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }
            boolean allNamesChosen = false;

            while (!allNamesChosen){

                allNamesChosen = true;

                for (Client client: clients) {

                    if(client.name == null){

                        allNamesChosen = false;
                    }
                }
            }
            isServerReady = true;
        }
    }

    private boolean hasMaxPlayers(){
        if(clients.size() == maxPlayers) return true;

        return false    ;
    }

    private void createServerSocket(int port){
        try{
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}