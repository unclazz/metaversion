package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.Project;
import org.unclazz.metaversion.entity.ProjectStats;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;

/**
 * {@link Project}と{@link ProjectStats}のためのORマッパー.
 * 動的SQLを利用するためSQLステートメントはアノテーションではなくXMLにて記述している。
 */
public interface ProjectMapper {
	/**
	 * シーケンスから新しいIDを採番する.
	 * @return ID
	 */
	int selectNextVal();
	
	/**
	 * 中間一致検索によりプロジェクト名を取得する.
	 * @param partialName 部分文字列
	 * @param limitOffset LIMIT/OFFSET句の情報を格納するVO
	 * @return プロジェクト名のリスト
	 */
	List<String> selectNameByPartialName(@Param("partialName") String partialName, 
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	/**
	 * IDによりプロジェクト情報を取得する.
	 * @param id ID
	 * @return プロジェクト情報
	 */
	Project selectOneById(@Param("id") int id);
	
	/**
	 * IDによりプロジェクト統計情報を取得する.
	 * @param id ID
	 * @return プロジェクト統計情報
	 */
	ProjectStats selectStatsOneById(@Param("id") int id);
	
	/**
	 * すべてのプロジェクト情報を取得する.
	 * @param orderBy ORDER BY句の情報を格納するVO
	 * @param limitOffset LIMIT/OFFSET句の情報を格納するVO
	 * @return プロジェクト情報のリスト
	 */
	List<Project> selectAll(@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	/**
	 * すべてのプロジェクト情報の件数を取得する.
	 * @return プロジェクト情報の件数
	 */
	int selectCount();
	
	/**
	 * プロジェクト名の中間一致検索とコミット紐付け済み除外によりプロジェクト情報を検索する.
	 * @param like 部分文字列
	 * @param unlinkedCommitId 紐付け済み除外の対象となるコミットID
	 * @param orderBy ORDER BY句の情報を格納するVO
	 * @param limitOffset LIMIT/OFFSET句の情報を格納するVO
	 * @return プロジェクト情報のリスト
	 */
	List<Project> selectByPartialName(
			@Param("like") String like,
			@Param("unlinkedCommitId") int unlinkedCommitId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	/**
	 * プロジェクト名の中間一致検索とコミット紐付け済み除外によりプロジェクト情報をカウントする.
	 * @param like 部分文字列
	 * @param unlinkedCommitId 紐付け済み除外の対象となるコミットID
	 * @return プロジェクト情報の件数
	 */
	int selectCountByPartialName(
			@Param("like") String partialName,
			@Param("unlinkedCommitId") int unlinkedCommitId);
	
	/**
	 * 変更パス名の中間一致検索とコミット紐付け済み除外によりプロジェクト情報を検索する.
	 * @param like 部分文字列
	 * @param unlinkedCommitId 紐付け済み除外の対象となるコミットID
	 * @param orderBy ORDER BY句の情報を格納するVO
	 * @param limitOffset LIMIT/OFFSET句の情報を格納するVO
	 * @return プロジェクト情報のリスト
	 */
	List<Project> selectByPartialPath(@Param("like") String like,
			@Param("unlinkedCommitId") int unlinkedCommitId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	/**
	 * 変更パス名の中間一致検索とコミット紐付け済み除外によりプロジェクト情報をカウントする.
	 * @param like 部分文字列
	 * @param unlinkedCommitId 紐付け済み除外の対象となるコミットID
	 * @return プロジェクト情報の件数
	 */
	int selectCountByPartialPath(@Param("like") String like,
			@Param("unlinkedCommitId") int unlinkedCommitId);

	/**
	 * コミットIDに紐付け済みのプロジェクト情報を検索する.
	 * @param commitId コミットID
	 * @param orderBy ORDER BY句の情報を格納するVO
	 * @param limitOffset LIMIT/OFFSET句の情報を格納するVO
	 * @return プロジェクト情報のリスト
	 */
	List<Project> selectByCommitId(@Param("commitId") int commitId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	/**
	 * コミットIDに紐付け済みのプロジェクト情報をカウントする.
	 * @param commitId コミットID
	 * @return プロジェクト情報の件数
	 */
	int selectCountByCommitId(@Param("commitId") int commitId);
	
	/**
	 * プロジェクト情報をINSERTする.
	 * IDにはシーケンスで採番された値があらかじめ設定されている前提で処理が行われる。
	 * @param project プロジェクト情報
	 * @param auth 認証済みユーザ情報
	 * @return 処理件数
	 */
	int insert(@Param("proj") Project project, @Param("auth") MVUserDetails auth);

	/**
	 * プロジェクト情報をUPDATEする.
	 * @param project プロジェクト情報
	 * @param auth 認証済みユーザ情報
	 * @return 処理件数
	 */
	int update(@Param("proj") Project project, @Param("auth") MVUserDetails auth);

	/**
	 * プロジェクト情報をDELETEする.
	 * @param id ID
	 * @return 処理件数
	 */
	int delete(@Param("id") int id);
}
