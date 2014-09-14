package com.t3coode.togg.activities.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceManagerUtils {

  public static interface Executable {

    public void call(SharedPreferences.Editor editor);

  }

  private SharedPreferences preferences;

  public PreferenceManagerUtils(Context context) {
    this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public void setPreferences(Executable e) {
    SharedPreferences.Editor editor = this.preferences.edit();

    e.call(editor);

    editor.commit();
  }

  public String getString(String key){
    return this.getString(key, "");
  }

  public String getString(String key, String defaultValue){
    return this.preferences.getString(key, defaultValue);
  }

  public boolean getBoolean(String key, boolean defaultValue){
    return this.preferences.getBoolean(key, defaultValue);
  }

  public boolean getBoolean(String key){
    return this.getBoolean(key, true);
  }

  public SharedPreferences.Editor edit() {
    return this.preferences.edit();
  }

  public boolean hasValues(String ...keys){
    if (keys.length == 0) {
      return false;
    }


    boolean result = true;

    for(String key : keys) {
      result = result && hasValue(key);
    }

    return result;
  }

  public boolean hasValue(String key){
    return this.preferences.getString(key, "").equals("");
  }

}
