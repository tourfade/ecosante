package com.kamitsoft.ecosante.client.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageButton;

import com.kamitsoft.ecosante.R;

import androidx.recyclerview.widget.RecyclerView;

public abstract class AbstractAdapter<T extends AbstractAdapter.EdtiableHolder> extends RecyclerView.Adapter<T>  {
     ItemClickListener myClickListener;
     AnimatorSet anset;

    public   class EdtiableHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private ImageButton delete,edit;
        private View verso;

        // create constructor to get widget reference
        public EdtiableHolder(View itemView) {
            super(itemView);
            verso = itemView.findViewById(R.id.verso);
            delete = itemView.findViewById(R.id.item_delete);
            edit = itemView.findViewById(R.id.item_edit);
            if(verso != null) {

                if(delete!=null)
                    delete.setOnClickListener(this);

                if(edit!=null)
                    edit.setOnClickListener(this);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(v -> {
                    anset = new AnimatorSet();
                    anset.play(ObjectAnimator
                            .ofInt(itemView.findViewById(R.id.insider), "scrollX", 0)
                            .setDuration(200))
                            .after(3000)
                            .after(ObjectAnimator
                                    .ofInt(itemView.findViewById(R.id.insider), "scrollX", verso.getRight() - verso.getLeft())
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

            if(verso !=null) {
                if (anset!=null && anset.isRunning()) {
                    close();
                    return;
                }
            }
            if (myClickListener != null) {
                myClickListener.onItemClick(getAdapterPosition(), v);
            }
            close();
        }
        public  void close() {
            if(anset !=null && anset.isRunning()){
                Animator cllosing = anset.getChildAnimations().get(0);
                if(!cllosing.isRunning()) {
                    anset.cancel();
                    cllosing.start();
                    anset = null;
                }
            }
        }


    }




    public void setItemClickListener(ItemClickListener itemClickListener){
        myClickListener = itemClickListener;
    }

    public void removeListener() {
        myClickListener = null;
    }
}