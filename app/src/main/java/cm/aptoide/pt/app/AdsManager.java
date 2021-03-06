package cm.aptoide.pt.app;

import androidx.annotation.NonNull;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MinimalAd;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.database.RoomStoredMinimalAdPersistence;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.view.app.AppsList;
import java.util.List;
import rx.Observable;
import rx.Single;

/**
 * Created by D01 on 04/05/18.
 */

public class AdsManager {

  private final AdsRepository adsRepository;
  private final RoomStoredMinimalAdPersistence storedMinimalAdPersistence;
  private final MinimalAdMapper adMapper;

  public AdsManager(AdsRepository adsRepository,
      RoomStoredMinimalAdPersistence storedMinimalAdPersistence, MinimalAdMapper adMapper) {
    this.adsRepository = adsRepository;
    this.storedMinimalAdPersistence = storedMinimalAdPersistence;
    this.adMapper = adMapper;
  }

  public Single<MinimalAd> loadAds(String packageName, String storeName) {
    return adsRepository.loadAdsFromAppView(packageName, storeName)
        .toSingle();
  }

  public Single<MinimalAdRequestResult> loadAd(String packageName, List<String> keyWords) {
    return adsRepository.loadAdsFromAppviewSuggested(packageName, keyWords)
        .flatMap(minimalAds -> Observable.just(new MinimalAdRequestResult(minimalAds.get(0))))
        .toSingle()
        .onErrorReturn(throwable -> createMinimalAdRequestResultError(throwable));
  }

  @NonNull private MinimalAdRequestResult createMinimalAdRequestResultError(Throwable throwable) {
    if (throwable instanceof NoNetworkConnectionException) {
      return new MinimalAdRequestResult(AppsList.Error.NETWORK);
    } else {
      return new MinimalAdRequestResult(AppsList.Error.GENERIC);
    }
  }

  public void handleAdsLogic(SearchAdResult searchAdResult) {
    storedMinimalAdPersistence.insert(adMapper.map(searchAdResult, null));
    AdNetworkUtils.knockCpc(adMapper.map(searchAdResult));
  }
}
