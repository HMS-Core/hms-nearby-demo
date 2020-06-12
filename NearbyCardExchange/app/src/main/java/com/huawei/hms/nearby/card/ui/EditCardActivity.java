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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.huawei.hms.nearby.card.R;
import com.huawei.hms.nearby.card.model.CardInfo;
import com.huawei.hms.nearby.card.model.Constants;
import com.huawei.hms.nearby.card.utils.JsonUtils;

/**
 * Edit Card Activity.
 *
 * @since 2020-06-04
 */
public class EditCardActivity extends AppCompatActivity {
    private RadioGroup mRadioGroup;
    private TextInputLayout mNameInputLayout;
    private TextInputEditText mNameInputEditText;
    private TextInputLayout mJobTypeInputLayout;
    private TextInputEditText mJobTypeInputEditText;
    private TextInputLayout mMottoInputLayout;
    private TextInputEditText mMottoInputEditText;
    private TextInputLayout mPhoneInputLayout;
    private TextInputEditText mPhoneInputEditText;
    private TextInputLayout mEmailInputLayout;
    private TextInputEditText mEmailInputEditText;
    private TextInputLayout mTelephoneInputLayout;
    private TextInputEditText mTelephoneInputEditText;
    private TextInputLayout mFaxInputLayout;
    private TextInputEditText mFaxInputEditText;
    private TextInputLayout mCompanyInputLayout;
    private TextInputEditText mCompanyInputEditText;
    private CardInfo mCardInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCardInfo = getIntent().getParcelableExtra(CardInfo.class.getSimpleName());

        mRadioGroup = findViewById(R.id.radio_group);
        mNameInputLayout = findViewById(R.id.name_input_layout);
        mNameInputEditText = findViewById(R.id.name_input_edit_text);
        mJobTypeInputLayout = findViewById(R.id.job_type_input_layout);
        mJobTypeInputEditText = findViewById(R.id.job_type_input_edit_text);
        mMottoInputLayout = findViewById(R.id.motto_input_layout);
        mMottoInputEditText = findViewById(R.id.motto_input_edit_text);
        mPhoneInputLayout = findViewById(R.id.phone_input_layout);
        mPhoneInputEditText = findViewById(R.id.phone_input_edit_text);
        mEmailInputLayout = findViewById(R.id.email_input_layout);
        mEmailInputEditText = findViewById(R.id.email_input_edit_text);
        mTelephoneInputLayout = findViewById(R.id.telephone_input_layout);
        mTelephoneInputEditText = findViewById(R.id.telephone_input_edit_text);
        mFaxInputLayout = findViewById(R.id.fax_input_layout);
        mFaxInputEditText = findViewById(R.id.fax_input_edit_text);
        mCompanyInputLayout = findViewById(R.id.company_input_layout);
        mCompanyInputEditText = findViewById(R.id.company_input_edit_text);
        initCardInfoView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mCardInfo == null) {
                Toast.makeText(this, R.string.card_null, Toast.LENGTH_LONG).show();
                return true;
            }

            Intent intent = new Intent();
            intent.putExtra(CardInfo.class.getSimpleName(), mCardInfo);
            setResult(Constants.EDIT_REQUEST_CODE, intent);
            finish();
            return true;
        }

        if (item.getItemId() == R.id.confirm) {
            if (!isValid()) {
                Toast.makeText(this, R.string.card_invalid, Toast.LENGTH_LONG).show();
                return true;
            }

            CardInfo.Builder builder = new CardInfo.Builder();
            builder.setCardInfo(mCardInfo);
            RadioButton radioButton = findViewById(mRadioGroup.getCheckedRadioButtonId());
            builder.setSex(radioButton.getText().toString());
            builder.setName(mNameInputEditText.getText().toString());
            builder.setJobType(mJobTypeInputEditText.getText().toString());
            builder.setMotto(mMottoInputEditText.getText().toString());
            builder.setPhone(mPhoneInputEditText.getText().toString());
            builder.setEmail(mEmailInputEditText.getText().toString());
            builder.setTelephone(mTelephoneInputEditText.getText().toString());
            builder.setFax(mFaxInputEditText.getText().toString());
            builder.setCompany(mCompanyInputEditText.getText().toString());
            mCardInfo = builder.build();
            SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(Constants.MY_CARD_KEY, JsonUtils.object2Json(mCardInfo)).apply();
            Intent intent = new Intent();
            intent.putExtra(CardInfo.class.getSimpleName(), mCardInfo);
            setResult(Constants.EDIT_REQUEST_CODE, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initCardInfoView() {
        if (mCardInfo == null) {
            return;
        }

        for (int i = 0; i < mRadioGroup.getChildCount(); ++i) {
            RadioButton radioButton = (RadioButton) mRadioGroup.getChildAt(i);
            if (radioButton.getText().equals(mCardInfo.getSex())) {
                radioButton.setChecked(true);
            }
        }
        mNameInputEditText.setText(mCardInfo.getName());
        mJobTypeInputEditText.setText(mCardInfo.getJobType());
        mMottoInputEditText.setText(mCardInfo.getMotto());
        mPhoneInputEditText.setText(mCardInfo.getPhone());
        mEmailInputEditText.setText(mCardInfo.getEmail());
        mTelephoneInputEditText.setText(mCardInfo.getTelephone());
        mFaxInputEditText.setText(mCardInfo.getFax());
        mCompanyInputEditText.setText(mCardInfo.getCompany());
    }

    private boolean isValid() {
        if (mNameInputEditText.getText().length() == 0
                || mNameInputEditText.getText().length() > mNameInputLayout.getCounterMaxLength()) {
            return false;
        }
        if (mPhoneInputEditText.getText().length() == 0
                || mPhoneInputEditText.getText().length() > mPhoneInputLayout.getCounterMaxLength()) {
            return false;
        }

        if (mJobTypeInputEditText.getText().length() > mJobTypeInputLayout.getCounterMaxLength()
                || mMottoInputEditText.getText().length() > mMottoInputLayout.getCounterMaxLength()
                || mEmailInputEditText.getText().length() > mEmailInputLayout.getCounterMaxLength()
                || mTelephoneInputEditText.getText().length() > mTelephoneInputLayout.getCounterMaxLength()
                || mFaxInputEditText.getText().length() > mFaxInputLayout.getCounterMaxLength()
                || mCompanyInputEditText.getText().length() > mCompanyInputLayout.getCounterMaxLength()) {
            return false;
        }

        return true;
    }
}
