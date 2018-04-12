package photochange.tosya.photochange.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import photochange.tosya.photochange.R;
import photochange.tosya.photochange.content.DropBoxListContent.DropBoxItem;
import photochange.tosya.photochange.custom.RoundImageView;
import photochange.tosya.photochange.fragments.DropBoxListFragment.OnListFragmentInteractionListener;


public class DropBoxListRecyclerViewAdapter extends RecyclerView.Adapter<DropBoxListRecyclerViewAdapter.ViewHolder> {

    private final List<DropBoxItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public DropBoxListRecyclerViewAdapter(List<DropBoxItem> items, OnListFragmentInteractionListener listener) {
        setHasStableIds(true);
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mAvatar.setImageBitmap(mValues.get(position).avatar);
        holder.mName.setText(mValues.get(position).name);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mAvatar;
        public final TextView mName;
        public DropBoxItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAvatar = view.findViewById(R.id.photograph_avatar);
            mName = view.findViewById(R.id.photograph_name);
        }
    }
}
