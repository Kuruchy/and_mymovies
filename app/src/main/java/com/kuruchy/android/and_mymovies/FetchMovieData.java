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

import android.os.AsyncTask;

import java.net.URL;

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
                    .getMovieArrayFromJSONData(jsonMovieResponse);
            return movieDataArray;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Movie[] movieData) {
        if (movieData != null) {
            MainActivity.getmMovieAdapter().setmMoviesData(movieData);
        }
    }
}