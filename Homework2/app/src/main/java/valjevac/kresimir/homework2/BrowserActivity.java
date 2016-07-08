package valjevac.kresimir.homework2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class BrowserActivity extends AppCompatActivity {
    public static final String SEARCH_URL = "SEARCH URL";
    WebView webView;
    Button btnGo;
    EditText editTextSearch;

    private void loadValidatedUrl(String url) {
        if (!url.contains("http") || !url.contains("https"))
            url = "http://" + url;

        webView.loadUrl(url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        String url = getIntent().getStringExtra(SEARCH_URL);

        webView = (WebView) findViewById(R.id.webview);
        btnGo = (Button) findViewById(R.id.btn_go);
        editTextSearch = (EditText) findViewById(R.id.et_address);

        webView.setWebViewClient(new WebViewClient());
        webView.requestFocus();

        loadValidatedUrl(url);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = editTextSearch.getText().toString();

                InputMethodManager inputManager = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null)
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                loadValidatedUrl(url);
            }
        });
    }
}
