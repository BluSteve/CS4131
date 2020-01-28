package com.example.billsplitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Duration;

public class MainActivity extends AppCompatActivity {
    EditText amountTextField, noOfPplTextField, taxTextField;
    Button minusBtn, plusBtn, resetBtn, submitBtn;
    Switch gstSwitch, scSwitch;
    TextView taxTextView, totalTextView, perPersonTextView;
    TableLayout resultTable;
    Context context;
    int duration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        amountTextField = findViewById(R.id.amountTextField);
        noOfPplTextField = findViewById(R.id.noOfPplTextField);
        taxTextField = findViewById(R.id.taxTextField);
        minusBtn = findViewById(R.id.minusBtn);
        plusBtn = findViewById(R.id.plusBtn);
        resetBtn = findViewById(R.id.resetBtn);
        submitBtn = findViewById(R.id.submitBtn);
        gstSwitch = findViewById(R.id.gstSwitch);
        scSwitch = findViewById(R.id.scSwitch);
        taxTextView = findViewById(R.id.taxTextView);
        totalTextView = findViewById(R.id.totalTextView);
        perPersonTextView = findViewById(R.id.perPersonTextView);
        resultTable = findViewById(R.id.resultTable);
        context = getApplicationContext();
        duration = Toast.LENGTH_SHORT;

        plusBtn.setOnClickListener((s) -> {
            noOfPplTextField.setText((Integer.parseInt(noOfPplTextField.getText().toString())+1)+"");
        });
        minusBtn.setOnClickListener((s) -> {
            if (Integer.parseInt(noOfPplTextField.getText().toString())>1)
            noOfPplTextField.setText((Integer.parseInt(noOfPplTextField.getText().toString())-1)+"");
        });
        submitBtn.setOnClickListener((s) -> {
            submit();
        });
        resetBtn.setOnClickListener((s) -> {
            reset();
        });
    }

    private void toast(String s) {
        Toast.makeText(context,s, duration).show();
    }

    private void submit() {
        if (taxTextField.getText().toString().equals(""))
            toast("GST field is empty");
        else if (amountTextField.getText().toString().equals("") || Double.parseDouble(amountTextField
                .getText().toString()) <= 0)
            toast("Please enter valid amount");

        else {
            Double a = Double.parseDouble(amountTextField.getText().toString());
            Double t = Double.parseDouble(taxTextField.getText().toString());

            double extra = 0;
            if (gstSwitch.isChecked())
                extra += t / 100.0;
            if (scSwitch.isChecked())
                extra += .1;

            double tax = Math.round(a * extra * 100.0) / 100.0;
            double total = Math.round(a * (extra + 1) * 100.0) / 100.0;
            double perPerson = Math.round(
                    total / (Double.parseDouble(noOfPplTextField.getText().toString())) * 100.0) / 100.0;

            resultTable.setVisibility(View.VISIBLE);
            taxTextView.setText(String.format("$%.2f",tax));
            totalTextView.setText(String.format("$%.2f",total));
            perPersonTextView.setText(String.format("$%.2f",perPerson));
        }
    }

    private void reset() {
        resultTable.setVisibility(View.INVISIBLE);
        amountTextField.setText("");
        noOfPplTextField.setText("1");
        taxTextField.setText("");
        gstSwitch.setChecked(false);
        scSwitch.setChecked(false);
    }
}
