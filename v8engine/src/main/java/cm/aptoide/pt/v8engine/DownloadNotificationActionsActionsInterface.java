package cm.aptoide.pt.v8engine;

import android.content.Intent;
import android.support.annotation.NonNull;
import cm.aptoide.pt.downloadmanager.interfaces.DownloadNotificationActionsInterface;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.receivers.DeepLinkIntentReceiver;

/**
 * Created by trinkes on 6/28/16.
 */
@Deprecated public class DownloadNotificationActionsActionsInterface
    implements DownloadNotificationActionsInterface {

  private static final String TAG =
      DownloadNotificationActionsActionsInterface.class.getSimpleName();

  @Override public void button1Pressed() {
    Intent intent = createDeeplinkingIntent();
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.FROM_DOWNLOAD_NOTIFICATION, true);
    Application.getContext().startActivity(intent);
  }

  @Override public void notificationPressed(String md5) {
    Intent intent = createDeeplinkingIntent();
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_MD5_KEY, md5);
    Application.getContext().startActivity(intent);
  }

  @NonNull private Intent createDeeplinkingIntent() {
    Intent intent = new Intent();
    intent.setClass(Application.getContext(),
        V8Engine.getActivityProvider().getMainActivityFragmentClass());
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    return intent;
  }
}