package ua.tmmaple.pr25.anmc;

import ua.tmmaple.pr25.graphics.GraphicManager;

import java.util.ArrayList;
import java.util.List;

public final class AnmParser {
    private static final int SCOPE_TOP = 1;
    private static final int SCOPE_SCRIPT = 2;

    private AnmIM.AnmProgram program;
    private List<AnmToken> input;
    private int index;

    private final List<Object> definitions;
    private int scope;

    private final List<Object> labels;
    private int byteOffset;
    private short time;

    public AnmParser() {
        definitions = new ArrayList<>();
        labels = new ArrayList<>();
    }

    public void parse(List<AnmToken> tokens) {
        program = AnmIM.createProgram();
        this.input = tokens;
        index = 0;

        definitions.clear();
        scope = SCOPE_TOP;

        while (!isAtEnd())
            next();
    }

    public AnmIM.AnmProgram getProgram() {
        return program;
    }

    private void next() {
        if (match(AnmToken.TOKEN_DIRECTIVE)) directive();
        else script();
    }

    private void directive() {
        AnmToken token = previous();
        if (token.iValue == AnmToken.DIRECTIVE_IMPORT) {
            if (!match(AnmToken.TOKEN_STRING)) throw new AnmParserException("Expected a string literal containing filename, got " + peek());
            token = previous();
            program.addImport(token.sValue);
        } else if (token.iValue == AnmToken.DIRECTIVE_SOURCE) {
            AnmIM.AnmValue[] values = parseArguments(AnmIM.VALUE_TYPE_BYTE, AnmIM.VALUE_TYPE_INTEGER, AnmIM.VALUE_TYPE_INTEGER, AnmIM.VALUE_TYPE_INTEGER, AnmIM.VALUE_TYPE_INTEGER);
            program.addSource(values[0].asByte(), values[1].asInteger(), values[2].asInteger(), values[3].asInteger(), values[4].asInteger());
        } else if (token.iValue == AnmToken.DIRECTIVE_DEFINE) {
            if (!match(AnmToken.TOKEN_ID)) throw new AnmParserException("Expected an identifier, got " + peek());
            token = previous();
            if (constantExists(token.sValue)) throw new AnmParserException("Constant '" + token.sValue + "' already exists");
            AnmIM.AnmValue value = parseArgument();
            definitions.add(token.sValue);
            definitions.add(value);
        }
    }

    private void script() {
        labels.clear();
        if (!check(AnmToken.TOKEN_KEYWORD) || peek().iValue != AnmToken.KEYWORD_SCRIPT) throw new AnmParserException("Expected script, got " + peek());
        advance();
        if (!check(AnmToken.TOKEN_ID)) throw new AnmParserException("Expected identifier, got " + this);
        String name = advance().sValue;
        AnmIM.AnmScript script = program.addScript(name);
        scriptBody(script);
    }

    private void scriptBody(AnmIM.AnmScript script) {
        if (!match(AnmToken.TOKEN_START_BLOCK)) throw new AnmParserException("Expected {, got " + previous());
        scope = SCOPE_SCRIPT;
        scriptPreprocess();
        scriptProcess(script);
        scope = SCOPE_TOP;
    }

    private void scriptPreprocess() {
        byteOffset = 0;
        int start = index;
        while (!isAtEnd() && !match(AnmToken.TOKEN_END_BLOCK)) {
            if (match(AnmToken.TOKEN_INTEGER) && check(AnmToken.TOKEN_COLON)) advance();
            else if (match(AnmToken.TOKEN_PLUS) && match(AnmToken.TOKEN_INTEGER) && check(AnmToken.TOKEN_COLON)) advance();
            else if (match(AnmToken.TOKEN_KEYWORD)) {
                AnmToken token = previous();
                if (token.iValue == AnmToken.KEYWORD_INTERRUPT && match(AnmToken.TOKEN_ID, AnmToken.TOKEN_INTEGER) && match(AnmToken.TOKEN_SEMICOLON))
                    byteOffset += AnmInstructionDecl.size(GraphicManager.AnmVirtualMachine.ANM_OP_INTERRUPT);
                else if (token.iValue == AnmToken.KEYWORD_SLEEP && match(AnmToken.TOKEN_INTEGER, AnmToken.TOKEN_ID) && match(AnmToken.TOKEN_SEMICOLON))
                    byteOffset += AnmInstructionDecl.size(GraphicManager.AnmVirtualMachine.ANM_OP_SLEEP);
                else if (token.iValue == AnmToken.KEYWORD_JUMP && match(AnmToken.TOKEN_ID) && match(AnmToken.TOKEN_SEMICOLON))
                    byteOffset += AnmInstructionDecl.size(GraphicManager.AnmVirtualMachine.ANM_OP_JUMP);
                else if (AnmInstructionDecl.byKeyword(token.iValue) != 0 && match(AnmToken.TOKEN_SEMICOLON)) byteOffset += AnmInstructionDecl.sizeByKeyword(token.iValue);
                else throw new AnmParserException("Unexpected token " + token);
            } else if (match(AnmToken.TOKEN_ID)) {
                AnmToken token = previous();
                if (match(AnmToken.TOKEN_COLON)) {
                    if (containsLabel(token.sValue)) throw new AnmParserException("Label '" + token.sValue + "' already exists");
                    labels.add(token.sValue);
                    labels.add(byteOffset);
                } else {
                    byte opcode = AnmInstructionDecl.find(token.sValue);
                    if (opcode != 0) {
                        if (!match(AnmToken.TOKEN_LEFT_PAREN)) throw new AnmParserException("Unexpected token " + token);
                        while (!isAtEnd() && !match(AnmToken.TOKEN_RIGHT_PAREN)) advance();
                        if (isAtEnd()) throw new AnmParserException("Expected ), got EOF");
                        if (!match(AnmToken.TOKEN_SEMICOLON)) throw new AnmParserException("Expected ; " + token);
                        byteOffset += AnmInstructionDecl.size(opcode);
                    }
                }
            } else throw new AnmParserException("Unexpected token " + peek());
        }
        index = start;
    }

    private void scriptProcess(AnmIM.AnmScript script) {
        byteOffset = 0;
        time = 0;
        while (!isAtEnd() && !match(AnmToken.TOKEN_END_BLOCK)) {
            if (match(AnmToken.TOKEN_INTEGER) && check(AnmToken.TOKEN_COLON)) {
                time = (short) previous().iValue;
                advance();
            } else if (match(AnmToken.TOKEN_PLUS) && match(AnmToken.TOKEN_INTEGER) && check(AnmToken.TOKEN_COLON)) {
                time += (short) previous().iValue;
                advance();
            } else if (match(AnmToken.TOKEN_KEYWORD)) script.instructions.add(keyword());
            else if (match(AnmToken.TOKEN_ID)) {
                if (check(AnmToken.TOKEN_COLON)) advance();
                else script.instructions.add(identifier());
            } else advance();
        }
    }

    private AnmIM.AnmInstruction keyword() {
        AnmToken token = previous();
        AnmIM.AnmInstruction instruction;
        switch (token.iValue) {
            case AnmToken.KEYWORD_JUMP: {
                if (!match(AnmToken.TOKEN_ID)) throw new AnmParserException("Expected id, got " + token);
                token = previous();
                int label = findLabel(token.sValue);
                if (label == -1) throw new AnmParserException("Label '" + token.sValue + "' not found");
                instruction = new AnmIM.AnmInstruction(time, GraphicManager.AnmVirtualMachine.ANM_OP_JUMP, byteOffset, AnmIM.AnmValue.byteOffset(label));
                byteOffset += AnmInstructionDecl.size(GraphicManager.AnmVirtualMachine.ANM_OP_JUMP);
                if (!match(AnmToken.TOKEN_SEMICOLON)) throw new AnmParserException("Expected ; got " + token);
            } break;
            case AnmToken.KEYWORD_SLEEP: {
                AnmIM.AnmValue arg = parseArgument(AnmIM.VALUE_TYPE_INTEGER);
                if (arg.type == AnmIM.VALUE_TYPE_FLOAT || arg.type == AnmIM.VALUE_TYPE_BYTE_OFFSET)
                    throw new AnmParserException("Invalid argument type: expected integer");
                instruction = new AnmIM.AnmInstruction(time, GraphicManager.AnmVirtualMachine.ANM_OP_SLEEP, byteOffset, arg);
                byteOffset += AnmInstructionDecl.size(GraphicManager.AnmVirtualMachine.ANM_OP_SLEEP);
                if (!match(AnmToken.TOKEN_SEMICOLON)) throw new AnmParserException("Expected ; got " + token);
            } break;
            case AnmToken.KEYWORD_INTERRUPT: {
                AnmIM.AnmValue arg = parseArgument(AnmIM.VALUE_TYPE_BYTE);
                if (arg.type == AnmIM.VALUE_TYPE_FLOAT || arg.type == AnmIM.VALUE_TYPE_BYTE_OFFSET)
                    throw new AnmParserException("Invalid argument type: expected integer");
                instruction = new AnmIM.AnmInstruction(time, GraphicManager.AnmVirtualMachine.ANM_OP_INTERRUPT, byteOffset, arg);
                byteOffset += AnmInstructionDecl.size(GraphicManager.AnmVirtualMachine.ANM_OP_INTERRUPT);
                if (!match(AnmToken.TOKEN_SEMICOLON)) throw new AnmParserException("Expected ; got " + token);
            } break;
            default: {
                instruction = new AnmIM.AnmInstruction(time, AnmInstructionDecl.byKeyword(token.iValue), byteOffset);
                byteOffset += AnmInstructionDecl.sizeByKeyword(token.iValue);
            } break;
        }
        return instruction;
    }

    private AnmIM.AnmInstruction identifier() {
        AnmToken token = previous();
        AnmIM.AnmInstruction instruction;
        int opcode = AnmInstructionDecl.find(token.sValue);
        if (opcode == 0) throw new AnmParserException("Instruction '" + token.sValue + "' not found");
        AnmIM.AnmValue[] args = parseArguments(AnmInstructionDecl.args(opcode));
        if (!match(AnmToken.TOKEN_SEMICOLON)) throw new AnmParserException("Expected ; got " + token);
        instruction = new AnmIM.AnmInstruction(time, (byte) opcode, byteOffset, args);
        byteOffset += AnmInstructionDecl.size(opcode);
        return instruction;
    }

    private AnmIM.AnmValue[] parseArguments(byte... types) {
        if (!match(AnmToken.TOKEN_LEFT_PAREN)) throw new AnmParserException("Expected (, got " + advance());
        AnmIM.AnmValue[] arguments = null;
        if (types != null && types.length > 0) {
            arguments = new AnmIM.AnmValue[types.length];
            for (int i = 0; i < types.length; ++i) {
                byte type = types[i];
                AnmIM.AnmValue value = parseArgument(type);
                if (i < types.length - 1 && !match(AnmToken.TOKEN_COMMA))
                    throw new AnmParserException("Expected , but got " + advance());
                arguments[i] = value;
            }
        }
        if (!match(AnmToken.TOKEN_RIGHT_PAREN)) throw new AnmParserException("Expected ), got " + advance());
        return arguments;
    }

    private AnmIM.AnmValue parseArgument() {
        AnmIM.AnmValue result;
        AnmToken token = advance();
        if (token.type == AnmToken.TOKEN_KEYWORD) {
            if (token.iValue == AnmToken.KEYWORD_TRUE) result = new AnmIM.AnmValue((byte) 1);
            else if (token.iValue == AnmToken.KEYWORD_FALSE) result = new AnmIM.AnmValue((byte) 0);
            else throw new AnmParserException("Expected true or false, got " + token);
        } else if (token.type == AnmToken.TOKEN_INTEGER)
            result = new AnmIM.AnmValue(token.iValue);
        else if (token.type == AnmToken.TOKEN_FLOAT)
            result = new AnmIM.AnmValue(token.fValue);
        else if (token.type == AnmToken.TOKEN_ID) {
            result = findConstant(token.sValue);
            if (result == null) throw new AnmParserException("No such constant is defined: " + token.sValue);
        }
        else throw new AnmParserException("Invalid token: " + token);
        return result;
    }

    private AnmIM.AnmValue parseArgument(byte type) {
        AnmIM.AnmValue result = parseArgument();
        if (result.type != type)
            switch (type) {
                case AnmIM.VALUE_TYPE_BYTE: result = new AnmIM.AnmValue(result.asByte()); break;
                case AnmIM.VALUE_TYPE_INTEGER: result = new AnmIM.AnmValue(result.asInteger()); break;
                case AnmIM.VALUE_TYPE_FLOAT: result = new AnmIM.AnmValue(result.asFloat()); break;
                case AnmIM.VALUE_TYPE_BYTE_OFFSET: result = new AnmIM.AnmValue(result.asByteOffset()); break;
            }
        return result;
    }

    private AnmIM.AnmValue findConstant(String name) {
        for (int i = 0; i < AnmIM.BUILTIN_CONSTANTS.length; i += 2)
            if (AnmIM.BUILTIN_CONSTANTS[i].equals(name)) return (AnmIM.AnmValue) AnmIM.BUILTIN_CONSTANTS[i + 1];
        for (int i = 0; i < definitions.size(); i += 2)
            if (definitions.get(i).equals(name)) return (AnmIM.AnmValue) definitions.get(i + 1);
        return null;
    }

    private boolean constantExists(String name) {
        return findConstant(name) != null;
    }

    private int findLabel(String label) {
        for (int i = 0; i < labels.size(); i += 2)
            if (label.equals(labels.get(i))) return (int) labels.get(i + 1);
        return -1;
    }

    private boolean containsLabel(String label) {
        return findLabel(label) != -1;
    }

    private boolean match(int... types) {
        for (int type : types)
            if (check(type)) {
                advance();
                return true;
            }
        return false;
    }

    private boolean check(int type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private AnmToken advance() {
        if (!isAtEnd()) index++;
        return previous();
    }

    private AnmToken peek() {
        return input.get(index);
    }

    private AnmToken previous() {
        return input.get(index - 1);
    }

    private boolean isAtEnd() {
        return input.get(index).type == AnmToken.TOKEN_EOF;
    }
}
