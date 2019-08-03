package com.tokopedia.testproject.problems.news.view;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tokopedia.testproject.R;
import com.tokopedia.testproject.problems.news.model.Banner;
import com.tokopedia.testproject.problems.news.model.ButtonMore;
import com.tokopedia.testproject.problems.news.model.NewArticle;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter {
    private NewsActivity context;
    private ArrayList<Object> list;
    private List<Banner> bannerData;
    private List<NewArticle> newsData;
    private final int menu_banner = 1;
    private final int menu_news = 2;
    private final int menu_button = 3;
    private Boolean isScroll = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private NewArticleAdapter newArticleAdapter;
    private static final String TAG = "MainAdapter";


    public MainAdapter(ArrayList<Object> list, List<NewArticle> newsData, List<Banner> bannerData, NewsActivity context) {
        this.list = list;
        setNewsData(newsData);
        setBannerData(bannerData);

        this.context = context;
    }

    void setNewsData(List<NewArticle> newsData) {
        if(newsData.size() > 0) {
            this.newsData = newsData;
        }
    }

    void setBannerData(List<Banner> bannerData){
        if(bannerData.size() > 0){
            this.bannerData = bannerData;
        }
    }

    void setMenu_button(ButtonMore button){
        list.add(button);
    }

    public ArrayList<Object> getList(){
        return list;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType){
            case menu_news:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_news,parent,false);
                return new NewsViewholder(v);
            case menu_banner:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_banner,parent,false);
                return new BannerViewholder(v);
            case menu_button:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_more,parent,false);
                return new MoreViewholder(v);
            case -1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_news,parent,false);
                return new defaultViewholder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NotNull final RecyclerView.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: "+newsData.size());
        switch (holder.getItemViewType()){
            case menu_news:
                NewsView((NewsViewholder)holder);
                break;
            case menu_banner:
                BannerView((BannerViewholder)holder);
                break;
            case menu_button:
                MoreView((MoreViewholder)holder, position);
                break;
        }
    }

    private void NewsView(final NewsViewholder holder){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, OrientationHelper.VERTICAL,false);
        holder.recyclerView.setLayoutManager(linearLayoutManager);
        newArticleAdapter = new NewArticleAdapter(null, context);
        holder.recyclerView.setAdapter(newArticleAdapter);
        if(newsData.size() > 0){
            Log.d(TAG, "NewsView: run with data "+newsData.size());
            holder.recyclerView.setVisibility(View.VISIBLE);
            newArticleAdapter.setNewArticleList(newsData);
            newArticleAdapter.notifyDataSetChanged();
        } else {
            holder.recyclerView.setVisibility(View.GONE);
            context.showSnackbar("Empty Data");
        }
    }

    private void BannerView(final BannerViewholder holder){
        final LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        final int position = manager.findLastCompletelyVisibleItemPosition();
        final Runnable SCROLLING_RUNNABLE = new Runnable() {
            @Override
            public void run() {
                //holder.recyclerView.smoothScrollBy(30, 0);
                holder.recyclerView.scrollToPosition(position);
                createDotsBanner(position,holder.dotsLayout);
                mHandler.postDelayed(this, 5000);
            }
        };
        holder.recyclerView.setLayoutManager(manager);
        holder.recyclerView.setHasFixedSize(true);
        final BannerAdapter adapter = new BannerAdapter(null);
        holder.recyclerView.setAdapter(adapter);
        if(bannerData.size() > 0){
            holder.recyclerView.setVisibility(View.VISIBLE);
            adapter.setBannerList(bannerData);
            adapter.notifyDataSetChanged();
        } else {
            holder.recyclerView.setVisibility(View.GONE);
            context.showSnackbar("Empty Data");
        }
        SnapHelper snapHelper = new PagerSnapHelper();
        holder.recyclerView.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(holder.recyclerView);
        createDotsBanner(0,holder.dotsLayout);
        holder.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*if(isScroll){
                    if(manager.findLastCompletelyVisibleItemPosition() >= 0){
                        createDotsBanner(manager.findLastCompletelyVisibleItemPosition(), holder.dotsLayout);
                    }
                } else {*/
                    if(position == manager.getItemCount()-1){
                        mHandler.removeCallbacks(SCROLLING_RUNNABLE);
                        Handler postHandler = new Handler();
                        postHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.recyclerView.setAdapter(null);
                                holder.recyclerView.setAdapter(adapter);
                                createDotsBanner(position, holder.dotsLayout);
                                mHandler.postDelayed(SCROLLING_RUNNABLE, 5000);
                            }
                        }, 5000);
                    }
                    mHandler.postDelayed(SCROLLING_RUNNABLE,5000);
                //}
            }
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScroll = true;
                }
            }

        });
    }

    private void MoreView(final MoreViewholder holder, final int position){
        ButtonMore data = (ButtonMore) list.get(position);
        holder.buttonText.setText(data.getTitle());
        holder.buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.buttonMore.setVisibility(View.GONE);
                list.remove(position);
                context.moreNews();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if( list.get(position) instanceof NewArticle)
            return menu_news;
        if( list.get(position) instanceof Banner)
            return menu_banner;
        if( list.get(position) instanceof ButtonMore)
            return menu_button;
        else
            return -1;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class NewsViewholder extends RecyclerView.ViewHolder{
        private RecyclerView recyclerView;
        private NewsViewholder(View itemView){
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }

    private class BannerViewholder extends RecyclerView.ViewHolder{
        private RecyclerView recyclerView;
        private LinearLayout dotsLayout;
        private BannerViewholder(View itemView){
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            dotsLayout = itemView.findViewById(R.id.dotsLayout);
        }
    }

    private class MoreViewholder extends RecyclerView.ViewHolder{
        private RelativeLayout buttonMore;
        private TextView buttonText;
        private MoreViewholder(View itemView){
            super(itemView);
            buttonMore = itemView.findViewById(R.id.buttonMore);
            buttonText = itemView.findViewById(R.id.buttonText);
        }
    }

    private class defaultViewholder extends RecyclerView.ViewHolder{
        private defaultViewholder(View itemView){
            super(itemView);
        }
    }

    private void createDotsBanner(int position, LinearLayout dotsLayout){
        int limit = 5;
        if(dotsLayout != null){
            dotsLayout.removeAllViews();
        }
        ImageView[] dots = new ImageView[limit];
        for (int i = 0; i < limit; i++) {
            dots[i] = new ImageView(context);
            if (i == position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.circle_active));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.circle_default));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);

            //assert dotsLayout != null;
            dotsLayout.addView(dots[i], params);
        }
    }

    public void searchFilter(List<NewArticle> data){
        Log.d(TAG, "searchFilter: Before Search "+newsData.size());
        Log.d(TAG, "searchFilter: total "+data.size());
        setNewsData(data);
        Log.d(TAG, "searchFilter: After search "+newsData.size());
        newArticleAdapter.notifyDataSetChanged();
    }
}
