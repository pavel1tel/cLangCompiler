package org.example.parser;

import org.example.lexer.Token;

public class UnaryOp extends AST {
    private final Token token;
    private final Token op;
    private final AST expr;

    public UnaryOp(Token op, AST expr) {
        this.token = op;
        this.op = op;
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
        return token;
    }

    @Override
    public Token getOp() {
        return op;
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
