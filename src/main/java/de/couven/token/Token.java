package de.couven.token;

/** Ein vom Scanner erzeugtes Token: Typ, Lexem und Quelltextposition. */
public record Token(TokenType type, String lexeme, Position position) {

    @Override
    public String toString() {
        return type + "(" + type.nummer() + "):" + lexeme;
    }
}
