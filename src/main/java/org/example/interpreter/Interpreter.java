package org.example.interpreter;

import org.example.lexer.Lexer;
import org.example.lexer.Token;
import org.example.lexer.Type;
import org.example.parser.AST;
import org.example.parser.BinOp;
import org.example.parser.Num;
import org.example.parser.Parser;

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

    public int visit_Num(AST node) {
        return Integer.parseInt(node.getValue());
    }

    public int interpreter() {
        AST tree = parser.parse();
        return visit(tree);
    }
}
