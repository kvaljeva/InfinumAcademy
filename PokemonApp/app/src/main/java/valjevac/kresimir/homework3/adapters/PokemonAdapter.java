package valjevac.kresimir.homework3.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.MainActivity;
import valjevac.kresimir.homework3.fragments.PokemonDetailsFragment;
import valjevac.kresimir.homework3.helpers.BitmapHelper;
import valjevac.kresimir.homework3.interfaces.RecyclerViewClickListener;
import valjevac.kresimir.homework3.models.Pokemon;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.ViewHolder> {
    private List<Pokemon> pokemonList;

    private Context context;

    private RecyclerViewClickListener<Pokemon> clickListener;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.civPokemonImage.setTransitionName(getImageTransitionName(position));
        }

        holder.tvPokemonName.setText(pokemonList.get(position).getName());
        BitmapHelper.loadBitmap(holder.civPokemonImage, pokemonList.get(position).getImage(), true);
    }

    public String getImageTransitionName(int position) {
        return context.getString(R.string.details_transit) + position;
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
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
                        ImageView imageView = (ImageView) view.findViewById(R.id.civ_pokemon_image);

                        clickListener.OnClick(pokemonList.get(getAdapterPosition()), imageView);
                    }
                });
            }
        }
    }
}
