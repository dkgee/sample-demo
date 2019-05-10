package com.example.demo.clickhouse;

import java.sql.*;

/**
 * Description：CK的JDBC连接方式
 * Author；JinHuatao
 * Date: 2019/4/24 15:52
 */
public class ClickhouseClientDemo {

    public static void main(String[] args) {
        try {
            //batchQuery();
            simpleQuery();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void simpleQuery() throws Exception{
        Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://172.30.154.241:8123");

        Statement stmt = connection.createStatement();
//        ResultSet rs = stmt.executeQuery("SELECT (number % 3 + 1) as n, sum(number) FROM numbers(10000000) GROUP BY n");

        ResultSet rs = stmt.executeQuery("INSERT INTO alarm.ct_alarm_info VALUES ('3', 'xxx', 'indoor-002', 569, 'ssss', '2019-05-08 11:32:24');");

        while (rs.next()) {
            System.out.println(rs.getInt(1) + "\t" + rs.getLong(2));
        }
    }

    public static void batchQuery() throws Exception{
        Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://172.30.154.241:9000");

        Statement stmt = connection.createStatement();
        stmt.executeQuery("drop table if exists test_jdbc_example");
        stmt.executeQuery("create table test_jdbc_example(day Date, name String, age UInt8) Engine=Log");

        PreparedStatement pstmt = connection.prepareStatement("INSERT INTO test_jdbc_example VALUES(?, ?, ?)");

        for (int i = 0; i < 200; i++) {
            pstmt.setDate(1, new Date(System.currentTimeMillis()));
            pstmt.setString(2, "Zhang San" + i);
            pstmt.setByte(3, (byte)i);
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        stmt.executeQuery("drop table test_jdbc_example");
    }
}
