package valjevac.kresimir.pokemonApp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.pokemonApp.R;
import valjevac.kresimir.pokemonApp.activities.MainActivity;
import valjevac.kresimir.pokemonApp.interfaces.RecyclerViewClickListener;
import valjevac.kresimir.pokemonApp.models.Comment;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> commentList;

    private Context context;

    private RecyclerViewClickListener<Comment> listener;

    private ActionMode actionMode;

    private static final String DATE_FORMAT = "MMM dd, yyyy";

    private MultiSelector multiSelector = new MultiSelector();

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

                for (int i = commentList.size(); i >= 0; i--) {
                    if (multiSelector.isSelected(i, 0)) {
                        listener.onDeleteItem(commentList.get(i).getId(), i);
                    }
                }

                multiSelector.clearSelections();
                return true;
            }

            return false;
        }
    };

    public CommentAdapter(Context context, List<Comment> comments, RecyclerViewClickListener<Comment> listener) {
        this.context = context;
        this.commentList = new ArrayList<>(comments);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);

        holder.tvCommentUsername.setText(commentList.get(position).getUsername());
        holder.tvCommentBody.setText(commentList.get(position).getContent());
        holder.tvCommentDate.setText(dateFormat.format(commentList.get(position).getDate()));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void update(ArrayList<Comment> updateList) {
        if (commentList != null) {
            commentList.clear();
            commentList.addAll(updateList);
        }
        else {
            commentList = updateList;
        }

        notifyDataSetChanged();
    }

    public void update(int position) {
        commentList.remove(position);
        notifyItemRemoved(position);
    }

    protected class ViewHolder extends SwappingHolder {
        @BindView(R.id.tv_comment_username)
        TextView tvCommentUsername;

        @BindView(R.id.tv_comment_date)
        TextView tvCommentDate;

        @BindView(R.id.tv_comment_body)
        TextView tvCommentBody;

        public ViewHolder(View view) {
            super(view, multiSelector);

            ButterKnife.bind(this, view);

            setSelectionModeBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.selected_list_item_selector));
            setSelectionModeStateListAnimator(null);

            if (listener != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!multiSelector.tapSelection(ViewHolder.this)) {
                            listener.onClick(commentList.get(getAdapterPosition()), null);
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
