package co.maskyn.udacitypopularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = MoviesDatabase.VERSION)
public final class MoviesDatabase {
    private MoviesDatabase(){}

    public static final int VERSION = 2;


        @Table(MoviesColumns.class) public static final String MOVIES = "movies";



}
