package de.couven.scanner;

import de.couven.token.Position;


public class Scanner {
    private final String input;
    private int pos = 0;
    private int line = 1;
    private int col = 1;

    public Scanner(String input) {
        this.input = input;
    }

    public char current() {

        if (pos >= input.length())
            return '\0';

        return input.charAt(pos);
    }

    public void advance() {
        if (current() == '\n') {
            line++;
            col = 1;
        } else {
            col++;
        }
        pos++;
    }

    public Position here() {
        return new Position(line, col);
    }

    public void skipWhitespace() {

        while (Character.isWhitespace(current())) {
            advance();
        }
    }

}
