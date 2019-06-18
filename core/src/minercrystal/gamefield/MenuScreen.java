package minercrystal.gamefield;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

import minercrystal.gamefield.AnimationScreens.BackgroundScreen;
import minercrystal.gamefield.AnimationScreens.SplashScreen;
import minercrystal.gamefield.Tools.MenuButtons;
import minercrystal.gamefield.WorkScreens.MenuSelectScreen;

public class MenuScreen implements Screen {
    GameClass gameClass;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;
    SplashScreen splashScreen;
    BackgroundScreen backgroundScreen;
    MenuButtons menuButtons;
    float W_INDEX, H_INDEX;
    BitmapFont font12;
    public MenuScreen(GameClass gameClass) {
        this.gameClass = gameClass;
        camera = new OrthographicCamera();
        camera = new OrthographicCamera(480, 800);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
        W_INDEX = (float) (480.0 / Gdx.graphics.getWidth());
        H_INDEX = (float) (800.0 / Gdx.graphics.getHeight());
        backgroundScreen = new BackgroundScreen();
        spriteBatch = new SpriteBatch();
        splashScreen = new SplashScreen();
        setInputProcessor();
        FileHandle baseFileHandle = Gdx.files.internal("Bundles/Bundle");
        Locale locale = new Locale("ru");
        I18NBundle myBundle = I18NBundle.createBundle(baseFileHandle, locale);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/MainFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 52;
        parameter.color = new Color(111f, 55f, 19f, 1);
        font12 = generator.generateFont(parameter);

        menuButtons = new MenuButtons(font12, myBundle);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.setProjectionMatrix(camera.combined);
        if(splashScreen.isSplashScreenActive())splashScreen.setSplashScreen(delta, spriteBatch, camera, backgroundScreen);
        else{
            spriteBatch.begin();
            backgroundScreen.render(delta, spriteBatch);
            spriteBatch.end();
            menuButtons.render(delta, spriteBatch);
            if(menuButtons.isPlayBtnPressed()){
                backgroundScreen.setSPEED(backgroundScreen.getSPEED() * 1.04f);
                if (backgroundScreen.getSPEED() > 40){
                    Color c = spriteBatch.getColor();
                    float alpha = 1-(backgroundScreen.getSPEED()-40)/50;
                    if(alpha < 0.1)alpha = 0;
                    spriteBatch.setColor(c.r, c.g, c.b, alpha);
                }
                if(backgroundScreen.getSPEED() > 90) {
                    gameClass.setScreen(new MenuSelectScreen(gameClass));
                }
            }
        }


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
    private void setInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                menuButtons.reset();
                menuButtons.isContains((int) (screenX * W_INDEX), (int) (800 - screenY * H_INDEX));
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                //buttons.dragged((int) (screenX * W_INDEX), (int) (480 - screenY * H_INDEX));

                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                menuButtons.reset();
                return true;
            }
        });
    }
}
