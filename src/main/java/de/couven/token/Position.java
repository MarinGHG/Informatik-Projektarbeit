package de.couven.token;

/** Quelltextposition eines Tokens (1-basiert). */
public record Position(int zeile, int spalte) {

    @Override
    public String toString() {
        return "Zeile " + zeile + ", Spalte " + spalte;
    }
}
