package co.infinum.homework1;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private int counter;
    private TextView textView;
    Button btnCount, btnReset;
    Animation animBounce;

    private void initTextValue() {
        counter = 0;
        validateTextValue();
    }

    private void validateTextValue() {
        if (counter == 0) {
            textView.setTextColor(Color.BLACK);
        }
        else {
            if (counter % 2 == 0)
                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            else
                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }

        textView.startAnimation(animBounce);
        textView.setText(String.valueOf(counter));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnCount = (Button) findViewById(R.id.btnCount);
        btnReset = (Button) findViewById(R.id.btnReset);
        textView = (TextView) findViewById(R.id.textView);
        animBounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);

        initTextValue();

        if (btnCount != null && textView != null) {
            btnCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    counter++;
                    validateTextValue();
                }
            });
        }

        if (btnReset != null && textView != null) {
            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initTextValue();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("counterState", counter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        counter = savedInstanceState.getInt("counterState");
        validateTextValue();

        super.onRestoreInstanceState(savedInstanceState);
    }
}
