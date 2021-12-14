package nts.uk.ctx.at.request.dom.cancelapplication.algorithm;

import java.util.Arrays;
import java.util.Optional;

import lombok.val;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.cancelapplication.RQRecoverAppReflectOutputImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.scherec.convert.ConvertApplicationToShare;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExecutionCondition;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.PreCheckProcessWorkRecord;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.PreCheckProcessWorkSchedule.PreCheckProcessResult;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * @author thanh_nx
 *
 *         勤務実績の取消処理
 */
public class RCApplicationCancellationProcess {

	public static RCCancelProcessOneDayOutput processRecord(Require require, String cid, Application app,
			GeneralDate date, int closureId, ReflectStatusResult statusWorkRecord, NotUseAtr dbRegisterClassfi, EmploymentHistShareImport empHist) {

		// [input.反映状態.反映状態]をチェック
		if (statusWorkRecord.getReflectStatus() != ReflectedState.REFLECTED) {
			statusWorkRecord.setReflectStatus(ReflectedState.CANCELED);
			return new RCCancelProcessOneDayOutput(statusWorkRecord, Optional.empty(), AtomTask.none());
		}

		// DB登録するか区分をチェック
		if (dbRegisterClassfi == NotUseAtr.USE) {
			// [申請反映実行条件]を取得する
			Optional<AppReflectExecutionCondition> appReflectCond = require.findAppReflectExecCond(cid);
			// [勤務実績が確定状態でも反映する]をチェック
			if (appReflectCond.isPresent()
					&& appReflectCond.get().getEvenIfWorkRecordConfirmed() == NotUseAtr.NOT_USE) {
				// 事前チェック処理
				PreCheckProcessResult preCheck = PreCheckProcessWorkRecord.preCheck(require, cid, app, closureId, false,
						statusWorkRecord, date, Arrays.asList(new SEmpHistImport(empHist.getEmployeeId(), empHist.getEmploymentCode(), "", empHist.getPeriod())));
				if (preCheck.getProcessFlag() == NotUseAtr.NOT_USE) {
					return new RCCancelProcessOneDayOutput(preCheck.getReflectStatus(), Optional.empty(), AtomTask.none());
				}
			}

		}

		// 勤務実績の取消
		val result = require.processRecover(ConvertApplicationToShare.toOnlyAppliction(app), date, statusWorkRecord,
				dbRegisterClassfi);
		return new RCCancelProcessOneDayOutput(result.getReflectStatus(), result.getWorkRecord(),
				result.getAtomTask());

	}

	public static interface Require extends PreCheckProcessWorkRecord.Require {

		/**
		 * require{ 申請反映実行条件を取得する(会社ID) ｝
		 */
		// AppReflectExeConditionRepository
		public Optional<AppReflectExecutionCondition> findAppReflectExecCond(String companyId);

		// RecoverRCBeforeAppReflectAdaper
		public RQRecoverAppReflectOutputImport processRecover(ApplicationShare application, GeneralDate date,
				ReflectStatusResult reflectStatus, NotUseAtr dbRegisterClassfi);
	}
}
