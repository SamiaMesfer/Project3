package com.example.finalmovieapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalmovieapp.R;
import com.example.finalmovieapp.model.Movie;
import com.example.finalmovieapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private List<Movie> movieList;
    private final Context context;
    final private ListItemClickListener clickListener;

    public interface ListItemClickListener {
        void OnListItemClick(Movie movieItem);
    }

    public MovieAdapter(List<Movie> movieItemList, ListItemClickListener listener, Context context) {

        movieList = movieItemList;

        clickListener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movieList == null ? 0 : movieList.size();
    }

    public void setMovieData(List<Movie> movieItemList) {
        movieList = movieItemList;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView listMovieItemView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            listMovieItemView = itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            Movie movieItem = movieList.get(listIndex);
            listMovieItemView = itemView.findViewById(R.id.movie_poster);
            String posterPathURL = NetworkUtils.buildPosterUrl(movieItem.getImage());
            try {
                Picasso.with(context)
                        .load(posterPathURL)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(listMovieItemView);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            clickListener.OnListItemClick(movieList.get(clickedPosition));
        }
    }

}
