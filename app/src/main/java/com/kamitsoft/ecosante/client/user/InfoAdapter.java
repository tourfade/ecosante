package com.kamitsoft.ecosante.client.user;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.adapters.ItemClickListener;
import com.kamitsoft.ecosante.constant.Gender;
import com.kamitsoft.ecosante.constant.MaritalStatus;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.StatInfo;
import com.kamitsoft.ecosante.model.SummaryInfo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by hassa on 01/06/2017.
 */

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.MyHolder>  {


    private final EcoSanteApp app;

    private DateFormat df = DateFormat.getDateInstance();
    private Context context;
    private List<Object> mdata;
    static ItemClickListener myClickListener;
    private PatientInfo current;
    private PatientInfo pat;

    // create constructor to innitilize context and data sent from MainActivity
    public InfoAdapter(Context context) {
        this.context = context;
        mdata = new ArrayList<>();
        app = (EcoSanteApp)context.getApplicationContext();

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return mdata.size();

    }

    @Override
    public @LayoutRes int getItemViewType(int position) {
        if(position < mdata.size()) {
            Object o = mdata.get(position);
            Log.i("XXXXXX", ""+o);
            if (o instanceof PatientInfo) {
                return R.layout.patient_item_view;
            }
            if (o instanceof StatInfo) {
                return R.layout.patient_tracker_item_view;
            }
            if (o instanceof SummaryInfo) {
                return R.layout.summary_item;
            }
        }
        return  R.layout.layout_empty;
    }



    // Inflate the layout when viewholder created
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, @LayoutRes  int viewType) {
        View v  = LayoutInflater.from(this.context).inflate(viewType, parent, false);
        return  new MyHolder(v);


    }

    // Bind data
    @Override
    public void onBindViewHolder(MyHolder myHolder, int position) {
        Object data = mdata.get(position);
        switch (getItemViewType(position)){
           case R.layout.patient_item_view: bindPatientID(myHolder, (PatientInfo) data); break;
           case R.layout.summary_item: bindPatientResume(myHolder, (SummaryInfo) data); break;
           case R.layout.patient_tracker_item_view: bindStat(myHolder, (StatInfo) data); break;
       }

    }

    private void bindPatientID(MyHolder myHolder, PatientInfo pi) {
        current = pi;
        if(current == null){return;}
        myHolder.fname.setText(current.getFirstName() + " "+ Utils.niceFormat(current.getMiddleName()));
        myHolder.lname.setText(current.getLastName());
        myHolder.dob.setText(current.getDob()==null?"N/A":df.format(current.getDob()));
        myHolder.pob.setText(current.getPob());
        myHolder.sex.setText(Gender.sex(current.getSex()).title);
        myHolder.mobile.setText(current.getMobile());
        myHolder.status.setText(MaritalStatus.status(current.getMaritalStatus()).title);
        myHolder.occupation.setText(current.getOccupation());
        Utils.load(context,current.getAvatar(),myHolder.avatar,R.drawable.user_avatar,R.drawable.patient);

    }

    private void bindPatientResume(MyHolder myHolder, SummaryInfo si) {
        myHolder.container.removeAllViews();
        SummaryInfo sumary = si;
        if(sumary.isHta()) {
            addLabeledCheckbox(myHolder, R.string.hta, R.string.suffer_of_hta);
        }

        if(sumary.isAvc()) {
           addLabeledCheckbox(myHolder, R.string.avc, R.string.avc_victim);
        }
        if(sumary.isDiabete()) {
            addLabeledCheckbox(myHolder, R.string.diabetes, R.string.suffer_of_diabet);
        }
        if(sumary.isEpilepsy()) {
            addLabeledCheckbox(myHolder, R.string.epilepsy, R.string.suffer_of_epilepsia);
        }

        if(sumary.isAsthma()) {
            addLabeledCheckbox(myHolder, R.string.ashmatic, R.string.suffer_of_asthma);
        }


        if(sumary.isFalciform()) {
            addLabeledCheckbox(myHolder,
                    R.string.falciform,
                    context.getString(R.string.has_falciform) +
                            " forme "
                            +Utils.stringFromArray(context,R.array.anemia_forms,sumary.getFalciform().form));
        }

        if(!Utils.isNullOrEmpty(sumary.getRunningLongTreatment())) {
            addLabeledText(myHolder, R.string.running_treatment, sumary.getRunningLongTreatment());
        }

    }
    private void addLabeledCheckbox(MyHolder myHolder, @StringRes int label, String description){
        addLabeledCheckbox(myHolder, context.getString(label),description);
    }
    private void addLabeledCheckbox(MyHolder myHolder, @StringRes int label, @StringRes int description){
         addLabeledCheckbox(myHolder, context.getString(label),context.getString(description));
    }
    private void addLabeledCheckbox(MyHolder myHolder,String label, String description){
        View view = LayoutInflater.from(this.context).inflate(R.layout.labeled_checkbox, null);
        ((TextView)view.findViewById(R.id.label)).setText(label);
        ((AppCompatCheckBox)view.findViewById(R.id.checkbox)).setChecked(true);
        ((AppCompatCheckBox)view.findViewById(R.id.checkbox)).setText(description);
        myHolder.container.addView(view);
    }
    private void addLabeledText(MyHolder myHolder, @StringRes int label,String description){
        addLabeledCheckbox(myHolder, context.getString(label),description);
    }
    private void addLabeledText(MyHolder myHolder,String label, String description){
        View view = LayoutInflater.from(this.context).inflate(R.layout.labeled_checkbox, null);
        ((TextView)view.findViewById(R.id.label)).setText(label);
        ((AppCompatCheckBox)view.findViewById(R.id.checkbox)).setChecked(true);
        ((AppCompatCheckBox)view.findViewById(R.id.checkbox)).setText(description);
        myHolder.container.addView(view);

    }
    private void bindStat(MyHolder myHolder, StatInfo stat) {
        myHolder.chart.animateXY(1000,100);
        List<Entry> systo = new ArrayList<>();
        List<Entry> diasto = new ArrayList<>();
        switch (stat.type) {
            case 1:
                myHolder.title.setText("Evolution de la Pression Arterielle");
                for (int i = 0; i < stat.getEncounterInfos().size(); i++) {
                        EncounterInfo v = stat.getEncounterInfos().get(i);
                        systo.add(new Entry(i, v.getPressureSystolic()));
                        diasto.add(new Entry(i, v.getPressureDiastolic()));
                    }
                LineDataSet systol = new LineDataSet(systo, "Systole");
                systol.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                systol.setColor(R.color.colorAccent);
                systol.setCubicIntensity(0.2f);
                systol.setDrawHighlightIndicators(false);
                LineDataSet diastol = new LineDataSet(diasto, "Diastole");
                diastol.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                diastol.setCubicIntensity(0.2f);
                diastol.setDrawHighlightIndicators(false);
                LineData d = new LineData(systol, diastol);
                myHolder.chart.setData(d);
                break;
            case 2:
                myHolder.title.setText("Evolution de la Glycemie");
                for (int i = 0; i < stat.getEncounterInfos().size(); i++) {
                    EncounterInfo v = stat.getEncounterInfos().get(i);
                    systo.add(new Entry(i, v.getGlycemy()));
                }
                systol = new LineDataSet(systo, "Systole");
                systol.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                systol.setCubicIntensity(0.2f);
                systol.setDrawHighlightIndicators(false);
                myHolder.chart.setData(new LineData(systol));
                break;

            case 3:
                boolean adult = current != null && Utils.ageOf(current.getDob())> 21;

                myHolder.title.setText(!adult? "Evolution du Poids et de la Taille":"Evolution du Poids et du Tour de Taille");


                for (int i = 0; i < stat.getEncounterInfos().size(); i++) {
                    EncounterInfo v = stat.getEncounterInfos().get(i);
                    systo.add(new Entry(i, v.getWeight()));
                    diasto.add(new Entry(i, adult?v.getWaistSize():v.getHeight()));
                }
                systol = new LineDataSet(systo, "Poids");
                systol.setCubicIntensity(0.2f);
                systol.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                systol.setColor(R.color.colorAccent);
                systol.setDrawHighlightIndicators(false);
                diastol = new LineDataSet(diasto, adult?"Tour de Taille":"Taille");
                diastol.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                diastol.setCubicIntensity(0.2f);
                diastol.setDrawHighlightIndicators(false);
                d = new LineData(systol, diastol);

                myHolder.chart.setData(d);
                break;

        }

    }

    public @Nullable Object getItem(int position){
        return  mdata != null &&
                0 <= position &&
                position < mdata.size() ? mdata.get(position):null;
    }


    public void syncData(PatientInfo p) {
        mdata.clear();
        notifyDataSetChanged();
        if(p !=null) {
            mdata.add(p);
            notifyItemInserted(mdata.size()-1);
            new Loader(p).execute();
        }

    }




    @Override
    public void onViewDetachedFromWindow(MyHolder holder) {
        holder.clearAnimation();
    }



    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout container;
        //VIEW_TYPE_PATIENT_ID
        TextView fname,lname, pob, dob, sex, mobile, status,occupation;
        ImageView avatar;
        // VIEW_TYPE_PATIENT_STAT
        TextView title;
        LineChart chart;
        // summary
        // create constructor to get widget reference
        public MyHolder(View itemView) {
             super(itemView);
             fname = itemView.findViewById(R.id.firstname);
             lname = itemView.findViewById(R.id.lastname);
             pob = itemView.findViewById(R.id.pob);
             dob = itemView.findViewById(R.id.dob);
             sex = itemView.findViewById(R.id.sex);
             mobile = itemView.findViewById(R.id.mobile);
             status = itemView.findViewById(R.id.status);
             occupation = itemView.findViewById(R.id.occupation);
             avatar = itemView.findViewById(R.id.patientPicture);
             itemView.setOnClickListener(this);

             title = itemView.findViewById(R.id.title);
             chart = itemView.findViewById(R.id.pressureChart);
             if(chart != null) {

                 chart.setDrawGridBackground(false);
                 XAxis xx = chart.getXAxis();
                 xx.setDrawGridLines(false);
                 xx.setDrawAxisLine(true);
                 xx.setValueFormatter(new DefaultAxisValueFormatter(0));
                 xx.setPosition(XAxis.XAxisPosition.BOTTOM);
                 chart.getAxisRight().setEnabled(false);
                 chart.getLegend().setEnabled(false);
                 chart.getDescription().setEnabled(false);
             }

             container = itemView.findViewById(R.id.container);


        }

        public void clearAnimation(){
            itemView.clearAnimation();
        }
        @Override
        public void onClick(View v) {
            if (myClickListener != null) {
                myClickListener.onItemClick(getAdapterPosition(), v);
            }

        }


    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        myClickListener = itemClickListener;
    }

    class  Loader extends AsyncTask<Void, Object, Void> {
        PatientInfo p;
        public  Loader(PatientInfo p){
           this.p = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(p == null){

                return null;
            }
            publishProgress(app.getDb().summaryDAO().getPatientSummary(p.getUuid()));

            List<EncounterInfo> encounters = app.getDb().encounterDAO().getAllPatientEncounters(p.getUuid());
            if(encounters !=null && encounters.size() > 1) {
                publishProgress(new StatInfo(encounters, 1));
                publishProgress(new StatInfo(encounters, 2));
                publishProgress(new StatInfo(encounters, 3));
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            if(values!=null && values[0] != null) {
                mdata.add(values[0]);
                notifyItemInserted(mdata.size() -1);
            }
        }


    }


}