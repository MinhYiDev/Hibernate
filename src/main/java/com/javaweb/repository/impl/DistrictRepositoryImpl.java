package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.springframework.stereotype.Repository;

import com.javaweb.repository.DistrictRepository;
import com.javaweb.repository.entity.DistrictEntity;
import com.javaweb.utils.ConnectionJdbcUtil;

@Repository
public class DistrictRepositoryImpl implements DistrictRepository {

	@Override
	public DistrictEntity findNameById(Long id) {
		StringBuilder sql = new StringBuilder("SELECT d.name FROM district d WHERE d.id = " + id + ";");
		DistrictEntity districtEntity = new DistrictEntity();
		try {
			Connection conn = ConnectionJdbcUtil.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());

			while (rs.next()) {
				districtEntity.setName(rs.getString("name"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return districtEntity;
	}
}
