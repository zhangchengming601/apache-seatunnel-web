/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.app.thirdparty.transfrom.impl;

import org.apache.seatunnel.shade.com.typesafe.config.Config;
import org.apache.seatunnel.shade.com.typesafe.config.ConfigValueFactory;

import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.app.domain.request.job.TableSchemaReq;
import org.apache.seatunnel.app.domain.request.job.transform.Split;
import org.apache.seatunnel.app.domain.request.job.transform.SplitTransformOptions;
import org.apache.seatunnel.app.domain.request.job.transform.Transform;
import org.apache.seatunnel.app.domain.request.job.transform.TransformOptions;
import org.apache.seatunnel.app.dynamicforms.FormStructure;
import org.apache.seatunnel.app.thirdparty.transfrom.TransformConfigSwitcher;

import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@AutoService(TransformConfigSwitcher.class)
public class SplitTransformSwitcher implements TransformConfigSwitcher {
    @Override
    public Transform getTransform() {
        return Transform.SPLIT;
    }

    @Override
    public FormStructure getFormStructure(OptionRule transformOptionRule) {
        return null;
    }

    @Override
    public Config mergeTransformConfig(
            Config transformConfig, TransformOptions transformOption, TableSchemaReq inputSchema) {

        SplitTransformOptions splitTransformOptions = (SplitTransformOptions) transformOption;

        checkArgument(
                splitTransformOptions.getSplits().size() > 0,
                "SplitTransformSwitcher splits must be greater than 0");

        List<Map<String, Object>> splitOPs = new ArrayList<>();

        for (Split split : splitTransformOptions.getSplits()) {
            Map<String, Object> splitOP = new HashMap<>();
            splitOP.put("separator", split.getSeparator());
            splitOP.put("split_field", split.getSourceFieldName());
            splitOP.put("output_fields", ConfigValueFactory.fromIterable(split.getOutputFields()));
            splitOPs.add(splitOP);
        }

        return transformConfig.withValue("splitOPs", ConfigValueFactory.fromIterable(splitOPs));
    }
}
