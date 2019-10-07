package com.example.finalmovieapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalmovieapp.R;
import com.example.finalmovieapp.model.Trailer;
import java.util.ArrayList;
import java.util.List;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private final Context context;
    private List<Trailer> trailerList;
    final private ListItemClickListener clickListener;

    public interface ListItemClickListener {
        void OnListItemClick(Trailer trailerItem);
    }

    public TrailerAdapter(Context mContext, ArrayList<Trailer> items, ListItemClickListener listener) {
        this.context = mContext;
        trailerList = items;
        clickListener = listener;
    }

    @NonNull
    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return trailerList == null ? 0 : trailerList.size();
    }

    public void setTrailerData(List<Trailer> trailerItemList) {
        trailerList = trailerItemList;
        notifyDataSetChanged();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView listTrailerItemView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            listTrailerItemView = itemView.findViewById(R.id.trailer_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            clickListener.OnListItemClick(trailerList.get(clickedPosition));
        }

        public void bind(int position) {
            listTrailerItemView.setText(trailerList.get(position).getName());
        }
    }
}
