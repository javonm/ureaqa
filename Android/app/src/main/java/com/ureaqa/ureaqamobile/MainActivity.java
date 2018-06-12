package com.ureaqa.ureaqamobile;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {


    String username, name, last_name;
    int weight, birthday;

    ResultsFragment resultsFragment;
    InsightsFragment insightsFragment;
    SettingsFragment settingsFragment;
    TestFragment testFragment;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        last_name = intent.getStringExtra("last_name");
        birthday = intent.getIntExtra("birthday", -1);
        username = intent.getStringExtra("username");
        weight = intent.getIntExtra("weight", -1);

        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("name", name);
        bundle.putString("last_name", last_name);
        bundle.putInt("birthday", birthday);
        bundle.putInt("weight",weight);


        //FrameLayout mMainFrame = (FrameLayout) findViewById(R.id.content_frame);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        resultsFragment = new ResultsFragment();
        insightsFragment = new InsightsFragment();
        settingsFragment = new SettingsFragment();
        testFragment = new TestFragment();

        insightsFragment.setArguments(bundle);
        settingsFragment.setArguments(bundle);
        resultsFragment.setArguments(bundle);
        testFragment.setArguments(bundle);


        setFragment(resultsFragment);



        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // update the UI based on the item selected

                        switch (menuItem.getItemId()) {
                            case R.id.nav_results:
                                MainActivity.this.setTitle(getResources().getString(R.string.results));
                                setFragment(resultsFragment);
                                break;
                            case R.id.nav_test:
                                MainActivity.this.setTitle(getResources().getString(R.string.test));
                                setFragment(testFragment);
                                break;
                            /*case R.id.nav_insights:
                                //do the thing
                                MainActivity.this.setTitle(getResources().getString(R.string.insights));
                                setFragment(insightsFragment);
                                break;
                            case R.id.nav_settings:
                                //ok
                                MainActivity.this.setTitle(getResources().getString(R.string.settings));
                                setFragment(settingsFragment);
                                break;
                            */

                        }

                        return true;
                    }
                });

        //mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {});

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setFragment(android.support.v4.app.Fragment fragment) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }
}
