package cm.aptoide.pt.editorialList;

import cm.aptoide.pt.reactions.data.TopReaction;
import java.util.Collections;
import java.util.List;

public class CurationCard {
  private final String id;
  private final String subTitle;
  private final String icon;
  private final String title;
  private final String views;
  private final String type;
  private final String date;
  private final String captionColor;
  private final String flair;
  private List<TopReaction> reactions;
  private String userReaction;
  private int numberOfReactions;

  public CurationCard(String id, String subTitle, String icon, String title, String views,
      String type, String date, String captionColor, String flair) {
    this.id = id;
    this.subTitle = subTitle;
    this.icon = icon;
    this.title = title;
    this.views = views;
    this.type = type;
    this.date = date;
    this.captionColor = captionColor;
    this.flair = flair;
    reactions = Collections.emptyList();
    userReaction = "";
    numberOfReactions = -1;
  }

  public CurationCard() {
    id = "";
    subTitle = "";
    icon = "";
    title = "";
    views = "";
    type = "";
    date = "";
    captionColor = "";
    flair = "";
  }

  public String getId() {
    return id;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public String getIcon() {
    return icon;
  }

  public String getTitle() {
    return title;
  }

  public String getViews() {
    return views;
  }

  public String getType() {
    return type;
  }

  public String getDate() {
    return date;
  }

  public List<TopReaction> getReactions() {
    return reactions;
  }

  public void setReactions(List<TopReaction> reactions) {
    this.reactions = reactions;
  }

  public String getUserReaction() {
    return userReaction;
  }

  public void setUserReaction(String userReaction) {
    this.userReaction = userReaction;
  }

  public int getNumberOfReactions() {
    return numberOfReactions;
  }

  public void setNumberOfReactions(int numberOfReactions) {
    this.numberOfReactions = numberOfReactions;
  }

  public String getCaptionColor() {
    return this.captionColor;
  }

  public String getFlair() {
    return flair;
  }
}
