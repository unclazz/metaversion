package org.unclazz.metaversion.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.MVProperties;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.ProjectVirtualChangedPath;
import org.unclazz.metaversion.entity.VirtualChangedPath;
import org.unclazz.metaversion.mapper.VirtualChangedPathMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@Service
public class VirtualChangedPathService {
	@Autowired
	private MVProperties props;
	@Autowired
	private VirtualChangedPathMapper mapper;
	
	public ProjectVirtualChangedPath getPathById(final int projectId, final int virtualChangedPathId) {
		return mapper.selectOneById(virtualChangedPathId);
	}
	
	public Paginated<ProjectVirtualChangedPath> getPathListByProjectId(final int projectId, final Paging page) {
		final OrderByClause orderBy = OrderByClause.of("repositoryName").and("path");
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(page);
		return Paginated.<ProjectVirtualChangedPath>of(page, mapper
				.selectByProjectId(projectId, orderBy, limitOffset),
				mapper.selectCountByProjectId(projectId));
	}
	
	public boolean registerPath(final VirtualChangedPath path, final MVUserDetails auth) {
		path.setId(mapper.selectNextVal());
		return mapper.insert(path, auth) == 1;
	}
	
	public boolean removePath(final int id, final MVUserDetails auth) {
		return mapper.deleteById(id) == 1;
	}
	
	public byte[] getPathCsvByProjectId(final int id) throws IOException {
		final OrderByClause orderBy = OrderByClause.of("repositoryName").and("path");
		final LimitOffsetClause limitOffset = LimitOffsetClause.ALL;
		
		// CSVファイルのコンテンツを一時的に格納するためバイト配列出力ストリームを初期化
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// そのストリームをラップするかたちでライターを初期化
		final OutputStreamWriter osw = new OutputStreamWriter(os, props.getCsvCharset());
		// CSVフォーマットを定義
		final CSVFormat format = CSVFormat.EXCEL.withHeader(
				"REPOSITORY_ID",
				"REPOSITORY_NAME",
				"PATH");
		
		// CSVプリンタを初期化
		final CSVPrinter printer = format.print(osw);
		
		for (final ProjectVirtualChangedPath rec : mapper.selectByProjectId(id, orderBy, limitOffset)) {
			// CSVレコードを書き出す
			printer.printRecord(
					rec.getRepositoryId(),
					rec.getRepositoryName(),
					rec.getPath());
		}
		
		// プリンタをクローズ
		printer.close();
		
		// ストリームに書き込まれた情報を再度バイト配列に変換
		return os.toByteArray();
	}
}
