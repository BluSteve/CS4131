package com.example.nushscs4203.activitytoactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    int currentOrder;
    double price=4.50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.priceTextView)).setText("" + NumberFormat.getCurrencyInstance().format(0));

        /**different event handling approaches
         * refer to Button
         * onClick attribute
         */


        Button b = (Button)findViewById(R.id.orderButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView qtyTextView = (TextView)findViewById(R.id.quantityTextView);
                qtyTextView.setText("" + currentOrder);

                TextView priceTextView = (TextView)findViewById(R.id.priceTextView);
                priceTextView.setText("" + NumberFormat.getCurrencyInstance().format(price * currentOrder));

                Toast toast = Toast.makeText(getApplicationContext(), "moving to another activity", Toast.LENGTH_LONG);
                toast.show();

                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                intent.putExtra("order", "" + currentOrder);
                intent.putExtra("total", "" + price*currentOrder);
                startActivity(intent);

            }
        });


    }

    public void changeOrder(View view){
        switch (view.getId()){
            case R.id.addButton:currentOrder++;
                break;
            case R.id.subButton:if(currentOrder>0)currentOrder--;
                break;
        }

        TextView qtyTextView = (TextView)findViewById(R.id.quantityTextView);
        qtyTextView.setText("" + currentOrder);

        TextView priceTextView = (TextView)findViewById(R.id.priceTextView);
        priceTextView.setText("" + NumberFormat.getCurrencyInstance().format(price * currentOrder));
    }

    public void submitOrder(View view){
        Toast toast = Toast.makeText(getApplicationContext(), "moving to another activity", Toast.LENGTH_LONG);
        toast.show();

        Intent intent = new Intent(this,Main2Activity.class);
        intent.putExtra("order", "" + currentOrder);
        intent.putExtra("total", "" + price*currentOrder);
        startActivity(intent);

    }


}
