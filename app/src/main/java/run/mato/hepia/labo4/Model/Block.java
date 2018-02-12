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
    private int score = 0;

    public Block(Type type, int row, int column, int score) {
        this(type, row, column);
        this.score = score;
    }

    public Block(Type type, int pX, int pY) {
        row = pY;
        column = pX;
        SIZE = Ball.RADIUS * 2;
        this.type = type;
        if (type == Type.PLATFORM) {
            this.rectangle = new RectF(pX * SIZE, pY * SIZE + SIZE / 2, (pX + 1) * SIZE, pY * SIZE + SIZE / 2 + 1);
        } else if (type == Type.WALL) {
            this.rectangle = new RectF(pX * SIZE + SIZE / 2, pY * SIZE, pX * SIZE + SIZE / 2 + 1, (pY + 1) * SIZE);
        } else {
            this.rectangle = new RectF(pX * SIZE, pY * SIZE, (pX + 1) * SIZE, (pY + 1) * SIZE);
        }
    }

    public Type getType() {
        return type;
    }

    public RectF getRectangle() {
        return rectangle;
    }

    public int getScore() {
        return score;
    }

    public enum Type {START, PLATFORM, WALL, BONUS, MALUS, END, BORDER}
}
