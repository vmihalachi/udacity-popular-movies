package co.maskyn.udacitypopularmovies;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import co.maskyn.udacitypopularmovies.adapter.TrailersRecyclerViewAdapter;
import co.maskyn.udacitypopularmovies.data.Trailer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchTrailersTask extends AsyncTask<Void, Void, ArrayList<Trailer>> {

    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private final WeakReference<Activity> mActivity;
    private TrailersRecyclerViewAdapter.TrailerListCallBack mCallBack;
    private RecyclerView mRecyclerView;
    private int mMovieId;

    public FetchTrailersTask(Activity activity, TrailersRecyclerViewAdapter.TrailerListCallBack callBack, RecyclerView recyclerView, int movieId) {
        this.mActivity = new WeakReference<>(activity);
        this.mCallBack = callBack;
        this.mRecyclerView = recyclerView;
        this.mMovieId = movieId;
    }

    @Override
    protected ArrayList<Trailer> doInBackground(Void... params) {

        // Will contain the movies
        ArrayList<Trailer> trailers;

        try {
            OkHttpClient client = new OkHttpClient();
            String url = String.format("http://api.themoviedb.org/3/movie/%d/videos?api_key=%s", mMovieId, Constants.API_KEY);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            trailers = getMoviesDataFromJson(response.body().string());
        } catch (IOException | JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, or there was an error when parsin the JSON
            return null;
        }

        return trailers;
    }

    @Override
    protected void onPostExecute(ArrayList<Trailer> trailers) {
        super.onPostExecute(trailers);
        // new data
        if (trailers != null && mActivity.get() != null) {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity.get());
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            TrailersRecyclerViewAdapter mAdapter = new TrailersRecyclerViewAdapter(mCallBack, trailers, mActivity.get().getString(R.string.trailer_number));
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private ArrayList<Trailer> getMoviesDataFromJson(String jsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String KEY = "key";

        JSONObject reviewsJson = new JSONObject(jsonStr);
        JSONArray reviews = reviewsJson.getJSONArray(RESULTS);

        ArrayList<Trailer> result = new ArrayList<>();
        for (int i = 0; i < reviews.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject movieJson = reviews.getJSONObject(i);
            String key = movieJson.getString(KEY);
            // add the film to the arraylist
            result.add(new Trailer(key));
        }
        return result;

    }
}