package co.maskyn.udacitypopularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    // constant used when passing data between activities
    public static final String ARG_ITEM = "item_id";
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
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(movie.originalTitle);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (movie != null) {
            // synopsis
            ((TextView) rootView.findViewById(R.id.movie_detail)).setText(movie.plotSynopsis);
            // rating
            ((TextView) rootView.findViewById(R.id.user_rating)).setText(movie.userRating + "/10");
            // date
            Calendar cal = new GregorianCalendar();
            try {
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(movie.releaseDate));
                ((TextView) rootView.findViewById(R.id.release_date)).setText(String.valueOf(cal.get(Calendar.YEAR)));
            } catch (ParseException e) {
                Toast.makeText(getActivity(), getString(R.string.error_parsing_release_date), Toast.LENGTH_SHORT).show();
            }
            // poster
            ImageView filmPoster = (ImageView) rootView.findViewById(R.id.image);
            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/w342/" + movie.posterUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error).into(filmPoster);
        }

        return rootView;
    }
}
