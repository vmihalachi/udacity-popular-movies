package co.maskyn.udacitypopularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.movie_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        new FetchFilmsTask().execute();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private static final int SORT_MOST_POPULAR = 0;
    private static final int SORT_TOP_RATED = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // change the sort type
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (item.getItemId()) {
            case R.id.most_popular:
                editor.putInt(getString(R.string.key_sort), SORT_MOST_POPULAR);
                editor.apply();
                // refresh the list
                new FetchFilmsTask().execute();
                break;
            case R.id.top_rated:
                editor.putInt(getString(R.string.key_sort), SORT_TOP_RATED);
                editor.apply();
                // refresh the list
                new FetchFilmsTask().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Movie> mValues;

        public SimpleItemRecyclerViewAdapter(List<Movie> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            Picasso.with(holder.mContentView.getContext())
                    .load("http://image.tmdb.org/t/p/w780/" + holder.mItem.posterUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error).into(holder.mContentView);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(MovieDetailFragment.ARG_ITEM, holder.mItem);
                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra(MovieDetailFragment.ARG_ITEM, holder.mItem);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mContentView;
            public Movie mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (ImageView) view.findViewById(R.id.image);
            }
        }
    }

    public class FetchFilmsTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchFilmsTask.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            // Will contain the movies
            ArrayList<Movie> movies = new ArrayList<>();

            String key = "";
            String queryType;
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            int sortType = sharedPref.getInt(getResources().getString(R.string.key_sort), 0);
            switch (sortType) {
                case SORT_MOST_POPULAR:
                    queryType = "popular";
                    break;
                case SORT_TOP_RATED:
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
            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            // new data
            if (movies != null) {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movie_list);
                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(movies));
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
}
