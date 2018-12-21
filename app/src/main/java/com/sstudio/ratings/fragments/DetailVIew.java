package com.sstudio.ratings.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sstudio.ratings.MainActivity;
import com.sstudio.ratings.Models.trailerurl.TrailerUrl;
import com.sstudio.ratings.R;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import it.sephiroth.android.library.picasso.Picasso;
import it.sephiroth.android.library.picasso.Target;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailVIew.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailVIew#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailVIew extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String imdbID,VideoURL;
    Context context;
    private OnFragmentInteractionListener mListener;
    TextView title, rating, description, year, genre, castVIew, starsView, director;
    ImageView image,media_playButton;
    ProgressBar progressBar,media_progress;
    VideoView details_trailer;
    MediaController mediaController;

    public DetailVIew() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailVIew.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailVIew newInstance(String param1, String param2) {
        DetailVIew fragment = new DetailVIew();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    RequestQueue queue;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            imdbID = getArguments().getString("imdbID");
            Log.d("imdbId: ", imdbID);
        }
        queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "http://www.theimdbapi.org/api/movie?movie_id=" + imdbID,
                null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson g = new Gson();
                TrailerUrl t = g.fromJson(String.valueOf(response), TrailerUrl.class);
                info(t);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setTag("detail");
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_view, container, false);
        title = view.findViewById(R.id.movieName);
        rating = view.findViewById(R.id.movieRating);
        description = view.findViewById(R.id.movieDescription);
        image = view.findViewById(R.id.movieImage);
        year = view.findViewById(R.id.movieYear);
        genre = view.findViewById(R.id.movieGenre);
        castVIew = view.findViewById(R.id.full_cast);
        starsView = view.findViewById(R.id.stars);
        director = view.findViewById(R.id.director);
        details_trailer = view.findViewById(R.id.details_trailer);
        //mediaController = view.findViewById(R.id.media_controller);
        progressBar = view.findViewById(R.id.imageLoading);
        (media_progress = view.findViewById(R.id.media_progress)).setVisibility(View.GONE);
        (media_playButton = view.findViewById(R.id.media_play_button)).setVisibility(View.GONE);
        media_playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                media_progress.setVisibility(View.VISIBLE);
                try {
                    final MediaController controller = new MediaController(context);

                    media_playButton.setVisibility(View.GONE);
                    controller.setAnchorView(details_trailer);
                    details_trailer.setVideoURI(Uri.parse(VideoURL));
                    details_trailer.setMediaController(controller);
                    details_trailer.requestFocus();
                    details_trailer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            details_trailer.start();
                            mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                                @Override
                                public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                                    if (i == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                                        media_progress.setVisibility(View.VISIBLE);
                                    }
                                    if (i == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                                        media_progress.setVisibility(View.GONE);
                                    }
                                    if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                        media_progress.setVisibility(View.GONE);
                                    }
                                /*if (i == MediaPlayer.MEDIA_INFO_VIDEO_NOT_PLAYING){
                                    //media_playButton.setVisibility(View.VISIBLE);
                                    Log.d("DetailView: ","video not playing");
                                }*/
                                    return false;
                                }
                            });
                        }
                    });
                    details_trailer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                            Toast.makeText(context, "Failed. Try again..", Toast.LENGTH_SHORT).show();
                            //getActivity().getFragmentManager().popBackStack();
                            return false;
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDetailFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity)getActivity()).hideFab();
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity)getActivity()).showFab();
        mListener = null;
        details_trailer.stopPlayback();
        details_trailer.suspend();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDetailFragmentInteraction(Uri uri);
    }

    private void info(TrailerUrl t) {
        for (int i=0;i<t.getTrailer().size();i++){
            if (t.getTrailer().get(i).getDefinition().equals("auto")){
                VideoURL=t.getTrailer().get(i).getVideoUrl();
            }
        }
        media_playButton.setVisibility(View.VISIBLE);
        String cast = null, string_genre = null, stars = null, writers = null;
        for (int i = 0; i < t.getStars().size(); i++) {
            if (i == 0) {
                stars = t.getStars().get(i);

            } else {
                stars += ", " + t.getStars().get(i);
            }

            Log.v("iterating.. i==", String.valueOf(i));
        }
        for (int i = 0; i < t.getGenre().size(); i++) {
            if (i == 0) {
                string_genre = t.getGenre().get(i);
            } else {
                string_genre += ", " + t.getGenre().get(i);
            }
        }
        for (int i = 0; i < t.getCast().size(); i++) {
            if (i == 0) {
                cast = t.getCast().get(i).getName();

            } else {
                cast += ", " + t.getCast().get(i).getName();
            }

            Log.v("iterating.. i==", String.valueOf(i));
        }
        for (int i = 0; i < t.getWriters().size(); i++) {
            if (i == 0) {
                writers = t.getWriters().get(i);

            } else {
                writers += ", " + t.getWriters().get(i);
            }
        }
        castVIew.setText("Cast: " + cast);
        starsView.setText("Stars: " + stars);
        director.setText("Director: " + t.getDirector());
        genre.setText("Genre: " + string_genre + "\n\nBudget:" + t.getMetadata().getBudget() + "\nReturn: " + t.getMetadata().getGross());
        title.setText(t.getTitle());
        rating.setText("IMDB: " + t.getRating() + "\n" + "Votes: "
                + t.getRating_count() + "\n" + t.getContent_rating());
        Log.d("showInfo : ", String.valueOf(t.getRating_count()));
        description.setText("Duration: " + t.getLength() + " minutes\n\nStory: " + t.getStoryline() + "\n\nWriters: " + writers);
        Picasso.with(context)
                .load(t.getPoster().getThumb())
                .centerInside()
                .resize(300, 400)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                        image.setImageBitmap(bitmap);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onBitmapFailed(Drawable drawable) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable drawable) {

                    }
                });
        year.setText("Year: " + t.getYear() + "\n\nRelease date: " + t.getRelease_date());
    }
}
