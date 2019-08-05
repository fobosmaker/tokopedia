package com.tokopedia.testproject.problems.news.presenter;

import android.util.Log;

import com.tokopedia.testproject.problems.news.model.Article;
import com.tokopedia.testproject.problems.news.model.Banner;
import com.tokopedia.testproject.problems.news.model.NewArticle;
import com.tokopedia.testproject.problems.news.model.NewsResult;
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

    private static final String TAG = "NewsPresenter";

    public interface View {

        void onSuccessGetNewsFormat(List<NewArticle> newArticleList);

        void onErrorGetNewsFormat(Throwable t);

        void onSuccessGetBanner(List<Banner> banners);

        void onErrorGetBanner(Throwable t);

        void onSuccessGetMoreNews(List<NewArticle> newArticleList);

        void onErrorGetMoreNews(Throwable t);
    }

    public NewsPresenter(NewsPresenter.View view) {
        this.view = view;
    }

    public void getEverything(String keyword, String sortBy) {
        NewsDataSource.getService().getEverything(keyword, sortBy)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        composite.add(d);
                    }

                    @Override
                    public void onNext(NewsResult newsResult) {
                        view.onSuccessGetNewsFormat(normalizeData(newsResult.getArticles()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onErrorGetNewsFormat(e);
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
                        view.onSuccessGetBanner(getBanner(newsResult.getArticles()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onErrorGetBanner(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getMoreNews(String keyword, String sortBy) {
        NewsDataSource.getService().getEverything(keyword, sortBy)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        composite.add(d);
                    }

                    @Override
                    public void onNext(NewsResult newsResult) {
                        view.onSuccessGetMoreNews(normalizeData(newsResult.getArticles()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onErrorGetMoreNews(e);
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

        /*//sort data from the newest
        for(int i = 0; i < dateArticleTemp.size(); i++){
            for(int j = 0; j < dateArticleTemp.size(); j++){
                if(i!=j) {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy",Locale.ENGLISH);
                        Date a = format.parse(dateArticleTemp.get(i));
                        Date b = format.parse(dateArticleTemp.get(j));
                        if (a.after(b)) {
                            String temp = dateArticleTemp.get(i);
                            dateArticleTemp.set(i, dateArticleTemp.get(j));
                            dateArticleTemp.set(j, temp);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/

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


}
