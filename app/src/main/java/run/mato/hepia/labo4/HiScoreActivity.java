package run.mato.hepia.labo4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class HiScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hi_score);
        TextView textList = findViewById(R.id.textView);

        // Get the difficulty
        Intent intent = getIntent();
        StringBuilder scoreList = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            scoreList.append(Integer.toString(intent.getIntExtra(String.valueOf(i), 0))).append("\n");
        }
        textList.setText(scoreList);
    }
}
