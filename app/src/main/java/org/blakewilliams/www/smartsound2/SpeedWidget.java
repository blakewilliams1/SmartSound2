package org.blakewilliams.www.smartsound2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class SpeedWidget extends AppWidgetProvider {
	public static String YOUR_AWESOME_ACTION = "YourAwesomeAction";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
	        Intent intent = new Intent(context, SpeedWidget.class);
	        intent.setAction(YOUR_AWESOME_ACTION);
	        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
	        // Get the layout for the App Widget and attach an on-click listener to the button
	        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.speed_widget);
	        views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

	        // Tell the AppWidgetManager to perform an update on the current App Widget
	        appWidgetManager.updateAppWidget(appWidgetId, views);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        Intent intent = new Intent(context, MainActivity.class);
	    intent.setAction(YOUR_AWESOME_ACTION);

	    //intent.putExtra("Goal",context.getString(R.string.stopThread));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Get the layout for the App Widget and attach an on-click listener
        // to the button
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.speed_widget);
	    //TODO: try using widget layout id next
        views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

	@Override
	public void onReceive(Context context, Intent intent){
		super.onReceive(context,intent);
		if (intent.getAction().equals(YOUR_AWESOME_ACTION)) {
			Log.i("Widget","Caught custom action");
		}
	}

}

