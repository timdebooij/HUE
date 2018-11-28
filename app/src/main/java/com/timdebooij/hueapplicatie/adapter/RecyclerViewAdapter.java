package com.timdebooij.hueapplicatie.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timdebooij.hueapplicatie.BridgeDetailActivity;
import com.timdebooij.hueapplicatie.fragmentTest;
import com.timdebooij.hueapplicatie.R;
import com.timdebooij.hueapplicatie.models.Bridge;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Bridge> bridges;

    public RecyclerViewAdapter(Context context, ArrayList<Bridge> bridges)
    {
        this.context = context;
        this.bridges = bridges;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bridgeview, viewGroup, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int i) {
        Bridge bridge = bridges.get(i);

        viewHolder.name.setText(bridge.name);
        viewHolder.ip.setText(context.getString(R.string.ipAddressBridgeDetail) + bridge.ipAddress);
        viewHolder.setListener(bridge, viewHolder.getLayoutPosition());

        viewHolder.port.setText(context.getString(R.string.portMain) + bridge.port);
        viewHolder.bulbs.setText(context.getString(R.string.amountLightsMain) + bridge.lightBulbs.size());
    }

    @Override
    public int getItemCount() {
        return bridges.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView ip;
        TextView port;
        TextView bulbs;
        public ViewHolder(View itemView, final Context context){
            super(itemView);
            //context = context;
            name = itemView.findViewById(R.id.bridgeName);
            ip = itemView.findViewById(R.id.bridgeIp);
            port = itemView.findViewById(R.id.bridgePort);
            bulbs = itemView.findViewById(R.id.bridgeBulbAmount);
        }

        public void setListener(final Bridge bridge, final int i){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), fragmentTest.class);
                    intent.putExtra("bridge", bridge);
                    intent.putExtra("number", i);
                    view.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }
}
