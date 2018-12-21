package com.sstudio.ratings;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.sstudio.ratings.Models.com.moviemeter.Datum;
import com.sstudio.ratings.fragments.About;
import com.sstudio.ratings.fragments.ComingSoon;
import com.sstudio.ratings.fragments.DetailVIew;
import com.sstudio.ratings.fragments.Intheater_fragment;
import com.sstudio.ratings.fragments.Search;
import com.sstudio.ratings.fragments.TopMovies;
import com.sstudio.ratings.vision.OcrCaptureActivity;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ItemFragment.OnListFragmentInteractionListener,
        TopMovies.OnFragmentInteractionListener,
        Intheater_fragment.OnFragmentInteractionListener,
        Search.OnFragmentInteractionListener,
        ComingSoon.OnFragmentInteractionListener,
        DetailVIew.OnFragmentInteractionListener , About.OnFragmentInteractionListener{

    private static final String TAG = "Main Activity";
    String find = "interstellar";
    SearchView search;
    RequestQueue queue;
    FloatingActionButton fab;
    FragmentManager manager;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        search = findViewById(R.id.et);
        fab = findViewById(R.id.fab);

        try {
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().get("fromWidget").equals("yes")) {      //if the intent data is from widget open ocr stuff directly
                    Intent intent = new Intent(MainActivity.this, OcrCaptureActivity.class);
                    intent.putExtra(OcrCaptureActivity.AutoFocus, true);
                    intent.putExtra(OcrCaptureActivity.UseFlash, false);
                    startActivityForResult(intent, 11);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Activity to open camera and scan for movie names
                Intent intent = new Intent(MainActivity.this, OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, true);
                intent.putExtra(OcrCaptureActivity.UseFlash, false);
                startActivityForResult(intent, 11);
               // startActivity(new Intent(MainActivity.this,TrailerPlayer.class));
            }
        });
        queue = Volley.newRequestQueue(MainActivity.this);
        search.setSubmitButtonEnabled(true);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (search.getQuery().toString() != null || search.getQuery().toString() != "") {
                    find = search.getQuery().toString().trim();
                    Bundle bundle = new Bundle();
                    bundle.putString("find", find);
                    Search fragment = new Search();
                    fragment.setArguments(bundle);
                    manager = getSupportFragmentManager();
                    transaction = manager.beginTransaction();
                    transaction.setCustomAnimations(R.animator.enter, R.animator.exit, R.animator.enter, R.animator.exit);
                    transaction.add(R.id.container_lay, fragment,"find");
                    transaction.addToBackStack("find");
                    transaction.commit();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intheater_fragment fragment = new Intheater_fragment();
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter, R.animator.exit, R.animator.enter, R.animator.exit);
        transaction.add(R.id.container_lay, fragment,"theater");
        transaction.addToBackStack("theater");
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        if(manager.getBackStackEntryCount()==0){
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter, R.animator.exit, R.animator.enter, R.animator.exit);
        if (id == R.id.nav_camera) {
            if (manager.findFragmentByTag("top")!=null){
                for (Fragment frag:manager.getFragments()){
                    if (frag==manager.findFragmentByTag("top")){
                        transaction.show(frag).commit();
                    }else {
                        transaction.hide(frag);
                    }
                }
            }else {
                TopMovies fragment = new TopMovies();
                transaction.add(R.id.container_lay, fragment,"top");
                transaction.addToBackStack("top");
                transaction.commit();
            }

        } else if (id == R.id.nav_gallery) {
            if (manager.findFragmentByTag("theater")!=null){
                for (Fragment frag:manager.getFragments()){
                    if (frag==manager.findFragmentByTag("theater")){
                        transaction.show(frag).commit();
                    }else {
                        transaction.hide(frag);
                    }
                }
            }else {
                Intheater_fragment fragment = new Intheater_fragment();
                transaction.add(R.id.container_lay, fragment, "theater");
                transaction.addToBackStack("theater");
                transaction.commit();
            }
        } else if (id == R.id.nav_slideshow) {
            if (manager.findFragmentByTag("coming")!=null){
                for (Fragment frag:manager.getFragments()){
                    if (frag==manager.findFragmentByTag("coming")){
                        transaction.show(frag).commit();
                    }else {
                        transaction.hide(frag);
                    }
                }
            }else {
                ComingSoon fragment = new ComingSoon();
                transaction.add(R.id.container_lay, fragment, "coming");
                transaction.addToBackStack("coming");
                transaction.commit();
            }
        } else if (id == R.id.nav_manage) {
            if (manager.findFragmentByTag("item")!=null){
                for (Fragment frag:manager.getFragments()){
                    if (frag==manager.findFragmentByTag("item")){
                        transaction.show(frag).commit();
                    }else {
                        transaction.hide(frag);
                    }
                }
            }else {
                ItemFragment fragment = new ItemFragment();
                transaction.add(R.id.container_lay, fragment, "item");
                transaction.addToBackStack("item");
                transaction.commit();
            }
        } else if (id == R.id.nav_share) {
            shareApplication();
            //Toast.makeText(this, "//TODO", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            About fragment = new About();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.animator.enter_from_top, R.animator.exit_to_top,
                    R.animator.enter_from_top, R.animator.exit_to_top);
            transaction.add(R.id.container_lay, fragment, "detail");
            transaction.addToBackStack("detail");
            transaction.commit();
            //Toast.makeText(this, "//TODO", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    find = text;
                    /*Trying to get the most optimal output
                     and a relevent file name from the scanned image*/
                    String[] split = text.split("\\,");     // if there is a comma (,) splitting the string and taking the first part
                    split[0] = FilenameUtils.removeExtension(split[0]);     //using apache filename utils to remove extension. will be removed in future
                    find = split[0].replaceAll("\\.", " ");     //removing any dots present in the current string
                    split[0] = find.replaceAll("[^a-zA-Z]", " ");       //replacing any other special characters
                    split = split[0].split("  +");      // now splitting the string from where more than two spaces produced
                    find = split[0];        //assigning the final string to the global variable
                    Log.d(TAG, "Text read: " + find);
                    sugestion("http://google.com/complete/search?&output=toolbar&q=" + find);       //method to get the item[0] from google suggesstion
                    // for getting even more relevent file name
                } else {
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                /*statusMessage.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));*/
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void sugestion(String key) {
        StringRequest request = new StringRequest(Request.Method.GET, key, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                try {
                    List<String> sugstn = new ArrayList<>();
                    JSONObject object = XML.toJSONObject(response);     //parding the xml to json object
                    JSONObject object1;
                    Log.d(TAG, object.toString());
                    object = object.getJSONObject("toplevel");      // getting object named toplevel
                    JSONArray array = object.getJSONArray("CompleteSuggestion");
                    for (int i = 0; i < array.length(); i++) {
                        object1 = array.getJSONObject(i);
                        object1 = object1.getJSONObject("suggestion");
                        sugstn.add(object1.get("data").toString());
                        Log.d(TAG, sugstn.toString());
                    }
                    if (sugstn.contains(find.toLowerCase())) {
                        Toast.makeText(MainActivity.this, "Searching " + find, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Searching " + sugstn.get(0), Toast.LENGTH_SHORT).show();
                        find = sugstn.get(0);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("find", find);
                    Search fragment = new Search();
                    fragment.setArguments(bundle);
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.container_lay, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    search.setQuery(find, false);
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Try again.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }


    @Override
    public void onTopFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTheaterInteraction(Uri uri) {

    }

    @Override
    public void onSearchFragmentInteraction(Uri uri) {

    }

    @Override
    public void onComingSoonFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Datum item) {
        //Toast.makeText(this, "Item clicked"+item.getIdIMDB(), Toast.LENGTH_SHORT).show();
    }

    /*to hide the fab from outside the activity*/
    public void hideFab() {
        fab.hide();
    }

    /*to show the fab from outside the activty*/
    public void showFab() {
        fab.show();
    }


    //private int oldOptions;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                oldOptions = getWindow().getDecorView().getSystemUiVisibility();
                int newOptions = oldOptions;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    newOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE &
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE &
                            View.SYSTEM_UI_FLAG_FULLSCREEN &
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION &
                            View.SYSTEM_UI_FLAG_IMMERSIVE &
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                }
                getWindow().getDecorView().setSystemUiVisibility(newOptions);
                getSupportActionBar().hide();
            } else {
                getWindow().getDecorView().setSystemUiVisibility(oldOptions);
                getSupportActionBar().show();
            }
        }*/
    }

    @Override
    public void onDetailFragmentInteraction(Uri uri) {

    }
    private void shareApplication() {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        // MIME of .apk is "application/vnd.android.package-archive".
        // but Bluetooth does not accept this. Let's use "*/*" instead.
        intent.setType("*/*");

        // Append file and send Intent
        File originalApk = new File(filePath);

        try {
            //Make new directory in new location
            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk");
            //If directory doesn't exists create new
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            //Get application's name and convert to lowercase
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ","").toLowerCase() + ".apk");
            //If file doesn't exists create new
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            //Copy file to new location
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            //Open share dialog
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID+".provider",tempFile) /*Uri.fromFile(tempFile)*/);
            startActivity(Intent.createChooser(intent, "Share app via").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
