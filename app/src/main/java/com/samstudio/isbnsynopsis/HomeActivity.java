package com.samstudio.isbnsynopsis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mrengineer13.snackbar.SnackBar;
import com.samstudio.isbnsynopsis.fragments.HomeFragment;
import com.samstudio.isbnsynopsis.fragments.ISBNSearchFragment;
import com.samstudio.isbnsynopsis.fragments.LoginFragment;
import com.samstudio.isbnsynopsis.fragments.SearchBookFragment;
import com.samstudio.isbnsynopsis.utils.CommonConstants;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button scanBarcodeBtn;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private boolean isInputting;
    private String message;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        message = "Selamat Datang";
        handleIntent();

        new SnackBar.Builder(this)
                .withMessage(message) // OR
                .withTextColorId(R.color.colorAccent)
                .withBackgroundColorId(R.color.colorPrimary)
                .withDuration((short) 5000)
                .show();


        android.support.v4.app.FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content, Fragment.instantiate(this, HomeFragment.class.getCanonicalName()));
        tx.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(ACTION_SCAN);
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(intent, CommonConstants.SCAN_BOOK_CODE);
                } catch (ActivityNotFoundException anfe) {
                    showDialog(HomeActivity.this, "No Scanner Found", "Download a code scanner activity?", "Yes", "No").show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        if (ISBNSynopsisApplication.getInstance().isLoggedIn()) {
            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_login);
            menuItem.setTitle("Log Out");
        }

        TextView nameHeaderTV = (TextView) findViewById(R.id.name_header_tv);
        TextView emailHeaderTV = (TextView) findViewById(R.id.email_header_tv);
        nameHeaderTV.setText(ISBNSynopsisApplication.getInstance().getSharedPreferences().getString(CommonConstants.NAME, "Android"));
        emailHeaderTV.setText(ISBNSynopsisApplication.getInstance().getSharedPreferences().getString(CommonConstants.EMAIL, "email"));
    }

    private void handleIntent() {
        Intent intent = getIntent();
        message = intent.getStringExtra(CommonConstants.MESSAGE);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_scan) {
            try {
                Intent intent = new Intent(ACTION_SCAN);
                intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                startActivityForResult(intent, CommonConstants.SCAN_BOOK_CODE);
            } catch (ActivityNotFoundException anfe) {
                showDialog(HomeActivity.this, "No Scanner Found", "Download a code scanner activity?", "Yes", "No").show();
            }
        } else if (id == R.id.nav_home) {
            android.support.v4.app.FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.content, Fragment.instantiate(this, HomeFragment.class.getCanonicalName()));
            tx.commit();
        } else if (id == R.id.nav_search_book) {
            getSupportActionBar().setTitle("Cari Buku");
            android.support.v4.app.FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.content, Fragment.instantiate(this, SearchBookFragment.class.getCanonicalName()));
            tx.commit();
        } else if (id == R.id.nav_isbn) {
            android.support.v4.app.FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.content, Fragment.instantiate(HomeActivity.this, ISBNSearchFragment.class.getCanonicalName()));
            tx.commit();
        } else if (id == R.id.nav_input) {
            if (ISBNSynopsisApplication.getInstance().isLoggedIn()) {
                try {
                    Intent intent = new Intent(ACTION_SCAN);
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(intent, CommonConstants.INPUT_BOOK_CODE);
                } catch (ActivityNotFoundException anfe) {
                    showDialog(HomeActivity.this, "No Scanner Found", "Download a code scanner activity?", "Yes", "No").show();
                }
            } else {
                makeLoginRequestDialog();
            }
        } else if (id == R.id.nav_about) {
        } else if (id == R.id.nav_login) {
            if(ISBNSynopsisApplication.getInstance().isLoggedIn()) {
                SharedPreferences.Editor editor = ISBNSynopsisApplication.getInstance().getSharedPreferences().edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent (this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(CommonConstants.MESSAGE, "Anda sudah log out");
                startActivity(intent);
            } else {
                android.support.v4.app.FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                tx.replace(R.id.content, Fragment.instantiate(this, LoginFragment.class.getCanonicalName()));
                tx.commit();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void makeLoginRequestDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Log In");
        alertDialog.setMessage("Anda harus log in untuk bisa mengakses fitur ini. Log in?");
        alertDialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                navigationView.getMenu().getItem(4).setChecked(false);
                android.support.v4.app.FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                tx.replace(R.id.content, Fragment.instantiate(HomeActivity.this, LoginFragment.class.getCanonicalName()));
                tx.commit();
                navigationView.getMenu().findItem(R.id.nav_login).setChecked(true);
            }
        });
        alertDialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonConstants.SCAN_BOOK_CODE && resultCode == RESULT_OK) {
            String contents = data.getStringExtra("SCAN_RESULT");
            String format = data.getStringExtra("SCAN_RESULT_FORMAT");

            Intent intent = new Intent(HomeActivity.this, BookDetailActivity.class);
            intent.putExtra(CommonConstants.CONTENT_CODE, contents);
            startActivity(intent);
        } else if (requestCode == CommonConstants.INPUT_BOOK_CODE && resultCode == RESULT_OK) {
            String contents = data.getStringExtra("SCAN_RESULT");
            String format = data.getStringExtra("SCAN_RESULT_FORMAT");

            Intent intent = new Intent(HomeActivity.this, DataInputActivity.class);
            intent.putExtra(CommonConstants.CONTENT_CODE, contents);
            startActivity(intent);
        }
    }
}
