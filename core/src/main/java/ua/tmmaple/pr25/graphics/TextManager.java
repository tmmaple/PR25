package ua.tmmaple.pr25.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import ua.tmmaple.pr25.util.PR25RuntimeException;

/**
 * Керує відмалюванням тексту на екран.
 * @author uwuhasmile
 */
public final class TextManager {
    public static TextManager global;

    private final BitmapFont[] fonts;
    private boolean drawing;

    public TextManager() {
        fonts = new BitmapFont[6];
        drawing = false;
    }

    /**
     * Передзавантажує всі шрифти в пам'ять.
     * @author uwuhasmile
     */
    public void initialize() {
        fonts[0] = new BitmapFont(Gdx.files.internal("fonts/ui24.fnt"), Gdx.files.internal("fonts/ui24.png"), false);
        fonts[1] = new BitmapFont(Gdx.files.internal("fonts/ui24.fnt"), Gdx.files.internal("fonts/ui24Flat.png"), false);
        fonts[2] = new BitmapFont(Gdx.files.internal("fonts/ui34.fnt"), Gdx.files.internal("fonts/ui34.png"), false);
        fonts[3] = new BitmapFont(Gdx.files.internal("fonts/ui34.fnt"), Gdx.files.internal("fonts/ui34Flat.png"), false);
        fonts[4] = new BitmapFont(Gdx.files.internal("fonts/dialogue.fnt"));
        fonts[5] = new BitmapFont(Gdx.files.internal("fonts/digits.fnt"));
    }

    /**
     * Вивантажує всі шрифти з пам'яті.
     * @author uwuhasmile
     */
    public void shutdown() {
        for (BitmapFont font : fonts)
            if (font != null) font.dispose();
    }

    /**
     * Переходить в режим малювання.
     * Має викликатись тоді, коли {@link ua.tmmaple.pr25.graphics.GraphicManager} переходить в режим малювання.
     * @throws PR25RuntimeException якщо вже в режимі малювання
     * @author uwuhasmile
     */
    public void begin() {
        if (drawing) throw new PR25RuntimeException("TextManager already begun");
        drawing = true;
    }

    /**
     * Виходить з режиму малювання.
     * Має викликатись тоді, коли {@link ua.tmmaple.pr25.graphics.GraphicManager} переходить в режим відмалювання.
     * @throws PR25RuntimeException якщо не в режимі малювання
     * @author uwuhasmile
     */
    public void end() {
        if (!drawing) throw new PR25RuntimeException("TextManager did not begin");
        drawing = false;
    }

    /**
     * Шаблон налаштувань тексту.
     * @author uwuhasmile
     */
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

        public TextSettings() {
            parent = null;
            font = 0;
            color = Color.WHITE;
            targetWidth = 64.0f;
            start = 0;
            end = Integer.MAX_VALUE;
            hAlign = Align.center;
            wrap = true;
        }

        /**
         * Встановлює шрифт відмалювання
         * @throws PR25RuntimeException якщо шрифта не існує
         */
        public void setFont(byte font) {
            if (font < 0 || font >= fonts.length) throw new PR25RuntimeException("Invalid font number");
            this.font = font;
        }

        /**
         * Відмальовує текст з певними налаштуваннями.
         * @throws PR25RuntimeException якщо не в режимі малювання
         * @author uwuhasmile
         */
        public void draw(CharSequence text) {
            if (!drawing) throw new PR25RuntimeException("TextManager did not begin");
            if (text == null || text.length() == 0) return;
            BitmapFont font = fonts[this.font];
            Color color = this.color.cpy();
            Vector2 pos = position.cpy();
            if (parent != null) {
                Color parentColor = parent.absoluteColor();
                color.mul(parentColor.r, parentColor.g, parentColor.b, parent.absoluteAlpha());
                pos.add(parent.absolutePosition());
            }
            font.setColor(color);
            int start = Math.max(this.start, 0);
            int end = Math.min(this.end, text.length());
            font.draw(GraphicManager.global.batch, text, pos.x, pos.y, start, end, targetWidth, hAlign, wrap);
        }
    }
}
