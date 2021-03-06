package ca.mcgill.ecse321.rideshare9.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.w3c.dom.Text;

import ca.mcgill.ecse321.rideshare9.FullscreenActivity_login;
import ca.mcgill.ecse321.rideshare9.HttpUtils;
import ca.mcgill.ecse321.rideshare9.R;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YouFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YouFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YouFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public YouFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YouFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YouFragment newInstance(String param1, String param2) {
        YouFragment fragment = new YouFragment();
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
        View view = inflater.inflate(R.layout.fragment_you, container, false);
        TextView usernameDisply = (TextView)view.findViewById(R.id.profileUsernameText);
        usernameDisply.setText(getArguments().getString("username",""));
        //add listener to switch account;
        TextView switchaccount = (TextView)view.findViewById(R.id.switchAccText);
        final TextView statstext = (TextView)view.findViewById(R.id.statsText);

        switchaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FullscreenActivity_login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        statstext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Header[] headers = {new BasicHeader("Authorization","Bearer "+getArguments().getString("token"))};
                HttpUtils.get(getContext(),"map/get-map",headers,new RequestParams(),new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        if(statstext.getText().equals("Statistic")) {
                            statstext.setText("You had " + response.length() + " trips!");
                        }
                        else{
                            statstext.setText("Statistic");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        statstext.setText("There is a problem.");
                    }
                });
            }
        });
        return view;
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
