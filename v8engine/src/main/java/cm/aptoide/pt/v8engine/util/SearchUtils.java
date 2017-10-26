/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 28/06/2016.
 */

package cm.aptoide.pt.v8engine.util;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import cm.aptoide.pt.navigation.NavigationManagerV4;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.activity.SearchActivity;
import cm.aptoide.pt.v8engine.websocket.WebSocketSingleton;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchUtils {

  public static void setupGlobalSearchView(Menu menu, NavigationManagerV4 navigationManager) {
    setupSearchView(menu.findItem(R.id.action_search), navigationManager,
        s -> V8Engine.getFragmentProvider().newSearchFragment(s));
  }

  private static void setupSearchView(MenuItem searchItem, NavigationManagerV4 navigationManager,
      CreateQueryFragmentInterface createSearchFragmentInterface) {

    // Get the SearchView and set the searchable configuration
    final SearchManager searchManager =
        (SearchManager) V8Engine.getContext().getSystemService(Context.SEARCH_SERVICE);
    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    ComponentName cn = new ComponentName(V8Engine.getContext(), SearchActivity.class);
    searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String s) {
        MenuItemCompat.collapseActionView(searchItem);

        boolean validQueryLenght = s.length() > 1;

        if (validQueryLenght) {
          navigationManager.navigateTo(createSearchFragmentInterface.create(s));
        } else {
          ShowMessage.asToast(V8Engine.getContext(), R.string.search_minimum_chars);
        }

        return true;
      }

      @Override public boolean onQueryTextChange(String s) {
        return false;
      }
    });

    searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
      @Override public boolean onSuggestionSelect(int position) {
        return false;
      }

      @Override public boolean onSuggestionClick(int position) {
        Cursor item = (Cursor) searchView.getSuggestionsAdapter().getItem(position);

        navigationManager.navigateTo(createSearchFragmentInterface.create(item.getString(1)));

        return true;
      }
    });

    searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {

      if (!hasFocus) {
        MenuItemCompat.collapseActionView(searchItem);

        WebSocketSingleton.getInstance().disconnect();
      }
    });

    searchView.setOnSearchClickListener(v -> WebSocketSingleton.getInstance().connect());
  }

  public static void setupInsideStoreSearchView(Menu menu, NavigationManagerV4 navigationManager,
      String storeName) {
    setupSearchView(menu.findItem(R.id.action_search), navigationManager,
        s -> V8Engine.getFragmentProvider().newSearchFragment(s, storeName));
  }
}