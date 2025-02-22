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

package org.apache.seatunnel.datasource.plugin.redshift.s3;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;

import static org.apache.hadoop.fs.FileSystem.FS_DEFAULT_NAME_KEY;

@Slf4j
public class HadoopS3AConfiguration {

    /* S3 constants */
    private static final String S3A_SCHEMA = "s3a";
    private static final String HDFS_S3N_IMPL = "org.apache.hadoop.fs.s3native.NativeS3FileSystem";
    private static final String HDFS_S3A_IMPL = "org.apache.hadoop.fs.s3a.S3AFileSystem";
    private static final String S3A_PROTOCOL = "s3a";
    private static final String DEFAULT_PROTOCOL = "s3n";
    private static final String S3_FORMAT_KEY = "fs.%s.%s";
    private static final String HDFS_IMPL_KEY = "impl";

    public static Configuration getConfiguration(Map<String, String> s3Options) {

        if (!s3Options.containsKey(S3RedshiftOptionRule.BUCKET.key())) {
            throw new IllegalArgumentException(
                    "S3Redshift datasource bucket is null, please check your config");
        }
        if (!s3Options.containsKey(S3RedshiftOptionRule.FS_S3A_ENDPOINT.key())) {
            throw new IllegalArgumentException(
                    "S3Redshift datasource endpoint is null, please check your config");
        }
        String bucket = s3Options.get(S3RedshiftOptionRule.BUCKET.key());

        String protocol = DEFAULT_PROTOCOL;
        if (bucket.startsWith(S3A_PROTOCOL)) {
            protocol = S3A_PROTOCOL;
        }
        String fsImpl = protocol.equals(S3A_PROTOCOL) ? HDFS_S3A_IMPL : HDFS_S3N_IMPL;
        Configuration hadoopConf = new Configuration();
        hadoopConf.set(FS_DEFAULT_NAME_KEY, bucket);
        hadoopConf.set(
                S3RedshiftOptionRule.FS_S3A_ENDPOINT.key(),
                s3Options.get(S3RedshiftOptionRule.FS_S3A_ENDPOINT.key()));
        hadoopConf.set(formatKey(protocol, HDFS_IMPL_KEY), fsImpl);
        if (s3Options.containsKey(S3RedshiftOptionRule.HADOOP_S3_PROPERTIES.key())) {
            Arrays.stream(
                            s3Options
                                    .get(S3RedshiftOptionRule.HADOOP_S3_PROPERTIES.key())
                                    .split("\n"))
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .forEach(
                            line -> {
                                String[] kv = line.split("=");
                                if (kv.length == 2) {
                                    hadoopConf.set(kv[0].trim(), kv[1].trim());
                                }
                            });
        }
        if (S3RedshiftOptionRule.S3aAwsCredentialsProvider.SimpleAWSCredentialsProvider
                .getProvider()
                .equals(s3Options.get(S3RedshiftOptionRule.S3A_AWS_CREDENTIALS_PROVIDER.key()))) {
            hadoopConf.set(
                    S3RedshiftOptionRule.S3A_AWS_CREDENTIALS_PROVIDER.key(),
                    s3Options.get(S3RedshiftOptionRule.S3A_AWS_CREDENTIALS_PROVIDER.key()));
            hadoopConf.set(
                    "fs.s3a.access.key", s3Options.get(S3RedshiftOptionRule.ACCESS_KEY.key()));
            hadoopConf.set(
                    "fs.s3a.secret.key", s3Options.get(S3RedshiftOptionRule.SECRET_KEY.key()));
        } else {
            hadoopConf.set(
                    S3RedshiftOptionRule.S3A_AWS_CREDENTIALS_PROVIDER.key(),
                    s3Options.get(S3RedshiftOptionRule.S3A_AWS_CREDENTIALS_PROVIDER.key()));
        }
        return hadoopConf;
    }

    private static String formatKey(String protocol, String key) {
        return String.format(S3_FORMAT_KEY, protocol, key);
    }
}
