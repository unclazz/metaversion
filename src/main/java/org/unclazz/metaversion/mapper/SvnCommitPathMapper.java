package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
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
	int selectNextVal();
	
	/**
	 * コミット情報として取り込まれた既知のパスのうち条件にマッチするものを返す.
	 * リポジトリ横断的に検索を行う。
	 * またプロジェクトとの紐付けのないコミットのパスは結果から除外される。
	 * @param partialPath 部分パス文字列
	 * @param limitOffset LIMIT/OFFSET句指定
	 * @return パスのリスト
	 */
	List<String> selectPathByPartialPath(@Param("partialPath") String partialPath, 
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	/**
	 * コミットIDをキーにコミットパス情報を検索して返す.
	 * @param commitId コミットID
	 * @param orderBy ORDER BY句指定
	 * @param limitOffset LIMIT/OFFSET句指定
	 * @return コミットパス情報のリスト
	 */
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
	int selectCountBySvnCommitId(@Param("commitId") int commitId);
	
	/**
	 * コミットIDをキーにプロジェクト変更パス情報を検索して返す.
	 * @param projectId プロジェクトID
	 * @param orderBy ORDER BY句指定
	 * @param limitOffset LIMIT/OFFSET句指定
	 * @return プロジェクト変更パス情報のリスト
	 */
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
	int selectCountByProjectId(@Param("projectId") int projectId);
	
	/**
	 * コミットIDをキーにコミットパス情報を検索しブランチ名のみ集約して返す.
	 * @param commitId コミットID
	 * @return ブランチ名のリスト
	 */
	List<String> selecBranchNameByCommitId(@Param("commitId") int commitId);
	
	List<String> selectPathNameByRepositoryIdAndPartialPath(
			@Param("repositoryId") int repositoryId, 
			@Param("partialPath") String partialPath, 
			@Param("unlinkedTo") int unlinkedTo,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	int selectCountPathNameByRepositoryIdAndPartialPath(
			@Param("repositoryId") int repositoryId, 
			@Param("partialPath") String partialPath, 
			@Param("unlinkedTo") int unlinkedTo);
	
	/**
	 * コミットパス情報を登録する.
	 * @param path コミットパス情報
	 * @param auth 認証情報
	 * @return 登録件数
	 */
	int insert(@Param("path") SvnCommitPathWithRawInfo path, @Param("auth") MVUserDetails auth);
	
	/**
	 * リポジトリに紐づくコミットパス情報を一括で削除する.
	 * @param repositoryId リポジトリID
	 * @return 削除件数
	 */
	int deleteBySvnRepositoryId(@Param("repositoryId") int repositoryId);
}
