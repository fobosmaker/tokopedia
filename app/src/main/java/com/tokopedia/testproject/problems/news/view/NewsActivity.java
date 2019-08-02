package com.tokopedia.testproject.problems.news.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;

import com.tokopedia.testproject.R;
import com.tokopedia.testproject.problems.news.model.Article;
import com.tokopedia.testproject.problems.news.model.Source;
import com.tokopedia.testproject.problems.news.presenter.NewsPresenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NewsActivity extends AppCompatActivity implements com.tokopedia.testproject.problems.news.presenter.NewsPresenter.View {

    private NewsPresenter newsPresenter;
    private NewsAdapter newsAdapter;
    private LoadingDialog loading;
    private GetDataNews mGetDataNews;
    private RecyclerView recyclerView;
    private DataStore mDataStore;
    private static final String TAG = "NewsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        newsPresenter = new NewsPresenter(this);
        newsAdapter = new NewsAdapter(null);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(newsAdapter);
        loading = new LoadingDialog();
        mDataStore = new DataStore(NewsActivity.this);
        mDataStore.clearData();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSuccessGetNews(List<Article> articleList) {
        Log.d(TAG, "onSuccessGetNews: get data success");
        mDataStore.storeData(articleList);
        newsAdapter.setArticleList(mDataStore.getData());
        newsAdapter.notifyDataSetChanged();
        loading.dismiss();
    }

    @Override
    public void onErrorGetNews(Throwable throwable) {
        //Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
        List<Article> data = mDataStore.getData();
        Log.d(TAG, "onSuccessGetNews: data from sp:"+data.size());
        if(data.size() == 0){
            showSnackbar(throwable.getMessage());
        } else {
            newsAdapter.setArticleList(data);
            newsAdapter.notifyDataSetChanged();
        }
        loading.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        newsPresenter.unsubscribe();
    }

    public void init(){
        //call AsyncTask
        mGetDataNews = new GetDataNews("android");
        mGetDataNews.execute();
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

    public class GetDataNews extends AsyncTask<Void,Void,Boolean> {

        private String keyword;

        private GetDataNews(String keyword){
            this.keyword = keyword;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.show(getSupportFragmentManager(),"Loading");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Simulate network access.
                Thread.sleep(1000);
                Log.d(TAG, "doInBackground: start");
                newsPresenter.getEverything(keyword);
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            Log.d(TAG, "onPostExecute: start");
            mGetDataNews = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mGetDataNews = null;
        }
    }

    public static class LoadingDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            if(getActivity()!= null){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.loading_progress, null);
                builder.setView(dialogView);
                return builder.create();
            } else {
                return null;
            }
        }

        @Override
        public void onDismiss(final DialogInterface dialog) {
            super.onDismiss(dialog);
            final Activity activity = getActivity();
            if (activity instanceof DialogInterface.OnDismissListener) {
                ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
            }
        }
    }

    public class DataStore {
        private SharedPreferences sp;
        private SharedPreferences.Editor editor;
        private Context _context;

        private final String SHARE_NAME = "Data Session";
        private final int MODE_PRIVATE = 0;

        private DataStore(Context context){
            this._context = context;
            sp = context.getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
            editor = sp.edit();
        }

        private void storeData(List<Article> data){
            List<Article> prevData = getData();
            if(prevData.size() == 0){
                editor.putInt("article_size",data.size());
                for(int i = 0; i < data.size(); i++){
                    putData(data.get(i), i);
                }
            } else{
                for(int i = 0; i < data.size(); i++){
                    if(!prevData.get(i).equals(data.get(i))){
                        putData(data.get(i),i);
                    }
                }
            }
            editor.apply();
        }

        private List<Article> getData() {
            int size = sp.getInt("article_size",0);
            List<Article> datax = new ArrayList<>();
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
            return datax;
        }

        private void putData(Article data, int i){
            editor.putString("getTitle_"+i, data.getTitle());
            editor.putString("getDescription_"+i, data.getDescription());
            editor.putString("getPublishedAt_"+i, formatDate(data.getPublishedAt()).toString());
            editor.putString("getUrlToImage_"+i, data.getUrlToImage());
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

        private void clearData(){
            editor.clear();
            editor.apply();
        }
    }
}
