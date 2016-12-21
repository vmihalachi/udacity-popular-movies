package co.maskyn.udacitypopularmovies.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import co.maskyn.udacitypopularmovies.MovieDetailActivity;
import co.maskyn.udacitypopularmovies.MovieDetailFragment;
import co.maskyn.udacitypopularmovies.R;
import co.maskyn.udacitypopularmovies.data.Movie;
import co.maskyn.udacitypopularmovies.data.MoviesColumns;
import co.maskyn.udacitypopularmovies.data.MoviesProvider;

/**
 * Created by sam_chordas on 8/12/15.
 * Credit to skyfishjy gist:
 *    https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */
public class MoviesCursorAdapter extends CursorRecyclerViewAdapter<MoviesCursorAdapter.ViewHolder> {

    private final AppCompatActivity mContext;
    private final boolean mTwoPane;

    public MoviesCursorAdapter(AppCompatActivity context, Cursor cursor, boolean twoPane) {
        super(context, cursor);
        mContext = context;
        mTwoPane = twoPane;
    }


    @Override
    public MoviesCursorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_content, parent, false);
        return new MoviesCursorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviesCursorAdapter.ViewHolder holder, Cursor cursor) {
        holder.mItem = new Movie(
                cursor.getInt(cursor.getColumnIndex(MoviesColumns.MOVIE_ID)),
                cursor.getString(cursor.getColumnIndex(MoviesColumns.POSTER_URL)),
                cursor.getString(cursor.getColumnIndex(MoviesColumns.ORIGINAL_TITLE)),
                cursor.getString(cursor.getColumnIndex(MoviesColumns.PLOT_SYNOPSIS)),
                cursor.getDouble(cursor.getColumnIndex(MoviesColumns.USER_RATING)),
                cursor.getString(cursor.getColumnIndex(MoviesColumns.RELEASE_DATE))
        );

        DatabaseUtils.dumpCursor(cursor);

        Picasso.with(holder.mContentView.getContext())
                .load("http://image.tmdb.org/t/p/w780/" + holder.mItem.posterUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.mContentView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(MovieDetailFragment.ARG_ITEM, holder.mItem);
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    mContext.getSupportFragmentManager().beginTransaction()
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

    /*@Override
    public int getItemCount() {
        return cur.size();
    }*/

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
