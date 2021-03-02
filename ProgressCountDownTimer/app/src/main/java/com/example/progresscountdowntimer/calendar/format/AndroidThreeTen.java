package com.example.progresscountdowntimer.calendar.format;

import android.app.Application;
import android.content.Context;


import com.example.progresscountdowntimer.calendar.bpcalendar.zone.ZoneRulesInitializer;

import java.util.concurrent.atomic.AtomicBoolean;

/** Android-specific initializer for the JSR-310 library. */
public final class AndroidThreeTen {
  private static final AtomicBoolean initialized = new AtomicBoolean();

  public static void init(Application application) {
    init((Context) application);
  }

  public static void init(Context context) {
    init(context, "TZDB.dat");
  }

  public static void init(Context context, String assetPath) {
    if (!initialized.getAndSet(true)) {
      ZoneRulesInitializer.setInitializer(new AssetsZoneRulesInitializer(context, assetPath));
    }
  }

  private AndroidThreeTen() {
    throw new AssertionError();
  }
}
