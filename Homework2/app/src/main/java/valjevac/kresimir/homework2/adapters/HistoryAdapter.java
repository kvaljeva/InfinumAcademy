package valjevac.kresimir.homework2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import valjevac.kresimir.homework2.R;
import valjevac.kresimir.homework2.listeners.RecyclerViewClickListener;
import valjevac.kresimir.homework2.models.UrlModel;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<UrlModel> urlList;
    private List<UrlModel> originalUrlList;
    private Context context;
    private RecyclerViewClickListener<UrlModel> clickListener;

    public HistoryAdapter(Context context, List<UrlModel> urls,
                          RecyclerViewClickListener<UrlModel> clickListener) {

        this.context = context;
        this.urlList = urls;
        this.originalUrlList = new ArrayList<>(urls);
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvPageName.setText(urlList.get(position).getPageName());
        holder.tvPageUrl.setText(urlList.get(position).getUrl());
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    public void update(ArrayList<UrlModel> urls) {
        this.urlList.clear();
        this.urlList.addAll(urls);
        this.originalUrlList.clear();
        this.originalUrlList.addAll(urls);

        notifyDataSetChanged();
    }

    public void filter(String query) {
        if (query.contains("\n")) {
            query = query.replace("\n", "");
        }

        if (query.isEmpty()) {
            urlList.clear();
            urlList.addAll(originalUrlList);
        }
        else {
            ArrayList<UrlModel> filteredList = new ArrayList<>();
            query = query.toLowerCase();

            for (UrlModel urlModel : originalUrlList) {
                if (urlModel.getPageName().toLowerCase().contains(query)) {
                    filteredList.add(urlModel);
                }
            }

            urlList.clear();
            urlList.addAll(filteredList);
        }

        notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvPageName;
        protected TextView tvPageUrl;

        public ViewHolder(View view) {
            super(view);

            tvPageName = (TextView) view.findViewById(R.id.tv_page_name);
            tvPageUrl = (TextView) view.findViewById(R.id.tv_page_url);

            if (clickListener != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickListener.onClick(urlList.get(getAdapterPosition()));
                    }
                });
            }
        }
    }
}
