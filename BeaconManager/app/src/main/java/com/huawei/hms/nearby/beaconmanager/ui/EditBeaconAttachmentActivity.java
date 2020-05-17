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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.huawei.hms.nearby.beaconmanager.R;
import com.huawei.hms.nearby.beaconmanager.beaconbase.BeaconUtil;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.Attachment;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.Namespace;
import com.huawei.hms.nearby.beaconmanager.beaconbase.restfulapi.BeaconRestfulClient;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * EditBeaconAttachmentActivity
 *
 * @since 2019-11-14
 */
public class EditBeaconAttachmentActivity extends BaseActivity {
    private EditText attachmentTypeEt;

    private EditText attachmentValueEt;

    private String beaconId;

    private Attachment attachment;

    private Spinner namespaceSpinner;

    private ArrayAdapter<String> namespaceAdapter;

    private List<String> namespaceList;

    private Spinner typeSpinner;

    private ArrayAdapter<String> typeAdapter;

    private List<String> typeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beaconId = getIntent().getStringExtra(Constant.BEACONS_ID);
        typeList = getIntent().getStringArrayListExtra(Constant.BEACONS_ATTACHMENT_TYPE);
        setContentView(R.layout.activity_edit_beacon_attachment);
        attachment = new Attachment("", "", "");
        initView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void initView() {
        setTitle(R.string.beacon_attachments);
        namespaceSpinner = findViewById(R.id.sp_attachment_namespace);
        namespaceList = new ArrayList<>();
        getAllNamespace();
        namespaceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, namespaceList);
        namespaceSpinner.setAdapter(namespaceAdapter);
        namespaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                attachment.setNamespace(namespaceList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if (namespaceList.size() == 2) {
            namespaceSpinner.setSelection(1);
            attachment.setNamespace(namespaceList.get(1));
        } else {
            namespaceSpinner.setSelection(0);
        }

        attachmentTypeEt = findViewById(R.id.et_attachment_type);
        typeSpinner = findViewById(R.id.sp_attachment_type);
        typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeList);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    return;
                }
                attachmentTypeEt.setText(typeList.get(i));
                attachment.setType(typeList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        typeSpinner.setSelection(0);
        attachmentTypeEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm =
                    (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    v.requestFocus();
                    imm.showSoftInput(v, 0);
                }

                typeSpinner.performClick();
                return false;
            }
        });
        typeSpinner.setActivated(true);
        attachmentValueEt = findViewById(R.id.et_attachment_val);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.beacon_save_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_edit) {
            if (attachment.getNamespace().length() == 0) {
                Toast.makeText(this, "Namespace should not be null.", Toast.LENGTH_SHORT).show();
                return false;
            }

            String type = attachmentTypeEt.getText().toString();
            if ((type == null) || (type.length() == 0) || (type.length() > Attachment.TYPE_MAX_LEN)) {
                Toast.makeText(this, "Type should be 1~16B.", Toast.LENGTH_SHORT).show();
                return false;
            }

            String attachmentVal = attachmentValueEt.getText().toString();
            if ((attachmentVal == null) || (attachmentVal.length() == 0)
                || (attachmentVal.length() > Attachment.VAL_MAX_LEN)) {
                Toast.makeText(this, "attachment value should be 1~2048B.", Toast.LENGTH_SHORT).show();
                return false;
            }
            attachment.setType(type);
            attachment.setValue(BeaconUtil.bytesToBase64Str(attachmentVal.getBytes(Charset.forName("UTF-8"))));
            Attachment ret = BeaconRestfulClient.getInstance().addAttachment(beaconId, attachment);
            if (ret == null) {
                Toast.makeText(this, "Add Failed!", Toast.LENGTH_SHORT).show();
                return false;
            }
            Toast.makeText(this, "Add Success!", Toast.LENGTH_SHORT).show();

            ArrayList<Attachment> attachmentToEdit = new ArrayList<>();
            attachmentToEdit.add(ret);
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(Constant.BEACONS_ATTACHMENT, attachmentToEdit);
            setResult(0, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAllNamespace() {
        ArrayList<Namespace> namespaces = BeaconRestfulClient.getInstance().queryNamespace();
        if (namespaces == null) {
            return;
        }
        namespaceList.clear();
        namespaceList.add("");
        for (Namespace namespace : namespaces) {
            namespaceList.add(namespace.getNamespace());
        }
    }
}
