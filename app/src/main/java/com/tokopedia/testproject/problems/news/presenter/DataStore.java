package com.tokopedia.testproject.problems.news.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.tokopedia.testproject.problems.news.model.Article;
import com.tokopedia.testproject.problems.news.model.Banner;
import com.tokopedia.testproject.problems.news.model.NewArticle;
import com.tokopedia.testproject.problems.news.model.Source;

import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static final String KEY_BUNDLE_NEWS_SIZE = "bundleNewsSize_";
    private static final String KEY_BUNDLE_NEWS_PUBLISHED = "bundleNewsPublished_";
    private static final String KEY_NEWS_SIZE = "newsSize_";
    private static final String KEY_NEWS_TITLE = "newsTitle_";
    private static final String KEY_NEWS_DESCRIPTION = "newsDesc_";
    private static final String KEY_NEWS_PUBLISHED = "newsPublished_";
    private static final String KEY_NEWS_URL = "newsURL_";
    private static final String KEY_BANNER_SIZE = "bannerSize_";
    private static final String KEY_BANNER_TITLE = "bannerTitle_";
    private static final String KEY_BANNER_URL = "bannerURL_";

    public DataStore(Context context){ sp = context.getSharedPreferences("Data Session", 0); }

    public void storeDataNews(List<NewArticle> data){
        if(data.size() > 0){
            editor = sp.edit();
            editor.putInt(KEY_BUNDLE_NEWS_SIZE, data.size());
            for(int i = 0; i < data.size(); i++){
                editor.putString(KEY_BUNDLE_NEWS_PUBLISHED+i,data.get(i).getPublishedDate());
                List<Article> dataArticle = data.get(i).getArticles();
                if(dataArticle != null &&dataArticle.size() > 0){
                    editor.putInt(KEY_NEWS_SIZE+i,dataArticle.size());
                    for(int j = 0; j < dataArticle.size(); j++){
                        editor.putString(KEY_NEWS_TITLE+i+j, dataArticle.get(j).getTitle());
                        editor.putString(KEY_NEWS_DESCRIPTION+i+j, dataArticle.get(j).getDescription());
                        editor.putString(KEY_NEWS_PUBLISHED+i+j, dataArticle.get(j).getPublishedAt());
                        editor.putString(KEY_NEWS_URL+i+j, dataArticle.get(j).getUrlToImage());
                    }
                }
            }
            editor.apply();
        }
    }

    public List<NewArticle> getDataNews(){
        int size = sp.getInt(KEY_BUNDLE_NEWS_SIZE,0);
        if(size > 0){
            List<NewArticle> data = new ArrayList<>();
            for(int i = 0; i < size; i++){
                String date = sp.getString(KEY_BUNDLE_NEWS_PUBLISHED+i,null);
                int limitArticle = sp.getInt(KEY_NEWS_SIZE+i,0);
                if(limitArticle > 0){
                    List<Article> dataArticle = new ArrayList<>();
                    for(int j = 0; j < limitArticle; j++)
                        dataArticle.add(new Article(new Source("",""),"", sp.getString(KEY_NEWS_TITLE+i+j,null), sp.getString(KEY_NEWS_DESCRIPTION+i+j,null), "", sp.getString(KEY_NEWS_URL+i+j,null), sp.getString(KEY_NEWS_PUBLISHED+i+j,null), ""));
                    if(date != null)
                        data.add(new NewArticle(date,dataArticle));
                }
            }
            return data;
        } else return new ArrayList<>();
    }

    public void storeDataBanner(List<Banner> data){
        int limit = data.size();
        if(limit > 0){
            editor = sp.edit();
            editor.putInt(KEY_BANNER_SIZE,data.size());
            for(int i = 0; i < limit; i++){
                editor.putString(KEY_BANNER_TITLE+i, data.get(i).getTitle());
                editor.putString(KEY_BANNER_URL+i, data.get(i).getUrlToImage());
            }
            editor.apply();
        }
    }

    public List<Banner> getDataBanner(){
        int size = sp.getInt(KEY_BANNER_SIZE,0);
        if(size > 0){
            List<Banner> data = new ArrayList<>();
            for(int i = 0; i < size; i++)
                data.add(new Banner(sp.getString(KEY_BANNER_TITLE+i,null), sp.getString(KEY_BANNER_URL+i,null)));
            return data;
        } else return new ArrayList<>();
    }
}
