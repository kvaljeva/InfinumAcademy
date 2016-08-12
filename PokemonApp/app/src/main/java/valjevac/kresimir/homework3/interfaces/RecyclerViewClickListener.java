package valjevac.kresimir.homework3.interfaces;

import android.widget.ImageView;

public interface RecyclerViewClickListener<T> {

    void OnClick(T object, ImageView imageView);
}
