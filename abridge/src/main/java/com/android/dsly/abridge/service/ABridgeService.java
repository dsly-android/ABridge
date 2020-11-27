package com.android.dsly.abridge.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.android.dsly.aidl.IReceiverAidlInterface;
import com.android.dsly.aidl.ISenderAidlInterface;
import com.android.dsly.abridge.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class ABridgeService extends Service {

    private List<Client> mClients = new ArrayList<>();
    private RemoteCallbackList<IReceiverAidlInterface> mCallbacks = new RemoteCallbackList<>();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        LogUtils.d("onDestroy");
        super.onDestroy();
        mCallbacks.kill();
    }

    private final ISenderAidlInterface.Stub mBinder = new ISenderAidlInterface.Stub() {
        @Override
        public void join(IBinder token) throws RemoteException {
            int idx = findClient(token);
            if (idx >= 0) {
                LogUtils.d(token + " already joined , client size " + mClients.size());
                return;
            }
            Client client = new Client(token);
            // 注册客户端死掉的通知
            token.linkToDeath(client, 0);
            mClients.add(client);
            LogUtils.d(token + " join , client size " + mClients.size());
        }

        @Override
        public void leave(IBinder token) throws RemoteException {
            int idx = findClient(token);
            if (idx < 0) {
                LogUtils.d(token + " already left , client size " + mClients.size());
                return;
            }
            Client client = mClients.get(idx);
            mClients.remove(client);
            // 取消注册
            client.mToken.unlinkToDeath(client, 0);
            LogUtils.d(token + " left , client size " + mClients.size());

            if (mClients.size() == 0) {
                stopSelf();//没有客户端就停止自己
            }
        }

        @Override
        public void sendMessage(String message) throws RemoteException {
            LogUtils.d("sendMessage :" + message);
            onSuccessCallBack(message);
        }

        @Override
        public void registerCallback(IReceiverAidlInterface cb) throws RemoteException {
            LogUtils.d("registerCallback " + cb);
            mCallbacks.register(cb);
        }

        @Override
        public void unregisterCallback(IReceiverAidlInterface cb) throws RemoteException {
            LogUtils.d("unregisterCallback " + cb);
            mCallbacks.unregister(cb);
        }
    };

    private int findClient(IBinder token) {
        for (int i = 0; i < mClients.size(); i++) {
            if (mClients.get(i).mToken == token) {
                return i;
            }
        }
        return -1;
    }

    private void onSuccessCallBack(String message) {
        final int len = mCallbacks.beginBroadcast();
        for (int i = 0; i < len; i++) {
            try {
                // 通知回调
                mCallbacks.getBroadcastItem(i).receiveMessage(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mCallbacks.finishBroadcast();
    }

    private final class Client implements IBinder.DeathRecipient {
        private final IBinder mToken;

        private Client(IBinder token) {
            mToken = token;
        }

        @Override
        public void binderDied() {
            // 客户端死掉，执行此回调
            int index = mClients.indexOf(this);
            if (index < 0) {
                return;
            }

            LogUtils.d("client died" );
            mClients.remove(this);
        }
    }

}
