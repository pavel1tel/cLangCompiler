package org.example.interpreter;

import org.example.parser.AST;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class CallStack implements Stack<AST>{
    Deque<AST> stack = new LinkedList<>();

    public void push(AST func) {
        stack.addLast(func);
    }

    public AST pop() {
        return stack.pollLast();
    }

    public AST peek() {
        return stack.peekLast();
    }
}
