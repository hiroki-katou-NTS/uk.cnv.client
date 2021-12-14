package nts.uk.ctx.exio.dom.input.canonicalize.domains.organization.workplace;

import static java.util.stream.Collectors.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.organization.workplace.WorkplaceCanonicalization.Items;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.IntermediateResult;
import nts.uk.ctx.exio.dom.input.util.Either;

/**
 * 期間に関する処理
 */
public class PeriodUtil {
	
	static Either.Sequence<RecordWithPeriod, RecordWithPeriod> checkDuplicates(List<RecordWithPeriod> records) {
		
		val periods = PeriodUtil.getSortedPeriods(records);
		
		// 期間の重複をチェック
		Set<DatePeriod> duplicatedPeriods = PeriodUtil.collectDuplicated(periods);
		
		return Either.sequenceOf(records)
				.separate(r -> duplicatedPeriods.contains(r.period));
	}

	/**
	 * recordsからDatePeriodを取り出し、開始日の昇順にソートして返す
	 * @param records
	 * @return
	 */
	static List<DatePeriod> getSortedPeriods(List<RecordWithPeriod> records) {
		
		return records.stream()
				.map(r -> PeriodUtil.getPeriod(r.interm))
				.distinct()
				.sorted(Comparator.comparing(p -> p.start()))
				.collect(toList());
	}

	/**
	 * DatePeriodを取り出す
	 * @param record
	 * @return
	 */
	static DatePeriod getPeriod(IntermediateResult record) {
		
		val start = record.getItemByNo(Items.開始日).get().getDate();
		val end = record.getItemByNo(Items.終了日).get().getDate();
		
		return new DatePeriod(start, end);
	}

	/**
	 * 相互に重複している期間を集める
	 * @param periods
	 * @return
	 */
	static Set<DatePeriod> collectDuplicated(List<DatePeriod> periods) {
		
		Set<DatePeriod> duplicatedPeriods = new HashSet<>();
		
		val sorted = periods.stream()
				.sorted(Comparator.comparing(p -> p.start()))
				.collect(toList());
		
		for (int i = 0; i < sorted.size() - 1; i++) {
			val current = sorted.get(i);
			val next = sorted.get(i + 1);
			if (current.compare(next).isDuplicated()) {
				duplicatedPeriods.add(current);
				duplicatedPeriods.add(next);
			}
		}
		
		return duplicatedPeriods;
	}
	
}
