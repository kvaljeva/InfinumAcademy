package valjevac.kresimir.homework3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.listeners.RecyclerViewClickListener;
import valjevac.kresimir.homework3.models.PokemonModel;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.ViewHolder> {
    private ArrayList<PokemonModel> pokemonList;
    private Context context;
    private RecyclerViewClickListener<PokemonModel> clickListener;

    public PokemonAdapter(Context context, ArrayList<PokemonModel> pokemonList,
                          RecyclerViewClickListener<PokemonModel> clickListener) {

        this.context = context;
        this.pokemonList = pokemonList;
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
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    public void update() {
        notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_pokemon_name)
        TextView tvPokemonName;

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
    }
}
