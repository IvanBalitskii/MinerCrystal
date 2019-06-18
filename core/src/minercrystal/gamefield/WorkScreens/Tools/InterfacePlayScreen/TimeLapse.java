package minercrystal.gamefield.WorkScreens.Tools.InterfacePlayScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;


public class TimeLapse {
    private float splashTimer = 1.60f, Speed, HealthSpeed = 15f, DeltaFrames = 0.55f,
            FramesAccumulator, AnimLong = 0.38f, AnimAccumulator, IndexSize = 0.35f;
    private Texture timeLapseFrame, red, star;
    private Rectangle timeLapse, redLine, starRect1, starRect2, starRect3;
    private Array<Rectangle> animFrames;
    private Array<Float> splashFrames;
    private boolean isMaxAnimate = false, isLose = false;
    private int starsCount = 3;

    public TimeLapse(float Speed) {
        this.Speed = Speed;
        animFrames = new Array<Rectangle>();
        splashFrames = new Array<Float>();
        timeLapseFrame = new Texture("tools/timelapseframe.png");
        red = new Texture("tools/red.png");
        star = new Texture("tools/star.png");
        timeLapse = new Rectangle(250, 770, 220, 22);
        redLine = new Rectangle(timeLapse.x + 5, timeLapse.y + 3, timeLapse.width - 8, timeLapse.height - 5);
        starRect1 = new Rectangle(timeLapse.x + 10, timeLapse.y + 3, timeLapse.height / 2, timeLapse.height - 5);
        starRect2 = new Rectangle(timeLapse.x + timeLapse.width / 3, timeLapse.y + 3, timeLapse.height / 2, timeLapse.height - 5);
        starRect3 = new Rectangle(timeLapse.x + timeLapse.width / 2, timeLapse.y + 3, timeLapse.height / 2, timeLapse.height - 5);

    }

    public void render(SpriteBatch spriteBatch, float delta) {
        if (redLine.width > 1)
            redLine.width -= Speed * delta;
        else{
            starsCount = 0;
            isLose = true;
        }
        if (redLine.x + redLine.width > starRect3.x) starsCount = 3;
        else if (redLine.x + redLine.width > starRect2.x) starsCount = 2;
        else if (redLine.x + redLine.height > starRect1.x) starsCount = 1;
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
        spriteBatch.draw(red, redLine.x, redLine.y, redLine.width, redLine.height);
        if (starsCount >= 1)
            spriteBatch.draw(star, starRect1.x, starRect1.y, starRect1.width, starRect1.height);
        if (starsCount >= 2)
            spriteBatch.draw(star, starRect2.x, starRect3.y, starRect1.width, starRect1.height);
        if (starsCount == 3)
            spriteBatch.draw(star, starRect3.x, starRect3.y, starRect1.width, starRect1.height);
        spriteBatch.draw(timeLapseFrame, timeLapse.x, timeLapse.y, timeLapse.width, timeLapse.height);

    }

    public boolean plus() {
        if (redLine.width + HealthSpeed < timeLapse.width - 8) {
            redLine.width += HealthSpeed;
            return false;
        } else {
            if (!isMaxAnimate)
                isMaxAnimate = true;
            redLine.width = timeLapse.width - 8;
            addAnimFrame();
            return true;
        }
    }

    private void drawAnimFrame(float delta, SpriteBatch spriteBatch, Rectangle r) {
        splashFrames.set(animFrames.indexOf(r, false), splashFrames.get(animFrames.indexOf(r, false)) - splashTimer * delta);
        Color c = spriteBatch.getColor();
        spriteBatch.end();
        spriteBatch.setColor(c.r, c.g, c.b, splashFrames.get(animFrames.indexOf(r, false)));

        spriteBatch.begin();
        spriteBatch.draw(timeLapseFrame, r.x, r.y, r.width, r.height);
        spriteBatch.end();
        spriteBatch.begin();
        if (splashFrames.get(animFrames.indexOf(r, false)) < 0) {
            splashFrames.removeValue(splashFrames.get(animFrames.indexOf(r, false)), false);
            animFrames.removeValue(r, false);
        }
        spriteBatch.setColor(c.r, c.g, c.b, 1);
    }

    private void addAnimFrame() {
        animFrames.add(new Rectangle(timeLapse.x, timeLapse.y, timeLapse.width, timeLapse.height));
        splashFrames.add(1f);
    }

    public int getStarsCount() {
        return starsCount;
    }

    public boolean isLose() {
        return isLose;
    }
}
