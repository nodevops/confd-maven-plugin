package com.github.nodevops.confd.maven.plugin.templating;

public class Token {
    private Type type;
    private String value;
    private int position;

    public Token(Type type, int position) {
        this.type = type;
        this.value = "";
        this.position = position;
    }

    public Token(Type type, String value, int position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    public Type getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public int getPosition() {
        return this.position;
    }

    public String toString() {
        return "Token{type=" + this.type + ", value=\'" + this.value + '\'' + '}';
    }
}

