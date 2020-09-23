package org.example;

import org.example.interpreter.Interpreter;
import org.example.lexer.Lexer;
import org.example.parser.Parser;

import java.io.*;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        File file = new File("src/main/java/org/example/test.c");
        Scanner scanner = new Scanner(file);
        StringBuilder text = new StringBuilder();
        while (scanner.hasNextLine()) {
            text.append(scanner.nextLine() + " ");
        }
        scanner.close();
        Lexer lexer = new Lexer(text.toString());
        Parser parser = new Parser(lexer);
        File sourceFile = new File("src/main/java/org/example/source.c");
        FileWriter sourceWriter = new FileWriter(sourceFile);
        Interpreter interpreter = new Interpreter(sourceWriter, parser);
        interpreter.interpreter();

    }
}
