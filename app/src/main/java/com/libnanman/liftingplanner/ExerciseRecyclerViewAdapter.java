package com.libnanman.liftingplanner;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import com.libnanman.liftingplanner.LiftFragment.OnListFragmentInteractionListener;
//import com.libnanman.liftingplanner.dummy.DummyContent.Lift;

import java.util.List;

///**
// * {@link RecyclerView.Adapter} that can display a {@link Lift} and makes a call to the
// * specified {@link OnListFragmentInteractionListener}.
// * TODO: Replace the implementation with code for your data type.
// */
public class ExerciseRecyclerViewAdapter extends RecyclerView.Adapter<ExerciseRecyclerViewAdapter.ExerciseViewHolder> {

    private List<Exercise> exerciseList;
    public int position;

    public class ExerciseViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView workoutName, workoutReps, workoutWeight, workoutSets;

        public ExerciseViewHolder(View view) {
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
            menu.add(Menu.NONE, R.id.takeVideo, Menu.NONE, "Take Video");
            menu.add(Menu.NONE, R.id.selectVideo, Menu.NONE, "Select Video");
        }
    }

    public ExerciseRecyclerViewAdapter(List<Exercise> exerciseList) { this.exerciseList = exerciseList; }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_list_row, parent, false);

        return new ExerciseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ExerciseViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        holder.workoutName.setText(exercise.getName());
        holder.workoutWeight.setText(Integer.toString(exercise.getWeight()));
        holder.workoutReps.setText(Integer.toString(exercise.getReps()));
        holder.workoutSets.setText(Integer.toString(exercise.getSets()));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });

        if(exercise.isComplete()) {
            holder.itemView.setBackgroundColor(Color.parseColor("#32CD32"));
        }

    }

    @Override
    public int getItemCount() { return exerciseList.size(); }

    public int getPosition() { return position; }

    public void setPosition(int position) { this.position = position; }
}
