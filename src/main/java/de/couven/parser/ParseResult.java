package de.couven.parser;

import de.couven.token.Position;

/**
 * Ergebnis eines Parse-Vorgangs nach aussen (GUI-Schnittstelle).
 * Bei Erfolg ist {@code ok} true und {@code errorPos} null,
 * im Fehlerfall traegt {@code errorPos} die Position des Fehlers.
 */
public record ParseResult(boolean ok, String message, Position errorPos) {

    public static ParseResult success() {
        return new ParseResult(true, "Syntaktisch korrekt.", null);
    }

    public static ParseResult error(String message, Position errorPos) {
        return new ParseResult(false, message, errorPos);
    }
}
