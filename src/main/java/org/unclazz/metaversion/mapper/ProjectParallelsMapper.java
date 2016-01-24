package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.entity.ProjectParallels;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;

public interface ProjectParallelsMapper {
	@Select("SELECT self_project_id selfProjectId, "
			+ "		path path, "
			+ "		parallel_type parallelType, "
			+ "		self_min_revision selfMinRevision, "
			+ "		self_min_commit_date selfMinCommitDate, "
			+ "		self_max_revision selfMaxRevision, "
			+ "		self_max_commit_date selfMaxCommitDate, "
			+ "		other_project_id otherProjectId, "
			+ "		other_project_name otherProjectName, "
			+ "		other_project_code otherProjectCode, "
			+ "		other_project_responsible_person otherProjectResponsiblePerson, "
			+ "		other_min_revision otherMinRevision, "
			+ "		other_min_commit_date otherMinCommitDate, "
			+ "		other_max_revision otherMaxRevision, "
			+ "		other_max_commit_date otherMaxCommitDate "
			+ "FROM project_parallels_view "
			+ "WHERE self_project_id = #{projectId} "
			+ "${orderBy} ${limitOffset} ")
	List<ProjectParallels> selectByProjectId(
			@Param("projectId") int projectId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT count(1) "
			+ "FROM project_parallels_view "
			+ "WHERE self_project_id = #{projectId} ")
	int selectCountByProjectId(@Param("projectId") int projectId);
}
