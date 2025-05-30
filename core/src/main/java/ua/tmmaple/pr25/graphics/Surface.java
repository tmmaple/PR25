package ua.tmmaple.pr25.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public final class Surface {
    FrameBuffer fbo;

    public float x;
    public float y;
    public float width;
    public float height;

    public Surface(int originalWidth, int originalHeight) {
        this(originalWidth, originalHeight, 0.0f, 0.0f, originalWidth, originalHeight);
    }

    public Surface(int originalWidth, int originalHeight, float width, float height) {
        this(originalWidth, originalHeight, 0.0f, 0.0f, width, height);
    }

    public Surface(int originalWidth, int originalHeight, float x, float y, float width, float height) {
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, originalWidth, originalHeight, false);
    }

    public void resize(int newWidth, int newHeight) {
        fbo.dispose();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, newWidth, newHeight, false);
    }

    void dispose() {
        fbo.dispose();
    }
}
