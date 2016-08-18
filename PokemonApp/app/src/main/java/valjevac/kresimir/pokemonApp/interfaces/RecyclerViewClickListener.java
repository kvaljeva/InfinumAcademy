package valjevac.kresimir.pokemonApp.interfaces;

import android.widget.ImageView;

public interface RecyclerViewClickListener<T> {

    void onClick(T object, ImageView imageView);

    void onDeleteItem(int itemId, int position);
}
