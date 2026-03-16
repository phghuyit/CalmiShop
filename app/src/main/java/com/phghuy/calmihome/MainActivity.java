package com.phghuy.calmihome;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 pager2 = findViewById(R.id.viewPager2);

        TablayoutAdapter adapter = new TablayoutAdapter(this);
        pager2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, pager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int i) {
                switch (i){
                    case 0:
                        tab.setIcon(R.drawable.ic_home_24);
                        tab.setText("Home");
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_history_24);
                        tab.setText("History");
                        break;
                    case 2:
                        tab.setIcon(R.drawable.ic_account_24);
                        tab.setText("Account");
                        break;
                }
            }
        }).attach();
    }
}
