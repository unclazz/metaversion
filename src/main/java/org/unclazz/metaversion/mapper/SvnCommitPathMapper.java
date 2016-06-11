package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.ProjectChangedPath;
import org.unclazz.metaversion.entity.SvnCommitPathWithBranchName;
import org.unclazz.metaversion.entity.SvnCommitPathWithRawInfo;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;

/**
 * コミットパス情報のためのORマッパー.
 */
public interface SvnCommitPathMapper {
	/**
	 * コミットパス情報のIDを採番する.
	 * @return ID
	 */
	@Select("SELECT nextval('svn_commit_path_seq') ")
	int selectNextVal();
	
	/**
	 * コミット情報として取り込まれた既知のパスのうち条件にマッチするものを返す.
	 * リポジトリ横断的に検索を行う。
	 * またプロジェクトとの紐付けのないコミットのパスは結果から除外される。
	 * @param partialPath 部分パス文字列
	 * @param limitOffset LIMIT/OFFSET句指定
	 * @return パスのリスト
	 */
	@Select("SELECT path "
			+ "FROM svn_commit_path cp "
			+ "INNER JOIN project_svn_commit pc "
			+ "ON cp.commit_id = pc.commit_id "
			+ "WHERE path like ('%' || #{partialPath} || '%') "
			+ "GROUP BY path "
			+ "ORDER BY path ${limitOffset} ")
	List<String> selectPathByPartialPath(@Param("partialPath") String partialPath, 
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	/**
	 * コミット情報として取り込まれた既知のパスのうち条件にマッチするものを返す.
	 * プロジェクトとの紐付けのないコミットのパスも結果に含まれる。
	 * @param repositoryId リポジトリID
	 * @param partialPath 部分パス文字列
	 * @param limitOffset LIMIT/OFFSET句指定
	 * @return パスのリスト
	 */
	@Select("SELECT path "
			+ "FROM svn_repository_path_view "
			+ "WHERE repository_id = #{repositoryId} "
			+ "AND path like ('%' || #{partialPath} || '%') "
			+ "${limitOffset} ")
	List<String> selectPathByRepositoryIdAndPartialPath(
			@Param("repositoryId") int repositoryId, 
			@Param("partialPath") String partialPath, 
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	
	/**
	 * コミットIDをキーにコミットパス情報を検索して返す.
	 * @param commitId コミットID
	 * @param orderBy ORDER BY句指定
	 * @param limitOffset LIMIT/OFFSET句指定
	 * @return コミットパス情報のリスト
	 */
	@Select("SELECT id, commit_id commitId, change_type_id changeTypeId, "
			+ "path, branch_path_segment branchName "
			+ "FROM svn_commit_path "
			+ "WHERE commit_id = #{commitId} "
			+ "${orderBy} ${limitOffset} ")
	List<SvnCommitPathWithBranchName> selectBySvnCommitId(
			@Param("commitId") int commitId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	/**
	 * {@link #selectBySvnCommitId(int, OrderByClause, LimitOffsetClause)}と同じ条件で検索し
	 * 結果件数を返す.
	 * @param commitId コミットID
	 * @return コミットパス情報の件数
	 */
	@Select("SELECT count(1) FROM svn_commit_path WHERE commit_id = #{commitId} ")
	int selectCountBySvnCommitId(@Param("commitId") int commitId);
	
	/**
	 * コミットIDをキーにプロジェクト変更パス情報を検索して返す.
	 * @param projectId プロジェクトID
	 * @param orderBy ORDER BY句指定
	 * @param limitOffset LIMIT/OFFSET句指定
	 * @return プロジェクト変更パス情報のリスト
	 */
	@Select("SELECT path, svn_repository_id repositoryId, svn_repository_name repositoryName, " 
			+ "commit_count commitCount, min_revision minRevision, max_revision maxRevision, " 
			+ "min_commit_date minCommitDate, max_commit_date maxCommitDate " 
			+ "FROM project_changedpath_view " 
			+ "WHERE project_id = #{projectId} " 
			+ "${orderBy} ${limitOffset} ")
	List<ProjectChangedPath> selectByProjectId(
			@Param("projectId") int projectId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	/**
	 * {@link #selectByProjectId(int, OrderByClause, LimitOffsetClause)}と同じ条件で検索し
	 * 結果件数を返す.
	 * @param projectId プロジェクトID
	 * @return プロジェクト変更パスの件数
	 */
	@Select("SELECT count(1) " 
			+ "FROM  project_changedpath_view " 
			+ "WHERE project_id = #{projectId} ")
	int selectCountByProjectId(@Param("projectId") int projectId);
	
	/**
	 * コミットIDをキーにコミットパス情報を検索しブランチ名のみ集約して返す.
	 * @param commitId コミットID
	 * @return ブランチ名のリスト
	 */
	@Select("SELECT branch_path_segment "
			+ "FROM svn_commit_path "
			+ "WHERE commit_id = #{commitId} "
			+ "GROUP BY branch_path_segment ")
	List<String> selecBranchNameByCommitId(@Param("commitId") int commitId);
	
	/**
	 * コミットパス情報を登録する.
	 * @param path コミットパス情報
	 * @param auth 認証情報
	 * @return 登録件数
	 */
	@Insert("INSERT INTO svn_commit_path "
			+ "(id, commit_id, change_type_id, path, "
			+ "raw_path, base_path_segment, branch_path_segment, create_user_id) "
			+ "VALUES (#{path.id}, #{path.commitId}, #{path.changeTypeId}, #{path.path}, "
			+ "#{path.rawPath}, #{path.basePathSegment}, #{path.branchPathSegment}, #{auth.id}) ")
	int insert(@Param("path") SvnCommitPathWithRawInfo path, @Param("auth") MVUserDetails auth);
	
	/**
	 * リポジトリに紐づくコミットパス情報を一括で削除する.
	 * @param repositoryId リポジトリID
	 * @return 削除件数
	 */
	@Delete("DELETE FROM svn_commit_path "
			+ "WHERE commit_id IN ("
			+ "	SELECT id "
			+ "	FROM svn_commit "
			+ "	WHERE repository_id = #{repositoryId}) ")
	int deleteBySvnRepositoryId(@Param("repositoryId") int repositoryId);
}
