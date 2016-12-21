package co.maskyn.udacitypopularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface MoviesColumns {

    // movie id in the online database
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    public static final String MOVIE_ID = "_id";

    // poster url
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String POSTER_URL =
            "poster_url";

    // original title
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String ORIGINAL_TITLE =
            "original_title";

    // plot synopsis
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String PLOT_SYNOPSIS =
            "plot_synopsis";

    // user rating
    @DataType(DataType.Type.REAL) @NotNull
    public static final String USER_RATING =
            "user_rating";

    // release_date
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String RELEASE_DATE =
            "release_date";
}

