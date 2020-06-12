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

package com.huawei.hms.nearby.card.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hms.nearby.Nearby;
import com.huawei.hms.nearby.card.R;
import com.huawei.hms.nearby.card.model.CardInfo;
import com.huawei.hms.nearby.card.model.Constants;
import com.huawei.hms.nearby.card.utils.BluetoothCheckUtil;
import com.huawei.hms.nearby.card.utils.JsonUtils;
import com.huawei.hms.nearby.card.utils.LocationCheckUtil;
import com.huawei.hms.nearby.card.utils.NetCheckUtil;
import com.huawei.hms.nearby.card.utils.PermissionUtil;
import com.huawei.hms.nearby.message.GetCallback;
import com.huawei.hms.nearby.message.GetOption;
import com.huawei.hms.nearby.message.Message;
import com.huawei.hms.nearby.message.MessageHandler;
import com.huawei.hms.nearby.message.MessagePicker;
import com.huawei.hms.nearby.message.Policy;
import com.huawei.hms.nearby.message.PutOption;

import java.nio.charset.Charset;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * My card fragment.
 *
 * @since 2020-06-04
 */
public class MyCardFragment extends Fragment implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = MyCardFragment.class.getSimpleName();
    private static final String DEFAULT_NAMESPACE = "my_card_namespace";
    private static final String DEFAULT_TYPE = "my_card_type";

    private static final int UNPUBLISH_STATUS = 0;
    private static final int PUBLISH_STATUS = 1;

    private ImageView mPersionImageView;
    private TextView mNameTextView;
    private TextView mJobTypeTextView;
    private TextView mMottoTextView;
    private TextView mPhoneTextView;
    private LinearLayout mEmailLayout;
    private TextView mEmailTextView;
    private LinearLayout mTelephoneLayout;
    private TextView mTelephoneTextView;
    private LinearLayout mFaxLayout;
    private TextView mFaxTextView;
    private LinearLayout mCompanyLayout;
    private TextView mCompanyTextView;
    private CardInfo mCardInfo;
    private SearchCardDialogFragment mSearchCardDialogFragment;

    private int mStatus = UNPUBLISH_STATUS;

    private MessageHandler mMessageHandler = new MessageHandler() {
        @Override
        public void onFound(Message message) {
            CardInfo cardInfo = JsonUtils.json2Object(new String(message.getContent(), Charset.forName("UTF-8")),
                    CardInfo.class);
            if (cardInfo == null) {
                return;
            }

            mSearchCardDialogFragment.addCardInfo(cardInfo);
        }

        @Override
        public void onLost(Message message) {
            CardInfo cardInfo = JsonUtils.json2Object(new String(message.getContent(), Charset.forName("UTF-8")),
                    CardInfo.class);
            if (cardInfo == null) {
                return;
            }

            mSearchCardDialogFragment.removeCardInfo(cardInfo);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSearchCardDialogFragment = new SearchCardDialogFragment();
        mCardInfo = getActivity().getIntent().getParcelableExtra(CardInfo.class.getSimpleName());
        if (mCardInfo == null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
            String jsonStr = sharedPreferences.getString(Constants.MY_CARD_KEY, "");
            if (!jsonStr.isEmpty()) {
                mCardInfo = JsonUtils.json2Object(jsonStr, CardInfo.class);
            }
        }
        if (mCardInfo == null) {
            Intent intent = new Intent(getActivity(), EditCardActivity.class);
            intent.putExtra(CardInfo.class.getSimpleName(), mCardInfo);
            startActivityForResult(intent, Constants.EDIT_REQUEST_CODE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_card, container, false);
        mPersionImageView = view.findViewById(R.id.persion);
        mNameTextView = view.findViewById(R.id.name);
        mJobTypeTextView = view.findViewById(R.id.job_type);
        mMottoTextView = view.findViewById(R.id.motto);
        mPhoneTextView = view.findViewById(R.id.phone);
        mEmailLayout = view.findViewById(R.id.email_layout);
        mEmailTextView = view.findViewById(R.id.email);
        mTelephoneLayout = view.findViewById(R.id.telephone_layout);
        mTelephoneTextView = view.findViewById(R.id.telephone);
        mFaxLayout = view.findViewById(R.id.fax_layout);
        mFaxTextView = view.findViewById(R.id.fax);
        mCompanyLayout = view.findViewById(R.id.company_layout);
        mCompanyTextView = view.findViewById(R.id.company);

        initCardInfoView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().getApplication().registerActivityLifecycleCallbacks(this);
        checkPermission();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_my_card, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem publishItem = menu.findItem(R.id.publish);
        MenuItem unpublishItem = menu.findItem(R.id.unpublish);
        MenuItem subscribeItem = menu.findItem(R.id.subscribe);
        MenuItem exchangeItem = menu.findItem(R.id.exchange);

        if (mStatus  == PUBLISH_STATUS) {
            publishItem.setVisible(false);
            unpublishItem.setVisible(true);
        } else {
            publishItem.setVisible(true);
            unpublishItem.setVisible(false);
        }

        subscribeItem.setVisible(true);
        exchangeItem.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            Intent intent = new Intent(this.getActivity(), EditCardActivity.class);
            intent.putExtra(CardInfo.class.getSimpleName(), mCardInfo);
            startActivityForResult(intent, Constants.EDIT_REQUEST_CODE);
            return true;
        }

        if (item.getItemId() == R.id.publish) {
            return onPublishItemSelected();
        }

        if (item.getItemId() == R.id.unpublish) {
            return onUnpublishItemSelected();
        }

        if (item.getItemId() == R.id.subscribe) {
            return onSubscribeItemSelected();
        }

        if (item.getItemId() == R.id.exchange) {
            return onExchangeItemSelected();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.EDIT_REQUEST_CODE) {
            mCardInfo = data.getParcelableExtra(CardInfo.class.getSimpleName());
            initCardInfoView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for (int i = 0; i < permissions.length; ++i) {
            if (grantResults[i] != 0) {
                showWarnDialog(Constants.LOCATION_ERROR);
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if (getActivity() != activity) {
            return;
        }

        mStatus = UNPUBLISH_STATUS;
        activity.getApplication().unregisterActivityLifecycleCallbacks(this);
        try {
            mSearchCardDialogFragment.dismiss();
        } catch (IllegalStateException e) {
            Log.d(TAG, "Close fail.");
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    private void showWarnDialog(String content) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(R.string.warn);
        builder.setIcon(R.mipmap.warn);
        builder.setMessage(content);
        builder.setNegativeButton(R.string.confirm, onClickListener);
        builder.show();
    }

    private void checkPermission() {
        if (!BluetoothCheckUtil.isBlueEnabled()) {
            showWarnDialog(Constants.BLUETOOTH_ERROR);
            return;
        }

        if (!LocationCheckUtil.isLocationEnabled(this.getActivity())) {
            showWarnDialog(Constants.LOCATION_SWITCH_ERROR);
            return;
        }

        if (!NetCheckUtil.isNetworkAvailable(this.getActivity())) {
            showWarnDialog(Constants.NETWORK_ERROR);
            return;
        }

        String[] deniedPermission = PermissionUtil.getDeniedPermissions(this.getActivity(), new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        });
        if (deniedPermission.length > 0) {
            PermissionUtil.requestPermissions(this.getActivity(), deniedPermission, 10);
        }
    }

    private void initCardInfoView() {
        if (mCardInfo == null) {
            return;
        }

        mPersionImageView.setImageResource(mCardInfo.getPersionResourceId());
        mNameTextView.setText(mCardInfo.getName());
        if (!TextUtils.isEmpty(mCardInfo.getJobType())) {
            mJobTypeTextView.setText(mCardInfo.getJobType());
        } else {
            mJobTypeTextView.setText("");
        }
        if (!TextUtils.isEmpty(mCardInfo.getMotto())) {
            mMottoTextView.setVisibility(View.VISIBLE);
            mMottoTextView.setText(mCardInfo.getMotto());
        } else {
            mMottoTextView.setVisibility(View.GONE);
            mMottoTextView.setText("");
        }
        mPhoneTextView.setText(mCardInfo.getPhone());
        if (!TextUtils.isEmpty(mCardInfo.getEmail())) {
            mEmailLayout.setVisibility(View.VISIBLE);
            mEmailTextView.setText(mCardInfo.getEmail());
        } else {
            mEmailLayout.setVisibility(View.GONE);
            mEmailTextView.setText("");
        }
        if (!TextUtils.isEmpty(mCardInfo.getTelephone())) {
            mTelephoneLayout.setVisibility(View.VISIBLE);
            mTelephoneTextView.setText(mCardInfo.getTelephone());
        } else {
            mTelephoneLayout.setVisibility(View.GONE);
            mTelephoneTextView.setText("");
        }
        if (!TextUtils.isEmpty(mCardInfo.getFax())) {
            mFaxLayout.setVisibility(View.VISIBLE);
            mFaxTextView.setText(mCardInfo.getFax());
        } else {
            mFaxLayout.setVisibility(View.GONE);
            mFaxTextView.setText("");
        }
        if (!TextUtils.isEmpty(mCardInfo.getCompany())) {
            mCompanyLayout.setVisibility(View.VISIBLE);
            mCompanyTextView.setText(mCardInfo.getCompany());
        } else {
            mCompanyLayout.setVisibility(View.GONE);
            mCompanyTextView.setText("");
        }
    }

    private void publish(String namespace, String type, int ttlSeconds, OnCompleteListener<Void> listener) {
        Message message = new Message(JsonUtils.object2Json(mCardInfo).getBytes(Charset.forName("UTF-8")), type,
                namespace);
        Policy policy = new Policy.Builder().setTtlSeconds(ttlSeconds).build();
        PutOption option = new PutOption.Builder().setPolicy(policy).build();
        Nearby.getMessageEngine(getActivity()).put(message, option).addOnCompleteListener(listener);
    }

    private void subscribe(String namespace, String type, int ttlSeconds, OnCompleteListener<Void> listener,
        GetCallback callback) {
        Policy policy = new Policy.Builder().setTtlSeconds(ttlSeconds).build();
        MessagePicker picker = new MessagePicker.Builder().includeNamespaceType(namespace, type).build();
        GetOption.Builder builder = new GetOption.Builder().setPolicy(policy).setPicker(picker);
        if (callback != null) {
            builder.setCallback(callback);
        }
        Nearby.getMessageEngine(getActivity()).get(mMessageHandler, builder.build()).addOnCompleteListener(listener);
    }

    private void unpublish(String namespace, String type, OnCompleteListener<Void> listener) {
        Message message = new Message(JsonUtils.object2Json(mCardInfo).getBytes(Charset.forName("UTF-8")),
                type, namespace);
        Nearby.getMessageEngine(getActivity()).unput(message)
                .addOnCompleteListener(listener);
    }

    private void unsubscribe(OnCompleteListener<Void> listener) {
        Nearby.getMessageEngine(getActivity()).unget(mMessageHandler).addOnCompleteListener(listener);
    }

    private boolean onPublishItemSelected() {
        publish(DEFAULT_NAMESPACE, DEFAULT_TYPE, Policy.POLICY_TTL_SECONDS_MAX, result -> {
            if (result.isSuccessful()) {
                mStatus = PUBLISH_STATUS;
                Toast.makeText(getActivity(), "Publish my card successful.", Toast.LENGTH_LONG).show();
                return;
            }

            String str = "Publish my card fail, exception: " + result.getException().getMessage();
            Log.e(TAG, str);
            Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
        });

        return true;
    }

    private boolean onUnpublishItemSelected() {
        unpublish(DEFAULT_NAMESPACE, DEFAULT_TYPE, result -> {
            if (result.isSuccessful()) {
                mStatus = UNPUBLISH_STATUS;
                Toast.makeText(getActivity(), "Unublish my card successful.", Toast.LENGTH_LONG).show();
                return;
            }

            String str = "Unpublish my card fail, exception: " + result.getException().getMessage();
            Log.e(TAG, str);
            Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
        });

        return true;
    }

    private boolean onSubscribeItemSelected() {
        subscribe(DEFAULT_NAMESPACE, DEFAULT_TYPE, Policy.POLICY_TTL_SECONDS_INFINITE, result -> {
            if (!result.isSuccessful()) {
                String str = "Subscribe is fail, exception: " + result.getException().getMessage();
                Log.e(TAG, str);
                Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
                return;
            }
            mSearchCardDialogFragment.setOnCloseListener(() -> {
                MyCardFragment.this.unsubscribe(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Unsubscribe fail, exception: "
                                + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });
            mSearchCardDialogFragment.show(getParentFragmentManager(), "Search Card");
            Toast.makeText(getActivity(), "Subscribe is successful.", Toast.LENGTH_LONG).show();
        }, null);

        return true;
    }

    private boolean onExchangeItemSelected() {
        PinCodeDialogFragment dialogFragment = new PinCodeDialogFragment(passwrod -> {
            MyCardFragment.this.publish(passwrod, passwrod, Policy.POLICY_TTL_SECONDS_MAX, result -> {
                if (!result.isSuccessful()) {
                    String str = "Exchange card fail, because publish my card fail. exception: "
                            + result.getException().getMessage();
                    Log.e(TAG, str);
                    Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
                    return;
                }
                MyCardFragment.this.subscribe(passwrod, passwrod, Policy.POLICY_TTL_SECONDS_INFINITE, ret -> {
                    if (!ret.isSuccessful()) {
                        MyCardFragment.this.unpublish(passwrod, passwrod, task -> {
                            String str = "Exchange card fail, because subscribe is fail, exception("
                                    + ret.getException().getMessage() + ")";
                            if (!task.isSuccessful()) {
                                str = str + " and unpublish fail, exception(" + task.getException().getMessage()
                                        + ")";
                            }

                            Log.e(TAG, str);
                            Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
                        });
                        return;
                    }
                    mSearchCardDialogFragment.setOnCloseListener(() -> {
                        MyCardFragment.this.unpublish(passwrod, passwrod, task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Unpublish my card fail, exception: "
                                        + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        MyCardFragment.this.unsubscribe(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Unsubscribe fail, exception: "
                                        + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    });
                    mSearchCardDialogFragment.show(getParentFragmentManager(), "Search Card");
                }, null);
            });
        });
        dialogFragment.show(getParentFragmentManager(), "pin code");

        return true;
    }
}
