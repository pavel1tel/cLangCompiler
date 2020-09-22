package org.example;

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
            Interpreter interpreter = new Interpreter(lexer);
            System.out.println(interpreter.expr());
        }
    }
}
