package run.mato.hepia.labo4;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import run.mato.hepia.labo4.Model.Difficulty;

public class MainActivity extends AppCompatActivity {
    public Difficulty difficulty = Difficulty.EASY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launchButton(View v) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("difficulty", difficulty);
        startActivityForResult(i, 1);
    }

    public void hiScoreButton(View v) {
        Intent i = new Intent(this, HiScoreActivity.class);
        for (int j = 1; j <= 5; j++) {
            i.putExtra(String.valueOf(j), this.getScore(j));
        }
        startActivity(i);
    }

    private int getScore(int i) {
        SharedPreferences pref = getSharedPreferences("Score", MODE_PRIVATE);
        return pref.getInt(String.valueOf(i), 0);
    }

    private void setScore(int i, int score) {
        SharedPreferences.Editor editor = getSharedPreferences("Score", MODE_PRIVATE).edit();
        editor.putInt(String.valueOf(i), score);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = "";
        switch (item.getItemId()) {
            case R.id.easy:
                this.difficulty = Difficulty.EASY;
                message = getResources().getString(R.string.difficulty) + " " + getResources().getString(R.string.easy);
                break;
            case R.id.medium:
                this.difficulty = Difficulty.MEDIUM;
                message = getResources().getString(R.string.difficulty) + " " + getResources().getString(R.string.medium);
                break;
            case R.id.hard:
                this.difficulty = Difficulty.HARD;
                message = getResources().getString(R.string.difficulty) + " " + getResources().getString(R.string.hard);
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        switch (difficulty) {
            case EASY:
                menu.findItem(R.id.easy).setChecked(true);
                break;
            case MEDIUM:
                menu.findItem(R.id.medium).setChecked(true);
                break;
            case HARD:
                menu.findItem(R.id.hard).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                int score = data.getIntExtra("score", 0);
                Toast.makeText(this, "Score: " + score, Toast.LENGTH_SHORT).show();

                for (int i = 1; i <= 5; i++) {
                    int topScore = this.getScore(i);
                    if (score > topScore) {
                        // Move all others results
                        for (int j = 1; j < 6 - i; j++) {
                            this.setScore(6 - j, this.getScore(6 - j - 1));
                        }
                        this.setScore(i, score);
                        Toast.makeText(this, "Top 5!!!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }
    }
}
