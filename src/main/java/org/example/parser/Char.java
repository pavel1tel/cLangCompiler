package org.example.parser;

import org.example.lexer.Token;

public class Char extends AST{
    private final Token token;
    private final String value;
    private final AST valueType;

    public Char(Token token, AST valueType) {
        this.token = token;
        this.value = token.getValue();
        this.valueType = valueType;
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

    @Override
    public AST getValueType() {
        return valueType;
    }
}
