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
import android.widget.ImageView;
import android.widget.TextView;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.LabType;
import com.kamitsoft.ecosante.model.DistrictInfo;
import com.kamitsoft.ecosante.model.LabInfo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by fade; on 28/03/2019.
 */

public class DistrictAdapter extends RecyclerView.Adapter<DistrictAdapter.MyHolder>  {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    private final NumberFormat decimalFormatf;
    private final EcoSanteApp app;

    private Context context;
    private List<DistrictInfo> mdata;
    static ItemClickListener myClickListener;

    // create constructor to innitilize context and data sent from MainActivity
    public DistrictAdapter(Context context) {
        this.context = context;
        mdata = new ArrayList<>();
        decimalFormatf = DecimalFormat.getNumberInstance();
        app = (EcoSanteApp)context.getApplicationContext();

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return mdata.size();

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
            v = LayoutInflater.from(this.context).inflate(R.layout.lab_item_view, parent, false);
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

            DistrictInfo current = mdata.get(position);
            myHolder.labName.setText(Utils.niceFormat(current.getName()));
            myHolder.labValue.setText(String.valueOf(decimalFormatf.format(current.getPopulation())));
            myHolder.date.setText(Utils.format(current.getCreatedAt()));

            myHolder.sep.setBackgroundColor(current.isDeleted()?context.getColor(R.color.colorAccent): Color.RED);

        }


    }

    public @Nullable DistrictInfo getItem(int position){
        return  mdata !=null && mdata.size() > 0? mdata.get(position):null;
    }
    public void syncData(DistrictInfo data) {
        int idx = mdata.indexOf(data);
        if (idx >= 0) {
            mdata.set(idx, data);
            notifyItemChanged(idx);
        } else {
            mdata.add(0,data);
            notifyItemInserted(0);
        }

    }
    public void syncData(List<DistrictInfo> data) {
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

    public List<DistrictInfo> getData() {
        return mdata;
    }


    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView labName, labValue;
        TextView status, date;
        ImageView icon;
        View sep;
        ImageButton delete;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
             super(itemView);
             sep = itemView.findViewById(R.id.sep);
             labName = itemView.findViewById(R.id.labName);
             labValue = itemView.findViewById(R.id.labValue);
             status = itemView.findViewById(R.id.status);
             date = itemView.findViewById(R.id.date);
             icon = itemView.findViewById(R.id.icon);
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