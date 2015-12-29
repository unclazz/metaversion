package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.LimitOffsetClause;
import org.unclazz.metaversion.entity.OrderByClause;
import org.unclazz.metaversion.entity.SvnLogImport;
import org.unclazz.metaversion.entity.SvnLogImportAndItsStatus;

public interface SvnLogImportMapper {
	@Select("select nextval('svn_log_import_seq')")
	int selectNextVal();
	
	@Select("select sli.id, sli.start_date startDate, "
			+ "sli.end_date endDate, slis.id statusId, slis.code statusCode "
			+ "rp.id repositoryId, rp.name repositoryName "
			+ "from svn_log_import sli "
			+ "inner join svn_log_import_status slis "
			+ "on sli.status_id = slis.id "
			+ "inner join repository rp "
			+ "on sli.repository_id = rp.id "
			+ "where sli.id = (select max(id) from svn_log_import)")
	SvnLogImportAndItsStatus selectOneByMaxId();
	
	@Select("select sli.id, sli.start_date startDate, "
			+ "sli.end_date endDate, slis.id statusId, slis.code statusCode, "
			+ "rp.id repositoryId, rp.name repositoryName "
			+ "from svn_log_import sli "
			+ "inner join svn_log_import_status slis "
			+ "on sli.status_id = slis.id "
			+ "inner join repository rp "
			+ "on sli.repository_id = rp.id "
			+ "${orderBy} ${limitOffset} ")
	List<SvnLogImportAndItsStatus> selectAll(@Param("orderBy") OrderByClause orderBy, @Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Insert("insert into svn_log_import (id, start_date, end_date, status_id, repository_id, create_user_id) "
			+ "values (#{svnLogImport.id}, #{svnLogImport.startDate}, #{svnLogImport.endDate}, "
			+ "#{svnLogImport.statusId}, #{svnLogImport.repositoryId}, #{auth.id})")
	int insert(@Param("svnLogImport") SvnLogImport svnLogImport, @Param("auth") MVUserDetails auth);

	@Update("update svn_log_import "
			+ "set start_date = #{svnLogImport.startDate}, end_date = #{svnLogImport.endDate}, "
			+ "status_id = #{svnLogImport.statusId}, repository_id = #{svnLogImport.repositoryId} "
			+ "where id = #{svnLogImport.id} ")
	int update(@Param("svnLogImport") SvnLogImport svnLogImport, @Param("auth") MVUserDetails auth);
}
