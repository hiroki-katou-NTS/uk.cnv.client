package nts.uk.ctx.exio.dom.input.canonicalize.domains.organization.workplace;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.util.Either;

/**
 * 履歴同士の連続性をチェックし、エラー行を除外し、最後の履歴の終了日を99991231に補正する
 * すでに重複履歴は除外されている前提とする
 */
public class CanonicalizeHistoryContinuity {

	public static Either.Sequence<ExternalImportError, RecordWithPeriod> canonicalize(
			List<RecordWithPeriod> records) {
		
		if (records.isEmpty()) {
			return Either.sequenceEmpty();
		}
		
		val periods = PeriodUtil.getSortedPeriods(records);
		
		Set<DatePeriod> validPeriods = new HashSet<>();
		validPeriods.add(periods.get(0));
		
		// 前の履歴との連続性をチェック
		DatePeriod prev = periods.get(0);
		for (int i = 1; i < periods.size(); i++) {
			
			val current = periods.get(i);
			
			// (有効な)前の履歴と繋がっている
			if (prev.end().addDays(1).equals(current.start())) {
				validPeriods.add(current);
				
				// 履歴がちゃんと繋がったときだけprevを更新する
				// 一度途切れたら以降は全滅
				prev = current;
			}
		}
		
		// 最後の履歴の期間
		val lastPeriod = validPeriods.stream()
			.max(Comparator.comparing(p -> p.start()))
			.orElse(null);
		
		return Either.sequenceOf(records)
				.separate(r -> validPeriods.contains(r.period))
				.mapLeft(r -> ExternalImportError.record(r.getRowNo(), "履歴の期間が直前の履歴と連続していません。"))
				.map(r -> {
					// 最後の履歴は99991231に自動補正
					if (r.period.equals(lastPeriod)) {
						return r.changePeriod(r.period.newSpanWithMaxEnd());
					} else {
						return r;
					}
				});
	}
}
