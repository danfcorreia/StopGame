package org.academiadecodigo.bootcamp;

import java.util.Random;

public enum Letter {
    // a(1, "a"), b(2, "b"), c(3, "c"), d(4, "d"), e(5, "e"), f(6, "f"), g(7, "g"), h(8, "h"), i(9, "i"), j(10, "j"), l(11, "l"), m(12, "m"), n(13, "n"), o(14, "o"), p(15, "p"), q(16, "q"), r(17, "r"), s(18, "s"), t(19, "t"), u(20, "u"), v(21, "v"), z(22, "z");
    E, R, T, U, I, O, P, A, S, D, F, G, H, J, L, C, V, B, N, M;


private int code;
private String symbol;

    public static Letter randomLetter() {
        int pick = new Random().nextInt(Letter.values().length);
        return Letter.values()[pick];
    }
}
