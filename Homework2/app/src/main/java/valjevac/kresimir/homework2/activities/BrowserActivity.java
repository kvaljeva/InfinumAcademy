package valjevac.kresimir.homework2.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import valjevac.kresimir.homework2.R;
import valjevac.kresimir.homework2.helpers.HistoryHelper;
import valjevac.kresimir.homework2.helpers.UrlHelper;

@SuppressWarnings("setJavascriptEnabled")
public class BrowserActivity extends AppCompatActivity {
    public static final String SEARCH_URL = "SEARCH URL";
    private WebView webView;
    private EditText editTextSearch;
    private ProgressBar progressBar;
    Button btnGo;
    String lastUrl;

    private void loadValidatedUrl(String url) {
        url = UrlHelper.validateUrl(url);
        webView.loadUrl(url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        String url = getIntent().getStringExtra(SEARCH_URL);
        lastUrl = "";

        // If there's nothing passed through the explicit intent, try loading data from the implicit intent
        if (url == null) url = getIntent().getDataString();

        webView = (WebView) findViewById(R.id.webview);
        btnGo = (Button) findViewById(R.id.btn_go);
        editTextSearch = (EditText) findViewById(R.id.et_address);
        progressBar = (ProgressBar) findViewById(R.id.pb_page_load);

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }

                progressBar.setProgress(newProgress);

                if (newProgress >= 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                BrowserActivity.this.setTitle(view.getTitle());

                if (view.canGoBack() || view.canGoForward())
                    invalidateOptionsMenu();

                if (!lastUrl.equals(url))
                    HistoryHelper.writeToHistory(url);

                lastUrl = url;
            }
        });
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.requestFocus();

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }
        else {
            loadValidatedUrl(url);
        }

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
            case R.id.action_refresh:
                webView.loadUrl("javascript:window.location.reload(true)");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        lastUrl = savedInstanceState.getString("LAST_URL");

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        webView.saveState(outState);
        outState.putString("LAST_URL", lastUrl);
    }
}
