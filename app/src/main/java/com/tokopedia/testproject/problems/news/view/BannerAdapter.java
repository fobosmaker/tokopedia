package com.tokopedia.testproject.problems.news.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tokopedia.testproject.R;
import com.tokopedia.testproject.problems.news.model.Banner;
import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.NewsViewHolder> {

    private List<Banner> bannerList;

    BannerAdapter(List<Banner> bannerList) {
        setBannerList(bannerList);
    }

    void setBannerList(List<Banner> bannerList) {
        if (bannerList == null) this.bannerList = new ArrayList<>();
        else this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { return new NewsViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_banner, viewGroup, false)); }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder newsViewHolder, int i) { newsViewHolder.bind(bannerList.get(i)); }

    @Override
    public int getItemCount() { return bannerList.size(); }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvTitle;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        void bind(Banner banner) {
            Glide.with(itemView).load(banner.getUrlToImage()).into(imageView);
            tvTitle.setText(banner.getTitle());
        }
    }
}
