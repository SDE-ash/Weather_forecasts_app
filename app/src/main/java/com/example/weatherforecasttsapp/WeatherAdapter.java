package com.example.weatherforecasttsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {

        WeatherRVModel model =  weatherRVModelArrayList.get(position);


        holder.idTemp.setText(model.getTemperature().concat("°C"));

        Picasso.get().load("http:".concat(model.getIcon())).into(holder.idIconCondition);

        holder.idWindspeed.setText(model.getWindSpeed().concat("km/hr"));

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try{
            Date t = input.parse(model.getTime());
            holder.idTime.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView idTime, idTemp,idWindspeed;
        private ImageView idIconCondition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idTime = itemView.findViewById(R.id.idTime);
            idTemp = itemView.findViewById(R.id.idTemp);
            idWindspeed = itemView.findViewById(R.id.idWindspeed);
            idIconCondition = itemView.findViewById(R.id.idIconCondition);
        }
    }
}
