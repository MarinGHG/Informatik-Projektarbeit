package de.couven.scanner;

import de.couven.token.TokenType;

public class Token {
    private TokenType type;
    private String value;

    public TokenType getType(){
        return type;
    }

    public String getValue(){
        return value;
    }

    public String toString(){
        return type + ":" + value;
    }

}
