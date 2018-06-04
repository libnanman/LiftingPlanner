package com.libnanman.liftingplanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView liftListRecyclerView;
    //private RecyclerView.Adapter liftListAdapter;
    private LiftRecyclerViewAdapter liftListAdapter;
    private RecyclerView.LayoutManager liftListLayoutManager;
    private List<Lift> liftList = new ArrayList<>();
    private EditText liftName;
    private EditText liftMax;
    private Button addLift;
    private Button viewWorkout;
    private MenuItem newMax;
    private MenuItem deleteLift;
    private MenuItem calendarButton;
    private Toolbar mainActionBar;
    private CalendarView calendarView;
    private String date;
    private int newMaxValue;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLiftList();

        liftName = (EditText) findViewById(R.id.liftName);
        liftMax = (EditText) findViewById(R.id.liftMax);
        addLift = (Button) findViewById(R.id.addLiftButton);
        viewWorkout = (Button) findViewById(R.id.viewWorkoutButton);
        newMax = (MenuItem) findViewById(R.id.newMax);
        deleteLift = (MenuItem) findViewById(R.id.deleteLift);
        calendarButton = (MenuItem) findViewById(R.id.calendar_button);
        mainActionBar = (Toolbar) findViewById(R.id.main_toolbar);
        //calendarView = (CalendarView) findViewById(R.id.calendarView);

        setSupportActionBar(mainActionBar);

        liftListRecyclerView = (RecyclerView) findViewById(R.id.liftListRecyclerView);

        liftListAdapter = new LiftRecyclerViewAdapter(liftList);
        liftListLayoutManager = new LinearLayoutManager(getApplicationContext());
        liftListRecyclerView.setLayoutManager(liftListLayoutManager);
        liftListRecyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        liftListRecyclerView.addItemDecoration(dividerItemDecoration);

        liftListRecyclerView.setAdapter(liftListAdapter);

        liftListRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), liftListRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Lift lift = liftList.get(position);
                Toast.makeText(getApplicationContext(), lift.getName() + " video!", Toast.LENGTH_SHORT).show();

                String mediaPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +"/100MEDIA/VIDEO0026.mp4";
                File media = new File(mediaPath);
                Uri uri = Uri.fromFile(media);
                Intent videoIntent5 = new Intent(Intent.ACTION_VIEW);
                videoIntent5.setDataAndType(uri, "video/*");
                Intent chooser = Intent.createChooser(videoIntent5, getResources().getString(R.string.choose_video_app));
                startActivity(chooser);
            }

            @Override
            public void onLongClick(View view, int position) {
                //do nothing
                //getSupportActionBar().hide();
            }
        }));

        registerForContextMenu(liftListRecyclerView);

        date = simpleDateFormat.format(new Date());

        //prepareTestLifts();

    }

    public void onAddLift(View view) {
        if(liftName.getText().toString().equals("") || liftMax.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Lift not saved. There was an empty field you dummy.", Toast.LENGTH_LONG).show();
            return;
        }

        String filename = "liftData";
        File file = new File(getApplicationContext().getFilesDir(), filename);
        FileOutputStream outputStream;

        Lift lift = new Lift(liftName.getText().toString(), Integer.parseInt(liftMax.getText().toString()), new Date());
        String fileContents = liftName.getText().toString() + "!@!" + liftMax.getText().toString() + "!@!" + new Date() +"\n";
        liftList.add(lift);

        try {
            if(file.exists())
                outputStream = new FileOutputStream(getApplicationContext().getFilesDir().toString() + "/" + filename, true);
            else
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);

            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        liftName.setText("");
        liftMax.setText("");

        liftListAdapter.notifyDataSetChanged();
    }

    public void onViewTodayWorkout(View view) {
        date = simpleDateFormat.format(new Date());
        onViewWorkout(view);
    }


    public void onViewWorkout(View view) {
        Intent intent = new Intent(this, ViewWorkoutActivity.class);
        intent.putExtra("date", date);
        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = liftListAdapter.getPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (item.getItemId()) {
            case R.id.newMax:
                showNewMaxDialog(position);
                break;
            case R.id.takeVideo:
                //do something
                break;
            case R.id.selectVideo:
                //onSelectVideo(position);
                break;
            case R.id.deleteLift:
                removeLift(position);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void setLiftList() {
        //File directory = getApplicationContext().getFilesDir();
        //File file = new File(directory, "liftData");
        String filename = "liftData";
        String data = "";
        int byteVal;
        char c;
        try {
            FileInputStream inputStream = openFileInput(filename);
            //current = inputStream.read();
            while((byteVal = inputStream.read()) != -1){
                c = (char) byteVal;
                data = data + c;
            }

            String[] tokens = data.split("\n");
            for (String token : tokens) {
                liftConversion(token);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        //liftListAdapter.notifyDataSetChanged();
    }

    private void resetLiftListData() {
        String filename = "liftData";
        FileOutputStream outputStream;

        String fileContents = "";

        for(Lift lift : liftList) {
            fileContents = fileContents + lift.getName() + "!@!" + Integer.toString(lift.getMax()) + "!@!" + lift.getDate() +"\n";
        }

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        liftListAdapter.notifyDataSetChanged();
    }

    private void liftConversion(String splitString) throws ParseException {
        String[] tokens = splitString.split("!@!");
        Lift lift = new Lift(tokens[0], Integer.parseInt(tokens[1]), new Date(tokens[2]));

        liftList.add(lift);
    }

    protected void showNewMaxDialog(final int position) {

        //int newMax;
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.new_max_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.newMaxDialogEditText);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(!editText.getText().toString().equals("")) {
                            newMaxValue = Integer.parseInt(editText.getText().toString());
                            setNewMax(position);
                        }
                        else
                            Toast.makeText(getApplicationContext(), "New max not saved. You didn't enter a value, dummy.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    private void onSelectVideo(int position) {
        Lift lift = liftList.get(position);
        
    }

    protected void showCalendarDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.calendar_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);


        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        calendarView = (CalendarView) promptView.findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                Toast.makeText(getApplicationContext(), month + 1 + "/" + day + "/" + year + " selected", Toast.LENGTH_LONG).show();
                date = (month + 1) + "/" + day + "/" + year;
                onViewWorkout(view);
                alert.cancel();
            }
        });

    }


    private void setNewMax(int position) {
        Lift lift = liftList.get(position);
        Toast.makeText(getApplicationContext(), lift.getName() + " max changed", Toast.LENGTH_SHORT).show();
        liftList.remove(position);
        lift.setDate(new Date());
        lift.setMax(newMaxValue);
        liftList.add(lift);
        resetLiftListData();
    }

    private void removeLift(int position) {
        Lift lift = liftList.get(position);
        Toast.makeText(getApplicationContext(), lift.getName() + " deleted", Toast.LENGTH_SHORT).show();
        liftList.remove(position);
        resetLiftListData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.calendar_button:
                showCalendarDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
