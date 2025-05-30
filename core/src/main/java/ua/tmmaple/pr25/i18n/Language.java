package ua.tmmaple.pr25.i18n;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import ua.tmmaple.pr25.Flow;
import ua.tmmaple.pr25.God;

import java.util.Locale;

public final class Language {
    public static final int LANGUAGE_ENGLISH = 0;
    public static final int LANGUAGE_UKRAINIAN = 1;

    private static final String LANGUAGE_BASE_HANDLE = "i18n/content";

    private static final Locale[] SUPPORTED_LOCALES = {
        new Locale("en", "US"),
        new Locale("uk", "UA"),
    };

    private static Language instance;

    private FileHandle baseHandle;
    private I18NBundle bundle;

    public static void register(Language instance) {
        if (Language.instance != null) throw new RuntimeException("Language is already initialized");

        instance.baseHandle = Gdx.files.internal(LANGUAGE_BASE_HANDLE);
        Language.instance = instance;

        Flow.FlowNode<Language> node = new Flow.FlowNode<>(instance, Language::update, Language::added);
        Flow.addToUpdate(node, 997);
    }

    public static String get(String key) {
        if (instance == null) return "???" + key + "???";
        return instance.bundle.get(key);
    }

    public static String get(String key, Object... args) {
        if (instance == null) return "???" + key + "???";
        return instance.bundle.format(key, args);
    }

    private static int added(Language instance) {
        instance.bundle = I18NBundle.createBundle(instance.baseHandle, SUPPORTED_LOCALES[God.get().language()]);
        return 0;
    }

    private static int update(Language instance) {
        if (instance.bundle == null || instance.bundle.getLocale() != SUPPORTED_LOCALES[God.get().language()])
            instance.bundle = I18NBundle.createBundle(instance.baseHandle, SUPPORTED_LOCALES[God.get().language()]);
        return Flow.FLOW_RESULT_CONTINUE;
    }
}
