package com.sstudio.ratings;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sstudio.ratings.Models.trailerurl.TrailerUrl;

import org.json.JSONObject;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by Alan on 11/24/2017.
 */

public class ShowInfo {
    private Context context;
    private RequestQueue queue;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    public ShowInfo() {
    }
    public ShowInfo(Context context, RequestQueue queue){
        this.context=context;
        this.queue=queue;
    }

    public ShowInfo(RequestQueue queue) {
        this.queue=queue;
    }

    ShowInfo(Context context) {
        this.context = context;
    }

    public void showinfo(String id) {
        LinearLayout layout=new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView textView=new TextView(context);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(5,5,5,5);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
        , LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);
        textView.setLayoutParams(layoutParams);
        textView.setText("Fetching data..");
        ProgressBar p=new ProgressBar(context,null,android.R.attr.progressBarStyleHorizontal);
        p.setIndeterminate(true);
        p.setLayoutParams(layoutParams);
        p.setPadding(3,3,3,3);
        layout.addView(textView);
        layout.addView(p);
        builder= new AlertDialog.Builder(context);
        builder.setView(layout);
         dialog= builder.create();
         dialog.show();
        RequestQueue queue= Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "http://www.theimdbapi.org/api/movie?movie_id="+id,
                null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                //showData(response);
                Gson g= new Gson();
                TrailerUrl t=g.fromJson(String.valueOf(response),TrailerUrl.class);
                info(t);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void info(TrailerUrl t) {
        View dialogView = View.inflate(context, R.layout.dialog_lay, null);
        TextView title = dialogView.findViewById(R.id.movieName);
        TextView rating = dialogView.findViewById(R.id.movieRating);
        TextView description = dialogView.findViewById(R.id.movieDescription);
        ImageView image = dialogView.findViewById(R.id.movieImage);
        TextView year = dialogView.findViewById(R.id.movieYear);
        TextView genre = dialogView.findViewById(R.id.movieGenre);
        TextView actors = dialogView.findViewById(R.id.full_cast);
        Log.v("case size : ", String.valueOf(t.getCast().size()));
        String cast  = null,string_genre=null;
        for (int i=0;i<t.getCast().size();i++){
            if (i==0){
                cast=t.getCast().get(i).getName();

            }else {
                cast+=", "+t.getCast().get(i).getName();
            }

            Log.v("iterating.. i==", String.valueOf(i));
        }
        for (int i=0;i<t.getGenre().size();i++){
            if (i==0){
                string_genre=t.getGenre().get(i);
            }else {
                string_genre+=", "+t.getGenre().get(i);
            }
        }
        actors.setText("Stars: " + cast);
        genre.setText("Genre: " + string_genre);
        title.setText(t.getTitle());
        rating.setText("IMDB: " + t.getRating() + "\n" + "Votes: "
                + t.getRating_count());
        Log.d("showInfo : ",String.valueOf(t.getRating_count()));
        description.setText("Duration: " + t.getLength() + "\nStory: " + t.getStoryline());
        Picasso.with(context)
                .load(t.getPoster().getLarge())
                .centerInside()
                .resize(300,400)
                .into(image);
        year.setText("Year: " + t.getYear());
        new AlertDialog.Builder(context)
                .setView(dialogView)
                .show();
    }

    private void showData(JSONObject s) {
        try {
            View dialogView = View.inflate(context, R.layout.dialog_lay, null);
            TextView title = dialogView.findViewById(R.id.movieName);
            TextView rating = dialogView.findViewById(R.id.movieRating);
            TextView description = dialogView.findViewById(R.id.movieDescription);
            ImageView image = dialogView.findViewById(R.id.movieImage);
            TextView year = dialogView.findViewById(R.id.movieYear);
            TextView genre = dialogView.findViewById(R.id.movieGenre);
            TextView actors = dialogView.findViewById(R.id.full_cast);
            actors.setText("Cast: " + s.get("Actors"));
            genre.setText("Genre: " + s.get("Genre"));
            title.setText(s.get("Title").toString());
            rating.setText("IMDB: " + s.get("imdbRating") + "\n" + "Votes: "
                    + s.getString("imdbVotes"));
            description.setText("Duration: " + s.get("Runtime") + "\nStory: " + s.get("Plot").toString());
            Picasso.with(context)
                    .load(s.get("Poster").toString())
                    .into(image);
            year.setText("Year: " + s.get("Year").toString());
            new AlertDialog.Builder(context)
                    .setView(dialogView)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
