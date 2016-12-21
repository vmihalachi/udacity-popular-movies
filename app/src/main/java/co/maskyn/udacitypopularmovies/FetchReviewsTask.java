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

import co.maskyn.udacitypopularmovies.adapter.ReviewsRecyclerViewAdapter;
import co.maskyn.udacitypopularmovies.data.Review;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchReviewsTask extends AsyncTask<Void, Void, ArrayList<Review>> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private final WeakReference<Activity> mActivity;
    private RecyclerView mRecyclerView;
    private int mMovieId;
    private ReviewsRecyclerViewAdapter.ReviewListCallBack mCallBack;

    public FetchReviewsTask(Activity activity, ReviewsRecyclerViewAdapter.ReviewListCallBack callBack, RecyclerView recyclerView, int movieId) {
        this.mActivity = new WeakReference<>(activity);
        this.mRecyclerView = recyclerView;
        this.mMovieId = movieId;
        this.mCallBack = callBack;
    }

    @Override
    protected ArrayList<Review> doInBackground(Void... params) {

        // Will contain the movies
        ArrayList<Review> reviews;

        try {
            OkHttpClient client = new OkHttpClient();
            String url = String.format("http://api.themoviedb.org/3/movie/%d/reviews?api_key=%s", mMovieId, Constants.API_KEY);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            reviews = getMoviesDataFromJson(response.body().string());
        } catch (IOException | JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, or there was an error when parsin the JSON
            return null;
        }

        return reviews;
    }

    @Override
    protected void onPostExecute(ArrayList<Review> reviews) {
        super.onPostExecute(reviews);
        // new data
        if (reviews != null && mActivity.get() != null) {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity.get());
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            ReviewsRecyclerViewAdapter mAdapter = new ReviewsRecyclerViewAdapter(mCallBack, reviews);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private ArrayList<Review> getMoviesDataFromJson(String jsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";

        JSONObject reviewsJson = new JSONObject(jsonStr);
        JSONArray reviews = reviewsJson.getJSONArray(RESULTS);

        ArrayList<Review> result = new ArrayList<>();
        for (int i = 0; i < reviews.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject movieJson = reviews.getJSONObject(i);
            String author = movieJson.getString(AUTHOR);
            String content = movieJson.getString(CONTENT);
            String url = movieJson.getString(URL);
            // add the film to the arraylist
            result.add(new Review(author, content, url));
        }
        return result;

    }
}