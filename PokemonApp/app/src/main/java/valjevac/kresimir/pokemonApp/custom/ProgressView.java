package valjevac.kresimir.pokemonApp.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.pokemonApp.R;

public class ProgressView extends RelativeLayout {

    public static final String PROGRESS_BAR_SPINNER = "spinner";

    public static final String PROGRESS_BAR_HORIZONTAL = "horizontal";

    public static final String DEFAULT_TEXT_VALUE = "Loading progress";

    public static final String PROGRESS_BAR_STYLE_DEFAULT = "default";

    public static final String PROGRESS_BAR_STYLE_SMALL = "small";

    public static final int DEFAULT_OFFSET = 30;

    public static final int DEFAULT_TEXT_SIZE = 19;

    public static final int DEFAULT_HEIGHT = 130;

    public static final int DEFAULT_WIDTH = 130;

    public static final boolean IS_INDETERMINATE = true;

    @BindView(R.id.tv_text_progress_view)
    TextView tvProgressText;

    @BindView(R.id.ll_progress_bar_container)
    LinearLayout llProgressBarContainer;

    ProgressBar progressBar;

    private String text;

    private String progressBarType;

    private String progressBarStyle;

    private boolean indeterminate;

    private int offset;

    private int textSize;

    private int progressBarHeight;

    private int progressBarWidth;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        LayoutInflater.from(getContext()).inflate(R.layout.view_progress, this, true);
        ButterKnife.bind(this);

        progressBarType = PROGRESS_BAR_SPINNER;
        progressBarStyle = PROGRESS_BAR_STYLE_DEFAULT;
        text = DEFAULT_TEXT_VALUE;
        indeterminate = IS_INDETERMINATE;
        textSize = DEFAULT_TEXT_SIZE;
        progressBarHeight = DEFAULT_HEIGHT;
        progressBarWidth = DEFAULT_WIDTH;

        if (attrs != null) {

            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressView, 0, 0);

            text = a.getString(R.styleable.ProgressView_text);
            progressBarType = a.getString(R.styleable.ProgressView_progressBarType);
            indeterminate = a.getBoolean(R.styleable.ProgressView_indeterminate, IS_INDETERMINATE);
            offset = a.getInt(R.styleable.ProgressView_progressBarOffset, DEFAULT_OFFSET);
            progressBarStyle = a.getString(R.styleable.ProgressView_progressBarSizeStyle);
            textSize = a.getInt(R.styleable.ProgressView_progressTextSize, DEFAULT_TEXT_SIZE);
            progressBarHeight = a.getInt(R.styleable.ProgressView_progressBarHeight, DEFAULT_HEIGHT);
            progressBarWidth = a.getInt(R.styleable.ProgressView_progressBarWidth, DEFAULT_WIDTH);

            a.recycle();

            update();
        }
    }

    private void update() {

        tvProgressText.setText(text);
        tvProgressText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        createProgressBar();
    }

    private void createProgressBar() {

        if (progressBarType != null) {

            switch(progressBarType) {

                case PROGRESS_BAR_SPINNER:
                    if (!TextUtils.isEmpty(progressBarStyle) && progressBarStyle.equals(PROGRESS_BAR_STYLE_SMALL)) {
                        progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
                    }
                    else {
                        progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyle);
                    }
                    break;

                case PROGRESS_BAR_HORIZONTAL:
                    progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
                    break;
            }
        }

        progressBar.setIndeterminate(indeterminate);

        if (progressBar != null) {

            llProgressBarContainer.addView(progressBar);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progressBar.getLayoutParams();

            if (offset != DEFAULT_OFFSET) {
                params.setMargins(params.leftMargin, offset, params.rightMargin, params.bottomMargin);
            }

            if (progressBarHeight != DEFAULT_HEIGHT) {
                params.height = progressBarHeight;
            }

            if (progressBarWidth != DEFAULT_WIDTH) {
                params.width = progressBarWidth;
            }

            params.gravity = Gravity.CENTER;

            progressBar.setLayoutParams(params);
        }
    }

    public void show() {
        this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        this.setVisibility(View.GONE);
    }
}
