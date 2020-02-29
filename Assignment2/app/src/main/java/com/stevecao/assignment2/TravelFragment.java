package com.stevecao.assignment2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.TableDataAdapter;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class TravelFragment extends Fragment {
    FloatingActionButton fab;
    TextView travelNoUser;
    SortableTableView<String[]> travelTable;
    CardView travelCard;
    FirebaseFirestore db;
    ArrayList<ArrayList<String>> travels = new ArrayList<>(0);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_travel, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fab = getView().findViewById(R.id.addTravel);
        travelTable = getView().findViewById(R.id.travelTable);
        travelCard = getView().findViewById(R.id.travelCard);
        travelNoUser = getView().findViewById(R.id.travelNoUser);

        String[] test2 = new String[]{"Date", "Days", "Country", "City"};
        TableColumnWeightModel model = new TableColumnWeightModel(4);
        model.setColumnWeight(0, 5);
        model.setColumnWeight(2, 4);
        model.setColumnWeight(3, 4);
        model.setColumnWeight(1, 3);
        travelTable.setColumnModel(model);
        SimpleTableHeaderAdapter stha = new SimpleTableHeaderAdapter(getActivity(), test2);
        stha.setTextSize(16);
        travelTable.setHeaderAdapter(stha);
        travelTable.setSwipeToRefreshEnabled(true);
        travelTable.setSwipeToRefreshListener((s) -> {
            updateTable();
            s.hide();
        });

        fab.setOnClickListener((s) -> {
            Intent intent = new Intent(getActivity(), AddTravelActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            travelCard.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            travelNoUser.setVisibility(View.VISIBLE);
        } else {
            travelCard.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            travelNoUser.setVisibility(View.GONE);
            updateTable();
        }
    }

    private void updateTable() {

        travels = new ArrayList<>(0);
        db = FirebaseFirestore.getInstance();
        db.collection("travel")
                .orderBy("date", Query.Direction.DESCENDING)
                .whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        Log.d("travel", task.getResult().size() + "");
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("travel", FirebaseAuth.getInstance().getCurrentUser().getEmail() + "");


                            Date date = ((Timestamp) document.get("date")).toDate();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

                            travels.add(new ArrayList<String>(Arrays.asList(
                                    formatter.format(calendar.getTime()),
                                    document.get("duration").toString(),
                                    document.get("country").toString(),
                                    document.get("city").toString())));

                        }


                        String[][] forTable = new String[travels.size()][];
                        for (int i = 0; i < travels.size(); i++) {
                            ArrayList<String> row = travels.get(i);
                            forTable[i] = row.toArray(new String[row.size()]);

                        }
                        SimpleTableDataAdapter stda = new SimpleTableDataAdapter(getActivity(), forTable);
                        stda.setTextColor(travelNoUser.getCurrentTextColor());
                        travelTable.setDataAdapter(stda);
                        for (int x = 0; x < travelTable.getColumnCount(); x++) {
                            travelTable.setColumnComparator(x, new StringComparator(x));
                        }
                    }
                });
    }


    private static class StringComparator implements Comparator<String[]> {
        int column = 0;

        public StringComparator(int column) {
            super();
            this.column = column;
        }

        @Override
        public int compare(String[] s1, String[] s2) {
            Log.d("compare", s1.length + "");
            switch (column) {
                case 0:
                    return s1[0].compareTo(s2[0]);
                case 1:
                    Integer ints1 = Integer.parseInt(s1[1]);
                    Integer ints2 = Integer.parseInt(s2[1]);
                    return ints1.compareTo(ints2);
                case 2:
                    return s1[2].compareTo(s2[2]);
                case 3:
                    return s1[3].compareTo(s2[3]);
                default:
                    return 0;
            }
        }
    }
}
