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

package com.huawei.hms.nearby.beaconmanager.beaconbase;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.json.JsonSanitizer;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;

/**
 * Service Account Signin Utils
 *
 * @since 2020-01-04
 */
public class ServiceAccountSignInClient {
    /**
     * TOKEN_EXPIRE_TIME_MS
     */
    public static final int TOKEN_EXPIRE_TIME_MS = 3600;

    private static final String TAG = ServiceAccountSignInClient.class.getSimpleName();

    private static final String PRIV_KEY_PERFIX = "-----BEGIN PRIVATE KEY-----\n";

    private static final String PRIV_KEY_SUFFIX = "\n-----END PRIVATE KEY-----\n";

    @SerializedName("sub_account")
    private String issuer;

    @SerializedName("key_id")
    private String keyId;

    @SerializedName("private_key")
    private String privateKey;

    @SerializedName("token_uri")
    private String audience;

    @Expose
    private String jwt = "";

    private ServiceAccountSignInClient(String issuer, String keyId, String privateKey, String audience) {
        this.issuer = issuer;
        this.keyId = keyId;
        this.privateKey = privateKey;
        this.audience = audience;
        jwt = "";
    }

    /**
     * Generate ServiceAccountSignInClient instance from service account private key json file
     *
     * @param jsonData Service account private key json file
     * @return ServiceAccountSignInClient
     */
    public static ServiceAccountSignInClient buildFromJsonData(String jsonData) {
        ServiceAccountSignInClient serviceAccountSignInClient = null;
        try {
            serviceAccountSignInClient = new Gson().fromJson(JsonSanitizer.sanitize(jsonData),
                    ServiceAccountSignInClient.class);
        } catch (JsonSyntaxException e) {
            BeaconBaseLog.e(TAG, "jsonData format err: " + e.getMessage());
            return serviceAccountSignInClient;
        }

        String orignPrivateKey = serviceAccountSignInClient.getPrivateKey();
        if (!orignPrivateKey.startsWith(PRIV_KEY_PERFIX) || !orignPrivateKey.endsWith(PRIV_KEY_SUFFIX)) {
            BeaconBaseLog.e(TAG, "private_key format err.");
            serviceAccountSignInClient = null;
            return serviceAccountSignInClient;
        }
        serviceAccountSignInClient.setPrivateKey(orignPrivateKey
                .substring(PRIV_KEY_PERFIX.length(), orignPrivateKey.length() - PRIV_KEY_SUFFIX.length()));
        return serviceAccountSignInClient;
    }

    /**
     * sign in service account and get JWT
     *
     * @return jwt
     */
    public int signIn() {
        if ((issuer == null) || (issuer.length() == 0) || (keyId == null) || (keyId.length() == 0)
            || (privateKey == null) || (privateKey.length() == 0) || (audience == null) || (audience.length() == 0)) {
            return -1;
        }

        try {
            byte[] encodedKey = BeaconUtil.base64StrToBytes(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
            PrivateKey tmpPrivateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);
            long iat = System.currentTimeMillis() / 1000;
            long exp = iat + TOKEN_EXPIRE_TIME_MS;
            jwt = Jwts.builder().setHeaderParam("kid", keyId).setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                    .setIssuer(issuer)
                    .setAudience(audience)
                    .claim("iat", iat)
                    .claim("exp", exp)
                    .signWith(tmpPrivateKey, SignatureAlgorithm.RS256).compact();
            return 0;
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException e) {
            BeaconBaseLog.e(TAG, e.getMessage());
            return -1;
        }
    }

    public String getIssuer() {
        return issuer;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getJwt() {
        return jwt;
    }
}
