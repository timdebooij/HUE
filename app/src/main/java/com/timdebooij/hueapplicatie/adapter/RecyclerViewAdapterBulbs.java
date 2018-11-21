package com.timdebooij.hueapplicatie.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.timdebooij.hueapplicatie.BridgeDetailActivity;
import com.timdebooij.hueapplicatie.LightbulbDetailActivity;
import com.timdebooij.hueapplicatie.R;
import com.timdebooij.hueapplicatie.models.Bridge;
import com.timdebooij.hueapplicatie.models.LightBulb;

import java.util.ArrayList;

public class RecyclerViewAdapterBulbs extends RecyclerView.Adapter<RecyclerViewAdapterBulbs.ViewHolderBulb> {
    private Context context;
    private ArrayList<LightBulb> lightBulbs;
    private Bridge bridge;

    public RecyclerViewAdapterBulbs(Context context, ArrayList<LightBulb> lightBulbs, Bridge bridge) {
        this.context = context;
        this.lightBulbs = lightBulbs;
        this.bridge = bridge;
    }

    @NonNull
    @Override
    public ViewHolderBulb onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bulbview, viewGroup, false);
        return new RecyclerViewAdapterBulbs.ViewHolderBulb(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBulb viewHolderBulb, int i) {
        LightBulb bulb = lightBulbs.get(i);
        viewHolderBulb.id.setText(bulb.id);
        viewHolderBulb.color.setBackgroundColor(Color.RED);
        viewHolderBulb.setListener(bulb, viewHolderBulb.getLayoutPosition());
    }

    @Override
    public int getItemCount() {
        return lightBulbs.size();
    }

    public class ViewHolderBulb extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView id;
        ImageView color;
        public ViewHolderBulb(View itemView, final Context context){
            super(itemView);
            //context = context;
            id = itemView.findViewById(R.id.lightbulbID);
            color = itemView.findViewById(R.id.lightbulbColor);
        }

        public void setListener(final LightBulb bulb, final int i){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), LightbulbDetailActivity.class);
                    intent.putExtra("bulb", bulb);
                    Log.i("infocom", "bulb is: " + bulb.on);
                    Log.i("infocom", "bulb hue: " + bulb.hue);
                    Log.i("infocom", "bulb number: " + i);
                    intent.putExtra("number", i);
                    intent.putExtra("bridge", bridge);
                    view.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }
}
