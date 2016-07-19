package valjevac.kresimir.homework2.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import valjevac.kresimir.homework2.models.UrlModel;

@SuppressWarnings("setJavascriptEnabled")
public class BrowserActivity extends AppCompatActivity {
    public static final String SEARCH_URL = "SEARCH URL";
    public static final String LAST_URL = "LAST URL";
    public static final int PROGRESS_BAR_MAX = 100;
    private WebView webView;
    private EditText editTextSearch;
    private ProgressBar progressBarPageLoad;
    Button btnGo;
    String previousUrl;
    MenuItem menuActionBack;
    MenuItem menuActionForward;

    private void loadValidatedUrl(String url) {
        url = UrlHelper.validateUrl(url);
        webView.loadUrl(url);
    }

    private void initWebview() {
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < PROGRESS_BAR_MAX && progressBarPageLoad.getVisibility() == ProgressBar.GONE) {
                    progressBarPageLoad.setVisibility(ProgressBar.VISIBLE);
                }

                progressBarPageLoad.setProgress(newProgress);

                if (newProgress >= 100) {
                    progressBarPageLoad.setVisibility(ProgressBar.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                BrowserActivity.this.setTitle(view.getTitle());

                if (view.canGoBack() || view.canGoForward()) {
                    updateMenuItems(view.canGoForward(), view.canGoBack());
                }

                if (!previousUrl.equals(url)) {
                    UrlModel urlModel = new UrlModel(view.getTitle(), url);
                    HistoryHelper.writeToHistory(urlModel);
                }

                previousUrl = url;
            }
        });
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        String url = getIntent().getStringExtra(SEARCH_URL);
        previousUrl = "";

        // If there's nothing passed through the explicit intent, try loading data from the implicit intent
        if (TextUtils.isEmpty(url)) {
            url = getIntent().getDataString();
        }

        webView = (WebView) findViewById(R.id.webview);
        btnGo = (Button) findViewById(R.id.btn_go);
        editTextSearch = (EditText) findViewById(R.id.et_address);
        progressBarPageLoad = (ProgressBar) findViewById(R.id.pb_page_load);

        initWebview();

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
                if (getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                loadValidatedUrl(url);

                editTextSearch.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buttons, menu);

        menuActionBack = menu.findItem(R.id.action_back);
        menuActionForward = menu.findItem(R.id.action_forward);

        updateMenuItems(false, false);

        return true;
    }

    private void updateMenuItems(boolean forwardState, boolean backState) {
        menuActionForward.setEnabled(forwardState);
        menuActionBack.setEnabled(backState);
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
        previousUrl = savedInstanceState.getString(LAST_URL);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        webView.saveState(outState);
        outState.putString(LAST_URL, previousUrl);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
