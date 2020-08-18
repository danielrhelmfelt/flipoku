package com.flipoku;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flipoku.Domain.Board;
import com.flipoku.Domain.Statistics;
import com.flipoku.Repository.StatisticsRepository;
import com.flipoku.Util.StaticMethodsHelper;

public class StatisticsActivity extends AppCompatActivity {
    //private static final String TAG = "flipoku";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout statisticsTabLayout;
    //private DialogCreator dialogCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.statisticsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        statisticsTabLayout = (TabLayout) findViewById(R.id.statisticsTabLayout);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(statisticsTabLayout));
        statisticsTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        //dialogCreator = new DialogCreator(this, this.getLayoutInflater());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_erase_all) {
            //Log.i(TAG, "Clickeado eliminar todos los MODOS");
            //preguntamos si esta seguro
            //dialogCreator.createDialogAreYouSure(yesDeleteHandler, null, getResources().getString(R.string.erase), getResources().getString(R.string.cancel)).show();
            // eliminamos todos los Statistics
            StatisticsRepository repository = Statistics.getRepository(StatisticsActivity.this);
            repository.deleteAll();
            mViewPager.setAdapter(mSectionsPagerAdapter);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
            TextView playedTextView = (TextView) rootView.findViewById(R.id.playedTextView);
            TextView wonTextView = (TextView) rootView.findViewById(R.id.wonTextView);
            TextView lostTextView = (TextView) rootView.findViewById(R.id.lostTextView);
            TextView bestTimeTextView = (TextView) rootView.findViewById(R.id.bestTimeTextView);
            TextView winningStreakTextView = (TextView) rootView.findViewById(R.id.winningStreakTextView);

            int currentClues = 0;
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                currentClues = Board.CLUES_EASY;
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                currentClues = Board.CLUES_MEDIUM;
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                currentClues = Board.CLUES_HARD;
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 4) {
                currentClues = Board.CLUES_EXPERT;
            }
            StatisticsRepository repository = Statistics.getRepository(rootView.getContext());
            Statistics currentStatistics = repository.findByClues(currentClues);
            if (currentStatistics == null) {
                currentStatistics = new Statistics(currentClues, 0, 0, 0, 0);
                currentStatistics = repository.save(currentStatistics);
            }
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            bestTimeTextView.setText(StaticMethodsHelper.getTimeFromMillis(currentStatistics.getBestTime()));
            playedTextView.setText((currentStatistics.getLoses() + currentStatistics.getWins()) + "");
            wonTextView.setText(currentStatistics.getWins() + "");
            lostTextView.setText(currentStatistics.getLoses() + "");
            winningStreakTextView.setText(currentStatistics.getStreak() + "");
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show total pages.
            return 4;
        }
    }
}