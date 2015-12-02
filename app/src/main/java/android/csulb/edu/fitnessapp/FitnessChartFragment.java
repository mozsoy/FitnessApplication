package android.csulb.edu.fitnessapp;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link android.csulb.edu.fitnessapp.FitnessChartFragment.OnFitnessChartListener} interface
 * to handle interaction events.
 * Use the {@link FitnessChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FitnessChartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TIME = "Time";
    private static final String DISTANCE = "Distance";
    private static final String CALORIES = "Calories";
    private GraphView graphCalories;
    private GraphView graphDistance;

    // TODO: Rename and change types of parameters
    private String mTime;
    private String mDistance;
    private String mCalories;
    TreeMap<String, Integer> tracks;
    LineGraphSeries<DataPoint> distances;
    LineGraphSeries<DataPoint> calories;

    private OnFitnessChartListener mListener;

    TrackDBHelper mDBHelper;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param param3 Parameter 3.
     * @return A new instance of fragment FitnessChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FitnessChartFragment newInstance(String param1, String param2, String param3) {
        FitnessChartFragment fragment = new FitnessChartFragment();
        Bundle args = new Bundle();
        args.putString(TIME, param1);
        args.putString(DISTANCE, param2);
        args.putString(CALORIES, param3);
        fragment.setArguments(args);
        return fragment;
    }

    public FitnessChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTime = getArguments().getString(TIME);
            mDistance = getArguments().getString(DISTANCE);
            mCalories = getArguments().getString(CALORIES);

            mDBHelper = new TrackDBHelper(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // init example series data
        distances = new LineGraphSeries<DataPoint>();
        calories = new LineGraphSeries<DataPoint>();
        // read from data from database
        TreeMap<String, Integer> allTracks = new TreeMap<>();
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from tracks", null);
        int count = res.getCount();
        res.moveToFirst();
        int i = 0;
        while (res.isAfterLast() == false) {
            calories.appendData(new DataPoint(i, res.getInt(res.getColumnIndex("calories"))), true, count);
            distances.appendData(new DataPoint(i, res.getInt(res.getColumnIndex("distance"))), true, count);
            i++;
            res.moveToNext();
        }

        View rootView = inflater.inflate(R.layout.fragment_fitness_chart, container, false);

        GraphView graph = (GraphView) rootView.findViewById(R.id.graph);
        GraphView graph2 = (GraphView) rootView.findViewById(R.id.graph2);

        graph.addSeries(distances);
        graph2.addSeries(calories);

        // legend
        distances.setTitle("Distance");
        calories.setTitle("Calories");
        graph.getLegendRenderer().setVisible(true);
        //graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        graph2.getLegendRenderer().setVisible(true);
        //graph2.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String text) {
        if (mListener != null) {
            mListener.onFitnessChartInteraction(text);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFitnessChartListener) activity;
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
    public interface OnFitnessChartListener {
        public void onFitnessChartInteraction(String text);
    }

}
