package com.example.weatherapp.adapter

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.weatherapp.R
import com.example.weatherapp.model.WeatherListItem
import java.util.Calendar

class TodaysForecastAdapter(
    var hourlyList : List<WeatherListItem?>
) : RecyclerView.Adapter<TodaysForecastAdapter.TodayViewHolder>()  {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val view = inflate.inflate(R.layout.today_hourly_list, parent,false)
        return TodayViewHolder(view)
    }

    override fun getItemCount(): Int = hourlyList.size

    override fun onBindViewHolder(holder: TodayViewHolder, position: Int) {
        var item = hourlyList.get(position)
        holder.timeText.text = item?.dtTxt!!.substring(11,16)

        val tempFerhn = item?.main?.temp
        val tempCels = tempFerhn?.minus(273.15)?.toInt()
        val tempFormatted = tempCels.toString()

        holder.tempText.text = "$tempFormatted°C"

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("HH:mm")
        val formattedTime = dateFormat.format(calendar.time)

        val timeOfAPI = item?.dtTxt!!.split(" ")
        val parsedTime = timeOfAPI[1]


        for ( i in item?.weather!!) {

            // Clear Sky Day
            if(i?.icon == "01d") {
                holder.statusIcon.setImageResource(R.drawable.ic_sunny)
            }

            // Clear Sky Night
            if(i?.icon == "01n") {
                holder.statusIcon.setImageResource(R.drawable.moon_stars)
            }

            // Partly Cloudy Day
            if(i?.icon == "02d") {
                holder.statusIcon.setImageResource(R.drawable.ic_sunnycloudy)
            }

            // Partly Cloudy Night
            if(i?.icon == "02n") {
                holder.statusIcon.setImageResource(R.drawable.moon_clouds)
            }

            // Cloudy
            if (i?.icon == "04d" || i?.icon == "04n") {
                holder.statusIcon.setImageResource(R.drawable.ic_cloudy)
            }

            // Light Rain Day and Night
            if(i?.icon == "09d" || i?.icon == "09n") {
                holder.statusIcon.setImageResource(R.drawable.ic_rainy)
            }

            // Rain Day and Night
            if(i?.icon == "10d" || i?.icon == "10n") {
                holder.statusIcon.setImageResource(R.drawable.ic_rainshower)
            }

            // Thunder Day and Night
            if(i?.icon == "11d" || i?.icon == "11n") {
                holder.statusIcon.setImageResource(R.drawable.ic_thunder)
            }

            // Snow Day and Night
            if(i?.icon == "13d" || i?.icon == "13n") {
                holder.statusIcon.setImageResource(R.drawable.ic_heavysnow)
            }

            // Mist Day and Night
            if(i?.icon == "50d" || i?.icon == "50n") {
                holder.statusIcon.setImageResource(R.drawable.mist)
            }

        }
    }

    fun updateDay(newList : List<WeatherListItem?>){
        hourlyList = newList
        notifyDataSetChanged()
    }

    class TodayViewHolder(itemView : View) : ViewHolder(itemView) {

        val timeText : TextView = itemView.findViewById(R.id.day_time)
        val statusIcon : ImageView = itemView.findViewById(R.id.day_time_weather_icon)
        val tempText : TextView = itemView.findViewById(R.id.current_hour_temp)
    }
}