package org.example;

import org.example.interpreter.Interpreter;
import org.example.lexer.Lexer;
import org.example.parser.Parser;

import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(">>>");
            String input = scanner.nextLine();
            Lexer lexer = new Lexer(input);
            Parser parser = new Parser(lexer);
            Interpreter interpreter = new Interpreter(parser);
            System.out.println(interpreter.interpreter());
        }
    }
}
