package org.example.interpreter;

import org.example.lexer.Type;
import org.example.parser.*;

public class Interpreter {
    private final Parser parser;

    public Interpreter(Parser parser) {
        this.parser = parser;
    }

    public int visit(AST node) {
        if (node instanceof BinOp) {
            return visit_BinOp(node);
        } else if (node instanceof Num) {
            return visit_Num(node);
        } else if (node instanceof UnaryOp) {
            return visit_UnaryOp(node);
        }
        throw new RuntimeException();
    }

    public int visit_BinOp(AST node) {
        if (node.getOp().getType().equals(Type.PLUS)) {
            return visit(node.getLeft()) + visit(node.getRight());
        }
        if (node.getOp().getType().equals(Type.MINUS)) {
            return visit(node.getLeft()) - visit(node.getRight());
        }
        if (node.getOp().getType().equals(Type.MUL)) {
            return visit(node.getLeft()) * visit(node.getRight());
        }
        if (node.getOp().getType().equals(Type.DIV)) {
            return visit(node.getLeft()) / visit(node.getRight());
        }
        throw new RuntimeException();
    }

    public int visit_UnaryOp(AST node) {
        Type op = node.getOp().getType();
        if (op.equals(Type.MINUS)){
            return -visit(node.getExpr());
        }
        if (op.equals(Type.PLUS)){
            return +visit(node.getExpr());
        }
        throw new RuntimeException();
    }

    public int visit_Num(AST node) {
        return Integer.parseInt(node.getValue());
    }

    public int interpreter() {
        AST tree = parser.parse();
        //TreePrinter.printAST(tree);
        return visit(tree);
    }
}
