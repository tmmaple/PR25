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

    public static Language global;

    private FileHandle baseHandle;
    private I18NBundle bundle;

    public static void register() {
        global.baseHandle = Gdx.files.internal(LANGUAGE_BASE_HANDLE);

        Flow.FlowNode<Language> node = new Flow.FlowNode<>(global, Language::update, Language::added);
        Flow.global.addToUpdate(node, 997);
    }

    public String get(String key) {
        return bundle.get(key);
    }

    public String get(String key, Object... args) {
        return bundle.format(key, args);
    }

    private static int added(Language language) {
        language.bundle = I18NBundle.createBundle(language.baseHandle, SUPPORTED_LOCALES[God.global.language()]);
        return 0;
    }

    private static int update(Language language) {
        if (language.bundle == null || language.bundle.getLocale() != SUPPORTED_LOCALES[God.global.language()])
            language.bundle = I18NBundle.createBundle(language.baseHandle, SUPPORTED_LOCALES[God.global.language()]);
        return Flow.FLOW_RESULT_CONTINUE;
    }
}
