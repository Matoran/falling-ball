package run.mato.hepia.labo4.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import run.mato.hepia.labo4.Controller.GameEngine;
import run.mato.hepia.labo4.Model.Ball;
import run.mato.hepia.labo4.Model.Block;

/**
 * Created by matoran on 1/6/18.
 */

public class GameRenderer extends SurfaceView implements SurfaceHolder.Callback {
    private final GameEngine gameEngine;
    Ball ball;
    SurfaceHolder surfaceHolder;
    DrawingThread thread;
    Paint paint;
    private List<Block> blocks = null;
    private float multiplicator;
    private boolean initialized = false;

    public GameRenderer(Context context, GameEngine gameEngine) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        thread = new DrawingThread();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        this.gameEngine = gameEngine;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(canvas == null)
            return;
        // Dessiner le fond de l'écran en premier
        canvas.drawColor(Color.BLACK);
        if (blocks != null) {
            // Dessiner tous les blocs du labyrinthe
            for (Block b : blocks) {
                switch (b.getType()) {
                    case START:
                        paint.setColor(Color.BLACK);
                        break;
                    case END:
                        paint.setColor(Color.RED);
                        break;
                    case WALL:
                        paint.setColor(Color.WHITE);
                        break;
                }
                canvas.drawRect(b.getRectangle(), paint);
            }
        }

        // Dessiner la Ball
        if (ball != null) {
            paint.setColor(ball.getColor());
            canvas.drawCircle(ball.getX(), ball.getY(), Ball.RADIUS, paint);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.keepDrawing = true;
        thread.start();
        // Quand on crée la Ball, on lui indique les coordonnées de l'écran
        float ratio = (float)getWidth() / getHeight();
        System.out.println("width:" + getWidth());
        System.out.println("height:" + getHeight());
        System.out.println("ratio:" + (float)getWidth() / getHeight());
        System.out.println("9.0 / 16:" + 9.0f / 16);
        if (ratio <= 9.0f / 16) {
            multiplicator = (float)getWidth() / GameEngine.COLUMNS;
        } else if (ratio > 9.0f / 16){
            System.out.println("ratio plus grand");
            multiplicator = (float)getHeight() / GameEngine.ROWS;
        }
        System.out.println("multiplicator: "  + multiplicator);
        if (ball != null) {
            this.ball.setHeight(getHeight());
            this.ball.setWidth(getWidth());
        }
        gameEngine.surfaceCreated();
        blocks = gameEngine.getBlocks();
        ball = gameEngine.getBall();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.keepDrawing = false;
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }

    }

    public float getMultiplicator() {
        return multiplicator;
    }

    private class DrawingThread extends Thread {
        boolean keepDrawing = true;

        @Override
        public void run() {
            Canvas canvas;
            while (keepDrawing) {
                canvas = null;

                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        onDraw(canvas);
                    }
                } finally {
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }

                // Pour dessiner à 50 fps
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
