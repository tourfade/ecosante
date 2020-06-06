package com.kamitsoft.ecosante.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.Gender;
import com.kamitsoft.ecosante.constant.StatusConstant;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.EncounterHeaderInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.PatientInfo;

import org.w3c.dom.ls.LSException;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by fadel on 01/09/2019.
 */

public class UserEncountersAdapter extends AbstractAdapter<UserEncountersAdapter.MyHolder>  {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    private final EcoSanteApp app;

    private Context context;
    private List<EncounterHeaderInfo> mdata;

    static ItemClickListener myClickListener;

    // create constructor to innitilize context and data sent from MainActivity
    public UserEncountersAdapter(Context context) {
        this.context = context;
        mdata = new ArrayList<>();
        app = (EcoSanteApp)context.getApplicationContext();
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
            v = LayoutInflater.from(this.context).inflate(R.layout.user_encounter_item_view, parent, false);
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

        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_NORMAL){

            // Get current position of item in recyclerview to bind data and assign values from list

            EncounterHeaderInfo current = mdata.get(position);
            myHolder.title.setText("Visite du "+Utils.format(current.getCreatedAt()));
            myHolder.patient.setText(Utils.formatName(context, Utils.niceFormat(current.getFirstName())+" "+Utils.niceFormat(current.getMiddleName()),current.getLastName(),-1));
            myHolder.dobpob.setText(Utils.formatAge(current.getDob())+" né(é) à "+Utils.niceFormat(current.getPob()));
            myHolder.mobile.setText(current.getMobile());
            myHolder.status.setBackground(StatusConstant.ofStatus(current.currentStatus().status).drawable);

            if(UserType.isNurse(app.getCurrentUser().getUserType())){
                myHolder.monitorTitle.setText("Supervisé par:");
                myHolder.monitor.setText(Utils.niceFormat(current.getSupervisor().supFullName));
            }
            if(UserType.isPhysist(app.getCurrentUser().getUserType())){
                myHolder.monitorTitle.setText("Vu  par:");
                myHolder.monitor.setText(Utils.niceFormat(current.getMonitor().monitorFullName));
            }


            Utils.load(context, BuildConfig.AVATAR_BUCKET,
                    current.getAvatar(),
                    myHolder.avatar,
                    R.mipmap.visit_icon_round,R.mipmap.visit_icon_round);


        }else {

        }
        setAnimation(myHolder.itemView,position);

    }


    public void syncData(List<EncounterHeaderInfo> data) {
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

    public void setItemClickListener(ItemClickListener listener) {
        myClickListener = listener;
    }



    public EncounterHeaderInfo getItem(int itemPosition) {
        return mdata.get(itemPosition);
    }

    public  class MyHolder extends AbstractAdapter.EdtiableHolder  implements View.OnClickListener {

        TextView patient,dobpob,mobile,title, monitorTitle, monitor;
        ImageView avatar;
        View status;
        // create constructor to get widget reference
        public MyHolder(View itemView) {
             super(itemView);
             status = itemView.findViewById(R.id.status);
             title =  itemView.findViewById(R.id.title);
             avatar = itemView.findViewById(R.id.patientImg);
             patient =itemView.findViewById(R.id.patient);
             dobpob = itemView.findViewById(R.id.dobpob);
             mobile = itemView.findViewById(R.id.mobile);
             monitor =itemView.findViewById(R.id.monitor);
             monitorTitle = itemView.findViewById(R.id.monitorTitle);

            itemView.setOnClickListener(this);

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





}