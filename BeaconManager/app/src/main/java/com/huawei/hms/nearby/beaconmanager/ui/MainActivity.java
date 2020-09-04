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
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.huawei.hms.nearby.beaconmanager.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity
 *
 * @since 2019-11-14
 */
public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    private NearbyBeaconsFragment unRegisteredNearbyBeaconsFragment;

    private NearbyBeaconsFragment registeredNearbyBeaconsFragment;

    private PersonalCenterFragment personalCenterFragment;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView
            .setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Log.d(TAG, "onNavigationItemSelected is click: ");
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    hideAllFragment(transaction);
                    switch (item.getItemId()) {
                        case R.id.becons_unregistered: {
                            if (unRegisteredNearbyBeaconsFragment == null) {
                                unRegisteredNearbyBeaconsFragment = NearbyBeaconsFragment.newInstance(false);
                                transaction.add(R.id.FramePage, unRegisteredNearbyBeaconsFragment);
                            } else {
                                transaction.show(unRegisteredNearbyBeaconsFragment);
                            }
                            item.setChecked(true);
                            break;
                        }
                        case R.id.becons_registered: {
                            if (registeredNearbyBeaconsFragment == null) {
                                registeredNearbyBeaconsFragment = NearbyBeaconsFragment.newInstance(true);
                                transaction.add(R.id.FramePage, registeredNearbyBeaconsFragment);
                            } else {
                                transaction.show(registeredNearbyBeaconsFragment);
                            }
                            item.setChecked(true);
                            break;
                        }
                        case R.id.main_personal_center: {
                            if (personalCenterFragment == null) {
                                personalCenterFragment = new PersonalCenterFragment();
                                transaction.add(R.id.FramePage, personalCenterFragment);
                            } else {
                                transaction.show(personalCenterFragment);
                            }
                            item.setChecked(true);
                            break;
                        }
                        default:
                            break;
                    }
                    transaction.commit();
                    return false;
                }
            });
        bottomNavigationView.setSelectedItemId(R.id.main_personal_center);
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        if (unRegisteredNearbyBeaconsFragment != null) {
            transaction.hide(unRegisteredNearbyBeaconsFragment);
        }
        if (registeredNearbyBeaconsFragment != null) {
            transaction.hide(registeredNearbyBeaconsFragment);
        }
        if (personalCenterFragment != null) {
            transaction.hide(personalCenterFragment);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int selectedItemId = bottomNavigationView.getSelectedItemId();
        switch (selectedItemId) {
            case R.id.main_personal_center: {
                personalCenterFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            }
            case R.id.becons_unregistered: {
                unRegisteredNearbyBeaconsFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            }
            case R.id.becons_registered: {
                registeredNearbyBeaconsFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            }
            default:
                break;
        }
    }

    /**
     * show AlertDialog
     *
     * @param context context
     * @param title title
     * @param content content
     */
    public static void showAlertDialog(Context context, String title, String content) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(content);
        alertDialog.setPositiveButton("Confirm", null);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
