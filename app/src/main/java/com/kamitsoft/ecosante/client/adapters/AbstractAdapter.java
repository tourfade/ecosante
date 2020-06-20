package com.kamitsoft.ecosante.client.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageButton;

import com.kamitsoft.ecosante.R;

import androidx.recyclerview.widget.RecyclerView;

public abstract class AbstractAdapter<T extends AbstractAdapter.EdtiableHolder> extends RecyclerView.Adapter<T>  {
    protected ItemClickListener myClickListener;
    protected AnimatorSet anset;

    public   class EdtiableHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private ImageButton[] actions;
        private View verso;

        // create constructor to get widget reference
        public EdtiableHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            verso = itemView.findViewById(R.id.verso);
            if(verso == null){
                return;
            }
            actions = new ImageButton[]{ itemView.findViewById(R.id.item_delete),itemView.findViewById(R.id.item_edit)};
            if(verso != null) {
                for (View b :actions ) {
                    if(b!=null)
                        b.setOnClickListener(this);
                }




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
            boolean click = false;
            if(verso !=null) {
                if (anset!=null && anset.isRunning()) {
                    for(View b:actions){
                        if(v == b){
                            click = true;
                        }
                    }
                    if(!click) {
                        close();
                    }

                }
            }
            if (myClickListener != null ) {
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
