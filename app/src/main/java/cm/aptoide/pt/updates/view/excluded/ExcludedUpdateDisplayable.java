/*
 * Copyright (c) 2016.
 * Modified on 13/07/2016.
 */

package cm.aptoide.pt.updates.view.excluded;

import cm.aptoide.pt.R;
import cm.aptoide.pt.database.room.RoomUpdate;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

/**
 * Created on 15/06/16.
 */
public class ExcludedUpdateDisplayable extends DisplayablePojo<RoomUpdate> {

  private boolean selected;

  public ExcludedUpdateDisplayable() {
  }

  public ExcludedUpdateDisplayable(RoomUpdate pojo) {
    super(pojo);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.row_excluded_update;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}
