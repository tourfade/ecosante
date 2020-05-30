package com.kamitsoft.ecosante.client.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.MedicationStatus;
import com.kamitsoft.ecosante.model.MedicationInfo;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by hassa on 01/06/2017.
 */

public class MedicationsAdapter extends RecyclerView.Adapter<MedicationsAdapter.MyHolder>  {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    private final NumberFormat decimalFormatf;
   // private final EcoSanteApp app;

    private Context context;
    private List<MedicationInfo> mdata;
    static ItemClickListener myClickListener;

    // create constructor to innitilize context and data sent from MainActivity
    public MedicationsAdapter(Context context) {
        this.context = context;
        mdata = new ArrayList<>();
        decimalFormatf = DecimalFormat.getNumberInstance();

    }

    // return total item from List
    @Override
    public int getItemCount() {
        if (mdata == null || mdata.size() == 0){
            return 1;
        }else {
            return mdata.size();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mdata == null || mdata.size() == 0){
            return VIEW_TYPE_EMPTY;
        }else {
            return VIEW_TYPE_NORMAL;
        }
    }



    // Inflate the layout when viewholder created
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        MyHolder vh;

        if (viewType == VIEW_TYPE_NORMAL){
            v = LayoutInflater.from(this.context).inflate(R.layout.medication_item, parent, false);
            vh = new MyHolder(v);
        }else {
            v = LayoutInflater.from(this.context).inflate(R.layout.layout_empty, parent, false);
            vh = new MyHolder(v);
        }

        return vh;
    }

    // Bind data
    @Override
    public void onBindViewHolder(MyHolder myHolder, int position) {

       if (getItemViewType(position) == VIEW_TYPE_NORMAL){

            MedicationInfo current = mdata.get(position);
            myHolder.drugName.setText(current.getDrugName() );
            myHolder.drugRenew.setText(String.valueOf(current.getRenewal()));
            myHolder.startingDate.setText(Utils.format(current.getStartingDate()));
            //setAnimation(myHolder.itemView,position);
            switch (MedicationStatus.ofStatus(current.getStatus())){
                case NEW:
                    myHolder.endingDateTitler.setText(R.string.new_med);
                    myHolder.endingDate.setText(R.string.prescribed);

                    myHolder.sep.setBackgroundColor(context.getColor(R.color.colorAccent));
                    break;
                case RUNNING:
                    myHolder.endingDateTitler.setText(R.string.running_since);

                    myHolder.endingDate.setText(Utils.format(current.getStartingDate()));
                    myHolder.sep.setBackgroundColor(Color.GREEN);
                    break;
                case TERMINATED:
                    myHolder.sep.setBackgroundColor(Color.RED);
                    myHolder.endingDateTitler.setText(R.string.terminate_at);
                    myHolder.endingDate.setText(Utils.format(current.getEndingDate()));
                    break;
                default:
                    myHolder.sep.setBackgroundColor(Color.LTGRAY);
                    myHolder.endingDateTitler.setText(R.string.terminate_at);
                    myHolder.endingDate.setText(Utils.format(current.getEndingDate()));
                    break;

            }

       }


    }

    public @Nullable MedicationInfo getItem(int position){
        return  mdata !=null && mdata.size() > 0? mdata.get(position):null;
    }
    public void syncData(MedicationInfo data) {
        int idx = mdata.indexOf(data);
        if (idx >= 0) {
            mdata.set(idx, data);
            notifyItemChanged(idx);
        } else {
            mdata.add(0,data);
            notifyItemInserted(0);
        }

    }
    public void syncData(List<MedicationInfo> data) {
        mdata = data;
        notifyDataSetChanged();
    }


    int lastpos = -1;
    private void setAnimation(View itemView, int position) {
        if(position > lastpos){

            Animation anim = AnimationUtils.loadAnimation(context,android.R.anim.fade_in);
            itemView.startAnimation(anim);
        }
        lastpos = position;
    }

    @Override
    public void onViewDetachedFromWindow(MyHolder holder) {
        holder.clearAnimation();
    }

    public List<MedicationInfo> getData() {
        return mdata;
    }


    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        View sep;
        TextView drugName;
        TextView drugRenew, endingDateTitler;
        TextView startingDate, endingDate;
        ImageButton delete;
        // create constructor to get widget reference
        public MyHolder(View itemView) {
             super(itemView);
             sep = itemView.findViewById(R.id.sep);
             drugName = itemView.findViewById(R.id.drug);
             drugRenew = itemView.findViewById(R.id.renewal);
             startingDate = itemView.findViewById(R.id.starting_date);
             endingDate =   itemView.findViewById(R.id.ending_date);
             endingDateTitler = itemView.findViewById(R.id.endingDateTitler);
             delete = itemView.findViewById(R.id.item_delete);
             if(delete != null) {
                 delete.setOnClickListener(this);
                 itemView.setOnClickListener(this);
                 itemView.setOnLongClickListener(v -> {
                     AnimatorSet anset = new AnimatorSet();
                     anset.play(ObjectAnimator
                             .ofInt(itemView.findViewById(R.id.insider), "scrollX", 0)
                             .setDuration(200))
                             .after(2000)
                             .after(ObjectAnimator
                                     .ofInt(itemView.findViewById(R.id.insider), "scrollX", 130)
                                     .setDuration(200));
                     anset.start();

                     return true;
                 });
             }
        }

        public void clearAnimation(){
            itemView.clearAnimation();
        }
        @Override
        public void onClick(View v) {
            if(myClickListener != null && getItemViewType() == VIEW_TYPE_NORMAL) {
                myClickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        myClickListener = itemClickListener;
    }




}