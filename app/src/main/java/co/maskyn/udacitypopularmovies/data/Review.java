package co.maskyn.udacitypopularmovies.data;

public class Review {

    public final String author;
    public final String content;
    public final String url;

    public Review(String author, String content, String url) {
        this.author = author;
        this.content = content;
        this.url = url;
    }

    @Override
    public String toString() {
        return String.valueOf(url);
    }
}