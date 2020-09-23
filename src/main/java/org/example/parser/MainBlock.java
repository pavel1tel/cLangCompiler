package org.example.parser;

import org.example.lexer.Token;

public class MainBlock extends AST{

    private final AST expr;

    public MainBlock(AST expr) {
        this.expr = expr;
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
        return null;
    }

    @Override
    public Token getOp() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public AST getExpr() {
        return expr;
    }
}
