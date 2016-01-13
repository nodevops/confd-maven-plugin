package com.github.nodevops.confd.maven.plugin.templating;

import java.io.*;
import java.util.Map;


public class Parser {
    private static final char NEW_LINE = '\n';

    private File templateFile;
    private String encoding;
    private int lineNumber;

    public Parser(File templateFile, String encoding) {
        this.templateFile = templateFile;
        this.encoding = encoding;
    }

    public String parse(Map<String, String> env) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(templateFile), encoding));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                sb.append(evaluateSource(line, env)).append(NEW_LINE);
            }
            return sb.toString();
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    private String evaluateSource(String source, Map<String, String> env) throws IOException {
        LexicalAnalyzer sc = new LexicalAnalyzer(source);
        StringBuilder sb = new StringBuilder();

        for (Token l = sc.nextToken(); l.getType() != Type.EOF; l = sc.nextToken()) {
            switch (l.getType()) {
                case ENTERING_TEMPLATE_EXPRESSION:
                    evaluateTemplateExpression(sb, sc, env);
                    break;
                default:
                    sb.append(l.getValue());
            }
        }

        return sb.toString();
    }

    private void evaluateTemplateExpression(StringBuilder sb, LexicalAnalyzer sc, Map<String, String> env) throws IOException {
        expectIdentifer("getv", sc);
        Token t = expectString(sc);
        String keyName = t.getValue();
        if (!env.containsKey(keyName)) {
            throw new IOException(getErrorMessage("key " + keyName + " does not exist", t));
        } else {
            sb.append((String) env.get(keyName));
            expectClosingBraces(sc);
        }
    }

    private void expectClosingBraces(LexicalAnalyzer sc) throws IOException {
        Token t = sc.nextToken();

        while (t.getType() != Type.LEAVING_TEMPLATE_EXPRESSION) {
            switch (t.getType()) {
                case EOF:
                    throw new IOException(getErrorMessage("unexpected unclosed action in command", t));
                default:
                    throw new IOException(getErrorMessage("expected }}  but found " + t.getValue(), t));
            }
        }

    }

    private Token expectString(LexicalAnalyzer sc) throws IOException {
        Token t = sc.nextToken();

        while (t.getType() != Type.STRING) {
            switch (t.getType()) {
                case LEAVING_TEMPLATE_EXPRESSION:
                case EOF:
                    throw new IOException(getErrorMessage("wrong number of args for getv: want 1 got 0", t));
                default:
                    throw new IOException(getErrorMessage("expected string but found " + t.getValue(), t));
            }
        }

        return t;
    }

    private void expectIdentifer(String id, LexicalAnalyzer sc) throws IOException {
        Token t = sc.nextToken();

        while (t.getType() != Type.IDENTIFIER) {
            switch (t.getType()) {
                case EOF:
                    throw new IOException(getErrorMessage("unexpected unclosed action in command", t));
                case LEAVING_TEMPLATE_EXPRESSION:
                    throw new IOException(getErrorMessage("missing value for command", t));
                default:
                    throw new IOException(getErrorMessage("expected \'getv\' but found " + t.getValue(), t));
            }
        }

        if (!t.getValue().equals(id)) {
            throw new IOException(getErrorMessage("function " + t.getValue() + " not defined", t));
        }
    }


    private String getErrorMessage(String message, Token t) {
        StringBuilder sb = new StringBuilder("File : ");
        sb.append(templateFile)
            .append(" [").append(lineNumber).append(',').append(t.getPosition()).append("] : ")
            .append(message);

        return sb.toString();

    }
}
