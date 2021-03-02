package com.example.progresscountdowntimer.calendar.format;

import android.content.Context;


import com.example.progresscountdowntimer.calendar.bpcalendar.zone.TzdbZoneRulesProvider;
import com.example.progresscountdowntimer.calendar.bpcalendar.zone.ZoneRulesInitializer;
import com.example.progresscountdowntimer.calendar.bpcalendar.zone.ZoneRulesProvider;

import java.io.IOException;
import java.io.InputStream;

final class AssetsZoneRulesInitializer extends ZoneRulesInitializer {
  private final Context context;
  private final String assetPath;

  AssetsZoneRulesInitializer(Context context, String assetPath) {
    this.context = context;
    this.assetPath = assetPath;
  }

  @Override protected void initializeProviders() {
    TzdbZoneRulesProvider provider;

    InputStream is = null;
    try {
      is = context.getAssets().open(assetPath);
      provider = new TzdbZoneRulesProvider(is);
    } catch (IOException e) {
      throw new IllegalStateException(assetPath + " missing from assets", e);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException ignored) {
        }
      }
    }

    ZoneRulesProvider.registerProvider(provider);
  }
}