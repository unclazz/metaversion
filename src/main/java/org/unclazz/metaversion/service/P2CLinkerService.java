package org.unclazz.metaversion.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.entity.OnlineBatchLock;
import org.unclazz.metaversion.entity.OnlineBatchProgram;
import org.unclazz.metaversion.entity.Project;
import org.unclazz.metaversion.entity.ProjectSvnCommit;
import org.unclazz.metaversion.entity.ProjectSvnRepository;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.mapper.ProjectMapper;
import org.unclazz.metaversion.mapper.ProjectSvnCommitMapper;
import org.unclazz.metaversion.mapper.ProjectSvnRepositoryMapper;
import org.unclazz.metaversion.mapper.SvnCommitMapper;
import org.unclazz.metaversion.service.BatchExecutorService.ProccessIsAlreadyRunning;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.MaxRevision;
import org.unclazz.metaversion.vo.OrderByClause;

@Service
public class P2CLinkerService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private SvnCommitMapper svnCommitMapper;
	@Autowired
	private ProjectSvnRepositoryMapper projectSvnRepositoryMapper;
	@Autowired
	private ProjectSvnCommitMapper projectSvnCommitMapper;
	@Autowired
	private BatchExecutorService executorService;
	@Autowired
	private SystemBootLogService bootLogService;
	
	public void doP2CLinkAsynchronously(final MVUserDetails auth) {
		final OnlineBatchLock lock = executorService.getLastExecutionLock(OnlineBatchProgram.P2C_LINKER);
		final Date nowBootDate = bootLogService.getSystemBootDate();
		
		logger.info("プロジェクト・コミット紐付け（非同期）を開始");
		logger.info("ロック状態： {}", lock.isLocked());
		logger.info("ロック時システムブート日時： {}", lock.getSystemBootDate());
		logger.info("現在時点システムブート日時： {}", nowBootDate);
		
		if (lock.isLocked() && lock.getSystemBootDate().compareTo(nowBootDate) >= 0) {
			logger.info("プロジェクト・コミット紐付け（非同期）を中止");
			return;
		}
		final Date lastExecDate = lock.getLastUnlockDate();
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -60);
		final Date xMinutesBefore = cal.getTime();

		logger.info("最終バッチ実行日時： {}", lastExecDate);
		logger.info("バッチ起動閾値日時： {}", xMinutesBefore);

		if (lastExecDate.compareTo(xMinutesBefore) > 0) {
			logger.info("プロジェクト・コミット紐付け（非同期）を中止");
			return;
		}
		
		final OrderByClause orderBy = OrderByClause.noParticularOrder();
		final LimitOffsetClause limitOffset = LimitOffsetClause.ALL;
		final List<Project> list = projectMapper.selectAll(orderBy, limitOffset);
		new Thread(executorService.wrapRunnableWithLock(
			OnlineBatchProgram.P2C_LINKER,
			new Runnable() {
				@Override
				public void run() {
					logger.info("対象プロジェクト数： {}", list.size());
					for (final Project r : list) {
						logger.info("対象プロジェクト： {}({})", r.getName(), r.getId());
						try {
							doP2CLinkMain(r, auth);
						} catch (final Exception e) {
							logger.info("処理中の例外スロー： {}", r.getName(), r.getId(), e);
						}
					}
					logger.info("プロジェクト・コミット紐付け（非同期）を終了");
				}
			}, 
			auth)).start();
	}
	
	@Transactional
	public void doP2CLinkMain(final Project project, final MVUserDetails auth) {
		final int projectId = project.getId();
		final Pattern compiledPattern = Pattern.compile(project.getCommitSignPattern());
		projectSvnRepositoryMapper.insertMissingLink(auth);
		
		final List<ProjectSvnRepository> obsoletedList = projectSvnRepositoryMapper.
				selectObsoletedRecordByProjectId(projectId);
		
		logger.info("紐付け対象リポジトリ数： {}", obsoletedList.size());
		
		for (final ProjectSvnRepository obsoleted : obsoletedList) {
			final List<SvnCommit> commitList = svnCommitMapper.
					selectAutolinkCandidateByProjectIdAndRepositoryId(projectId, obsoleted.getRepositoryId());
			final MaxRevision maxRevision = MaxRevision.startsWith(obsoleted.getLastRevision());
			
			logger.info("紐付け対象リポジトリID： {}", obsoleted.getRepositoryId());
			logger.info("前回処理済みリビジョン： {}", obsoleted.getLastRevision());
			
			for (final SvnCommit commit : commitList) {
				maxRevision.trySetNewValue(commit.getRevision());
				final Matcher matcher = compiledPattern.matcher(commit.getCommitMessage());
				if (!matcher.find()) {
					continue;
				}
				final ProjectSvnCommit vo = new ProjectSvnCommit();
				vo.setCommitId(commit.getId());
				vo.setProjectId(projectId);
				vo.setAutoLinked(true);
				projectSvnCommitMapper.insert(vo, auth);
			}
			
			logger.info("今回処理済みリビジョン： {}", maxRevision.getValue());
			
			obsoleted.setLastRevision(maxRevision.getValue());
			projectSvnRepositoryMapper.update(obsoleted, auth);
		}
	}
	
	@Transactional
	public void doP2CLinkSynchronously(final Project project, final MVUserDetails auth) {
		logger.info("プロジェクト・コミット紐付け（自動のみ）を解除");
		projectSvnCommitMapper.deleteByProjectIdAndAutoLinked(project.getId(), true);
		projectSvnRepositoryMapper.deleteByProjectId(project.getId());
		
		try {
			executorService.wrapRunnableWithLock(
				OnlineBatchProgram.P2C_LINKER,
				new Runnable() {
					@Override
					public void run() {
						logger.info("プロジェクト・コミット紐付け（同期）を開始");
						logger.info("対象プロジェクト： {}({})", project.getName(), project.getId());
						try {
							doP2CLinkMain(project, auth);
						} catch (final Exception e) {
							logger.info("処理中の例外スロー： {}", project.getName(), project.getId(), e);
						}
						logger.info("プロジェクト・コミット紐付け（同期）を終了");
					}
				}, 
				auth).run();
		
		} catch (final ProccessIsAlreadyRunning e) {
			logger.info("別プロセスで紐付けを実行中");
			logger.info("プロジェクト・コミット紐付け（同期）を中止");
			throw e;
		}
	}
	
	public void registerCommitLink(final ProjectSvnCommit projectSvnCommit, final MVUserDetails auth) {
		try {
			projectSvnCommitMapper.insert(projectSvnCommit, auth);
		} catch (final DuplicateKeyException e) {
			logger.info("プロジェクト（ID={}）とコミット（ID={}）は紐付け済み", 
					projectSvnCommit.getProjectId(), projectSvnCommit.getCommitId());
			return;
		}
		catch (final RuntimeException e) {
			throw MVUtils.unexpectedResult(e, "Unexpected error has occurred while "
					+ "linking between a project(id=%s) and a commit(id=%s). ",
					projectSvnCommit.getProjectId(), projectSvnCommit.getCommitId());
		}
	}
	
	public void removeCommitLink(final ProjectSvnCommit projectSvnCommit, final MVUserDetails auth) {
		if (projectSvnCommitMapper.delete(projectSvnCommit) != 1) {
			throw MVUtils.unexpectedResult("Unexpected error has occurred while "
					+ "unlinking between a project(id=%s) and a commit(id=%s). ",
					projectSvnCommit.getProjectId(), projectSvnCommit.getCommitId());
		}
	}
}
