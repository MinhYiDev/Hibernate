package com.javaweb.repository;

import java.util.List;
import java.util.Map;

import com.javaweb.builder.BuildingSearchBuilder;
import com.javaweb.repository.entity.BuildingEntity;

public interface BuildingRepository {
//	List<BuildingEntity> findAll(Map<String,Object> params,List<String> typeCode);
	List<BuildingEntity> findAll(BuildingSearchBuilder buildingSearchBuilder);

}
