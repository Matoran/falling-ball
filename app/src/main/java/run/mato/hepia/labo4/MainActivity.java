package run.mato.hepia.labo4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import run.mato.hepia.labo4.Model.Difficulty;

public class MainActivity extends AppCompatActivity {
    private Difficulty difficulty = Difficulty.EASY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launchButton(View v){
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("difficulty", difficulty);
        startActivity(i);
    }

    public void hiScoreButton(View v){
        Intent i = new Intent(this, HiScoreActivity.class);
        startActivity(i);
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
                message = getResources().getString(R.string.difficulty)+" "+getResources().getString(R.string.easy);
                break;
            case R.id.medium:
                this.difficulty =  Difficulty.MEDIUM;
                message = getResources().getString(R.string.difficulty)+" "+getResources().getString(R.string.medium);
                break;
            case R.id.hard:
                this.difficulty = Difficulty.HARD;
                message = getResources().getString(R.string.difficulty)+" "+getResources().getString(R.string.hard);
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
}
