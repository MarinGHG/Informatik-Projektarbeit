package de.couven.parser;

import de.couven.token.Position;
import de.couven.token.Token;
import de.couven.token.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Rekursiver-Abstiegs-Parser fuer die reduzierte Mindest-Grammatik:
 *
 * <pre>
 * Methode    -> TYP NAME KLAMMERAUF KLAMMERZU Block
 * Block      -> BLOCKAUF Zuweisung BLOCKZU
 * Zuweisung  -> NAME ZUWEISUNGSOP Ausdruck SEMIKOLON
 * Ausdruck   -> Term (STRICHOP Ausdruck)?
 * Term       -> Faktor (PUNKTOP Term)?
 * Faktor     -> NAME | ZAHL | KLAMMERAUF Ausdruck KLAMMERZU
 * </pre>
 *
 * Der Aufrufstack der Methoden bildet das geforderte Stackprinzip ab.
 * Fehler werden intern ueber {@link ParseException} mit Position
 * hochgereicht und in {@link #parse()} in ein {@link ParseResult} umgewandelt.
 */
public class Parser {

    /** Interne Ausnahme zum Hochreichen eines Syntaxfehlers mit Position. */
    private static class ParseException extends RuntimeException {
        final Position position;

        ParseException(String message, Position position) {
            super(message);
            this.position = position;
        }
    }

    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        // Defensive Kopie und EOF-Sentinel sicherstellen, damit peek() nie ins Leere greift.
        this.tokens = new ArrayList<>(tokens);
        if (this.tokens.isEmpty()
                || this.tokens.get(this.tokens.size() - 1).type() != TokenType.EOF) {
            this.tokens.add(new Token(TokenType.EOF, "", endPosition()));
        }
    }

    private Position endPosition() {
        if (tokens.isEmpty()) {
            return new Position(1, 1);
        }
        return tokens.get(tokens.size() - 1).position();
    }

    // --- Infrastruktur ---------------------------------------------------

    private Token peek() {
        return tokens.get(pos);
    }

    private Token advance() {
        Token t = tokens.get(pos);
        if (pos < tokens.size() - 1) {
            pos++;
        }
        return t;
    }

    private boolean match(TokenType type) {
        return peek().type() == type;
    }

    private Token expect(TokenType type) {
        if (match(type)) {
            return advance();
        }
        throw new ParseException(
                "Erwartet: " + type + ", gefunden: " + peek().type(),
                peek().position());
    }

    // --- Einstieg --------------------------------------------------------

    public ParseResult parse() {
        try {
            parseMethode();
            if (!match(TokenType.EOF)) {
                throw new ParseException(
                        "Unerwartetes Token nach Methodenende: " + peek().type(),
                        peek().position());
            }
            return ParseResult.success();
        } catch (ParseException e) {
            return ParseResult.error(e.getMessage(), e.position);
        }
    }

    // --- Aeussere (lineare) Regeln --------------------------------------

    private void parseMethode() {
        expect(TokenType.TYP);
        expect(TokenType.NAME);
        expect(TokenType.KLAMMERAUF);
        expect(TokenType.KLAMMERZU);
        parseBlock();
    }

    private void parseBlock() {
        expect(TokenType.BLOCKAUF);
        parseZuweisung();
        expect(TokenType.BLOCKZU);
    }

    private void parseZuweisung() {
        expect(TokenType.NAME);
        expect(TokenType.ZUWEISUNGSOP);
        parseAusdruck();
        expect(TokenType.SEMIKOLON);
    }

    // --- Innere (rekursive) Regeln --------------------------------------

    private void parseAusdruck() {
        parseTerm();
        if (match(TokenType.STRICHOP)) {
            advance();
            parseAusdruck();
        }
    }

    private void parseTerm() {
        parseFaktor();
        if (match(TokenType.PUNKTOP)) {
            advance();
            parseTerm();
        }
    }

    private void parseFaktor() {
        if (match(TokenType.NAME) || match(TokenType.ZAHL)) {
            advance();
        } else if (match(TokenType.KLAMMERAUF)) {
            advance();
            parseAusdruck();
            expect(TokenType.KLAMMERZU);
        } else {
            throw new ParseException(
                    "Erwartet: NAME, ZAHL oder '(', gefunden: " + peek().type(),
                    peek().position());
        }
    }
}
