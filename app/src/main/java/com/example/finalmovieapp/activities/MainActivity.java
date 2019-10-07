package com.example.finalmovieapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalmovieapp.database.MainViewModel;
import com.example.finalmovieapp.R;
import com.example.finalmovieapp.adapter.MovieAdapter;
import com.example.finalmovieapp.database.FavoriteMovie;
import com.example.finalmovieapp.model.Movie;
import com.example.finalmovieapp.utilities.JsonUtils;
import com.example.finalmovieapp.utilities.NetworkUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {
    private ArrayList<Movie> movieArrayList;
    private RecyclerView movieRecyclerView;
    private MovieAdapter movieAdapter;
    private List<FavoriteMovie> favoriteMovies;
    private static final String popular = "popular";
    private static final String top_rated = "top_rated";
    private static final String favorite = "favorite";
    private static String sort = popular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        movieRecyclerView.setLayoutManager(layoutManager);
        movieRecyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(movieArrayList, this, this);
        movieRecyclerView.setAdapter(movieAdapter);
        favoriteMovies = new ArrayList<FavoriteMovie>();

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<FavoriteMovie>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteMovie> favoriteMovies) {
                if(favoriteMovies.size()>0) {
                    MainActivity.this.favoriteMovies.clear();
                    MainActivity.this.favoriteMovies = favoriteMovies;
                }
                getMovies();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.popular && !sort.equals(popular)) {
            ClearMovieItemList();
            sort = popular;
            getMovies();
            return true;
        }else if (id == R.id.top_rated && !sort.equals(top_rated)) {
            ClearMovieItemList();
            sort = top_rated;
            getMovies();
            return true;
        }else if (id == R.id.favorite && !sort.equals(favorite)) {
            ClearMovieItemList();
            sort = favorite;
            getMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ClearMovieItemList() {
        if (movieArrayList != null) {
            movieArrayList.clear();
        } else {
            movieArrayList = new ArrayList<Movie>();
        }
    }

    private void getMovies() {

        if (sort.equals(favorite)) {
            ClearMovieItemList();
            for (int i = 0; i< favoriteMovies.size(); i++) {
                Movie mov = new Movie(
                        String.valueOf(favoriteMovies.get(i).getId()),
                        favoriteMovies.get(i).getTitle(),
                        favoriteMovies.get(i).getReleaseDate(),
                        favoriteMovies.get(i).getVote(),
                        favoriteMovies.get(i).getPopularity(),
                        favoriteMovies.get(i).getSynopsis(),
                        favoriteMovies.get(i).getImage()
                );
                movieArrayList.add( mov );
            }
            movieAdapter.setMovieData(movieArrayList);
        } else {
            String movieQuery = sort;
            URL movieSearchUrl = NetworkUtils.buildUrl(movieQuery, getText(R.string.api_key).toString());
            new FetchMovieTask().execute(movieSearchUrl);
        }
    }


    public class FetchMovieTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String searchResults) {
            if (searchResults != null && !searchResults.equals("")) {
                movieArrayList = JsonUtils.parseMoviesJson(searchResults);
                movieAdapter.setMovieData(movieArrayList);
            }
        }
    }

    @Override
    public void OnListItemClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }
}
