package com.libnanman.liftingplanner;

import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.libnanman.liftingplanner.Utils.getRealPathFromURI;

public class MainActivity extends AppCompatActivity {
    private RecyclerView liftListRecyclerView;
    //private RecyclerView.Adapter liftListAdapter;
    private LiftRecyclerViewAdapter liftListAdapter;
    private RecyclerView.LayoutManager liftListLayoutManager;
    private List<Lift> liftList = new ArrayList<>();
    private Lift currentLift;
    private HashMap<Lift, String> liftIdHash = new HashMap<>();
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
//    private int newMaxValue;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yyyy");
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseRef = database.getReference();
    private DatabaseReference liftsRef;// = database.getReference("lifts");
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference maxesRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        setLiftList();

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = mUser.getUid();

        liftsRef = database.getReference("lifts/" + uid);
        maxesRef = storage.getReference("maxes/" + uid);

//        Query liftsQuery = liftsRef.orderByChild("uid").startAt(uid).endAt(uid);

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
                StorageReference maxRef = maxesRef.child(lift.getName());
                Toast.makeText(getApplicationContext(), lift.getName() + " video!", Toast.LENGTH_SHORT).show();
                maxRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent videoIntent5 = new Intent(Intent.ACTION_VIEW);
                        videoIntent5.setDataAndType(uri, "video/*");
                        Intent chooser = Intent.createChooser(videoIntent5, getResources().getString(R.string.choose_video_app));
                        startActivity(chooser);
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

                //do nothing
                //getSupportActionBar().hide();
            }
        }));

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Lift newLift = dataSnapshot.getValue(Lift.class);
                liftIdHash.put(newLift, dataSnapshot.getKey());
                liftList.add(newLift);
                liftListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Lift changedLift = dataSnapshot.getValue(Lift.class);
                liftIdHash.put(changedLift, dataSnapshot.getKey());
                liftList.add(changedLift);
                liftListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                String liftKey = dataSnapshot.getKey();
//                liftsRef.child(liftKey).removeValue();
                Lift removedLift = dataSnapshot.getValue(Lift.class);
                liftIdHash.remove(removedLift);
                removeFromLiftList(removedLift);
                liftListAdapter.notifyDataSetChanged();
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
        liftsRef.addChildEventListener(childEventListener);
//        liftsQuery.addChildEventListener(childEventListener);

        registerForContextMenu(liftListRecyclerView);

        date = simpleDateFormat.format(new Date());

        //prepareTestLifts();

    }

    public void onAddLift(View view) {
        if(liftName.getText().toString().equals("") || liftMax.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Lift not saved. There was an empty field you dummy.", Toast.LENGTH_LONG).show();
            return;
        }

//        String filename = "liftData";
//        File file = new File(getApplicationContext().getFilesDir(), filename);
//        FileOutputStream outputStream;

        Lift lift = new Lift(liftName.getText().toString(), Integer.parseInt(liftMax.getText().toString()), new Date(), uid);
//        String fileContents = liftName.getText().toString() + "!@!" + liftMax.getText().toString() + "!@!" + new Date() +"\n";
//        liftList.add(lift);

//        try {
//            if(file.exists())
//                outputStream = new FileOutputStream(getApplicationContext().getFilesDir().toString() + "/" + filename, true);
//            else
//                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//
//            outputStream.write(fileContents.getBytes());
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        liftName.setText("");
        liftMax.setText("");

//        databaseRef.child("lifts").child(lift.getName()).setValue(lift);
//        liftsRef.push(lift);
        liftsRef.push().setValue(lift);

        liftListAdapter.notifyDataSetChanged();
    }

    public void onViewTodayWorkout(View view) {
        date = simpleDateFormat.format(new Date());
        onViewWorkout(view);
    }


    public void onViewWorkout(View view) {
        Intent intent = new Intent(this, ViewWorkoutActivity.class);
        intent.putExtra("date", date);
        intent.putExtra("uid", uid);
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
                onTakeVideo(position);
                break;
            case R.id.selectVideo:
                try {
                    onSelectVideo(position);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "file not found", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;
            case R.id.deleteLift:
                removeLift(position);
                break;
        }
        return super.onContextItemSelected(item);
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
                            int newMaxValue = Integer.parseInt(editText.getText().toString());
                            setNewMax(position, newMaxValue);
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

    private void onTakeVideo(int position){
        Lift lift = liftList.get(position);
        currentLift = lift;
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, 1);
        }
    }

    private void onSelectVideo(int position) throws FileNotFoundException {
        Lift lift = liftList.get(position);
        currentLift = lift;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, 0);
        }
    }

    private void saveVideoToDB(File file) throws FileNotFoundException {
        StorageReference maxRef = maxesRef.child(currentLift.getName());
        InputStream stream = new FileInputStream(file);
        UploadTask uploadTask = maxRef.putStream(stream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "upload completed", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Toast.makeText(getApplicationContext(), "uploading...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if(requestCode == 0) {
            // Make sure the request was successful
            if(resultCode == RESULT_OK) {
                File file = new File(getRealPathFromURI(data.getData(), getApplicationContext()));
                try{
                    saveVideoToDB(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == 1){
            if(resultCode == RESULT_OK) {
                File file = new File(getRealPathFromURI(data.getData(), getApplicationContext()));
                try{
                    saveVideoToDB(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
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


    private void setNewMax(int position, int newMaxValue) {
        Lift lift = liftList.get(position);
        String liftKey = liftIdHash.get(lift);
        Toast.makeText(getApplicationContext(), lift.getName() + " max changed", Toast.LENGTH_SHORT).show();
        liftList.remove(position);
        Map<String, Object> newMaxUpdates = new HashMap<>();
        newMaxUpdates.put("date", new Date());
        newMaxUpdates.put("max", newMaxValue);
        liftsRef.child(liftKey).updateChildren(newMaxUpdates);

    }

    private void removeLift(int position) {
        Lift lift = liftList.get(position);
        Toast.makeText(getApplicationContext(), lift.getName() + " deleted", Toast.LENGTH_SHORT).show();
        liftsRef.child(liftIdHash.get(lift)).removeValue();
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

}
