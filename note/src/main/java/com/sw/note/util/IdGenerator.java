package com.sw.note.util;

/**
 * @author sw
 */
public class IdGenerator {

    private volatile static SnowflakeIdWorker snowflakeIdWorker;

    public static long next() {
        if (snowflakeIdWorker == null) {
            synchronized (IdGenerator.class) {
                if (snowflakeIdWorker == null) {
                    snowflakeIdWorker = new SnowflakeIdWorker(0, 0);
                }
            }
        }
        return snowflakeIdWorker.nextId();
    }
}
