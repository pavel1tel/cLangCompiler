package org.example.parser;

import org.example.lexer.Token;

public class Var extends AST {

    private final Token token;
    private final String value;

    public Var(Token token) {
        this.token = token;
        this.value = token.getValue();
    }

    @Override
    public AST getLeft() {
        return null;
    }

    @Override
    public AST getRight() {
        return null;
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public Token getOp() {
        return null;
    }

    @Override
    public String getValue() {
        return value;
    }
}
