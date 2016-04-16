package com.ben.mymusicplayer.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.ben.mymusicplayer.R;
import com.ben.mymusicplayer.vo.Mp3Info;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/3/30 0030.
 */
public class MediaUtils {
    //获取专辑封面的Uri
    private static final Uri albumArtUri  = Uri.parse("content://media/external/audio/albumart");

    /*
    根据歌曲id查询歌曲信息

     */
    public static Mp3Info getMp3Info(Context context,long _id)
    {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,
                MediaStore.Audio.Media._ID + "=" +_id,null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        Mp3Info mp3Info =  null;
        if(cursor.moveToNext())
        {
            mp3Info = new Mp3Info();
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));//音乐id
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            String album = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM));//专辑
            long albumId = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));//文件大小
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));//文件路径
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐
            if(isMusic != 0)//只把音乐添加到集合
            {
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setAlbum(album);
                mp3Info.setAlbumId(albumId);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
            }
        }
        cursor.close();
        return mp3Info;
    }
    /*
    用于从数据库中查询歌曲的信息，保存在List中

     */
    public static long[] getMp3InfoIds(Context context)
    {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID},
                MediaStore.Audio.Media.DURATION + ">=60000",null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        long[] ids = null;
        if(cursor != null)
        {
            int c = cursor.getCount();
            ids = new long[c];
            for(int i=0;i<c;i++)
            {
                cursor.moveToNext();
                ids[i] = cursor.getLong(0);
            }
        }
        cursor.close();
        return ids;
    }
    /*
    用于从数据库中查询歌曲的信息，保存在List中
     */
    public static ArrayList<Mp3Info> getMp3Infos(Context context)
    {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.DURATION + ">=60000",null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        ArrayList<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
        int c = cursor.getCount();
        for(int i=0;i<c;i++)
        {
            cursor.moveToNext();
            Mp3Info mp3Info = new Mp3Info();
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));//音乐id
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            String album = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM));//专辑
            long albumId = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));//文件大小
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));//文件路径
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐
            if(isMusic != 0)//只把音乐添加到集合
            {
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setAlbum(album);
                mp3Info.setAlbumId(albumId);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
                mp3Infos.add(mp3Info);
            }

        }
        cursor.close();
        return mp3Infos;
    }
    /*
    往List集合中添加Map对象数据，每一个Map对象存放益寿音乐的所有属性

     */
    public static List<HashMap<String,String>> getMusicMaps(List<Mp3Info> mp3Infos)
    {
        List<HashMap<String,String>> mp3list = new ArrayList<HashMap<String,String>>();
        for(Iterator iterator = mp3Infos.iterator();iterator.hasNext();)
        {
            Mp3Info mp3Info = (Mp3Info) iterator.next();
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("title",mp3Info.getTitle());
            map.put("Artist",mp3Info.getArtist());
            map.put("album",mp3Info.getAlbum());
            map.put("albumId",String.valueOf(mp3Info.getAlbumId()));
            map.put("duration",formatTime(mp3Info.getDuration()));
            map.put("size",String.valueOf(mp3Info.getSize()));
            map.put("url",mp3Info.getUrl());
            mp3list.add(map);
        }
        return mp3list;
    }
    /*
    格式化时间：将毫秒转化为 分：秒 格式
     */
    public static String formatTime(long time)
    {
        String min = time/(1000*60)+"";
        String sec = time%(1000*60)+"";
        if(min.length()<2)
        {
            min = "0" + time / (1000*60)+"";
        }
        else
        {
            min = time / (1000*60) + "";
        }
        if(sec.length()==4)
        {
            sec = "0" + (time % (1000*60)) + "";
        }
        else if(sec.length()==3)
        {
            sec = "00" + (time % (1000*60)) + "";
        }
        else if(sec.length()==2)
        {
            sec = "000" + (time % (1000*60)) + "";
        }
        else if(sec.length()==1)
        {
            sec = "0000" + (time % (1000*60)) + "";
        }
        return min + ":" + sec.trim().substring(0,2);
    }
    /*
        获取默认专辑图片
     */
    public static Bitmap getDefaultArtwork(Context context,boolean small)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        if(small)
        {
            //返回小图片
            return BitmapFactory.decodeStream(context.getResources()
            .openRawResource(R.mipmap.app_start2),null,options);
        }
        return BitmapFactory.decodeStream(context.getResources()
                .openRawResource(R.mipmap.app_start2),null,options);

    }

    /*
        从文件中获取专辑封面位图
     */
    private static Bitmap getArtworkFromFile(Context context,long songid,long albumid)
    {
        Bitmap bm = null;
        if(albumid < 0 && songid < 0)
        {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if(albumid < 0)
            {
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        +songid+"/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri,"r");
                if(pfd!=null)
                {
                    fd = pfd.getFileDescriptor();
                }
            }
            else
            {
                Uri uri = ContentUris.withAppendedId(albumArtUri,albumid);
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri,"r");
                if(pfd!=null)
                {
                    fd = pfd.getFileDescriptor();
                }
            }
            options.inSampleSize = 1;
            //只进行大小判断
            options.inJustDecodeBounds = true;
            //调用此方法得到options的到图片大小
            BitmapFactory.decodeFileDescriptor(fd,null,options);
            //我们的目标是在800pixel的画面上显示
            //所以需要调用computeSampleSize得到图片的缩放比例
            options.inSampleSize  = 100;
            //我们得到了缩放比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            //根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd,null,options);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }
    /*
        获取专辑封面位图对象
     */
    public static Bitmap getArtwork(Context context,long song_id,long album_id,boolean allowDefault,boolean small) {
        if (album_id<0)
        {
            if(song_id<0)
            {
                Bitmap bm = getArtworkFromFile(context,song_id,-1);
                if(bm!=null)
                {
                    return bm;
                }
            }
            if(allowDefault)
            {
                return getDefaultArtwork(context,small);
            }
            return null;
        }
        ContentResolver res =context.getContentResolver();
        Uri uri  = ContentUris.withAppendedId(albumArtUri,album_id);
        if(uri!=null)
        {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                //先制定原始大小
                options.inSampleSize = 1;
                //只进行大小判断
                options.inJustDecodeBounds = true;
                //调用此方法得到图片大小
                BitmapFactory.decodeStream(in,null,options);
                /*我们的目标是在你N pixel的画面上显示，所以需要调用computeSampleSize得到图片
                * 缩放比例，这里target为800是根据默认专辑图片大小决定的，
                * 800只是测试数字但是试验后发现完美的结合*/
                if(small)
                {
                    options.inSampleSize = computeSampleSize(options,40);
                }
                else
                {
                    options.inSampleSize = computeSampleSize(options,600);
                }
                //我们得到了缩放的比例，现在开始正式读入Bitmap数据
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in,null,options);
            } catch (FileNotFoundException e) {
                Bitmap bm = getArtworkFromFile(context,song_id,album_id);
                if(bm!=null)
                {
                    if(bm.getConfig()==null)
                    {
                        bm = bm.copy(Bitmap.Config.RGB_565,false);
                        if(bm==null && allowDefault)
                        {
                            return getDefaultArtwork(context,small);
                        }
                    }
                }
                else if(allowDefault)
                {
                    bm = getDefaultArtwork(context,small);
                }
                return bm;
            }
            finally {
                try {
                    if(in!=null)
                    {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /*
        对图片进行合适的缩放
     */
    private static int computeSampleSize(BitmapFactory.Options options, int i) {
        //获取位图的原尺寸
        int w = options.outWidth;
        int h = options.outHeight;
        int inSampleSize = 1;

        if(w>i || h>i)
        {
            if(w>h)
            {
                inSampleSize = Math.round((float)h/(float)i);
            }
            else
            {
                inSampleSize = Math.round((float)w/(float)i);
            }
        }

        return inSampleSize;
    }

}
