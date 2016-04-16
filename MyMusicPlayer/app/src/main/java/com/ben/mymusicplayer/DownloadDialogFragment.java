package com.ben.mymusicplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.ben.mymusicplayer.utils.DownloadUtils;
import com.ben.mymusicplayer.vo.SearchResult;

/**
 * Created by Administrator on 2016/4/3 0003.
 */
public class DownloadDialogFragment extends DialogFragment {
    private SearchResult searchResult;//当前要下载的歌曲对象
    private MainActivity mainActivity;
    public static DownloadDialogFragment newInstance(SearchResult searchResult)
    {
        DownloadDialogFragment downloadDialogFragment = new DownloadDialogFragment();
        downloadDialogFragment.searchResult = searchResult;
        return downloadDialogFragment;
    }
    private String[] items;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) getActivity();
        items = new String[]{"下载","取消"};
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setCancelable(true);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //执行下载
                        downloadMusic();
                        break;
                    case 1:
                        //取消
                        dialog.dismiss();
                        break;
                }
            }
        });
        return builder.show();
    }

    private void downloadMusic() {
        Toast.makeText(mainActivity,"正在下载"+searchResult.getMusicName(),Toast.LENGTH_SHORT).show();
        DownloadUtils.getInstance().setListener(new DownloadUtils.OnDownloadListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void OnDownload(String mp3Url) {
                Toast.makeText(mainActivity, searchResult.getMusicName() + "下载成功", Toast.LENGTH_SHORT).show();
                //扫描新下载的歌曲
//                Uri contentUri = Uri.fromFile(new File(mp3Url));
//                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri);
//                getContext().sendBroadcast(mediaScanIntent);
            }

            @Override
            public void OnFailed(String error) {
                Toast.makeText(mainActivity, error, Toast.LENGTH_SHORT).show();
            }
        }).download(searchResult);
    }



}
