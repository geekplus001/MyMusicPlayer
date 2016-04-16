package com.ben.mymusicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ben.mymusicplayer.R;
import com.ben.mymusicplayer.vo.SearchResult;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/3 0003.
 */
public class NetMusicAdapter extends BaseAdapter {

    private Context ctx;
    private ArrayList<SearchResult> searchResults;
    public NetMusicAdapter(Context ctx,ArrayList<SearchResult> searchResults)
    {
        this.ctx = ctx;
        this.searchResults = searchResults;
    }


    public ArrayList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public int getCount() {
        return searchResults.size();
    }

    @Override
    public Object getItem(int position) {
        return searchResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null)
        {
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_net_list,null);
            vh = new ViewHolder();
            vh.textView_title = (TextView) convertView.findViewById(R.id.textView2_songName);
            vh.textView_singer = (TextView) convertView.findViewById(R.id.textView2_singer);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
        SearchResult result = searchResults.get(position);
        vh.textView_title.setText(result.getMusicName());
        vh.textView_singer.setText(result.getArtist());
        return convertView;
    }
    static class ViewHolder{
        TextView textView_title;
        TextView textView_singer;
    }
}
