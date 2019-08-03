package com.tokopedia.testproject.problems.news.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tokopedia.testproject.R;
import com.tokopedia.testproject.problems.news.model.Article;
import com.tokopedia.testproject.problems.news.model.NewArticle;

import java.util.ArrayList;
import java.util.List;

public class NewArticleAdapter extends RecyclerView.Adapter<NewArticleAdapter.NewArticleViewHolder> {
    private List<NewArticle> articleList;

    NewArticleAdapter(List<NewArticle> articleList) {
        setNewArticleList(articleList);
    }

    void setNewArticleList(List<NewArticle> articleList) {
        if (articleList == null) {
            this.articleList = new ArrayList<>();
        } else {
            this.articleList = articleList;
        }
    }

    @NonNull
    @Override
    public NewArticleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new NewArticleViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_article, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewArticleViewHolder holder, int i) {
        if(articleList.get(i).getArticles() != null && articleList.get(i).getArticles().size() > 0){
            holder.textPublishedDate.setText(articleList.get(i).getPublishedDate());
            NewsAdapter newsAdapter = new NewsAdapter(null);
            holder.recyclerView.setAdapter(newsAdapter);
            List<Article> data = articleList.get(i).getArticles();
            newsAdapter.setArticleList(data);
            newsAdapter.notifyDataSetChanged();
        }
    }

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

       /* void bind(NewArticle article) {
            textPublishedDate.setText(article.getPublishedDate());
            NewsAdapter adapter = new NewArticleAdapter(null);
            recyclerView.setAdapter(adapter);
            if(article.getArticles() != null && article.getArticles().size() > 0){
                List<Article> data = article.getArticles();
                adapter.setArticleList(data);
                adapter.notifyDataSetChanged();
            } else {
                holder.recyclerView.setVisibility(View.GONE);
                context.showSnackbar("Empty Data");
            }
        }*/
    }
}
