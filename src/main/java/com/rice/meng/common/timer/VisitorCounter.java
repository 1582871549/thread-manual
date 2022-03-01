package com.rice.meng.common.timer;

import lombok.Data;

import java.util.concurrent.atomic.LongAdder;

@Data
public class VisitorCounter {

    private LongAdder count = new LongAdder();
    private LongAdder totalTime = new LongAdder();
}
