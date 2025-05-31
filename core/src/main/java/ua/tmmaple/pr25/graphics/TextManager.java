package ua.tmmaple.pr25.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public final class TextManager {
    public static TextManager global;

    private final BitmapFont[] fonts;
    private boolean drawing;

    public TextManager() {
        fonts = new BitmapFont[6];
        drawing = false;
    }

    public void initialize() {
        fonts[0] = new BitmapFont(Gdx.files.internal("fonts/ui24.fnt"), Gdx.files.internal("fonts/ui24.png"), false);
        fonts[1] = new BitmapFont(Gdx.files.internal("fonts/ui24.fnt"), Gdx.files.internal("fonts/ui24Flat.png"), false);
        fonts[2] = new BitmapFont(Gdx.files.internal("fonts/ui34.fnt"), Gdx.files.internal("fonts/ui34.png"), false);
        fonts[3] = new BitmapFont(Gdx.files.internal("fonts/ui34.fnt"), Gdx.files.internal("fonts/ui34Flat.png"), false);
        fonts[4] = new BitmapFont(Gdx.files.internal("fonts/dialogue.fnt"));
        fonts[5] = new BitmapFont(Gdx.files.internal("fonts/digits.fnt"));
    }

    public void shutdown() {
        for (BitmapFont font : fonts)
            if (font != null) font.dispose();
    }

    public TextSettings create() {
        return new TextSettings();
    }

    public void begin() {
        if (drawing) throw new IllegalStateException("TextManager already begun");
        drawing = true;
    }

    public void draw(CharSequence text, TextSettings settings) {
        if (!drawing) throw new IllegalStateException("TextManager did not begin");
        if (text == null || text.length() == 0) return;
        BitmapFont font = fonts[settings.font];
        Color color = settings.color.cpy();
        Vector2 pos = settings.position.cpy();
        if (settings.parent != null) {
            Color parentColor = settings.parent.absoluteColor();
            color.mul(parentColor.r, parentColor.g, parentColor.b, settings.parent.absoluteAlpha());
            pos.add(settings.parent.absolutePosition());
        }
        font.setColor(color);
        int start = Math.max(settings.start, 0);
        int end = Math.min(settings.end, text.length());
        font.draw(GraphicManager.global.batch, text, pos.x, pos.y, start, end, settings.targetWidth, settings.hAlign, settings.wrap);
    }

    public void end() {
        if (!drawing) throw new IllegalStateException("TextManager did not begin");
        drawing = false;
    }

    public class TextSettings {
        private byte font;

        public GraphicManager.AnmVirtualMachine parent;

        public Color color;
        public Vector2 position;
        public float targetWidth;
        public int start;
        public int end;
        public int hAlign;
        public boolean wrap;

        private TextSettings() {
            parent = null;
            font = 0;
            color = Color.WHITE;
            targetWidth = 64.0f;
            start = 0;
            end = Integer.MAX_VALUE;
            hAlign = Align.center;
            wrap = true;
        }

        public void setFont(byte font) {
            if (font < 0 || font >= fonts.length) throw new IllegalArgumentException("Invalid font number");
            this.font = font;
        }
    }
}
