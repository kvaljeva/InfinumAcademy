package valjevac.kresimir.homework2.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import valjevac.kresimir.homework2.adapters.HistoryAdapter;
import valjevac.kresimir.homework2.R;
import valjevac.kresimir.homework2.listeners.RecyclerViewClickListener;
import valjevac.kresimir.homework2.models.UrlModel;
import valjevac.kresimir.homework2.helpers.HistoryHelper;
import valjevac.kresimir.homework2.helpers.UrlHelper;

public class MainActivity extends AppCompatActivity {
    private EditText etWebSearch, ethistorySearch;
    private ArrayList<UrlModel> history;
    private HistoryAdapter historyAdapter;
    private RecyclerView recyclerViewHistory;
    private RelativeLayout rlHistoryContainer;
    private LinearLayout llHistoryOptionsContainer;
    Button btnWebview, btnBrowser, btnClearHistory;

    private void loadHistory() {
        history = HistoryHelper.readHistory(true);

        if (history != null && (history.size() > 0)) {
            rlHistoryContainer.setVisibility(View.GONE);
            llHistoryOptionsContainer.setVisibility(View.VISIBLE);
            recyclerViewHistory.setVisibility(View.VISIBLE);

            if (historyAdapter == null) {
                historyAdapter = new HistoryAdapter(this, history, new RecyclerViewClickListener<UrlModel>() {
                    @Override
                    public void onClick(UrlModel object) {
                        etWebSearch.setText(object.getUrl());
                    }
                });

                recyclerViewHistory.setAdapter(historyAdapter);
                recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
            }
            else {
                historyAdapter.update(history);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HistoryHelper.init();

        btnWebview = (Button) findViewById(R.id.btn_webview);
        btnBrowser = (Button) findViewById(R.id.btn_browser);
        btnClearHistory = (Button) findViewById(R.id.btn_clear_history);
        ethistorySearch = (EditText) findViewById(R.id.et_search_history);
        etWebSearch = (EditText) findViewById(R.id.et_web_search);
        recyclerViewHistory = (RecyclerView) findViewById(R.id.lv_history);
        rlHistoryContainer = (RelativeLayout) findViewById(R.id.rl_container_no_history);
        llHistoryOptionsContainer = (LinearLayout) findViewById(R.id.ll_history_options_container);

        btnWebview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                intent.putExtra(BrowserActivity.SEARCH_URL, etWebSearch.getText().toString());

                InputMethodManager inputManager = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                startActivity(intent);
            }
        });

        btnBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                String url = UrlHelper.validateUrl(etWebSearch.getText().toString());

                intent.setData(Uri.parse(url));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (history != null) {
                    history = HistoryHelper.clearHistory();
                }

                rlHistoryContainer.setVisibility(View.VISIBLE);
                recyclerViewHistory.setVisibility(View.GONE);
                llHistoryOptionsContainer.setVisibility(View.GONE);
            }
        });

        ethistorySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                MainActivity.this.historyAdapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadHistory();
    }
}
