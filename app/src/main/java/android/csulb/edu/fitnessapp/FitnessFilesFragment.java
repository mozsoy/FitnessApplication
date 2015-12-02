package android.csulb.edu.fitnessapp;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.csulb.edu.fitnessapp.dummy.DummyContent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link android.csulb.edu.fitnessapp.FitnessFilesFragment.OnFitnessFileListener}
 * interface.
 */
public class FitnessFilesFragment extends ListFragment {
    private ArrayList<String> itemList = new ArrayList<String>();
    TrackDBHelper mDBHelper;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFitnessFileListener mListener;
    TreeMap<String, Integer> tracks;

    // TODO: Rename and change types of parameters
    public static FitnessFilesFragment newInstance(String param1, String param2) {
        FitnessFilesFragment fragment = new FitnessFilesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FitnessFilesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mDBHelper = new TrackDBHelper(getActivity());
        tracks = mDBHelper.getAllTracks();
        // Add track from database to list
        itemList.addAll(mDBHelper.getAllTracks().keySet());

        // TODO: Change Adapter to display your content
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, itemList));
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // Update Item List
        itemList.clear();
        mDBHelper = new TrackDBHelper(getActivity());
        tracks = mDBHelper.getAllTracks();
        itemList.addAll(mDBHelper.getAllTracks().keySet());

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) {
                // On long click of an item in list, delete the item from list and database
                TextView txt = (TextView) v;
                String choice = txt.getText().toString();
                Cursor resultSet = mDBHelper.getData(tracks.get(choice));
                resultSet.moveToFirst();
                mDBHelper.deleteTrack(resultSet.getString(0));
                itemList.remove(position);
                setListAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, itemList));
                Toast.makeText(getActivity(), "Track Deleted", Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFitnessFileListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // Construct pointList based on item clicked
        ArrayList<LatLng> pointList = new ArrayList<>();
        TextView txt = (TextView) v;
        String choice = txt.getText().toString();

        Cursor resultSet = mDBHelper.getData(tracks.get(choice));
        resultSet.moveToFirst();
        String coords = resultSet.getString(resultSet.getColumnIndex(TrackDBHelper.COORDINATES));
        String time = resultSet.getString(resultSet.getColumnIndex(TrackDBHelper.TIME));
        String calories = resultSet.getString(resultSet.getColumnIndex(TrackDBHelper.CALORIES));
        String transportation = resultSet.getString(resultSet.getColumnIndex(TrackDBHelper.TRANSPORTATION));
        String distance = resultSet.getString(resultSet.getColumnIndex(TrackDBHelper.DISTANCE));

        String[] latlong = coords.split(",");
        for (int i = 0; i < latlong.length; i++) {
            System.out.println(latlong[i]);
        }

        try
        {
            for (int i = 0; i < latlong.length; i += 2)
            {
                pointList.add(new LatLng(Double.parseDouble(latlong[i]), Double.parseDouble(latlong[i + 1])));
            }
        }
        catch(NumberFormatException ex)
        {
            System.out.println("NumberFormatException occurred. latlong probably contained an empty string");
            pointList = null;
        }

        // When ListItem clicked, go back to MapActivity
        Intent intent = new Intent(getActivity(), MapActivity.class);
        // Pass ArrayList<LatLng>
        intent.putExtra("selectedTrack", pointList);
        intent.putExtra("time", time);
        intent.putExtra("calories", calories);
        intent.putExtra("transportation", transportation);
        intent.putExtra("distance", distance);
        startActivity(intent);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFitnessFileListener {
        public void onFitnessFileInteraction(String text);
    }

}
