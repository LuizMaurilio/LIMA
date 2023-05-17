package com.example.lima.lima;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class HistoricoViewHolder extends GroupViewHolder {
    private TextView mTextView;
    private ImageView arrow;
    private View item;
    private Historico hist;

    HistoricoViewHolder(View itemView) {
        super(itemView);
        item = itemView;
        mTextView = itemView.findViewById(R.id.textView);
        arrow = itemView.findViewById(R.id.arrow);
    }

    void bind(Historico historico){
        hist = historico;
        mTextView.setText(historico.getTitle());
    }

    @Override
    public void expand() {
        if(hist.getItems().size() <= 0){
            //Toast.makeText(item.getContext(),"Não há dados ainda", Toast.LENGTH_SHORT).show();
            Toast.makeText(item.getContext(),"No data yet", Toast.LENGTH_SHORT).show();
        }
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

}
