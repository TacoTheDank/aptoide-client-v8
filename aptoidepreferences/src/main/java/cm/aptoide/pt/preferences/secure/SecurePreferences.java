package cm.aptoide.pt.preferences.secure;

import android.content.SharedPreferences;
import cm.aptoide.pt.logger.Logger;

/**
 * Created by neuro on 21-04-2016.
 */
public class SecurePreferences {

  public static boolean shouldRunApkFy(SharedPreferences securePreferences) {
    return securePreferences.getBoolean(SecureKeys.SHOULD_RUN_APK_FY, true);
  }

  public static void setApkFyRun(SharedPreferences securePreferences) {
    securePreferences.edit()
        .putBoolean(SecureKeys.SHOULD_RUN_APK_FY, false)
        .apply();
  }

  public static boolean isFirstRun(SharedPreferences securePreferences) {
    return securePreferences.getBoolean(SecureKeys.FIRST_RUN, true);
  }

  public static int getLatestVersionCode(SharedPreferences securePreferences) {
    return securePreferences.getInt(SecureKeys.LATEST_VERSION_CODE, -1);
  }

  public static void setFirstRun(boolean b, SharedPreferences securePreferences) {
    Logger.getInstance()
        .d("First Run", "set first run -> " + b);
    securePreferences.edit()
        .putBoolean(SecureKeys.FIRST_RUN, b)
        .apply();
  }

  public static void setCurrentVersionCode(int versionCode, SharedPreferences securePreferences) {
    securePreferences.edit()
        .putInt(SecureKeys.LATEST_VERSION_CODE, versionCode)
        .apply();
  }

  public static boolean isWizardAvailable(SharedPreferences securePreferences) {
    return securePreferences.getBoolean(SecureKeys.WIZARD_AVAILABLE, true);
  }

  public static void setWizardAvailable(boolean available, SharedPreferences securePreferences) {
    securePreferences.edit()
        .putBoolean(SecureKeys.WIZARD_AVAILABLE, available)
        //.apply();
        .commit();
  }

  public static boolean isRootDialogShowed(SharedPreferences securePreferences) {
    return securePreferences.getBoolean(SecureKeys.ROOT_DIALOG_ShOWED, false);
  }

  public static void setRootDialogShowed(boolean displayed, SharedPreferences securePreferences) {
    securePreferences.edit()
        .putBoolean(SecureKeys.ROOT_DIALOG_ShOWED, displayed)
        .apply();
  }

  public static void setUpdatesFirstLoad(boolean updatesFirstLoad,
      SharedPreferences securePreferences) {
    securePreferences.edit()
        .putBoolean(SecureKeys.UPDATES_FIRST_LOAD, updatesFirstLoad)
        .apply();
  }

  public static boolean isUpdatesFirstLoad(SharedPreferences securePreferences) {
    return securePreferences.getBoolean(SecureKeys.UPDATES_FIRST_LOAD, true);
  }
}
