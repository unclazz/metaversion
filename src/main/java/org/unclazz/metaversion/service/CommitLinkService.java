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
import org.unclazz.metaversion.entity.ProjectSvnRepository;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.mapper.ProjectMapper;
import org.unclazz.metaversion.mapper.ProjectSvnCommitMapper;
import org.unclazz.metaversion.mapper.ProjectSvnRepositoryMapper;
import org.unclazz.metaversion.mapper.SvnCommitMapper;
import org.unclazz.metaversion.service.BatchExecutorService.Executable;
import org.unclazz.metaversion.vo.MaxRevision;

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
	
	public void doCommitLink(final int projectId, final MVUserDetails auth) {
		executorService.execute(OnlineBatchProgram.COMMIT_LINK,
				new Executable() {
					@Override
					public void execute() {
						doCommitLinkMain(projectId, auth);
					}
		}, auth);
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
					selectForMatchingByProjectIdAndRepositoryId(projectId, obsoleted.getSvnRepositoryId());
			final MaxRevision maxRevision = MaxRevision.startsWith(obsoleted.getLastRevision());
			
			for (final SvnCommit commit : commitList) {
				final Matcher matcher = compiledPattern.matcher(commit.getCommitMessage());
				if (!matcher.find()) {
					continue;
				}
				projectSvnCommitMapper.insert(projectId, commit.getId(), auth);
				maxRevision.trySetNewValue(commit.getRevision());
			}
			
			obsoleted.setLastRevision(maxRevision.getValue());
			projectSvnRepositoryMapper.update(obsoleted, auth);
		}
	}
}
