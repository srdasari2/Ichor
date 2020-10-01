package io.particle.hydroalert;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class SummaryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value_tabs);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);


        EventFragmentPagerAdapter mFragmentPagerAdapter = new EventFragmentPagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mFragmentPagerAdapter);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:       // Handling back arrow
                Intent upInent = NavUtils.getParentActivityIntent(this);
                //  if(NavUtils.shouldUpRecreateTask(this, upInent)){
                TaskStackBuilder.create(this).addNextIntentWithParentStack(upInent).startActivities();
                finish();
        }
        return true;
    }
    public void onBackPressed(){   // When Android back button is pressed,  taking user back to devices screen
        super.onBackPressed();
        Intent upInent = NavUtils.getParentActivityIntent(this);
        TaskStackBuilder.create(this).addNextIntentWithParentStack(upInent).startActivities();
        finish();
    }
}