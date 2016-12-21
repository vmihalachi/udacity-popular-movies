package co.maskyn.udacitypopularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import co.maskyn.udacitypopularmovies.R;
import co.maskyn.udacitypopularmovies.data.Review;

public class ReviewsRecyclerViewAdapter extends RecyclerView.Adapter<ReviewsRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Review> mDataset;
    private ReviewListCallBack mCallBack;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewsRecyclerViewAdapter(ReviewListCallBack callBack, ArrayList<Review> myDataset) {
        this.mDataset = myDataset;
        this.mCallBack = callBack;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_content, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextAuthor.setText(mDataset.get(holder.getAdapterPosition()).author);
        holder.mTextContent.setText(mDataset.get(holder.getAdapterPosition()).content);
        holder.mReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.onReviewClicked(mDataset.get(holder.getAdapterPosition()).url);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextAuthor;
        public TextView mTextContent;
        public TextView mReadMore;

        public ViewHolder(View v) {
            super(v);
            mTextAuthor = (TextView) v.findViewById(android.R.id.text1);
            mTextContent = (TextView) v.findViewById(android.R.id.text2);
            mReadMore = (TextView) v.findViewById(R.id.read_more);
        }
    }

    public interface ReviewListCallBack {
        void onReviewClicked(String url);
    }
}

