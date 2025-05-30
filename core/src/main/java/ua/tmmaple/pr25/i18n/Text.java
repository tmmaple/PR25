package ua.tmmaple.pr25.i18n;

public final class Text implements CharSequence {
    public final boolean loc;
    public final String key;

    public static Text localized(String key) {
        return new Text(true, key);
    }

    public static Text string(String key) {
        return new Text(false, key);
    }

    private Text(boolean loc, String key) {
        this.loc = true;
        this.key = key;
    }

    @Override
    public int length() {
        return (loc ? Language.get(key) : key).length();
    }

    @Override
    public char charAt(int index) {
        return (loc ? Language.get(key) : key).charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return (loc ? Language.get(key) : key).subSequence(start, end);
    }

    @Override
    public String toString() {
        return (loc ? Language.get(key) : key);
    }
}
