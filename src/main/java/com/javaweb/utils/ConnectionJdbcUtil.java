package com.javaweb.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application.properties")
public class ConnectionJdbcUtil {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/estatebasic";
	private static final String USER = "root";
	private static final String PASS = "123456789";

	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
}
