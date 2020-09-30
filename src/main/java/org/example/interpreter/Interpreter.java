package org.example.interpreter;

import lombok.SneakyThrows;
import org.example.lexer.Type;
import org.example.parser.*;

import java.io.FileWriter;
import java.util.logging.Logger;

public class Interpreter {
    private final FileWriter sourceWriter;
    private final Parser parser;
    private final Stack<AST> callStack;
    private String returnRegister = "eax";
    Logger logger = Logger.getLogger("logger");

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
        } else if (node instanceof Char) {
            visit_Char(node);
            return;
        }
        throw new RuntimeException();
    }

    @SneakyThrows
    private void visit_Char(AST node) {
        int value = node.getValue().charAt(0);
        sourceWriter.write("mov " + returnRegister + ", "+ value);
        sourceWriter.write(System.getProperty( "line.separator" ));
    }

    @SneakyThrows
    private void visit_Main(AST node) {
        callStack.push(node);
        //sourceWriter.write(node.getValueType().getToken().getValue().toLowerCase()+ " ");
        if (node.getValueType().getToken().getType().equals(Type.INT)) {
            returnRegister = "eax";
        } else if (node.getValueType().getToken().getType().equals(Type.CHAR)){
            returnRegister = "ah";
        }
        //sourceWriter.write("main()");
        visit(node.getExpr());
    }

    @lombok.SneakyThrows
    public void visit_BinOp(AST node) {
        if (node.getOp().getType().equals(Type.PLUS)) {
            //sourceWriter.write("(");
            visit(node.getLeft());
            //sourceWriter.write("+");
            visit(node.getRight());
            //sourceWriter.write(")");
            return;
        }
        if (node.getOp().getType().equals(Type.MINUS)) {
            //sourceWriter.write("(");
            visit(node.getLeft());
            //sourceWriter.write("-");
            visit(node.getRight());
            //sourceWriter.write(")");
            return;
        }
        if (node.getOp().getType().equals(Type.MUL)) {
            //sourceWriter.write("(");
            visit(node.getLeft());
            //sourceWriter.write("*");
            visit(node.getRight());
            //sourceWriter.write(")");
            return;
        }
        if (node.getOp().getType().equals(Type.DIV)) {
            //sourceWriter.write("(");
            visit(node.getLeft());
            //sourceWriter.write("/");
            visit(node.getRight());
            //sourceWriter.write(")");
            return;
        }
        throw new RuntimeException();
    }

    @SneakyThrows
    public void visit_UnaryOp(AST node) {
        Type op = node.getOp().getType();
        if (op.equals(Type.MINUS)) {
            //sourceWriter.write("(");
            //sourceWriter.write("-");
            visit(node.getExpr());
            //sourceWriter.write(")");
            return;
        }
        if (op.equals(Type.PLUS)) {
            //sourceWriter.write("(");
            //sourceWriter.write("+");
            visit(node.getExpr());
            //sourceWriter.write(")");
            return;
        }
        throw new RuntimeException();
    }

    @SneakyThrows
    public void visit_Compound(AST node) {
        //sourceWriter.write("{");
        for (AST child : node.getChildren()) {
            if(child instanceof ReturnOp){
                visit(child);
          //      sourceWriter.write(";");
            //    sourceWriter.write("}");
                return;
            } else {
                visit(child);
            }
            //sourceWriter.write(";");
        }
        //sourceWriter.write("}");
    }

    @SneakyThrows
    public void visit_Return(AST node) {
        AST func = callStack.peek();
        if (func.getValueType().getToken().getType().equals(Type.INT) && !(
                node.getValueType().getToken().getType().equals(Type.DECIMAL) ||
                        node.getValueType().getToken().getType().equals(Type.HEX) ||
                        node.getValueType().getToken().getType().equals(Type.CHAR)
        )) {
            logger.warning("wrong return type");
            throw new RuntimeException("wrong return type");
        }
        if (func.getValueType().getToken().getType().equals(Type.CHAR) && (
                node.getValueType().getToken().getType().equals(Type.DECIMAL) ||
                        node.getValueType().getToken().getType().equals(Type.HEX)
        )) {
            logger.warning("wrong return type");
            throw new RuntimeException("wrong return type");
        }
        visit(node.getExpr());
        sourceWriter.write("mov b, " + returnRegister);
        callStack.pop();
    }

    @SneakyThrows
    public void visit_Num(AST node) {
        sourceWriter.write("mov " + returnRegister + ", "+ node.getValue());
        sourceWriter.write(System.getProperty( "line.separator" ));
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
