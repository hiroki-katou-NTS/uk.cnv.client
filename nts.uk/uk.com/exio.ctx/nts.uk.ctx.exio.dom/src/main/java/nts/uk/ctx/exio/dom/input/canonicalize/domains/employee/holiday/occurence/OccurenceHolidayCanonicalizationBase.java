package nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.occurence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Value;
import lombok.val;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.occurence.compensatory.CompensatoryHolidayCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.occurence.compensatory.HolidayWorkCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.occurence.substitute.SubstituteHolidayCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.employee.holiday.occurence.substitute.SubstituteWorkCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToChange;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToDelete;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.StringifiedValue;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.EmployeeCodeCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.result.CanonicalItem;
import nts.uk.ctx.exio.dom.input.canonicalize.result.IntermediateResult;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;

/**
 * 随時発生する休暇の残数データ（振休振出／代休休出）の正準化の基底クラス
 */
public abstract class OccurenceHolidayCanonicalizationBase implements DomainCanonicalization {

	private final EmployeeCodeCanonicalization employeeCodeCanonicalization;
	
	public OccurenceHolidayCanonicalizationBase() {
		employeeCodeCanonicalization = new EmployeeCodeCanonicalization(getItemNoMap());
	}
	
	@Override
	public void canonicalize(DomainCanonicalization.RequireCanonicalize require, ExecutionContext context) {
		
		List<String> employeeCodes = require.getStringsOfRevisedData(context, itemNoEmployeeCode());
		
		// 重複チェック用
		Set<RecordKey> keys = new HashSet<>();
		
		for (String employeeCode : employeeCodes) {
			
			employeeCodeCanonicalization.canonicalize(require, context, employeeCode)
				.ifLeft(errors -> {
					errors.forEach(error -> require.add(ExternalImportError.of(context.getDomainId(), error)));
				})
				.ifRight(interms -> {
					canonicalize(require, context, keys, interms.collect(Collectors.toList()));
				});
		}
	}

	private void canonicalize(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context,
			Set<RecordKey> keys,
			List<IntermediateResult> interms) {
		
		saveToDelete(require, context, interms);
		
		for (val interm : interms) {

			// 日付不明（受け入れない）ならば重複チェック不要
			if (interm.isImporting(Items.TARGET_DATE)) {
				val key = RecordKey.of(interm);
				if (keys.contains(key)) {
					require.add(ExternalImportError.record(interm.getRowNo(), context.getDomainId(), "受入データの中に重複レコード（社員と日付が同じ）があります。"));
					continue;
				}
				keys.add(key);
			}
			
			canonicalizeRecord(require, context, interm);
		}
	}
	
	/**
	 * 重複チェック用
	 */
	@Value
	private static class RecordKey {
		String employeeId;
		GeneralDate date; // nullable
		
		static RecordKey of(IntermediateResult interm) {
			String employeeId = interm.getItemByNo(Items.SID).get().getString();
			GeneralDate date = interm.getItemByNo(Items.TARGET_DATE).get().getDate();
			return new RecordKey(employeeId, date);
		}
	}
	
	/**
	 * 対象社員のデータを全て削除するための補正データを保存する
	 * @param require
	 * @param context
	 * @param interms
	 */
	private void saveToDelete(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context,
			List<IntermediateResult> intermsList) {
			
		if (intermsList.isEmpty()) {
			return;
		}
		
		int itemNo = getItemNoOfEmployeeId();
		String employeeId = intermsList.get(0).getItemByNo(itemNo).get().getString();
		
		val toDelete = AnyRecordToDelete.create(context)
				.addKey(itemNo, StringifiedValue.of(employeeId));
		
		require.save(context, toDelete);
		
	}
	
	private void canonicalizeRecord(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context,
			IntermediateResult interm) {
		
		// 新規IDを発行
		interm = interm.addCanonicalized(CanonicalItem.of(Items.DATA_ID, IdentifierUtil.randomUniqueId()));
		
		// 日付不明
		boolean isDateUnknown = interm.getItemByNo(Items.TARGET_DATE).map(d -> d.getValue()).orElse(null) == null;
		interm = interm.addCanonicalized(CanonicalItem.of(Items.TARGET_DATE_UNKNOWN, isDateUnknown ? 1 : 0));
		
		interm = canonicalizeExtends(require, context, interm);
		
		require.save(context, interm.complete());
	}
	
	/**
	 * 派生クラス側で追加の正準化が必要ならOverrideする
	 * @param require
	 * @param context
	 * @param interm
	 * @return
	 */
	protected abstract IntermediateResult canonicalizeExtends(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context,
			IntermediateResult interm);

	public static interface RequireCanonicalize {
	}
	
	private static final class Items {
		
		// データID
		static final int DATA_ID = 100;
		
		// 対象日（振休日とか休出日とか）
		static final int TARGET_DATE = 2;
		
		// 社員ID
		static final int SID = 101;
		
		// 日付不明
		static final int TARGET_DATE_UNKNOWN = 102;
	}
	
	private int itemNoEmployeeCode() {
		return employeeCodeCanonicalization.getItemNoEmployeeCode();
	}

	@Override
	public ImportingDataMeta appendMeta(ImportingDataMeta source) {
		return source;
	}

	@Override
	public AtomTask adjust(
			DomainCanonicalization.RequireAdjsut require,
			ExecutionContext context,
			List<AnyRecordToChange> recordsToChange,
			List<AnyRecordToDelete> recordsToDelete) {

		if (recordsToChange.size() > 0) {
			throw new RuntimeException("既存データの変更は無いはず");
		}
		
		return AtomTask.of(() -> {
			for (val toDelete : recordsToDelete) {
				
				String employeeId = toDelete.getKey(getItemNoOfEmployeeId()).asString();
				deleteExistingData(require, employeeId);
			}
		});
	}
	
	protected abstract void deleteExistingData(RequireAdjust require, String employeeId);
	
	public static interface RequireAdjust extends
			SubstituteHolidayCanonicalization.RequireAdjust,
			SubstituteWorkCanonicalization.RequireAdjust,
			CompensatoryHolidayCanonicalization.RequireAdjust,
			HolidayWorkCanonicalization.RequireAdjust {
	}

	@Override
	public int getItemNoOfEmployeeId() {
		return employeeCodeCanonicalization.getItemNoEmployeeId();
	}
}
