package valjevac.kresimir.homework2.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import valjevac.kresimir.homework2.R;
import valjevac.kresimir.homework2.helpers.HistoryHelper;
import valjevac.kresimir.homework2.helpers.UrlHelper;

public class MainActivity extends AppCompatActivity {
    private EditText etWebSearch, ethistorySearch;
    private ArrayList<String> history;
    private ArrayAdapter<String> arrayAdapter;
    ListView listViewHistory;
    Button btnWebview, btnBrowser, btnClearHistory;
    RelativeLayout rlHistoryContainer;

    private void loadHistory() {
        history = HistoryHelper.readHistory();

        if (history != null && (history.size() > 0)) {
            rlHistoryContainer.setVisibility(View.GONE);

            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, history);

            listViewHistory.setAdapter(arrayAdapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HistoryHelper.init(getApplicationContext());

        btnWebview = (Button) findViewById(R.id.btn_webview);
        btnBrowser = (Button) findViewById(R.id.btn_browser);
        btnClearHistory = (Button) findViewById(R.id.btn_clear_history);
        ethistorySearch = (EditText) findViewById(R.id.et_search_history);
        etWebSearch = (EditText) findViewById(R.id.et_web_search);
        listViewHistory = (ListView) findViewById(R.id.lv_history);
        rlHistoryContainer = (RelativeLayout) findViewById(R.id.rl_container_no_history);

        loadHistory();

        listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url = ((TextView) view).getText().toString();

                etWebSearch.setText(url);
            }
        });

        btnWebview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                intent.putExtra(BrowserActivity.SEARCH_URL, etWebSearch.getText().toString());

                InputMethodManager inputManager = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null)
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

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
                HistoryHelper.clearHistory();
                history.clear();

                rlHistoryContainer.setVisibility(View.VISIBLE);
            }
        });

        ethistorySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.equals("")) return;

                MainActivity.this.arrayAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadHistory();
    }
}
