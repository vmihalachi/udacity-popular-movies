package co.maskyn.udacitypopularmovies.data;

public class Trailer {

    public final String key;

    public Trailer(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return String.valueOf(key);
    }
}