package com.samansepahvand.mazhabi.ziyaratashora;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.autofill.AutofillValue;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.samansepahvand.mazhabi.ziyaratashora.Adapter.RecyclerAdapter;
import com.samansepahvand.mazhabi.ziyaratashora.AnotherPager.About;
import com.samansepahvand.mazhabi.ziyaratashora.AnotherPager.Bookmark;
import com.samansepahvand.mazhabi.ziyaratashora.AnotherPager.Contact;
import com.samansepahvand.mazhabi.ziyaratashora.Data.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    String ziyarat;
    RecyclerAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<DataModel> dataModels=new ArrayList<>();
    String st_matn,st_tarjomeh;
    SharedPref sharedPref;


    ////fab
    private ImageView buttonPlayStop;
    public MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private View fab;
    FABToolbarLayout layout;
    boolean play_flage=true;
    private final Handler handler = new Handler();
    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref=new SharedPref(this);
        if (sharedPref.loadNightModeState()==true){

            setTheme(R.style.darkthem);
        }else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences sharedPreferences=getSharedPreferences("Screen",MODE_PRIVATE);
        if (sharedPreferences.contains("Screen_On")){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }


        Toolbar toolbar1=(Toolbar)findViewById(R.id.toolbar1);

        setSupportActionBar(toolbar1);
        initViews();



        FloatingActionButton fab_sahre=(FloatingActionButton)findViewById(R.id.fab_share);
        fab_sahre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareData(v);
            }
        });

        ImageView img_more=(ImageView)findViewById(R.id.img_more) ;
        img_more.setOnClickListener(
                new View.OnClickListener() {
                    @SuppressLint("RestrictedApi")

            @Override
            public void onClick(View v) {
                MenuBuilder menuBuilder=new MenuBuilder(MainActivity.this);
                MenuInflater inflater=new MenuInflater(MainActivity.this);
                inflater.inflate(R.menu.item,menuBuilder);
                MenuPopupHelper popupHelper=new MenuPopupHelper(MainActivity.this,menuBuilder,v);
                popupHelper.setForceShowIcon(true);

                        menuBuilder.setCallback(new MenuBuilder.Callback() {
                            @Override
                            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.item_about:
                                        startActivity(new Intent(MainActivity.this,About.class));
                                         return true;
                                    case R.id.item_contact:
                                        startActivity(new Intent(MainActivity.this,Contact.class));
                                         return true;
                                }


                                return false;
                            }

                            @Override
                            public void onMenuModeChange(MenuBuilder menu) {

                            }
                        });
                popupHelper.show();
            }
        });


        ImageView img_setting=(ImageView )findViewById(R.id.img_srtting);
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Setting.class));
            }
        });

        ImageView img_bookmark=(ImageView )findViewById(R.id.img_bookmark);
        img_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Bookmark.class));
            }
        });


        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);

        adapter=new RecyclerAdapter(dataModels,MainActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        recyclerView.setAdapter(adapter);
        getJson();



    }

    public void getJson(){




        try {

            InputStream inputStream = getAssets().open("data.json");

            int size = inputStream.available();
            byte[] buffer = new byte[size];

            inputStream.read(buffer);
            inputStream.close();


            ziyarat = new String(buffer, "UTF-8");


            if (ziyarat != null) {

                JSONObject jo = new JSONObject(ziyarat);

                JSONArray ziyarat = jo.getJSONArray("ziyarat");
                for (int i = 0; i < ziyarat.length(); i++) {

                    JSONObject o = ziyarat.getJSONObject(i);
                    st_matn = o.getString("matn");
                    st_tarjomeh = o.getString("tarjomeh");

                    dataModels.add(new DataModel(st_matn, st_tarjomeh));
                }

                adapter.notifyDataSetChanged();



            }else {

                Toast.makeText(this, "فایل دیتابیس موجود نیست!", Toast.LENGTH_SHORT).show();
                finish();
            }
            }catch(IOException e){
                e.printStackTrace();
                Toast.makeText(this, "IOException : " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }catch(JSONException e){
                e.printStackTrace();
                Toast.makeText(this, "JSONException : " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }




    }


/////fab_Action






    private void initViews() {






        layout = (FABToolbarLayout) findViewById(R.id.fab_toolbar);
        fab = findViewById(R.id.fabtoolbar_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.show();
            }
        });
        ImageView img_close=(ImageView)findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.hide();
            }
        });



        buttonPlayStop = (ImageView) findViewById(R.id.ButtonPlayStop);

        buttonPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick();
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.ziarat_ashura);
        seekBar = (SeekBar) findViewById(R.id.seekbar01);
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnTouchListener(new View.OnTouchListener() {public boolean onTouch(View v, MotionEvent event) {
            seekChange(v);
            return false; }
        });

    }
    public void startPlayProgressUpdater() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification,1000);
        }else{
            mediaPlayer.pause();
            play_flage=true;
            buttonPlayStop.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
            seekBar.setProgress(0);
        }
    }
    private void seekChange(View v){
        if(mediaPlayer.isPlaying()){
            SeekBar sb = (SeekBar)v;
            mediaPlayer.seekTo((int) sb.getProgress());
        }
    }
    private void buttonClick(){
        if (play_flage==true) {

            buttonPlayStop.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
            play_flage=false;
            try{
                mediaPlayer.start();
                startPlayProgressUpdater();
            }catch (IllegalStateException e) {
                mediaPlayer.pause();
            }
        }else {

            buttonPlayStop.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
            play_flage=true;
            mediaPlayer.pause();
        }
    }
    @Override
    public void onBackPressed() {
        layout.hide();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        super.onPause();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void shareData(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String my_string = getResources().getString(R.string.text_full);
        intent.putExtra(Intent.EXTRA_TEXT, my_string);
        startActivity(Intent.createChooser(intent, "Share this text via"));
    }
}
