package com.example.back.mapper;

import com.example.back.entity.Project;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface ProjectMapper {

    @Select("SELECT EXISTS(SELECT 1 FROM project WHERE userName = #{userName} AND projectName = #{projectName})")
    boolean existsByUserAndProjectName(@Param("userName") String userName, @Param("projectName") String projectName);

    @Select("SELECT * FROM project WHERE userName = #{userName} AND projectName = #{projectName}")
    List<Project> findByUserAndProjectName(@Param("userName") String userName, @Param("projectName") String projectName);

    @Insert("INSERT INTO project (userName, projectName,siteId,model,startDate,endDate,description,created_at,updated_at ) VALUES (#{userName}, #{projectName}, #{siteId}, #{model}, #{startDate}, #{endDate}, #{description}, #{created_at}, #{updated_at})")
    void createProject(@Param("userName") String userName, @Param("projectName") String projectName, @Param("siteId") String siteId, @Param("model") String model, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("description") String description, @Param("created_at") Date createdAt, @Param("updated_at") Date updatedAt);

    @Select("SELECT * FROM project WHERE userName = #{userName}")
    List<Project> findByUser(String userName);
}