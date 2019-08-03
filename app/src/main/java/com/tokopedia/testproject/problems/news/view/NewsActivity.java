package com.tokopedia.testproject.problems.news.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.tokopedia.testproject.R;
import com.tokopedia.testproject.problems.news.model.Article;
import com.tokopedia.testproject.problems.news.model.Banner;
import com.tokopedia.testproject.problems.news.model.ButtonMore;
import com.tokopedia.testproject.problems.news.model.NewArticle;
import com.tokopedia.testproject.problems.news.presenter.NewsPresenter;

import java.util.ArrayList;
import java.util.List;


public class NewsActivity extends AppCompatActivity implements com.tokopedia.testproject.problems.news.presenter.NewsPresenter.View {

    private NewsPresenter newsPresenter;
    private RecyclerView recyclerView;
    private MainAdapter adapter;
    //private LinearLayoutManager linearLayoutManager;
    private ArrayList<Object> menuData = new ArrayList<>();
    private List<Banner> bannerData = new ArrayList<>();
    private List<NewArticle> newsData = new ArrayList<>();
    private static final String TAG = "NewsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        newsPresenter = new NewsPresenter(this, NewsActivity.this);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MainAdapter(menuData,newsData,bannerData,NewsActivity.this);
        recyclerView.setAdapter(adapter);
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
                                if(data.get(j).getTitle().toLowerCase().contains(newText.toLowerCase()) || data.get(j).getDescription().toLowerCase().contains(newText.toLowerCase())){
                                    temp.add(data.get(j));
                                }
                            }
                            if(temp.size() > 0){
                                search.add(new NewArticle(newsData.get(i).getPublishedDate(),temp));
                                temp.clear();
                            }
                        }
                    }
                    Log.d(TAG, "onQueryTextChange: filtered "+search.size());
                    adapter.searchFilter(search);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public void onSuccessGetNewsFormat(List<NewArticle> newArticleList) {
        newsData = newArticleList;
        checkData();
    }

    @Override
    public void onErrorGetNewsFormat(Throwable t) {
        showSnackbar(t.getMessage());
    }

    @Override
    public void onSuccessGetBanner(List<Banner> bannerList) {
        bannerData = bannerList;
        checkData();
    }

    @Override
    public void onErrorGetBanner(Throwable t) {
        showSnackbar(t.getMessage());
    }

    public void checkData(){
        if(newsData.size() > 0 && bannerData.size() > 0){
            if(adapter.getItemCount() == 0){
                menuData.add(bannerData.get(0));
                menuData.add(newsData.get(0));
                menuData.add(new ButtonMore("Load More"));
                adapter.setBannerData(bannerData);
                adapter.setNewsData(newsData);
                adapter.notifyDataSetChanged();
                showLoading(false);
            } else {
                adapter.setNewsData(newsData);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        newsPresenter.unsubscribe();
    }

    public void init(){
        showLoading(true);
        newsPresenter.getHeadline("id","technology");
        newsPresenter.getEverything("android");
    }

    private void showLoading(Boolean bool){
        ProgressBar pb = findViewById(R.id.progressBar);
        if(bool){
            pb.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            pb.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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
    }

    public void moreNews(){
        newsPresenter.getEverything("android");
    }
}
