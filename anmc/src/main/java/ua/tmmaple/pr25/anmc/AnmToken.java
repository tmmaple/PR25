package ua.tmmaple.pr25.anmc;

public final class AnmToken {
    public static final int TOKEN_EOF = 0;
    public static final int TOKEN_DIRECTIVE = 1;
    public static final int TOKEN_KEYWORD = 2;
    public static final int TOKEN_ID = 3;
    public static final int TOKEN_STRING = 4;
    public static final int TOKEN_INTEGER = 5;
    public static final int TOKEN_FLOAT = 6;
    public static final int TOKEN_LEFT_PAREN = 7;
    public static final int TOKEN_RIGHT_PAREN = 8;
    public static final int TOKEN_LEFT_BRACKET = 9;
    public static final int TOKEN_RIGHT_BRACKET = 10;
    public static final int TOKEN_START_BLOCK = 11;
    public static final int TOKEN_END_BLOCK = 12;
    public static final int TOKEN_COLON = 13;
    public static final int TOKEN_SEMICOLON = 14;
    public static final int TOKEN_COMMA = 15;
    public static final int TOKEN_PLUS = 16;

    public static final int DIRECTIVE_IMPORT = 1;
    public static final int DIRECTIVE_SOURCE = 2;
    public static final int DIRECTIVE_DEFINE = 3;

    public static final int KEYWORD_SCRIPT = 1;
    public static final int KEYWORD_DELETE = 2;
    public static final int KEYWORD_STOP = 3;
    public static final int KEYWORD_PAUSE = 4;
    public static final int KEYWORD_HIDE_PAUSE = 5;
    public static final int KEYWORD_INTERRUPT = 6;
    public static final int KEYWORD_SLEEP = 7;
    public static final int KEYWORD_RETURN = 8;
    public static final int KEYWORD_JUMP = 9;
    public static final int KEYWORD_TRUE = 10;
    public static final int KEYWORD_FALSE = 11;

    public final int type;
    public final int iValue;
    public final float fValue;
    public final String sValue;

    public final int line;
    public final int column;

    public static AnmToken directive(int value, int line, int column) {
        return new AnmToken(TOKEN_DIRECTIVE, value, 0.0f, null, line, column);
    }

    public static AnmToken keyword(int value, int line, int column) {
        return new AnmToken(TOKEN_KEYWORD, value, 0.0f, null, line, column);
    }

    public static AnmToken keywordFalse(int line, int column) {
        return new AnmToken(TOKEN_KEYWORD, KEYWORD_FALSE, 0.0f, null, line, column);
    }

    public static AnmToken id(String value, int line, int column) {
        return new AnmToken(TOKEN_ID, 0, 0.0f, value, line, column);
    }

    public static AnmToken string(String value, int line, int column) {
        return new AnmToken(TOKEN_STRING, 0, 0.0f, value, line, column);
    }

    public static AnmToken integer(int value, int line, int column) {
        return new AnmToken(TOKEN_INTEGER, value, 0.0f, null, line, column);
    }

    public static AnmToken fl(float value, int line, int column) {
        return new AnmToken(TOKEN_FLOAT, 0, value, null, line, column);
    }

    public static AnmToken leftParen(int line, int column) {
        return new AnmToken(TOKEN_LEFT_PAREN, 0, 0.0f, null, line, column);
    }

    public static AnmToken rightParen(int line, int column) {
        return new AnmToken(TOKEN_RIGHT_PAREN, 0, 0.0f, null, line, column);
    }

    public static AnmToken leftBracket(int line, int column) {
        return new AnmToken(TOKEN_LEFT_BRACKET, 0, 0.0f, null, line, column);
    }

    public static AnmToken rightBracket(int line, int column) {
        return new AnmToken(TOKEN_RIGHT_BRACKET, 0, 0.0f, null, line, column);
    }

    public static AnmToken startBlock(int line, int column) {
        return new AnmToken(TOKEN_START_BLOCK, 0, 0.0f, null, line, column);
    }

    public static AnmToken endBlock(int line, int column) {
        return new AnmToken(TOKEN_END_BLOCK, 0, 0.0f, null, line, column);
    }

    public static AnmToken colon(int line, int column) {
        return new AnmToken(TOKEN_COLON, 0, 0.0f, null, line, column);
    }

    public static AnmToken semicolon(int line, int column) {
        return new AnmToken(TOKEN_SEMICOLON, 0, 0.0f, null, line, column);
    }

    public static AnmToken comma(int line, int column) {
        return new AnmToken(TOKEN_COMMA, 0, 0.0f, null, line, column);
    }

    public static AnmToken plus(int line, int column) {
        return new AnmToken(TOKEN_PLUS, 0, 0.0f, null, line, column);
    }

    public static AnmToken eof(int line, int column) {
        return new AnmToken(TOKEN_EOF, 0, 0.0f, null, line, column);
    }

    private AnmToken(int type, int iValue, float fValue, String sValue, int line, int column) {
        this.type = type;
        this.iValue = iValue;
        this.fValue = fValue;
        this.sValue = sValue;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        String result = "Invalid";
        switch (type) {
            case TOKEN_DIRECTIVE:
                switch (iValue) {
                    case DIRECTIVE_IMPORT: result = "Directive: #import"; break;
                    case DIRECTIVE_SOURCE: result = "Directive: #source"; break;
                    case DIRECTIVE_DEFINE: result = "Directive: #define"; break;
                }
                break;
            case TOKEN_KEYWORD:
                switch (iValue) {
                    case KEYWORD_SCRIPT: result = "Keyword: script"; break;
                    case KEYWORD_DELETE: result = "Keyword: delete"; break;
                    case KEYWORD_STOP: result = "Keyword: stop"; break;
                    case KEYWORD_PAUSE: result = "Keyword: pause"; break;
                    case KEYWORD_HIDE_PAUSE: result = "Keyword: hidePause"; break;
                    case KEYWORD_INTERRUPT: result = "Keyword: interrupt"; break;
                    case KEYWORD_SLEEP: result = "Keyword: sleep"; break;
                    case KEYWORD_RETURN: result = "Keyword: return"; break;
                    case KEYWORD_JUMP: result = "Keyword: jump"; break;
                    case KEYWORD_TRUE: result = "Keyword: true"; break;
                    case KEYWORD_FALSE: result = "Keyword: false"; break;
                }
                break;
            case TOKEN_ID: result = "Identificator: " + sValue; break;
            case TOKEN_STRING: result = "String: \"" + sValue + '"'; break;
            case TOKEN_INTEGER: result = "Integer: " + iValue; break;
            case TOKEN_FLOAT: result = "Float: " + fValue; break;
            case TOKEN_LEFT_PAREN: result = "("; break;
            case TOKEN_RIGHT_PAREN: result = ")"; break;
            case TOKEN_LEFT_BRACKET: result = "["; break;
            case TOKEN_RIGHT_BRACKET: result = "]"; break;
            case TOKEN_START_BLOCK: result = "{"; break;
            case TOKEN_END_BLOCK: result = "}"; break;
            case TOKEN_COLON: result = ":"; break;
            case TOKEN_SEMICOLON: result = ";"; break;
            case TOKEN_COMMA: result = ","; break;
            case TOKEN_PLUS: result = "+"; break;
            case TOKEN_EOF: result = "EOF"; break;
        }
        return result + " at line " + line + ", column " + column;
    }
}
