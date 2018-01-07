package run.mato.hepia.labo4.Controller;

import android.app.Service;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import run.mato.hepia.labo4.GameActivity;
import run.mato.hepia.labo4.Model.Ball;
import run.mato.hepia.labo4.Model.Block;
import run.mato.hepia.labo4.Model.Block.Type;
import run.mato.hepia.labo4.View.GameRenderer;

/**
 * Created by matoran on 1/6/18.
 */

public class GameEngine {
    public static final int ROWS = 32;
    public static final int COLUMNS = 18;
    SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent pEvent) {
            /*float x = pEvent.values[0];
            float y = pEvent.values[1];

            if(ball != null) {
                // On met à jour les coordonnées de la Ball
                RectF hitBox = ball.putXAndY(x, y);

                // Pour tous les blocs du labyrinthe
                for(Block block : blocks) {
                    // On crée un nouveau rectangle pour ne pas modifier celui du bloc
                    RectF inter = new RectF(block.getRectangle());
                    if(inter.intersect(hitBox)) {
                        // On agit différement en fonction du type de bloc
                        switch(block.getType()) {
                            case WALL:
                                mActivity.showDialog(GameActivity.DEFEAT_DIALOG);
                                break;

                            case START:
                                break;

                            case END:
                                mActivity.showDialog(GameActivity.VICTORY_DIALOG);
                                break;
                        }
                        break;
                    }
                }
            }*/
        }

        @Override
        public void onAccuracyChanged(Sensor pSensor, int pAccuracy) {

        }
    };
    private Ball ball = null;
    // Le labyrinthe
    private List<Block> blocks = null;
    private GameActivity mActivity = null;
    private SensorManager sensorManager = null;
    private Sensor accelerometer = null;
    private float multiplicator;
    private GameRenderer view;
    private boolean initialized = false;

    public GameEngine(GameActivity gameActivity) {
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
    }

    // Redémarre le capteur
    public void resume() {
        sensorManager.registerListener(mSensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    // Construit le labyrinthe
    public List<Block> buildLabyrinthe() {
        Random random = new Random();
        blocks = new ArrayList<>();
        Block start = new Block(Type.START, 1, 1);
        ball.setInitialRectangle(new RectF(start.getRectangle()));
        blocks.add(start);
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if(column == 0 || column == COLUMNS-1 || row == 0 || row == ROWS-1){
                    blocks.add(new Block(Type.WALL, column, row));
                }else{
                    if(row % 4 == 0){
                        if(random.nextBoolean()){
                            blocks.add(new Block(Type.WALL, column, row));
                        }
                    }
                }
            }
        }

        blocks.add(new Block(Type.END, 8, 11));

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
}
