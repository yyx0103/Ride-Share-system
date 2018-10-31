package ca.mcgill.ecse321.rideshare9.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.hanks.htextview.base.HTextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.ecse321.rideshare9.HttpUtils;

import ca.mcgill.ecse321.rideshare9.R;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("2","onCreate_Fragment");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("3","onCreateView_Fragment");
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rvtrace);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final HTextView hTextView = (HTextView) view.findViewById(R.id.GreetingText);
        final HTextView hereisyour = (HTextView) view.findViewById(R.id.hereisyourcurrent);
        final Button refreshbutton = (Button) view.findViewById(R.id.homerefreshButton);
        refreshbutton.setVisibility(view.GONE);
        final Header[] headers = {new BasicHeader("Authorization","Bearer "+getArguments().getString("token"))};

        final List<Trace> traceList = new ArrayList<>(10);


        refreshbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtils.get(getContext(),"user/get-logged-user",headers,new RequestParams(),new JsonHttpResponseHandler(){
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        hereisyour.animateText("Sorry, Failure to load data.");
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if(response.getString("status").equals("ON_RIDE")){
                                if(getContext()!=null) {
                                    traceList.add(new Trace("2016-05-25 17:48:00", "London"));
                                    traceList.add(new Trace("2016-05-25 14:13:00", "Paris"));
                                    traceList.add(new Trace("2016-05-25 13:01:04", "Moscow"));
                                    traceList.add(new Trace("2016-05-25 12:19:47", "Beijing"));
                                    traceList.add(new Trace("2016-05-25 11:12:44", "Tokyo"));
                                    hereisyour.animateText("Here is your current trip:");
                                    recyclerView.setVisibility(view.VISIBLE);
                                    TraceListAdapter adapter = new TraceListAdapter(getContext(), traceList);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.scheduleLayoutAnimation();
                                }
                            }
                            else{
                                hereisyour.animateText("You do not currently on a trip");
                                recyclerView.setVisibility(view.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        HttpUtils.get(getContext(),"user/get-logged-user",headers,new RequestParams(),new JsonHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                hereisyour.animateText("Sorry, Failure to load data.");
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(response.getString("status").equals("ON_RIDE")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hereisyour.animateText("Here is your current trip:");
                            }
                        },4400);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(getContext()!=null) {
                                    traceList.add(new Trace("2016-05-25 17:48:00", "London"));
                                    traceList.add(new Trace("2016-05-25 14:13:00", "Paris"));
                                    traceList.add(new Trace("2016-05-25 13:01:04", "Moscow"));
                                    traceList.add(new Trace("2016-05-25 12:19:47", "Beijing"));
                                    traceList.add(new Trace("2016-05-25 11:12:44", "Tokyo"));
                                    TraceListAdapter adapter = new TraceListAdapter(getContext(), traceList);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.scheduleLayoutAnimation();
                                    refreshbutton.setVisibility(view.VISIBLE);
                                }
                            }
                        },5000);
                    }
                    else{
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(getContext()!=null) {
                                    hereisyour.animateText("You do not currently on a trip");
                                    refreshbutton.setVisibility(view.VISIBLE);
                                }
                            }
                        },4400);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                log.d("Bearer in user", getArguments().getString("token"));
            }
        },5400);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hTextView.animateText("Hello " + getArguments().getString("username") + "!");
            }
        },500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hTextView.animateText("How are you Today?");
            }
        },2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hTextView.animateText("Need a Ride? ");
            }
        },3200);

        return view;
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d("1","onAttach_Fragment");
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
        Log.d("5","onDetach_Fragment");
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
        void onFragmentInteraction(Uri uri);
    }
}