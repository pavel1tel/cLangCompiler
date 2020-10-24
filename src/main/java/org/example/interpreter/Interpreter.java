package org.example.interpreter;

import lombok.SneakyThrows;
import org.example.lexer.Type;
import org.example.parser.*;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.logging.Logger;

public class Interpreter {
    private final FileWriter sourceWriter;
    private final Parser parser;
    private final Stack<AST> callStack;
    private String returnRegister = "eax";
    private boolean first = true;
    Logger logger = Logger.getLogger("logger");
    HashMap<String, Integer> varStack = new HashMap();
    Integer stackIndex = -4;

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
        } else if (node instanceof Assing) {
            visit_Assign(node);
            return;
        } else if (node instanceof Var) {
            visit_Var(node);
            return;
        } else if (node instanceof reAssign) {
            visit_reAssign(node);
            return;
        }
        throw new RuntimeException();
    }

    @SneakyThrows
    private void visit_Char(AST node) {
        int value = node.getValue().charAt(0);
        sourceWriter.write("mov " + returnRegister + ", " + value);
        sourceWriter.write(System.getProperty("line.separator"));
    }

    @SneakyThrows
    private void visit_Main(AST node) {
        sourceWriter.write("push ebp");
        sourceWriter.write(System.getProperty("line.separator"));
        sourceWriter.write("mov ebp, esp");
        sourceWriter.write(System.getProperty("line.separator"));
        callStack.push(node);
        //sourceWriter.write(node.getValueType().getToken().getValue().toLowerCase()+ " ");
        if (node.getValueType().getToken().getType().equals(Type.INT)) {
            returnRegister = "eax";
        } else if (node.getValueType().getToken().getType().equals(Type.CHAR)) {
            returnRegister = "al";
        }
        //sourceWriter.write("main()");
        visit(node.getExpr());
    }

    @lombok.SneakyThrows
    public void visit_BinOp(AST node) {
        if (node.getOp().getType().equals(Type.MINUS)) {
            visit(node.getLeft());
            sourceWriter.write("push eax");
            sourceWriter.write(System.getProperty("line.separator"));
            visit(node.getRight());
            sourceWriter.write("pop ecx");
            sourceWriter.write(System.getProperty("line.separator"));
            sourceWriter.write("sub ecx, eax");
            sourceWriter.write(System.getProperty("line.separator"));
            sourceWriter.write("mov " + returnRegister + ", ecx");
            sourceWriter.write(System.getProperty("line.separator"));
            return;
        }
        if (node.getOp().getType().equals(Type.PLUS)) {
            visit(node.getLeft());
            sourceWriter.write("push eax");
            sourceWriter.write(System.getProperty("line.separator"));
            visit(node.getRight());
            sourceWriter.write("pop ecx");
            sourceWriter.write(System.getProperty("line.separator"));
            sourceWriter.write("add ecx, eax");
            sourceWriter.write(System.getProperty("line.separator"));
            sourceWriter.write("mov eax, ecx");
            sourceWriter.write(System.getProperty("line.separator"));
            return;
        }
        if (node.getOp().getType().equals(Type.OR)) {
            visit(node.getLeft());
            sourceWriter.write("push eax");
            sourceWriter.write(System.getProperty("line.separator"));
            visit(node.getRight());
            sourceWriter.write("pop ecx");
            sourceWriter.write(System.getProperty("line.separator"));
            sourceWriter.write("or ecx, eax");
            sourceWriter.write(System.getProperty("line.separator"));
            sourceWriter.write("mov eax, ecx");
            sourceWriter.write(System.getProperty("line.separator"));
            return;
        }
        throw new RuntimeException();
    }

    @SneakyThrows
    public void visit_UnaryOp(AST node) {
        Type op = node.getOp().getType();
        if (op.equals(Type.TILDE)) {
            visit(node.getExpr());
            sourceWriter.write("not eax");
            sourceWriter.write(System.getProperty("line.separator"));
            return;
        }
        throw new RuntimeException();
    }

    @SneakyThrows
    public void visit_Compound(AST node) {
        for (AST child : node.getChildren()) {
            if (child instanceof ReturnOp) {
                visit(child);
                return;
            } else {
                visit(child);
            }
        }
    }

    @SneakyThrows
    public void visit_Assign(AST node) {
        String varName = node.getLeft().getValue();
        if (varStack.containsKey(varName) && (node.getRight() == null)) {
            logger.warning(varName + " is already declared");
            throw new RuntimeException(varName + " is already declared");
        }
        if (node.getRight() == null) {
            varStack.put(varName, stackIndex);
            //stackIndex = stackIndex - 4;
        } else if (!varStack.containsKey(varName)) {
            visit(node.getRight());
            sourceWriter.write("push eax");
            sourceWriter.write(System.getProperty("line.separator"));
            varStack.put(varName, stackIndex);
            stackIndex = stackIndex - 4;
        }
    }

    @SneakyThrows
    public void visit_reAssign(AST node) {
        String varName = node.getLeft().getValue();
        if (!varStack.containsKey(varName)) {
            logger.warning(varName + " is not declared");
            throw new RuntimeException(varName + " is not declared");
        }
        visit(node.getRight());
        sourceWriter.write("push eax");
        sourceWriter.write(System.getProperty("line.separator"));
        varStack.replace(varName, stackIndex);
        stackIndex = stackIndex - 4;
    }

    @SneakyThrows
    public void visit_Var(AST node) {
        int varOffset;
        try {
             varOffset = varStack.get(node.getValue());
        } catch (Exception ex) {
            logger.warning(node.getToken().getValue() + " is not declared");
            throw new RuntimeException(node.getToken().getValue() + " is not declared");
        }
        sourceWriter.write("mov eax" + ", [" + varOffset + " + ebp]");
        sourceWriter.write(System.getProperty("line.separator"));
    }

    @SneakyThrows
    public void visit_Return(AST node) {
        AST func = callStack.peek();
        if (func.getValueType().getToken().getType().equals(Type.INT) && !(
                node.getValueType().getToken().getType().equals(Type.DECIMAL) ||
                        node.getValueType().getToken().getType().equals(Type.HEX) ||
                        node.getValueType().getToken().getType().equals(Type.CHAR) ||
                        node.getValueType().getToken().getType().equals(Type.TILDE) ||
                        node.getValueType().getToken().getType().equals(Type.ID)
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
        sourceWriter.write("mov esp, ebp");
        sourceWriter.write(System.getProperty("line.separator"));
        sourceWriter.write("pop ebp");
        sourceWriter.write(System.getProperty("line.separator"));
        sourceWriter.write("mov b, " + returnRegister);
        callStack.pop();
    }

    @SneakyThrows
    public void visit_Num(AST node) {
        sourceWriter.write("mov eax" + ", " + node.getValue());
        sourceWriter.write(System.getProperty("line.separator"));

    }

    public void visit_NoOp(AST node) {
    }

    @SneakyThrows
    public void interpreter() {
        AST tree = parser.parse();
        visit(tree);
        sourceWriter.close();
    }
}
