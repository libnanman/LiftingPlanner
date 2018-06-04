package com.libnanman.liftingplanner;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
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
public class LiftRecyclerViewAdapter extends RecyclerView.Adapter<LiftRecyclerViewAdapter.LiftViewHolder> {

    private List<Lift> liftList;
    private int position;
    //SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd, yyyy");

    public class LiftViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView liftNameRecycler, liftMaxRecycler, liftDateRecycler;

        public LiftViewHolder(View view) {
            super(view);
            liftNameRecycler = (TextView) view.findViewById(R.id.liftName);
            liftMaxRecycler = (TextView) view.findViewById(R.id.liftMax);
            liftDateRecycler = (TextView) view.findViewById(R.id.liftDate);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.newMax, Menu.NONE, R.string.new_max);
            menu.add(Menu.NONE, R.id.deleteLift, Menu.NONE, R.string.delete_lift);
            menu.add(Menu.NONE, R.id.takeVideo, Menu.NONE, R.string.take_video);
            menu.add(Menu.NONE, R.id.selectVideo, Menu.NONE, R.string.select_video);
        }
    }

    public LiftRecyclerViewAdapter(List<Lift> liftList) {
        this.liftList = liftList;
    }

    @Override
    public LiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lift_list_row, parent, false);

        return new LiftViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LiftViewHolder holder, int position) {
        Lift lift = liftList.get(position);
        holder.liftNameRecycler.setText(lift.getName());
        holder.liftMaxRecycler.setText(Integer.toString(lift.getMax()));
        holder.liftDateRecycler.setText(lift.getDate().toString());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return liftList.size();
    }

    public int getPosition() { return position; }

    public void setPosition(int position) { this.position = position; }

}
