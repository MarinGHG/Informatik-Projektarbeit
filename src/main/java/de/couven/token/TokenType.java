package de.couven.token;

/**
 * Alle Token-Typen der Grammatik mit ihrer Nummer (0-16) gemaess Token-Tabelle.
 * EOF ist ein interner Sentinel und gehoert nicht zur Tabelle (Nummer -1).
 */
public enum TokenType {

    FEHLER(0),
    KLASSE(1),
    TYP(2),
    BLOCKAUF(3),
    BLOCKZU(4),
    SEMIKOLON(5),
    KLAMMERAUF(6),
    KLAMMERZU(7),
    ZUWEISUNGSOP(8),
    PUNKTOP(9),
    STRICHOP(10),
    ZAHL(11),
    NAME(12),
    VERGLOP(13),
    WENN(14),
    SONST(15),
    SOLANGE(16),

    EOF(-1);

    private final int nummer;

    TokenType(int nummer) {
        this.nummer = nummer;
    }

    public int nummer() {
        return nummer;
    }
}
