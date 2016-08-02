package valjevac.kresimir.homework3.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.homework3.R;

public class ProgressView extends RelativeLayout {

    public static final String PROGRESS_BAR_SPINNER = "spinner";

    public static final String PROGRESS_BAR_HORIZONTAL = "horizontal";

    public static final String DEFAULT_TEXT_VALUE = "Loading progress";

    public static final int DEFAULT_OFFSET = 30;

    public static final boolean IS_INDETERMINATE = true;

    @BindView(R.id.tv_text_progress_view)
    TextView tvProgressText;

    @BindView(R.id.pb_progress_view)
    ProgressBar progressBar;

    private String text;

    private String progressBarStyle;

    private boolean indeterminate;

    private int offset;

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

        progressBarStyle = PROGRESS_BAR_SPINNER;
        text = DEFAULT_TEXT_VALUE;
        indeterminate = IS_INDETERMINATE;

        if (attrs != null) {

            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressView, 0, 0);

            text = a.getString(R.styleable.ProgressView_text);
            progressBarStyle = a.getString(R.styleable.ProgressView_progressStyle);
            indeterminate = a.getBoolean(R.styleable.ProgressView_indeterminate, IS_INDETERMINATE);
            offset = a.getInt(R.styleable.ProgressView_progressBarOffset, DEFAULT_OFFSET);

            a.recycle();

            update();
        }
    }

    private void update() {

        tvProgressText.setText(text);

        if (progressBarStyle != null) {

            switch(progressBarStyle) {
                case PROGRESS_BAR_SPINNER:
                    progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyle);
                    break;
                case PROGRESS_BAR_HORIZONTAL:
                    progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
            }
        }

        progressBar.setIndeterminate(indeterminate);

        if (offset != DEFAULT_OFFSET) {

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
            params.setMargins(params.leftMargin, offset, params.rightMargin, params.bottomMargin);

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
