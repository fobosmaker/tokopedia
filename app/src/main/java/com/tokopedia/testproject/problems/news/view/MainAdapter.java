package com.tokopedia.testproject.problems.news.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tokopedia.testproject.R;
import com.tokopedia.testproject.problems.news.model.Article;
import com.tokopedia.testproject.problems.news.model.Banner;
import com.tokopedia.testproject.problems.news.model.ButtonMore;
import com.tokopedia.testproject.problems.news.model.NewArticle;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter {
    private NewsActivity context;
    private ArrayList<Object> list;
    //private List<Article> newsData;
    private List<Banner> bannerData;
    private List<NewArticle> newsData;
    private final int menu_banner = 1;
    private final int menu_news = 2;
    private final int menu_button = 3;
    private static final String TAG = "MainAdapter";
    private boolean isScrollStatus = false;
    private boolean isScrollNews = false;

    public MainAdapter(List<NewArticle> newsData, List<Banner> bannerData, NewsActivity context) {
        list = new ArrayList<>(3);
        setNewsData(newsData);
        setBannerData(bannerData);

        this.context = context;
    }

    void setNewsData(List<NewArticle> newsData) {
        if(newsData.size() > 0) {
            list.add(newsData.get(0));
            this.newsData = newsData;
        }
    }

    void setBannerData(List<Banner> bannerData){
        if(bannerData.size() > 0){
            list.add(bannerData.get(0));
            this.bannerData = bannerData;
        }
    }

    void setMenu_button(ButtonMore button){
        list.add(button);
    }

    public ArrayList<Object> getList(){
        return list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
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
        NewArticleAdapter adapter = new NewArticleAdapter(null);
        holder.recyclerView.setAdapter(adapter);
        if(newsData.size() > 0){
            holder.recyclerView.setVisibility(View.VISIBLE);
            adapter.setNewArticleList(newsData);
            adapter.notifyDataSetChanged();
        } else {
            holder.recyclerView.setVisibility(View.GONE);
            context.showSnackbar("Empty Data");
        }
    }

    private void BannerView(final BannerViewholder holder){
        final LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(manager);
        holder.recyclerView.setHasFixedSize(true);
        BannerAdapter adapter = new BannerAdapter(null);
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
        /*holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.onMore("gomudikNews");
            }
        });*/
        /*MenuGoMudikNewsAdapter adapter = new MenuGoMudikNewsAdapter(context.getPlylst().body().getItems(), context);
        holder.recyclerView.setAdapter(adapter);*/
        //createDotsNews(0,holder.dotsLayout,5);
        /*holder.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isScrollNews && manager.findLastCompletelyVisibleItemPosition() >= 0) {
                    Log.d(TAG, "onScrolled: position "+manager.findLastCompletelyVisibleItemPosition());
                    createDotsNews(manager.findLastCompletelyVisibleItemPosition(), holder.dotsLayout, 5);
                }
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrollNews = true;
                }
            }

        });*/
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
        //private ImageButton arrow;
        //private LinearLayout dotsLayout;
        private BannerViewholder(View itemView){
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            //arrow = itemView.findViewById(R.id.arrow);
            //dotsLayout = itemView.findViewById(R.id.dotsLayout);
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
}
