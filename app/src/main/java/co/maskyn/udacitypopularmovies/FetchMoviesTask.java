package co.maskyn.udacitypopularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import co.maskyn.udacitypopularmovies.adapter.MoviesRecyclerViewAdapter;
import co.maskyn.udacitypopularmovies.data.Movie;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private final WeakReference<MovieListActivity> mActivity;

    public FetchMoviesTask(MovieListActivity movieListActivity) {
        this.mActivity = new WeakReference<>(movieListActivity);
    }

    @Override
    protected ArrayList<Movie> doInBackground(Void... params) {

        // Will contain the movies
        ArrayList<Movie> movies = null;
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
                OkHttpClient client = new OkHttpClient();
                String url = String.format("http://api.themoviedb.org/3/movie/%s?api_key=%s", queryType, Constants.API_KEY);
                final Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                movies = getMoviesDataFromJson(response.body().string());
            } catch (IOException | JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
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
            recyclerView.setAdapter(new MoviesRecyclerViewAdapter(movies, mActivity.get(), mActivity.get().mTwoPane));
        }
    }

    private ArrayList<Movie> getMoviesDataFromJson(String jsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String ID = "id";
        final String POSTER_URL = "poster_path";
        final String ORIGINAL_TITLE = "original_title";
        final String PLOT_SYNOPSIS = "overview";
        final String USER_RATING = "vote_average";
        final String RELEASE_DATE = "release_date";

        JSONObject moviesJson = new JSONObject(jsonStr);
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