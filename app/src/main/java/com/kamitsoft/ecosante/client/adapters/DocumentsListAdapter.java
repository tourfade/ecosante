package com.kamitsoft.ecosante.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.model.DocumentInfo;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;



public class DocumentsListAdapter extends AbstractAdapter<DocumentsListAdapter.MyHolder>  {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    private Context context;
    private List<DocumentInfo> mdata;

    // create constructor to innitilize context and data sent from MainActivity
    public DocumentsListAdapter(Context context) {
        this.context = context;
        mdata = new ArrayList<>();
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
            v = LayoutInflater.from(this.context).inflate(R.layout.doc_item, parent, false);
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

            DocumentInfo current = mdata.get(position);
            if(current != null) {
                myHolder.title.setText(Utils.niceFormat(current.getDocName()));
                myHolder.date.setText(Utils.format(current.getDate()));
                if(Utils.isPicutre(current.getMimeType())) {
                    Utils.loadSquare(context, current.getAttachment(), myHolder.document, R.drawable.ic_menu_gallery, R.drawable.pdf);
                }else{
                    myHolder.document.setImageResource(Utils.getPicture(current.getMimeType()));

                }
            }
       }

    }

    public @Nullable DocumentInfo getItem(int position){
        return  mdata != null && mdata.size() > 0? mdata.get(position):null;
    }
    public void syncData(List<DocumentInfo> data) {
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



    public  class MyHolder extends AbstractAdapter.EdtiableHolder {


        TextView title, date;
        ImageView document;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
             super(itemView);
             title = itemView.findViewById(R.id.title);
             document = itemView.findViewById(R.id.thumbnail);
             date = itemView.findViewById(R.id.date);
        }
    }





}