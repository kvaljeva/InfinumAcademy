package valjevac.kresimir.homework2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private EditText editTextSearch;
    Button btnWebview, btnBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnWebview = (Button) findViewById(R.id.btn_webview);
        btnBrowser = (Button) findViewById(R.id.btn_browser);
        editTextSearch = (EditText) findViewById(R.id.et_search);

        btnWebview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                intent.putExtra(BrowserActivity.SEARCH_URL, editTextSearch.getText().toString());

                InputMethodManager inputManager = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null)
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                startActivity(intent);
            }
        });
    }
}
