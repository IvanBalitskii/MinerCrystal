package minercrystal.gamefield;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import minercrystal.gamefield.Objects.Item;


public class MovingHandler {
    private boolean Destroy = false;
    Array<Array<Item>> Items;
    int SameX = 1, SameY = 1, count = 0;
    private Array<Vector2> theSame;
    public Array<Vector2> toDestroy;
    Array<Integer> Xs, Ys;

    public MovingHandler() {
        theSame = new Array<Vector2>();
        toDestroy = new Array<Vector2>();
        Xs = new Array<Integer>();
        Ys = new Array<Integer>();
    }


    public boolean destroyIfExistTheSame(int SX1, int SY1, int SX2, int SY2, Array<Array<Item>> Items) {

        this.Items = Items;
        theSame.clear();
        Xs.clear();
        Ys.clear();

        if (SX1 == SX2 && SY1 == SY2) {
            isExistTheSame(SX2, SY2, Items.get(SX1).get(SY1).getMODE());

        }else{
            theSame.add(new Vector2(SX1, SY1));
            isExistTheSame(SX2, SY2, Items.get(SX1).get(SY1).getMODE());
            theSame.removeValue(new Vector2(SX1, SY1), false);
        }

        isNeedToDestroy();

        if (Destroy) sortTheSame();

        return Destroy;
    }

    private Array<Vector2> isExistTheSame(int IndexX, int IndexY, int MODE) {

        boolean isAlreadyAdded = false;
        for (Vector2 elementXY : theSame)
            if (elementXY.x == IndexX && elementXY.y == IndexY)
                isAlreadyAdded = true;

        if (!isAlreadyAdded) {

            theSame.add(new Vector2(IndexX, IndexY));

            if (IndexY > 0)
                if (MODE == Items.get(IndexX).get(IndexY - 1).getMODE())
                    isExistTheSame(IndexX, IndexY - 1, MODE);

            if (IndexY < Items.get(IndexX).size - 1)
                if (MODE == Items.get(IndexX).get(IndexY + 1).getMODE())
                    isExistTheSame(IndexX, IndexY + 1, MODE);

            if (IndexX > 0)
                if (MODE == Items.get(IndexX - 1).get(IndexY).getMODE())
                    isExistTheSame(IndexX - 1, IndexY, MODE);

            if (IndexX < Items.get(IndexX).size - 1)
                if (MODE == Items.get(IndexX + 1).get(IndexY).getMODE())
                    isExistTheSame(IndexX + 1, IndexY, MODE);
        }

        return theSame;
    }

    private void isNeedToDestroy() {
        SameX = 0;
        SameY = 0;
        count = 0;
        Xs.clear();
        Ys.clear();
        sortByXY();
        if (theSame.size > 2) {
            for (int i = 0; i < Xs.size - 1; i++) {
                if (Xs.get(i) == Xs.get(i + 1)) {
                    SameX++;
                    if (SameX >= 2) {
                        Destroy = true;
                        toDestroy.addAll(theSame);
                        break;
                    }
                } else {
                    SameX = 0;
                }
            }
            for (int i = 0; i < Ys.size - 1; i++) {
                if (Ys.get(i) == Ys.get(i + 1)) {
                    SameY++;
                    if (SameY >= 2) {
                        Destroy = true;
                        toDestroy.addAll(theSame);
                        break;
                    }
                } else {
                    SameY = 0;
                }
            }
        }
        theSame.clear();
    }

    private void sortByXY() {
        for (Vector2 item : theSame) {
            Xs.add((int) item.x);
            Ys.add((int) item.y);
        }
        Xs.sort();
        Ys.sort();
    }

    private void sortTheSame() {
        boolean isSort = false;
        while (!isSort) {
            isSort = true;
            for (int i = 0; i < theSame.size - 1; i++) {
                if (theSame.get(i).y < theSame.get(i + 1).y) {
                    isSort = false;
                    theSame.swap(i, i + 1);
                }
            }
        }
    }

    public boolean isNeedDestroy() {
        return Destroy;
    }

    public void setNeedDestroy(boolean needDestroy) {
        Destroy = needDestroy;
        toDestroy.clear();
    }
}
