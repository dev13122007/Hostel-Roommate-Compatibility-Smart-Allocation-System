package util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class to generate standardized and unique system identifiers.
 */
public class IdGenerator {
    private static final AtomicInteger studentCounter = new AtomicInteger(100);
    private static final AtomicInteger requestCounter = new AtomicInteger(1000);
    private static final AtomicInteger allocationCounter = new AtomicInteger(1000);

    public static synchronized void initCounters(int maxStudentId, int maxRequestId, int maxAllocId) {
        if (maxStudentId >= studentCounter.get()) {
            studentCounter.set(maxStudentId + 1);
        }
        if (maxRequestId >= requestCounter.get()) {
            requestCounter.set(maxRequestId + 1);
        }
        if (maxAllocId >= allocationCounter.get()) {
            allocationCounter.set(maxAllocId + 1);
        }
    }

    public static String generateStudentId() {
        return "ST" + studentCounter.getAndIncrement();
    }

    public static String generateRequestId() {
        return "REQ-" + requestCounter.getAndIncrement();
    }

    public static String generateAllocationId() {
        return "ALC-" + allocationCounter.getAndIncrement();
    }
}
