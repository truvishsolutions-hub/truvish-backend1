package com.truvish.truvishbackend.monitoring;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

@Component
public class DatabaseMetrics implements StatementInspector {

    private final AtomicLong readCount = new AtomicLong(0);
    private final AtomicLong writeCount = new AtomicLong(0);

    private final AtomicLong selectCount = new AtomicLong(0);
    private final AtomicLong insertCount = new AtomicLong(0);
    private final AtomicLong updateCount = new AtomicLong(0);
    private final AtomicLong deleteCount = new AtomicLong(0);

    @Override
    public String inspect(String sql) {

        if (sql == null || sql.isBlank()) {
            return sql;
        }

        String normalizedSql = sql
                .trim()
                .toLowerCase(Locale.ROOT);

        if (normalizedSql.startsWith("select")
                || normalizedSql.startsWith("with")) {

            selectCount.incrementAndGet();
            readCount.incrementAndGet();

        } else if (normalizedSql.startsWith("insert")) {

            insertCount.incrementAndGet();
            writeCount.incrementAndGet();

        } else if (normalizedSql.startsWith("update")) {

            updateCount.incrementAndGet();
            writeCount.incrementAndGet();

        } else if (normalizedSql.startsWith("delete")) {

            deleteCount.incrementAndGet();
            writeCount.incrementAndGet();
        }

        return sql;
    }

    public long getReadCount() {
        return readCount.get();
    }

    public long getWriteCount() {
        return writeCount.get();
    }

    public long getSelectCount() {
        return selectCount.get();
    }

    public long getInsertCount() {
        return insertCount.get();
    }

    public long getUpdateCount() {
        return updateCount.get();
    }

    public long getDeleteCount() {
        return deleteCount.get();
    }

    public long getTotalCount() {
        return readCount.get() + writeCount.get();
    }

    public void reset() {

        readCount.set(0);
        writeCount.set(0);

        selectCount.set(0);
        insertCount.set(0);
        updateCount.set(0);
        deleteCount.set(0);
    }
}