package org.unclazz.metaversion;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.unclazz.metaversion.vo.SvnRepositoryInfo;

public class SvnRepositoryInfoTest {

	@Test
	public void of_SVNInfo() throws SVNException {
		final SVNInfo orig = new SVNInfo("/trunk/foo/bar",
				SVNURL.parseURIEncoded("http://example.com/svn/trunk/foo/bar"),
				SVNRevision.create(100), SVNNodeKind.DIR, "uuid",
				SVNURL.parseURIEncoded("http://example.com/svn"),
				1, null, null, null, null, 1);
		
		final SvnRepositoryInfo info = SvnRepositoryInfo.of(orig);
		assertThat(info.getHeadRevision(), is(100));
		assertThat(info.getRootUrl(), is("http://example.com/svn"));
		assertThat(info.getUuid(), is("uuid"));
	}
}
