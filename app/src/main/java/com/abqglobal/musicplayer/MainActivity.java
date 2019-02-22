package com.abqglobal.musicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button music;
    ListView listView;
    ContentResolver contentResolver;
    Uri uri;
    String songTitle,songTitles;
    // list to store all titles
    List<String> list,lists;

    // M is marshmallow version
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        music = findViewById(R.id.showmusic);
        listView = findViewById(R.id.listview);
        // giving array to list
        list=new ArrayList<>();
        lists=new ArrayList<>();

        // dangerous runtime permission

        runTimePermission();

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getAllMusic();
                // attaches data inside list in form of array
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,lists);
                listView.setAdapter(arrayAdapter);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaPlayer mediaPlayer=MediaPlayer.create(getApplicationContext(),uri.parse(list.get(position)));
                mediaPlayer.start();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getAllMusic()
    {
        // A class ContentResolver helps to access the data inside your mobile

        contentResolver=getApplicationContext().getContentResolver();
        uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        //
        Cursor cursor=contentResolver.query(uri,null,null,null,null);
        if(cursor==null){
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }
        else if(!cursor.moveToFirst()){
            Toast.makeText(this, "No Music Found", Toast.LENGTH_SHORT).show();
        }
        else{
            // to get the index position and title of the music media
            int title=cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int titles=cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            do{
                songTitle=cursor.getString(title);
                songTitles=cursor.getString(titles);
                // Adding Media files to list in ArrayList
                list.add(songTitle);
                lists.add(songTitles);
            }
            while (cursor.moveToNext());
        }
    }


    private void runTimePermission() {

        // runtime permission released after Marshmallow version
        // phones above marshmallow will get popup to allow deny location, camera etc
        // phones below marshmallow will get directly allowed

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            // Package Manager is a inbuilt class which stores all the permissions allowed
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                // rationale method shows alert Dialog
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(MainActivity.this);
                    alert_builder.setMessage("External Storage Permission Required");
                    alert_builder.setTitle("Please Grant Permission");
                    alert_builder.setPositiveButton("ALLOW", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // ActivityCompat is the class which has inbuilt method requestPermissions
                            // requestPermissions has a brother onRequestPermissionResult method
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                    });
                    alert_builder.setNeutralButton("DENY",null);

                    AlertDialog alertDialog=alert_builder.create();
                    alertDialog.show();
                }
                else {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            }


        }
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    // int[] grantResult array has index of all the permissions to be asked to allow
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1)
        {
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {

            }
        }

    }
}
