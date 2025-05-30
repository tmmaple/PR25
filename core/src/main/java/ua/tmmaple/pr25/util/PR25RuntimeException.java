package ua.tmmaple.pr25.util;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class PR25RuntimeException extends GdxRuntimeException {
    public PR25RuntimeException(Exception e) {
        super(e);
    }

    public PR25RuntimeException(String message) {
        super(message);
    }
}
