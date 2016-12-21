package co.maskyn.udacitypopularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.maskyn.udacitypopularmovies.adapter.MoviesCursorAdapter;
import co.maskyn.udacitypopularmovies.data.MoviesProvider;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    boolean mTwoPane;
    // cursor adapter for favorite movies
    private MoviesCursorAdapter mCursorAdapter;
    // id of the loader
    private static final int CURSOR_LOADER_ID = 0;

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

        mCursorAdapter = new MoviesCursorAdapter(this, null, mTwoPane);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int sortType = sharedPref.getInt(getString(R.string.key_sort), 0);
        switch (sortType) {
            case Constants.SORT_MOST_POPULAR:
            case Constants.SORT_TOP_RATED:
                new FetchMoviesTask(this).execute();
                break;
            default:
                ((RecyclerView) recyclerView).setAdapter(mCursorAdapter);
                break;

        }

        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // change the sort type
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (item.getItemId()) {
            case R.id.most_popular:
                editor.putInt(getString(R.string.key_sort), Constants.SORT_MOST_POPULAR);
                editor.apply();
                // refresh the list
                new FetchMoviesTask(this).execute();
                break;
            case R.id.top_rated:
                editor.putInt(getString(R.string.key_sort), Constants.SORT_TOP_RATED);
                editor.apply();
                // refresh the list
                new FetchMoviesTask(this).execute();
                break;
            case R.id.favorites:
                editor.putInt(getString(R.string.key_sort), Constants.SORT_FAVORITES);
                editor.apply();
                // refresh the list
                ((RecyclerView) findViewById(R.id.movie_list)).setAdapter(mCursorAdapter);
                //new FetchFilmsTask(this).execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        return new CursorLoader(MovieListActivity.this, MoviesProvider.Movies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mCursorAdapter.swapCursor(null);
    }
}
