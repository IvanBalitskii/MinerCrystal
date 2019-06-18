package minercrystal.gamefield.WorkScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;


import minercrystal.gamefield.GameClass;
import minercrystal.gamefield.SpinWheel.SpinWheel;
import minercrystal.gamefield.WorkScreens.Tools.BackMenuSelectScreen;
import minercrystal.gamefield.WorkScreens.Tools.CameraScroller;

public class MenuSelectScreen implements Screen {
    GameClass gameClass;
    SpriteBatch spriteBatch;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    OrthographicCamera camera;
    float W_INDEX, H_INDEX;
    int[] backgroundLayers = {0}; // don't allocate every frame!
    int[] foregroundLayers;    // don't allocate every frame!
    float firstX, firstY, selectR = 400, selectL = 27, DELTA_Y_SELECT = 80, last;
    Rectangle select;
    int Location, Level;
    boolean isLeft;
    Preferences mainPrefs;
    BackMenuSelectScreen backMenuSelectScreen;
    CameraScroller cameraScroller;
    SpinWheel spinWheel;
    public MenuSelectScreen(GameClass gameClass) {
        this.gameClass = gameClass;
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera(480, 800);
        camera.position.set(camera.viewportWidth / 2f + 480 * Location, 3299, 0);
        camera.update();
        W_INDEX = (float) (480.0 / Gdx.graphics.getWidth());
        H_INDEX = (float) (800.0 / Gdx.graphics.getHeight());
        spinWheel = new SpinWheel(480, 800, 380, 240, 400, 6);
        mainPrefs = Gdx.app.getPreferences("MainPreferences");
        //
        //createPrefs(mainPrefs);
        map = new TmxMapLoader().load("Maps/Map" + String.valueOf(mainPrefs.getInteger("Map", 1)) + "/map.tmx");
        Level = mainPrefs.getInteger("Level", 0);
        initSelectRectangle();
        cameraScroller = new CameraScroller(camera, 0 + 400, 3300, select.y);
        backMenuSelectScreen = new BackMenuSelectScreen(String.valueOf(mainPrefs.getInteger("Map", 1)), Location, select.y);

        renderer = new OrthogonalTiledMapRenderer(map, 1 / 2.4f);
        setInputProcessor();
        foregroundLayers = new int[Level * 2 + 1];
        for (int i = 1; i < Level * 2 + 1; i++) {
            foregroundLayers[i] = i;
        }

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.setProjectionMatrix(camera.combined);
        backMenuSelectScreen.renderBack(delta);
        cameraScroller.act(delta);
        backMenuSelectScreen.update(delta);
        camera.update();
        backGroundRender(camera);
        frontGroundRender(camera);
        backMenuSelectScreen.cameraScroll(camera, delta);
        backMenuSelectScreen.renderFront(delta, spriteBatch, select, camera);
        //spinWheel.render(true);

    }

    private void update(float delta) {

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

    public void backGroundRender(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render(backgroundLayers);
    }

    public void frontGroundRender(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render(foregroundLayers);
    }

    private void setInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                //spinWheel.spin(20);
                backMenuSelectScreen.tochDown();
                firstX = screenX;
                firstY = screenY;
                if (select.contains(screenX * W_INDEX, camera.position.y + 400 - screenY * H_INDEX))
                    gameClass.setScreen(new PlayScreen(gameClass, mainPrefs, isLeft));
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (Gdx.input.getDeltaY() >= 0 && last <= 0 || Gdx.input.getDeltaY() <= 0 && last >= 0)
                    backMenuSelectScreen.tochDown();
                last = Gdx.input.getDeltaY();
                cameraScroller.touchDragged(screenX, screenY, pointer);
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                cameraScroller.touchUp(screenX, screenY, pointer, button);
                return true;
            }
        });
    }

    private void initSelectRectangle() {
        if (Level == 0) {
            select = new Rectangle(322, 3413, 53, 54);
            isLeft = false;
        } else {
            if (Level % 2 == 0) {
                select = new Rectangle(selectR, 3413 - DELTA_Y_SELECT * Level, 53, 54);
                isLeft = false;
            } else {
                select = new Rectangle(selectL, 3413 - DELTA_Y_SELECT * Level, 53, 54);
                isLeft = true;
            }
        }

    }

    private void createPrefs(Preferences preferences) {
        preferences.putInteger("Map", 1);
        preferences.putInteger("Level", 0);
        preferences.flush();
    }
}
