package com.example.finalmovieapp.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<FavoriteMovie>> movies;
    public MainViewModel(Application application) {
        super(application);
        MovieDatabase database = MovieDatabase.getInstance(this.getApplication());
        movies = database.movieDao().loadAllMovies();
    }

    public LiveData<List<FavoriteMovie>> getMovies() {
        return movies;
    }
}
