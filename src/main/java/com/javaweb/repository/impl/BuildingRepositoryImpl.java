package com.javaweb.repository.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.javaweb.repository.BuildingRepository;
import com.javaweb.repository.entity.BuildingEntity;

@Repository
public class BuildingRepositoryImpl implements BuildingRepository {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/estatebasic";
	private static final String USER = "root";
	private static final String PASS = "123456789";

	public static void joinTable(Map<String, Object> params, List<String> typeCode, StringBuilder sql) {
		String staffId = (String) params.get("staffId");
		if (staffId != null && !staffId.equals("")) {
			sql.append(" INNER JOIN assignmentbuilding ON b.id = assignmentbuilding.buildingid");
		}
		if (typeCode != null && typeCode.size() != 0) {
			sql.append(" INNER JOIN buildingrenttype ON b.id = buildingrenttype.buildingid");
			sql.append(" INNER JOIN renttype ON renttype.id = buildingrenttype.buildingrenttype");
		}

		String rentAreaTo = (String)params.get("areaTo");
		String rentAreaFrom = (String) params.get("areaFrom");

		if ((rentAreaTo != null && !rentAreaTo.equals("")) || (rentAreaFrom != null && !rentAreaFrom.equals(""))) {
			sql.append(" INNER JOIN rentarea ON rentarea.buildingid = b.id");
		}

	}

	public static void queryNormal(Map<String, Object> params, List<String> typeCode) {

	}

	@Override
	public List<BuildingEntity> findAll(Map<String, Object> params, List<String> typeCode) {
		StringBuilder sql = new StringBuilder(
				"SELECT b.id,b.name,b.districtid,b.street,b.ward,b.numberofbasement,b.floorarea,b.rentprice,"
						+ "b.managername,b.managerphonenumber,b.servicefee,b.brokeragefee FROM building b ");

		List<BuildingEntity> result = new ArrayList<BuildingEntity>();
		StringBuilder where = new StringBuilder(" where 1=1 ");
		joinTable(params, typeCode, sql);
		System.out.println(sql);
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());

			while (rs.next()) {
				BuildingEntity building = new BuildingEntity();
				building.setName(rs.getString("name"));
				building.setStreet(rs.getString("street"));
				building.setWard(rs.getString("ward"));
				building.setNumberofbasement(rs.getInt("numberofbasement"));
				result.add(building);
			}

			System.out.println("OK SQL");
		} catch (Exception e) {
			System.out.println("NO SQL");
			System.out.println(e.getMessage());
		}
		return result;
	}
}
