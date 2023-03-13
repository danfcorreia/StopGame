package org.academiadecodigo.bootcamp;

import java.util.Random;

public enum Letter {
    E, R, T, U, I, O, P, A, S, D, F, G, H, J, L, C, V, B, N, M;


private int code;
private String symbol;

    public static Letter randomLetter() {
        int pick = new Random().nextInt(Letter.values().length);
        return Letter.values()[pick];
    }
}
