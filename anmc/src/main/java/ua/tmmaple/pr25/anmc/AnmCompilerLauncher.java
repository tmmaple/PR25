package ua.tmmaple.pr25.anmc;

import ua.tmmaple.pr25.g2d.Anm;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class AnmCompilerLauncher {
    private final String root;
    private final File from;
    private final String to;

    private final FilenameFilter filter;

    private final AnmLexer lexer;
    private final AnmParser parser;
    private final AnmCompiler compiler;

    public static void main(String[] args) {
        System.out.println("Anm compiler for Team Maple ANM version " + Anm.ANM_VERSION);
        System.out.println("Copyright (c) 2025 Team Maple, Hasmile\n");
        new AnmCompilerLauncher().run();
    }

    private AnmCompilerLauncher() {
        filter = (dir, name) -> dir.isDirectory() || name.endsWith(".anmsrc");
        lexer = new AnmLexer();
        parser = new AnmParser();
        compiler = new AnmCompiler();
        root = "source/anm/";
        from = new File(root, "");
        to = "assets/";
    }

    private void run() {
        if (!from.exists()) {
            System.out.println(from.getAbsolutePath() + " does not exist");
            return;
        }
        try {
            compile(from);
        } catch (IOException e) {
            System.err.println("Unexpected IO exception happened: " + e + ". Exiting program...");
            System.exit(1);
        }
    }

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
        String outputName = file.getPath().replace(".anmsrc", ".anm").replace('\\', '/').replaceFirst(root, to);
        try {
            compiler.compile(parser.getProgram(), new File(outputName));
        } catch (AnmCompilerException e) {
            System.out.println("Failed to compile: " + e + ". Skipping...");
        }
        System.out.println("Compilation completed, result: " + outputName);
    }
}
