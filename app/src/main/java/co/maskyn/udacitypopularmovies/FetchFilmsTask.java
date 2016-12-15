package co.maskyn.udacitypopularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FetchFilmsTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

    private final String LOG_TAG = FetchFilmsTask.class.getSimpleName();
    private final WeakReference<MovieListActivity> mActivity;

    public FetchFilmsTask(MovieListActivity movieListActivity) {
        this.mActivity = new WeakReference<>(movieListActivity);
    }

    @Override
    protected ArrayList<Movie> doInBackground(Void... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;
        // Will contain the movies
        ArrayList<Movie> movies = new ArrayList<>();
        String key = "";
        String queryType;

        if (mActivity.get() != null) {

            SharedPreferences sharedPref = mActivity.get().getPreferences(Context.MODE_PRIVATE);
            int sortType = sharedPref.getInt(mActivity.get().getResources().getString(R.string.key_sort), 0);
            switch (sortType) {
                case Constants.SORT_MOST_POPULAR:
                    queryType = "popular";
                    break;
                case Constants.SORT_TOP_RATED:
                    queryType = "top_rated";
                    break;
                default:
                    queryType = "popular";
                    break;

            }

            try {
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/movie";
                final String KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendEncodedPath(queryType)
                        .appendQueryParameter(KEY_PARAM, key)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, moviesJsonStr);
                movies = getMoviesDataFromJson(moviesJsonStr);
            } catch (IOException | JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, or there was an error when parsin the JSON
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }

        return movies;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        super.onPostExecute(movies);
        // new data
        if (movies != null && mActivity.get() != null) {
            RecyclerView recyclerView = (RecyclerView) mActivity.get().findViewById(R.id.movie_list);
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(movies, mActivity.get(), mActivity.get().mTwoPane));
        }
    }

    private ArrayList<Movie> getMoviesDataFromJson(String filmJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String ID = "id";
        final String POSTER_URL = "poster_path";
        final String ORIGINAL_TITLE = "original_title";
        final String PLOT_SYNOPSIS = "overview";
        final String USER_RATING = "vote_average";
        final String RELEASE_DATE = "release_date";

        JSONObject moviesJson = new JSONObject(filmJsonStr);
        JSONArray movies = moviesJson.getJSONArray(RESULTS);

        ArrayList<Movie> result = new ArrayList<>();
        for (int i = 0; i < movies.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject movieJson = movies.getJSONObject(i);
            int id = movieJson.getInt(ID);
            String posterUrl = movieJson.getString(POSTER_URL);
            String originalTitle = movieJson.getString(ORIGINAL_TITLE);
            String plotSynopsis = movieJson.getString(PLOT_SYNOPSIS);
            double userRating = movieJson.getDouble(USER_RATING);
            String releaseDate = movieJson.getString(RELEASE_DATE);
            // add the film to the arraylist
            result.add(new Movie(id, posterUrl, originalTitle, plotSynopsis, userRating, releaseDate));
        }
        return result;

    }
}