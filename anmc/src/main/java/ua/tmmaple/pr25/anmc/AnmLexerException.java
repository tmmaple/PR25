package ua.tmmaple.pr25.anmc;

public class AnmLexerException extends RuntimeException {
    public AnmLexerException(String message, int line, int column) {
        super(message + " at line " + line + " column " + column);
    }
}
