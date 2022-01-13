package nts.uk.ctx.exio.dom.input.canonicalize.methods;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceInformation;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.ItemNoMap;
import nts.uk.ctx.exio.dom.input.canonicalize.result.CanonicalItem;
import nts.uk.ctx.exio.dom.input.canonicalize.result.IntermediateResult;
import nts.uk.ctx.exio.dom.input.errors.RecordError;
import nts.gul.util.Either;

/**
 * 職場コードを職場IDに正準化
 */
@Value
@AllArgsConstructor
public class WorkplaceCodeCanonicalization {
	/** 開始日の項目No */
	private final int itemNoStartDate;
	/** 職場コードの項目No */
	private final int itemNoWorkplaceCode;
	/** 職場IDの項目No */
	private final int itemNoWorkplaceId;
	
	public WorkplaceCodeCanonicalization(ItemNoMap map) {
		itemNoStartDate = map.getItemNo("開始日");
		itemNoWorkplaceCode = map.getItemNo("職場コード");
		itemNoWorkplaceId = map.getItemNo("WORKPLACE_ID");
	}

	/**
	 * 渡された編集済みデータを正準化する
	 * @param require
	 * @param revisedData
	 * @return
	 */
	public Either<RecordError, IntermediateResult> canonicalize(Require require, IntermediateResult revisedData, int csvRowNo) {
		String workplaceCode = revisedData.getItemByNo(itemNoWorkplaceCode).get().getString();
		GeneralDate startDate = revisedData.getItemByNo(itemNoStartDate).get().getDate();

		return getWorkplaceId(require, workplaceCode, startDate, csvRowNo)
				.map(workplaceId -> canonicalize(revisedData, workplaceId));
		
	}

	private static Either<RecordError, String> getWorkplaceId(Require require, String workplaceCode, GeneralDate startDate, int csvRowNo) {
		val workplace = require.getWorkplaceByCode(workplaceCode, startDate);
		
		return Either.rightOptional(
				workplace.map(e -> e.getWorkplaceId()),
				() -> new RecordError(csvRowNo, "未登録の職場コードです。"));
	}

	private IntermediateResult canonicalize(IntermediateResult canonicalizingData, String workplaceId) {
		return canonicalizingData.addCanonicalized(
				CanonicalItem.of(itemNoWorkplaceId, workplaceId));
	}

	public static interface Require {
		Optional<WorkplaceInformation> getWorkplaceByCode(String workplaceCode, GeneralDate startdate);
	}
}
