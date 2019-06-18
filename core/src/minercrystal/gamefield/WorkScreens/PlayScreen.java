package minercrystal.gamefield.WorkScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

import minercrystal.gamefield.Animation.Animation;
import minercrystal.gamefield.Animation.AnimationItem;
import minercrystal.gamefield.GameClass;
import minercrystal.gamefield.MovingHandler;
import minercrystal.gamefield.Objects.Item;
import minercrystal.gamefield.WorkScreens.Tools.InterfacePlayScreen.Coins;
import minercrystal.gamefield.WorkScreens.Tools.InterfacePlayScreen.TimeLapse;

public class PlayScreen implements Screen {

    SpriteBatch batch;
    int touchX, touchY, HW_ITEM = 66, ITEM_ARRAY_SIZE = 0, MOTION_DIRECTION, count, Mode, BlocksCount = 51;
    boolean isMOTION_DIRECTION, destroy = false, check = false;
    Array<Array<Item>> Items;
    Array<Array<Vector2>> Matrix;
    OrthographicCamera camera;
    float W_INDEX, H_INDEX, nX, nY;
    int MATRIX_SIZE = 6, LEVEL = 4;
    double SPEED = 2;
    public static final int MODES_SIZE = 4;
    MovingHandler handler;
    Animation animation;
    AnimationItem animationItem;
    Random random;
    final GameClass game;
    Array<Integer> Exclude;
    Preferences preferences;
    TimeLapse timeLapse;
    Coins coins;
    public PlayScreen(final GameClass game, Preferences preferences, boolean isLeft) {
        this.game = game;
        this.preferences = preferences;
        coins = new Coins(preferences.getInteger("Crystals", 0), preferences.getInteger("Coins", 0));
        timeLapse = new TimeLapse(4.5f);
        Exclude = new Array<Integer>();
        W_INDEX = (float) (480.0 / Gdx.graphics.getWidth());
        H_INDEX = (float) (800.0 / Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        Items = new Array<Array<Item>>();
        Matrix = new Array<Array<Vector2>>();
        camera = new OrthographicCamera(480, 800);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        handler = new MovingHandler();
        animation = new Animation(LEVEL, isLeft);
        camera.update();
        random = new Random();

        for (int i = 0; i < MATRIX_SIZE; i++) {
            Array<Vector2> objects = new Array<Vector2>();
            Items.add(new Array<Item>());
            for (int i2 = 0; i2 < MATRIX_SIZE; i2++) {
                Vector2 vector2 = new Vector2(40.33f + i * HW_ITEM, 40.33f + i2 * HW_ITEM);
                objects.add(vector2);
            }
            Matrix.add(objects);
        }
        animationItem = new AnimationItem(MODES_SIZE, Matrix, handler, SPEED);
        spawnNew();
        setInputPr();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update();

        batch.setProjectionMatrix(camera.combined);
        animation.animateBack(delta);
        batch.begin();
        animationItem.render(batch, Items);

        batch.end();
        animation.animateFront(delta);
        batch.begin();
        coins.render(batch, delta);
        timeLapse.render(batch, delta);
        batch.end();

    }


    void update() {
        if (handler.isNeedDestroy()) {

            count = 0;
            destroy = true;
            while (destroy) {
                DestroyItems();
                count++;
                if (count == 2) {
                    animation.setShaking(true);
                    timeLapse.plus();
                    Vector2 block = animation.destroyBlock();
                    coins.addCoin(15, block.x, block.y, 80);

                    if (animation.Score >= BlocksCount) {
                        for (Array<Item> x : Items) {
                            for (Item y : x) {
                                y.setNeedToDestroy(true);
                            }
                        }
                        DestroyItems();
                        preferences.putInteger("Level", preferences.getInteger("Level")+1);
                        preferences.flush();
                        game.setScreen(new MenuSelectScreen(game));
                       animation.destroyBlock();
                        animation.Score = 0;
                    }
                    count = 0;
                }
            }
            for (Array<Item> x : Items) {
                for (Item y : x) {
                    y.setTouch(false);
                    y.setMoveTo((int) Matrix.get(Items.indexOf(x, false)).get(x.indexOf(y, false)).x, (int) Matrix.get(Items.indexOf(x, false)).get(x.indexOf(y, false)).y, true, Items.indexOf(x, false), x.indexOf(y, false), SPEED * (MATRIX_SIZE - x.indexOf(y, false)));
                }
            }
            spawnNew();
            count = 0;
            handler.setNeedDestroy(false);
        }

    }

    void spawnNew() {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            ITEM_ARRAY_SIZE = Items.get(i).size;
            for (int i2 = ITEM_ARRAY_SIZE; i2 < MATRIX_SIZE; i2++) {
                if (i2 == ITEM_ARRAY_SIZE) {
                    Item item = new Item(Matrix.get(i).get(i2).x, Matrix.get(i).get(MATRIX_SIZE - 1).y + HW_ITEM, HW_ITEM, HW_ITEM);
                    setMode(item, i, i2);
                    item.setMoveTo((int) Matrix.get(i).get(i2).x, (int) Matrix.get(i).get(i2).y, true, i, i2, SPEED * (MATRIX_SIZE - i2 + 2));
                    Items.get(i).insert(i2, item);

                } else {
                    Item item = new Item(Matrix.get(i).get(i2).x, Items.get(i).get(i2 - 1).getRectangle().y + HW_ITEM, HW_ITEM, HW_ITEM);
                    setMode(item, i, i2);
                    item.setMoveTo((int) Matrix.get(i).get(i2).x, (int) Matrix.get(i).get(i2).y, true, i, i2, SPEED * (MATRIX_SIZE - i2 + 2));
                    Items.get(i).insert(i2, item);

                }
            }
        }
    }

    private void setMode(Item item, int X, int Y) {
        check = false;
        Exclude.clear();
        Mode = 0;
        while (!check) {
            Exclude.sort();
            Mode = getRandomWithExclusion(random, 1, 4, Exclude);
            check = true;
            if (X > 1)
                if (Mode == Items.get(X - 1).get(Y).getMODE() && Mode == Items.get(X - 2).get(Y).getMODE()) {
                    check = false;
                    Exclude.add(Mode);
                }
            if (Y > 1 && check)
                if (Mode == Items.get(X).get(Y - 1).getMODE() && Mode == Items.get(X).get(Y - 2).getMODE()) {
                    Exclude.add(Mode);
                    check = false;
                }

            if (Y > 1 && X > 1 && check)
                if (Mode == Items.get(X).get(Y - 1).getMODE() && Mode == Items.get(X - 1).get(Y - 1).getMODE()
                        && item.getMODE() == Items.get(X - 1).get(Y).getMODE()) {
                    Exclude.add(Mode);
                    check = true;
                }

            if (X < Items.get(X).size && Y < Items.get(X).size && check)
                if (Mode == Items.get(X + 1).get(Y).getMODE() && Mode == Items.get(X + 2).get(Y).getMODE()) {
                    Exclude.add(Mode);
                    check = false;
                }

            if (X < Items.get(X).size && Y < Items.get(X).size && check)
                if (Mode == Items.get(X).get(Y + 1).getMODE() && Mode == Items.get(X).get(Y + 2).getMODE()) {
                    Exclude.add(Mode);
                    check = false;
                }

            if (Y < Items.get(X).size && X < Items.get(X).size && check)
                if (Mode == Items.get(X).get(Y + 1).getMODE() && Mode == Items.get(X + 1).get(Y + 1).getMODE()
                        && item.getMODE() == Items.get(X + 1).get(Y).getMODE()) {
                    Exclude.add(Mode);
                    check = true;
                }
        }
        item.setMODE(Mode);
    }

    private int getRandomWithExclusion(Random rnd, int start, int end, Array<Integer> exclude) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.size);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }

    public void defineDirection(int screenX, int screenY, int selectedX, int selectedY) {
        nX = screenX * W_INDEX;
        nY = (800 - screenY * H_INDEX);
        if (!isMOTION_DIRECTION && (Math.abs(nX - touchX) > 3 || Math.abs(nY - touchY) > 3) && selectedX >= 0) {
            if ((Math.abs(nX - touchX) < Math.abs(nY - touchY))) {
                if (touchY - nY > 0) {
                    if (selectedY > 0) {
                        MOTION_DIRECTION = 0; //Y---
                        Items.get(selectedX).get(selectedY).setTouch(true);
                        Items.get(selectedX).get(selectedY - 1).setTouch(true);
                        isMOTION_DIRECTION = true;
                    }
                } else {
                    if (selectedY < MATRIX_SIZE - 1) {
                        MOTION_DIRECTION = 1; //Y+++
                        Items.get(selectedX).get(selectedY).setTouch(true);
                        Items.get(selectedX).get(selectedY + 1).setTouch(true);
                        isMOTION_DIRECTION = true;
                    }
                }
            } else {
                if (touchX - nX > 0) {
                    if (selectedX > 0) {
                        MOTION_DIRECTION = 2; //X---
                        Items.get(selectedX).get(selectedY).setTouch(true);
                        Items.get(selectedX - 1).get(selectedY).setTouch(true);
                        isMOTION_DIRECTION = true;
                    }
                } else {
                    if (selectedX < MATRIX_SIZE - 1) {
                        MOTION_DIRECTION = 3; //X+++
                        Items.get(selectedX).get(selectedY).setTouch(true);
                        Items.get(selectedX + 1).get(selectedY).setTouch(true);
                        isMOTION_DIRECTION = true;
                    }
                }
            }


        } else if (selectedX >= 0 && isMOTION_DIRECTION) {
            animationItem.itemMoving(MOTION_DIRECTION, touchX - nX, touchY - nY, selectedX, selectedY, Items, Matrix);

        }
    }

    void DestroyItems() {
        destroy = false;
        for (Array<Item> x : Items) {
            for (Item y : x) {
                if (y.isNeedToDestroy()) {
                    animation.addExplosionAnimation(y.getRectangle().getX(), y.getRectangle().getY());
                    x.removeValue(y, false);
                    destroy = true;
                }
            }
        }
    }
    private void setInputPr(){
        Gdx.input.setInputProcessor(new InputAdapter() {
            int selectedX = -1, selectedY = -1;

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                selectedX = -1;
                selectedY = -1;
                touchX = (int) (screenX * W_INDEX);
                touchY = (int) (800 - screenY * H_INDEX);
                for (int i = 0; i < Items.size; i++)
                    for (int i2 = 0; i2 < Items.get(i).size; i2++)
                        if (Items.get(i).get(i2).isContains(touchX, touchY) && !animationItem.isMoving()) {
                            selectedX = i;
                            selectedY = i2;
                        }
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                defineDirection(screenX, screenY, selectedX, selectedY);
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                isMOTION_DIRECTION = false;
                MOTION_DIRECTION = -1;
                animationItem.setTouchUp(Items);
                return true;
            }
        });
    }
    @Override
    public void show() {

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
        batch.dispose();
    }
}