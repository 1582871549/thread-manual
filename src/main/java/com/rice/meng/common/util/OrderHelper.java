package com.rice.meng.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class OrderHelper {

    /**
     * 订单号生成(NEW)
     **/
    private static final AtomicInteger SEQ = new AtomicInteger(1000);
    private static final DateTimeFormatter DF_FMT_PREFIX = DateTimeFormatter.ofPattern("yyMMddHHmmssSS");
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");
    private static final LocalDateTime DATE_TIME = LocalDateTime.now(ZONE_ID);
    private static volatile String IP_SUFFIX = null;

    private OrderHelper() {
    }

    public static String generateOrderNo() {
        if (SEQ.get() > 9990) {
            SEQ.getAndSet(1000);
        }
        return DATE_TIME.format(DF_FMT_PREFIX) + getLocalIpSuffix() + SEQ.getAndIncrement();
    }

    private static String getLocalIpSuffix() {
        if (StringUtils.isBlank(IP_SUFFIX)) {
            try {
                synchronized (OrderHelper.class) {
                    if (StringUtils.isBlank(IP_SUFFIX)) {
                        String host = InetAddress.getLocalHost().getHostAddress();
                        if (StringUtils.isNotBlank(host) && host.length() > 4) {
                            IP_SUFFIX = handleHost(host);
                        } else {
                            IP_SUFFIX = RandomUtils.nextString(10, 20);
                        }
                    }
                }
            } catch (Exception e) {
                IP_SUFFIX = RandomUtils.nextString(10, 20);
                log.info("获取IP失败:", e);
            }
        }
        return IP_SUFFIX;
    }

    /**
     * 从Host中截取2位字符参与订单号生成
     *
     * @param host 172.17.0.4  172.17.0.199
     */
    private static String handleHost(String host) {
        String mix = host.trim().split("\\.")[3];
        if (mix.length() != 2) {
            mix = mix.length() == 1 ? "0" + mix : mix;
        }
        return mix.substring(0, 2);
    }

    public static void main(String[] args) {

        List<String> orderNos = Collections.synchronizedList(new ArrayList<>());

        IntStream.range(0, 8000)
                .parallel()
                .forEach(i -> orderNos.add(generateOrderNo()));

        List<String> filterOrderNos = orderNos.stream()
                .distinct()
                .collect(Collectors.toList());

        System.out.println("订单样例：" + orderNos.get(22));
        System.out.println("生成订单数：" + orderNos.size());
        System.out.println("过滤重复后订单数：" + filterOrderNos.size());
        System.out.println("重复订单数：" + (orderNos.size() - filterOrderNos.size()));
    }
}
