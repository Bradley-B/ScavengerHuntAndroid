package com.bradleyboxer.scavengerhunt.v3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.bradleyboxer.scavengerhunt.R;

public abstract class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int checkedId;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    void setCheckedId(int id) {
        this.checkedId = id;
        navigationView.setCheckedItem(checkedId);
    }

    @SuppressLint("RestrictedApi")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(checkedId==id) {
            return false;
        }

        Intent intent = null;
        if (id == R.id.nav_progress) {
            intent = new Intent(this, MainActivity.class);
        } else if (id == R.id.nav_clues) {
            intent = new Intent(this, ClueViewActivity.class);
        } else if (id == R.id.nav_export_qr) {

        } else if(id == R.id.nav_import_qr) {

        } else if (id == R.id.nav_answer_compass_clue) {
            ScavengerHunt scavengerHunt = FileUtil.loadScavengerHunt(this);
            Clue earliestUnsolved = scavengerHunt.getEarliestUnsolved(Clue.Type.COMPASS);

            intent = new Intent(this, CompassActivity.class);
            intent.putExtra("clue", earliestUnsolved);
        } else if (id == R.id.nav_answer_text_clue) {
            intent = new Intent(this, TextInputActivity.class);
        }

        if(intent==null) {
            return false;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        overridePendingTransition(0, 0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(checkedId);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }
}
