package com.libnanman.liftingplanner;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

//import com.libnanman.liftingplanner.LiftFragment.OnListFragmentInteractionListener;
//import com.libnanman.liftingplanner.dummy.DummyContent.Lift;

import java.text.SimpleDateFormat;
import java.util.List;

///**
// * {@link RecyclerView.Adapter} that can display a {@link Lift} and makes a call to the
// * specified {@link OnListFragmentInteractionListener}.
// * TODO: Replace the implementation with code for your data type.
// */
public class WorkoutRecyclerViewAdapter extends RecyclerView.Adapter<WorkoutRecyclerViewAdapter.WorkoutViewHolder> {

    private List<Workout> workoutList;
    public int position;

    public class WorkoutViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView workoutName, workoutReps, workoutWeight, workoutSets;

        public WorkoutViewHolder(View view) {
            super(view);
            workoutName = (TextView) view.findViewById(R.id.workoutName);
            workoutReps = (TextView) view.findViewById(R.id.workoutReps);
            workoutWeight = (TextView) view.findViewById(R.id.workoutWeight);
            workoutSets = (TextView) view.findViewById(R.id.workoutSets);
            view.setOnCreateContextMenuListener(this);
        }

        //MENU
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.deleteExercise, Menu.NONE, "Delete Exercise");
            menu.add(Menu.NONE, R.id.completedExercise, Menu.NONE, "Complete Exercise");
        }
    }

    public WorkoutRecyclerViewAdapter(List<Workout> workoutList) { this.workoutList = workoutList; }

    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_list_row, parent, false);

        return new WorkoutViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final WorkoutViewHolder holder, int position) {
        Workout workout = workoutList.get(position);
        holder.workoutName.setText(workout.getName());
        holder.workoutWeight.setText(Integer.toString(workout.getWeight()));
        holder.workoutReps.setText(Integer.toString(workout.getReps()));
        holder.workoutSets.setText(Integer.toString(workout.getSets()));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });

        if(workout.isComplete()) {
            holder.itemView.setBackgroundColor(Color.parseColor("#32CD32"));
        }

    }

    @Override
    public int getItemCount() { return workoutList.size(); }

    public int getPosition() { return position; }

    public void setPosition(int position) { this.position = position; }
}
