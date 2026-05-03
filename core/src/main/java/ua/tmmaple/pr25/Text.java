package ua.tmmaple.pr25;

/**
 * Локалізований текст. Звертається до завантаженої таблиці даних локалізації та повертає відповідний рядок.
 * @author afiliushkin
 */
public final class Text implements CharSequence {
    private final String id;
    private final boolean idIfNotFound;
    private Object[] args;

    /**
     * @param id ідентифікатор текста в таблиці
     * @param idIfNotFound якщо такого не знайдено, то замість """???<code>id</code>???" повертати <code>id</code>
     * @param formatArgs аргументи для форматування тексту
     * @author afiliushkin
     */
    public Text(String id, boolean idIfNotFound, Object... formatArgs) {
        this.id = id;
        this.idIfNotFound = idIfNotFound;
        this.args = formatArgs;
    }

    /**
     * @param id ідентифікатор текста в таблиці
     * @param idIfNotFound якщо такого не знайдено, то замість """???<code>id</code>???" повертати <code>id</code>
     * @author afiliushkin
     */
    public Text(String id, boolean idIfNotFound) {
        this(id, idIfNotFound, (Object[]) null);
    }

    /**
     * @param id ідентифікатор текста в таблиці
     * @param formatArgs аргументи для форматування тексту
     * @author afiliushkin
     */
    public Text(String id, Object... formatArgs) {
        this(id, false, formatArgs);
    }

    /**
     * @param id ідентифікатор текста в таблиці
     * @author afiliushkin
     */
    public Text(String id) {
        this(id, false);
    }

    /**
     * Переназначає параметри тексту.
     * @param args параметри
     * @author afiliushkin
     */
    public void params(Object... args) {
        this.args = args;
    }

    @Override
    public int length() {
        String result = toString();
        return result.length();
    }

    @Override
    public char charAt(int index) {
        String result = toString();
        return result.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        String result = toString();
        return result.subSequence(start, end);
    }

    @Override
    public String toString() {
        if (args == null)
            return God.global.getLocalizedString(id, idIfNotFound);
        else
            return God.global.getFormattedLocalizedString(id, idIfNotFound, args);
    }
}
