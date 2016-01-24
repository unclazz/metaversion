package org.unclazz.metaversion.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.entity.ProjectParallels;
import org.unclazz.metaversion.mapper.ProjectParallelsMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@Service
public class ProjectParallelsService {
	private static final Charset csvCharset = Charset.forName("Windows-31j");
	
	@Autowired
	private ProjectParallelsMapper projectParallelsMapper;
	
	public Paginated<ProjectParallels> getProjectParallelsByProjectId(final int projectId, final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("repositoryName").and("path");
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);

		// コミット情報を検索する
		return Paginated.of(paging, 
				projectParallelsMapper.selectByProjectId(projectId, orderBy, limitOffset),
				projectParallelsMapper.selectCountByProjectId(projectId));
		
	}
	
	public Resource getProjectParallelsCsvByProjectId(final int id) throws IOException {
		final OrderByClause orderBy = OrderByClause.of("repositoryName").and("path");
		final LimitOffsetClause limitOffset = LimitOffsetClause.ALL;
		
		// CSVファイルのコンテンツを一時的に格納するためバイト配列出力ストリームを初期化
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// そのストリームをラップするかたちでライターを初期化
		final OutputStreamWriter osw = new OutputStreamWriter(os, csvCharset);
		// CSVフォーマットを定義
		final CSVFormat format = CSVFormat.EXCEL.withHeader(
				"SELF_PROJECT_ID",
				"REPOSITORY_ID",
				"REPOSITORY_NAME",
				"PATH",
				"PARALLEL_TYPE",
				"SELF_MIN_REVISION",
				"SELF_MIN_COMMIT_DATE",
				"SELF_MAX_REVISION",
				"SELF_MAX_COMMIT_DATE",
				"OTHER_PROJECT_ID",
				"OTHER_PROJECT_NAME",
				"OTHER_PROJECT_CODE",
				"OTHER_PROJECT_RESPONSIBLE_PERSON",
				"OTHER_MIN_REVISION",
				"OTHER_MIN_COMMIT_DATE",
				"OTHER_MAX_REVISION",
				"OTHER_MAX_COMMIT_DATE");
		// CSVプリンタを初期化
		final CSVPrinter printer = format.print(osw);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		for (final ProjectParallels rec : projectParallelsMapper
				.selectByProjectId(id, orderBy, limitOffset)) {
			// CSVレコードを書き出す
			printer.printRecord(
					rec.getSelfProjectId(),
					rec.getRepositoryId(),
					rec.getRepositoryName(),
					rec.getPath(),
					rec.getParallelType(),
					rec.getSelfMinRevision(),
					dateFormat.format(rec.getSelfMinCommitDate()),
					rec.getSelfMaxRevision(),
					dateFormat.format(rec.getSelfMaxCommitDate()),
					rec.getOtherProjectId(),
					rec.getOtherProjectName(),
					rec.getOtherProjectCode(),
					rec.getOtherProjectResponsiblePerson(),
					rec.getOtherMinRevision(),
					dateFormat.format(rec.getOtherMinCommitDate()),
					rec.getOtherMaxRevision(),
					dateFormat.format(rec.getOtherMaxCommitDate()));
		}
		
		// プリンタをクローズ
		printer.close();
		
		// ストリームに書き込まれた情報を再度バイト配列に変換
		// Resourceインスタンスを初期化して返す
		return new ByteArrayResource(os.toByteArray());
	}
}
