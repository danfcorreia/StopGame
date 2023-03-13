package org.academiadecodigo.bootcamp;

import java.awt.font.FontRenderContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Game implements Runnable {
    ArrayList<ArrayList<String>> answersArrays = new ArrayList<>();
    ArrayList<Client> clients;
    VAR var;
    int roundsPlayed = 0;
    int roundsToPlay = 3;
    boolean gameStop = false;
    int maxPlayers;

    public Game(ArrayList<Client> clients, int maxPlayers) {
        this.clients = clients;
        this.maxPlayers = maxPlayers;
    }

    private void writeForVar(String message) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(var.socket.getOutputStream(), true);
            writer.println(message);
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void writeForClients(String message) {
        PrintWriter writer = null;
        for (Client client : clients) {
            try {
                writer = new PrintWriter(client.socket.getOutputStream(), true);
                writer.println(message);
                writer.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void writeForAll(String message) {
        writeForClients(message);
        writeForVar(message);
    }

    private VAR chooseVar() {
        int indexVar = (int) Math.floor(Math.random() * clients.size());
        var = new VAR(clients.get(indexVar).socket, clients.get(indexVar).name);

        clients.remove(indexVar);

        return var;
    }

    private ArrayList<ArrayList<String>> answerCheck(ArrayList<ArrayList<String>> array, String letter) {
        for (int i = 0; i < array.size(); i++) {
            for (int j = 0; j < 4; j++) {
                String letterCheck = (array.get(i)).get(j);
                if (!letterCheck.toLowerCase().startsWith(letter.toLowerCase())) {
                    (array.get(i)).remove(j);
                    (array.get(i)).add(j, "NotValid");
                }
                if ((letterCheck.toLowerCase().startsWith("!|!"))) {
                    writeForVar("no answer");
                    (array.get(i)).remove(j);
                    (array.get(i)).add(j, "NotAnswered");
                }
            }

        }
        return array;

    }
    private class CheckAllStoped implements Runnable{
        @Override
        public void run() {
            int counterStops = 0;
            for (Client client : clients) {
                if (client.getIndividualStop()) {
                    counterStops++;
                }
                if (counterStops == clients.size()){
                    setAllisStop(true);
                    gameStop = true;
                    break;
                }
            }
        }
    }

    private void startRound() {

        answersArrays.clear();
        writeForAll("YOU'VE PLAYED " + roundsPlayed + " GAMES!");

        Letter letter = Letter.randomLetter(); // ASSIGN A RANDOM LETTER FOR THIS ROUND

        if(roundsPlayed != 0 ){
            writeForClients("WAIT FOR THIS ROUND LETTER......" );
        }

        var.init();

        for (Client client : clients) { //SENDS LETTER TO EVERY CLIENT
            client.gameWaiting = false;
            client.setCurrentLetter(letter.toString());
        }

        writeForClients("\n\n<<<<<<<<<<THE CHOSEN LETTER IS " + letter + ">>>>>>>>>>\nANSWER NOW! GO! GO!");
        System.out.println("starting round!");

        for (int i = 0; i < clients.size(); i++) {
            Thread individualStops = new Thread(new CheckAllStoped());
            individualStops.start();
            System.out.println("Stopping after this");
            if(gameStop){
                break;
            }
            if (flag(clients.get(i))) {
                setAllisStop(true);
                gameStop = true;
                break;
            }
            if (i == clients.size() - 1) {
                i = -1;
            }
        }

        for (Client client : clients) {
            answersArrays.add(client.getAnswers());
        }

        answerCheck(answersArrays, letter.toString());

        writeForClients("GOOD JOB WAIT FOR NEXT ROUND\n");

        for (int i = 0; i < clients.size(); i++) {
            writeForVar(((Integer) answersArrays.size()).toString());
            writeForVar("  PLAYER         COUNTRY         BRAND          CAPITAL          FOOD");
            writeForVar(clients.get(i).name + " answered: " + answersArrays.get(i).get(0) + " / "
                    + answersArrays.get(i).get(1) + " / "
                    + answersArrays.get(i).get(2) + " / "
                    + answersArrays.get(i).get(3));
            var.getScore(clients.get(i));
        }
        resetPlayers();
        gameStop = false;
        roundsPlayed++;
        System.out.println("rounds played " + roundsPlayed);
    }

    private void setAllisStop(boolean bool) {

        for (Client clients : clients) { //STOPS WITH STOP FOR ALL
            clients.setGameWaiting(bool);
            clients.setStopForAll(bool);
        }
    }

    private boolean flag(Client client) {
        return client.stopForAll;
    }

    private void showScore() {
        ArrayList<Client> sortedClients = new ArrayList<>();

        ArrayList<Client> clientsCopy = clients;
        Client currentClient;
        if (maxPlayers == 3) {
            do {
                currentClient = clientsCopy.get(0);
                for (Client client : clientsCopy) {
                    if (currentClient.getScore() < client.getScore()) {
                        currentClient = client;
                    }
                }
                sortedClients.add(currentClient);
                clientsCopy.remove(currentClient);
            } while (clientsCopy.size() != 0);
            writeForClients("1ST : " + sortedClients.get(0).getName() + " -- " + sortedClients.get(0).getScore() + "\n" + "\n" +
                    "2ND : " + sortedClients.get(1).getName() + " -- " + sortedClients.get(1).getScore() + "\n");
            writeForVar("1ST : " + sortedClients.get(0).getName() + " -- " + sortedClients.get(0).getScore() + "\n" + "\n" +
                    "2ND : " + sortedClients.get(1).getName() + " -- " + sortedClients.get(1).getScore() + "\n");
            System.out.println("1ST : " + sortedClients.get(0).getName() + " -- " + sortedClients.get(0).getScore() + "\n" + "\n" +
                    "2ND : " + sortedClients.get(1).getName() + " -- " + sortedClients.get(1).getScore() + "\n");
        } else {
            do {
                currentClient = clientsCopy.get(0);
                for (Client client : clientsCopy) {
                    if (currentClient.getScore() < client.getScore()) {
                        currentClient = client;
                    }
                }
                sortedClients.add(currentClient);
                clientsCopy.remove(currentClient);
            } while (clientsCopy.size() != 0);
            writeForClients("1ST : " + sortedClients.get(0).getName() + " -- " + sortedClients.get(0).getScore() + "\n" + "\n" +
                    "2ND : " + sortedClients.get(1).getName() + " -- " + sortedClients.get(1).getScore() + "\n");
            writeForVar("1ST : " + sortedClients.get(0).getName() + " -- " + sortedClients.get(0).getScore() + "\n" + "\n" +
                    "2ND : " + sortedClients.get(1).getName() + " -- " + sortedClients.get(1).getScore() + "\n");
            System.out.println("1ST : " + sortedClients.get(0).getName() + " -- " + sortedClients.get(0).getScore() + "\n" + "\n" +
                    "2ND : " + sortedClients.get(1).getName() + " -- " + sortedClients.get(1).getScore() + "\n");
        }
    }


    private void resetPlayers(){
        for (int i = 0; i < clients.size(); i++) {
            setAllisStop(false);
            clients.get(i).resetStrings();
            clients.get(i).setIndividualStop(false);
        }

    }
    @Override
    public void run() {
        var = chooseVar(); // CHOSING THE VAR
        writeForAll("                                 >>>> WELCOME TO <<<<<\n\n"+
                    " .d88888b  d888888P  .88888.   888888ba      .88888.   .d888888  8888ba.88ba   88888888b\n"+
                    " 88.     '    88    d8'   `8b  88    `8b    d8'   `88 d8'    88  88  `8b  `8b  88\n"+
                    " `Y88888b.    88    88     88 a88aaaa8P'    88        88aaaaa88a 88   88   88 a88aaaa\n"+
                    "       `8b    88    88     88  88           88   YP88 88     88  88   88   88  88\n"+
                    " d8'   .8P    88    Y8.   .8P  88           Y8.   .88 88     88  88   88   88  88\n"+
                    "  Y88888P     dP     `8888P'   dP            `88888'  88     88  dP   dP   dP  88888888P\n\n"+
                "                <<<<<<<<<<WE WILL GIVE YOU A LETTER EACH ROUND>>>>>>>>>>\n" +
                ">>>>>>>>>>YOU SHOULD WRITE A WORD ABOUT EACH THEME STARTING WITH THAT LETTER<<<<<<<<<<\n" +
                "                    <<<<<<<<<<GET READY TO START THE GAME>>>>>>>>>>\n");

        writeForVar("\n>>>>>>>>>>>>>>>>>>>>YOU ARE THE CHOSEN PLAYER TO BE THE REFEREE<<<<<<<<<<<<<<<<<<<<" +
                    "\n   >>>>>>>>>>>>>>>>>>>>START THE GAME WHEN EVERYONE IS READY<<<<<<<<<<<<<<<<<<<<\n\n"); // TELL THE VAR HE IS THE VAR

        while (roundsPlayed < roundsToPlay) {
            startRound();
            System.out.println("rounds played " + roundsPlayed);
        }
        System.out.println("showing scored");
        showScore();

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(maxPlayers == 3){
            writeForClients("Game over");
        } else {
            writeForClients("Game over");
        }
    }
}