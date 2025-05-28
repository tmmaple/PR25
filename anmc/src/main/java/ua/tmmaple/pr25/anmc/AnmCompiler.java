package ua.tmmaple.pr25.anmc;

import ua.tmmaple.pr25.g2d.Anm;

import java.io.*;

public final class AnmCompiler {
    private DataOutputStream dos;

    private AnmIM.AnmProgram program;
    private File output;

    public void compile(AnmIM.AnmProgram program, File output) {
        this.program = program;
        output.mkdirs();
        try {
            if (!output.createNewFile()) {
                output.delete();
                output.createNewFile();
            }
            dos = new DataOutputStream(new FileOutputStream(output));
            dos.write(Anm.ANM_MAGIC);
            dos.write(Anm.ANM_VERSION);
            dos.writeByte(program.imports.size());
            for (String i : program.imports) {
                dos.writeByte(i.length());
                dos.writeBytes(i);
            }
            dos.writeByte(program.sources.size());
            for (AnmIM.AnmSource s : program.sources) {
                dos.writeByte(s.from);
                dos.writeInt(s.x);
                dos.writeInt(s.y);
                dos.writeInt(s.width);
                dos.writeInt(s.height);
            }
            dos.writeByte(program.scripts.size());
            for (AnmIM.AnmScript s : program.scripts) {
                dos.writeByte(s.name.length());
                dos.writeBytes(s.name);
                int fullSize = 0;
                for (AnmIM.AnmInstruction instr : s.instructions)
                    fullSize += AnmInstructionDecl.size(instr.opcode);
                dos.writeInt(fullSize);
                fullSize = 0;
                for (AnmIM.AnmInstruction instr : s.instructions) {
                    dos.writeByte(instr.opcode);
                    fullSize += 1;
                    dos.writeInt(instr.time);
                    fullSize += 4;
                    for (AnmIM.AnmValue arg : instr.args) {
                        switch (arg.type) {
                            case AnmIM.VALUE_TYPE_BYTE:
                                dos.writeByte(arg.asByte());
                                fullSize += 1;
                                break;
                            case AnmIM.VALUE_TYPE_INTEGER:
                                dos.writeInt(arg.asInteger());
                                fullSize += 4;
                                break;
                            case AnmIM.VALUE_TYPE_FLOAT: {
                                dos.writeFloat(arg.asFloat());
                                fullSize += 4;
                            } break;
                            case AnmIM.VALUE_TYPE_BYTE_OFFSET: {
                                dos.writeInt(arg.asByteOffset() - instr.byteOffset);
                                fullSize += 4;
                            } break;
                        }
                    }
                }
                fullSize = fullSize;
            }
            dos.close();
        } catch (IOException e) {
            throw new AnmCompilerException(e.getMessage());
        }
    }
}
