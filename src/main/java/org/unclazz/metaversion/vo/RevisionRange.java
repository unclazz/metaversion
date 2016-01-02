package org.unclazz.metaversion.vo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.unclazz.metaversion.MVUtils;

public final class RevisionRange {
	public static RevisionRange ofBetween(final int start, final int end) {
		return new RevisionRange(start, end);
	}
	
	private final int start;
	private final int end;
	private RevisionRange(final int start, final int end) {
		if (start < 1 || end < 1) {
			throw MVUtils.illegalArgument("Both value must be greater than 0. "
					+ "Specified start value is %s, and end value is %s.", start, end);
		}
		if (end - start < 0) {
			throw MVUtils.illegalArgument("End value must be greater than or equal start value. "
					+ "Specified start value is %s, and end value is %s.", start, end);
		}
		this.start = start;
		this.end = end;
	}
	public int getStart() {
		return start;
	}
	public int getEnd() {
		return end;
	}
	public int getEndExclusive() {
		return end + 1;
	}
	public List<RevisionRange> withStep(final int step) {
		// リビジョン番号としてまた増分としていずれも1より小さい数値はNG
		if (step < 1) {
			throw MVUtils.illegalArgument("Step value must be greater than 0.");
		}
		// 開始リビジョンのほうが大きい場合
		if (start > end) {
			// 空のリストを返すだけで処理を終える
			return Collections.emptyList();
		}
		// 続くfor文のために排他の上限値を定義
		final int endExclusive = end + 1;
		// 結果値となるリストを初期化
		final List<RevisionRange> result = new LinkedList<RevisionRange>();
		// 指定された増分を用いて繰り返しRevisionRangeを作成
		for (int i = start; i < endExclusive; i += step) {
			// 指定された増分を用いて終了リビジョンを単純計算
			final int iPlusSizeMinus1 = i + step - 1;
			// リストにRevisionRangeを追加
			// 単純計算した個別の終了リビジョンが全体の終了リビジョンを超える場合は後者を採用
			result.add(new RevisionRange(i, iPlusSizeMinus1 < end ? iPlusSizeMinus1 : end));
		}
		// 結果を呼び出し元に返す
		return result;
	}
}