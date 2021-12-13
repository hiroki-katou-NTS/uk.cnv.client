package nts.uk.ctx.exio.dom.input.canonicalize.domains.organization.workplace;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.val;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.errors.ItemError;
import nts.uk.ctx.exio.dom.input.errors.RecordError;
import nts.gul.util.Either;

/**
 * 同一履歴内でのコードの重複をチェックし、正常レコードとエラーレコードに仕分ける
 */
class CheckCodeDuplications {
	
	public static Either.Sequence<RecordError, RecordWithPeriod> check(
			List<RecordWithPeriod> records,
			int itemNo) {

		if (records.isEmpty()) {
			return Either.sequenceEmpty();
		}
		
		val results = records.stream()
			.collect(Collectors.groupingBy(r -> r.period))
			.values()
			.stream()
			.map(list -> checkInPeriod(list, itemNo))
			.collect(toList());
		
		return Either.Sequence.merge(results);
	}

	private static Either.Sequence<RecordError, RecordWithPeriod> checkInPeriod(
			List<RecordWithPeriod> records,
			int itemNo) {
		
		Set<String> duplicatedCodes = records.stream()
				.collect(Collectors.groupingBy(r -> getCode(r, itemNo)))
				.entrySet()
				.stream()
				.filter(e -> e.getValue().size() >= 2)
				.map(e -> e.getKey())
				.collect(Collectors.toSet());
		
		return Either.sequenceOf(records)
				.mapEither(r -> {
					if (duplicatedCodes.contains(getCode(r, itemNo))) {
						return Either.left(new RecordError(r.getRowNo(), itemNo, "コードが重複しています。"));
					} else {
						return Either.right(r);
					}
				});
	}
	
	private static String getCode(RecordWithPeriod record, int itemNo) {
		return record.interm.getItemByNo(itemNo).get().getString();
	}
}
