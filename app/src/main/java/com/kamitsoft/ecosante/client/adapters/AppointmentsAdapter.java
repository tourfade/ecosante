package com.kamitsoft.ecosante.client.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.AppointmentRequestStatus;
import com.kamitsoft.ecosante.constant.Gender;
import com.kamitsoft.ecosante.constant.MaritalStatus;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.AppointmentInfo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;



public class AppointmentsAdapter extends AbstractAdapter<AppointmentsAdapter.MyHolder>  {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    private Context context;
    private List<AppointmentInfo> mdata;
    private Calendar calendar = Calendar.getInstance();
    private boolean patientPointOfView;

    // create constructor to innitilize context and data sent from MainActivity
    public AppointmentsAdapter(Context context,boolean isppov) {
        this.context = context;
        mdata = new ArrayList<>();
        patientPointOfView = isppov;
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
            v = LayoutInflater.from(this.context).inflate(R.layout.appointement_item_view, parent, false);
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

            AppointmentInfo current = mdata.get(position);
            AppointmentRequestStatus status = AppointmentRequestStatus.ofStatus(current.getStatus());

            if(patientPointOfView) {
                myHolder.header.setText(UserType.isPhysist(current.getUserType())
                                            && current.getSpeciality()!=null?
                                            current.getSpeciality()
                                            :context.getString(UserType.typeOf(current.getUserType()).title));

                myHolder.objectTitle.setText((status == AppointmentRequestStatus.PENDING)?"Demande de":current.getPatientObject());
            }else{
                myHolder.objectTitle.setText((status == AppointmentRequestStatus.PENDING)?"Demande de":current.getUserObject());
            }

            myHolder.object.setText(Utils.niceFormat(current.getDetails()));
            myHolder.place.setText(Utils.niceFormat(current.getPlace()));
            myHolder.header.setBackgroundColor(context.getResources().getColor(status.color, context.getTheme()));
            if(status == AppointmentRequestStatus.ACCEPTED) {
                calendar.setTimeInMillis(current.getDate().getTime());
                myHolder.date.setText(Utils.formatDateWithDayOfWeek(context, calendar));
                myHolder.time.setText(Utils.formatTime(calendar));
                myHolder.dateTitle.setText(R.string.appointment_date);
                myHolder.apptContainer.setVisibility(View.VISIBLE);
            }else{
                calendar.setTimeInMillis(current.getRequestLastestDate().getTime());
                myHolder.date.setText(Utils.formatDateWithDayOfWeek(context, calendar));
                myHolder.dateTitle.setText(R.string.select_latest_date);
                myHolder.apptContainer.setVisibility(View.GONE);
            }

        }
        setAnimation(myHolder.itemView,position);

    }

    public @Nullable AppointmentInfo getItem(int position){
        return  mdata !=null && mdata.size()>0?mdata.get(position):null;
    }
    public void syncData(List<AppointmentInfo> data) {
        mdata = data;
        notifyDataSetChanged();
    }


    int lastpos = -1;
    private void setAnimation(View itemView, int position) {
        if(position > lastpos){

            Animation anim = AnimationUtils.loadAnimation(context,android.R.anim.slide_in_left);
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



    public  class MyHolder extends AbstractAdapter.EdtiableHolder {

        TextView object, objectTitle, header;
        TextView place;
        TextView date, dateTitle;
        TextView time;
        View apptContainer;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
             super(itemView);
             header = itemView.findViewById(R.id.header);
             objectTitle = itemView.findViewById(R.id.object_title);
             object = itemView.findViewById(R.id.object);
             place = itemView.findViewById(R.id.place);
             date = itemView.findViewById(R.id.date);
             time = itemView.findViewById(R.id.time);
             dateTitle = itemView.findViewById(R.id.date_title);
             apptContainer = itemView.findViewById(R.id.apptContainer);
        }


    }




}
