package com.example.assignment1;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.w3c.dom.Text;

public class CardInitializer extends AppCompatActivity {
    CardView card;
    LinearLayout ll;
    ImageView iv;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("idk", "asdf");
        setContentView(R.layout.cardview);
        card = findViewById(R.id.card);
        ll = (LinearLayout) card.getChildAt(0);
        iv = (ImageView)ll.getChildAt(0);
        tv = (TextView)ll.getChildAt(1);
    }
    public CardView generateCard(Drawable img, String txt) {


        iv.setImageDrawable(img);
        tv.setText(txt);
        return card;
    }
}
