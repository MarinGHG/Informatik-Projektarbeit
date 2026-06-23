package de.couven.scanner;

import de.couven.token.Token;
import de.couven.token.TokenType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer den Scanner/Tokenizer: korrekte Klassifizierung der Token,
 * Positionsfuehrung und Fehlerbehandlung ueber FEHLER-Token (Nr. 0).
 */
class ScannerTest {

    private List<Token> scan(String quelltext) {
        return new Tokenizer(quelltext).tokenize();
    }

    @Nested
    class GueltigeEingaben {

        @Test
        void zerlegtMethodeInKorrekteTokenfolge() {
            List<Token> tokens = scan("void main(){ x = 2 * 3; }");

            TokenType[] erwartet = {
                    TokenType.TYP, TokenType.NAME, TokenType.KLAMMERAUF, TokenType.KLAMMERZU,
                    TokenType.BLOCKAUF, TokenType.NAME, TokenType.ZUWEISUNGSOP, TokenType.ZAHL,
                    TokenType.PUNKTOP, TokenType.ZAHL, TokenType.SEMIKOLON, TokenType.BLOCKZU
            };
            assertEquals(erwartet.length, tokens.size());
            for (int i = 0; i < erwartet.length; i++) {
                assertEquals(erwartet[i], tokens.get(i).type(), "Token " + i);
            }
        }

        @Test
        void erkenntSchluesselwoerterUndTypen() {
            assertEquals(TokenType.WENN, scan("if").get(0).type());
            assertEquals(TokenType.SONST, scan("else").get(0).type());
            assertEquals(TokenType.SOLANGE, scan("while").get(0).type());
            assertEquals(TokenType.FUER, scan("for").get(0).type());
            assertEquals(TokenType.KLASSE, scan("class").get(0).type());
            assertEquals(TokenType.TYP, scan("int").get(0).type());
            assertEquals(TokenType.TYP, scan("String").get(0).type());
            assertEquals(TokenType.NAME, scan("xyz").get(0).type());
        }

        @Test
        void unterscheidetZuweisungVonVergleich() {
            assertEquals(TokenType.ZUWEISUNGSOP, scan("=").get(0).type());
            assertEquals(TokenType.VERGLOP, scan("==").get(0).type());
            assertEquals(TokenType.VERGLOP, scan("<").get(0).type());
            assertEquals(TokenType.VERGLOP, scan(">").get(0).type());
        }

        @Test
        void fuehrtZeileUndSpalteKorrekt() {
            // "a" in Zeile 2, Spalte 1 nach einem Zeilenumbruch
            Token t = scan("\n a").get(0);
            assertEquals(2, t.position().zeile());
            assertEquals(2, t.position().spalte());
        }
    }

    @Nested
    class FehlerBehandlung {

        @Test
        void ungueltigesZeichenWirdAlsFehlerTokenAufgenommen() {
            List<Token> tokens = scan("#");
            assertEquals(1, tokens.size());
            assertEquals(TokenType.FEHLER, tokens.get(0).type());
            assertEquals(0, tokens.get(0).type().nummer());
            assertEquals("#", tokens.get(0).lexeme());
        }

        @Test
        void mehrereFehlerWerdenAlleGezaehlt() {
            List<Token> tokens = scan("x = #@!;");
            long fehler = tokens.stream()
                    .filter(t -> t.type() == TokenType.FEHLER)
                    .count();
            assertEquals(3, fehler, "drei ungueltige Zeichen erwartet");
        }
    }
}
