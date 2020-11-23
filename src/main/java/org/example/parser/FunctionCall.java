package org.example.parser;

import org.example.lexer.Token;

import java.util.List;

public class FunctionCall extends AST{

    private AST left;
    private List<AST> children;

    public FunctionCall(AST left, List<AST> children) {
        this.left = left;
        this.children = children;
    }

    @Override
    public List<AST> getChildren() {
        return children;
    }

    @Override
    public AST getLeft() {
        return left;
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
}
