package com.stevecao.assignment2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.stevecao.assignment2.model.CustomNewsAdapter;
import com.stevecao.assignment2.model.NewsHandler;

public class NewsFragment extends Fragment {
    static LocalNewsFragment localNewsFragment, globalNewsFragment;
    ViewPager newsViewPager;
    TabLayout newsTabLayout;
    TabItem localNewsTab, globalNewsTab, statisticsTab, customNewsTab;
    SharedPreferences prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        prefs = getView().getContext().getSharedPreferences("com.stevecao.assignment2", Context.MODE_PRIVATE);
        newsViewPager = getView().findViewById(R.id.newsViewPager);
        newsTabLayout = getView().findViewById(R.id.newsTabLayout);
        localNewsTab = getView().findViewById(R.id.localNewsTab);
        globalNewsTab = getView().findViewById(R.id.globalNewsTab);
        statisticsTab = getView().findViewById(R.id.statisticsTab);
        customNewsTab = getView().findViewById(R.id.customNewsTab);


        NewsPagerAdapter newsPagerAdapter = new NewsPagerAdapter(getContext(), getChildFragmentManager());
        newsViewPager.setAdapter(newsPagerAdapter);
        newsTabLayout.setupWithViewPager(newsViewPager);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public class NewsPagerAdapter extends FragmentPagerAdapter {
        Context context;

        public NewsPagerAdapter(Context context, @NonNull FragmentManager fm) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int i) {
            String newslang = prefs.getString("com.stevecao.assignment2.newslang", "en");
            Log.d("news", i + "");
            if (i == 2) {
                LocalNewsFragment localNewsFragment = new LocalNewsFragment(false);
                NewsFragment.localNewsFragment = localNewsFragment;
                return localNewsFragment;
            } else if (i == 3) {
                LocalNewsFragment globalNewsFragment = new LocalNewsFragment(true);
                NewsFragment.globalNewsFragment = globalNewsFragment;
                return globalNewsFragment;
            } else if (i == 0) {
                StatisticsFragment statisticsFragment = new StatisticsFragment();
                return statisticsFragment;
            } else {
                CustomNewsFragment customNewsFragment = new CustomNewsFragment();
                return customNewsFragment;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            switch (i) {
                case 2:
                    return context.getString(R.string.localNewsTab);
                case 3:
                    return context.getString(R.string.globalNewsTab);
                case 0:
                    return context.getString(R.string.statisticsTab);
                case 1:
                    return context.getString(R.string.customNewsTab);
            }
            return null;
        }
    }
}
