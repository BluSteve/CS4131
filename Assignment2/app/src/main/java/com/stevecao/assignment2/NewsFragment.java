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
import com.stevecao.assignment2.model.NewsHandler;

public class NewsFragment extends Fragment {
    ViewPager newsViewPager;
    TabLayout newsTabLayout;
    TabItem localNewsTab;
    TabItem globalNewsTab;
    SharedPreferences prefs;
    static LocalNewsFragment localNewsFragment, globalNewsFragment;
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
        globalNewsTab= getView().findViewById(R.id.globalNewsTab);


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
        public Fragment getItem(int i){
            String newslang = prefs.getString("com.stevecao.assignment2.newslang", "en");
            if (i==0) {
                LocalNewsFragment localNewsFragment = new LocalNewsFragment(false);
                NewsFragment.localNewsFragment = localNewsFragment;
                return localNewsFragment;
            }
            else {
                LocalNewsFragment globalNewsFragment = new LocalNewsFragment(true);
                NewsFragment.globalNewsFragment = globalNewsFragment;
                return globalNewsFragment;
            }
        }
        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int i) {
            switch (i) {
                case 0: return context.getString(R.string.localNewsTab);
                case 1: return context.getString(R.string.globalNewsTab);
            }
            return null;
        }
    }
}
