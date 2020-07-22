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

package com.huawei.hms.nearbyconnectiondemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.core.content.FileProvider;

import com.huawei.hms.nearbyconnectiondemo.utils.ToastUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReceivedPhotoActivity extends Activity implements AdapterView.OnItemClickListener,
        View.OnClickListener, MainActivity.ReceivedFileListener {
    private static final String PATH = "/Download/nearby";

    private static final String TAG = "ReceivedPhotoActivity";
    /**
     * 显示图片的GridView
     */
    private GridView gvPhoto;
    /**
     * 文件夹下所有图片
     */
    private File[] mImages;

    private List<File> mImageList = new ArrayList<>();
    /**
     * 显示图片的适配器
     */
    private PhotoAdapter adapter;

    private MainActivity mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_photo);
        initView();
        initData();
    }

    private void initData() {
        String folderPath = Environment.getExternalStorageDirectory().getPath() + PATH;
        mImages = getImages(folderPath);
        if (mImages.length == 0) {
            ToastUtil.showShortToast(getApplicationContext(), "Get file is null");
            return;
        }
        List<File> list = Arrays.asList(mImages);
        mImageList = new ArrayList(list);
        adapter = new PhotoAdapter(mImageList, this);
        gvPhoto.setAdapter(adapter);
    }

    private void initView() {
        mService = MainActivity.getService();
        gvPhoto = findViewById(R.id.gv_photo);
        gvPhoto.setOnItemClickListener(this);
        findViewById(R.id.btn_sync).setOnClickListener(this);
        mService.setReceivedFileListener(this);
    }

    private File[] getImages(String folderPath) {
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            return folder.listFiles(new MyFileFilter());
        }

        File[] nullFile = new File[0];
        return nullFile;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String filePath = mImages[position].getPath();
        Uri uri;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {
            File file = new File(filePath);
            Log.i("ReceivedPhotoActivity", file.length() + "");
            uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName()
                    + ".fileprovider", new File(filePath));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(filePath));
        }
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        //同步照片
        if (v.getId() == R.id.btn_sync) {
            Log.d(TAG, "click btn_sync");
        }
    }

    @Override
    public void receivedFile(File file) {
        boolean isExist = false;
        Log.e(TAG, "file.path==========" + file.getPath());
        for (File file1 : mImageList) {
            Log.e(TAG, "file1.path==========" + file1.getPath());
            if (file.getPath().equalsIgnoreCase(file1.getPath())) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            Log.e(TAG, "file.path1111111111111111==========" + file.getPath());
            mImageList.add(file);
            adapter.updateData(mImageList);
        }
    }

    static class MyFileFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            String name = file.getName();
            return name.endsWith("jpg") || name.endsWith("png") || name.endsWith("gif")
                    || name.endsWith("jpeg") || name.endsWith("bmp");
        }
    }
}