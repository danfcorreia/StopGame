package org.academiadecodigo.bootcamp;

import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Client implements Runnable{
    String currentLetter;
    Socket socket;
    String name = null;
    Prompt prompt;
    volatile boolean stopForAll = false;
    ArrayList<String> roundAnswers = new ArrayList<>();
    int score = 0;
    boolean gameWaiting = true;
    boolean gameOnline = true;
    String def = "!|!notAnswered";
    String country = "!|!notAnswered";
    String brand = "!|!notAnswered";
    String capital = "!|!notAnswered";
    String food = "!|!notAnswered";
    boolean individualStop = false;
    boolean gameHolder = true;

    Client (Socket socket){
        this.socket=socket;
        // METHOD GET NAME
        prompt = createPrompt();
    }

    public void setCurrentLetter(String currentLetter) {
        this.currentLetter = currentLetter;
    }

    private Prompt createPrompt(){
        try{
            prompt = new Prompt(socket.getInputStream(),new PrintStream(socket.getOutputStream()));
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        return prompt;
    }
    public void resetStrings(){
        country = def;
        brand = def;
        capital = def;
        food = def;
        roundAnswers.clear();
    }

    public void setIndividualStop(boolean individualStop) {
        this.individualStop = individualStop;
    }

    public void chooseAnswers(String roundLetter){
        stopForAll = false;
        StringInputScanner scanner = new StringInputScanner();

        while (!individualStop && !stopForAll) {
            String[] optionsArray = {"Country", "Brand", "Capital", "Food", "STOP"};
            MenuInputScanner menuInputScanner = new MenuInputScanner(optionsArray);
            menuInputScanner.setMessage("WHICH THEME DO YOU WANT TO ANSWER NOW? \n");
            int answer = prompt.getUserInput(menuInputScanner);

            switch (answer){
                case 1:
                    if(stopForAll){
                        break;
                    }
                    scanner.setMessage("COUNTRY \n");
                    country = prompt.getUserInput(scanner);
                    break;
                case 2:
                    if(stopForAll){
                        break;
                    }
                    scanner.setMessage("BRAND \n");
                    brand = prompt.getUserInput(scanner);
                    break;
                case 3:
                    if(stopForAll){
                        break;
                    }
                    scanner.setMessage("CAPITAL \n");
                    capital = prompt.getUserInput(scanner);
                    break;
                case 4:
                    if(stopForAll){
                        break;
                    }
                    scanner.setMessage("FOOD \n");
                    food = prompt.getUserInput(scanner);
                    break;
                case 5:
                   if(food.equals(def) || capital.equals(def) || brand.equals(def) || country.equals(def)){
                        individualStop = true;
                        setGameWaiting(true);
                    } else {
                        setGameWaiting(true);
                        individualStop = true;
                        stopForAll = true;
                        System.out.println("closing round hopefully");
                        break;
                   }
            }
        }
    }

    public ArrayList<String> getAnswers(){
        roundAnswers.add(country);
        roundAnswers.add(brand);
        roundAnswers.add(capital);
        roundAnswers.add(food);
        return roundAnswers;
    }

    public boolean getIndividualStop() {
        return individualStop;
    }

    public ArrayList<String> getRoundAnswers() {
        return roundAnswers;
    }

    public boolean isStopForAll() {
        return stopForAll;
    }

    public void setStopForAll(boolean stopForAll) {
        this.stopForAll = stopForAll;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setGameWaiting(boolean gameWaiting) {
        this.gameWaiting = gameWaiting;
    }

    public void setGameOnline(boolean gameOnline) {
        this.gameOnline = gameOnline;
    }

    public String getName() {
        return name;
    }

    @Override
    public void run() {
        StringInputScanner askName = new StringInputScanner();
        askName.setMessage("Please insert your nickname!\n");
        askName.setError("You must insert your nickname first!\n");

        name = prompt.getUserInput(askName);

        while(gameOnline) {

            System.out.println("inside game online");
            while (gameWaiting) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
               //System.out.println("inside game waiting");
            }
            chooseAnswers(currentLetter);

            System.out.println("Need score");

        }


       // System.out.println("client ended run");
    }
}
