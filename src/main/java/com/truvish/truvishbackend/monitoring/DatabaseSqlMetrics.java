package com.truvish.truvishbackend.monitoring;

import java.util.concurrent.atomic.AtomicLong;

import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * ============================================================
 * DATABASE SQL METRICS
 * ============================================================
 *
 * Hibernate ke through execute hone wale SQL statements ko count
 * karta hai.
 *
 * READ:
 *     SELECT
 *
 * WRITE:
 *     INSERT + UPDATE + DELETE
 *
 * IMPORTANT:
 * Ye SQL STATEMENTS count karta hai,
 * rows affected nahi.
 *
 * Example:
 *
 * 1 API request
 *      |
 *      +---- SELECT
 *      +---- SELECT
 *      +---- SELECT
 *
 * READ = 3
 *
 * Counters application restart hone par 0 se start honge.
 */
public class DatabaseSqlMetrics implements StatementInspector {

    // ============================================================
    // SQL COUNTERS
    // ============================================================

    private static final AtomicLong SELECT_COUNT = new AtomicLong(0);

    private static final AtomicLong INSERT_COUNT = new AtomicLong(0);

    private static final AtomicLong UPDATE_COUNT = new AtomicLong(0);

    private static final AtomicLong DELETE_COUNT = new AtomicLong(0);


    // ============================================================
    // HIBERNATE SQL INSPECTOR
    // ============================================================

    @Override
    public String inspect(String sql) {

        // SQL null ya blank hai to kuch mat karo
        if (sql == null || sql.isBlank()) {
            return sql;
        }

        /*
         * Original SQL ko modify nahi karna hai.
         *
         * Sirf checking ke liye normalized copy bana rahe hain.
         */
        String normalizedSql = sql.trim().toLowerCase();

        /*
         * Hibernate kabhi-kabhi SQL ke beginning mein comments
         * add kar sakta hai.
         *
         * Example:
         *
         * /* select client *\/
         * select ...
         *
         * Isliye pehle comments remove karenge.
         */
        normalizedSql = removeLeadingComments(normalizedSql);


        // ========================================================
        // READ
        // ========================================================

        if (normalizedSql.startsWith("select")) {

            SELECT_COUNT.incrementAndGet();

        }

        // ========================================================
        // WITH / CTE
        // ========================================================
        /*
         * Normal CTE ko READ maana ja raha hai.
         *
         * Example:
         *
         * WITH clients AS (...)
         * SELECT ...
         *
         * NOTE:
         * Agar future mein write-CTE use karoge to usko separately
         * handle karna padega.
         */

        else if (normalizedSql.startsWith("with")) {

            SELECT_COUNT.incrementAndGet();

        }

        // ========================================================
        // INSERT
        // ========================================================

        else if (normalizedSql.startsWith("insert")) {

            INSERT_COUNT.incrementAndGet();

        }

        // ========================================================
        // UPDATE
        // ========================================================

        else if (normalizedSql.startsWith("update")) {

            UPDATE_COUNT.incrementAndGet();

        }

        // ========================================================
        // DELETE
        // ========================================================

        else if (normalizedSql.startsWith("delete")) {

            DELETE_COUNT.incrementAndGet();

        }


        /*
         * IMPORTANT:
         *
         * Hibernate ko ORIGINAL SQL hi return karna hai.
         */
        return sql;
    }


    // ============================================================
    // REMOVE LEADING SQL COMMENTS
    // ============================================================

    private String removeLeadingComments(String sql) {

        String result = sql.trim();

        while (true) {

            // ----------------------------------------------------
            // /* ... */ comment
            // ----------------------------------------------------

            if (result.startsWith("/*")) {

                int endComment = result.indexOf("*/");

                if (endComment >= 0) {

                    result = result
                            .substring(endComment + 2)
                            .trim();

                    continue;
                }
            }


            // ----------------------------------------------------
            // -- comment
            // ----------------------------------------------------

            if (result.startsWith("--")) {

                int endLine = result.indexOf('\n');

                if (endLine >= 0) {

                    result = result
                            .substring(endLine + 1)
                            .trim();

                    continue;
                }
            }


            break;
        }

        return result;
    }


    // ============================================================
    // SELECT
    // ============================================================

    public static long getSelectCount() {

        return SELECT_COUNT.get();
    }


    // ============================================================
    // INSERT
    // ============================================================

    public static long getInsertCount() {

        return INSERT_COUNT.get();
    }


    // ============================================================
    // UPDATE
    // ============================================================

    public static long getUpdateCount() {

        return UPDATE_COUNT.get();
    }


    // ============================================================
    // DELETE
    // ============================================================

    public static long getDeleteCount() {

        return DELETE_COUNT.get();
    }


    // ============================================================
    // READ
    // ============================================================

    public static long getReadCount() {

        return SELECT_COUNT.get();
    }


    // ============================================================
    // WRITE
    // ============================================================

    public static long getWriteCount() {

        return INSERT_COUNT.get()
                + UPDATE_COUNT.get()
                + DELETE_COUNT.get();
    }


    // ============================================================
    // TOTAL
    // ============================================================

    public static long getTotalCount() {

        return SELECT_COUNT.get()
                + INSERT_COUNT.get()
                + UPDATE_COUNT.get()
                + DELETE_COUNT.get();
    }
}