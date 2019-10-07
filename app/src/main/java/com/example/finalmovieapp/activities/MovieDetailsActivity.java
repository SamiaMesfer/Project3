package com.example.finalmovieapp.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalmovieapp.AppExecutors;
import com.example.finalmovieapp.R;
import com.example.finalmovieapp.adapter.TrailerAdapter;
import com.example.finalmovieapp.database.FavoriteMovie;
import com.example.finalmovieapp.database.MovieDatabase;
import com.example.finalmovieapp.model.Movie;
import com.example.finalmovieapp.model.Review;
import com.example.finalmovieapp.model.Trailer;
import com.example.finalmovieapp.utilities.JsonUtils;
import com.example.finalmovieapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity implements TrailerAdapter.ListItemClickListener{
    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    private Movie movie;
    private ArrayList<Review> reviewArrayList;
    private ArrayList<Trailer> trailerArrayList;
    private RecyclerView recyclerView;
    private TrailerAdapter trailerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private MovieDatabase movieDatabase;
    private ImageView favbutton;
    private Boolean isFavourit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError("intent is null");
        }

        movie = (Movie) intent.getSerializableExtra("movie");
        if (movie == null) {
            closeOnError("movie is null");
            return;
        }


        recyclerView = findViewById(R.id.rv_trailers);
        trailerAdapter = new TrailerAdapter(this, trailerArrayList, this);
        recyclerView.setAdapter(trailerAdapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        favbutton = findViewById(R.id.favButton);
        movieDatabase = MovieDatabase.getInstance(getApplicationContext());

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final FavoriteMovie favoriteMovie = movieDatabase.movieDao().loadMovieById(Integer.parseInt(movie.getId()));
                setFavorite((favoriteMovie != null)? true : false);
            }
        });


        getMoreDetails(movie.getId());

    }

    private void setFavorite(Boolean fav){
        if (fav) {
            isFavourit = true;
            favbutton.setImageResource(R.drawable.ic_favorite_solid);
        } else {
            isFavourit = false;
            favbutton.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private static class SearchURLs {
        URL reviewUrl;
        URL trailerUrl;
        SearchURLs(URL reviewSearchUrl, URL trailerSearchUrl){
            this.reviewUrl = reviewSearchUrl;
            this.trailerUrl = trailerSearchUrl;
        }
    }
    private static class ResultsStrings {
        String review;
        String trailer;
        ResultsStrings(String reviewStr, String trailerStr){
            this.review = reviewStr;
            this.trailer = trailerStr;
        }
    }
    private void getMoreDetails(String id) {
        String reviewQuery = id + File.separator + "reviews";
        String trailerQuery = id + File.separator + "videos";
        SearchURLs searchURLs = new SearchURLs(
                NetworkUtils.buildUrl(reviewQuery, getText(R.string.api_key).toString()),
                NetworkUtils.buildUrl(trailerQuery, getText(R.string.api_key).toString())
                );
        new FetchReviewsTask().execute(searchURLs);
    }



    public class FetchReviewsTask extends AsyncTask<SearchURLs, Void, ResultsStrings> {
        @Override
        protected ResultsStrings doInBackground(SearchURLs... params) {
            URL reviewUrl = params[0].reviewUrl;
            URL trailerUrl = params[0].trailerUrl;

            String reviewResults = null;
            try {
                reviewResults = NetworkUtils.getResponseFromHttpUrl(reviewUrl);
                reviewArrayList = JsonUtils.parseReviewsJson(reviewResults);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String trailerResults = null;
            try {
                trailerResults = NetworkUtils.getResponseFromHttpUrl(trailerUrl);
                trailerArrayList = JsonUtils.parseTrailersJson(trailerResults);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ResultsStrings results = new ResultsStrings(reviewResults,trailerResults);

            return results;
        }

        @Override
        protected void onPostExecute(ResultsStrings results) {
            String searchResults = results.review;
            if (searchResults != null && !searchResults.equals("")) {
                reviewArrayList = JsonUtils.parseReviewsJson(searchResults);
                showDetails();
            }
        }
    }

    @Override
    public void OnListItemClick(Trailer trailerItem) {
        Log.d(TAG,trailerItem.getKey());
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerItem.getKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + trailerItem.getKey()));
        webIntent.putExtra("finish_on_ended", true);
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    private void showDetails() {

        ((TextView)findViewById(R.id.title)).setText(movie.getTitle());
        ((TextView)findViewById(R.id.lable_rating)).append(" ("+ movie.getVote()+"/10)");
        ((RatingBar)findViewById(R.id.rating)).setRating(Float.parseFloat(movie.getVote()));
        ((TextView)findViewById(R.id.release_date)).setText(movie.getReleaseDate());
        ((TextView)findViewById(R.id.synopsis)).setText(movie.getSynopsis());
        favbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final FavoriteMovie mov = new FavoriteMovie(
                        Integer.parseInt(movie.getId()),
                        movie.getTitle(),
                        movie.getReleaseDate(),
                        movie.getVote(),
                        movie.getPopularity(),
                        movie.getSynopsis(),
                        movie.getImage());
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (isFavourit) {
                            movieDatabase.movieDao().deleteMovie(mov);
                        } else {
                            movieDatabase.movieDao().insertMovie(mov);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setFavorite(!isFavourit);
                            }
                        });
                    }

                });
            }
        });
        trailerAdapter.setTrailerData(trailerArrayList);
        ((TextView)findViewById(R.id.reviews)).setText("");
        for(int i = 0; i< reviewArrayList.size(); i++) {
            ((TextView)findViewById(R.id.reviews)).append("\n");
            ((TextView)findViewById(R.id.reviews)).append(reviewArrayList.get(i).getContent());
            ((TextView)findViewById(R.id.reviews)).append("\n\n");
            ((TextView)findViewById(R.id.reviews)).append(" - Reviewed by ");
            ((TextView)findViewById(R.id.reviews)).append(reviewArrayList.get(i).getAuthor());
            ((TextView)findViewById(R.id.reviews)).append("\n\n--------------\n");
        }

        String imagePathURL = NetworkUtils.buildPosterUrl(movie.getImage());

        try {
            Picasso.with(this)
                    .load(imagePathURL)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into((ImageView)this.findViewById(R.id.image));
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

    }

    private void closeOnError(String msg) {
        finish();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
