package com.example.myfinanceapp.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.R
import com.example.myfinanceapp.data.apidata.newsdata.Data


class NewsAdapter(var context: Context, private val newsList: List<Data>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newTitle = cutTitle(newsList[position].title)
        holder.tvNewsDescription.text = newTitle
        holder.tvPublishDate.text = newsList[position].published_at.substring(0,10)
        holder.tvNewsSource.text = newsList[position].source

        Glide
            .with(context)
            .load(newsList[position].image_url)
            .centerCrop()
            .into(holder.ivNewsPhoto);

        holder.clNews.setOnClickListener {
            val uri = Uri.parse(newsList[position].url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            val activity: AppCompatActivity = context as AppCompatActivity
            (activity as MainActivity).startActivity(intent)
        }
    }

    private fun cutTitle(title: String): String {
        val i = title.lastIndexOf('(')
        if (i == -1)
            return title
        return title.substring(0, i-1)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    inner class NewsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var clNews = itemView.findViewById<ConstraintLayout>(R.id.clNews)
        var tvNewsDescription  = itemView.findViewById<TextView>(R.id.tvNewsDescription)
        var tvPublishDate = itemView.findViewById<TextView>(R.id.tvNewsPublishDate)
        var ivNewsPhoto = itemView.findViewById<ImageView>(R.id.ivNews)
        var tvNewsSource = itemView.findViewById<TextView>(R.id.tvNewsSource)
    }
}