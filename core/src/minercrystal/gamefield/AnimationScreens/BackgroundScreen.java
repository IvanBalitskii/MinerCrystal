package minercrystal.gamefield.AnimationScreens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Random;


public class BackgroundScreen {
    float Y1 = 0, Y2 = -800, SPEED = 0.5f, rast = 100, accumulator = 0;
    Texture bgTexture;
    Array<Texture> torchAnimate;
    Array<Vector3> YTorch1, YTorch2;
    boolean isFirst = true;
    int XTorch1 = 100, XTorch2 = 300;
    Random r;
    public BackgroundScreen() {
        bgTexture = new Texture("BackgroundAnimation/BKMenu.png");
        r = new Random();
        YTorch1 = new Array<Vector3>();
        YTorch2 = new Array<Vector3>();

        torchAnimate = new Array<Texture>();
        YTorch1.add(new Vector3(r.nextInt(45) + XTorch1, -220, r.nextInt(8)));
        YTorch2.add(new Vector3(r.nextInt(50) + XTorch2, -220, r.nextInt(8)));

        for (int i = 1; i < 11; i++) {
            YTorch1.add(new Vector3(r.nextInt(45) + XTorch1, YTorch1.get(i - 1).y + rast, r.nextInt(8)));
            YTorch2.add(new Vector3(r.nextInt(50) + XTorch2, YTorch2.get(i - 1).y + rast, r.nextInt(8)));

        }
        for (int i = 1; i < 9; i++) {
            torchAnimate.add(new Texture("TorchAnimation/" + String.valueOf(i) + ".png"));
        }
    }

    public void render(float delta, SpriteBatch spriteBatch) {

        accumulator += delta;
        if (accumulator > 0.08) {
            for (Vector3 torch : YTorch1) {
                torch.z++;
                if (torch.z > 7) torch.z = 0;
            }
            for (Vector3 torch : YTorch2) {
                torch.z++;
                if (torch.z > 7) torch.z = 0;
            }
            accumulator = 0;
        }
        spriteBatch.draw(bgTexture, 0, Y1, 480, 800);
        spriteBatch.draw(bgTexture, 0, Y2, 480, 800);
        for (int i = 0; i < 10; i++) {
            spriteBatch.draw(torchAnimate.get((int) YTorch1.get(i).z), YTorch1.get(i).x, YTorch1.get(i).y, 47, 86);
            spriteBatch.draw(torchAnimate.get((int) YTorch2.get(i).z), YTorch2.get(i).x, YTorch2.get(i).y, 47, 86);
        }
        Y1 += SPEED;
        Y2 += SPEED;
        for (Vector3 torch : YTorch1) {
            torch.y += SPEED;
            if (torch.y -86>= 800) {
                float y = YTorch1.first().y - rast;
                Vector3 vector31 = new Vector3();
                Vector3 vector32 = new Vector3();
                vector31.set(YTorch1.get(0));
                vector32.set(YTorch1.get(1));
                for (int i = 1; i < 10; i++){
                    YTorch1.get(i).set(vector31);
                    vector31.set(vector32);
                    vector32.set(YTorch1.get(i+1));
                }
                YTorch1.set(YTorch1.size-1, vector31);
                YTorch1.get(0).y = y;
                YTorch1.get(0).x = r.nextInt(45) + XTorch1;
                YTorch1.get(0).z = r.nextInt(8);
            }
        }
        for (Vector3 torch : YTorch2) {
            torch.y += SPEED;
            if (torch.y - 86>= 800) {
                float y = YTorch2.first().y - rast;
                Vector3 vector31 = new Vector3();
                Vector3 vector32 = new Vector3();
                vector31.set(YTorch2.get(0));
                vector32.set(YTorch2.get(1));
                for (int i = 1; i < 10; i++){
                    YTorch2.get(i).set(vector31);
                    vector31.set(vector32);
                    vector32.set(YTorch2.get(i+1));
                }
                YTorch2.set(YTorch2.size-1, vector31);
                YTorch2.get(0).y = y;
                YTorch2.get(0).x = r.nextInt(60) + XTorch2;
                YTorch2.get(0).z = r.nextInt(8);
            }
        }
        if (isFirst && Y1 >= 800) {
            isFirst = false;
            Y1 = -800;
        } else if (Y2 >= 800) {
            isFirst = true;
            Y2 = -800;
        }
    }

    public void setSPEED(float SPEED) {
        this.SPEED = SPEED;
    }

    public float getSPEED() {
        return SPEED;
    }
}
