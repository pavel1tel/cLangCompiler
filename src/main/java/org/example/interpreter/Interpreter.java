package org.example.interpreter;

import javafx.util.Pair;
import lombok.SneakyThrows;
import org.example.lexer.Type;
import org.example.parser.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class Interpreter {
    private final FileWriter sourceWriter;
    private final Parser parser;
    private final Stack<AST> callStack;
    private String returnRegister = "eax";
    private boolean afterMain = false;
    int condIndex = 3;
    Logger logger = Logger.getLogger("logger");
    int index = -1;
    int functionIndex = -1;
    List<HashMap<String, Integer>> varStack = new ArrayList<>();
    List<HashMap<String, Integer>> argStack = new ArrayList<>();
    Integer stackIndex = -4;
    HashMap<String, Pair<Boolean, Integer>> functions = new HashMap<>();

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
        else if (node instanceof CondExp) {
            visit_condExp(node);
            return;
        }
        else if (node instanceof Function) {
            visit_function(node);
            return;
        }
        if (node instanceof Program) {
            visit_Program(node);
            return;
        }
        if (node instanceof FunctionCall) {
            visit_FunctionCall(node);
            return;
        }
        if (node instanceof FuncDeclaration) {
            visit_FuncDeclaration(node);
            return;
        }
        throw new RuntimeException(node.getClass().toString());
    }

    private void visit_FuncDeclaration(AST node) {
        functions.put(node.getValueType().getValue(), new Pair<>(false, node.getChildren().size()));
    }

    @SneakyThrows
    private void visit_FunctionCall(AST node){
        for(AST expr : node.getChildren()){
            visit(expr);
            sourceWriter.write("push eax");
            sourceWriter.write(System.getProperty("line.separator"));
        }
        sourceWriter.write("call " + node.getLeft().getValue());
        sourceWriter.write(System.getProperty("line.separator"));
        if (!functions.containsKey(node.getLeft().getValue())){
            logger.warning("function " + node.getLeft().getValue() + " is not defined or defined after main");
            return;
        }
        if (functions.get(node.getLeft().getValue()).getValue() != node.getChildren().size()){
            logger.warning("expected arguments : " + functions.get(node.getLeft().getValue()).getValue() +"\n given: " + node.getChildren().size());
            throw new RuntimeException("expected arguments : " + functions.get(node.getLeft().getValue()).getValue() +"\n given: " + node.getChildren().size());
        }
    }

    @SneakyThrows
    private void visit_function(AST node) {
        functionIndex++;
        argStack.add(new HashMap<>());
        stackIndex = -4;
        sourceWriter.write(System.getProperty("line.separator"));
        if (!functions.containsKey(node.getValueType().getValue())){
            if (afterMain) {
                logger.warning("implicit declaration of function: " + node.getValueType().getValue());
            } else {
                functions.put(node.getValueType().getValue(), new Pair<>(true, node.getChildren().size()));
            }
        } else if(!functions.get(node.getValueType().getValue()).getKey() && functions.get(node.getValueType().getValue()).getValue() == node.getChildren().size()){
            functions.replace(node.getValueType().getValue(), new Pair<>(true, node.getChildren().size()));
        } else if(functions.get(node.getValueType().getValue()).getValue() != node.getChildren().size()){
            if (afterMain) {
                logger.warning("implicit declaration of function: " + node.getValueType().getValue());
            } else {
                functions.replace(node.getValueType().getValue(), new Pair<>(true, node.getChildren().size()));
            }
        }
        sourceWriter.write(node.getValueType().getValue() + ":");
        sourceWriter.write(System.getProperty("line.separator"));
        sourceWriter.write("push ebp");
        sourceWriter.write(System.getProperty("line.separator"));
        sourceWriter.write("mov ebp, esp");
        sourceWriter.write(System.getProperty("line.separator"));
        callStack.push(node);
        int i = node.getChildren().size();
        for (AST args : node.getChildren()) {
            sourceWriter.write("mov eax, [ebp + " + (i*4 + 4) + "]");
            sourceWriter.write(System.getProperty("line.separator"));
            sourceWriter.write("push eax");
            sourceWriter.write(System.getProperty("line.separator"));
            String varName = args.getRight().getValue();
            HashMap<String, Integer> argStackMap = argStack.get(functionIndex);
            argStackMap.put(varName, stackIndex);
            stackIndex = stackIndex - 4;
            i--;
        }
        visit(node.getExpr());
        sourceWriter.write("ret " + node.getChildren().size() * 4);
        sourceWriter.write(System.getProperty("line.separator"));
        argStack.remove(functionIndex);
        functionIndex--;
    }

    @SneakyThrows
    private void visit_Program(AST node) {
        sourceWriter.write("jmp mainn");
        sourceWriter.write(System.getProperty("line.separator"));
        node.getChildren().forEach(this::visit);
        sourceWriter.write(System.getProperty("line.separator"));
        sourceWriter.write("_end:");
        sourceWriter.write(System.getProperty("line.separator"));
    }

    @SneakyThrows
    private void visit_condExp(AST node) {
        visit(node.getExpr());
        sourceWriter.write("cmp eax, 0");
        sourceWriter.write(System.getProperty("line.separator"));
        sourceWriter.write("je   _e" + condIndex);
        sourceWriter.write(System.getProperty("line.separator"));
        visit(node.getLeft());
        sourceWriter.write("jmp  _post_conditional" + condIndex);
        sourceWriter.write(System.getProperty("line.separator"));
        sourceWriter.write("_e" + condIndex + ":");
        sourceWriter.write(System.getProperty("line.separator"));
        visit(node.getRight());
        sourceWriter.write("_post_conditional" + condIndex + ":");
        condIndex++;
        sourceWriter.write(System.getProperty("line.separator"));
    }

    @SneakyThrows
    private void visit_Char(AST node) {
        int value = node.getValue().charAt(0);
        sourceWriter.write("mov " + returnRegister + ", " + value);
        sourceWriter.write(System.getProperty("line.separator"));
    }

    @SneakyThrows
    private void visit_Main(AST node) {
        stackIndex = -4;
        sourceWriter.write(System.getProperty("line.separator"));
        sourceWriter.write("mainn:");
        sourceWriter.write(System.getProperty("line.separator"));
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
        sourceWriter.write("mov b, " + returnRegister);
        sourceWriter.write(System.getProperty("line.separator"));
        sourceWriter.write("jmp _end");
        sourceWriter.write(System.getProperty("line.separator"));
        afterMain = true;
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
        index++;
        varStack.add(new HashMap<>());
        for (AST child : node.getChildren()) {
            if (child instanceof ReturnOp) {
//                if (callStack.size() == 0){
//                    varStack.remove(index);
//                    index--;
//                    return;
//                }
                visit(child);
                varStack.remove(index);
                index--;
                return;
            } else {
//                if (callStack.size() == 0){
//                    varStack.remove(index);
//                    index--;
//                    return;
//                }
                visit(child);
            }
        }
        varStack.remove(index);
        index--;
    }

    @SneakyThrows
    public void visit_Assign(AST node) {
        String varName = node.getLeft().getValue();
        HashMap<String, Integer> varStackMap = varStack.get(index);
        if (varStackMap.containsKey(varName) && (node.getRight() == null)) {
            logger.warning(varName + " is already declared");
            throw new RuntimeException(varName + " is already declared");
        }
        if (node.getRight() == null) {
            varStackMap.put(varName, stackIndex);
            //stackIndex = stackIndex - 4;
        } else if (!varStackMap.containsKey(varName)) {
            visit(node.getRight());
            sourceWriter.write("push eax");
            sourceWriter.write(System.getProperty("line.separator"));
            varStackMap.put(varName, stackIndex);
            stackIndex = stackIndex - 4;
        }
    }

    @SneakyThrows
    public void visit_reAssign(AST node) {
        String varName = node.getLeft().getValue();
        HashMap<String, Integer> varStackMap = getMapFromMapList(varName);
        visit(node.getRight());
        sourceWriter.write("push eax");
        sourceWriter.write(System.getProperty("line.separator"));
        varStackMap.replace(varName, stackIndex);
        stackIndex = stackIndex - 4;
    }

    @SneakyThrows
    public void visit_Var(AST node) {
        int varOffset;
        varOffset = getVarFromMapList(node.getValue());
        sourceWriter.write("mov eax" + ", [" + varOffset + " + ebp]");
        sourceWriter.write(System.getProperty("line.separator"));
    }

    public int getVarFromMapList(String varName) {
        int varOffset = 100;
        for (int i = index; i >= 0; i--){
            try{
                HashMap<String, Integer> varStackMap = varStack.get(i);
                varOffset = varStackMap.get(varName);
                i = 0;
            } catch (Exception ignored) {
            }
        }
        for (int i = functionIndex; i >= 0; i--){
            try{
                HashMap<String, Integer> argStackMap = argStack.get(i);
                varOffset = argStackMap.get(varName);
                i = 0;
            } catch (Exception ignored) {
            }
        }
        if (varOffset == 100) {
            logger.warning(varName + " is not declared");
            throw new RuntimeException(varName + " is not declared");
        }
        return varOffset;
    }

    public HashMap<String, Integer> getMapFromMapList(String varName) {
        HashMap<String, Integer> result = null;
        for (int i = index; i >= 0; i--){
            HashMap<String, Integer> varStackMap = varStack.get(i);
            if(varStackMap.containsKey(varName)){
                result = varStackMap;
                i = 0;
            }
        }
        for (int i = functionIndex; i >= 0; i--){
            try{
                HashMap<String, Integer> argStackMap = argStack.get(i);
                if(argStackMap.containsKey(varName)){
                    result = argStackMap;
                    i = 0;
                }
            } catch (Exception ignored) {
            }
        }
        if(result == null) {
            logger.warning(varName + " is not declared");
            throw new RuntimeException(varName + " is not declared");
        }
        return result;
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
