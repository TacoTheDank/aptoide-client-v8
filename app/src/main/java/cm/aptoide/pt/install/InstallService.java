/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 04/10/2016.
 */

package cm.aptoide.pt.install;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BaseService;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.file.CacheHelper;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.repository.RepositoryFactory;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.Single;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class InstallService extends BaseService {

  public static final String TAG = "InstallService";

  public static final String ACTION_OPEN_DOWNLOAD_MANAGER = "OPEN_DOWNLOAD_MANAGER";
  public static final String ACTION_OPEN_APP_VIEW = "OPEN_APP_VIEW";
  public static final String ACTION_STOP_INSTALL = "STOP_INSTALL";
  public static final String ACTION_STOP_ALL_INSTALLS = "STOP_ALL_INSTALLS";
  public static final String ACTION_START_INSTALL = "START_INSTALL";
  public static final String ACTION_INSTALL_FINISHED = "INSTALL_FINISHED";
  public static final String EXTRA_INSTALLATION_MD5 = "INSTALLATION_MD5";
  public static final String EXTRA_INSTALLER_TYPE = "INSTALLER_TYPE";
  public static final String EXTRA_FORCE_DEFAULT_INSTALL = "EXTRA_FORCE_DEFAULT_INSTALL";
  public static final String EXTRA_SET_PACKAGE_INSTALLER = "EXTRA_SET_PACKAGE_INSTALLER";
  public static final int INSTALLER_TYPE_DEFAULT = 0;
  public static final String FILE_MD5_EXTRA = "APTOIDE_APPID_EXTRA";
  static public final int PROGRESS_MAX_VALUE = 100;

  private static final int NOTIFICATION_ID = 8;

  @Inject AptoideDownloadManager downloadManager;
  @Inject @Named("default") Installer defaultInstaller;
  @Inject InstalledRepository installedRepository;
  @Inject DownloadAnalytics downloadAnalytics;
  @Inject CacheHelper cacheManager;
  private InstallManager installManager;
  private CompositeSubscription subscriptions;
  private Notification notification;
  private PublishSubject<String> openAppViewAction;
  private PublishSubject<Void> openDownloadManagerAction;

  @Override public void onCreate() {

    super.onCreate();
    getApplicationComponent().inject(this);
    Logger.getInstance()
        .d(TAG, "Install service is starting");
    final AptoideApplication application = (AptoideApplication) getApplicationContext();
    installManager = application.getInstallManager();
    subscriptions = new CompositeSubscription();
    openDownloadManagerAction = PublishSubject.create();
    openAppViewAction = PublishSubject.create();
    installedRepository = RepositoryFactory.getInstalledRepository(getApplicationContext());
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    /*if (intent != null) {
      String md5 = intent.getStringExtra(EXTRA_INSTALLATION_MD5);
      if (ACTION_START_INSTALL.equals(intent.getAction())) {
        Logger.getInstance()
            .d(TAG, "Observing download and install with an intent");
        subscriptions.add(downloadAndInstall(md5).subscribe(hasNext -> treatNext(hasNext),
            throwable -> removeNotificationAndStop()));
      } else if (ACTION_STOP_INSTALL.equals(intent.getAction())) {
        subscriptions.add(stopDownload(md5).subscribe(hasNext -> treatNext(hasNext),
            throwable -> removeNotificationAndStop()));
      } else if (ACTION_OPEN_APP_VIEW.equals(intent.getAction())) {
        openAppViewAction.onNext(md5);
      } else if (ACTION_OPEN_DOWNLOAD_MANAGER.equals(intent.getAction())) {
        openDownloadManagerAction.onNext(null);
      } else if (ACTION_STOP_ALL_INSTALLS.equals(intent.getAction())) {
        stopAllDownloads();
      }
    } else {
      Logger.getInstance()
          .d(TAG, "Observing current download and installation without an intent");
      subscriptions.add(downloadAndInstallCurrentDownload().subscribe(hasNext -> treatNext(hasNext),
          throwable -> removeNotificationAndStop()));
    }*/
    return START_STICKY;
  }

  @Override public void onDestroy() {
    Logger.getInstance()
        .d(this.getClass()
            .getName(), "InstallService.onDestroy");
    subscriptions.unsubscribe();
    openAppViewAction = null;
    openDownloadManagerAction = null;
    super.onDestroy();
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  private Observable<Boolean> stopDownload(String md5) {
    return downloadManager.pauseDownload(md5)
        .andThen(hasNextDownload());
  }

  private void stopAllDownloads() {
    downloadManager.pauseAllDownloads();
    //removeNotificationAndStop();
  }

  private void treatNext(boolean hasNext) {
    if (!hasNext) {
      //removeNotificationAndStop();
      subscriptions.add(cacheManager.cleanCache()
          .toSingle()
          .flatMap(cleaned -> downloadManager.invalidateDatabase()
              .andThen(Single.just(cleaned)))
          .subscribe(__ -> {
          }, throwable -> CrashReport.getInstance()
              .log(throwable)));
    }
  }

  private Observable<Boolean> downloadAndInstallCurrentDownload() {
    return downloadManager.getCurrentInProgressDownload()
        .first()
        .flatMap(currentDownload -> downloadAndInstall(currentDownload.getMd5()));
  }

  private Observable<Boolean> downloadAndInstall(String md5) {
    return downloadManager.getDownload(md5)
        .doOnNext(download -> {
          stopOnDownloadError(download.getOverallDownloadStatus());
        })
        .doOnNext(download -> Logger.getInstance()
            .d(TAG, "received download: "
                + download.getPackageName()
                + " state: "
                + download.getOverallDownloadStatus()))
        .first(download -> download.getOverallDownloadStatus() == Download.COMPLETED)
        .doOnNext(__ -> Logger.getInstance()
            .d(TAG, "test se recebe completeds antes"))
        .map(__ -> true);
  }

  private void stopOnDownloadError(int downloadStatus) {
    if (downloadStatus == Download.ERROR) {
      //removeNotificationAndStop();
    }
  }

  private Observable<Boolean> hasNextDownload() {
    return downloadManager.getCurrentActiveDownloads()
        .first()
        .map(downloads -> downloads != null && !downloads.isEmpty());
  }

  @NonNull private NotificationCompat.Action getPauseAction(int requestCode, String md5) {
    Bundle appIdExtras = new Bundle();
    appIdExtras.putString(FILE_MD5_EXTRA, md5);
    return getAction(cm.aptoide.pt.downloadmanager.R.drawable.media_pause,
        getString(cm.aptoide.pt.downloadmanager.R.string.pause_download), requestCode,
        ACTION_STOP_INSTALL, md5);
  }

  @NonNull private NotificationCompat.Action getDownloadManagerAction(int requestCode, String md5) {
    Bundle appIdExtras = new Bundle();
    appIdExtras.putString(FILE_MD5_EXTRA, md5);
    return getAction(R.drawable.ic_manager, getString(R.string.open_apps_manager), requestCode,
        ACTION_OPEN_DOWNLOAD_MANAGER, md5);
  }

 /* private Notification buildNotification(String appName, int progress, boolean isIndeterminate,
      NotificationCompat.Action pauseAction, NotificationCompat.Action openDownloadManager,
      PendingIntent contentIntent) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setSmallIcon(android.R.drawable.stat_sys_download)
        .setContentTitle(String.format(Locale.ENGLISH,
            getResources().getString(cm.aptoide.pt.downloadmanager.R.string.aptoide_downloading),
            getString(R.string.app_name)))
        .setContentText(new StringBuilder().append(appName)
            .append(" - ")
            .append(getString(cm.aptoide.pt.database.R.string.download_progress)))
        .setContentIntent(contentIntent)
        .setProgress(PROGRESS_MAX_VALUE, progress, isIndeterminate)
        .addAction(pauseAction)
        .addAction(openDownloadManager);
    return builder.build();
  }*/

  private NotificationCompat.Action getAction(int icon, String title, int requestCode,
      String action, String md5) {
    return new NotificationCompat.Action(icon, title, getPendingIntent(requestCode, action, md5));
  }

  private PendingIntent getPendingIntent(int requestCode, String action, String md5) {
    Intent intent = new Intent(this, InstallService.class);
    if (!TextUtils.isEmpty(md5)) {
      final Bundle bundle = new Bundle();
      bundle.putString(EXTRA_INSTALLATION_MD5, md5);
      intent.putExtras(bundle);
    }
    return PendingIntent.getService(this, requestCode, intent.setAction(action),
        PendingIntent.FLAG_ONE_SHOT);
  }

/*  @Override public Observable<String> handleOpenAppView() {
    return openAppViewAction;
  }

  @Override public Observable<Void> handleOpenDownloadManager() {
    return openDownloadManagerAction;
  }

  @Override public void openAppView(String md5) {
    Intent intent = createDeeplinkingIntent();
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_MD5_KEY, md5);
    startActivity(intent);
  }

  @Override public void openDownloadManager() {
    Intent intent = createDeeplinkingIntent();
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.FROM_DOWNLOAD_NOTIFICATION, true);
    startActivity(intent);
  }

  @Override
  public void setupNotification(String md5, String appName, int progress, boolean isIndeterminate) {
    int requestCode = md5.hashCode();

    NotificationCompat.Action downloadManagerAction = getDownloadManagerAction(requestCode, md5);
    PendingIntent appViewPendingIntent = getPendingIntent(requestCode, ACTION_OPEN_APP_VIEW, md5);
    NotificationCompat.Action pauseAction = getPauseAction(requestCode, md5);

    if (notification == null) {
      notification =
          buildNotification(appName, progress, isIndeterminate, pauseAction, downloadManagerAction,
              appViewPendingIntent);
    } else {
      long oldWhen = notification.when;
      notification =
          buildNotification(appName, progress, isIndeterminate, pauseAction, downloadManagerAction,
              appViewPendingIntent);
      notification.when = oldWhen;
    }

    startForeground(NOTIFICATION_ID, notification);
  }

  @Override public void removeNotificationAndStop() {
    stopForeground(true);
    stopSelf();
  }*/

  @NonNull private Intent createDeeplinkingIntent() {
    Intent intent = new Intent();
    intent.setClass(getApplicationContext(), AptoideApplication.getActivityProvider()
        .getMainActivityFragmentClass());
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    return intent;
  }
}
