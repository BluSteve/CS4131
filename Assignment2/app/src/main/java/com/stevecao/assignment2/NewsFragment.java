package com.stevecao.assignment2;
import android.content.Context;
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
    static LocalNewsFragment localNewsFragment, globalNewsFragment;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);
        return root;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        newsViewPager = getView().findViewById(R.id.newsViewPager);
        newsTabLayout = getView().findViewById(R.id.newsTabLayout);
        localNewsTab = getView().findViewById(R.id.localNewsTab);
        globalNewsTab= getView().findViewById(R.id.globalNewsTab);


        NewsPagerAdapter newsPagerAdapter = new NewsPagerAdapter(getContext(), getChildFragmentManager());
        newsViewPager.setAdapter(newsPagerAdapter);
        newsTabLayout.setupWithViewPager(newsViewPager);

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (localNewsFragment!=null&&globalNewsFragment!=null) {
//            localNewsFragment.onResume();
//            globalNewsFragment.onResume();
//        }
//        NewsPagerAdapter newsPagerAdapter = new NewsPagerAdapter(getContext(), getFragmentManager());
//        newsViewPager.setAdapter(newsPagerAdapter);
//        newsTabLayout.setupWithViewPager(newsViewPager);
//        Log.d("localnews", "resumed1");
//    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (localNewsFragment!=null&&globalNewsFragment!=null) {
//            localNewsFragment.onPause();
//            globalNewsFragment.onPause();
//        }
//        Log.d("localnews", "paused1");
//    }
    public class NewsPagerAdapter extends FragmentPagerAdapter {
        Context context;
        public NewsPagerAdapter(Context context, @NonNull FragmentManager fm) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int i){
            if (i==0) {
                LocalNewsFragment localNewsFragment = new LocalNewsFragment("https://newsapi.org/v2/top-headlines?q=c" +
                        "oronavirus&language=en&country=sg&apiKey=98d25766996b4d85a81df8c048cffe35");
                NewsFragment.localNewsFragment = localNewsFragment;
                return localNewsFragment;
            }
            else {
                LocalNewsFragment globalNewsFragment = new LocalNewsFragment("https://newsapi.org/v2/top-headlines?q=c" +
                        "oronavirus&language=en&apiKey=98d25766996b4d85a81df8c048cffe35");
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
