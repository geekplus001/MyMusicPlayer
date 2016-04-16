package com.ben.mymusicplayer;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ben.mymusicplayer.adapter.NetMusicAdapter;
import com.ben.mymusicplayer.utils.AppUtils;
import com.ben.mymusicplayer.utils.Constant;
import com.ben.mymusicplayer.utils.SearchMusicUtils;
import com.ben.mymusicplayer.vo.SearchResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/30 0030.
 */
public class NetMusicListFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{


    private MainActivity mainActivity;
    private ListView listView_net_music;
    private LinearLayout load_layout;
    private LinearLayout ll_search_btn_container;
    private LinearLayout ll_search_container;
    private ImageButton ib_search_btn;
    private EditText et_search_content;
    private ArrayList<SearchResult> searchResults = new ArrayList<>();
    private NetMusicAdapter netMusicAdapter;
    private int page = 1;//搜索音乐的页码

    public static NetMusicListFragment newInstance() {
        NetMusicListFragment net = new NetMusicListFragment();
        return net;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //UI组件的初始化
        View view = inflater.inflate(R.layout.net_music_list,null);
        listView_net_music = (ListView) view.findViewById(R.id.listView_net_music);
        load_layout = (LinearLayout) view.findViewById(R.id.load_layout);
        ll_search_btn_container = (LinearLayout) view.findViewById(R.id.ll_search_btn_container);
        ll_search_container = (LinearLayout) view.findViewById(R.id.ll_search_container);
        ib_search_btn = (ImageButton) view.findViewById(R.id.ib_search_btn);
        et_search_content = (EditText) view.findViewById(R.id.et_search_content);

        listView_net_music.setOnItemClickListener(this);
        ll_search_btn_container.setOnClickListener(this);
        ib_search_btn.setOnClickListener(this);
        loadNetData();
        return view;
    }
    //加载网络音乐
    private void loadNetData() {
        load_layout.setVisibility(View.VISIBLE);//搜索等待
        //执行一个异步加载网络音乐的任务
        new LoadNetDataTask().execute(Constant.BAIDU_URL + Constant.BAIDU_DAYHOT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ll_search_btn_container:
                ll_search_btn_container.setVisibility(View.GONE);
                ll_search_container.setVisibility(View.VISIBLE);
                break;
            case R.id.ib_search_btn:
                //搜索事件处理
                searchMusic();
                break;
        }
    }

    private void searchMusic() {
        //隐藏输入法
        AppUtils.hideInputMethod(et_search_content);
        ll_search_btn_container.setVisibility(View.VISIBLE);
        ll_search_container.setVisibility(View.GONE);
        String key = et_search_content.getText().toString();
        if(TextUtils.isEmpty(key))
        {
            Toast.makeText(mainActivity,"请输入搜索信息",Toast.LENGTH_SHORT).show();
            return;
        }
        load_layout.setVisibility(View.VISIBLE);
        SearchMusicUtils.getsInstance().setListener(new SearchMusicUtils.OnSearchResultListener() {
            @Override
            public void onSearchResult(ArrayList<SearchResult> results) {
                ArrayList<SearchResult> sr = netMusicAdapter.getSearchResults();
                sr.clear();
                sr.addAll(results);
                netMusicAdapter.notifyDataSetChanged();
                load_layout.setVisibility(View.GONE);
            }
        }).search(key,page);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position>=netMusicAdapter.getSearchResults().size() || position<0)
            return;
        showDownloadDialog(position);
    }
    //下载弹窗
    private void showDownloadDialog(final int position) {
        DownloadDialogFragment downloadDialogFragment = DownloadDialogFragment.newInstance(searchResults.get(position));
        downloadDialogFragment.show(getFragmentManager(),"download");
    }


    /*
        加载音乐的异步任务
     */
    class LoadNetDataTask extends AsyncTask<String,Integer,Integer>
    /*
        Android的AsyncTask比Handler更轻量级一些，适用于简单的异步处理。首先明确Android之所以有Handler
        和AsyncTask，都是为了不阻塞主线程（UI线程），且UI的更新只能在主线程中完成，因此异步处理是不可避免的。

        要使用AsyncTask工作我们要提供三个泛型参数，并重载几个方法(至少重载一个)。
        AsyncTask定义了三种泛型类型 Params，Progress和Result。
        Params 启动任务执行的输入参数，比如HTTP请求的URL。
        Progress 后台任务执行的百分比。
        Result 后台执行任务最终返回的结果，比如String。
       使用过AsyncTask 的同学都知道一个异步加载数据最少要重写以下这两个方法：
       doInBackground(Params…) 后台执行，比较耗时的操作都可以放在这里。注意这里不能直接操作UI。此方法在后台线程执行，完成任务的主要工作，通常需要较长的时间。在执行过程中可以调用publicProgress(Progress…)来更新任务的进度。
       onPostExecute(Result)  相当于Handler 处理UI的方式，在这里面可以使用在doInBackground 得到的结果处理操作UI。 此方法在主线程执行，任务执行的结果作为此方法的参数返回
       有必要的话你还得重写以下这三个方法，但不是必须的：
       onProgressUpdate(Progress…)   可以使用进度条增加用户体验度。 此方法在主线程执行，用于显示任务执行的进度。
       onPreExecute()        这里是最终用户调用Excute时的接口，当任务执行之前开始调用此方法，可以在这里显示进度对话框。
       onCancelled()             用户调用取消时，要做的操作
       使用AsyncTask类，以下是几条必须遵守的准则：
       Task的实例必须在UI thread中创建；
       execute方法必须在UI thread中调用；
       不要手动的调用onPreExecute(), onPostExecute(Result)，doInBackground(Params...), onProgressUpdate(Progress...)这几个方法；
       该task只能被执行一次，否则多次调用时将会出现异常；
     */
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            load_layout.setVisibility(View.VISIBLE);
            listView_net_music.setVisibility(View.GONE);
            searchResults.clear();
        }
        @Override
        protected Integer doInBackground(String... params) {
            String url = params[0];
            try {
                //使用jsoup组件请求网络并解析音乐数据
                Document doc = Jsoup.connect(url).userAgent(Constant.USER_AGENT).timeout(6*1000).get();
                Elements songTitles = doc.select("span.song-title");
                Elements artists = doc.select("span.author_list");
                for(int i=0;i<songTitles.size();i++)
                {
                    SearchResult searchResult = new SearchResult();
                    Elements urls = songTitles.get(i).getElementsByTag("a");//获取a连接
                    searchResult.setUrl(urls.get(0).attr("href"));//a连接中第一个的href属性
                    searchResult.setMusicName(urls.get(0).text());
                    //
                    Elements artistElements = artists.get(i).getElementsByTag("a");
                    searchResult.setArtist(artistElements.get(0).text());
//                    searchResult.setArtist("歌手");
                    searchResult.setAlbum("热歌榜");
                    searchResults.add(searchResult);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }

            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer==1)
            {
                netMusicAdapter = new NetMusicAdapter(mainActivity,searchResults);
                listView_net_music.setAdapter(netMusicAdapter);
                listView_net_music.addFooterView(LayoutInflater.from(mainActivity).inflate(R.layout.footview_layout,null));
            }
            load_layout.setVisibility(View.GONE);
            listView_net_music.setVisibility(View.VISIBLE);
        }
    }
}
