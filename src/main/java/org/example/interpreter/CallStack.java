package org.example.interpreter;

import org.example.parser.AST;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class CallStack<T> implements Stack<T>{
    Deque<T> stack = new LinkedList<>();

    public void push(T func) {
        stack.addLast(func);
    }

    public T pop() {
        return stack.pollLast();
    }

    public T peek() {
        return stack.peekLast();
    }

    @Override
    public int size() {
        return stack.size();
    }
}
