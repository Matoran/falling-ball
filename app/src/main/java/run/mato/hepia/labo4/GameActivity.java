package run.mato.hepia.labo4;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import run.mato.hepia.labo4.Controller.GameEngine;
import run.mato.hepia.labo4.Model.Difficulty;
import run.mato.hepia.labo4.View.GameRenderer;


public class GameActivity extends Activity {
    // Le moteur graphique du jeu
    private GameRenderer view;
    // Le moteur physique du jeu
    private GameEngine engine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Difficulty difficulty = (Difficulty) intent.getSerializableExtra("difficulty");
        engine = new GameEngine(this, difficulty, getBaseContext());
        setContentView(engine.getView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        engine.resume();
    }

    @Override
    protected void onPause() {
        super.onStop();
        engine.stop();
    }

    @Override
    public void onPrepareDialog(int id, Dialog box) {
        // A chaque fois qu'une boîte de dialogue est lancée, on arrête le moteur physique
        engine.stop();
    }


    public void showScore() {
        engine.stop();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Time over !");
        builder.setMessage("Your score " + engine.getScore());
        builder.setPositiveButton("Back to menu", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent i = new Intent();
                i.putExtra("score", engine.getScore());
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
