package com.example.jonas.areafoliar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

class FolhaViewHolder extends ChildViewHolder {
    private TextView txtNome,txtArea,txtAltura,txtLargura,txtPerimetro;
    private Folha folha;

    FolhaViewHolder(View itemView, final Context context) {
        super(itemView);
        txtNome = itemView.findViewById(R.id.lblNome);
        txtArea = itemView.findViewById(R.id.lblArea);
        txtAltura = itemView.findViewById(R.id.lblAltura);
        txtLargura = itemView.findViewById(R.id.lblLargura);
        txtPerimetro = itemView.findViewById(R.id.lblPerimetro);
        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(ActDados.calculos.size() > 0){
                    Intent it = new Intent(context, ActConfigDados.class);
                    it.putExtra("Id_img", folha.getIdImg());
                    ((AppCompatActivity)context).startActivityForResult(it,0);
                }
            }
        });
    }

    void bind(Folha folha){
        this.folha = folha;
        txtNome.setText(folha.getNomeImagem()+"");
        txtArea.setText(folha.getArea()+ "");
        txtAltura.setText(folha.getComprimento()+"");
        txtLargura.setText(folha.getLargura()+"");
        txtPerimetro.setText(folha.getPerimetro()+"");
    }

}
