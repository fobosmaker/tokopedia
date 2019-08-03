package com.tokopedia.testproject.problems.news.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NewsResult(
        @SerializedName("status")
        @Expose
        val status: String,
        @SerializedName("totalResults")
        @Expose
        val totalResults: Int,
        @SerializedName("articles")
        @Expose
        val articles: List<Article>? = null)

data class Source(

        @SerializedName("id")
        @Expose
        val id: String,
        @SerializedName("name")
        @Expose
        val name: String)

data class Article(
        @SerializedName("source")
        @Expose
        val source: Source? = null,
        @SerializedName("author")
        @Expose
        val author: String? = null,
        @SerializedName("title")
        @Expose
        val title: String? = null,
        @SerializedName("description")
        @Expose
        val description: String? = null,
        @SerializedName("url")
        @Expose
        val url: String? = null,
        @SerializedName("urlToImage")
        @Expose
        val urlToImage: String? = null,
        @SerializedName("publishedAt")
        @Expose
        val publishedAt: String? = null,
        @SerializedName("content")
        @Expose
        val content: String? = null)

data class Banner(
        @SerializedName("title")
        @Expose
        val title: String? = null,
        @SerializedName("urlToImage")
        @Expose
        val urlToImage: String? = null)

data class ButtonMore(
        @SerializedName("title")
        @Expose
        val title: String? = null)

data class NewArticle(
        @SerializedName("publishedAt")
        @Expose
        val publishedDate: String,
        @SerializedName("articles")
        @Expose
        val articles: List<Article>? = null
)