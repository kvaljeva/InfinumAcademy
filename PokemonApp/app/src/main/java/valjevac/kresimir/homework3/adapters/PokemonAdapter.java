package valjevac.kresimir.homework3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.helpers.BitmapHelper;
import valjevac.kresimir.homework3.interfaces.RecyclerViewClickListener;
import valjevac.kresimir.homework3.models.Pokemon;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.ViewHolder> {
    private List<Pokemon> pokemonList;

    private Context context;

    private RecyclerViewClickListener<Pokemon> clickListener;

    private final static int MIN_POSITION = -1;

    public PokemonAdapter(Context context, List<Pokemon> pokemonList,
                          RecyclerViewClickListener<Pokemon> clickListener) {

        this.context = context;
        this.pokemonList = new ArrayList<>(pokemonList);
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_pokemon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvPokemonName.setText(pokemonList.get(position).getName());
        BitmapHelper.loadBitmap(holder.civPokemonImage, pokemonList.get(position).getImage(), true);

        setAnimation(holder.llContainer, position);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        holder.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    private void setAnimation(View view, int position) {

        if (position > MIN_POSITION) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.recycler_item_anim);
            view.startAnimation(animation);
        }
    }

    public void update(ArrayList<Pokemon> updateList) {
        if (pokemonList != null) {
            pokemonList.clear();
            pokemonList.addAll(updateList);
        }
        else {
            pokemonList = updateList;
        }

        notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_pokemon_name)
        TextView tvPokemonName;

        @BindView(R.id.civ_pokemon_image)
        ImageView civPokemonImage;

        @BindView(R.id.pokemon_list_item_container)
        LinearLayout llContainer;

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            if (clickListener != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickListener.OnClick(pokemonList.get(getAdapterPosition()));
                    }
                });
            }
        }

        public void clearAnimation() {
            llContainer.clearAnimation();
        }
    }
}
