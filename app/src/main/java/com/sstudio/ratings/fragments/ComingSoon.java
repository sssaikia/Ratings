package com.sstudio.ratings.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sstudio.ratings.Models.MovieModels;
import com.sstudio.ratings.Models.intheater.Movie;
import com.sstudio.ratings.MovieAdapter;
import com.sstudio.ratings.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComingSoon.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComingSoon#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComingSoon extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ComingSoonFrag: ";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    RequestQueue queue;
    Context context;
    private List<MovieModels> list=new ArrayList<>();
    private MovieAdapter m;
    private ListView listView;
    private ProgressBar progressBar;

    public ComingSoon() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComingSoon.
     */
    // TODO: Rename and change types and number of parameters
    public static ComingSoon newInstance(String param1, String param2) {
        ComingSoon fragment = new ComingSoon();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        queue= Volley.newRequestQueue(context);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_coming_soon, container, false);
        listView=view.findViewById(R.id.comingSoon_listview);
        progressBar = view.findViewById(R.id.comingSoon_progress);
        progressBar.setIndeterminate(true);
        comingSoon("http://cctvproject.16mb.com/api/comingsoon.json");
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onComingSoonFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        mListener = null;
        //queue.stop();
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
        void onComingSoonFragmentInteraction(Uri uri);
    }
    public void comingSoon(String url) {
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                list.clear();
                try {
                    Gson gson=new Gson();
                    JSONObject top = new JSONObject(response.toString());
                    JSONObject obj = top.getJSONObject("data");
                    JSONArray jsonArray = obj.getJSONArray("comingSoon");
                    Log.d(TAG,jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        obj = jsonArray.getJSONObject(i);
                        JSONArray array=obj.getJSONArray("movies");
                        String yearfild=obj.getString("date");
                        for(int j=0; j<array.length();j++){
                            JSONObject mov=array.getJSONObject(j);
                            Movie movie = gson.fromJson(mov.toString(), Movie.class);
                            list.add(new MovieModels(movie.getTitle(),
                                    movie.getUrlPoster(),
                                    movie.getIdIMDB(),
                                    yearfild));
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    list.add(new MovieModels("404 Not found", "-1",
                            "Check if the name is correct", "Click on search button to edit\nor try again."));
                }
                m = new MovieAdapter(context, R.layout.content, list);
                listView.setAdapter(m);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(60),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
}
