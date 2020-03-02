package com.stevecao.assignment2;

import android.app.slice.Slice;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stevecao.assignment2.model.NewsAdapter;
import com.stevecao.assignment2.model.NewsHandler;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class StatisticsFragment extends Fragment {
    TextView mohInfoText;
    ImageView mohLoadingIV;
    PieChartView pieChartView;
    private boolean isMohSuccessful;
    private ArrayList<String> mohInfo = new ArrayList<String>(0);
    private Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mohInfoText = getView().findViewById(R.id.mohInfoText);
        mohLoadingIV = getView().findViewById(R.id.mohLoadingIV);
        pieChartView = getView().findViewById(R.id.pieChartView);
        mContext = view.getContext();
    }

    private void getMohInfo() {
        mohInfo.clear();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL mohUrl = new URL("https://pyrostore.nushhwboard.ml/api/covid-19");
            HttpURLConnection connection = (HttpURLConnection) mohUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200)
//                throw new RuntimeException("HttpResponseCode: " + responseCode);
                isMohSuccessful = false;
            else {
                Scanner s = new Scanner(mohUrl.openStream());
                String inline = "";
                while (s.hasNext()) {
                    inline += s.nextLine();
                }
                Log.d("moh", inline);
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonTree = jsonParser.parse(inline);
                JsonObject jsonObject = jsonTree.getAsJsonObject();
                JsonObject caseData = jsonObject.getAsJsonObject("caseData");
                String dorscon = jsonObject.get("dorscon").getAsString();
                String hospitalised_s = caseData.get("Hospitalised (Stable)").getAsString();
                String hospitalised_c = caseData.get("Hospitalised (Critical)").getAsString();
                String death = caseData.get("Death").getAsString();
                String discharged = caseData.get("Discharged").getAsString();
                String cases = "" + (caseData.get("ACTIVE CASES").getAsInt() + caseData.get("Discharged").getAsInt());
                Log.d("cases", cases);
                String lastUpdated = jsonObject.get("lastUpdated").getAsString();

                mohInfo.addAll(Arrays.asList(dorscon, hospitalised_s, hospitalised_c, death, discharged, cases, lastUpdated));
                mohInfo.add("100%");
                isMohSuccessful = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        (new UpdateStats()).execute();
    }

    private final class UpdateStats extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            getMohInfo();
            if (isMohSuccessful) {
                ArrayList<SliceValue> pieData = new ArrayList<>(0);
                double totalCases = Double.parseDouble(mohInfo.get(5));
                pieData.add(new SliceValue(Integer.parseInt(mohInfo.get(1)),
                        mContext.getColor(R.color.graphColor2)).setLabel(mContext.getString(R.string.hospitalised_s)
                        + ": " + String.format("%.1f", Double.parseDouble(mohInfo.get(1)) / totalCases * 100.0)
                        + "%"));
                pieData.add(new SliceValue(Integer.parseInt(mohInfo.get(2)),
                        mContext.getColor(R.color.colorSecondary)).setLabel(mContext.getString(R.string.hospitalised_c)
                        + ": " + String.format("%.1f", Double.parseDouble(mohInfo.get(2)) / totalCases * 100.0)
                        + "%"));
                pieData.add(new SliceValue(Integer.parseInt(mohInfo.get(4)),
                        mContext.getColor(R.color.colorPrimary)).setLabel(mContext.getString(R.string.discharged)
                        + ": " + String.format("%.1f", Double.parseDouble(mohInfo.get(4)) / totalCases * 100.0)
                        + "%"));
//                pieData.add(new SliceValue(Integer.parseInt(mohInfo.get(3)),
//                        mContext.getColor(R.color.graphColor1)).setLabel(mContext.getString(R.string.death)
//                        + ": " + String.format("%.1f", Double.parseDouble(mohInfo.get(3)) / totalCases * 100.0)
//                        + "%"));
                PieChartData pieChartData = new PieChartData(pieData);
                pieChartData.setHasLabels(true);
                pieChartData.setValueLabelsTextColor(Color.BLACK);
                pieChartView.setPieChartData(pieChartData);
            }
            return "Executed";
        }

        @Override
        protected void onPreExecute() {
            Glide.with(getContext())
                    .load(R.drawable.loading2)
                    .into(mohLoadingIV);
            pieChartView.setVisibility(View.GONE);
            mohLoadingIV.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            mohLoadingIV.setVisibility(View.GONE);
            if (isMohSuccessful) {
                pieChartView.setVisibility(View.VISIBLE);
                mohInfoText.setText(mContext.getText(R.string.dorscon) + ": " + mohInfo.get(0)
                        + "\n" + mContext.getText(R.string.hospitalised_s) + ": " + mohInfo.get(1)
                        + "\n" + mContext.getText(R.string.hospitalised_c) + ": " + mohInfo.get(2)
                        + "\n" + mContext.getText(R.string.death) + ": " + mohInfo.get(3)
                        + "\n" + mContext.getText(R.string.discharged) + ": " + mohInfo.get(4)
                        + "\n" + mContext.getText(R.string.cases) + ": " + mohInfo.get(5)
                        + "\n" + mContext.getText(R.string.lastUpdated) + ": " + mohInfo.get(6)
                        + "\n" + mContext.getText(R.string.deathProb) + ": " + mohInfo.get(7));
            }
            else {
                mohInfoText.setText(mContext.getString(R.string.internetFailure));
            }
        }
    }
}
