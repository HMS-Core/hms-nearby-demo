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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hms.nearby.card.R;
import com.huawei.hms.nearby.card.model.CardInfo;

/**
 * Card Activity.
 *
 * @since 2020-06-04
 */
public class CardActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCardInfo = getIntent().getParcelableExtra(CardInfo.class.getSimpleName());

        mPersionImageView = findViewById(R.id.persion);
        mNameTextView = findViewById(R.id.name);
        mJobTypeTextView = findViewById(R.id.job_type);
        mMottoTextView = findViewById(R.id.motto);
        mPhoneTextView = findViewById(R.id.phone);
        mEmailLayout = findViewById(R.id.email_layout);
        mEmailTextView = findViewById(R.id.email);
        mTelephoneLayout = findViewById(R.id.telephone_layout);
        mTelephoneTextView = findViewById(R.id.telephone);
        mFaxLayout = findViewById(R.id.fax_layout);
        mFaxTextView = findViewById(R.id.fax);
        mCompanyLayout = findViewById(R.id.company_layout);
        mCompanyTextView = findViewById(R.id.company);

        initCardInfoView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initCardInfoView() {
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
}
