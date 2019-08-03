package com.tokopedia.testproject.problems.news.network;

import com.tokopedia.testproject.problems.news.model.NewsResult;

import io.reactivex.Observable;
import okhttp3.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsService {
    @GET("everything")
    Observable<NewsResult> getEverything(@Query("q") String query);

    @GET("top-headlines")
    Observable<NewsResult> getHeadline(@Query("country") String country,
                                 @Query("category") String category);
}
