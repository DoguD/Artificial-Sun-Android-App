package co.oriens.bluelight;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.ViewConfiguration;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.ExceptionHandlerInitializer;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;

import java.io.File;
import java.lang.reflect.Field;

import co.github.androidutils.logger.LogcatLogWriterWithLines;
import co.github.androidutils.logger.Logger;
import co.github.androidutils.logger.LoggingExceptionHandler;
import co.github.androidutils.logger.StartupLogWriter;
import co.github.androidutils.wakelock.WakeLockManager;
import co.oriens.bluelight.model.AlarmsManager;
import co.oriens.bluelight.presenter.DynamicThemeHandler;

// @formatter:off
@ReportsCrashes(
        mailTo = "info@oriens.co",
        applicationLogFileLines = 150,
        customReportContent = {
                ReportField.IS_SILENT,
                ReportField.APP_VERSION_CODE,
                ReportField.PHONE_MODEL,
                ReportField.ANDROID_VERSION,
                ReportField.CUSTOM_DATA,
                ReportField.STACK_TRACE,
                ReportField.SHARED_PREFERENCES,
        })
// @formatter:on
public class AlarmApplication extends Application {

    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        DynamicThemeHandler.init(this);
        setTheme(DynamicThemeHandler.getInstance().getIdForName(DynamicThemeHandler.DEFAULT));

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }

        Logger logger = Logger.getDefaultLogger();
        logger.addLogWriter(LogcatLogWriterWithLines.getInstance());
        logger.addLogWriter(StartupLogWriter.getInstance());
        LoggingExceptionHandler.addLoggingExceptionHandlerToAllThreads(logger);

        WakeLockManager.init(getApplicationContext(), new Logger(), true);
        AlarmsManager.init(getApplicationContext(), logger);

        ACRA.getErrorReporter().setExceptionHandlerInitializer(new ExceptionHandlerInitializer() {
            @Override
            public void initializeExceptionHandler(ErrorReporter reporter) {
                reporter.putCustomData("STARTUP_LOG", StartupLogWriter.getInstance().getMessagesAsString());
            }
        });

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        deleteLogs(logger, getApplicationContext());

        logger.d("onCreate");
        super.onCreate();
    }

    private void deleteLogs(Logger logger, Context context) {
        final File logFile = new File(context.getFilesDir(), "applog.log");
        if (logFile.exists()) {
            logFile.delete();
            logger.d("Deleted log file");
        } else {
            logger.d("Log file was already deleted");
        }
    }
}
