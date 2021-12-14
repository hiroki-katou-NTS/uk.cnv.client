package nts.uk.ctx.exio.dom.input;

import java.util.List;

import lombok.val;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AdjustExistingData;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomain;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.transfer.TransferCanonicalData;

/**
 * 受入処理を実行する
 */
public class ExecuteImporting {

	public static Iterable<AtomTask> execute(Require require, ExternalImportSetting setting) {
		
		val context = ExecutionContext.create(setting);
		
		val transactionUnit = require.getImportingDomain(context.getDomainId())
				.getTransactionUnit();
		
		switch (transactionUnit) {
		case ALL:
			return executeAll(require, context);
		case EMPLOYEE:
			return executeEmployees(require, context);
		default:
			throw new RuntimeException("unknown: " + transactionUnit);
		}
	}
	
	/**
	 * 全データを一括で受け入れる
	 * @param require
	 * @param context
	 * @return
	 */
	private static Iterable<AtomTask> executeAll(Require require, ExecutionContext context) {
		
		return AtomTask.iterateSingle(() -> {

			AtomTask atomTaskAdjust = AdjustExistingData.adjust(require, context);
			
			AtomTask atomTaskTransfer = TransferCanonicalData.transferAll(require, context);
			
			return atomTaskAdjust.then(atomTaskTransfer);
		});
	}
	
	/**
	 * １社員ずつ受け入れる
	 * @param require
	 * @param context
	 * @return
	 */
	private static Iterable<AtomTask> executeEmployees(Require require, ExecutionContext context) {
		
		val employeeIds = require.getAllEmployeeIdsOfCanonicalizedData(context);
		
		return AtomTask.iterate(employeeIds, employeeId -> {
			
			AtomTask atomTaskAdjust = AdjustExistingData.adjust(require, context, employeeId);
			
			AtomTask atomTaskTransfer = TransferCanonicalData.transferByEmployee(require, context, employeeId);
			
			return atomTaskAdjust.then(atomTaskTransfer);
		});
	}
	
	public static interface Require extends
			AdjustExistingData.RequireAll,
			AdjustExistingData.RequireEmployee,
			TransferCanonicalData.Require {

		ImportingDomain getImportingDomain(ImportingDomainId domainId);
		
		List<String> getAllEmployeeIdsOfCanonicalizedData(ExecutionContext context);
	}
}
