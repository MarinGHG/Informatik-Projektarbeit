package de.couven.token;

public enum TokenType {

    KLASSE, // klasse -> class
    TYP,          // typ -> int, string, void, etc.
    NAME,         // name (IDENTIFIER) -> main
    KLAMMERAUF,   // klammerauf -> (
    KLAMMERZU,    // klammerzu -> )
    BLOCKAUF,     // blockauf -> {
    BLOCKZU,      // blockzu -> }

    ZUWEISUNGSOP,  // zuweisungsoperator -> =
    SEMIKOLON,    // semikolon -> ;
    STRICHOP, // strichoperator -> + or -
    PUNKTOP,  // punktoperator -> * or /
    VERGLEICHOP, // vergleichsoperator -> ==, !=, <, >, <=, >=

    WENN,
    SONST,
    SOLANGE,
    FÜR,

    ZAHL,         // zahl
}

