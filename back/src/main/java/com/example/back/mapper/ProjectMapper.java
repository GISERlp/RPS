package com.example.back.mapper;

import com.example.back.entity.Project;
import org.apache.ibatis.annotations.*;

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
    @Delete("DELETE FROM project WHERE userName = #{userName} AND projectName = #{projectName}")
    void deleteByUserAndProject(@Param("userName") String userName, @Param("projectName") String projectName);

    @Update("UPDATE project SET siteId = #{siteId}, model = #{model}, startDate = #{startDate}, endDate = #{endDate}, description = #{description}, updated_at = #{updated_at} WHERE userName = #{userName} AND projectName = #{projectName}")
    void updateProjectConfig(
            @Param("userName") String userName,
            @Param("projectName") String projectName,
            @Param("siteId") String siteId,
            @Param("model") String model,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("description") String description,
            @Param("updated_at") Date updatedAt
    );


}