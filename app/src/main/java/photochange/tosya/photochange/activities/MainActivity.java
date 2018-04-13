package photochange.tosya.photochange.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.SupportActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;
import butterknife.OnClick;
import photochange.tosya.photochange.fragments.DropBoxListFragment;
import photochange.tosya.photochange.R;
import photochange.tosya.photochange.content.DropBoxListContent;
import photochange.tosya.photochange.fragments.PhotographersFragment;
import photochange.tosya.photochange.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DropBoxListFragment.OnListFragmentInteractionListener {

    TextView mNameLabel;
    TextView mEmailLabel;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mNameLabel = navigationView.getHeaderView(0).findViewById(R.id.menu_name_label);
        mEmailLabel = navigationView.getHeaderView(0).findViewById(R.id.menu_email_label);

        mAuth = FirebaseAuth.getInstance();

        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            mNameLabel.setText(mUser.getDisplayName());
            mEmailLabel.setText(mUser.getEmail());
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new PhotographersFragment()).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ProfileFragment()).commit();
        }  else if (id == R.id.nav_persons) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new PhotographersFragment()).commit();
        }  else if (id == R.id.nav_exit) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.menu_lines)
    void onMenuOpen() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void onListFragmentInteraction(DropBoxListContent.DropBoxItem item) {

    }
}
