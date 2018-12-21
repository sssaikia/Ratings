package com.sstudio.ratings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sstudio.ratings.ItemFragment.OnListFragmentInteractionListener;
import com.sstudio.ratings.Models.com.moviemeter.Datum;
import com.sstudio.ratings.dummy.DummyContent.DummyItem;
import com.sstudio.ratings.fragments.DetailVIew;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Datum> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private int lastPosition=0;

    MyItemRecyclerViewAdapter(List<Datum> items, OnListFragmentInteractionListener listener, Context context1) {
        mValues = items;
        mListener = listener;
        context=context1;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.Title.setText(mValues.get(position).getTitle());
        holder.ContentRating.setText(mValues.get(position).getRating());
        holder.year.setText(mValues.get(position).getYear());
        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mListener.onListFragmentInteraction(holder.mItem);
                DetailVIew fragment=new DetailVIew();
                Bundle bundle = new Bundle();
                bundle.putString("imdbID", mValues.get(position).getIdIMDB());
                fragment.setArguments(bundle);
                FragmentManager manager = ((FragmentActivity)context).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.animator.enter_from_top, R.animator.exit_to_top,
                        R.animator.enter_from_top, R.animator.exit_to_top);
                transaction.add(R.id.container_lay, fragment,"detail");
                transaction.addToBackStack("detail");
                transaction.commit();
            }
        });
        holder.treailers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, TrailerPlayer.class)
                        .putExtra("imdbID", mValues.get(position).getIdIMDB()));
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.

                }
            }
        });
        if (position == getItemCount() - 1||lastPosition==getItemCount()-1) {
            ((MainActivity) context).hideFab();
        } else {
            ((MainActivity) context).showFab();
            Log.d("position : ", String.valueOf(position));
        }
        lastPosition=position;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView Title;
        final TextView ContentRating;
        public final TextView year;
        Button details,treailers;
        Datum mItem;
        ImageView poster;


        ViewHolder(View view) {
            super(view);
            mView = view;
            poster=view.findViewById(R.id.movie_meter_imageview);
            Title = view.findViewById(R.id.movie_meter_title);
            ContentRating = view.findViewById(R.id.movie_meter_rating);
            year=view.findViewById(R.id.movie_meter_year);
            details=view.findViewById(R.id.item_details);
            treailers=view.findViewById(R.id.item_trailer);
        }

        @Override
        public String toString() {
            return super.toString() /*+ " '" + mContentView.getText() + "'"*/;
        }
    }
}
