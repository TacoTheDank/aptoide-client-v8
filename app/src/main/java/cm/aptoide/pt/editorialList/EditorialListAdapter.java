package cm.aptoide.pt.editorialList;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.bonus.BonusAppcModel;
import cm.aptoide.pt.editorial.CaptionBackgroundPainter;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.home.bundles.editorial.EditorialBundleViewHolder;
import cm.aptoide.pt.home.bundles.editorial.EditorialViewHolder;
import cm.aptoide.pt.themes.ThemeManager;
import java.util.List;
import rx.subjects.PublishSubject;

class EditorialListAdapter extends RecyclerView.Adapter<EditorialViewHolder> {

  private static final int LOADING = R.layout.progress_item;
  private static final int EDITORIAL_CARD = R.layout.editorial_action_item;
  private final ProgressCard progressBundle;
  private final PublishSubject<HomeEvent> uiEventsListener;
  private final CaptionBackgroundPainter captionBackgroundPainter;
  private final ThemeManager themeAttributeProvider;
  private List<CurationCard> editorialListItems;
  private BonusAppcModel bonusAppcModel;

  public EditorialListAdapter(List<CurationCard> editorialListItems, ProgressCard progressBundle,
      PublishSubject<HomeEvent> uiEventsListener, CaptionBackgroundPainter captionBackgroundPainter,
      ThemeManager themeAttributeProvider) {
    this.editorialListItems = editorialListItems;
    this.progressBundle = progressBundle;
    this.uiEventsListener = uiEventsListener;
    this.captionBackgroundPainter = captionBackgroundPainter;
    this.themeAttributeProvider = themeAttributeProvider;
  }

  @Override public EditorialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == EDITORIAL_CARD) {
      return new EditorialBundleViewHolder(LayoutInflater.from(parent.getContext())
          .inflate(EDITORIAL_CARD, parent, false), uiEventsListener, captionBackgroundPainter,
          themeAttributeProvider);
    } else {
      return new LoadingViewHolder(LayoutInflater.from(parent.getContext())
          .inflate(LOADING, parent, false), uiEventsListener, captionBackgroundPainter);
    }
  }

  @Override public void onBindViewHolder(EditorialViewHolder editorialsViewHolder, int position) {
    if (editorialsViewHolder instanceof EditorialBundleViewHolder) {
      ((EditorialBundleViewHolder) editorialsViewHolder).setEditorialCard(
          editorialListItems.get(position), position, bonusAppcModel);
    }
  }

  @Override public int getItemViewType(int position) {
    if (editorialListItems.get(position) instanceof ProgressCard) {
      return LOADING;
    } else {
      return EDITORIAL_CARD;
    }
  }

  @Override public int getItemCount() {
    return editorialListItems.size();
  }

  public void add(List<CurationCard> editorialItemList, BonusAppcModel bonusAppcModel) {
    int size = editorialListItems.size();
    this.editorialListItems.addAll(editorialItemList);
    this.bonusAppcModel = bonusAppcModel;
    notifyItemRangeInserted(size, editorialItemList.size());
  }

  public void addLoadMore() {
    if (getLoadingPosition() < 0) {
      editorialListItems.add(progressBundle);
      notifyItemInserted(editorialListItems.size() - 1);
    }
  }

  public void removeLoadMore() {
    int loadingPosition = getLoadingPosition();
    if (loadingPosition >= 0) {
      editorialListItems.remove(loadingPosition);
      notifyItemRemoved(loadingPosition);
    }
  }

  public synchronized int getLoadingPosition() {
    for (int i = editorialListItems.size() - 1; i >= 0; i--) {
      CurationCard curationCard = editorialListItems.get(i);
      if (curationCard instanceof ProgressCard) {
        return i;
      }
    }
    return -1;
  }

  public void update(List<CurationCard> curationCards) {
    this.editorialListItems = curationCards;
    notifyDataSetChanged();
  }

  public CurationCard getCard(int visibleItem) {
    return editorialListItems.get(visibleItem);
  }

  public void updateEditorialCard(CurationCard curationCard) {
    for (int i = 0; i < editorialListItems.size(); i++) {
      if (curationCard != null && editorialListItems.get(i)
          .getId()
          .equals(curationCard.getId())) {
        editorialListItems.set(i, curationCard);
        notifyItemChanged(i);
      }
    }
  }
}
