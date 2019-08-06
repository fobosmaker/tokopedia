package com.tokopedia.testproject.problems.news.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tokopedia.testproject.R;
import com.tokopedia.testproject.problems.news.model.Article;
import com.tokopedia.testproject.problems.news.model.NewArticle;

import java.util.ArrayList;
import java.util.List;

public class NewArticleAdapter extends RecyclerView.Adapter<NewArticleAdapter.NewArticleViewHolder> {
    private List<NewArticle> articleList;
    private Context context;

    NewArticleAdapter(List<NewArticle> articleList, Context context) {
        setNewArticleList(articleList);
        this.context = context;
    }

    void setNewArticleList(List<NewArticle> articleList) {
        if (articleList == null) this.articleList = new ArrayList<>();
        else this.articleList = articleList;
    }

    @NonNull
    @Override
    public NewArticleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { return new NewArticleViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_article, viewGroup, false)); }

    @Override
    public void onBindViewHolder(@NonNull NewArticleViewHolder holder, int i) { holder.bind(articleList.get(i)); }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    class NewArticleViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private TextView textPublishedDate;

        NewArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            textPublishedDate = itemView.findViewById(R.id.textPublishedDate);
        }

        void bind(NewArticle article){
            if(article.getArticles()!=null){
                if(article.getArticles().size() > 0){
                    textPublishedDate.setText(article.getPublishedDate());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, OrientationHelper.VERTICAL,false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    NewsAdapter newsAdapter = new NewsAdapter(null);
                    recyclerView.setAdapter(newsAdapter);
                    List<Article> data = article.getArticles();
                    newsAdapter.setArticleList(data);
                    newsAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
