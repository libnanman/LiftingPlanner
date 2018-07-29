package com.libnanman.liftingplanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewWorkoutActivity extends AppCompatActivity {

    private RecyclerView exerciseRecyclerView;
    private ExerciseRecyclerViewAdapter exerciseAdapter;
    private RecyclerView.LayoutManager exerciseLayoutManager;
    private Toolbar viewWorkoutActionBar;
    private TextView viewWorkoutDate;
    private CalendarView calendarView;
    private MenuItem calendarButton;
    private MenuItem deleteWorkout;
    private String date;
    private List<Exercise> exerciseList = new ArrayList<>();
    private HashMap<Exercise, String> exerciseIdHash = new HashMap<>();
    private List<Exercise> exerciseOutputs = new ArrayList<>();
    private List<Lift> liftList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseRef = database.getReference();
    private DatabaseReference liftsRef;// = database.getReference("lifts");
    private DatabaseReference exercisesRef;// = database.getReference("exercises");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yyyy");
    private String uid;

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
        uid = getIntent().getExtras().getString("uid");

        liftsRef = database.getReference("lifts/" + uid);
        exercisesRef = database.getReference("exercises/" + uid);

        Query liftsQuery = liftsRef.orderByChild("uid").startAt(uid).endAt(uid);
        Query exercisesQuery = exercisesRef.orderByChild("date").startAt(date).endAt(date);
//        Query finalExercisesQuery = exercisesQuery.orderByChild("date").startAt(date).endAt(date);

        viewWorkoutDate.setText(date);

        setSupportActionBar(viewWorkoutActionBar);

        exerciseRecyclerView = (RecyclerView) findViewById(R.id.exerciseListRecyclerView);

        exerciseAdapter = new ExerciseRecyclerViewAdapter(exerciseList);
        exerciseLayoutManager = new LinearLayoutManager(getApplicationContext());
        exerciseRecyclerView.setLayoutManager(exerciseLayoutManager);
        exerciseRecyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        exerciseRecyclerView.addItemDecoration(dividerItemDecoration);

        exerciseRecyclerView.setAdapter(exerciseAdapter);

        exerciseRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), exerciseRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Exercise exercise = exerciseList.get(position);
                Toast.makeText(getApplicationContext(), exercise.getName() + " video!", Toast.LENGTH_SHORT).show();

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

        ChildEventListener liftsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Lift newLift = dataSnapshot.getValue(Lift.class);
                liftList.add(newLift);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Lift changedLift = dataSnapshot.getValue(Lift.class);
                liftList.add(changedLift);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                String liftKey = dataSnapshot.getKey();
//                liftsRef.child(liftKey).removeValue();
                Lift removedLift = dataSnapshot.getValue(Lift.class);
                removeFromLiftList(removedLift);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //nothing
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //error
            }
        };
//        liftsRef.addChildEventListener(childEventListener);
        liftsQuery.addChildEventListener(liftsChildEventListener);

        ChildEventListener exercisesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Exercise newExercise = dataSnapshot.getValue(Exercise.class);
                exerciseList.add(newExercise);
                exerciseIdHash.put(newExercise, dataSnapshot.getKey());
                exerciseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Exercise changedExercise = dataSnapshot.getValue(Exercise.class);
                exerciseList.add(changedExercise);
                exerciseIdHash.put(changedExercise, dataSnapshot.getKey());
                exerciseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                String liftKey = dataSnapshot.getKey();
//                liftsRef.child(liftKey).removeValue();
                Exercise removedExercise = dataSnapshot.getValue(Exercise.class);
                exerciseIdHash.remove(removedExercise);
                removeFromExerciseList(removedExercise);
                exerciseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //nothing
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //error
            }
        };
//        liftsRef.addChildEventListener(childEventListener);
        exercisesQuery.addChildEventListener(exercisesChildEventListener);

        registerForContextMenu(exerciseRecyclerView);

        //setLiftList();
//        setExerciseList();

        //prepareTest();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = exerciseAdapter.getPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (item.getItemId()) {
            case R.id.deleteExercise:
                    removeExercise(position);
                break;
            case R.id.completedExercise:
                    completeExercise(position);
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
//                setExerciseList();
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

    public void onAddExerciseClick(View view){
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


                                Exercise exercise = new Exercise(newExerciseNameEditText.getText().toString(),
                                        Integer.parseInt(newExerciseWeightEditText.getText().toString()),
                                        Integer.parseInt(newExerciseRepsEditText.getText().toString()),
                                        Integer.parseInt(newExerciseSetsEditText.getText().toString()),
                                        newExerciseWeightSwitch.isChecked(), false, date, uid);
//                                onAddExercise(exercise);
                                onAddExercise(exercise);

                            }
                            else
                                Toast.makeText(getApplicationContext(), "New exercise not saved. You haven't saved a working max for that lift, dummy", Toast.LENGTH_LONG).show();
                            //exerciseList.add(workout);
                            //exerciseAdapter.notifyDataSetChanged();
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


    public void onAddExercise(Exercise exercise) {
        if(exercise.isPercentMax()){
            double max = findLiftMaxByName(exercise.getName());
            int weight = (int) max*exercise.getWeight()/100;
            exercise.setWeight(weight);
        }

        exercisesRef.push().setValue(exercise);
    }


    private double findLiftMaxByName(String liftName) {
        for(Lift lift : liftList) {
            if(lift.getName().equals(liftName))
                return lift.getMax();
        }
        return -69;
    }

    private void removeExercise(int position) {
        Exercise exercise = exerciseList.get(position);
        Toast.makeText(getApplicationContext(), exercise.getName() + " deleted", Toast.LENGTH_SHORT).show();
        exerciseList.remove(position);
        exercisesRef.child(exerciseIdHash.get(exercise)).removeValue();
//        resetExerciseListData();
    }

    private void completeExercise(final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(ViewWorkoutActivity.this);
        View promptView = layoutInflater.inflate(R.layout.complete_exercise_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewWorkoutActivity.this);
        alertDialogBuilder.setView(promptView);


        final Exercise exercise = exerciseList.get(position);

        final EditText completeExerciseWeightEditText = (EditText) promptView.findViewById(R.id.completeExerciseWeightEditText);
        final EditText completeExerciseSetsEditText = (EditText) promptView.findViewById(R.id.completeExerciseSetsEditText);
        final EditText completeExerciseRepsEditText = (EditText) promptView.findViewById(R.id.completeExerciseRepsEditText);

        completeExerciseWeightEditText.setText(String.valueOf(exercise.getWeight()));
        completeExerciseSetsEditText.setText(String.valueOf(exercise.getSets()));
        completeExerciseRepsEditText.setText(String.valueOf(exercise.getReps()));

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(completeExerciseRepsEditText.getText().toString().equals("") || completeExerciseSetsEditText.toString().equals("") || completeExerciseWeightEditText.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "You missed a value dummy.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //exercise.setPercentMax(false);
                        exercise.setWeight(Integer.parseInt(completeExerciseWeightEditText.getText().toString()));
                        exercise.setSets(Integer.parseInt(completeExerciseSetsEditText.getText().toString()));
                        exercise.setReps(Integer.parseInt(completeExerciseRepsEditText.getText().toString()));

                        setExerciseComplete(position, exercise);
//                        exerciseList.set(position, new Exercise(exercise.getName(), exercise.getWeight(), exercise.getReps(), exercise.getSets(), false, true, date, uid));
//                        Toast.makeText(getApplicationContext(), exercise.getName() + " completed", Toast.LENGTH_SHORT).show();
//                        resetExerciseListData();

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

    private void setExerciseComplete(int position, Exercise changedExercise){
        Exercise exercise = exerciseList.get(position);
        String exerciseKey = exerciseIdHash.get(exercise);
        Toast.makeText(getApplicationContext(), exercise.getName() + " completed", Toast.LENGTH_SHORT).show();
        exerciseList.remove(position);
        Map<String, Object> completedExerciseUpdates = new HashMap<>();
        completedExerciseUpdates.put("date", simpleDateFormat.format(new Date()));
        completedExerciseUpdates.put("weight", changedExercise.getWeight());
        completedExerciseUpdates.put("sets", changedExercise.getSets());
        completedExerciseUpdates.put("reps", changedExercise.getReps());
        completedExerciseUpdates.put("complete", true);
        exercisesRef.child(exerciseKey).updateChildren(completedExerciseUpdates);
    }

    private void removeFromLiftList(Lift lift){
        Lift removingLift;
        Lift[] liftArray = new Lift[liftList.size()];
        liftArray = liftList.toArray(liftArray);

        for (Lift listedLift : liftArray) {
            if(listedLift.getName().equals(lift.getName()) && listedLift.getMax() == lift.getMax() && listedLift.getDate().equals(lift.getDate())) {
                removingLift = listedLift;
                liftList.remove(removingLift);
            }
        }

    }


    private void removeFromExerciseList(Exercise exercise){
        Exercise removingExercise;
        Exercise[] exerciseArray = new Exercise[exerciseList.size()];
        exerciseArray = exerciseList.toArray(exerciseArray);

        for (Exercise listedExercise : exerciseArray) {
            if(listedExercise.getName().equals(exercise.getName()) && listedExercise.getReps() == exercise.getReps() && listedExercise.getDate().equals(exercise.getDate()) && exercise.getWeight() == listedExercise.getWeight() && exercise.getSets() == listedExercise.getSets()) {
                removingExercise = listedExercise;
                liftList.remove(removingExercise);
            }
        }

    }

}


