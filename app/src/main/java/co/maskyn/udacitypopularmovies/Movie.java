package co.maskyn.udacitypopularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    public final int id;
    public final String posterUrl;
    public final String originalTitle;
    public final String plotSynopsis;
    public final double userRating;
    public final String releaseDate;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(posterUrl);
        out.writeString(originalTitle);
        out.writeString(plotSynopsis);
        out.writeDouble(userRating);
        out.writeString(releaseDate);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        id = in.readInt();
        this.posterUrl = in.readString();
        this.originalTitle = in.readString();
        this.plotSynopsis = in.readString();
        this.userRating = in.readDouble();
        this.releaseDate = in.readString();
    }

    public Movie(int id, String posterUrl, String originalTitle, String plotSynopsis, double userRating, String releaseDate) {
        this.id = id;
        this.posterUrl = posterUrl;
        this.originalTitle = originalTitle;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    @Override
        public String toString() {
            return String.valueOf(id);
        }
    }