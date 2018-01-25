package run.mato.hepia.labo4.Model;

import android.graphics.RectF;

/**
 * Created by matoran on 1/6/18.
 */

public class Block {
    private float SIZE;
    private Type type = null;
    private RectF rectangle = null;
    private int row, column;
    public Block(Type type, int pX, int pY) {
        row = pY;
        column = pX;
        SIZE = Ball.RADIUS * 2;
        this.type = type;
        this.rectangle = new RectF(pX * SIZE, pY * SIZE, (pX + 1) * SIZE, (pY + 1) * SIZE);
    }

    public Type getType() {
        return type;
    }

    public RectF getRectangle() {
        return rectangle;
    }

    public enum Type {START, WALL, BONUS, MALUS, END}
}
