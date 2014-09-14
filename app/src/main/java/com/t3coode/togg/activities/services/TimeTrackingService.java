package com.t3coode.togg.activities.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.codebutler.android_websockets.WebSocketClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.t3coode.togg.R;
import com.t3coode.togg.ToggApp;
import com.t3coode.togg.activities.async.AsyncLoaderTask;
import com.t3coode.togg.activities.async.GenericLoader.GenericLoaderResponse;
import com.t3coode.togg.activities.async.LoaderTask;
import com.t3coode.togg.activities.services.utils.WebSocketMessageUtils;
import com.t3coode.togg.activities.services.utils.WebSocketMessageUtils.MessageActionsProcessor;
import com.t3coode.togg.activities.utils.HexUtils;
import com.t3coode.togg.services.TogglTimeEntries;
import com.t3coode.togg.services.dtos.TimeEntryDTO;
import com.t3coode.togg.services.dtos.UserDTO;
import com.t3coode.togg.services.dtos.WebSocketMessageDTO;

public class TimeTrackingService extends Service implements
        LoaderTask<TimeEntryDTO>, MessageActionsProcessor {

    public static final String BROADCAST_USER_UPDATE = "com.t3coode.togg.activities.services.BroadcastUserUpdate";
    public static final String BROADCAST_USER_JSON = "com.t3coode.togg.activities.services.BroadcastUserUpdateUser";

    public static final String BROADCAST_TIME_ENTRY_UPDATE = "com.t3coode.togg.activities.services.BroadcastTimeEntryUpdate";
    public static final String BROADCAST_TIME_ENTRY_CREATE = "com.t3coode.togg.activities.services.BroadcastTimeEntryCreate";
    public static final String BROADCAST_TIME_ENTRY_DELETE = "com.t3coode.togg.activities.services.BroadcastTimeEntryDelete";
    public static final String BROADCAST_TIME_ENTRY_JSON = "com.t3coode.togg.activities.services.BroadcastTimeEntryUpdateTimeEntry";

    public static final String SERVICE_TIME_ENTRY = "com.t3coode.togg.activities.services.ServiceTimeEntry";

    public static final String BROADCAST_TIME_ENTRY_UPDATE_ACTION = "com.t3coode.togg.activities.services.BroadcastTimeEntryUpdateAction";
    public static final int ACTION_UPDATE = 0;
    public static final int ACTION_STOP = 1;
    public static final int ACTION_START = 2;
    public static final int ACTION_DELETE = 3;

    private static final String TAG = TimeTrackingService.class.getName();

    private WebSocketClient mClient;
    private ObjectMapper mMapper;
    private AsyncLoaderTask<TimeEntryDTO> mLoaderTask;

    @Override
    public void onCreate() {
        mMapper = new ObjectMapper();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new TimeTrackingBinder(this);
    }

    public static class TimeTrackingBinder extends Binder {

        private final TimeTrackingService service;

        public TimeTrackingBinder(TimeTrackingService service) {
            this.service = service;
        }

        public TimeTrackingService getService() {
            return service;
        }
    }

    @SuppressWarnings("unchecked")
    public void updateTimeEntry(TimeEntryDTO timeEntry, int action) {
        if (mLoaderTask != null) {
            mLoaderTask.cancel(true);
        }

        Map<String, Object> params = new HashMap<String, Object>();

        params.put(SERVICE_TIME_ENTRY, timeEntry);
        params.put(BROADCAST_TIME_ENTRY_UPDATE_ACTION, action);

        this.mLoaderTask = new AsyncLoaderTask<TimeEntryDTO>();
        mLoaderTask.setParamsMap(params);
        mLoaderTask.execute(this);
    }

    private void notifyTimeEntryUpdated(String timeEntryJson) {
        Intent i = new Intent(BROADCAST_TIME_ENTRY_UPDATE);
        i.putExtra(BROADCAST_TIME_ENTRY_JSON, timeEntryJson);
        sendBroadcast(i);
    }

    private void notifyTimeEntryCreated(String timeEntryJson) {
        Intent i = new Intent(BROADCAST_TIME_ENTRY_CREATE);
        i.putExtra(BROADCAST_TIME_ENTRY_JSON, timeEntryJson);
        sendBroadcast(i);
    }

    private void notifyTimeEntryDeleted(String timeEntryJson) {
        Intent i = new Intent(BROADCAST_TIME_ENTRY_DELETE);
        i.putExtra(BROADCAST_TIME_ENTRY_JSON, timeEntryJson);
        sendBroadcast(i);
    }

    private void notifyUserUpdated(String userJson) {
        Intent i = new Intent(BROADCAST_USER_UPDATE);
        i.putExtra(BROADCAST_USER_JSON, userJson);
        sendBroadcast(i);
    }

    @SuppressWarnings("unchecked")
    public void deleteTimeEntry(TimeEntryDTO timeEntry) {
        if (mLoaderTask != null) {
            mLoaderTask.cancel(true);
        }

        Map<String, Object> params = new HashMap<String, Object>();

        params.put(SERVICE_TIME_ENTRY, timeEntry);
        params.put(BROADCAST_TIME_ENTRY_UPDATE_ACTION, ACTION_DELETE);

        this.mLoaderTask = new AsyncLoaderTask<TimeEntryDTO>();
        mLoaderTask.setParamsMap(params);
        mLoaderTask.execute(this);
    }

    public void startWebSocketClient() {
        if (mClient == null) {

            List<BasicNameValuePair> extraHeaders = new ArrayList<BasicNameValuePair>();

            mClient = new WebSocketClient(
                    URI.create(getString(R.string.websocket_api_service_path)),
                    new WebSocketClient.Listener() {
                        @Override
                        public void onConnect() {
                            Log.d(TAG, "Connected!");
                            ObjectNode node = JsonNodeFactory.instance
                                    .objectNode();
                            node.put("type", "authenticate");
                            node.put("api_token", ToggApp.getApplication()
                                    .getCurrentUser().getApiToken());

                            mClient.send(node.toString());
                        }

                        @Override
                        public void onMessage(String message) {
                            Log.d(TAG, String.format("Got string message! %s",
                                    message));
                            WebSocketMessageUtils.processMessage(mClient,
                                    message, TimeTrackingService.this);
                        }

                        @Override
                        public void onMessage(byte[] data) {
                            Log.d(TAG, String.format("Got binary message! %s",
                                    HexUtils.getHexStringFromBytes(data)));
                        }

                        @Override
                        public void onDisconnect(int code, String reason) {
                            Log.d(TAG, String.format(
                                    "Disconnected! Code: %d Reason: %s", code,
                                    reason));
                            startWebSocketClient();
                        }

                        @Override
                        public void onError(Exception error) {
                            Log.e(TAG, "Error!", error);
                        }

                    }, extraHeaders);
        }

        if (!mClient.isConnected()) {
            mClient.connect();
        }
    }

    @Override
    public void onDestroy() {
        mClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onPostExecute(GenericLoaderResponse<TimeEntryDTO> result,
            Map<String, Object> params) {

        int action = (Integer) params.get(BROADCAST_TIME_ENTRY_UPDATE_ACTION);

        if (result.success()) {
            String jsonData = (result.getData() != null) ? result.getData()
                    .toJSON() : null;

            if (action == ACTION_DELETE) {
                notifyTimeEntryDeleted(jsonData);
            } else {
                notifyTimeEntryUpdated(jsonData);
            }
        }
    }

    @Override
    public TimeEntryDTO onExecute(Map<String, Object> params) throws Exception {
        TogglTimeEntries timeEntriesManager = ToggApp.getApplication()
                .getTogglServices().manageTimeEntries();
        TimeEntryDTO timeEntry = (TimeEntryDTO) params.get(SERVICE_TIME_ENTRY);

        int action = (Integer) params.get(BROADCAST_TIME_ENTRY_UPDATE_ACTION);

        if (action == ACTION_UPDATE) {
            if (timeEntry.getId() != null) {
                return timeEntriesManager.update(timeEntry);
            } else {
                return timeEntriesManager.create(timeEntry);
            }
        } else if (action == ACTION_STOP) {
            return timeEntriesManager.stop(timeEntry);
        } else if (action == ACTION_START) {
            return timeEntriesManager.start(timeEntry);
        } else if (action == ACTION_DELETE) {
            return timeEntriesManager.delete(timeEntry.getId()) ? timeEntry
                    : null;
        }

        return null;
    }

    @Override
    public boolean awaitingTaskResult(Map<String, Object> params) {
        return true;
    }

    @Override
    public void onMessageActionUpdate(WebSocketMessageDTO message) {
        if (checkTimeEntryMessage(message)) {
            notifyTimeEntryUpdated(message.getRawData());
        } else if (checkUserMessage(message)) {
            notifyUserUpdated(message.getRawData());
        }
    }

    @Override
    public void onMessageActionCreate(WebSocketMessageDTO message) {
        if (checkTimeEntryMessage(message)) {
            notifyTimeEntryCreated(message.getRawData());
        }
    }

    @Override
    public void onMessageActionDelete(WebSocketMessageDTO message) {
        if (checkTimeEntryMessage(message)) {
            notifyTimeEntryDeleted(message.getRawData());
        }
    }

    @Override
    public void onMessageActionOther(WebSocketMessageDTO message) {
        if (checkTimeEntryMessage(message)) {
        }
    }

    private boolean checkTimeEntryMessage(WebSocketMessageDTO message) {
        return message.getData() instanceof TimeEntryDTO;
    }

    private boolean checkUserMessage(WebSocketMessageDTO message) {
        return message.getData() instanceof UserDTO;
    }
}
