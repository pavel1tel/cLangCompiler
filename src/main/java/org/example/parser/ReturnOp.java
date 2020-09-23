package org.example.parser;

import org.example.lexer.Token;

public class ReturnOp extends AST {

    private final AST expr;

    public ReturnOp(AST expr) {
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
