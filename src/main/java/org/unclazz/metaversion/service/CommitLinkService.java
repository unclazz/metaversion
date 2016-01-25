package org.unclazz.metaversion.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.entity.OnlineBatchProgram;
import org.unclazz.metaversion.entity.Project;
import org.unclazz.metaversion.entity.ProjectSvnCommit;
import org.unclazz.metaversion.entity.ProjectSvnRepository;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.mapper.ProjectMapper;
import org.unclazz.metaversion.mapper.ProjectSvnCommitMapper;
import org.unclazz.metaversion.mapper.ProjectSvnRepositoryMapper;
import org.unclazz.metaversion.mapper.SvnCommitMapper;
import org.unclazz.metaversion.service.BatchExecutorService.OnlineBatchRunnable;
import org.unclazz.metaversion.service.BatchExecutorService.OnlineBatchRunnableFactory;
import org.unclazz.metaversion.service.BatchExecutorService.OnlineBatchRunnableFactorySupport;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.MaxRevision;
import org.unclazz.metaversion.vo.OrderByClause;

@Service
public class CommitLinkService {
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
	
	public OnlineBatchRunnableFactory getRunnableFactory(final MVUserDetails auth) {
		final OnlineBatchRunnableFactory factory = new OnlineBatchRunnableFactorySupport() {
			@Override
			public OnlineBatchRunnable create() {
				MVUtils.argsMustBeNotNull("Program and UserDetails", getProgram(), getUserDetails());
				final OrderByClause orderBy = OrderByClause.noParticularOrder();
				final LimitOffsetClause limitOffset = LimitOffsetClause.ALL;
				final Runnable runnable;
				if (getArguments().size() > 0) {
					final Object o = getArguments().get(0);
					final int projectId;
					if (o instanceof String) {
						projectId = Integer.parseInt(o.toString());
					} else if (o instanceof Integer) {
						projectId = (Integer) o;
					} else {
						throw MVUtils.illegalArgument("Unknown argument was found (%s).", o);
					}
					runnable = new Runnable() {
						@Override
						public void run() {
							doCommitLinkMain(projectId, auth);
						}
					};
				} else {
					final List<Project> list = projectMapper.selectAll(orderBy, limitOffset);
					runnable = new Runnable() {
						@Override
						public void run() {
							for (final Project p : list) {
								doCommitLinkMain(p.getId(), auth);
							}
						}
					};
				}
				return executorService.wrapRunnableWithLock(OnlineBatchProgram.LOG_IMPORTER,
						runnable, auth);
			}
		};
		factory.setProgram(OnlineBatchProgram.LOG_IMPORTER);
		return factory;
	}
	
	@Transactional
	public void doCommitLinkMain(final int projectId, final MVUserDetails auth) {
		final Project proj = projectMapper.selectOneById(projectId);
		if (proj == null) {
			throw MVUtils.illegalArgument("Unknown project(id=%s).", projectId);
		}
		
		final Pattern compiledPattern = Pattern.compile(proj.getCommitSignPattern());
		projectSvnRepositoryMapper.insertMissingLink(auth);
		
		final List<ProjectSvnRepository> obsoletedList = projectSvnRepositoryMapper.
				selectObsoletedRecordByProjectId(projectId);
		
		for (final ProjectSvnRepository obsoleted : obsoletedList) {
			final List<SvnCommit> commitList = svnCommitMapper.
					selectAutolinkCandidateByProjectIdAndRepositoryId(projectId, obsoleted.getRepositoryId());
			final MaxRevision maxRevision = MaxRevision.startsWith(obsoleted.getLastRevision());
			
			for (final SvnCommit commit : commitList) {
				maxRevision.trySetNewValue(commit.getRevision());
				final Matcher matcher = compiledPattern.matcher(commit.getCommitMessage());
				if (!matcher.find()) {
					continue;
				}
				final ProjectSvnCommit vo = new ProjectSvnCommit();
				vo.setCommitId(commit.getId());
				vo.setProjectId(projectId);
				projectSvnCommitMapper.insert(vo, auth);
			}
			
			obsoleted.setLastRevision(maxRevision.getValue());
			projectSvnRepositoryMapper.update(obsoleted, auth);
		}
	}
	
	public void registerCommitLink(final ProjectSvnCommit projectSvnCommit, final MVUserDetails auth) {
		if (projectSvnCommitMapper.selectCountByProjectIdAndCommitId
			(projectSvnCommit.getProjectId(), projectSvnCommit.getCommitId()) > 0) {
			return;
		}
		if (projectSvnCommitMapper.insert(projectSvnCommit, auth) != 1) {
			
			throw MVUtils.unexpectedResult("Unexpected error has occurred while "
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
