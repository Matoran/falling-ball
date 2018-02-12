package run.mato.hepia.labo4.Controller;

import android.app.Service;
import android.content.Context;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import run.mato.hepia.labo4.GameActivity;
import run.mato.hepia.labo4.Model.Ball;
import run.mato.hepia.labo4.Model.Block;
import run.mato.hepia.labo4.Model.Block.Type;
import run.mato.hepia.labo4.Model.Difficulty;
import run.mato.hepia.labo4.R;
import run.mato.hepia.labo4.View.GameRenderer;


/**
 *
 */
public class GameEngine {
    public static final int ROWS = 32;
    public static final int COLUMNS = 18;
    private final Difficulty difficulty;
    private final int POURCENTAGE_BONUS = 1;
    boolean first = true;
    private boolean pause = false;
    private float y = 9.8f;
    private float x = 0;
    //milliseconds
    private float timeLeft = 120000;
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
    private Context baseContext;
    private float speedMultiplicator;

    public GameEngine(GameActivity gameActivity, Difficulty difficulty, Context baseContext) {
        this.difficulty = difficulty;
        mActivity = gameActivity;
        sensorManager = (SensorManager) mActivity.getBaseContext().getSystemService(Service.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        view = new GameRenderer(gameActivity, this);
        this.baseContext = baseContext;
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

    public void readFile() {
        InputStreamReader input = null;
        BufferedReader reader = null;
        Block block = null;
        blocks.clear();
        try {
            input = new InputStreamReader(baseContext.getResources().openRawResource(R.raw.lab1));
            reader = new BufferedReader(input);

            // L'indice qui correspond aux colonnes dans le fichier
            int column = 0;
            // L'indice qui correspond aux lignes dans le fichier
            int row = 0;

            // La valeur récupérée par le flux
            int c;
            // Tant que la valeur n'est pas de -1, c'est qu'on lit un caractère du fichier
            while ((c = reader.read()) != -1) {

                char character = (char) c;
                if (character >= '0' && character <= '9')
                    block = new Block(Type.END, column, row, (character - '0') * 10);
                if (character == 's') {
                    Block start = new Block(Type.START, column, row);
                    ;
                    ball.setInitialRectangle(start.getRectangle());
                    blocks.add(start);
                } else if (character == 'e')
                    block = new Block(Type.END, column, row);
                else if (character == '-')
                    block = new Block(Type.PLATFORM, column, row);
                else if (character == '|')
                    block = new Block(Type.WALL, column, row);
                else if (character == '#')
                    block = new Block(Type.BORDER, column, row);
                else if (character == 'b')
                    block = new Block(Type.BONUS, column, row);
                else if (character == 'm')
                    block = new Block(Type.MALUS, column, row);
                else if (character == '\n') {
                    // Si le caractère est un retour à la ligne, on retourne avant la première colonne
                    // Car on aura column++ juste après, ainsi column vaudra 0, la première colonne !
                    column = -1;
                    // Et on passe à la ligne suivante
                    row++;
                }
                // Si le bloc n'est pas nul, alors le caractère n'était pas un retour à la ligne
                if (block != null)
                    // On l'ajoute alors au labyrinthe
                    blocks.add(block);
                // On passe à la colonne suivante
                column++;
                // On remet bloc à null, utile quand on a un retour à la ligne pour ne pas ajouter de bloc qui n'existe pas
                block = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null)
                try {
                    input.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

    // Construit le labyrinthe
    public void buildLabyrinthe() {
        if (first) {
            readFile();
            first = false;
            return;
        }

        blocks.clear();
        Random random = new Random();
        Block start = new Block(Type.START, 2, 2);
        ball.setInitialRectangle(start.getRectangle());
        blocks.add(start);
        boolean previousEmpty = false;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                //les bordures
                if (column == 0 || column == COLUMNS - 1 || row == 0 || row == ROWS - 1) {
                    blocks.add(new Block(Type.BORDER, column, row));
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
                            blocks.add(new Block(Type.PLATFORM, column, row));
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
                blocks.add(new Block(Type.END, column, ROWS - 2, random.nextInt(10) * 10));
            } else {
                blocks.add(new Block(Type.WALL, column, ROWS - 2));
            }
        }
    }

    public void surfaceCreated() {
        multiplicator = view.getMultiplicator();
        ball = new Ball(multiplicator);
        buildLabyrinthe();
        initialized = true;
    }

    public GameRenderer getView() {
        return view;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void update() {
        if (pause || !initialized)
            return;
        if (ball == null)
            return;
        // On met à jour les coordonnées de la Ball
        ball.putXAndY(x * speedMultiplicator * multiplicator / 100, y * speedMultiplicator * multiplicator / 100);

        // Pour tous les blocs du labyrinthe
        boolean end = false;
        for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext(); ) {
            Block block = iterator.next();
            // On crée un nouveau rectangle pour ne pas modifier celui du bloc
            RectF inter = new RectF(ball.getRectangle());
            if (inter.intersect(block.getRectangle())) {
                // On agit différement en fonction du type de bloc
                switch (block.getType()) {
                    case PLATFORM:
                    case WALL:
                    case BORDER:
                        detectSide(block);
                        break;
                    case BONUS:
                        iterator.remove();
                        timeLeft += 1000;
                        break;
                    case MALUS:
                        iterator.remove();
                        timeLeft -= 1000;
                        break;
                    case START:
                        break;

                    case END:
                        score += block.getScore();
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
        timeLeft -= (currentTime - lastTime);
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
        if (ball.getSpeedY() > 0 && (block.getRectangle().top - ball.getY()) > 0 &&
                (block.getRectangle().top - ball.getY()) < Ball.RADIUS) {
            ball.setY(block.getRectangle().top - Ball.RADIUS);
            ball.changeYSpeed();
        } else if (ball.getSpeedY() < 0 && (ball.getY() - block.getRectangle().bottom) > 0 &&
                (ball.getY() - block.getRectangle().bottom) < Ball.RADIUS) {
            ball.setY(block.getRectangle().bottom + Ball.RADIUS);
            ball.changeYSpeed();
        } else if (ball.getSpeedX() > 0) {
            if (Math.abs(ball.getX() - block.getRectangle().left) < Ball.RADIUS) {
                ball.setX(block.getRectangle().left - Ball.RADIUS - 2);
                ball.changeXSpeed();
                System.out.println("right");
            }
        } else if (ball.getSpeedX() < 0) {
            if (Math.abs(ball.getX() - block.getRectangle().right) < Ball.RADIUS) {
                ball.setX(block.getRectangle().right + Ball.RADIUS + 2);
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

    public void setSpeedMultiplicator(float speedMultiplicator) {
        this.speedMultiplicator = speedMultiplicator;
    }
}
