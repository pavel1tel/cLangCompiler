package org.example.parser;

import org.example.lexer.Token;

public class VType extends AST{

    private final Token token;

    public VType(Token token) {
        this.token = token;
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
        return null;
    }
}
