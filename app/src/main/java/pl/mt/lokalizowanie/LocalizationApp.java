package pl.mt.lokalizowanie;

import android.app.Application;

import com.cengalabs.flatui.FlatUI;
import com.crashlytics.android.Crashlytics;

import timber.log.Timber;


public class LocalizationApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.SEA);
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.HollowTree {
        @Override
        public void i(String message, Object... args) {
            Crashlytics.log(String.format(message, args));
        }

        @Override
        public void i(Throwable t, String message, Object... args) {
            i(message, args); // Just add to the log.
        }

        @Override
        public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            e(message, args);
            Crashlytics.logException(t);
        }
    }
}
