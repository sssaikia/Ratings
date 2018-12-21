package com.sstudio.ratings.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sstudio.ratings.Models.MovieModels;
import com.sstudio.ratings.MovieAdapter;
import com.sstudio.ratings.R;
import com.sstudio.ratings.ShowInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Search.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "SearchFragment: ";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    Context context;
    String find;
    RequestQueue queue;
    private List<MovieModels> list = new ArrayList<>();
    private MovieAdapter m;
    ListView listView;
    Button loadmore;
    private int currentPage;
    private int currentPosition=0;
    private int pageCount=10;
    private ProgressBar progressBar;

    public Search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Search.
     */
    // TODO: Rename and change types and number of parameters
    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        queue = Volley.newRequestQueue(context);
        if (getArguments() != null) {
            find = getArguments().getString("find", "null");
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        progressBar = view. findViewById(R.id.search_progress);
        progressBar.setIndeterminate(true);
        listView = view.findViewById(R.id.search_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new ShowInfo(context,queue).showinfo(list.get(i).getImdbID());
            }
        });
        fillLilst("1");
        loadmore = new Button(context);
        loadmore.setText("Load more");
        listView.addFooterView(loadmore);
        currentPage=1;
        loadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentPage <= pageCount) {
                    fillLilst(currentPage + "");
                    currentPage++;
                    currentPosition = listView.getFirstVisiblePosition();
                }

            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSearchFragmentInteraction(uri);
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
        void onSearchFragmentInteraction(Uri uri);
    }

    public void fillLilst(String pageno) {
        progressBar.setVisibility(View.VISIBLE);
        //listView.addFooterView(loadmore);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "http://omdbapi.com/?apikey=b4470872&s=" + find + "&type=movie&page=" + pageno,
                null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressBar.setVisibility(View.GONE);
                    loadIntoListView(response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void loadIntoListView(String json) throws JSONException {

        try {
            //Log.d(TAG,json);
            JSONObject mainSrc = new JSONObject(json);
            int totalItems = mainSrc.getInt("totalResults");
            pageCount = (int) Math.ceil(totalItems / 10);
            JSONArray jsonArray = mainSrc.getJSONArray("Search");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                list.add(new MovieModels(obj.getString("Title"),
                        obj.getString("Poster"),
                        obj.getString("imdbID"),
                        obj.getString("Year")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            list.add(new MovieModels("404 Not found", "-1",
                    "Check if the name is correct", "Click on search button to edit\nor try again."));
        }
        m = new MovieAdapter(context, R.layout.content, list);
        listView.setAdapter(m);
        listView.setSelection(currentPosition);
    }

}
