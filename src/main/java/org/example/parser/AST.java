package org.example.parser;

import org.example.lexer.Token;

public abstract class AST {
    private Token left;
    private Token right;
    private Token token;
    private Token op;
    private Token value;
    private AST expr;

    public abstract AST getLeft();
    public abstract AST getRight();
    public abstract Token getToken();
    public abstract Token getOp();
    public abstract String getValue();

    public AST getExpr() {
        return null;
    }
}
