package edu.stlawu.cs450fall18_hw3_gpsdistance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button checkPoint_btn;
    private ScrollView scrollView;
    private LinearLayout SV_vertical_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.checkPoint_btn = findViewById(R.id.checkpoint_btn);
        this.scrollView = findViewById(R.id.myScrollView);
        this.SV_vertical_layout = findViewById(R.id.SV_vertical_layout);

        checkPoint_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLayout();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void createLayout(){
        LinearLayout aLayout = new LinearLayout(MainActivity.this);
        aLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView tv_lon = new TextView(MainActivity.this);
        TextView tv_lat = new TextView(MainActivity.this);
        TextView tv_dist = new TextView(MainActivity.this);

        tv_dist.setPadding(20,20, 20, 20);
        tv_lon.setPadding(20,20, 20, 20);
        tv_lat.setPadding(20,20, 20, 20);

        tv_dist.setText("TEXTEXETs");
        tv_lat.setText("TEXTEXET");
        tv_lon.setText("TEXTEXET");


        aLayout.addView(tv_lat);
        aLayout.addView(tv_lon);
        aLayout.addView(tv_dist);

        SV_vertical_layout.addView(aLayout);
    }
}
