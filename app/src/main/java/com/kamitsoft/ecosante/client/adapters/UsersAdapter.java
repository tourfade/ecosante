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
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.TitleType;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.UserInfo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by hassa on 01/06/2017.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyHolder>  {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    private DateFormat df = DateFormat.getDateInstance();
    private Context context;
    private EcoSanteApp app;
    private List<UserInfo> mdata;

    static ItemClickListener myClickListener;

    // create constructor to innitilize context and data sent from MainActivity
    public UsersAdapter(Context context) {
        this.context = context;
        app = (EcoSanteApp)context.getApplicationContext();
        mdata = new ArrayList<>();

    }

    // return total item from List
    @Override
    public int getItemCount() {
        if (mdata == null){
            return 1;
        }else {
            return mdata.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mdata == null){
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
            v = LayoutInflater.from(this.context).inflate(R.layout.user_item, parent, false);
            vh = new MyHolder(v);
        }else {
            v = LayoutInflater.from(this.context).inflate(R.layout.layout_empty, parent, false);
            vh = new MyHolder(v);
        }

        return vh;
    }


    public UserInfo getItem(int position) {
        if(position >= mdata.size() || position < 0){
            return null;
        }
        return mdata.get(position);
    }

    // Bind data
    @Override
    public void onBindViewHolder(MyHolder myHolder, int position) {

        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_NORMAL){

            // Get current position of item in recyclerview to bind data and assign values from list

            UserInfo current = mdata.get(position);
            myHolder.title.setText(TitleType.typeOf(current.getTitle()).title);
            myHolder.user.setText(Utils.formatName(context, current.getFirstName()+" "+Utils.niceFormat(current.getMiddleName()),current.getLastName(),-1));
            if(UserType.isPhysist(current.getUserType())) {
                myHolder.specialityOrSupervisorText.setText(R.string.speciality);
                myHolder.specialityOrSupervisor.setText(Utils.niceFormat(current.getSpeciality()));
            }else if(UserType.isNurse(current.getUserType())) {
                myHolder.specialityOrSupervisorText.setText(R.string.supervisor);
                myHolder.specialityOrSupervisor.setText(current.getSupervisor() != null? current.getSupervisor().supFullName:"No supervisor");
            }
            myHolder.mobile.setText(Utils.niceFormat(current.getMobilePhone()));
            myHolder.fix.setText(Utils.niceFormat(current.getFixPhone()));
            myHolder.email.setText(Utils.niceFormat(current.getEmail()));
            myHolder.type.setText(UserType.typeOf(current.getUserType()).title);
            Utils.load(context,current.getAvatar(), myHolder.avatar,R.drawable.user_avatar,R.drawable.physist);


        }else {
        }
        setAnimation(myHolder.itemView,position);

    }


    public void syncData(List<UserInfo> data) {
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


    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title,user,specialityOrSupervisor,mobile,fix, email, type, specialityOrSupervisorText;
        ImageView avatar;
        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.userPicture);
            title =   itemView.findViewById(R.id.title);
            user = itemView.findViewById(R.id.titlename);
            specialityOrSupervisorText = itemView.findViewById(R.id.specialityOrSupervisorText);
            specialityOrSupervisor = itemView.findViewById(R.id.specialityOrSupervisor);
            mobile = itemView.findViewById(R.id.mobile);
            fix = itemView.findViewById(R.id.fixphone);
            email = itemView.findViewById(R.id.email);
            type = itemView.findViewById(R.id.userType);

             itemView.setOnClickListener(this);

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





}