package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.LimitOffsetClause;
import org.unclazz.metaversion.entity.OrderByClause;
import org.unclazz.metaversion.entity.LogImport;
import org.unclazz.metaversion.entity.LogImportAndItsStatus;

public interface LogImportMapper {
	@Select("SELECT nextval('log_import_seq')")
	int selectNextVal();
	
	@Select("SELECT sli.id, sli.start_date startDate, "
			+ "sli.end_date endDate, slis.id statusId, slis.code statusCode "
			+ "rp.id repositoryId, rp.name repositoryName "
			+ "FROM log_import sli "
			+ "INNER JOIN log_import_status slis "
			+ "ON sli.status_id = slis.id "
			+ "INNER JOIN repository rp "
			+ "ON sli.repository_id = rp.id "
			+ "WHERE sli.id = (SELECT max(id) FROM log_import)")
	LogImportAndItsStatus selectOneByMaxId();
	
	@Select("SELECT sli.id, sli.start_date startDate, "
			+ "sli.end_date endDate, slis.id statusId, slis.code statusCode, "
			+ "rp.id repositoryId, rp.name repositoryName "
			+ "FROM log_import sli "
			+ "INNER JOIN log_import_status slis "
			+ "ON sli.status_id = slis.id "
			+ "INNER JOIN repository rp "
			+ "ON sli.repository_id = rp.id "
			+ "${orderBy} ${limitOffset} ")
	List<LogImportAndItsStatus> selectAll(@Param("orderBy") OrderByClause orderBy, @Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Insert("INSERT INTO log_import (id, start_date, end_date, status_id, repository_id, create_user_id) "
			+ "VALUES (#{svnLogImport.id}, #{svnLogImport.startDate}, #{svnLogImport.endDate}, "
			+ "#{svnLogImport.statusId}, #{svnLogImport.repositoryId}, #{auth.id})")
	int insert(@Param("svnLogImport") LogImport svnLogImport, @Param("auth") MVUserDetails auth);

	@Update("UPDATE log_import "
			+ "SET start_date = #{svnLogImport.startDate}, end_date = #{svnLogImport.endDate}, "
			+ "status_id = #{svnLogImport.statusId}, repository_id = #{svnLogImport.repositoryId} "
			+ "WHERE id = #{svnLogImport.id} ")
	int update(@Param("svnLogImport") LogImport svnLogImport, @Param("auth") MVUserDetails auth);
}
