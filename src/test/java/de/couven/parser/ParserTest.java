package de.couven.parser;

import de.couven.token.Position;
import de.couven.token.Token;
import de.couven.token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private Token tok(TokenType type) {
        return new Token(type, type.name(), new Position(1, 1));
    }

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
}
