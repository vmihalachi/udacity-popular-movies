package co.maskyn.udacitypopularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.maskyn.udacitypopularmovies.adapter.ReviewsRecyclerViewAdapter;
import co.maskyn.udacitypopularmovies.adapter.TrailersRecyclerViewAdapter;
import co.maskyn.udacitypopularmovies.data.Movie;
import co.maskyn.udacitypopularmovies.data.MoviesColumns;
import co.maskyn.udacitypopularmovies.data.MoviesProvider;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment implements ReviewsRecyclerViewAdapter.ReviewListCallBack, TrailersRecyclerViewAdapter.TrailerListCallBack{

    // logcat tag
    public static final String TAG = MovieDetailFragment.class.getName();
    // constant used when passing data between activities
    public static final String ARG_ITEM = "item_id";
    // for the share menu item
    private ShareActionProvider mShareActionProvider;
    // movie description
    @BindView(R.id.movie_detail)
    TextView textDetail;
    // movie rating
    @BindView(R.id.user_rating)
    TextView textRating;
    // release date
    @BindView(R.id.release_date)
    TextView textReleaseDate;
    // poster
    @BindView(R.id.image)
    ImageView poster;
    // trailers list
    @BindView(R.id.trailers_list)
    RecyclerView listTrailers;
    // reviews list
    @BindView(R.id.reviews_list)
    RecyclerView listReviews;
    // button add to favorites
    @BindView(R.id.add_to_favorites)
    Button addToFavorites;
    // the film
    private Movie movie;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // if the previous activity passed a film
        if (getArguments().containsKey(ARG_ITEM)) {
            // the film
            movie = getArguments().getParcelable(ARG_ITEM);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // if the film is not null
        if (movie != null) {
            // set the title to the toolbar
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(movie.originalTitle);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        // bind butterknife
        ButterKnife.bind(this, rootView);
        // if the movie is not null we set the various views
        if (movie != null) {
            // synopsis
            textDetail.setText(movie.plotSynopsis);
            // rating
            textRating.setText(String.format("%s/10", movie.userRating));
            // date
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(movie.releaseDate));
                textReleaseDate.setText(String.valueOf(cal.get(Calendar.YEAR)));
            } catch (ParseException e) {
                Toast.makeText(getActivity(), getString(R.string.error_parsing_release_date), Toast.LENGTH_SHORT).show();
            }
            // poster
            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/w342/" + movie.posterUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error).into(poster);

            // trailer list
            new FetchTrailersTask(getActivity(), this, listTrailers, movie.id).execute();
            // reviews list
            new FetchReviewsTask(getActivity(), this, listReviews, movie.id).execute();

            // button favorites
            // query to find if the film is already in the favorites
            Cursor c = getActivity().getContentResolver().query(MoviesProvider.Movies.CONTENT_URI,
                    new String[] {MoviesColumns.MOVIE_ID}, MoviesColumns.MOVIE_ID + " LIKE ?", new String[] {String.valueOf(movie.id)}, null);
            if (c != null){
                // if we already have that element in the favorites
                if (c.getCount() != 0)
                    // we disaable the buton
                    addToFavorites.setEnabled(false);
                c.close();
            }
        }

        return rootView;
    }

    @Override
    public void onReviewClicked(String url) {
        // open the browser
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void onTrailerClicked(String key) {
        // open youtube
        String url = String.format("https://www.youtube.com/watch?v=%s", key);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @OnClick(R.id.add_to_favorites)
    public void addToFavorites() {
        long cursorId = movie.id;
        ContentValues cv = new ContentValues();
        cv.put(MoviesColumns.MOVIE_ID, movie.id);
        cv.put(MoviesColumns.POSTER_URL, movie.posterUrl);
        cv.put(MoviesColumns.ORIGINAL_TITLE, movie.originalTitle);
        cv.put(MoviesColumns.PLOT_SYNOPSIS, movie.plotSynopsis);
        cv.put(MoviesColumns.USER_RATING, movie.userRating);
        cv.put(MoviesColumns.RELEASE_DATE, movie.releaseDate);

        // add the movie
        getActivity().getContentResolver().insert(MoviesProvider.Movies.withId(cursorId), cv);
        // we can't add the movie to the favorites two times..
        addToFavorites.setEnabled(false);
    }
}
