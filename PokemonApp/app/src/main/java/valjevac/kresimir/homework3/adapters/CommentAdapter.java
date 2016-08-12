package valjevac.kresimir.homework3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.interfaces.RecyclerViewClickListener;
import valjevac.kresimir.homework3.models.Comment;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> commentList;

    private Context context;

    private RecyclerViewClickListener<Comment> listener;

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

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

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_comment_username)
        TextView tvCommentUsername;

        @BindView(R.id.tv_comment_date)
        TextView tvCommentDate;

        @BindView(R.id.tv_comment_body)
        TextView tvCommentBody;

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            if (listener != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.OnClick(commentList.get(getAdapterPosition()), null);
                    }
                });
            }
        }
    }
}
