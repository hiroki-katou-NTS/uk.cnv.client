package nts.uk.ctx.exio.dom.input.canonicalize.domains.organization.workplace;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceHierarchyUnitCode;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.organization.workplace.WorkplaceCanonicalization.Items;
import nts.uk.ctx.exio.dom.input.canonicalize.result.CanonicalItem;
import nts.uk.ctx.exio.dom.input.canonicalize.result.IntermediateResult;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.errors.RecordError;
import nts.gul.util.Either;

/**
 * 階層コードを正準化する
 */
class CanonicalizeHierarchyCode {
	
	public static Either<RecordError, RecordWithPeriod> canonicalize(RecordWithPeriod interm) {
		
		List<String> parts = Arrays.asList(
				getCode(interm, Items.職場階層コード1),
				getCode(interm, Items.職場階層コード2),
				getCode(interm, Items.職場階層コード3),
				getCode(interm, Items.職場階層コード4),
				getCode(interm, Items.職場階層コード5),
				getCode(interm, Items.職場階層コード6),
				getCode(interm, Items.職場階層コード7),
				getCode(interm, Items.職場階層コード8),
				getCode(interm, Items.職場階層コード9),
				getCode(interm, Items.職場階層コード10));

		String fullCode = getFullCode(interm);
		boolean existsFull = fullCode.length() > 0;
		boolean existsParts = parts.stream().anyMatch(s -> s.length() > 0);

		if (existsParts && existsFull) {
			return Either.left(error(interm, "「階層コード」と「階層コード1～10」は同時に受け入れできません。"));
		}

		if (!existsParts && !existsFull) {
			return Either.left(error(interm, "「階層コード」と「階層コード1～10」のいずれかを受け入れてください。"));
		}
		
		if (existsParts) {
			if (!validateParts(parts)) {
				return Either.left(error(interm, "「階層コード1～10」は、必ず1から順番に受け入れてください。"));
			}
			
			fullCode = parts.stream().collect(Collectors.joining());
		}
		
		IntermediateResult canonical = interm.interm.addCanonicalized(CanonicalItem.of(Items.職場階層コード, fullCode));
		
		return Either.right(new RecordWithPeriod(interm.period, canonical));
	}
	
	private static RecordError error(RecordWithPeriod record, String message) {
		return RecordError.record(record.getRowNo(), message);
	}
	
	private static boolean validateParts(List<String> parts) {
		
		if (parts.get(0).equals("")) {
			return false;
		}
		
		int i = 1;
		for (; i < 10; i++) {
			if (parts.get(i).equals("")) break;
		}
		
		for (; i < 10; i++) {
			if (!parts.get(i).equals("")) return false;
		}
		
		return true;
	}

	private static String getCode(RecordWithPeriod record, int itemNo) {
		// 自動ゼロ埋め
		return getCode(record, itemNo, s -> new WorkplaceHierarchyUnitCode(s).v());
	}

	private static String getFullCode(RecordWithPeriod record) {
		return getCode(record, Items.職場階層コード, s -> s);
	}
	
	private static String getCode(RecordWithPeriod record, int itemNo, UnaryOperator<String> revise) {
		return record.interm.getItemByNo(itemNo)
				.map(d -> d.getString())
				// 多分文字列でnullは無いと思うけど、念の為チェックしてすべて "" に揃える
				.filter(s -> s != null && s.length() > 0)
				.map(revise)
				.orElse("");
	}
}
