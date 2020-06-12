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

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.hms.nearby.card.R;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Pin code dialog fragment.
 *
 * @since 2020-06-05
 */
public class PinCodeDialogFragment extends DialogFragment {
    private static final int EXCHANGE_CODE_LENGTH = 6;
    private EditText mPinCodeEditText;
    private final OnConfirmCallback mCallback;

    public PinCodeDialogFragment(@NonNull OnConfirmCallback callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pin_code, null);
        mPinCodeEditText = view.findViewById(R.id.pin_code);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                    String password = mPinCodeEditText.getText().toString();
                    if (password.length() != EXCHANGE_CODE_LENGTH) {
                        Toast.makeText(getActivity(), String.format(Locale.ENGLISH,
                                "The exchange code should be in %d digit.", EXCHANGE_CODE_LENGTH), Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    mCallback.onConfirm(password);
                })
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * On confirm callback.
     */
    public interface OnConfirmCallback {
        /**
         * On confirm.
         *
         * @param passwrod The pin code.
         */
        void onConfirm(String passwrod);
    }
}
