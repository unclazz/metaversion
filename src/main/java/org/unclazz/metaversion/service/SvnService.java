package org.unclazz.metaversion.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.entity.SvnCommitPath;
import org.unclazz.metaversion.entity.SvnRepository;

@Service
public class SvnService {
	public static final class SvnCommitAndItsPathList extends SvnCommit {
		private final List<SvnCommitPath> pathList = new LinkedList<SvnCommitPath>();
		public List<SvnCommitPath> getPathList() {
			return pathList;
		}
	}
	
	public int getHeadRevision(SvnRepository repository) {
		// TODO SVNWCClient#doInfo(...)メソッドを使う
		return 1;
	}
	
	public List<SvnCommitAndItsPathList> getCommitAndItsPathList(SvnRepository repository, int revision) {
		final List<SvnCommitAndItsPathList> list = new LinkedList<SvnCommitAndItsPathList>();
		// TODO SVNLogClient#doLog(...)メソッドを使う 
		// ISVNLogEntryHandlerを実装し、SVNLogEntry経由でSVNLogEntryPathにアクセス
		return list;
	}
}
