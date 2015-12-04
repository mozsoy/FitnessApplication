package android.csulb.edu.fitnessapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;
import java.util.TreeMap;

public class FitnessChartFragment extends Fragment {
    private static final String TIME = "Time";
    private static final String DISTANCE = "Distance";
    private static final String CALORIES = "Calories";
    private GraphView graphCalories;
    private GraphView graphDistance;

    private String mTime;
    private String mDistance;
    private String mCalories;
    TreeMap<String, Integer> tracks;
    LineGraphSeries<DataPoint> distances;
    LineGraphSeries<DataPoint> calories;

    private OnFitnessChartListener mListener;

    static TrackDBHelper mDBHelper;
    static Context mContext;

    static GraphView graph;
    static GraphView graph2;

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mContext = getActivity();
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
        int i = 1;
        while (res.isAfterLast() == false) {
            calories.appendData(new DataPoint(i, res.getInt(res.getColumnIndex("calories"))), true, count);
            distances.appendData(new DataPoint(i, res.getInt(res.getColumnIndex("distance"))), true, count);
            i++;
            res.moveToNext();
        }

        View rootView = inflater.inflate(R.layout.fragment_fitness_chart, container, false);

        graph = (GraphView) rootView.findViewById(R.id.graph);
        graph2 = (GraphView) rootView.findViewById(R.id.graph2);

        distances.setDrawDataPoints(true);
        distances.setDataPointsRadius(20);
        distances.setThickness(10);
        graph.addSeries(distances);

        calories.setDrawDataPoints(true);
        calories.setDataPointsRadius(20);
        calories.setThickness(10);
        graph2.addSeries(calories);

        // legend
        distances.setTitle("Distance");
        calories.setTitle("Calories");

        graph.getGridLabelRenderer().setVerticalAxisTitle("Distance (m)");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Track #");
        graph.getLegendRenderer().setVisible(true);

        graph2.getGridLabelRenderer().setVerticalAxisTitle("Calories (cal)");
        graph2.getGridLabelRenderer().setHorizontalAxisTitle("Track #");
        graph2.getLegendRenderer().setVisible(true);

        return rootView;
    }

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

    public interface OnFitnessChartListener {
        public void onFitnessChartInteraction(String text);
    }

    public static void updateCharts() {
        LineGraphSeries<DataPoint> dist = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> cal = new LineGraphSeries<DataPoint>();
        TreeMap<String, Integer> allTracks = new TreeMap<>();
        mDBHelper = new TrackDBHelper(mContext);
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        Cursor res = db.rawQuery("select * from tracks", null);
        int count = res.getCount();
        res.moveToFirst();
        int i = 1;
        while (res.isAfterLast() == false) {
            cal.appendData(new DataPoint(i, res.getInt(res.getColumnIndex("calories"))), true, count);
            dist.appendData(new DataPoint(i, res.getInt(res.getColumnIndex("distance"))), true, count);
            i++;
            res.moveToNext();
        }

        graph.removeAllSeries();
        graph2.removeAllSeries();
        dist.setDrawDataPoints(true);
        dist.setDataPointsRadius(20);
        dist.setThickness(10);
        graph.addSeries(dist);

        cal.setDrawDataPoints(true);
        cal.setDataPointsRadius(20);
        cal.setThickness(10);
        graph2.addSeries(cal);

        // legend
        dist.setTitle("Distance");
        cal.setTitle("Calories");

        graph.getGridLabelRenderer().setVerticalAxisTitle("Distance (m)");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Track #");
        graph.getLegendRenderer().setVisible(true);

        graph2.getGridLabelRenderer().setVerticalAxisTitle("Calories (cal)");
        graph2.getGridLabelRenderer().setHorizontalAxisTitle("Track #");
        graph2.getLegendRenderer().setVisible(true);
    }
}
