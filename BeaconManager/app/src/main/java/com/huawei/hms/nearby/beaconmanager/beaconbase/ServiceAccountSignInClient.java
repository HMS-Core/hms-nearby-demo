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

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

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

    private String issuer;

    private String keyId;

    private String privateKey;

    private String audience;

    private String jwt;

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
        JsonObject jsonObject;
        try {
            JsonParser parser = new JsonParser();
            jsonObject = (JsonObject) parser.parse(jsonData);
        } catch (JsonIOException | JsonSyntaxException e) {
            BeaconBaseLog.e(TAG, "jsonData format err: " + e.getMessage());
            return null;
        }

        String subAcout = jsonObject.get("sub_account").getAsString();
        String keyId = jsonObject.get("key_id").getAsString();
        String orignPrivateKey = jsonObject.get("private_key").getAsString();
        if (!orignPrivateKey.startsWith(PRIV_KEY_PERFIX) || !orignPrivateKey.endsWith(PRIV_KEY_SUFFIX)) {
            BeaconBaseLog.e(TAG, "private_key format err.");
            return null;
        }
        String privKey =
            orignPrivateKey.substring(PRIV_KEY_PERFIX.length(), orignPrivateKey.length() - PRIV_KEY_SUFFIX.length());

        String audience = jsonObject.get("token_uri").getAsString();
        return new ServiceAccountSignInClient(subAcout, keyId, privKey, audience);
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
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);

            Algorithm algorithm = Algorithm.RSA256(null, rsaPrivateKey);
            long iat = System.currentTimeMillis() / 1000;
            long exp = iat + TOKEN_EXPIRE_TIME_MS;
            JWTCreator.Builder builder = JWT.create()
                .withIssuer(issuer)
                .withKeyId(keyId)
                .withAudience(audience)
                .withClaim("iat", iat)
                .withClaim("exp", exp);
            jwt = builder.sign(algorithm);
            return 0;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | JWTCreationException e) {
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

    public String getJwt() {
        return jwt;
    }
}
