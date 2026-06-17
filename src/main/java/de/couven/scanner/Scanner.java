package de.couven.scanner;

import de.couven.token.Position;
import de.couven.token.Token;
import de.couven.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

    private String input;
    private int pos = 0;
    private int line = 1;
    private int col = 1;

    public Scanner(String input) {
        this.input = input;
    }

    private char current() {

        if (pos >= input.length())
            return '\0';

        return input.charAt(pos);
    }

    private void advance() {
        if (current() == '\n') {
            line++;
            col = 1;
        } else {
            col++;
        }
        pos++;
    }

    private Position here() {
        return new Position(line, col);
    }

    private void skipWhitespace() {

        while (Character.isWhitespace(current())) {
            advance();
        }
    }

    private Token readNumber() {

        Position start = here();
        StringBuilder sb = new StringBuilder();

        while (Character.isDigit(current())) {
            sb.append(current());
            advance();
        }

        return new Token(TokenType.ZAHL, sb.toString(), start);
    }

    private Token readIdentifier() {

        Position start = here();
        StringBuilder sb = new StringBuilder();

        while (Character.isLetterOrDigit(current())
                || current() == '_') {

            sb.append(current());
            advance();
        }

        String word = sb.toString();

        switch (word) {

            case "if":
                return new Token(TokenType.WENN, word, start);

            case "else":
                return new Token(TokenType.SONST, word, start);

            case "while":
                return new Token(TokenType.SOLANGE, word, start);

            case "class":
                return new Token(TokenType.KLASSE, word, start);

            case "void":
            case "int":
                return new Token(TokenType.TYP, word, start);

            default:
                return new Token(TokenType.NAME, word, start);
        }
    }

    public List<Token> scan() {

        List<Token> tokens = new ArrayList<>();

        while (current() != '\0') {

            skipWhitespace();

            if (current() == '\0')
                break;

            if (Character.isDigit(current())) {
                tokens.add(readNumber());
                continue;
            }

            if (Character.isLetter(current())) {
                tokens.add(readIdentifier());
                continue;
            }

            Position start = here();

            switch (current()) {

                case '=':
                    advance();

                    if (current() == '=') {
                        advance();
                        tokens.add(
                                new Token(TokenType.VERGLOP, "==", start));
                    } else {
                        tokens.add(
                                new Token(TokenType.ZUWEISUNGSOP, "=", start));
                    }
                    break;

                case '+':
                    tokens.add(new Token(TokenType.STRICHOP, "+", start));
                    advance();
                    break;

                case '-':
                    tokens.add(new Token(TokenType.STRICHOP, "-", start));
                    advance();
                    break;

                case '*':
                    tokens.add(new Token(TokenType.PUNKTOP, "*", start));
                    advance();
                    break;

                case '/':
                    tokens.add(new Token(TokenType.PUNKTOP, "/", start));
                    advance();
                    break;

                case '(':
                    tokens.add(new Token(TokenType.KLAMMERAUF, "(", start));
                    advance();
                    break;

                case ')':
                    tokens.add(new Token(TokenType.KLAMMERZU, ")", start));
                    advance();
                    break;

                case '{':
                    tokens.add(new Token(TokenType.BLOCKAUF, "{", start));
                    advance();
                    break;

                case '}':
                    tokens.add(new Token(TokenType.BLOCKZU, "}", start));
                    advance();
                    break;

                case ';':
                    tokens.add(
                            new Token(TokenType.SEMIKOLON, ";", start));
                    advance();
                    break;

                default:
                    tokens.add(new Token(
                            TokenType.FEHLER, String.valueOf(current()), start));
                    advance();
                    break;
            }
        }

        tokens.add(new Token(TokenType.EOF, "", here()));

        return tokens;
    }
}
