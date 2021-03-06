package com.fashi.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumpException;
import com.facebook.stetho.dumpapp.DumperContext;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.fashi.popularmovies.database.AppDatabase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    public static int PAGE = 1;
    public static String LANGUAGE = "en-US";
    public static String CATEGORY = "popular";
    public static String MOST_POPULAR = "popular";
    public static String HIGHEST_RATED = "top_rated";
    public static String BASE_URL = "https://api.themoviedb.org/";
    public static final String API_KEY = "ENTER YOUR API KEY";
    public GridLayoutManager gridLayoutManager;
    public RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;
    AppDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);
        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
        textView = findViewById(R.id.title);

        launchStetho();

        mDb = AppDatabase.getsInstance(getApplicationContext());
        // get the reference of RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        fetchMovies();
        // set a GridLayoutManager with default vertical orientation and 2 number of columns
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void launchStetho() {
        Stetho.initialize(Stetho.newInitializerBuilder(getApplicationContext())
                .enableDumpapp(new DumperPluginsProvider() {
                    @Override
                    public Iterable<DumperPlugin> get() {
                        return new Stetho.DefaultDumperPluginsBuilder(getApplicationContext())
                                .provide(new DumperPlugin() {
                                    @Override
                                    public String getName() {
                                        return null;
                                    }

                                    @Override
                                    public void dump(DumperContext dumpContext) throws DumpException {

                                    }
                                })
                                .finish();
                    }
                })
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(getApplicationContext()))
                .build());
    }

    // Network call
    private void fetchMovies() {
        Call<Movie> call = Api.apiInterface().getMovies(CATEGORY, API_KEY, LANGUAGE, PAGE);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                Movie result = (Movie) response.body();
                List<Movie.ResultsBean> listofmovies = result.getResults();
                Movie.ResultsBean firstmovies = listofmovies.get(1);
                recyclerView.setAdapter(new PopularMovieAdapter(getApplicationContext(), listofmovies));

            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                shimmerFrameLayout.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // query the database and display
    private void openFavouriteActivity() {
        Intent i = new Intent(MainActivity.this, FavouriteActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.most_popular) {
            CATEGORY = MOST_POPULAR;
            fetchMovies();
            return true;
        }
        if (id == R.id.highest_rated) {
            CATEGORY = HIGHEST_RATED;
            fetchMovies();
            return true;
        }
        if (id == R.id.favourite_movies) {
            openFavouriteActivity();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmerAnimation();
    }
}

