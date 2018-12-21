package com.sstudio.ratings;

/**
 * Created by Alan on 11/21/2017.
 */


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sstudio.ratings.Models.MovieModels;
import com.sstudio.ratings.fragments.DetailVIew;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by souvik on 27/11/16.
 */

public class MovieAdapter extends ArrayAdapter {

    private int lastPosition=0;
    private List<MovieModels> movieModelsList;
    private int resource;
    private Context context;

    public MovieAdapter(Context context, int resource, List<MovieModels> objects) {
        super(context, resource, objects);
        movieModelsList = objects;
        this.resource = resource;
        this.context = context;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.content, null);

        }

        TextView name;
        ImageView imageView;
        TextView rating;

        name = convertView.findViewById(R.id.name);
        imageView = convertView.findViewById(R.id.movieIcon);
        rating = convertView.findViewById(R.id.ratingBar);
        rating.setText(movieModelsList.get(position).getYear());
        name.setText(/*"#"+(position+1)+" "+*/movieModelsList.get(position).getName());
        Picasso.with(getContext()).load(movieModelsList.get(position).getImageURL()).into(imageView);
        Button info = convertView.findViewById(R.id.info);
        Button trail = convertView.findViewById(R.id.trailer_video);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailVIew fragment = new DetailVIew();
                Bundle bundle = new Bundle();
                bundle.putString("imdbID", movieModelsList.get(position).getImdbID());
                fragment.setArguments(bundle);
                FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.animator.enter_from_top, R.animator.exit_to_top,
                        R.animator.enter_from_top, R.animator.exit_to_top);
                transaction.add(R.id.container_lay, fragment, "detail");
                transaction.addToBackStack("detail");
                transaction.commit();

            }
        });
        trail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, TrailerPlayer.class)
                        .putExtra("imdbID", movieModelsList.get(position).getImdbID()));
            }
        });

        if (position == getCount() - 1||lastPosition==getCount()-1) {
            ((MainActivity) context).hideFab();
        } else {
            ((MainActivity) context).showFab();
            Log.d("position : ", String.valueOf(position));
        }
        lastPosition=position;
        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}