package minercrystal.gamefield.WorkScreens.Tools.InterfacePlayScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;
import java.util.Random;

import minercrystal.gamefield.Tools.CoinAnimation;

public class Coins {
    CoinAnimation coinAnimation;
    BitmapFont font12;
    private int coins, crystals;
    private Rectangle coinRect;
    private Array<Rectangle> coinsRects;
    private Random random;
    private Array<Vector2> coinsSpeed;
    private float speedDel = 60;

    public Coins(int crystals, int coins) {
        this.crystals = crystals;
        this.coins = coins;
        random = new Random();
        coinsSpeed = new Array<Vector2>();
        coinAnimation = new CoinAnimation(40, 765);
        coinRect = new Rectangle(31, 760, 5, 10);
        coinsRects = new Array<Rectangle>();
        FileHandle baseFileHandle = Gdx.files.internal("Bundles/Bundle");
        Locale locale = new Locale("ru");
        I18NBundle myBundle = I18NBundle.createBundle(baseFileHandle, locale);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/MainFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 29;
        parameter.color = new Color(111f, 55f, 19f, 1);
        font12 = generator.generateFont(parameter);
    }

    public void render(SpriteBatch spriteBatch, float delta) {
        coinAnimation.render(spriteBatch, delta);
        font12.draw(spriteBatch, String.valueOf(coins), 41, 789);
        if (coinsRects.size > 0) {
            for (int i = 0; i < coinsRects.size; i++) {
                coinsRects.get(i).y += coinsSpeed.get(i).y;
                coinsRects.get(i).x += coinsSpeed.get(i).x;
                if (coinsRects.get(i).overlaps(coinRect)) {
                    coinsSpeed.removeValue(coinsSpeed.get(i), false);
                    coinsRects.removeValue(coinsRects.get(i), false);
                    coinAnimation.kick();
                    coins++;
                } else {
                    spriteBatch.draw(coinAnimation.getFirstTextureCoin(), coinsRects.get(i).x, coinsRects.get(i).y, coinsRects.get(i).width, coinsRects.get(i).height);
                }
            }
        }

    }

    public void addCoin(int boost, float X, float Y, int WHBLOCK) {
        for (int i = 0; i < boost; i++) {
            Rectangle r = new Rectangle(X + random.nextInt(WHBLOCK), Y + random.nextInt(WHBLOCK), 35, 35);
            coinsRects.add(r);
            coinsSpeed.add(new Vector2(((coinRect.x) - r.x) / speedDel, ((coinRect.y) - r.y) / speedDel));
        }
    }

    public void destroy() {

    }
}
