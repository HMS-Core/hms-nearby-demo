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

package com.huawei.hms.nearby.card.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.huawei.hms.nearby.card.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.Nullable;

/**
 * The card information.
 *
 * @since 2020-06-04
 */
public class CardInfo implements Parcelable {
    /**
     * Creator of CardInfo
     */
    public static final Creator<CardInfo> CREATOR = new Creator<CardInfo>() {
        @Override
        public CardInfo createFromParcel(Parcel in) {
            return new CardInfo(in);
        }

        @Override
        public CardInfo[] newArray(int size) {
            return new CardInfo[size];
        }
    };

    private static final Map<String, Integer> mPersionMap = new HashMap(){{
        put("Male", R.mipmap.male_icon);
        put("Female", R.mipmap.female_icon);
    }};

    private String mId;
    private String mSex;
    private String mName;
    private String mJobType;
    private String mMotto;
    private String mPhone;
    private String mEmail;
    private String mTelephone;
    private String mFax;
    private String mCompany;

    private CardInfo(String id, String sex, String name, String jobType, String motto, String phone, String email,
                    String telephone, String fax, String company) {
        mId = id;
        mSex = sex;
        mName = name;
        mJobType = jobType;
        mMotto = motto;
        mPhone = phone;
        mEmail = email;
        mTelephone = telephone;
        mFax = fax;
        mCompany = company;
    }

    protected CardInfo(Parcel in) {
        mId = in.readString();
        mSex = in.readString();
        mName = in.readString();
        mJobType = in.readString();
        mMotto = in.readString();
        mPhone = in.readString();
        mEmail = in.readString();
        mTelephone = in.readString();
        mFax = in.readString();
        mCompany = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mSex);
        dest.writeString(mName);
        dest.writeString(mJobType);
        dest.writeString(mMotto);
        dest.writeString(mPhone);
        dest.writeString(mEmail);
        dest.writeString(mTelephone);
        dest.writeString(mFax);
        dest.writeString(mCompany);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return mId;
    }

    public String getSex() {
        return mSex;
    }

    public String getName() {
        return mName;
    }

    public String getJobType() {
        return mJobType;
    }

    public String getMotto() {
        return mMotto;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getTelephone() {
        return mTelephone;
    }

    public String getFax() {
        return mFax;
    }

    public String getCompany() {
        return mCompany;
    }

    public int getPersionResourceId() {
        return mPersionMap.get(mSex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mSex, mName, mJobType, mMotto, mPhone, mEmail, mTelephone, mFax, mCompany);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CardInfo)) {
            return false;
        }

        CardInfo target = (CardInfo) obj;

        return Objects.equals(mId, target.mId) && Objects.equals(mSex, target.mSex)
                && Objects.equals(mName, target.mName) && Objects.equals(mJobType, target.mJobType)
                && Objects.equals(mMotto, target.mMotto) && Objects.equals(mPhone, target.mPhone)
                && Objects.equals(mEmail, target.mEmail) && Objects.equals(mTelephone, target.mTelephone)
                && Objects.equals(mFax, target.mFax) && Objects.equals(mCompany, target.mCompany);
    }

    public static final class Builder {
        private String mId;
        private String mSex;
        private String mName;
        private String mJobType;
        private String mMotto;
        private String mPhone;
        private String mEmail;
        private String mTelephone;
        private String mFax;
        private String mCompany;

        public Builder() {
            mId = UUID.randomUUID().toString();
        }

        /**
         * Fill in the card information to the builder.
         *
         * @param cardInfo the card information
         * @return Builder
         */
        public Builder setCardInfo(CardInfo cardInfo) {
            if (cardInfo != null) {
                mId = cardInfo.mId;
                mSex = cardInfo.mSex;
                mName = cardInfo.mName;
                mJobType = cardInfo.mJobType;
                mMotto = cardInfo.mMotto;
                mPhone = cardInfo.mPhone;
                mEmail = cardInfo.mEmail;
                mTelephone = cardInfo.mTelephone;
                mFax = cardInfo.mFax;
                mCompany = cardInfo.mCompany;
            }

            return this;
        }

        /**
         * Set sex.
         *
         * @param sex The sex of the card.
         * @return Builder
         */
        public Builder setSex(String sex) {
            mSex = sex;

            return this;
        }

        /**
         * Set name.
         *
         * @param name The name of the card.
         * @return Builder
         */
        public Builder setName(String name) {
            mName = name;

            return this;
        }

        /**
         * Set job type.
         *
         * @param jobType The job type of the card.
         * @return Builder
         */
        public Builder setJobType(String jobType) {
            mJobType = jobType;

            return this;
        }

        /**
         * Set motto.
         *
         * @param motto The motto of the card.
         * @return Builder
         */
        public Builder setMotto(String motto) {
            mMotto = motto;

            return this;
        }

        /**
         * Set phone number.
         *
         * @param phone the phone number of the card.
         * @return Builder
         */
        public Builder setPhone(String phone) {
            mPhone = phone;

            return this;
        }

        /**
         * Set email address.
         *
         * @param email the email address of the card.
         * @return Builder
         */
        public Builder setEmail(String email) {
            mEmail = email;

            return this;
        }

        /**
         * Set telephone number.
         *
         * @param telephone The telephone number of the card.
         * @return Builder
         */
        public Builder setTelephone(String telephone) {
            mTelephone = telephone;

            return this;
        }

        /**
         * Set fax number.
         *
         * @param fax The fax number of the card.
         * @return Builder
         */
        public Builder setFax(String fax) {
            mFax = fax;

            return this;
        }

        /**
         * Set company name.
         *
         * @param company The company name of the card.
         * @return Builder
         */
        public Builder setCompany(String company) {
            mCompany = company;

            return this;
        }

        /**
         * Build the card info.
         *
         * @return CardInfo
         */
        public CardInfo build() {
            return new CardInfo(mId, mSex, mName, mJobType, mMotto, mPhone, mEmail, mTelephone, mFax, mCompany);
        }
    }
}
