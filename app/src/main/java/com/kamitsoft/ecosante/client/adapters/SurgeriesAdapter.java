package com.kamitsoft.ecosante.client.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.model.json.ExtraData;

import java.sql.Timestamp;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by hassa on 01/06/2017.
 */

public class SurgeriesAdapter extends RecyclerView.Adapter<SurgeriesAdapter.MyHolder>  {

   private final EcoSanteApp app;

    private Context context;
    private List<ExtraData> mdata;
    static ItemClickListener myClickListener;

    // create constructor to innitilize context and data sent from MainActivity
    public SurgeriesAdapter(Context context, List<ExtraData> mdata) {
        this.context = context;
        this.mdata = mdata;
        app = (EcoSanteApp)context.getApplicationContext();

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return mdata.size();

    }





    // Inflate the layout when viewholder created
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        MyHolder vh;
        v = LayoutInflater.from(this.context).inflate(R.layout.summary_item_view, parent, false);
        vh = new MyHolder(v);


        return vh;
    }

    // Bind data
    @Override
    public void onBindViewHolder(MyHolder myHolder, int position) {

        ExtraData current = mdata.get(position);
        myHolder.actName.setText(Utils.niceFormat(current.name));
        myHolder.date.setText(Utils.format(new Timestamp(current.date)));

    }

    public  ExtraData getItem(int position){
        return  mdata !=null && mdata.size() > 0? mdata.get(position):null;
    }
    public void syncData(ExtraData data) {
        int idx = mdata.indexOf(data);
        if (idx >= 0) {
            mdata.set(idx, data);
            notifyItemChanged(idx);
        } else {
            mdata.add(0,data);
            notifyItemInserted(0);
        }

    }
    public void syncData(List<ExtraData> data) {
        mdata = data;
        notifyDataSetChanged();

    }




    @Override
    public void onViewDetachedFromWindow(MyHolder holder) {
        holder.clearAnimation();
    }

    public List<ExtraData> getData() {
        return mdata;
    }


    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView actName, date;
        ImageButton delete;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
             super(itemView);
             actName = itemView.findViewById(R.id.name);
             date = itemView.findViewById(R.id.date);
             delete = itemView.findViewById(R.id.item_delete);
             delete.setOnClickListener(this);
             itemView.setOnClickListener(this);
             itemView.setOnLongClickListener(v -> {
                AnimatorSet anset = new AnimatorSet();
                anset.play(ObjectAnimator
                        .ofInt( itemView.findViewById(R.id.insider), "scrollX", 0)
                        .setDuration(200))
                        .after(2000)
                        .after(ObjectAnimator
                                .ofInt( itemView.findViewById(R.id.insider), "scrollX", 130)
                                .setDuration(200));
                anset.start();

                return true;
             });
        }

        public void clearAnimation(){
            itemView.clearAnimation();
        }
        @Override
        public void onClick(View v) {
            if(myClickListener != null) {
                myClickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        myClickListener = itemClickListener;
    }



}