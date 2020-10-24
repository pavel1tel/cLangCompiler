package org.example.parser;

import org.example.lexer.Token;
import org.example.lexer.Type;

public class Assing extends AST {

    private final AST right;
    private final Token op;
    private final AST left;
    private final VType type;

    public Assing(AST right, Token op, AST left, VType type) {
        this.right = right;
        this.op = op;
        this.left = left;
        this.type = type;
    }

    @Override
    public AST getValueType() {
        return type;
    }

    @Override
    public AST getLeft() {
        return left;
    }

    @Override
    public AST getRight() {
        return right;
    }

    @Override
    public Token getToken() {
        return null;
    }

    @Override
    public Token getOp() {
        return op;
    }

    @Override
    public String getValue() {
        return null;
    }
}
