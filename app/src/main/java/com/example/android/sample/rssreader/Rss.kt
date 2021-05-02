package com.example.android.sample.rssreader

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import org.w3c.dom.NodeList
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

data class Article(val title: String, val link: String, val pubDate:Date)

data class Rss(val title: String, val pubData: Date, val articles: List<Article>)

fun parseRss(stream: InputStream): Rss {

    val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(stream)
    stream.close()

    val xPath = XPathFactory.newInstance().newXPath()

    val formatter = SimpleDateFormat("EEE, dd MMM yyy HH:mm:ss z", Locale.US)

    val items = xPath.evaluate("/rss/channel//item", doc, XPathConstants.NODESET) as NodeList

    val articles = arrayListOf<Article>()

    for (i in 0 until items.length) {
        val item = items.item(i)

        val article = Article(
                title = xPath.evaluate("./title/text()", item),
                link = xPath.evaluate(".link/text()", item),
                pubDate = formatter.parse(xPath.evaluate(".pubDate/text()", item))
        )
        articles.add(article)
    }

    return Rss(title = xPath.evaluate("/rss/channel/title/text()", doc),
            pubData = formatter.parse(xPath.evaluate("/rss/channel/pubDate/text()", doc)),
            articles = articles
            )
}

class RssLoader (context: Context): AsyncTaskLoader<Rss>(context) {

    private var cache : Rss? = null

    // このローダーがバックグラウンドで行う処理
    override fun loadInBackground(): Rss? {
        val response = httpGet("https://www.sbbit.jp/rss/HotTpoics.rss")

        if (response != null) {
            // 取得に成功したら、パースして返す
            return parseRss(response)
        }

        return null
    }

    //  コールバッククラスに返す前に通る処理
    override fun deliverResult(data: Rss?) {
        if (isReset || data == null) return

        // 結果をキャッシュする
        cache = data
        super.deliverResult(data)
    }

    // バックグラウンド処理が開始される前に呼ばれる
    override fun onStartLoading() {
        // キャッシュがあるなら、キャッシュを返す
        if (cache != null) {
            deliverResult(cache)
        }

        // コンテンツが変化している場合やキャッシュがない場合には、バックグラウンド処理を行う
        if (takeContentChanged() || cache == null) {
            forceLoad()
        }
    }

    // ローダーが停止する前に呼ばれる処理
    override fun onStopLoading() {
        cancelLoad()
    }

    // ローダーが破棄される前に呼ばれる処理
    // 中ではキャッシュをnullにしている
    override fun onReset() {
        super.onReset()
        onStopLoading()
        cache = null
    }
}