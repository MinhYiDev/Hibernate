package com.javaweb.repository.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.javaweb.builder.BuildingSearchBuilder;
import com.javaweb.repository.BuildingRepository;
import com.javaweb.repository.entity.BuildingEntity;
import com.javaweb.utils.ConnectionJdbcUtil;
import com.javaweb.utils.NumberUtil;
import com.javaweb.utils.StringUtil;

@Repository
public class BuildingRepositoryImpl implements BuildingRepository {

	public static void joinTable(BuildingSearchBuilder buildingSearchBuilder, StringBuilder sql) {
		Long staffId = buildingSearchBuilder.getStaffId();

		if (staffId != null) {
			sql.append(" INNER JOIN assignmentbuilding ON b.id = assignmentbuilding.buildingid");
		}

		List<String> typeCode = buildingSearchBuilder.getTypeCode();

		if (typeCode != null && typeCode.size() != 0) {
			sql.append(" INNER JOIN buildingrenttype ON b.id = buildingrenttype.buildingid ");
			sql.append(" INNER JOIN renttype ON renttype.id = buildingrenttype.renttypeid ");
		}
//
//		String rentAreaTo = (String) params.get("areaTo");
//		String rentAreaFrom = (String) params.get("areaFrom");
//
//		if (StringUtil.checkString(rentAreaTo) == true || StringUtil.checkString(rentAreaFrom) == true) {
//			sql.append(" INNER JOIN rentarea ON rentarea.buildingid = b.id ");
//		}

	}

	public static void queryNormal(BuildingSearchBuilder buildingSearchBuilder, StringBuilder where) {
//		for (Map.Entry<String, Object> it : params.entrySet()) {
//			if (!it.getKey().equals("staffId") && !it.getKey().equals("typeCode") && !it.getKey().startsWith("area")
//					&& !it.getKey().startsWith("rentPrice")) {
//				String value = it.getValue().toString();
//				if (StringUtil.checkString(value)) {
//					if (NumberUtil.isNumber(value) == true) {
//						where.append(" AND b." + it.getKey() + " = " + value);
//					} else {
//						where.append(" AND b." + it.getKey() + " LIKE '%" + value + "%' ");
//					}
//				}
//			}
//		}

		try {
			Field[] fileds = BuildingSearchBuilder.class.getDeclaredFields();

			for (Field item : fileds) {
				item.setAccessible(true);
				String fieldName = item.getName();

				if (!fieldName.equals("staffId") && !fieldName.equals("typeCode") && !fieldName.startsWith("area")
						&& !fieldName.startsWith("rentPrice")) {
					Object value = item.get(buildingSearchBuilder);
					if (value != null) {
						if (item.getType().getName().equals("java.lang.Long")
								|| item.getType().getName().equals("java.lang.Integer")) {
							where.append(" AND b." + fieldName + " = " + value);
						} else if (item.getType().getName().equals("java.lang.String")) {
							where.append(" AND b." + fieldName + " LIKE '%" + value + "%' ");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void querySpectial(BuildingSearchBuilder buildingSearchBuilder, StringBuilder where) {
		Long staffId = buildingSearchBuilder.getStaffId();

		if (staffId != null) {
			where.append(" AND assignmentbuilding.staffid = " + staffId);
		}

		Long rentAreaTo = buildingSearchBuilder.getRentPriceTo();
		Long rentAreaFrom = buildingSearchBuilder.getRentPriceFrom();

		if (rentAreaFrom != null || rentAreaTo != null) {
			where.append(" AND EXISTS (SELECT * FROM rentarea r WHERE b.id = r.buildingid ");
			if (rentAreaFrom != null) {
				where.append(" AND r.value >= " + rentAreaFrom);

			}
			if (rentAreaTo != null) {
				where.append(" AND r.value <= " + rentAreaTo);
			}

			where.append(" ) ");
		}

		Long rentPriceTo = buildingSearchBuilder.getRentPriceTo();
		Long rentPriceFrom = buildingSearchBuilder.getRentPriceFrom();

		if (rentPriceTo != null || rentPriceFrom != null) {
			if (rentPriceFrom != null) {
				where.append(" AND b.rentprice >= " + rentPriceFrom);
			}
			if (rentPriceTo != null) {
				where.append(" AND b.rentprice <= " + rentPriceTo);
			}
		}

		// java 7
//		if (typeCode != null && typeCode.size() != 0) {
//			List<String> code = new ArrayList<String>();
//			for (String item : typeCode) {
//				code.add("'" + item + "'");
//			}
//			where.append(" AND renttype.code IN(" + String.join(",", code) + ") ");
//		}

		// java 8
		List<String> typeCode = buildingSearchBuilder.getTypeCode();

		if (typeCode != null && typeCode.size() != 0) {
			where.append(" AND (");
			String sql = typeCode.stream().map(it -> "renttype.code LIKE" + "'%" + it + "%'")
					.collect(Collectors.joining(" OR "));
			where.append(sql);
			where.append(" ) ");

		}
	}

	@Override
	public List<BuildingEntity> findAll(BuildingSearchBuilder buildingSearchBuilder) {
		StringBuilder sql = new StringBuilder(
				"SELECT b.id,b.name,b.districtid,b.street,b.ward,b.numberofbasement,b.floorarea,b.rentprice,"
						+ "b.managername,b.managerphonenumber,b.servicefee,b.brokeragefee FROM building b ");

		joinTable(buildingSearchBuilder, sql);
		StringBuilder where = new StringBuilder(" where 1=1 ");
		queryNormal(buildingSearchBuilder, where);
		querySpectial(buildingSearchBuilder, where);
		sql.append(where);
		sql.append(" GROUP BY b.id ");
		List<BuildingEntity> result = new ArrayList<BuildingEntity>();

		System.out.println(sql);
		try {
			Connection conn = ConnectionJdbcUtil.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());

			while (rs.next()) {
				BuildingEntity buildingEntity = new BuildingEntity();
				buildingEntity.setId(rs.getLong("b.id"));
				buildingEntity.setName(rs.getString("b.name"));
				buildingEntity.setWard(rs.getString("b.ward"));
				buildingEntity.setDistrictid(rs.getLong("b.districtid"));
				buildingEntity.setStreet(rs.getString("b.street"));
				buildingEntity.setFloorArea(rs.getLong("b.floorarea"));
				buildingEntity.setRentPrice(rs.getLong("b.rentprice"));
				buildingEntity.setServiceFee(rs.getString("b.servicefee"));
				buildingEntity.setBrokerageFee(rs.getLong("b.brokeragefee"));
				buildingEntity.setManagerName(rs.getString("b.managername"));
				buildingEntity.setManagerPhoneNumber(rs.getString("b.managerphonenumber"));
				result.add(buildingEntity);
			}
			System.out.println("OK SQL");
		} catch (Exception e) {
			System.out.println("NO SQL");
			System.out.println(e.getMessage());
		}

		return result;
	}
}
