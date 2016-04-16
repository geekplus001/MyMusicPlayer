package com.ben.mymusicplayer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ben.mymusicplayer.utils.MediaUtils;
import com.ben.mymusicplayer.vo.Mp3Info;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import src.douzi.android.view.LrcView;

public class MusicPlayActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,ViewPager.OnPageChangeListener {

    private TextView textView_songName, textView_nowTime,
            textView_allTime,textView_title;
    private ImageView imageView_songImage, imageView_playMode, imageView_playPre,
            imageView_playNow, imageView_playNext, imageView_love,imageView_album;
    private SeekBar seekBar;
//    private ArrayList<Mp3Info> mp3Infos;

    private int position;

    private static final int UPDATE_TIME = 0x10;//更新播放时间的标记
    //    private boolean isPause = false;
    private ViewPager viewPager;
    private LrcView lrcView;
    private ArrayList<View> views =  new ArrayList<>();
    private static final int UPDATE_LRC = 0x20;//更新歌词

    private BenPlayerApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT > 18) {

            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }


        setContentView(R.layout.activity_music_play);
        View view = getLayoutInflater().inflate(R.layout.activity_music_play,null);
        views.add(view);

        app = (BenPlayerApp) getApplication();

        textView_songName = (TextView) findViewById(R.id.textView_songName);
        textView_nowTime = (TextView) findViewById(R.id.textView_nowTime);
        textView_allTime = (TextView) findViewById(R.id.textView_allTime);
        imageView_songImage = (ImageView) findViewById(R.id.imageView_songImage);
        imageView_playMode = (ImageView) findViewById(R.id.imageView_playMode);
        imageView_playPre = (ImageView) findViewById(R.id.imageView_playPre);
        imageView_playNow = (ImageView) findViewById(R.id.imageView_playNow);
        imageView_playNext = (ImageView) findViewById(R.id.imageView_playNext);
        imageView_love = (ImageView) findViewById(R.id.imageView_loveSong);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        initViewPager();

        imageView_playNow.setOnClickListener(this);
        imageView_playPre.setOnClickListener(this);
        imageView_playNext.setOnClickListener(this);
        imageView_playMode.setOnClickListener(this);
        imageView_love.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);

        myHandler = new MyHandler(this);
    }

//    private void initViewPager() {
//        View album_image_layout = getLayoutInflater().inflate(R.layout.album_image_layout,null);
//        imageView_album = (ImageView) album_image_layout.findViewById(R.id.imageview_album);
//        textView_title = (TextView) album_image_layout.findViewById(R.id.textView_title);
//        views.add(album_image_layout);
//        View lrc_layout = getLayoutInflater().inflate(R.layout.lrc_layout,null);
//        lrcView = (LrcView) lrc_layout.findViewById(R.id.lrcView);
//        //设置滚动事件
//        lrcView.setListener(new ILrcView.LrcViewListener() {
//            @Override
//            public void onLrcSeeked(int newPosition, LrcRow row) {
//                if(playService.isPlaying())
//                {
//                    playService.seekTo((int) row.time);
//                }
//            }
//        });
//        lrcView.setLoadingTipText("正在加载歌词");
//        lrcView.setBackgroundResource(R.mipmap.app_start2);
//        lrcView.getBackground().setAlpha(150);//设置透明度0~255
//        views.add(lrc_layout);
//        viewPager.setAdapter(new MyPagerAdapter());
//        viewPager.addOnPageChangeListener(this);
//    }
//    //加载歌词
//    private void loadLRC(File lrcFile)
//    {
//        StringBuffer buf = new StringBuffer(1024*10);
//        char[] chars = new char[1024];
//        try {
//            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(lrcFile)));
//            int len = -1;
//            while((len=in.read(chars))!=-1)
//            {
//                buf.append(chars,0,len);
//            }
//            in.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ILrcBuilder builder = new DefaultLrcBuilder();
//        List<LrcRow> rows = builder.getLrcRows(buf.toString());
//        lrcView.setLrc(rows);
//
////        long id = mp3Info.getMp3InfoId()==0?mp3Info.getId():mp3Info.getMp3InfoId();
////        Bitmap bg = MediaUtils.getArtwork(this,id,mp3Info.getAlbumId(),false,false);
////        if(bg!=null)
////        {
////            lrcView.setBackground(new BitmapDrawable(getResources(),bg));
////            lrcView.getBackground().setAlpha(150);
////        }
//    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class MyPagerAdapter extends PagerAdapter{



        @Override
        public int getCount() {
            return views.size();
        }
        // 实例化选项卡
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = views.get(position);
            container.addView(v);
            return v;
        }
        // 删除选项卡
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
        // 判断视图是否为返回的对象
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindPlayService();
    }


    private static MyHandler myHandler;

    //滚动条拖动
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
//            playService.pause();
            playService.seekTo(progress);
//            playService.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


    static class MyHandler extends Handler {

        private MusicPlayActivity musicPlayActivity;
        private WeakReference<MusicPlayActivity> weak;

        public MyHandler(MusicPlayActivity musicPlayActivity) {
            weak = new WeakReference<MusicPlayActivity>(musicPlayActivity);
//            this.musicPlayActivity = musicPlayActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            musicPlayActivity = weak.get();
            if (musicPlayActivity != null) {
                switch (msg.what) {
                    case UPDATE_TIME:
                        musicPlayActivity.textView_nowTime.setText(MediaUtils.formatTime(msg.arg1));
                        break;
//                    case UPDATE_LRC:
//                        musicPlayActivity.lrcView.seekLrcToTime((int) msg.obj);
//                        break;
//                    case DownloadUtils.SUCCESS_LRC:
//                        musicPlayActivity.loadLRC(new File((String) msg.obj));
//                        break;
//                    case DownloadUtils.FAILED_LRC:
//                        Toast.makeText(musicPlayActivity,"歌词下载失败",Toast.LENGTH_SHORT).show();
//                        break;
                    default:
                        break;
                }
            }
        }
    }

    //此处可用一个软引用
    @Override
    public void publish(int progress) {

//        textView_nowTime.setText(MediaUtils.formatTime(progress));
//        Message msg = myHandler.obtainMessage(UPDATE_TIME);
//        msg.arg1 = progress;
//        myHandler.sendMessage(msg);
        myHandler.obtainMessage(UPDATE_TIME, progress).sendToTarget();
        seekBar.setProgress(progress);
        myHandler.obtainMessage(UPDATE_LRC,progress).sendToTarget();
    }

    @Override
    public void change(int position) {
//        //如果在播放
//        if(this.playService.isPlaying())
//        {
        Mp3Info mp3Info = playService.mp3Infos.get(position);
        //跟新歌名
        textView_songName.setText(mp3Info.getTitle());
        //更新album图片
        Bitmap albumBitmap = MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, false);
        imageView_songImage.setImageBitmap(albumBitmap);
        //总时间
        textView_allTime.setText(MediaUtils.formatTime(mp3Info.getDuration()));
        //更新播放按钮
        imageView_playNow.setImageResource(R.mipmap.player_btn_pause_normal);
        //更新进度
        seekBar.setProgress(0);
        seekBar.setMax((int) mp3Info.getDuration());
        if (playService.isPlaying()) {
            imageView_playNow.setImageResource(R.mipmap.player_btn_pause_normal);
        } else {
            imageView_playNow.setImageResource(R.mipmap.player_btn_play_normal);
        }
//        }
        //记忆上次退出时候需要设置图标
        switch (playService.getPlay_mode()) {
            case PlayService.ORDER_PLAY:
                imageView_playMode.setImageResource(android.R.drawable.ic_menu_sort_by_size);
//                imageView_playMode.setTag(PlayService.ORDER_PLAY);
                break;
            case PlayService.RANDOM_PLAY:
                imageView_playMode.setImageResource(android.R.drawable.ic_menu_help);
//                imageView_playMode.setTag(PlayService.RANDOM_PLAY);
                break;
            case PlayService.SINGLE_PLAY:
                imageView_playMode.setImageResource(android.R.drawable.checkbox_on_background);
//                imageView_playMode.setTag(PlayService.SINGLE_PLAY);
                break;
        }
        long id = getId(mp3Info);
        //初始化收藏状态
        try {
            Mp3Info loveMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId", "=", id));
            if (loveMp3Info != null && loveMp3Info.getIsLike()==1) {
//                if(loveMp3Info.getIsLike()==1 )
//                {
                    imageView_love.setImageResource(android.R.drawable.star_big_on);
//                }
//                else
//                {
//                    imageView_love.setImageResource(android.R.drawable.star_big_off);
//                }
            }
            else
            {
                imageView_love.setImageResource(android.R.drawable.star_big_off);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        //歌词
//        String songName = mp3Info.getTitle();
//        String lrcPath = Environment.getExternalStorageDirectory()+ Constant.DIR_LRC+"/"+songName+".lrc";
//        File lrcFile = new File(lrcPath);
//        if(!lrcFile.exists())
//        {
//            //下载
//            SearchMusicUtils.getsInstance().setListener(new SearchMusicUtils.OnSearchResultListener() {
//                @Override
//                public void onSearchResult(ArrayList<SearchResult> results) {
//
//                    SearchResult searchResult = results.get(0);
//                    String url = Constant.BAIDU_URL+searchResult.getUrl();
//                    DownloadUtils.getInstance().downloadLRC(url,searchResult.getMusicName(),myHandler);
//                }
//            }).search(songName+" "+mp3Info.getArtist(),1);
//        }
//        else
//        {
//            loadLRC(lrcFile);
//        }
    }

    private long getId(Mp3Info mp3Info) {
        long id = 0;
        switch (playService.getChangePlayList()) {
            case PlayService.MY_MUSIC_LIST:
                id = mp3Info.getId();
                break;
            case PlayService.LOVE_MUSIC_LIST:
                id = mp3Info.getMp3InfoId();
                break;
            default:
                break;
        }
        return id;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_playNow:
                if (playService.isPlaying()) {
                    imageView_playNow.setImageResource(R.mipmap.player_btn_play_normal);
                    playService.pause();
                } else {
                    if (playService.isPause()) {
                        imageView_playNow.setImageResource(R.mipmap.player_btn_pause_normal);
                        playService.start();
                    } else {
                        playService.play(playService.getCurrentPosition());
                    }
                }
                break;
            case R.id.imageView_playPre:
                playService.prev();
                break;
            case R.id.imageView_playNext:
                playService.next();
                break;
            case R.id.imageView_playMode:
//                int mode = (int) imageView_playMode.getTag();
                switch (playService.getPlay_mode()) {
                    case PlayService.ORDER_PLAY:
                        imageView_playMode.setImageResource(android.R.drawable.ic_menu_help);
//                        imageView_playMode.setTag(PlayService.RANDOM_PLAY);
                        playService.setPlay_mode(PlayService.RANDOM_PLAY);
                        Toast.makeText(MusicPlayActivity.this, getString(R.string.randomp_lay), Toast.LENGTH_SHORT).show();
                        break;
                    case PlayService.RANDOM_PLAY:
                        imageView_playMode.setImageResource(android.R.drawable.checkbox_on_background);
//                        imageView_playMode.setTag(PlayService.SINGLE_PLAY);
                        playService.setPlay_mode(PlayService.SINGLE_PLAY);
                        Toast.makeText(MusicPlayActivity.this, getString(R.string.single_play), Toast.LENGTH_SHORT).show();
                        break;
                    case PlayService.SINGLE_PLAY:
                        imageView_playMode.setImageResource(android.R.drawable.ic_menu_sort_by_size);
//                        imageView_playMode.setTag(PlayService.ORDER_PLAY);
                        playService.setPlay_mode(PlayService.ORDER_PLAY);
                        Toast.makeText(MusicPlayActivity.this, getString(R.string.order_play), Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case R.id.imageView_loveSong:
                Mp3Info mp3Info = playService.mp3Infos.get(playService.getCurrentPosition());
                try {
                    Mp3Info loveMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId", "=", getId(mp3Info)));
                    if (loveMp3Info == null)
                    {
                        mp3Info.setMp3InfoId(mp3Info.getId());
                        mp3Info.setIsLike(1);
                        app.dbUtils.save(mp3Info);
                        imageView_love.setImageResource(android.R.drawable.star_big_on);
                        Toast.makeText(MusicPlayActivity.this, getString(R.string.lovehint), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        int isLike = loveMp3Info.getIsLike();
                        if (isLike == 1)
                        {
                            loveMp3Info.setIsLike(0);
                            imageView_love.setImageResource(android.R.drawable.star_big_off);
                            Toast.makeText(MusicPlayActivity.this, getString(R.string.unlovehint), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loveMp3Info.setIsLike(1);
                            imageView_love.setImageResource(android.R.drawable.star_big_on);
                            Toast.makeText(MusicPlayActivity.this, getString(R.string.lovehint), Toast.LENGTH_SHORT).show();
                        }
                        app.dbUtils.update(loveMp3Info, "isLike");
                    }
                }
                catch (DbException e)
                {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
