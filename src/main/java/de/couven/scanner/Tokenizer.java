package de.couven.scanner;

import de.couven.token.Position;
import de.couven.token.Token;
import de.couven.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private final Scanner scanner;

    public Tokenizer(String input) {
        this.scanner = new Scanner(input);
    }

    public List<Token> tokenize() {

        List<Token> tokens = new ArrayList<>();

        while (scanner.current() != '\0') {

            scanner.skipWhitespace();

            if (scanner.current() == '\0') {
                break;
            }

            Position start = scanner.here();
            char c = scanner.current();

            // ZAHL
            if (Character.isDigit(c)) {
                tokens.add(readNumber(start));
            }

            // NAME / KEYWORD
            else if (Character.isLetter(c)) {
                tokens.add(readIdentifier(start));
            }

            else {

                switch (c) {

                    case '(':
                        tokens.add(new Token(TokenType.KLAMMERAUF, "(", start));
                        scanner.advance();
                        break;

                    case ')':
                        tokens.add(new Token(TokenType.KLAMMERZU, ")", start));
                        scanner.advance();
                        break;

                    case '{':
                        tokens.add(new Token(TokenType.BLOCKAUF, "{", start));
                        scanner.advance();
                        break;

                    case '}':
                        tokens.add(new Token(TokenType.BLOCKZU, "}", start));
                        scanner.advance();
                        break;

                    case ';':
                        tokens.add(new Token(TokenType.SEMIKOLON, ";", start));
                        scanner.advance();
                        break;

                    case '=':
                        scanner.advance();

                        if (scanner.current() == '=') {
                            scanner.advance();
                            tokens.add(new Token(
                                    TokenType.VERGLOP,
                                    "==",
                                    start));
                        } else {
                            tokens.add(new Token(
                                    TokenType.ZUWEISUNGSOP,
                                    "=",
                                    start));
                        }

                        break;

                    case '+':
                    case '-':
                        tokens.add(
                                new Token(
                                        TokenType.STRICHOP,
                                        String.valueOf(c),
                                        start));

                        scanner.advance();
                        break;

                    case '*':
                    case '/':
                    case '%':
                        tokens.add(
                                new Token(
                                        TokenType.PUNKTOP,
                                        String.valueOf(c),
                                        start));

                        scanner.advance();
                        break;

                    case '<':
                    case '>':
                        tokens.add(
                                new Token(
                                        TokenType.VERGLOP,
                                        String.valueOf(c),
                                        start));

                        scanner.advance();
                        break;

                    case '!':
                        scanner.advance();
                        if (scanner.current() == '=') {
                            scanner.advance();
                            tokens.add(new Token(TokenType.VERGLOP, "!=", start));
                        } else {
                            tokens.add(new Token(TokenType.FEHLER, "!", start));
                        }
                        break;

                    default:
                        // Ungueltiges Zeichen: als FEHLER-Token (Nr. 0) aufnehmen
                        // statt abzubrechen, damit mehrere Fehler gezaehlt werden
                        // koennen (siehe Token-Tabelle / GUI-Fehlerzaehler).
                        tokens.add(new Token(
                                TokenType.FEHLER,
                                String.valueOf(c),
                                start));
                        scanner.advance();
                        break;
                }
            }
        }

        return tokens;
    }

    private Token readNumber(Position start) {

        StringBuilder sb = new StringBuilder();

        while (Character.isDigit(scanner.current())) {
            sb.append(scanner.current());
            scanner.advance();
        }

        return new Token(
                TokenType.ZAHL,
                sb.toString(),
                start);
    }

    private Token readIdentifier(Position start) {

        StringBuilder sb = new StringBuilder();

        while (Character.isLetterOrDigit(scanner.current())) {
            sb.append(scanner.current());
            scanner.advance();
        }

        String text = sb.toString();

        return switch (text) {
            case "class" -> new Token(TokenType.KLASSE, text, start);
            case "if" -> new Token(TokenType.WENN, text, start);
            case "else" -> new Token(TokenType.SONST, text, start);
            case "while" -> new Token(TokenType.SOLANGE, text, start);
            case "for" -> new Token(TokenType.FUER, text, start);
            case "int", "String", "void", "boolean" -> new Token(TokenType.TYP, text, start);
            default -> new Token(TokenType.NAME, text, start);
        };
    }
}

