/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package backtype.storm.nimbus;

import backtype.storm.Config;
import com.alibaba.jstorm.client.ConfigExtension;
import com.alibaba.jstorm.utils.NetWorkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

public class NimbusInfo implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(NimbusInfo.class);
    private static final String DELIM = ":";

    private String host;
    private int port;
    private boolean isLeader;

    public NimbusInfo(String host, int port, boolean isLeader) {
        this.host = host;
        this.port = port;
        this.isLeader = isLeader;
    }

    public static NimbusInfo parse(String nimbusInfo) {
        String[] hostAndPort = nimbusInfo.split(DELIM);
        if(hostAndPort != null && hostAndPort.length == 2) {
            return new NimbusInfo(hostAndPort[0], Integer.parseInt(hostAndPort[1]), false);
        } else {
            throw new RuntimeException("nimbusInfo should have format of host:port, invalid string " + nimbusInfo);
        }
    }

    public static NimbusInfo fromConf(Map conf) {
        String host;
        if (!ConfigExtension.isNimbusUseIp(conf)) {
            host = NetWorkUtils.hostname();
        } else {
            host = NetWorkUtils.ip();
        }

        int port = Integer.parseInt(conf.get(Config.NIMBUS_THRIFT_PORT).toString());
        return new NimbusInfo(host, port, false);
    }

    public String toHostPortString() {
        return String.format("%s%s%s",host,DELIM,port);
    }

    public boolean isLeader() {
        return isLeader;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getHostPort(){
        return host + ":" + port;
    }

    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NimbusInfo)) return false;

        NimbusInfo that = (NimbusInfo) o;

        if (isLeader != that.isLeader) return false;
        if (port != that.port) return false;
        return host.equals(that.host);
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        result = 31 * result + (isLeader ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NimbusInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", isLeader=" + isLeader +
                '}';
    }
}
