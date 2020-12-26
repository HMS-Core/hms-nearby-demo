/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.hms.nearby.im.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.nearby.Nearby;
import com.huawei.hms.nearby.StatusCode;
import com.huawei.hms.nearby.im.bean.MessageBean;
import com.huawei.hms.nearby.im.utils.CommonUtil;
import com.huawei.hms.nearby.im.utils.Constants;
import com.huawei.hms.nearby.im.view.BaseView;
import com.huawei.hms.nearby.message.GetOption;
import com.huawei.hms.nearby.message.Message;
import com.huawei.hms.nearby.message.MessageEngine;
import com.huawei.hms.nearby.message.MessageHandler;
import com.huawei.hms.nearby.message.Policy;
import com.huawei.hms.nearby.message.PutOption;
import static com.huawei.hms.nearby.im.utils.Constants.CHART_TYPE;
import static com.huawei.hms.nearby.im.utils.Constants.MESSAGE_NAMESPACE;
import static com.huawei.hms.nearby.im.utils.Constants.TAG;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.charset.Charset;

public class NearbyAgent{

    /**
     *  Sets the life cycle of the message. default:240s
     */
    private static final int TTL_SECONDS = Policy.POLICY_TTL_SECONDS_MAX;
    private static final int DURATION_MESSAGE = 60 * 1000;
    private final INearbyMessageView view;
    private final Context mContext;
    private MessageEngine engine;
    private Gson gson;
    private MessageHandler messageHandler;
    private long lastReceiveTime;
    private PutOption putOption;

    public NearbyAgent(Context mContext, INearbyMessageView view) {
        this.view = view;
        this.mContext = mContext;
        init();
    }

    private void init(){
        engine = Nearby.getMessageEngine(mContext);
        Policy policy = new Policy.Builder().setTtlSeconds(TTL_SECONDS).build();
        putOption = new PutOption.Builder().setPolicy(policy).build();
        gson = new Gson();
    }


    public void startScan() {
        startScan(null);
    }

    public void startScan(String groupId) {
        if (engine == null) {
            Toast.makeText(mContext,"MessageEngine is null",Toast.LENGTH_SHORT).show();
            return;
        }
        Policy policy = new Policy.Builder().setTtlSeconds(Policy.POLICY_TTL_SECONDS_INFINITE).build();
        GetOption getOption = new GetOption.Builder().setPolicy(policy).build();
        messageHandler = new MessageHandler() {
            /**
             * This method is called upon receipt of the message.
             * The first time it is received or after the message is lost.
             * @param message
             */
            @Override
            public void onFound(Message message) {
                super.onFound(message);
                String json = new String(message.getContent(), UTF_8);
                Log.i(Constants.TAG,"NearbyAgent onFound() json:"+json);
                if ((TextUtils.isEmpty(json)) || (!json.contains("userName")) || (!json.startsWith("{"))) {
                    return;
                }
                MessageBean bean = gson.fromJson(json, MessageBean.class);
                if ((bean == null) || (view == null)) {
                    return;
                }
                long receivedTime = System.currentTimeMillis();
                if(receivedTime - lastReceiveTime > DURATION_MESSAGE){
                    bean.setReceiveTime(receivedTime);
                }
                lastReceiveTime = receivedTime;
                bean.setType(MessageBean.TYPE_RECEIVE_TEXT);
                if ((!TextUtils.isEmpty(groupId)) && (!groupId.equals(bean.getGroupId()))) {
                    return;
                }
                view.onMessageFound(bean);
            }

            @Override
            public void onLost(Message message) {
                super.onLost(message);
                String json = new String(message.getContent(), UTF_8);
                Log.i(Constants.TAG,"NearbyAgent onLost() json:" + json);
                if ((TextUtils.isEmpty(json)) || (!json.contains("userName")) || (!json.startsWith("{"))) {
                    return;
                }
                MessageBean bean = gson.fromJson(json, MessageBean.class);
                if ((view == null) || (bean == null)) {
                    return;
                }
                view.onMessageLost(bean);
            }
        };
        engine.get(messageHandler, getOption);
    }

    /**
     * Message encapsulation and send message
     */
    public void broadcastMessage(String groupId, String content) {
        if (engine == null) {
            Toast.makeText(mContext,"MessageEngine is null",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(CommonUtil.userName)) {
            Toast.makeText(mContext,"userName cannot be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        MessageBean messageBean = generateMessageBean(groupId, CommonUtil.userName, content);
        String json = gson.toJson(messageBean);
        Message message = new Message(json.getBytes(Charset.defaultCharset()), CHART_TYPE,MESSAGE_NAMESPACE);
        Log.i(TAG, "start sendMsg:"+json);
        engine.put(message,putOption).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                if (view != null) {
                    view.onMsgSendResult(false,messageBean);
                }
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int errorStatusCode = apiException.getStatusCode();
                    Toast.makeText(mContext, StatusCode.getStatusCode(errorStatusCode), Toast.LENGTH_SHORT).show();
                    Log.e(Constants.TAG, "apiException.getStatusCode()===="+errorStatusCode);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (view != null) {
                    messageBean.setType(MessageBean.TYPE_SEND_TEXT);
                    view.onMsgSendResult(true,messageBean);
                }
            }
        });
    }

    private MessageBean generateMessageBean(String groupId,String userName,String content){
        MessageBean messageBean = new MessageBean();
        messageBean.setUserName(userName);
        messageBean.setMsg(content);
        if (!TextUtils.isEmpty(groupId)) {
            messageBean.setGroupId(groupId);
        }
        messageBean.setSendTime(CommonUtil.getCurrentTime(CommonUtil.FORMAT));
        messageBean.setSendTimeValue(System.currentTimeMillis());
        return messageBean;
    }

    public void stopScan() {
        if (engine != null && messageHandler != null) {
            engine.unget(messageHandler);
        }
    }

    public interface INearbyMessageView extends BaseView {

        void onMessageFound(MessageBean messageBean);

        void onMessageLost(MessageBean messageBean);

        void onMsgSendResult(boolean isSucceed,MessageBean item);
    }
}
