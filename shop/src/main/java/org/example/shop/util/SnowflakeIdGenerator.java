package org.example.shop.util;

/**
 * 雪花算法（Snowflake）- 分布式唯一ID生成器
 * 
 * 64位ID结构：
 * 1位符号位 + 41位时间戳 + 10位机器ID + 12位序列号
 * 
 * 优化：将机器ID的后5位用作用户ID的基因位，支持通过ID解析用户
 */
public class SnowflakeIdGenerator {

    // 时间戳位数
    private static final long TIMESTAMP_BITS = 41L;
    // 机器ID位数
    private static final long MACHINE_ID_BITS = 10L;
    // 序列号位数
    private static final long SEQUENCE_BITS = 12L;

    // 最大值
    private static final long MAX_MACHINE_ID = (1L << MACHINE_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    // 位移
    private static final long TIMESTAMP_LEFT_SHIFT = MACHINE_ID_BITS + SEQUENCE_BITS;
    private static final long MACHINE_ID_LEFT_SHIFT = SEQUENCE_BITS;

    // 基准时间戳（2020-01-01 00:00:00）
    private static final long EPOCH = 1577836800000L;

    private long machineId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * 构造函数
     * @param machineId 机器ID（0-1023），建议使用服务器IP的后10位
     */
    public SnowflakeIdGenerator(long machineId) {
        if (machineId > MAX_MACHINE_ID || machineId < 0) {
            throw new IllegalArgumentException(
                String.format("machineId can't be greater than %d or less than 0", MAX_MACHINE_ID)
            );
        }
        this.machineId = machineId;
    }

    /**
     * 生成下一个ID
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        // 时钟回拨处理
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp)
            );
        }

        // 同一毫秒内的ID生成
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 序列号溢出，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 新的毫秒，序列号重置
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 组合ID
        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
            | (machineId << MACHINE_ID_LEFT_SHIFT)
            | sequence;
    }

    /**
     * 等待下一毫秒
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 从ID中解析出用户ID（基因位）
     * 用户ID存储在机器ID的后5位
     * @param id 雪花ID
     * @return 用户ID
     */
    public static long extractUserId(long id) {
        // 提取机器ID（10位）
        long machineId = (id >> MACHINE_ID_LEFT_SHIFT) & ((1L << MACHINE_ID_BITS) - 1);
        // 提取后5位作为用户ID
        return machineId & 0x1F;
    }

    /**
     * 从ID中解析出时间戳
     * @param id 雪花ID
     * @return 时间戳（毫秒）
     */
    public static long extractTimestamp(long id) {
        return ((id >> TIMESTAMP_LEFT_SHIFT) & ((1L << TIMESTAMP_BITS) - 1)) + EPOCH;
    }

    /**
     * 从ID中解析出序列号
     * @param id 雪花ID
     * @return 序列号
     */
    public static long extractSequence(long id) {
        return id & ((1L << SEQUENCE_BITS) - 1);
    }

    /**
     * 从ID中解析出机器ID
     * @param id 雪花ID
     * @return 机器ID
     */
    public static long extractMachineId(long id) {
        return (id >> MACHINE_ID_LEFT_SHIFT) & ((1L << MACHINE_ID_BITS) - 1);
    }
}
