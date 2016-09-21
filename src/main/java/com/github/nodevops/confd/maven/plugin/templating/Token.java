package com.github.nodevops.confd.maven.plugin.templating;

public class Token {
    private TokenType tokenType;
    private String value;
    private int position;

    public Token(TokenType tokenType, int position) {
        this.tokenType = tokenType;
        this.value = "";
        this.position = position;
    }

    public Token(TokenType tokenType, String value, int position) {
        this.tokenType = tokenType;
        this.value = value;
        this.position = position;
    }

    public TokenType getTokenType() {
        return this.tokenType;
    }

    public String getValue() {
        return this.value;
    }

    public int getPosition() {
        return this.position;
    }

    public String toString() {
        return "Token{tokenType=" + this.tokenType + ", value=\'" + this.value + '\'' + '}';
    }
}
