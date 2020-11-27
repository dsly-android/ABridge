// ICall.aidl
package com.android.dsly.aidl;

import com.android.dsly.aidl.IReceiverAidlInterface;

interface ISenderAidlInterface {

    void join(IBinder token);

    void leave(IBinder token);

    void sendMessage(String json);

    void registerCallback(IReceiverAidlInterface cb);

    void unregisterCallback(IReceiverAidlInterface cb);
}
