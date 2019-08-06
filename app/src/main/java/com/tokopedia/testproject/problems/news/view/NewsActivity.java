package com.tokopedia.testproject.problems.news.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tokopedia.testproject.R;
import com.tokopedia.testproject.problems.news.model.Article;
import com.tokopedia.testproject.problems.news.model.Banner;
import com.tokopedia.testproject.problems.news.model.ButtonMore;
import com.tokopedia.testproject.problems.news.model.NewArticle;
import com.tokopedia.testproject.problems.news.presenter.DataStore;
import com.tokopedia.testproject.problems.news.presenter.NewsPresenter;

import java.util.ArrayList;
import java.util.List;


public class NewsActivity extends AppCompatActivity implements com.tokopedia.testproject.problems.news.presenter.NewsPresenter.View {

    private NewsPresenter newsPresenter;
    private RecyclerView recyclerView;
    private MainAdapter adapter;
    private ArrayList<Object> menuData = new ArrayList<>();
    private List<Banner> bannerData = new ArrayList<>();
    private List<NewArticle> newsData = new ArrayList<>();
    private DataStore ds;
    private SwipeRefreshLayout swipeRefresh;
    private Thread mThread;
    private Handler mHandler = new Handler();
    private boolean isSlide = false;
    private static String DEFAULT_KEYWORD = "android";
    private static String DEFAULT_LANGUAGE = "id";
    private static String DEFAULT_SORT = "publishedAt";
    private static Integer DEFAULT_PAGE = 1;
    private static final String TAG = "NewsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        newsPresenter = new NewsPresenter(NewsActivity.this);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MainAdapter(menuData,newsData,bannerData,NewsActivity.this);
        recyclerView.setAdapter(adapter);
        ds = new DataStore(this);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
            }
        });
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem menuItem = menu.findItem(R.id.item_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newsData.size() > 0){
                    List<NewArticle> search = new ArrayList<>();
                    for (int i = 0; i < newsData.size(); i++){
                        List<Article> temp = new ArrayList<>();
                        List<Article> data = newsData.get(i).getArticles();
                        if(data!=null){
                            for(int j = 0; j < data.size(); j++){
                                if(data.get(j).getTitle().toLowerCase().contains(newText.toLowerCase()) || data.get(j).getDescription().toLowerCase().contains(newText.toLowerCase()))
                                    temp.add(data.get(j));
                            }
                        }
                        if(temp.size() > 0) search.add(new NewArticle(newsData.get(i).getPublishedDate(),temp));
                    }
                    adapter.setNewsData(search);
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public void onSuccessGetNewsFormat(List<NewArticle> newArticleList) {
        if(newArticleList.size() > 0){
            newsData = newArticleList;
            ds.storeDataNews(newsData);
            checkData();
        } else {
            List<NewArticle> data = ds.getDataNews();
            if(data.size() > 0){
                Toast.makeText(this, "Couldn't update your feed, check your connection", Toast.LENGTH_SHORT).show();
                newsData = data;
                checkData();
            } else showSnackbar("No data Found");
        }
    }

    @Override
    public void onErrorGetNewsFormat(Throwable t) {
        //check sp
        List<NewArticle> data = ds.getDataNews();
        if(data.size() > 0){
            Toast.makeText(this, "Couldn't update your feed, check your connection", Toast.LENGTH_SHORT).show();
            newsData = data;
            checkData();
        } else showSnackbar(t.getMessage());
    }

    @Override
    public void onSuccessGetBanner(List<Banner> bannerList) {
        if(bannerList.size() > 0){
            bannerData = bannerList;
            ds.storeDataBanner(bannerData);
            checkData();
        } else {
            Toast.makeText(this, "Couldn't update your feed, check your connection", Toast.LENGTH_SHORT).show();
            List<Banner> data = ds.getDataBanner();
            if(data.size() > 0){
                bannerData = data;
                checkData();
            } else showSnackbar("No data found");
        }
    }

    @Override
    public void onErrorGetBanner(Throwable t) {
        //check sp
        List<Banner> data = ds.getDataBanner();
        if(data.size() > 0){
            Toast.makeText(this, "Couldn't update your feed, check your connection", Toast.LENGTH_SHORT).show();
            bannerData = data;
            checkData();
        } else showSnackbar(t.getMessage());
    }

    @Override
    public void onSuccessGetMoreNews(List<NewArticle> newArticleList) {
        if(newArticleList.size() > 0){
            int size = newsData.size();
            for(int i = 0; i < size; i++){
                if(i >= newArticleList.size()) break;
                if(newsData.get(i).getPublishedDate().equalsIgnoreCase(newArticleList.get(i).getPublishedDate())){
                    List<Article> articleList = newArticleList.get(i).getArticles();
                    if(articleList != null) newsData.get(i).getArticles().addAll(articleList);
                } else newsData.add(newArticleList.get(i));
            }
            adapter.notifyDataSetChanged();
            showProgress(false);
        } else showSnackbar("No data found");
    }

    @Override
    public void onErrorGetMoreNews(Throwable t) { showSnackbar(t.getMessage()); }

    public void checkData(){
        if(newsData.size() > 0 && bannerData.size() > 0){
            if(adapter.getItemCount() == 0){
                menuData.add(bannerData.get(0));
                menuData.add(newsData.get(0));
                menuData.add(new ButtonMore("Load More"));
            }
            adapter.setBannerData(bannerData);
            adapter.setNewsData(newsData);
            adapter.notifyDataSetChanged();
            showProgress(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        newsPresenter.unsubscribe();
        stopSlideBanner();
    }

    public void init(){
        showProgress(true);
        stopSlideBanner();
        DEFAULT_PAGE = 1;
        if(newsData.size() > 0) newsData.clear();
        if(bannerData.size() > 0) bannerData.clear();
        newsPresenter.getHeadline(DEFAULT_LANGUAGE,"technology",5);
        newsPresenter.getEverything(DEFAULT_KEYWORD,DEFAULT_SORT,DEFAULT_LANGUAGE,DEFAULT_PAGE);
    }

    public void showProgress(Boolean show){
        if(show) swipeRefresh.setRefreshing(true);
        else swipeRefresh.setRefreshing(false);
    }

    public void showSnackbar(String message){
        Snackbar snackbar = Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });
        snackbar.show();
        if(swipeRefresh.isRefreshing()) showProgress(false);
    }

    public void moreNews(){
        showProgress(true);
        DEFAULT_PAGE+=1;
        newsPresenter.getMoreNews(DEFAULT_KEYWORD,DEFAULT_SORT,DEFAULT_LANGUAGE,DEFAULT_PAGE);
    }

    public void startSlidingBanner(final RecyclerView rv, final LinearLayout dots){
        isSlide = true;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isSlide){
                    Log.d(TAG, "run: Thread jalan");
                    try { Thread.sleep(5000); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Thread: slideBanner actice");
                            if(!adapter.isScroll) adapter.slidingBanner(rv, dots); }
                    });
                }
            }
        });
        mThread.start();
    }

    public void stopSlideBanner(){
        isSlide = false;
        mHandler.removeCallbacksAndMessages("");
        mThread = null;
    }
}
