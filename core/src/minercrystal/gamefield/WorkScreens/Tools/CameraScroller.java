package minercrystal.gamefield.WorkScreens.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import minercrystal.gamefield.WorkScreens.PlayScreen;

public class CameraScroller {
    public static final String TAG = "SCROLL_TAG";
    public static final float TIME_TO_SCROLL = 2.5f;

    private OrthographicCamera mCamera;
    private final float mLowerPosition;
    private final float mUpperPosition;
    private float mTimer = 0;
    private float mVelocityY = -4;
    private float lastY = 0;
    private float selectorPos, moveTo;
    private boolean moveDown = false;
    public CameraScroller(OrthographicCamera camera, float lowerPosition, float upperPosition, float selectorPosition) {
        this.selectorPos = selectorPosition;
        mUpperPosition = upperPosition;
        mLowerPosition = lowerPosition;
        mCamera = camera;
        moveDown(selectorPos, camera.position.y);
    }

    /**
     * Call in Screen.render()
     */
    public void act(float deltaTime) {
        if (moveDown){
            float acceleration_y = mVelocityY * 0.01f;// calculate acceleration (the rate of change of velocity)
            mVelocityY -= acceleration_y;// decreasing velocity
            mCamera.position.y += mVelocityY;
            if (mCamera.position.y < moveTo) {
                moveDown = false;
                mTimer = TIME_TO_SCROLL;
            }
        }else
        if (mTimer > 0) {// if timer is not 0
            float acceleration_y = mVelocityY * 0.01f;// calculate acceleration (the rate of change of velocity)
            mTimer -= 0.01;// decreasing timer
            mVelocityY -= acceleration_y;// decreasing velocity
            mCamera.position.y += mVelocityY;
            checkCameraPosition();// check if camera position is in not less or more than some value else stop camera (mTimer = 0)
        }
    }

    private void moveDown(float selectorPos, float camPos) {
       moveDown = true;
       moveTo = selectorPos;
       mVelocityY = (selectorPos-camPos)/100;
    }

    private void checkCameraPosition() {
        if (mCamera.position.y > mUpperPosition) {
            mCamera.position.y = mUpperPosition;
            mTimer = 0;
        }
        if (mCamera.position.y < mLowerPosition) {
            mCamera.position.y = mLowerPosition;
            mTimer = 0;
        }
    }


    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float x = Gdx.input.getDeltaX();
        float y = Gdx.input.getDeltaY();
        mVelocityY = lastY / 2;
        lastY = y;
        mCamera.translate(0, y);
        if (mCamera.position.y > mUpperPosition) mCamera.position.y = mUpperPosition;
        if (mCamera.position.y < mLowerPosition) mCamera.position.y = mLowerPosition;
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        mTimer = TIME_TO_SCROLL;
        return true;
    }

}