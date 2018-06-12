package com.ureaqa.ureaqamobile;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.Math.pow;


public class InsightsFragment extends Fragment {


    public InsightsFragment() {
        // Required empty public constructor
    }

    String name, last_name;
    int weight = 80;
    int duration = 90;
    double sweatRate = 1.2;
    double sodium = 1.0;
    double BML = 0.02;
    double A = 0.5841;
    double B = 0.003;
    double C = 0.000003;

    int fluidsBefore, fluidsDuring, fluidsAfter;
    int elecBefore, elecDuring, elecAfter;
    int carbsBefore, carbsDuring, carbsAfter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_insights, container, false);

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        SeekBar seekBar = view.findViewById(R.id.seekBar);
        SeekBar seekThreshold = view.findViewById(R.id.seekBar2);
        final TextView tvTitle = view.findViewById(R.id.tvTitle);
        final TextView tvFluids = view.findViewById(R.id.textView5);
        final TextView tvElecs = view.findViewById(R.id.textView8);
        final TextView tvCarbs = view.findViewById(R.id.textView11);
        final TextView tvHydrThreshold = view.findViewById(R.id.tvHydrThreshold);
        final TextView tvBefore = view.findViewById(R.id.textView);
        final TextView tvDuring = view.findViewById(R.id.textView2);
        final TextView tvAfter = view.findViewById(R.id.textView3);
        final EditText etDuration = view.findViewById(R.id.etSweatTime);
        final EditText etSweatRate = view.findViewById(R.id.etSweatRate);

        Bundle bundle = getArguments();
        assert bundle != null;
        weight = bundle.getInt("weight");
        name = bundle.getString("name");
        last_name = bundle.getString("last_name");

        String title = "Insights > " + name + " " +last_name;
        tvTitle.setText(title);

        try {
            initGraph(graph);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        final int[] when = new int[1];
        final int[] dht = new int[1];
        dht[0]=90;
        BML = (100-dht[0])/500;


        nutritionCalc();

        tvFluids.setText(fluidsDuring+" mL");
        tvElecs.setText(elecDuring+" mg");
        tvCarbs.setText(carbsDuring+" g");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser){
                    when[0] = progress;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                Double checkSweat = Double.valueOf(etSweatRate.getText().toString());
                int checkDuration = Integer.parseInt(etDuration.getText().toString());



                if (checkSweat > 0.2 && checkSweat < 2.5){
                    sweatRate = checkSweat;
                }
                else {
                    Toast.makeText(getContext(), checkSweat+" sweat rate seems odd...", Toast.LENGTH_SHORT).show();
                }
                if (checkDuration > 29) {
                    if (checkDuration < 720) {
                        duration = checkDuration;

                    } else {
                        duration = 720;
                        Toast.makeText(getContext(), "Prepping for an iron man? Consider working with a trainer or dietitian to fine-tune this plan.", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                duration = 30;
                Toast.makeText(getContext(), "Do you REALLY need a nutrition plan for this short a race?", Toast.LENGTH_LONG).show();
                }

                nutritionCalc();

                if (when[0] < 33) {
                    //Toast.makeText(getContext(), "Before!", Toast.LENGTH_SHORT/2).show();
                    tvFluids.setText(fluidsBefore+" mL");
                    tvElecs.setText(elecBefore+" mg");
                    tvCarbs.setText(carbsBefore+" g");
                    seekBar.setProgress(10);
                    tvBefore.setTypeface(null, Typeface.BOLD);
                    tvDuring.setTypeface(null, Typeface.NORMAL);
                    tvAfter.setTypeface(null, Typeface.NORMAL);

                }
                else if (when[0] > 67){
                    //Toast.makeText(getContext(), "After -_-", Toast.LENGTH_SHORT/2).show();
                    tvFluids.setText(fluidsAfter+" mL");
                    tvElecs.setText(elecAfter+" mg");
                    tvCarbs.setText(carbsAfter+" g");
                    seekBar.setProgress(90);
                    tvBefore.setTypeface(null, Typeface.NORMAL);
                    tvDuring.setTypeface(null, Typeface.NORMAL);
                    tvAfter.setTypeface(null, Typeface.BOLD);
                }
                else {
                    //Toast.makeText(getContext(), "During...", Toast.LENGTH_SHORT/2).show();
                    tvFluids.setText(fluidsDuring+" mL");
                    tvElecs.setText(elecDuring+" mg");
                    tvCarbs.setText(carbsDuring+" g");
                    seekBar.setProgress(50);
                    tvBefore.setTypeface(null, Typeface.NORMAL);
                    tvDuring.setTypeface(null, Typeface.BOLD);
                    tvAfter.setTypeface(null, Typeface.NORMAL);
                }

            }
        });



        seekThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                dht[0] = progress;
                dht[0] = (int)(dht[0]*0.3) + 65;


                //set min hydration level of 100-7*5 = 65%
                //set max hydration level of 100-1*5 = 95%
                //recommended 2% BML = 90% threshold
                }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getContext(), dht[0]+" BOII", Toast.LENGTH_SHORT).show();
                BML = (100.0-dht[0])/500.0;
                String strDHT = String.valueOf(dht[0])+" %";
                tvHydrThreshold.setText(strDHT);
                Toast.makeText(getContext(), BML+" BML",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


    public void nutritionCalc(){

        fluidsBefore = (int) 7.5*weight;
        fluidsDuring = (int)(1000*(duration*sweatRate/60 - weight*BML));
        fluidsAfter = (int)(1000*1.25*BML*weight);

        if (fluidsDuring < 0){
            fluidsDuring=0;
            int dH = (int)(100-BML*500);
            int thresh = (int) (100-500*(duration*sweatRate/(60*weight)));//solve for when fluid need is 0
            Toast.makeText(getContext(), "You won't dehydrate to "+dH+"% during this race.", Toast.LENGTH_LONG).show();
            if (dH < 90){
                Toast.makeText(getContext(), "Increase your threshold to "+thresh+"% for better results.", Toast.LENGTH_SHORT).show();
            }
        }


        elecBefore = (int) sodium*fluidsBefore/2;
        elecDuring = (int) sodium*fluidsDuring;
        elecAfter = (int) sodium*fluidsAfter;

        carbsBefore = (duration/60)*weight;
        carbsDuring = (int)(A*duration+B*pow(duration,2)-C*pow(duration,3));
        carbsAfter = (int) (elecAfter*0.4);




    }

    public void initGraph(GraphView graph) throws ParseException {

        Date d1, d2, d3, d4, d5, d6, d7;
        String s1, s2, s3, s4, s5, s6, s7;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        s1= "2018-04-13 18:47:24";
        d1 = sdf.parse(s1);
        s2= "2018-04-14 11:47:24";
        d2 = sdf.parse(s2);
        s3= "2018-04-18 19:47:24";
        d3 = sdf.parse(s3);
        s4= "2018-04-19 06:47:24";
        d4 = sdf.parse(s4);
        s5= "2018-04-20 06:47:24";
        d5 = sdf.parse(s5);
        s6= "2018-04-21 12:47:24";
        d6 = sdf.parse(s6);
        s7= "2018-04-24 22:47:24";
        d7 = sdf.parse(s7);

        //first series
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(d1, 86),
                new DataPoint(d2, 94),
                new DataPoint(d3, 89),
                new DataPoint(d4, 78),
                new DataPoint(d5, 89),
                new DataPoint(d6, 94),
                new DataPoint(d7, 73)
        });
        series.setTitle("hydration");
        graph.addSeries(series);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        series.setDrawDataPoints(true);
        series.setAnimated(true);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.argb(100, 60, 109, 179));



        // second series
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {

                new DataPoint(d2, 9.5),
                new DataPoint(d3, 9.2),
                new DataPoint(d6, 8.9),
                new DataPoint(d7, 9.0)
        });

        graph.getSecondScale().addSeries(series2);
        graph.getSecondScale().setMinY(0);
        graph.getSecondScale().setMaxY(12);
        series2.setTitle("speed");
        series2.setColor(Color.argb(255, 255, 60, 60));
        series2.setDrawDataPoints(true);
        series2.setAnimated(true);

        // third series
        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>(new DataPoint[] {

                new DataPoint(d2, 5.5),
                new DataPoint(d3, 6.9),
                new DataPoint(d6, 6.5),
                new DataPoint(d7, 7.6)
        });

        graph.getSecondScale().addSeries(series3);
        series3.setTitle("distance");
        series3.setColor(Color.argb(255, 90, 240, 90));
        series3.setDrawDataPoints(true);
        series3.setAnimated(true);


        // legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        graph.getLegendRenderer().setBackgroundColor(Color.argb(200, 255, 255, 255));

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 3 because of the space

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d7.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

}