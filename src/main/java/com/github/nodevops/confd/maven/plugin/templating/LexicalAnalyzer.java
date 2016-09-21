package com.github.nodevops.confd.maven.plugin.templating;

import java.io.IOException;
import java.nio.CharBuffer;

public class LexicalAnalyzer {
    private CharBuffer buffer;
    private boolean isInTemplateExpression;

    public LexicalAnalyzer(String source) {
        buffer = CharBuffer.wrap(source);
    }

    public Token nextToken() throws IOException {
        if (!buffer.hasRemaining()) {
            return new Token(TokenType.EOF, "", buffer.position());
        }
        if (!isInTemplateExpression) {
            return nextTokenNotInTemplateExpression();
        } else {
            return nextTokenInTemplateExpression();
        }
    }

    private Token nextTokenNotInTemplateExpression() {
        StringBuilder sb = new StringBuilder();
        int position = buffer.position();
        char c = buffer.get();
        if (c == '{') {
            if (buffer.hasRemaining()) {
                char c2 = buffer.get();
                if (c2 == '{') {
                    isInTemplateExpression = true;
                    return new Token(TokenType.ENTERING_TEMPLATE_EXPRESSION, position);
                } else {
                    sb.append(c).append(c2);
                    return nextLiteralToken(sb);
                }
            } else {
                return new Token(TokenType.LITERAL, sb.toString(), position);
            }
        } else {
            sb.append(c);
            return nextLiteralToken(sb);
        }
    }

    private Token nextTokenInTemplateExpression() throws IOException {
        int position = buffer.position();

        while (buffer.hasRemaining()) {

            char c = buffer.get();

            if (Character.isWhitespace(c)) {
                skipWhiteSpaces();
            } else if (Character.isLetter(c)) {
                backup();
                return nextIdentifierToken();
            } else if (c == '"') {
                return nextStringToken();
            } else if (c == '}') {
                if (buffer.hasRemaining()) {
                    c = buffer.get();
                    if (c == '}') {
                        isInTemplateExpression = false;
                        return new Token(TokenType.LEAVING_TEMPLATE_EXPRESSION, position);
                    } else {
                        backup();
                        return new Token(TokenType.OPERAND, "}", position);
                    }
                } else {
                    return new Token(TokenType.OPERAND, "}", position);
                }
            } else if (Character.isDigit(c)) {
                backup();
                return nextNumberToken();
            } else {
                return new Token(TokenType.OPERAND, String.valueOf(c), position);
            }
            position = buffer.position();
        }
        return new Token(TokenType.EOF, position);
    }

    private Token nextNumberToken() {
        StringBuilder sb = new StringBuilder();
        int position = buffer.position();

        while (buffer.hasRemaining()) {
            char c = buffer.get();
            if (!Character.isDigit(c)) {
                backup();
                break;
            }

            sb.append(c);
        }

        return new Token(TokenType.NUMBER, sb.toString(), position);
    }

    private Token nextStringToken() throws IOException {
        StringBuilder sb = new StringBuilder();
        int position = buffer.position();

        while (buffer.hasRemaining()) {
            char c = buffer.get();
            if (c == '"') {
                return new Token(TokenType.STRING, sb.toString(), position);
            }
            sb.append(c);
        }
        return new Token(TokenType.ERROR, "unterminated quoted string", position);
    }

    private Token nextIdentifierToken() {
        StringBuilder sb = new StringBuilder();
        int position = buffer.position();

        while (buffer.hasRemaining()) {
            char c = buffer.get();
            if (!Character.isLetterOrDigit(c) && c != 95) {
                backup();
                break;
            }

            sb.append(c);
        }

        return new Token(TokenType.IDENTIFIER, sb.toString(), position);
    }

    private void skipWhiteSpaces() {
        buffer.position();

        while (buffer.hasRemaining()) {
            char c = buffer.get();
            if (!Character.isWhitespace(c)) {
                backup();
                break;
            }
        }
    }

    private void backup() {
        buffer.position(buffer.position() - 1);
    }

    private Token nextLiteralToken(StringBuilder sb) {
        int position = buffer.position();

        while (buffer.hasRemaining()) {
            char c = buffer.get();
            if (c == '{') {
                backup();
                break;
            }

            sb.append(c);
        }

        return new Token(TokenType.LITERAL, sb.toString(), position);
    }
}
