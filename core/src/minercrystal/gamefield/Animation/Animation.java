package minercrystal.gamefield.Animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class Animation {
    Array<Vector3> explosionAnimation;
    Array<Texture> explosionTextures;
    Array<Vector2> mineXY, backXY, frontXY, blocksXY;
    TextureRegion backTexture, growTexture;
    int Level;
    float HW, duration, intensity, elapsed, cameraSpeed = 2.5f, cameraMoveTo, BLOCK_HW = 80;
    boolean isShaking = false, isLose = false, isCameraMove = false, isLeft;
    BitmapFont font;
    public int Score = 0;
    Random random;
    OrthographicCamera camera;
    SpriteBatch batch;

    public Animation(int Level, boolean isLeft) {
        this.isLeft = isLeft;
        explosionAnimation = new Array<Vector3>();
        explosionTextures = new Array<Texture>();
        random = new Random();
        Texture map = new Texture("Maps/Map1/mapLevel12.png");
        growTexture = new TextureRegion(map, 0, 383, 64, 64);
        backTexture = new TextureRegion(map, 253, 318, 64, 64);
        this.Level = Level;
        HW = 300 / Level;
        camera = new OrthographicCamera();
        camera = new OrthographicCamera(480, 800);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
        batch = new SpriteBatch();
        frontXY = new Array<Vector2>();
        font = new BitmapFont();
        font.setColor(Color.RED);
        for (int i = 1; i <= 9; i++) {
            explosionTextures.add(new Texture("an" + String.valueOf(i) + ".png"));
        }
        backXY = new Array<Vector2>();
        mineXY = new Array<Vector2>();
        blocksXY = new Array<Vector2>();
        if (!isLeft) {
            cameraSpeed *= -1;
            BLOCK_HW *= -1;
        }
        spawnBG(isLeft);
        cameraMoveTo = camera.position.x;
    }

    public void animateBack(float delta) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Vector2 bXY : backXY) {
            batch.draw(backTexture, bXY.x, bXY.y, 80, 80);
        }
        batch.end();
    }

    public void animateFront(float delta) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Vector2 fXY : frontXY) {
            batch.draw(backTexture, fXY.x, fXY.y, 80, 80);
        }
        for (Vector2 mXY : mineXY) {
            batch.draw(growTexture, mXY.x, mXY.y, 80, 80);
        }
        for (Vector2 blXY : blocksXY) {
            batch.draw(backTexture, blXY.x, blXY.y, 80, 80);
        }
        for (Vector3 item : explosionAnimation) {
            batch.draw(explosionTextures.get((int) item.z - 1), item.x - 40 + camera.position.x - 240, item.y - 30, 180, 180);
            item.z -= 24 * delta;
            if (item.z < 1) explosionAnimation.removeValue(item, true);
        }
        if (isShaking) {
            isShaking = shakingUpdate(delta);
            if (!isShaking) camera.position.set(camera.position.x, camera.viewportHeight / 2f, 0);
            camera.update();
        } else if (isCameraMove) {
            moveCamera(delta);
            camera.update();
        }
        batch.end();
    }

    private void shake(float intensity, float duration) {
        this.elapsed = 0;
        this.duration = duration / 1000f;
        this.intensity = intensity;
    }

    public void setLose(boolean lose) {
        isLose = lose;
    }

    private boolean shakingUpdate(float delta) {

        // Only shake when required.
        if (elapsed < duration) {

            // Calculate the amount of shake based on how long it has been shaking already
            float currentPower = intensity * camera.zoom * ((duration - elapsed) / duration);
            float x = (random.nextFloat() - 0.5f) * 2 * currentPower;
            float y = (random.nextFloat() - 0.5f) * 2 * currentPower;
            camera.translate(-x, -y);

            // Increase the elapsed time by the delta provided.
            elapsed += delta;
            return true;
        }
        return false;
    }

    public void setShaking(boolean shaking) {
        isShaking = shaking;
        Gdx.input.vibrate(80);
        shake(8, 140);
        Score++;
    }

    public void addExplosionAnimation(float X, float Y) {
        explosionAnimation.add(new Vector3(X, Y, 9));
    }


    private void spawnBG(boolean isLeft) {
        float x = 40, y = 680;


        if (isLeft) {
            for (int i = 0; i < 57; i++) {
                mineXY.add(new Vector2(x, y));
                if (y <= 520) {
                    y = 680;
                    x += 80;
                } else
                    y -= 80;
            }
            x = 200;
            y = 680;
            for (int i = 0; i <= 53; i++) {
                blocksXY.add(new Vector2(x, y));
                if (y <= 520) {
                    y = 680;
                    x += 80;
                } else
                    y -= 80;
            }
            blocksXY.add(new Vector2(-40, 520));
            blocksXY.add(new Vector2(-40, 600));
            blocksXY.add(new Vector2(-40, 680));
            mineXY.add(new Vector2(40, 760));
            mineXY.add(new Vector2(120, 760));
            x = -40;
            y = -40;
            for (int i = 0; i <= 125; i++) {
                backXY.add(new Vector2(x, y));
                if (y >= 360) {
                    y = -40;
                    x += 80;
                } else {
                    y += 80;
                }
            }
            x = -40;
            y = 440;
            for (int i = 0; i < 105; i++) {
                frontXY.add(new Vector2(x, y));
                if (y >= 760) {
                    y = 440;
                    x += 80;
                } else {
                    y += 80;
                }
            }
        } else {
            x = 360;
            y = 680;
            for (int i = 0; i < 57; i++) {
                mineXY.add(new Vector2(x, y));
                if (y <= 520) {
                    y = 680;
                    x -= 80;
                } else
                    y -= 80;
            }

            x = 200;
            y = 680;
            for (int i = 0; i <= 53; i++) {
                blocksXY.add(new Vector2(x, y));
                if (y <= 520) {
                    y = 680;
                    x -= 80;
                } else
                    y -= 80;
            }
            blocksXY.add(new Vector2(440, 520));
            blocksXY.add(new Vector2(440, 600));
            blocksXY.add(new Vector2(440, 680));
            mineXY.add(new Vector2(360, 760));
            mineXY.add(new Vector2(280, 760));

            x = 440;
            y = 440;
            for (int i = 0; i <= 104; i++) {
                frontXY.add(new Vector2(x, y));
                if (y >= 760) {
                    y = 440;
                    x -= 80;
                } else {
                    y += 80;
                }
            }
            x = 440;
            y = -40;
            for (int i = 0; i < 126; i++) {
                backXY.add(new Vector2(x, y));
                if (y >= 360) {
                    y = -40;
                    x -= 80;
                } else {
                    y += 80;
                }
            }
        }
    }

    private void moveCamera(float delta) {
        if(blocksXY.size > 12) {
            if (isLeft) {
                if (camera.position.x >= cameraMoveTo)
                    isCameraMove = false;
                else {
                    camera.position.x = camera.position.x + cameraSpeed;
                }
            } else {
                if (camera.position.x <= cameraMoveTo)
                    isCameraMove = false;
                else {
                    camera.position.x = camera.position.x + cameraSpeed;
                }
            }
        }
    }

    public Vector2 destroyBlock() {
        if (blocksXY.first().y == 520) {
            cameraMoveTo += BLOCK_HW;
            isCameraMove = true;
        }
        Vector2 re = blocksXY.first();
        re.x-=camera.position.x-240;
        blocksXY.removeValue(blocksXY.first(), false);
        return re;
    }
}
