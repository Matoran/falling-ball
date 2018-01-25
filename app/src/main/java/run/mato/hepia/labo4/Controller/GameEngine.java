package run.mato.hepia.labo4.Controller;

import android.app.Service;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import run.mato.hepia.labo4.GameActivity;
import run.mato.hepia.labo4.Model.Ball;
import run.mato.hepia.labo4.Model.Block;
import run.mato.hepia.labo4.Model.Block.Type;
import run.mato.hepia.labo4.Model.Difficulty;
import run.mato.hepia.labo4.View.GameRenderer;

/**
 * Created by matoran on 1/6/18.
 */

public class GameEngine {
    public static final int ROWS = 32;
    public static final int COLUMNS = 18;
    private final Difficulty difficulty;
    private final int POURCENTAGE_BONUS = 1;
    boolean pause = false;
    private float y = 9.8f;
    private float x = 0;
    private float timeLeft = 10;
    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent pEvent) {
            x = -pEvent.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor pSensor, int pAccuracy) {

        }
    };
    private Ball ball = null;
    // Le labyrinthe
    private List<Block> blocks = new ArrayList<>();
    private GameActivity mActivity = null;
    private SensorManager sensorManager = null;
    private Sensor accelerometer = null;
    private float multiplicator;
    private GameRenderer view;
    private boolean initialized = false;
    private int score = 0;
    private long lastTime;

    public GameEngine(GameActivity gameActivity, Difficulty difficulty) {
        this.difficulty = difficulty;
        this.multiplicator = multiplicator;
        mActivity = gameActivity;
        sensorManager = (SensorManager) mActivity.getBaseContext().getSystemService(Service.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        view = new GameRenderer(gameActivity, this);
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    // Remet à zéro l'emplacement de la Ball
    public void reset() {
        ball.reset();
    }

    // Arrête le capteur
    public void stop() {
        sensorManager.unregisterListener(mSensorEventListener, accelerometer);
        pause = true;
    }

    // Redémarre le capteur
    public void resume() {
        sensorManager.registerListener(mSensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        lastTime = System.currentTimeMillis();
        pause = false;
    }

    // Construit le labyrinthe
    public List<Block> buildLabyrinthe() {
        blocks.clear();
        Random random = new Random();
        Block start = new Block(Type.START, 2, 2);
        ball.setInitialRectangle(start.getRectangle());
        blocks.add(start);
        boolean previousEmpty = false;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (column == 0 || column == COLUMNS - 1 || row == 0 || row == ROWS - 1) {
                    blocks.add(new Block(Type.WALL, column, row));
                } else {
                    if ((row + 1) % 4 == 0 && !previousEmpty && row != 28) {
                        int rand = random.nextInt(100 / POURCENTAGE_BONUS);
                        if (rand == 0) {
                            blocks.add(new Block(Type.BONUS, column, row));
                        } else if (rand == 100 / POURCENTAGE_BONUS - 1) {
                            blocks.add(new Block(Type.MALUS, column, row));
                        }
                    } else if (row % 4 == 0 && !previousEmpty && row != 28) {
                        if (random.nextBoolean()) {
                            blocks.add(new Block(Type.WALL, column, row));
                        } else {
                            previousEmpty = true;
                        }
                    } else {
                        previousEmpty = false;
                    }
                }
            }
        }
        for (int column = 1; column < COLUMNS - 1; column++) {
            if (column % 3 == 0 || (column - 1) % 3 == 0) {
                blocks.add(new Block(Type.END, column, ROWS - 2));
            } else {
                blocks.add(new Block(Type.WALL, column, ROWS - 2));
            }
        }
        return blocks;
    }

    public void surfaceCreated() {
        initialized = true;
        ball = new Ball(view.getMultiplicator());
        buildLabyrinthe();
    }

    public GameRenderer getView() {
        return view;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void update() {
        if (pause)
            return;
        if (ball == null)
            return;
        // On met à jour les coordonnées de la Ball
        ball.putXAndY(x, y);

        // Pour tous les blocs du labyrinthe
        boolean end = false;
        for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext(); ) {
            Block block = iterator.next();
            // On crée un nouveau rectangle pour ne pas modifier celui du bloc
            RectF inter = new RectF(ball.getRectangle());
            if (inter.intersect(block.getRectangle())) {

                // On agit différement en fonction du type de bloc
                switch (block.getType()) {
                    case WALL:
                        System.out.println("collision");
                        detectSide(block);
                        break;
                    case BONUS:
                        iterator.remove();
                        timeLeft += 1;
                        break;
                    case MALUS:
                        iterator.remove();
                        timeLeft -= 1;
                        break;
                    case START:
                        break;

                    case END:
                        score += 100;
                        end = true;
                        break;
                }
            }
        }
        if (end) {
            ball.reset();
            buildLabyrinthe();
        }

        long currentTime = System.currentTimeMillis();
        timeLeft -= (currentTime - lastTime) / 1000f;
        if (timeLeft <= 0) {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    mActivity.showScore();
                }
            });
        }
        lastTime = currentTime;
    }

    private void detectSide(Block block) {
        if (ball.getSpeedY() > 0 && Math.abs(ball.getY() - block.getRectangle().top) < Ball.RADIUS) {
            ball.setY(block.getRectangle().top - Ball.RADIUS);
            ball.changeYSpeed();
            System.out.println("bottom");
        } else if (ball.getSpeedX() > 0) {
            if (Math.abs(ball.getX() - block.getRectangle().left) < Ball.RADIUS) {
                ball.setX(block.getRectangle().left - Ball.RADIUS);
                ball.changeXSpeed();
                System.out.println("right");
            }
        } else if (ball.getSpeedX() < 0) {
            if (Math.abs(ball.getX() - block.getRectangle().right) < Ball.RADIUS) {
                ball.setX(block.getRectangle().right + Ball.RADIUS);
                ball.changeXSpeed();
                System.out.println("left");
            }
        }


    }

    public int getScore() {
        return score;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public float getTimeLeft() {
        return timeLeft;
    }
}
