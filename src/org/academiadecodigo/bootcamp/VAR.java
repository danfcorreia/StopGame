package org.academiadecodigo.bootcamp;

import org.academiadecodigo.bootcamp.scanners.integer.IntegerInputScanner;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class VAR {
    Socket socket;
    String name = null;
    Prompt prompt;

    VAR(Socket socket, String name) {

        this.socket = socket;
        this.name = name;
        prompt = createPrompt();
    }

    private Prompt createPrompt() {
        try {
            prompt = new Prompt(socket.getInputStream(), new PrintStream(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return prompt;
    }

    public void getScore(Client client) {
        String[] scores = {"0 answers right ",
                "1 answer right",
                "2 answers right",
                "3 answers right",
                "4 answers right"
        };
        MenuInputScanner assignPoints = new MenuInputScanner(scores);
        assignPoints.setMessage("YOU WILL NOW DECIDE THE SCORE:");
        int number = prompt.getUserInput(assignPoints);
        switch (number) {
            case 1:
                break;
            case 2:
                client.setScore(client.getScore() + 1);
                break;
            case 3:
                client.setScore(client.getScore() + 2);
                break;
            case 4:
                client.setScore(client.getScore() + 3);
                break;
            case 5:
                client.setScore(client.getScore() + 5);
                break;
        }
    }

    public void init() {
        String[] startMenu = {"Start game"
        };
        MenuInputScanner startSheet = new MenuInputScanner(startMenu);
        startSheet.setMessage("THE GAME IS ABOUT TO START - PRESS 1 TO START!");
        int option = prompt.getUserInput(startSheet);
        switch (option) {
            case 1:
                break;
        }
    }
}