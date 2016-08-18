package valjevac.kresimir.homework3.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.MainActivity;
import valjevac.kresimir.homework3.helpers.BitmapHelper;
import valjevac.kresimir.homework3.interfaces.RecyclerViewClickListener;
import valjevac.kresimir.homework3.models.Pokemon;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.ViewHolder> {
    private List<Pokemon> pokemonList;

    private Context context;

    private RecyclerViewClickListener<Pokemon> clickListener;

    private MultiSelector multiSelector = new MultiSelector();

    private ActionMode actionMode;

    private ModalMultiSelectorCallback multiSelectorCallback = new ModalMultiSelectorCallback(multiSelector) {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            super.onCreateActionMode(actionMode, menu);

            if (context instanceof MainActivity) {
                ((MainActivity) context).getMenuInflater().inflate(R.menu.menu_item_delete, menu);
            }

            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                mode.finish();

                for (int i = pokemonList.size(); i >= 0; i--) {
                    if (multiSelector.isSelected(i, 0)) {
                        clickListener.onDeleteItem(pokemonList.get(i).getId(), i);
                    }
                }

                multiSelector.clearSelections();
                return true;
            }

            multiSelector.clearSelections();
            return false;
        }
    };

    public PokemonAdapter(Context context, List<Pokemon> pokemonList,
                          RecyclerViewClickListener<Pokemon> clickListener) {

        this.context = context;
        this.clickListener = clickListener;
        this.pokemonList = new ArrayList<>(pokemonList);
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

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    public String getImageTransitionName(int position) {
        return context.getString(R.string.details_transit) + position;
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

    public void deleteItem(int position) {
        pokemonList.remove(position);
        notifyItemRemoved(position);
    }

    protected class ViewHolder extends SwappingHolder {
        @BindView(R.id.tv_pokemon_name)
        TextView tvPokemonName;

        @BindView(R.id.civ_pokemon_image)
        ImageView civPokemonImage;

        @BindView(R.id.pokemon_list_item_container)
        LinearLayout llContainer;

        public ViewHolder(View view) {
            super(view, multiSelector);

            ButterKnife.bind(this, view);

            setSelectionModeBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.selected_list_item_selector));
            setSelectionModeStateListAnimator(null);

            if (clickListener != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!multiSelector.tapSelection(ViewHolder.this)) {
                            ImageView imageView = (ImageView) view.findViewById(R.id.civ_pokemon_image);
                            clickListener.onClick(pokemonList.get(getAdapterPosition()), imageView);
                        }
                        else {

                            if (multiSelector.getSelectedPositions().size() == 0) {
                                multiSelector.setSelectable(false);

                                if (actionMode != null) {
                                    actionMode.finish();
                                }
                            }
                            else {
                                actionMode.setTitle(String.valueOf(multiSelector.getSelectedPositions().size()));
                            }
                        }
                    }
                });

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!multiSelector.isSelectable()) {
                            actionMode = ((MainActivity) context).startSupportActionMode(multiSelectorCallback);

                            multiSelector.setSelectable(true);
                            multiSelector.setSelected(ViewHolder.this, true);

                            actionMode.setTitle(String.valueOf(multiSelector.getSelectedPositions().size()));

                            return true;
                        }

                        return false;
                    }
                });
            }
        }
    }
}
