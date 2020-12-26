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

package com.huawei.hms.nearby.stores.beaconbase.model;

import com.google.gson.annotations.SerializedName;

/**
 * Namespace
 *
 * @since 2020-01-03
 */
public class Namespace {
    @SerializedName("namespace")
    private String namespace;

    @SerializedName("visibility")
    private String visibility;

    public Namespace(String namespace, String visibility) {
        this.namespace = namespace;
        this.visibility = visibility;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getVisibility() {
        return visibility;
    }

    @Override
    public String toString() {
        return "NamespaceType{" + "namespace" + namespace + ", visibility='" + visibility + '}';
    }
}
