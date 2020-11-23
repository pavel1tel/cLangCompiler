package org.example.parser;

import org.example.lexer.Token;

import java.util.List;

public class Function extends AST{
    private final AST expr;
    private final AST valueType;
    private final List<AST> children;

    public Function(AST expr, AST valueType, List<AST> children) {
        this.expr = expr;
        this.valueType = valueType;
        this.children = children;
    }

    @Override
    public List<AST> getChildren() {
        return children;
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

    @Override
    public AST getValueType() {
        return valueType;
    }
}
