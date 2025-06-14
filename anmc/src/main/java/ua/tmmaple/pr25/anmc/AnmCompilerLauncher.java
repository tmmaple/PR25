package ua.tmmaple.pr25.anmc;

import ua.tmmaple.pr25.graphics.Anm;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Запускає компілятор ANM.
 * @author uwuhasmile
 */
public final class AnmCompilerLauncher {
    private String from;
    private String to;

    private final FileFilter filter;

    private final AnmLexer lexer;
    private final AnmParser parser;
    private final AnmCompiler compiler;

    public static void main(String[] args) {
        System.out.println("ANM compiler for Team Maple ANM version " + Anm.ANM_VERSION);
        System.out.println("Copyright (c) 2025 Team Maple, Hasmile\n");
        if (args.length == 0) {
            System.out.println("Usage: anmc <from> [<to>]");
            return;
        }
        String from = args[0];
        String to = args.length > 1 ? args[1] : args[0];
        System.exit(new AnmCompilerLauncher().run(from, to));
    }

    private AnmCompilerLauncher() {
        filter = (dir) -> dir.isDirectory() || dir.getName().endsWith(".anmsrc");
        lexer = new AnmLexer();
        parser = new AnmParser();
        compiler = new AnmCompiler();
    }

    /**
     * Запускає.
     * @param from тека, звідки беруться <code>.anmsrc</code>
     * @param to тека, куди будуть зберігатись скомпільовані <code>.anm</code>
     * @return 1, якщо помилка, 0, якщо успіх
     * @author uwuhasmile
     */
    private int run(String from, String to) {
        from = from.replace('\\', '/');
        to = to.replace('\\', '/');
        this.from = from;
        this.to = to;
        File f = new File( from);
        if (!f.exists()) {
            System.out.println(from + " does not exist");
            return 1;
        }
        try {
            compile(f);
        } catch (IOException e) {
            System.err.println("Unexpected IO exception happened: " + e + ". Exiting program...");
            return 1;
        }
        return 0;
    }

    /**
     * Компілює певний файл, або рекурсивно компілює теку.
     * Компілює тільки <code>.anmsrc</code>
     * @param file файл або тека
     * @author uwuhasmile
     */
    private void compile(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles(filter);
            if (files == null) return;
            for (File f : files)
                compile(f);
            return;
        }
        System.out.println("Compiling " + file.getPath().replace('\\', '/'));
        byte[] bytes = Files.readAllBytes(file.toPath());
        String source = new String(bytes, StandardCharsets.UTF_8);
        lexer.tokenize(source);
        if (!lexer.errors.isEmpty()) {
            for (String error : lexer.errors) {
                System.out.println("Tokenizer error: " + error);
            }
            System.out.println("Lexer errors detected. Skipping...");
            return;
        }
        AnmToken[] tokens = new AnmToken[lexer.tokens.size()];
        try {
            parser.parse(lexer.tokens);
        } catch (AnmParserException e) {
            System.out.println("Parser error: " + e + ". Skipping...");
            return;
        }
        String outputName = file.getPath().replace(".anmsrc", ".anm").replace('\\', '/').replaceFirst(from, to);
        try {
            compiler.compile(parser.getProgram(), new File(outputName));
        } catch (AnmCompilerException e) {
            System.out.println("Failed to compile: " + e + ". Skipping...");
        }
        System.out.println("Compilation completed, result: " + outputName);
    }
}
