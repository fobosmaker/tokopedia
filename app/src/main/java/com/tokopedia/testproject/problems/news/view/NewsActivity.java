package com.tokopedia.testproject.problems.news.view;

import android.os.AsyncTask;
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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tokopedia.testproject.R;
import com.tokopedia.testproject.problems.news.model.Article;
import com.tokopedia.testproject.problems.news.model.Banner;
import com.tokopedia.testproject.problems.news.model.ButtonMore;
import com.tokopedia.testproject.problems.news.model.NewArticle;
import com.tokopedia.testproject.problems.news.presenter.NewsPresenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NewsActivity extends AppCompatActivity implements com.tokopedia.testproject.problems.news.presenter.NewsPresenter.View {

    private NewsPresenter newsPresenter;
    //private NewsAdapter newsAdapter;
    private GetDataBaner mGetDataBanner;
    private RecyclerView recyclerView;
    //private DataStore mDataStore;
    private MainAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<Banner> bannerData = new ArrayList<>();
    //private List<Article> newsData = new ArrayList<>();
    private List<NewArticle> newsData = new ArrayList<>();
    private static final String TAG = "NewsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        newsPresenter = new NewsPresenter(this, NewsActivity.this);
        //newsAdapter = new NewsAdapter(null);
        //recyclerView.setAdapter(newsAdapter);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new MainAdapter(newsData,bannerData,NewsActivity.this);
        linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(linearLayoutManager.findFirstVisibleItemPosition() > 0){
                    Log.d(TAG, "onScrolled: menu pertama hilang");
                    //buttonBackToTop.setVisibility(View.GONE);
                }

                if(linearLayoutManager.getChildCount()+linearLayoutManager.findFirstVisibleItemPosition() == linearLayoutManager.getItemCount()){
                    Log.d(TAG, "onScrolled: abis");
                    //buttonBackToTop.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "onScrolled: masi sisa "+(linearLayoutManager.getItemCount()-linearLayoutManager.getChildCount()-linearLayoutManager.findFirstVisibleItemPosition()));
                    //buttonBackToTop.setVisibility(View.GONE);
                }
                Log.d(TAG, "onScrolled: "+linearLayoutManager.getChildCount()+" "+linearLayoutManager.findFirstVisibleItemPosition());
            }
        });
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
            adapter.setBannerData(bannerData);
            adapter.setNewsData(newsData);
            adapter.setMenu_button(new ButtonMore("Load More"));
            adapter.notifyDataSetChanged();
            showLoading(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        newsPresenter.unsubscribe();
    }

    public void init(){
       /* mGetDataBanner = new GetDataBaner("id","technology");
        mGetDataBanner.execute();*/
        //loading.show(getSupportFragmentManager(),"loading");
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

    public class GetDataBaner extends AsyncTask<Void,Void,Boolean> {
        private Integer type;

        public GetDataBaner(Integer type){
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loading.show(getSupportFragmentManager(),"Loading");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Simulate network access.
                Thread.sleep(3000);
                Log.d(TAG, "doInBackground: type "+type+" is running");
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mGetDataBanner = null;
            if(success){
                Log.d(TAG, "onPostExecute: type: "+type+" finish!");
            } else {
                showLoading(false);
                showSnackbar("Failed to get data, try again");
                //Toast.makeText(CCTVActivity.this, "Failed to get data, try again", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mGetDataBanner = null;
            showLoading(false);
        }
    }

    private Date formatDate(String date){
        Date returnDate = new Date("31 Ju");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        try {
            //Date dateNow = dateFormat.parse(date);
            returnDate =  dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "putData: "+e.getMessage());
            //return date;
        }
        return returnDate;
    }
}
