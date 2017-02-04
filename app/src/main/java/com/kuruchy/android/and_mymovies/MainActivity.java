/*
 * MIT License
 *
 * Copyright (c) 2017.  Bruno Retolaza
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.kuruchy.android.and_mymovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.net.URL;

/**
 * Main Activity Class.
 *
 */
public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TheMovieDatabaseNetworkUtils.setApi_key(getBaseContext().getString(R.string.mdb_id));

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        loadFetchMovieData(TheMovieDatabaseNetworkUtils.SORTING_PARAM);

    }

    /**
     * Calls the Async Task that will fetch the movie data.
     *
     * @param sorting The sorting method to run the Async Task.
     */
    private void loadFetchMovieData(String sorting) {
        new FetchMovieData().execute(sorting);
    }

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("movie_obj", (Parcelable) movie);
        startActivity(intentToStartDetailActivity);
    }

    /**
     * Fetch Movie Data Class.
     *
     * Extends from AsyncTask. Allowing to run a the movie list update on a background thread,
     * while publishing the results to the UI thread.
     */
    public class FetchMovieData extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            URL movieRequestURL = TheMovieDatabaseNetworkUtils.buildUrl(params[0]);

            try {
                String jsonMovieResponse = TheMovieDatabaseNetworkUtils
                        .getResponseFromHttpUrl(movieRequestURL);

                Movie[] movieDataArray = TheMovieDatabaseJsonUtils
                        .getMovieArrayFromJSONData(MainActivity.this, jsonMovieResponse);

                return movieDataArray;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movieData) {
            if (movieData != null) {
                mMovieAdapter.setmMoviesData(movieData);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.sort_by_popular:
                TheMovieDatabaseNetworkUtils.SORTING_PARAM = TheMovieDatabaseNetworkUtils.POPULAR;
                mMovieAdapter = new MovieAdapter(this);
                mRecyclerView.setAdapter(mMovieAdapter);
                loadFetchMovieData(TheMovieDatabaseNetworkUtils.SORTING_PARAM);
                return true;

            case R.id.sort_by_top:
                TheMovieDatabaseNetworkUtils.SORTING_PARAM = TheMovieDatabaseNetworkUtils.TOP_RATED;
                mMovieAdapter = new MovieAdapter(this);
                mRecyclerView.setAdapter(mMovieAdapter);
                loadFetchMovieData(TheMovieDatabaseNetworkUtils.SORTING_PARAM);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
