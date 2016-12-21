package co.maskyn.udacitypopularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.maskyn.udacitypopularmovies.R;
import co.maskyn.udacitypopularmovies.data.Trailer;

public class TrailersRecyclerViewAdapter extends RecyclerView.Adapter<TrailersRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Trailer> mDataset;
    private TrailerListCallBack mCallBack;
    private String mTrailerText;

    // Provide a suitable constructor (depends on the kind of dataset)
    public TrailersRecyclerViewAdapter(TrailerListCallBack callBack, ArrayList<Trailer> myDataset, String trailerText) {
        this.mCallBack = callBack;
        this.mDataset = myDataset;
        this.mTrailerText = trailerText;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TrailersRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_list_content, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(String.format(mTrailerText, holder.getAdapterPosition()+1));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.onTrailerClicked(mDataset.get(holder.getAdapterPosition()).key);
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
        public View mView;
        public TextView mTextView;
        public ImageView mPlayIcon;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            mTextView = (TextView) v.findViewById(android.R.id.text1);
            mPlayIcon = (ImageView) v.findViewById(android.R.id.icon1);
        }
    }

    public interface TrailerListCallBack {
        void onTrailerClicked(String key);
    }
}

