package android.csulb.edu.fitnessapp;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.csulb.edu.fitnessapp.dummy.DummyContent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link android.csulb.edu.fitnessapp.FitnessFilesFragment.OnFitnessFileListener}
 * interface.
 */
public class FitnessFilesFragment extends ListFragment
{
    static final int READ_BLOCK_SIZE = 100;
    private static final LatLng ATHERTON_BELLFLOWER = new LatLng(33.788542, -118.124377);
    private static final LatLng ATHERTON_STUDEBAKER = new LatLng(33.788666, -118.099318);
    private static final LatLng STUDEBAKER_7TH = new LatLng(33.774382, -118.103150);
    private static final LatLng BELLFLOWER_7TH = new LatLng(33.775328, -118.121233);

    private static final LatLng LIBRARY = new LatLng(33.777120, -118.114467);
    private static final LatLng PARKING_STRUCTURE_2 = new LatLng(33.786153, -118.109437);

    private static final LatLng PARKING_STRUCTURE_3 = new LatLng(33.787323, -118.109347);
    private static final LatLng PYRAMID = new LatLng(33.787621, -118.114198);

    private ArrayList<String> itemList = new ArrayList<String>();



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFitnessFileListener mListener;

    // TODO: Rename and change types of parameters
    public static FitnessFilesFragment newInstance(String param1, String param2)
    {
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
    public FitnessFilesFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Add track names to list
        itemList.add("5/6/2015");
        itemList.add("7/12/2015");
        itemList.add("11/1/2015");
        itemList.add("Read track from File");

        // TODO: Change Adapter to display your content
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, itemList));
    }


    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFitnessFileListener) activity;
        }
        catch (ClassCastException e)
        {
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
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFitnessFileInteraction(DummyContent.ITEMS.get(position).id);
        }

        // Construct pointList based on item clicked
        ArrayList<LatLng> pointList = new ArrayList<>();
        TextView txt = (TextView) v;
        String choice = txt.getText().toString();
        if(choice.equals("5/6/2015")) {
            pointList.add(ATHERTON_BELLFLOWER);
            pointList.add(ATHERTON_STUDEBAKER);
            pointList.add(STUDEBAKER_7TH);
            pointList.add(BELLFLOWER_7TH);
            pointList.add(ATHERTON_BELLFLOWER);
        } else if(choice.equals("7/12/2015")) {
            pointList.add(LIBRARY);
            pointList.add(PARKING_STRUCTURE_2);
        } else if(choice.equals("11/1/2015")) {
            pointList.add(PARKING_STRUCTURE_3);
            pointList.add(PYRAMID);
        } else if(choice.equals("Read track from File")){
            // read coordinates from file
            try {
                RandomAccessFile file = new RandomAccessFile("/data/user/0/android.csulb.edu.fitnessapp/files/track.txt","r");
                // read size of coordinates array
                int size = file.readInt();
                LatLng[] coordinates = new LatLng[size];
                // read coordinates one by one
                for(int i = 0; i < size; i++) {
                    double lat = file.readDouble();
                    double lng = file.readDouble();
                    coordinates[i] = new LatLng(lat, lng);
                    System.out.println("Lat = " + lat + " lng = " + lng);
                }
                // Convert coordinates to pointList so can send to MapActivity
                pointList = new ArrayList<LatLng>(Arrays.asList(coordinates));
                file.close();

                System.out.println("The size of the coordinates array is " + size);
                Toast.makeText(getActivity(), "File loaded successfully!",
                        Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // When ListItem clicked, go back to MapActivity
        Intent intent = new Intent(getActivity(), MapActivity.class);
        // Pass ArrayList<LatLng>
        intent.putExtra("selectedTrack", pointList);
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

    public interface OnFitnessFileListener
    {
        public void onFitnessFileInteraction(String text);
    }

}
