package it.univaq.khestodocente.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

/**
 * Created by beniamino on 23/09/15.
 */
public class Downloader {

    private DownloadManager downloadManager;

    private Long myDownloadReference;

    private BroadcastReceiver recieverDownloadComplete;

    private BroadcastReceiver recieverNotificationClicked;

    private Activity mActivity;


    public Downloader(Activity activity) {
        mActivity=activity;
        downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

    }

    public void download (it.univaq.khestodocente.model.File file) {

        String url = file.getUrl();
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        String filename = file.getFilename();
        request.setTitle(filename);
        request.setDescription("Khe-Sto");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        request.setVisibleInDownloadsUi(true);
        myDownloadReference = downloadManager.enqueue(request);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        recieverNotificationClicked = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String extraId = DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
                long [] references = intent.getLongArrayExtra(extraId);
                for (long reference : references)
                {
                    if (reference == myDownloadReference)
                    {

                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        recieverDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if (myDownloadReference == reference)
                {
                    //
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor cursor = downloadManager.query(query);

                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);

                    int fileNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                    String savedFilePath = cursor.getString(fileNameIndex);

                    int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                    int reason = cursor.getInt(columnReason);


                }
            }
        };

    }
}
