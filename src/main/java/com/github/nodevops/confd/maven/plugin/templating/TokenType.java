package com.github.nodevops.confd.maven.plugin.templating;

public enum TokenType {
    LITERAL,
    IDENTIFIER,
    STRING,
    ENTERING_TEMPLATE_EXPRESSION,
    LEAVING_TEMPLATE_EXPRESSION,
    OPERAND,
    NUMBER,
    ERROR,
    EOF;

    TokenType() {
    }
}
