package org.example.interpreter;

import lombok.SneakyThrows;
import org.example.lexer.Type;
import org.example.parser.*;

import java.io.FileWriter;

public class Interpreter {
    private final FileWriter sourceWriter;
    private final Parser parser;
    private final CallStack callStack;

    public Interpreter(FileWriter sourceWriter, Parser parser) {
        this.sourceWriter = sourceWriter;
        this.parser = parser;
        this.callStack = new CallStack();
    }

    public void visit(AST node) {
        if (node instanceof BinOp) {
            visit_BinOp(node);
            return;
        } else if (node instanceof Num) {
            visit_Num(node);
            return;
        } else if (node instanceof UnaryOp) {
            visit_UnaryOp(node);
            return;
        } else if (node instanceof Compound) {
            visit_Compound(node);
            return;
        } else if (node instanceof ReturnOp) {
            visit_Return(node);
            return;
        } else if (node instanceof NoOp) {
            visit_NoOp(node);
            return;
        } else if (node instanceof MainBlock) {
            visit_Main(node);
            return;
        }
        throw new RuntimeException();
    }

    @SneakyThrows
    private void visit_Main(AST node) {
        callStack.push(node);
        sourceWriter.write(node.getValueType().getToken().getValue().toLowerCase()+ " ");
        sourceWriter.write("main()");
        visit(node.getExpr());
    }

    @lombok.SneakyThrows
    public void visit_BinOp(AST node) {
        if (node.getOp().getType().equals(Type.PLUS)) {
            sourceWriter.write("(");
            visit(node.getLeft());
            sourceWriter.write("+");
            visit(node.getRight());
            sourceWriter.write(")");
            return;
        }
        if (node.getOp().getType().equals(Type.MINUS)) {
            sourceWriter.write("(");
            visit(node.getLeft());
            sourceWriter.write("-");
            visit(node.getRight());
            sourceWriter.write(")");
            return;
        }
        if (node.getOp().getType().equals(Type.MUL)) {
            sourceWriter.write("(");
            visit(node.getLeft());
            sourceWriter.write("*");
            visit(node.getRight());
            sourceWriter.write(")");
            return;
        }
        if (node.getOp().getType().equals(Type.DIV)) {
            sourceWriter.write("(");
            visit(node.getLeft());
            sourceWriter.write("/");
            visit(node.getRight());
            sourceWriter.write(")");
            return;
        }
        throw new RuntimeException();
    }

    @SneakyThrows
    public void visit_UnaryOp(AST node) {
        Type op = node.getOp().getType();
        if (op.equals(Type.MINUS)) {
            sourceWriter.write("(");
            sourceWriter.write("-");
            visit(node.getExpr());
            sourceWriter.write(")");
            return;
        }
        if (op.equals(Type.PLUS)) {
            sourceWriter.write("(");
            sourceWriter.write("+");
            visit(node.getExpr());
            sourceWriter.write(")");
            return;
        }
        throw new RuntimeException();
    }

    @SneakyThrows
    public void visit_Compound(AST node) {
        sourceWriter.write("{");
        for (AST child : node.getChildren()) {
            if(child instanceof ReturnOp){
                visit(child);
                return;
            } else {
                visit(child);
            }
            sourceWriter.write(";");
        }
        sourceWriter.write("}");
    }

    @SneakyThrows
    public void visit_Return(AST node) {
        AST func = callStack.peek();
        if (func.getValueType().getToken().getType().equals(Type.INT) && !(
                node.getValueType().getToken().getType().equals(Type.DECIMAL) ||
                        node.getValueType().getToken().getType().equals(Type.HEX)
        )) {
            throw new RuntimeException("wrong return type");
        }
        if (func.getValueType().getToken().getType().equals(Type.CHAR) && (
                node.getValueType().getToken().getType().equals(Type.DECIMAL) ||
                        node.getValueType().getToken().getType().equals(Type.HEX)
        )) {
            throw new RuntimeException("wrong return type");
        }
            sourceWriter.write("return ");
        visit(node.getExpr());
        callStack.pop();
    }

    @SneakyThrows
    public void visit_Num(AST node) {
        sourceWriter.write(node.getValue());
    }

    public void visit_NoOp(AST node) {
    }

    @SneakyThrows
    public void interpreter() {
        AST tree = parser.parse();
        //TreePrinter.printAST(tree);
        visit(tree);
        sourceWriter.close();
    }
}
