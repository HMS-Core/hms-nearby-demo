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

package com.huawei.hms.nearby.beaconmanager.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.huawei.hms.nearby.beaconmanager.R;

/**
 * EditPropertyDialogFragment
 *
 * @since 2020-01-07
 */
public class EditPropertyDialogFragment extends DialogFragment {
    /**
     * NoticeDialogListener
     */
    public interface NoticeDialogListener {
        /**
         * Call back for Click button DialogPositive
         *
         * @param dialog dialog
         */
        void onDialogPositiveClick(DialogFragment dialog);

        /**
         * Call back for Click button DialogNegative
         *
         * @param dialog dialog
         */
        void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener listener = null;

    private String key;

    private String value;

    private EditText keyEt;

    private EditText vlaueEt;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_property, null);
        keyEt = view.findViewById(R.id.et_property_key1);
        vlaueEt = view.findViewById(R.id.et_property_value1);
        keyEt.setText(key);
        vlaueEt.setText(value);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                key = keyEt.getText().toString();
                value = vlaueEt.getText().toString();
                if ((key.length() == 0) || (value.length() == 0)) {
                    return;
                }
                listener.onDialogPositiveClick(EditPropertyDialogFragment.this);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDialogNegativeClick(EditPropertyDialogFragment.this);
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof NoticeDialogListener)) {
            return;
        }
        listener = (NoticeDialogListener) context;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
