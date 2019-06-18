package minercrystal.gamefield.Tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class CoinAnimation {
    private Texture coinAtlas, coinFrame;
    private Array<TextureRegion> coins;
    private float width = 512, animationDelay = 0.05f, accumulator, coinWidth = 30, coinHeigh = 30, HWzoom = 0;
    private int index = 0;
    private final static float TIME_TO_ZOOM = 3, ZOOM_SPEED = 0.5f;
    private Vector2 position;
    private Array<Rectangle> animFrames;
    private Array<Float> splashFrames;
    private float splashTimer = 4.50f, DeltaFrames = 0.75f,
            FramesAccumulator, AnimLong = 0.08f, AnimAccumulator, IndexSize = 0.45f;
    private boolean  isMaxAnimate = false;

    public CoinAnimation(float X, float Y) {
        position = new Vector2(X, Y);
        coins = new Array<TextureRegion>();
        coinAtlas = new Texture("tools/coinAtlas.png");
        coinFrame = new Texture("tools/coinFrame.png");
        animFrames = new Array<Rectangle>();
        splashFrames = new Array<Float>();
        for (int i = 0; i < 4; i++) {
            TextureRegion textureRegion = new TextureRegion(coinAtlas, (int) (i * width), 512, 512, 512);
            coins.add(textureRegion);
        }
        for (int i = 0; i < 4; i++) {
            TextureRegion textureRegion = new TextureRegion(coinAtlas, (int) (i * width), 0, 512, 512);
            textureRegion.flip(false, true);
            coins.add(textureRegion);
        }


    }

    public void render(SpriteBatch spriteBatch, float delta) {
        accumulator += delta;
        if (accumulator > animationDelay) {
            accumulator = 0;
            index++;
            if (index == 8)
                index = 0;
        }
        if (HWzoom > 0) {
            HWzoom -= ZOOM_SPEED;
        }
        if (isMaxAnimate) {
            AnimAccumulator += delta;
            if (AnimAccumulator < AnimLong) {
                FramesAccumulator += delta;
                if (FramesAccumulator > DeltaFrames) {
                    FramesAccumulator = 0;
                    addAnimFrame();
                }
            }
            for (Rectangle r : animFrames) {
                r.x -= IndexSize;
                r.y -= IndexSize;
                r.width += IndexSize * 2;
                r.height += IndexSize * 2;
                drawAnimFrame(delta, spriteBatch, r);
            }
            if (animFrames.size == 0) {
                isMaxAnimate = false;
                AnimAccumulator = 0;
            }
        }
        spriteBatch.draw(coins.get(index), position.x , position.y, 0, 0, coinWidth + HWzoom, coinHeigh + HWzoom, 1, 1, 90);
    }
    private void drawAnimFrame(float delta, SpriteBatch spriteBatch, Rectangle r) {
        splashFrames.set(animFrames.indexOf(r, false), splashFrames.get(animFrames.indexOf(r, false)) - splashTimer * delta);
        Color c = spriteBatch.getColor();
        spriteBatch.end();
        spriteBatch.setColor(c.r, c.g, c.b, splashFrames.get(animFrames.indexOf(r, false)));

        spriteBatch.begin();
        spriteBatch.draw(coinFrame, r.x-28, r.y, r.width, r.height);
        spriteBatch.end();
        spriteBatch.begin();
        if (splashFrames.get(animFrames.indexOf(r, false)) < 0) {
            splashFrames.removeValue(splashFrames.get(animFrames.indexOf(r, false)), false);
            animFrames.removeValue(r, false);
        }
        spriteBatch.setColor(c.r, c.g, c.b, 1);
    }

    private void addAnimFrame() {
        animFrames.add(new Rectangle(position.x, position.y, coinWidth, coinHeigh));
        splashFrames.add(1f);
    }

    public void kick() {
        HWzoom = TIME_TO_ZOOM;
        isMaxAnimate = true;
        addAnimFrame();
    }

    public TextureRegion getFirstTextureCoin() {
        return coins.get(5);
    }
}
