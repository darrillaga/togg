package com.t3coode.togg.activities.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.t3coode.togg.services.dtos.UserDTO;

public class UserUpdateBroadcastReceiver extends BroadcastReceiver {

    private ReceiverListener mListener;

    public UserUpdateBroadcastReceiver(ReceiverListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction()
                .equals(TimeTrackingService.BROADCAST_USER_UPDATE)) {

            UserDTO user = new UserDTO();
            user.loadFromJSON(intent
                    .getStringExtra(TimeTrackingService.BROADCAST_USER_JSON));

            if (mListener != null && user.getId() != null
                    && mListener.isListenToUser(user.getId())) {
                mListener.onUpdateUser(user);
            }
        }
    }

    public static interface ReceiverListener {
        boolean isListenToUser(long id);

        void onUpdateUser(UserDTO user);
    }
}
