package valjevac.kresimir.homework2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

public class BrowserActivity extends AppCompatActivity {
    public static final String SEARCH_URL = "SEARCH URL";
    private static final String FALLBACK_URL = "http://google.com";
    WebView webView;
    Button btnGo;
    EditText editTextSearch;

    private String validateUrl(String url) {
        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean isUrl = URL_PATTERN.matcher(url).matches();

        if (!isUrl) {
            String stringUrl = url + "";

            if (!URLUtil.isNetworkUrl(stringUrl)) {
                url = FALLBACK_URL + "/search?q=" + stringUrl;
            }
        }

        if (!url.contains("http") && !url.contains("https"))
            url = "http://" + url;
        else if(url.equals("http://") || url.equals("https://"))
            url = FALLBACK_URL;

        return url;
    }

    private void loadValidatedUrl(String url) {
        url = validateUrl(url);
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

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                BrowserActivity.this.setTitle(view.getTitle());

                if (view.canGoBack() || view.canGoForward()) {
                    invalidateOptionsMenu();
                }
            }
        });
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

                editTextSearch.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buttons, menu);

        menu.findItem(R.id.action_forward).setEnabled(false);
        menu.findItem(R.id.action_back).setEnabled(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_back).setEnabled(webView.canGoBack());
        menu.findItem(R.id.action_forward).setEnabled(webView.canGoForward());

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_back:
                webView.goBack();
                return true;
            case R.id.action_forward:
                webView.goForward();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
