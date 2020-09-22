package org.example.parser;

import org.example.lexer.Token;

public class BinOp extends AST {
    private final AST left;
    private final AST right;
    private final Token token;
    private final Token op;


    public BinOp(AST left, AST right, Token op) {
        this.left = left;
        this.right = right;
        this.op = op;
        this.token = op;
    }

    public AST getLeft() {
        return left;
    }

    public AST getRight() {
        return right;
    }

    public Token getToken() {
        return token;
    }

    public Token getOp() {
        return op;
    }

    @Override
    public String getValue() {
        return null;
    }
}
