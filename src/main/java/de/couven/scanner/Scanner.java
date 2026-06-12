package de.couven.scanner;

import de.couven.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

    private String input;
    private int pos = 0;

    public Scanner(String input) {
        this.input = input;
    }

    private char current() {

        if (pos >= input.length())
            return '\0';

        return input.charAt(pos);
    }

    private void advance() {
        pos++;
    }

    private void skipWhitespace() {

        while (Character.isWhitespace(current())) {
            advance();
        }
    }

    private Token readNumber() {

        StringBuilder sb = new StringBuilder();

        while (Character.isDigit(current())) {
            sb.append(current());
            advance();
        }

        return new Token(TokenType.NUMBER, sb.toString());
    }

    private Token readIdentifier() {

        StringBuilder sb = new StringBuilder();

        while (Character.isLetterOrDigit(current())
                || current() == '_') {

            sb.append(current());
            advance();
        }

        String word = sb.toString();

        switch (word) {

            case "if":
                return new Token(TokenType.IF, word);

            case "else":
                return new Token(TokenType.ELSE, word);

            case "void":
                return new Token(TokenType.VOID, word);

            case "int":
                return new Token(TokenType.INT, word);

            default:
                return new Token(TokenType.IDENTIFIER, word);
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

            switch (current()) {

                case '=':
                    advance();

                    if (current() == '=') {
                        advance();
                        tokens.add(
                                new Token(TokenType.EQUALS, "=="));
                    } else {
                        tokens.add(
                                new Token(TokenType.ASSIGN, "="));
                    }
                    break;

                case '+':
                    tokens.add(new Token(TokenType.PLUS, "+"));
                    advance();
                    break;

                case '-':
                    tokens.add(new Token(TokenType.MINUS, "-"));
                    advance();
                    break;

                case '*':
                    tokens.add(new Token(TokenType.MULTIPLY, "*"));
                    advance();
                    break;

                case '/':
                    tokens.add(new Token(TokenType.DIVIDE, "/"));
                    advance();
                    break;

                case '(':
                    tokens.add(new Token(TokenType.LPAREN, "("));
                    advance();
                    break;

                case ')':
                    tokens.add(new Token(TokenType.RPAREN, ")"));
                    advance();
                    break;

                case '{':
                    tokens.add(new Token(TokenType.LBRACE, "{"));
                    advance();
                    break;

                case '}':
                    tokens.add(new Token(TokenType.RBRACE, "}"));
                    advance();
                    break;

                case ';':
                    tokens.add(
                            new Token(TokenType.SEMICOLON, ";"));
                    advance();
                    break;

                default:
                    throw new RuntimeException(
                            "Ungültiges Zeichen: " + current());
            }
        }

        tokens.add(new Token(TokenType.EOF, ""));

        return tokens;
    }
}
