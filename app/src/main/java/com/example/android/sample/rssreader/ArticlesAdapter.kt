package com.example.android.sample.rssreader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ArticlesAdapter(private val context: Context,
                      private val articles: List<Article>,
                      private val onArticleClicked: (Article) -> Unit
): RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder>() {

    private val inflater = LayoutInflater.from(context)


    // ビューホルダー
    class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.title)
        val pubData = view.findViewById<TextView>(R.id.pubDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = inflater.inflate(R.layout.grid_article_cell, parent, false)
        val viewHolder = ArticleViewHolder(view)

        view.setOnClickListener {
            // タップされた記事の位置
            val position = viewHolder.adapterPosition
            // タップされた位置に応じた記事
            val article = articles[position]
            // コールバックを呼ぶ
            onArticleClicked(article)
        }

        return viewHolder
    }

    override fun getItemCount() = articles.size

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]

        holder.title.text = article.title

        holder.pubData.text = context.getString(R.string.date_string, article.pubDate)
    }
}