/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dtstack.flinkx.connector.stream.sink;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.logical.RowType;

import com.dtstack.flinkx.conf.SyncConf;
import com.dtstack.flinkx.connector.stream.conf.StreamSinkConf;
import com.dtstack.flinkx.connector.stream.converter.StreamRowConverter;
import com.dtstack.flinkx.connector.stream.converter.StreamColumnConverter;
import com.dtstack.flinkx.converter.AbstractRowConverter;
import com.dtstack.flinkx.sink.SinkFactory;
import com.dtstack.flinkx.util.GsonUtil;
import com.dtstack.flinkx.util.TableUtil;

/**
 * Date: 2021/04/07
 * Company: www.dtstack.com
 *
 * @author tudou
 */
public class StreamSinkFactory extends SinkFactory {
    private final StreamSinkConf streamSinkConf;

    public StreamSinkFactory(SyncConf config) {
        super(config);
        streamSinkConf = GsonUtil.GSON.fromJson(GsonUtil.GSON.toJson(config.getWriter().getParameter()), StreamSinkConf.class);
        streamSinkConf.setColumn(config.getWriter().getFieldList());
        super.initFlinkxCommonConf(streamSinkConf);
    }

    @Override
    public DataStreamSink<RowData> createSink(DataStream<RowData> dataSet) {
        StreamOutputFormatBuilder builder = new StreamOutputFormatBuilder();
        builder.setStreamSinkConf(streamSinkConf);
        AbstractRowConverter converter;
        if(useAbstractBaseColumn){
            converter = new StreamColumnConverter();
        }else{
            final RowType rowType = (RowType) TableUtil.getDataType(streamSinkConf.getColumn()).getLogicalType();
            converter = new StreamRowConverter(rowType);
        }

        builder.setConverter(converter);
        return createOutput(dataSet, builder.finish());
    }
}
