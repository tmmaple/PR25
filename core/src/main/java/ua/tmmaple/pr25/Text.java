package ua.tmmaple.pr25;

public final class Text implements CharSequence {
    private final String id;
    private final boolean idIfNotFound;
    private final Object[] args;

    public Text(String id, boolean idIfNotFound, Object... formatArgs) {
        this.id = id;
        this.idIfNotFound = idIfNotFound;
        this.args = formatArgs;
    }

    public Text(String id, boolean idIfNotFound) {
        this(id, idIfNotFound, (Object[]) null);
    }

    public Text(String id, Object... formatArgs) {
        this(id, false, formatArgs);
    }

    public Text(String id) {
        this(id, false);
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
