package de.couven.parser;

import de.couven.scanner.Tokenizer;
import de.couven.token.Position;
import de.couven.token.Token;
import de.couven.token.TokenType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    /** Scannt den Quelltext und parst ihn anschliessend (Scanner + Parser im Verbund). */
    private ParseResult parse(String quelltext) {
        List<Token> tokens = new Tokenizer(quelltext).tokenize();
        return new Parser(tokens).parse();
    }

    /** Bequemes Erstellen eines Tokens fuer die tokenbasierten Tests. */
    private Token tok(TokenType type) {
        return new Token(type, type.name(), new Position(1, 1));
    }

    // === Tokenbasierte Tests (unabhaengig vom Scanner) ===================

    @Nested
    class TokenbasiertReduzierteGrammatik {

        /** Tokenliste 2-12-6-7-3-12-8-11-9-11-5-4 : void main(){ x = 2 * 3; } */
        @Test
        void akzeptiertGueltigeMethode() {
            List<Token> tokens = new ArrayList<>(List.of(
                    tok(TokenType.TYP),          // 2
                    tok(TokenType.NAME),         // 12
                    tok(TokenType.KLAMMERAUF),   // 6
                    tok(TokenType.KLAMMERZU),    // 7
                    tok(TokenType.BLOCKAUF),     // 3
                    tok(TokenType.NAME),         // 12
                    tok(TokenType.ZUWEISUNGSOP), // 8
                    tok(TokenType.ZAHL),         // 11
                    tok(TokenType.PUNKTOP),      // 9
                    tok(TokenType.ZAHL),         // 11
                    tok(TokenType.SEMIKOLON),    // 5
                    tok(TokenType.BLOCKZU)));    // 4

            ParseResult result = new Parser(tokens).parse();

            assertTrue(result.ok(), result.message());
            assertNull(result.errorPos());
        }

        /** Fehlendes Semikolon: muss Fehler mit Position melden. */
        @Test
        void meldetFehlendesSemikolonMitPosition() {
            List<Token> tokens = new ArrayList<>(List.of(
                    tok(TokenType.TYP),
                    tok(TokenType.NAME),
                    tok(TokenType.KLAMMERAUF),
                    tok(TokenType.KLAMMERZU),
                    tok(TokenType.BLOCKAUF),
                    tok(TokenType.NAME),
                    tok(TokenType.ZUWEISUNGSOP),
                    tok(TokenType.ZAHL),
                    // SEMIKOLON fehlt
                    tok(TokenType.BLOCKZU)));

            ParseResult result = new Parser(tokens).parse();

            assertFalse(result.ok());
            assertNotNull(result.errorPos());
        }

        /** Auch ohne EOF-Sentinel in der Eingabe muss korrekt geparst werden. */
        @Test
        void ergaenztFehlendenEofSentinel() {
            List<Token> tokens = new ArrayList<>(List.of(
                    tok(TokenType.TYP),
                    tok(TokenType.NAME),
                    tok(TokenType.KLAMMERAUF),
                    tok(TokenType.KLAMMERZU),
                    tok(TokenType.BLOCKAUF),
                    tok(TokenType.NAME),
                    tok(TokenType.ZUWEISUNGSOP),
                    tok(TokenType.ZAHL),
                    tok(TokenType.SEMIKOLON),
                    tok(TokenType.BLOCKZU)));

            ParseResult result = new Parser(tokens).parse();

            assertTrue(result.ok(), result.message());
        }
    }

    // === Quelltextbasierte Tests (Scanner + Parser) =====================

    @Nested
    class GueltigeProgramme {

        @Test
        void einfacheZuweisung() {
            assertOk("void main() { x = 1; }");
        }

        @Test
        void zuweisungMitAusdruck() {
            assertOk("int f() { y = 2 * 3 + 4; }");
        }

        @Test
        void punktVorStrichDurchKlammern() {
            assertOk("void main() { x = (2 + 3) * 4; }");
        }

        @Test
        void moduloOperator() {
            assertOk("void main() { x = 7 % 2; }");
        }

        @Test
        void mehrereAnweisungenNacheinander() {
            assertOk("void main() { x = 1; y = 2; z = x + y; }");
        }

        @Test
        void leererBlock() {
            assertOk("void main() { }");
        }

        @Test
        void geschachtelterBlock() {
            assertOk("void main() { { x = 1; } }");
        }

        @Test
        void wennAnweisung() {
            assertOk("void main() { if (a == b) x = 1; }");
        }

        @Test
        void wennSonstAnweisung() {
            assertOk("void main() { if (a == b) x = 1; else x = 2; }");
        }

        @Test
        void solangeAnweisung() {
            assertOk("void main() { while (i < n) i = i + 1; }");
        }

        @Test
        void solangeMitBlock() {
            assertOk("void main() { while (i < n) { i = i + 1; sum = sum + i; } }");
        }

        @Test
        void verschachtelteKontrollstrukturen() {
            assertOk("void main() { while (a < b) { if (a == 0) x = 1; else x = 2; } }");
        }

        @Test
        void einfacheKlasse() {
            assertOk("class MyClass { int myMethod() { x = 1; } }");
        }

        @Test
        void klasseMitMehrerenMethoden() {
            assertOk("class Foo { void a() { } int b() { x = 1; } }");
        }

        @Test
        void klasseOhneMethoden() {
            assertOk("class Empty { }");
        }

        @Test
        void fuerSchleife() {
            assertOk("void main() { for (i = 0; i < n; i = i + 1) x = 1; }");
        }

        @Test
        void fuerSchleifeMitBlock() {
            assertOk("void main() { for (i = 0; i < n; i = i + 1) { x = i + 1; } }");
        }

        @Test
        void ungleichOperator() {
            assertOk("void main() { if (a != b) x = 1; }");
        }

        @Test
        void booleanTyp() {
            assertOk("boolean check() { }");
        }

        @Test
        void mehrzeiligerQuelltext() {
            assertOk("""
                    void main() {
                        x = 1;
                        if (x == 1) {
                            y = x + 2;
                        } else {
                            y = 0;
                        }
                    }
                    """);
        }
    }

    @Nested
    class FehlerhafteProgramme {

        @Test
        void fehlenderTyp() {
            assertFehlerBei("main() { x = 1; }", 1, 1);
        }

        @Test
        void fehlendesSemikolon() {
            // "void main() { x = 1 }" -> '}' steht in Spalte 21, dort wird ';' erwartet
            ParseResult r = parse("void main() { x = 1 }");
            assertFalse(r.ok());
            assertEquals(new Position(1, 21), r.errorPos());
        }

        @Test
        void leereRechteSeite() {
            assertFalse(parse("void main() { x = ; }").ok());
        }

        @Test
        void fehlendeOeffnendeKlammerBeimWenn() {
            assertFalse(parse("void main() { if a == b) x = 1; }").ok());
        }

        @Test
        void fehlendeSchliessendeKlammerImAusdruck() {
            assertFalse(parse("void main() { x = (1 + 2; }").ok());
        }

        @Test
        void vergleichOhneOperator() {
            assertFalse(parse("void main() { if (a b) x = 1; }").ok());
        }

        @Test
        void nichtGeschlossenerBlock() {
            assertFalse(parse("void main() { x = 1;").ok());
        }

        @Test
        void zusaetzlicheTokensNachMethode() {
            ParseResult r = parse("void main() { x = 1; } y = 2;");
            assertFalse(r.ok());
            assertNotNull(r.errorPos());
        }

        @Test
        void zuweisungOhneName() {
            assertFalse(parse("void main() { = 1; }").ok());
        }

        @Test
        void fehlerPositionWirdGemeldet() {
            ParseResult r = parse("void main() { x = 1 y = 2; }");
            assertFalse(r.ok());
            assertNotNull(r.errorPos());
            assertFalse(r.message().isBlank());
        }
    }

    // === Hilfsmethoden ==================================================

    private void assertOk(String quelltext) {
        ParseResult result = parse(quelltext);
        assertTrue(result.ok(), () -> "Sollte gueltig sein, war: " + result.message()
                + " @" + result.errorPos());
        assertNull(result.errorPos());
    }

    private void assertFehlerBei(String quelltext, int zeile, int spalte) {
        ParseResult result = parse(quelltext);
        assertFalse(result.ok(), "Sollte ungueltig sein");
        assertNotNull(result.errorPos());
        assertEquals(new Position(zeile, spalte), result.errorPos());
    }
}
