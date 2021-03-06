/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseV7EndlessTimelineDataListResponse<T> extends BaseV7EndlessResponse {
  @JsonProperty("datalist") private DataList<T> dataList;
  @JsonProperty("data") private TimelineData data;

  public BaseV7EndlessTimelineDataListResponse() {
  }

  public TimelineData getData() {
    return data;
  }

  public void setData(TimelineData data) {
    this.data = data;
  }

  @Override public int getTotal() {
    return hasData() ? dataList.getTotal() : 0;
  }

  @Override public int getNextSize() {
    return hasData() ? dataList.getNext() : 0;
  }

  @Override public boolean hasData() {
    return dataList != null && dataList.getList() != null;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $dataList = this.getDataList();
    result = result * PRIME + ($dataList == null ? 43 : $dataList.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof BaseV7EndlessDataListResponse;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof BaseV7EndlessDataListResponse)) return false;
    final BaseV7EndlessDataListResponse other = (BaseV7EndlessDataListResponse) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$dataList = this.getDataList();
    final Object other$dataList = other.getDataList();
    if (this$dataList == null ? other$dataList != null : !this$dataList.equals(other$dataList)) {
      return false;
    }
    return true;
  }

  public DataList<T> getDataList() {
    return this.dataList;
  }

  public void setDataList(DataList<T> dataList) {
    this.dataList = dataList;
  }

  public String toString() {
    return "BaseV7EndlessTimelineDataListResponse(dataList=" + this.getDataList() + ")";
  }
}
