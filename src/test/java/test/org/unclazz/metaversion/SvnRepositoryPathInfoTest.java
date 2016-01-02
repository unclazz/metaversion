package test.org.unclazz.metaversion;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.unclazz.metaversion.entity.SvnRepository;
import org.unclazz.metaversion.vo.SvnRepositoryInfo;
import org.unclazz.metaversion.vo.SvnRepositoryPathInfo;

public class SvnRepositoryPathInfoTest {

	@Test
	public void composedOf_SVNInfo_SvnRepository() throws SVNException {
		final SVNInfo orig = new SVNInfo("/module_a/trunk/foo/bar",
				SVNURL.parseURIEncoded("http://example.com/svn/module_a/trunk/foo/bar"),
				SVNRevision.create(100), SVNNodeKind.DIR, "uuid",
				SVNURL.parseURIEncoded("http://example.com/svn"),
				1, null, null, null, null, 1);
		
		final SvnRepositoryInfo info = SvnRepositoryInfo.of(orig);
		final SvnRepository entity = new SvnRepository();
		entity.setBaseUrl("http://example.com/svn/module_a");
		entity.setBranchPathPattern("/branches/\\w+");
		entity.setId(123);
		entity.setMaxRevision(123);
		entity.setName("foo");
		entity.setPassword("****");
		entity.setTrunkPathPattern("/trunk");
		entity.setUsername("foo");
		
		final SvnRepositoryPathInfo pathInfo = SvnRepositoryPathInfo.composedOf(info, entity);
		assertThat(pathInfo.getRootUrl(), is("http://example.com/svn"));
		assertThat(pathInfo.getBaseUrl(), is("http://example.com/svn/module_a"));
		assertThat(pathInfo.getBaseUrlPathComponent(), is("/module_a"));
		assertThat(pathInfo.getCompiledTrunkPathPattern().pattern(), is("/trunk"));
		assertThat(pathInfo.getCompliedBranchPathPattern().pattern(), is("/branches/\\w+"));
	}
}
