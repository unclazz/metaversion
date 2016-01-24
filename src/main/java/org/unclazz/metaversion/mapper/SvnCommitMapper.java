package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.entity.SvnCommitStats;
import org.unclazz.metaversion.entity.SvnCommitWithRepositoryInfo;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;

public interface SvnCommitMapper {
	/**
	 * シーケンスから新しいIDを採番する.
	 * @return ID
	 */
	int selectNextVal();
	/**
	 * IDによってコミット情報を取得する.
	 * @param id ID
	 * @return コミット情報
	 */
	SvnCommit selectOneById(@Param("id") int id);
	/**
	 * IDによってコミット情報とそれが紐づくリポジトリ情報を取得する.
	 * @param id ID
	 * @return コミット情報およびそれが紐づくリポジトリ情報
	 */
	SvnCommitWithRepositoryInfo selectWithRepositoryInfoById(@Param("id") int id);
	/**
	 * プロジェクトIDとリポジトリIDをキーにプロジェクト-コミット自動紐付けの候補となるコミット情報を取得する.
	 * @param projectId プロジェクトID
	 * @param repositoryId リポジトリID
	 * @return コミット情報リスト
	 */
	List<SvnCommit> selectAutolinkCandidateByProjectIdAndRepositoryId(
			@Param("projectId") int projectId, @Param("repositoryId") int repositoryId);
	/**
	 * リポジトリIDをキーにしてプロジェクト紐付けのされていないコミットの情報を取得する.
	 * @param repositoryId リポジトリID
	 * @param orderBy ORDER BY句の情報を格納するVO
	 * @param limitOffset LIMIT/OFFSET句の情報を格納するVO
	 * @return コミット情報リスト
	 */
	List<SvnCommit> selectUnlinkedByRepositoryId(
			@Param("repositoryId") int repositoryId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	/**
	 * リポジトリIDをキーにしてプロジェクト紐付けのされていないコミットの情報をカウントする.
	 * @param repositoryId リポジトリID
	 * @return コミット情報の件数
	 */
	int selectUnlinkedCountByRepositoryId(@Param("repositoryId") int repositoryId);
	/**
	 * リポジトリIDをキーにしてコミット情報を取得する.
	 * @param repositoryId リポジトリID
	 * @param orderBy ORDER BY句の情報を格納するVO
	 * @param limitOffset LIMIT/OFFSET句の情報を格納するVO
	 * @return コミット情報リスト
	 */
	List<SvnCommit> selectByRepositoryId(
			@Param("repositoryId") int repositoryId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	/**
	 * リポジトリIDをキーにしてコミット情報をカウントする.
	 * @param repositoryId リポジトリID
	 * @return コミット情報リスト
	 */
	int selectCountByRepositoryId(@Param("repositoryId") int repositoryId);
	/**
	 * コミットIDをキーにしてコミット統計情報を取得する.
	 * @param commitId コミットID
	 * @return コミット統計情報
	 */
	SvnCommitStats selectStatsOneByCommitId(
			@Param("commitId") int commitId);
	/**
	 * リポジトリIDをキーにしてコミット統計情報を取得する.
	 * @param repositoryId リポジトリID
	 * @param orderBy ORDER BY句の情報を格納するVO
	 * @param limitOffset LIMIT/OFFSET句の情報を格納するVO
	 * @return コミット統計情報リスト
	 */
	List<SvnCommitStats> selectStatsByRepositoryId(
			@Param("repositoryId") int repositoryId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	/**
	 * リポジトリIDをキーにしてコミット統計情報をカウントする.
	 * @param repositoryId リポジトリID
	 * @return コミット統計情報の件数
	 */
	int selectStatsCountByRepositoryId(@Param("repositoryId") int repositoryId);
	/**
	 * プロジェクトIDをキーにして紐付きのあるコミット情報とそのリポジトリ情報を取得する.
	 * @param projectId プロジェクトID
	 * @param orderBy ORDER BY句の情報を格納するVO
	 * @param limitOffset LIMIT/OFFSET句の情報を格納するVO
	 * @return コミット情報とそのリポジトリ情報
	 */
	List<SvnCommitWithRepositoryInfo> selectByProjectId(
			@Param("projectId") int projectId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	/**
	 * プロジェクトIDをキーにして紐付きのあるコミット情報をカウントする.
	 * @param projectId プロジェクトID
	 * @return コミット情報の件数
	 */
	int selectCountByProjectId(@Param("projectId") int projectId);
	/**
	 * コミット情報をINSERTする.
	 * IDはあらかじめシーケンスから採番した値で初期化されていることを前提とする。
	 * @param commit コミット情報
	 * @param auth 認証済みユーザ情報
	 * @return 処理件数
	 */
	int insert(@Param("commit") SvnCommit commit, @Param("auth") MVUserDetails auth);
	/**
	 * リポジトリIDをキーにしてコミット情報を一括DELETEする.
	 * @param repositoryId リポジトリID
	 * @return 処理件数
	 */
	int deleteBySvnRepositoryId(@Param("repositoryId") int repositoryId);
}
