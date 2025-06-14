package ua.tmmaple.pr25.anmc;

import java.util.ArrayList;

/**
 * Лексер ANM.
 * @author uwuhasmile
 */
public final class AnmLexer {
    private static final String[] directives = {
        null, "import", "source", "define",
    };
    private static final String[] keywords = {
        null, "script", "delete", "stop", "pause", "hidePause", "interrupt", "sleep", "return", "jump", "true", "false",
    };

    private char[] input;

    public final ArrayList<AnmToken> tokens;
    private int index;
    private int line;
    private int column;

    private int startIndex;
    private int startLine;
    private int startColumn;

    public final ArrayList<String> errors;

    public AnmLexer() {
        tokens = new ArrayList<>();
        errors = new ArrayList<>();
    }

    /**
     * Токенізує вхідний текст.
     * @author uwuhasmile
     */
    public void tokenize(String input) {
        tokens.clear();
        errors.clear();

        this.input = input.toCharArray();
        this.index = 0;
        this.line = 0;
        this.column = 0;

        while (!isAtEnd())
            try {
                scanToken();
            } catch (AnmLexerException e) {
                errors.add(e.getMessage());
            }
        tokens.add(AnmToken.eof(line, column));
    }

    /**
     * Сканує токен.
     * @throws AnmLexerException якщо неправильний символ або сталась інша лексична помилка
     * @author uwuhasmile
     */
    private void scanToken() {
        startIndex = index;
        startLine = line;
        startColumn = column;
        char c = advance();
        switch (c) {
            case '#': directive(startLine, startColumn); break;
            case '(': tokens.add(AnmToken.leftParen(startLine, startColumn)); break;
            case ')': tokens.add(AnmToken.rightParen(startLine, startColumn)); break;
            case '[': tokens.add(AnmToken.leftBracket(startLine, startColumn)); break;
            case ']': tokens.add(AnmToken.rightBracket(startLine, startColumn)); break;
            case '{': tokens.add(AnmToken.startBlock(startLine, startColumn)); break;
            case '}': tokens.add(AnmToken.endBlock(startLine, startColumn)); break;
            case ':': tokens.add(AnmToken.colon(startLine, startColumn)); break;
            case ';': tokens.add(AnmToken.semicolon(startLine, startColumn)); break;
            case ',': tokens.add(AnmToken.comma(startLine, startColumn)); break;
            case '+': tokens.add(AnmToken.plus(startLine, startColumn)); break;
            case '"': string(); break;
            case '/': {
                c = advance();
                if (c == '/') while (peek() != '\n' && !isAtEnd()) advance();
                else throw new AnmLexerException("Invalid character " + c, startLine, startColumn);
            } break;
            default: {
                if (Character.isAlphabetic(c) || c == '_')
                    id();
                else if (Character.isDigit(c) || c == '.' || c == '-')
                    number();
                else if (!Character.isWhitespace(c))
                    throw new AnmLexerException("Invalid character " + c, startLine, startColumn);
            } break;
        }
    }

    /**
     * Сканує директиву.
     * @throws AnmLexerException якщо директиви не існує
     * @author uwuhasmile
     */
    private void directive(int startLine, int startColumn) {
        StringBuilder b = new StringBuilder();
        while (Character.isAlphabetic(peek()))
            b.append(advance());
        String directive = b.toString();
        for (int i = 1; i < directives.length; ++i)
            if (directives[i].equals(directive)) {
                tokens.add(AnmToken.directive(i, startLine, startColumn));
                return;
            }
        throw new AnmLexerException("Invalid directive: " + directive, startLine, startColumn);
    }

    /**
     * Сканує рядковий літерал.
     * @throws AnmLexerException якщо неправильний escape sequence або раптовий кінець файлу
     * @author uwuhasmile
     */
    private void string() {
        StringBuilder b = new StringBuilder();
        while (peek() != '"' && !isAtEnd()) {
            char c = advance();
            if (c == '\\' && !isAtEnd()) {
                c = advance();
                if (c == 'n') b.append('\n');
                else if (c == 't') b.append('\t');
                else if (c == 'r') b.append('\r');
                else throw new AnmLexerException("Invalid escape sequence: " + c, line, column);
            } else if (c != '\n' && c != '\t' && c != '\r')
                b.append(c);
        }
        if (isAtEnd()) throw new AnmLexerException("Sudden end of string", line, column);
        advance();
        tokens.add(AnmToken.string(b.toString(), startLine, startColumn));
    }

    /**
     * Сканує ідентифікатор.
     * @authow uwuhasmile
     */
    private void id() {
        while (Character.isAlphabetic(peek()) || Character.isDigit(peek()) || peek() == '_') advance();
        int keyword = 0;
        String content = new String(input, startIndex, index - startIndex);
        for (int i = 1; i < keywords.length; ++i) {
            if (keywords[i].equals(content)) {
                keyword = i;
                break;
            }
        }
        if (keyword > 0)
            tokens.add(AnmToken.keyword(keyword, startLine, startColumn));
        else
            tokens.add(AnmToken.id(content, startLine, startColumn));
    }

    /**
     * Сканує число.
     * @author uwuhasmile
     */
    private void number() {
        while (Character.isDigit(peek())) advance();
        if (peek() == '.') {
            advance();
            while (Character.isDigit(peek())) advance();
            tokens.add(AnmToken.fl(Float.parseFloat(new String(input, startIndex, index - startIndex)), startLine, startColumn));
        } else
            tokens.add(AnmToken.integer(Integer.parseInt(new String(input, startIndex, index - startIndex)), startLine, startColumn));
    }

    /**
     * @return поточний символ, або <code>\0</code>
     * @author uwuhasmile
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return input[index];
    }

    /**
     * Переходить до наступного символу.
     * @return поточний символ до переходу, або <code>\0</code>
     * @author uwuhasmile
     */
    private char advance() {
        char c = input[index++];
        if (c == '\n') {
            ++line;
            column = 0;
        } else
            ++column;
        return c;
    }

    /**
     * @return чи було прочитано весь текст.
     * @author uwuhasmile
     */
    private boolean isAtEnd() {
        return index >= input.length;
    }
}
