package com.tokopedia.testproject.problems.news.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tokopedia.testproject.problems.news.model.Article;
import com.tokopedia.testproject.problems.news.model.Banner;
import com.tokopedia.testproject.problems.news.model.NewArticle;
import com.tokopedia.testproject.problems.news.model.NewsResult;
import com.tokopedia.testproject.problems.news.model.Source;
import com.tokopedia.testproject.problems.news.network.NewsDataSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hendry on 27/01/19.
 */
public class NewsPresenter {

    private CompositeDisposable composite = new CompositeDisposable();

    private View view;
    private Context context;

    private static final String TAG = "NewsPresenter";

    public interface View {

        void onSuccessGetNewsFormat(List<NewArticle> newArticleList);

        void onErrorGetNewsFormat(Throwable t);

        void onSuccessGetBanner(List<Banner> banners);

        void onErrorGetBanner(Throwable t);
    }

    public NewsPresenter(NewsPresenter.View view, Context context) {
        this.view = view;
        this.context = context;
    }

    public void getEverything(String keyword) {
        NewsDataSource.getService().getEverything(keyword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        composite.add(d);
                    }

                    @Override
                    public void onNext(NewsResult newsResult) {
                        Log.d(TAG, "onNext: news "+newsResult.getArticles().size());
                        view.onSuccessGetNewsFormat(normalizeData(newsResult.getArticles()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onErrorGetNewsFormat(e);
                        Log.d(TAG, "onError: start"+e.getCause());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getHeadline(String country, String category) {
        NewsDataSource.getService().getHeadline(country, category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        composite.add(d);
                    }

                    @Override
                    public void onNext(NewsResult newsResult) {
                        Log.d(TAG, "onNext: headline "+ newsResult.getArticles().size());
                        view.onSuccessGetBanner(getBanner(newsResult.getArticles()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onErrorGetBanner(e);
                        Log.d(TAG, "onError: start"+e.getCause());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void unsubscribe() {
        composite.dispose();
    }

    private List<NewArticle> normalizeData(List<Article> data){

        List<NewArticle> newData = new ArrayList<>();

        //get Date only
        List<String> dateArticleTemp = new ArrayList<>();
        for(int i = 0; i < data.size(); i++){
            String date = formatDate(data.get(i).getPublishedAt());
            if(!dateArticleTemp.contains(date)){
                dateArticleTemp.add(date);
            }
        }

        //sort data from the newest
        for(int i = 0; i < dateArticleTemp.size(); i++){
            for(int j = 0; j < dateArticleTemp.size(); j++){
                if(i!=j) {
                    Date a = new Date(dateArticleTemp.get(i));
                    Date b = new Date(dateArticleTemp.get(j));
                    if (a.after(b)) {
                        String temp = dateArticleTemp.get(i);
                        dateArticleTemp.set(i, dateArticleTemp.get(j));
                        dateArticleTemp.set(j, temp);
                    }
                }
            }
        }
        Log.d(TAG, "normalizeData: "+dateArticleTemp);

        //create new structure for news data
        for(int i = 0; i < dateArticleTemp.size(); i++){
            List<Article> dataArticleTemp = new ArrayList<>();
            for(int j = 0; j < data.size(); j++){
                if(dateArticleTemp.get(i).equalsIgnoreCase(formatDate(data.get(j).getPublishedAt()))){
                    Article data2 = new Article(data.get(j).getSource(),data.get(j).getAuthor(),data.get(j).getTitle(),data.get(j).getDescription(),data.get(j).getUrl(),data.get(j).getUrlToImage(),formatDate(data.get(j).getPublishedAt()),data.get(j).getContent());
                    dataArticleTemp.add(data2);
                }
            }
            newData.add(i,new NewArticle(dateArticleTemp.get(i),dataArticleTemp));
        }

        return newData;
    }

    private List<Banner> getBanner(List<Article> data){
        List<Banner> bannerData = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            bannerData.add(new Banner(data.get(i).getTitle(),data.get(i).getUrlToImage()));
        }
        return bannerData;
    }

    private String formatDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        try {
            Date dateNow = dateFormat.parse(date);
            SimpleDateFormat newFormat = new SimpleDateFormat("dd MMMM yyyy",Locale.ENGLISH);
            return newFormat.format(dateNow);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "putData: "+e.getMessage());
            return date;
        }
    }

    public class DataStore {
        private SharedPreferences sp;
        private SharedPreferences.Editor editor;
        private final String SHARE_NAME = "Data Session";
        private final int MODE_PRIVATE = 0;

        private DataStore(Context context){
            sp = context.getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
            editor = sp.edit();
        }

        private void storeDataNews(List<NewArticle> data){

        }

        private void getDatanews(){
            int size = sp.getInt("news_size",0);
            List<NewArticle> data = new ArrayList<>();
            for(int i = 0; i < size; i++){

            }
        }

        private void storeData(List<Article> data, int type){
            int limit;
            if(type == 1){
                limit = data.size();
            } else {
                limit = 5;
            }
            List<Article> prevData = getData(type);
            if(prevData.size() == 0){
                editor.putInt("article_size",data.size());
                for(int i = 0; i < limit; i++){
                    putData(data.get(i), i,type);
                }
            } else{
                for(int i = 0; i < limit; i++){
                    if(!prevData.get(i).equals(data.get(i))){
                        putData(data.get(i),i,type);
                    }
                }
            }
            editor.apply();
        }

        private List<Article> getData(int type) {
            int size = sp.getInt("article_size",0);
            List<Article> datax = new ArrayList<>();
            switch (type){
                case 1:
                    for(int i = 0; i < size; i++){
                        datax.add(new Article(new Source("",""),
                                "",
                                sp.getString("getTitle_"+i,null),
                                sp.getString("getDescription_"+i,null),
                                "",
                                sp.getString("getUrlToImage_"+i,null),
                                sp.getString("getPublishedAt_"+i,null),
                                ""));
                    }
                    break;
                case 2:
                    for(int i = 0; i < size; i++){
                        datax.add(new Article(new Source("",""),
                                "",
                                sp.getString("getBannerTitle_"+i,null),
                                "",
                                "",
                                sp.getString("getBannerUrlToImage_"+i,null),
                                "",
                                ""));
                    }
                    break;
            }
            return datax;
        }

        private void putData(Article data, int i, int type){
            switch (type){
                case 1:
                    editor.putString("getTitle_"+i, data.getTitle());
                    editor.putString("getDescription_"+i, data.getDescription());
                    editor.putString("getPublishedAt_"+i, formatDate(data.getPublishedAt()).toString());
                    editor.putString("getUrlToImage_"+i, data.getUrlToImage());
                    break;
                case 2:
                    editor.putString("getBannerTitle_"+i, data.getTitle());
                    editor.putString("getBannerUrlToImage_"+i, data.getUrlToImage());
                    break;
            }
        }



        private void clearData(){
            editor.clear();
            editor.apply();
        }
    }
}
