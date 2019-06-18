package minercrystal.gamefield.WorkScreens.Tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class BackMenuSelectScreen {
    Texture skyTexture, bb, selectTexture;
    float skyY1 = 3550, skyY2 = 2750, SPEED = 0.04f, splashAccumulator = 1, splashTimer = 0.25f, velocity = 0.3f, mTimer = 0, dY;
    boolean isD, isSkyFirst = true, isCameraMove = false;
    SpriteBatch batch;
    OrthographicCamera camera;
    public static final float TIME_TO_SCROLL = 4f;

    public BackMenuSelectScreen(String Map, int Location, float yCamPos) {
        this.dY = yCamPos;
        skyTexture = new Texture("BackgroundAnimation/sky.png");
        bb = new Texture("bb.jpg");
        selectTexture = new Texture(Map + ".png");
        batch = new SpriteBatch();
        camera = new OrthographicCamera(480, 800);
        camera.position.set(camera.viewportWidth / 2f + 480 * Location, yCamPos, 0);
        camera.update();
    }

    public void renderBack(float delta) {

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, 1);
        batch.draw(skyTexture, camera.position.x - 240, skyY1, 480, 800);
        batch.draw(skyTexture, camera.position.x - 240, skyY2, 480, 800);
        batch.end();
    }

    public void renderFront(float delta, SpriteBatch spriteBatch, Rectangle select, OrthographicCamera camera) {
        spriteBatch.setProjectionMatrix(camera.combined);
        Color c = spriteBatch.getColor();
        spriteBatch.setColor(c.r, c.g, c.b, 1);
        spriteBatch.begin();
        spriteBatch.draw(selectTexture, select.getX(), select.getY(), select.getWidth(), select.getHeight());
        spriteBatch.end();
        if (!isD) {
            setAlpha(delta, spriteBatch);
            spriteBatch.begin();
            spriteBatch.draw(bb, camera.position.x - 240, camera.position.y - 400, 480, 800);
            spriteBatch.end();
        }
    }

    public void update(float delta) {
        skyY1 += SPEED;
        skyY2 += SPEED;
        if (isSkyFirst) {
            if (skyY1 > 4300) {
                skyY1 = 2700;
                isSkyFirst = false;
            }
        } else {
            if (skyY2 > 4300) {
                skyY2 = 2700;
                isSkyFirst = true;
            }
        }

    }

    public void cameraScroll(OrthographicCamera camera1, float deltaTime) {
        if (isCameraMove) {

            float acceleration_y = velocity * deltaTime;// calculate acceleration (the rate of change of velocity)
            mTimer -= deltaTime;// decreasing timer
            velocity -= acceleration_y;// decreasing velocity
            camera.position.y += velocity;

            camera.update();
        }
        if (camera.position.y > camera1.position.y - 2 && camera.position.y < camera1.position.y + 2)
            isCameraMove = false;
        else {
            isCameraMove = true;
            mTimer = TIME_TO_SCROLL;
            velocity = (camera1.position.y - camera.position.y);
        }


    }

    public void tochDown() {
        //velocity = 2.0f;
    }

    private void setAlpha(float delta, SpriteBatch spriteBatch) {
        splashAccumulator -= splashTimer * delta;
        Color c = spriteBatch.getColor();
        spriteBatch.setColor(c.r, c.g, c.b, splashAccumulator);
        if (splashAccumulator < 0.05) {
            splashAccumulator = 1;
            isD = true;
            spriteBatch.setColor(c.r, c.g, c.b, 0);

        }
    }
}
