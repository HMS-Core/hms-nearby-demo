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

package com.huawei.hms.nearbyFileTransfer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FileMainActivity extends AppCompatActivity {
    private static final int FILE_SELECT_CODE = 0;
    private Button sendBtn;
    private Button recvBtn;
    private ImageButton folderTabBtn;
    private ImageButton multiFileTabBtn;
    private NearbyAgent nearbyAgent;
    private List<File> files = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_main);

        nearbyAgent = new NearbyAgent(this);
        sendBtn = (Button)findViewById(R.id.sendBtn);
        recvBtn = (Button)findViewById(R.id.recvBtn);

        multiFileTabBtn = (ImageButton)findViewById(R.id.id_tab2_imageview);
        folderTabBtn = (ImageButton)findViewById(R.id.id_tab3_imageview);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        recvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nearbyAgent.receiveFile();
            }
        });

        folderTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFolderActivity();
            }
        });

        multiFileTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMultiFileActivity();
            }
        });
    }

    public void goToFolderActivity() {
        Intent intent = new Intent();
        intent.setClass(this, FolderMainActivity.class);
        startActivity(intent);
    }

    public void goToMultiFileActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MultiFileMainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    nearbyAgent.sendFile(new File(uri.getPath()));
                }
                break;
            case NearbyAgent.REQUEST_CODE_SCAN_ONE:
                nearbyAgent.onScanResult(data);
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showFolderChooser() {
        new ChooserDialog(FileMainActivity.this)
                .withFilter(true, false)
                // to handle the result(s)
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        Toast.makeText(FileMainActivity.this, "FOLDER: " + path, Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .show();
    }

    private void showFileChooser() {
        files.clear();
        new ChooserDialog(FileMainActivity.this)
                .enableMultiple(false)
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        //call nearby agent
                        nearbyAgent.sendFile(pathFile);
                        return;
                    }
                })
                // to handle the back key pressed or clicked outside the dialog:
                .withOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        dialog.cancel(); // MUST have
                    }
                })
                .build()
                .show();
    }
}
