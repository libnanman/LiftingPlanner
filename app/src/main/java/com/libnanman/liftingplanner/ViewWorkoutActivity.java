package com.libnanman.liftingplanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
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
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewWorkoutActivity extends AppCompatActivity {

    private RecyclerView workoutRecyclerView;
    private WorkoutRecyclerViewAdapter workoutAdapter;
    private RecyclerView.LayoutManager workoutLayoutManager;
    private Toolbar viewWorkoutActionBar;
    private TextView viewWorkoutDate;
    private CalendarView calendarView;
    private MenuItem calendarButton;
    private MenuItem deleteWorkout;
    private String date;
    private List<Workout> workoutList = new ArrayList<>();
    private List<Workout> workoutOutputs = new ArrayList<>();
    private List<Lift> liftList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workout);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        viewWorkoutActionBar = (Toolbar) findViewById(R.id.view_workout_toolbar);
        calendarView = (CalendarView) findViewById(R.id.calendarView) ;
        calendarButton = (MenuItem) findViewById(R.id.calendar_button);
        viewWorkoutDate = (TextView) findViewById(R.id.viewWorkoutDate);
        deleteWorkout = (MenuItem) findViewById(R.id.deleteExercise);

        date = getIntent().getExtras().getString("date");

        viewWorkoutDate.setText(date);

        setSupportActionBar(viewWorkoutActionBar);

        workoutRecyclerView = (RecyclerView) findViewById(R.id.workoutListRecyclerView);

        workoutAdapter = new WorkoutRecyclerViewAdapter(workoutOutputs);
        workoutLayoutManager = new LinearLayoutManager(getApplicationContext());
        workoutRecyclerView.setLayoutManager(workoutLayoutManager);
        workoutRecyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        workoutRecyclerView.addItemDecoration(dividerItemDecoration);

        workoutRecyclerView.setAdapter(workoutAdapter);

        workoutRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), workoutRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Workout workout = workoutList.get(position);
                Toast.makeText(getApplicationContext(), workout.getName() + " video!", Toast.LENGTH_SHORT).show();

                String mediaPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +"/100MEDIA/VIDEO0026.mp4";
                File media = new File(mediaPath);
                Uri uri = Uri.fromFile(media);
                Intent videoIntent5 = new Intent(Intent.ACTION_VIEW);
                videoIntent5.setDataAndType(uri, "video/*");
                Intent chooser = Intent.createChooser(videoIntent5, getResources().getString(R.string.choose_video_app));
                startActivity(chooser);

//                }
            }

            @Override
            public void onLongClick(View view, int position) {
                //do nothing
                //getSupportActionBar().hide();
            }
        }));

        registerForContextMenu(workoutRecyclerView);

        setLiftList();
        setWorkoutList();

        //prepareTest();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = workoutAdapter.getPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (item.getItemId()) {
            case R.id.deleteExercise:
                    removeWorkout(position);
                break;
            case R.id.completedExercise:
                    completeWorkout(position);
                break;
        }
        return super.onContextItemSelected(item);
    }

    protected void showCalendarDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(ViewWorkoutActivity.this);
        View promptView = layoutInflater.inflate(R.layout.calendar_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewWorkoutActivity.this);
        alertDialogBuilder.setView(promptView);


        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        calendarView = (CalendarView) promptView.findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                Toast.makeText(getApplicationContext(), month + 1 + "/" + day + "/" + year + " selected", Toast.LENGTH_LONG).show();
                date = (month + 1) + "/" + day + "/" + year;
                setWorkoutList();
                viewWorkoutDate.setText(date);
                alert.cancel();
            }
        });

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

    public void onAddExercise(View view){
        LayoutInflater layoutInflater = LayoutInflater.from(ViewWorkoutActivity.this);
        View promptView = layoutInflater.inflate(R.layout.add_exercise_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewWorkoutActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText newExerciseNameEditText = (EditText) promptView.findViewById(R.id.newExerciseNameEditText);
        final EditText newExerciseWeightEditText = (EditText) promptView.findViewById(R.id.newExerciseWeightEditText);
        final EditText newExerciseSetsEditText = (EditText) promptView.findViewById(R.id.newExerciseSetsEditText);
        final EditText newExerciseRepsEditText = (EditText) promptView.findViewById(R.id.newExerciseRepsEditText);
        final Switch newExerciseWeightSwitch = (Switch) promptView.findViewById(R.id.newExerciseWeightSwitch);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(!newExerciseNameEditText.getText().toString().equals("") && !newExerciseWeightEditText.getText().toString().equals("") && !newExerciseSetsEditText.getText().toString().equals("") && ! newExerciseRepsEditText.getText().toString().equals("")) {

                            if(!(newExerciseWeightSwitch.isChecked() && findLiftMaxByName(newExerciseNameEditText.getText().toString()) == -69)) {


                                Workout workout = new Workout(newExerciseNameEditText.getText().toString(),
                                        Integer.parseInt(newExerciseWeightEditText.getText().toString()),
                                        Integer.parseInt(newExerciseRepsEditText.getText().toString()),
                                        Integer.parseInt(newExerciseSetsEditText.getText().toString()),
                                        newExerciseWeightSwitch.isChecked(), false);
                                onAddWorkout(workout);

                            }
                            else
                                Toast.makeText(getApplicationContext(), "New exercise not saved. You haven't saved a working max for that lift, dummy", Toast.LENGTH_LONG).show();
                            //workoutList.add(workout);
                            //workoutAdapter.notifyDataSetChanged();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "New exercise not saved. You didn't enter a value, dummy.", Toast.LENGTH_LONG).show();
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

    public void onAddWorkout(Workout workout) {

        String filename = "workout_" + dateConverter(date);
        File file = new File(getApplicationContext().getFilesDir(), filename);
        FileOutputStream outputStream;

        String fileContents = workout.getName() + "!@!" + workout.getWeight() + "!@!" + workout.getSets() + "!@!"
                + workout.getReps() + "!@!" + workout.isPercentMax() + "!@!" + workout.isComplete() + "\n";

        //Workout lift = new Lift(workout.getName(), Integer.parseInt(liftMax.getText().toString()), new Date());
//        if(!workout.isPercentMax()) {
//            fileContents = workout.getName() + "!@!" + workout.getWeight() + "!@!" + workout.getSets() + "!@!" + workout.getReps() + "\n";
//        }
//        else {
//            fileContents = workout.getName() + "!@!" + workout.getWeight() + "!@!" + workout.getSets() + "!@!" + workout.getReps() + "\n";
//        }

        workoutList.add(workout);

        try {
            if (file.exists())
                outputStream = new FileOutputStream(getApplicationContext().getFilesDir().toString() + "/" + filename, true);
            else
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);

            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setWorkoutOutputs();

        workoutAdapter.notifyDataSetChanged();
    }

    private void setWorkoutOutputs() {
        workoutOutputs.removeAll(workoutOutputs);
        for(Workout workout : workoutList) {
            if(workout.isPercentMax()) {
                double weight = workout.getWeight()*0.01*findLiftMaxByName(workout.getName());
                workoutOutputs.add(new Workout(workout.getName(), (int) weight, workout.getReps(), workout.getSets(), workout.isPercentMax(), workout.isComplete()));
            }
            else
                workoutOutputs.add(workout);
        }
    }

    private void setWorkoutList() {
        //File directory = getApplicationContext().getFilesDir();
        //File file = new File(directory, "liftData");
        workoutList.removeAll(workoutList);
        String filename = "workout_" + dateConverter(date);
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
                workoutConversion(token);
            }

        } catch (Exception e) {
            //workoutList.removeAll(workoutList);
            e.printStackTrace();
        }

        setWorkoutOutputs();

        workoutAdapter.notifyDataSetChanged();
    }

    private void workoutConversion(String splitString) throws ParseException {
        String[] tokens = splitString.split("!@!");
        Workout workout = new Workout(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[2]), Boolean.parseBoolean(tokens[4]), Boolean.parseBoolean(tokens[5]));

        workoutList.add(workout);
    }

    private void resetWorkoutListData() {
        String filename = "workout_" + dateConverter(date);
        FileOutputStream outputStream;

        String fileContents = "";

        for(Workout workout : workoutList) {
            fileContents = fileContents + workout.getName() + "!@!" + Integer.toString(workout.getWeight()) + "!@!" +
                    Integer.toString(workout.getSets()) +"!@!" + Integer.toString(workout.getReps()) + "!@!" +
                    workout.isPercentMax() + "!@!" + workout.isComplete() + "\n";
        }

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setWorkoutOutputs();

        workoutAdapter.notifyDataSetChanged();
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

    private void liftConversion(String splitString) throws ParseException {
        String[] tokens = splitString.split("!@!");
        Lift lift = new Lift(tokens[0], Integer.parseInt(tokens[1]), new Date(tokens[2]));

        liftList.add(lift);
    }

    private void prepareTest() {
        Workout workout = new Workout("Bench", 150, 10, 3, false, false);
        workoutList.add(workout);
        workout = new Workout("Military", 100, 10, 3, false, false);
        workoutList.add(workout);
        return;
    }

    private String dateConverter(String date) {
        return date.replace("/", "-");
    }

    private double findLiftMaxByName(String liftName) {
        for(Lift lift : liftList) {
            if(lift.getName().equals(liftName))
                return lift.getMax();
        }
        return -69;
    }

    private void removeWorkout(int position) {
        Workout workout = workoutList.get(position);
        Toast.makeText(getApplicationContext(), workout.getName() + " deleted", Toast.LENGTH_SHORT).show();
        workoutList.remove(position);
        resetWorkoutListData();
    }

    private void completeWorkout(final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(ViewWorkoutActivity.this);
        View promptView = layoutInflater.inflate(R.layout.complete_exercise_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewWorkoutActivity.this);
        alertDialogBuilder.setView(promptView);


        final Workout workout = workoutOutputs.get(position);

        final EditText completeExerciseWeightEditText = (EditText) promptView.findViewById(R.id.completeExerciseWeightEditText);
        final EditText completeExerciseSetsEditText = (EditText) promptView.findViewById(R.id.completeExerciseSetsEditText);
        final EditText completeExerciseRepsEditText = (EditText) promptView.findViewById(R.id.completeExerciseRepsEditText);

        completeExerciseWeightEditText.setText(String.valueOf(workout.getWeight()));
        completeExerciseSetsEditText.setText(String.valueOf(workout.getSets()));
        completeExerciseRepsEditText.setText(String.valueOf(workout.getReps()));

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(completeExerciseRepsEditText.getText().toString().equals("") || completeExerciseSetsEditText.toString().equals("") || completeExerciseWeightEditText.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "You missed a value dummy.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        workout.setPercentMax(false);
                        workout.setWeight(Integer.parseInt(completeExerciseWeightEditText.getText().toString()));
                        workout.setSets(Integer.parseInt(completeExerciseSetsEditText.getText().toString()));
                        workout.setReps(Integer.parseInt(completeExerciseRepsEditText.getText().toString()));

                        workoutList.set(position, new Workout(workout.getName(), workout.getWeight(), workout.getReps(), workout.getSets(), false, true));
                        Toast.makeText(getApplicationContext(), workout.getName() + " completed", Toast.LENGTH_SHORT).show();
                        resetWorkoutListData();

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

    public List<Integer> findCompletedWorkouts() {
        List<Integer> completedPositions = new ArrayList<>();
        for(Workout workout : workoutList) {
            if(workout.isComplete())
                completedPositions.add(workoutList.indexOf(workout));
        }

        return completedPositions;
    }

}


