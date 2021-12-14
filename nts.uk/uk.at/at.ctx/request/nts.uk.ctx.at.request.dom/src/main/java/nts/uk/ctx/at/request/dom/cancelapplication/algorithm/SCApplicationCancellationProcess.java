package nts.uk.ctx.at.request.dom.cancelapplication.algorithm;

import java.util.Arrays;
import java.util.Optional;

import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.cancelapplication.RQRecoverAppReflectImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.scherec.convert.ConvertApplicationToShare;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExecutionCondition;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.PreCheckProcessWorkSchedule;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.PreCheckProcessWorkSchedule.PreCheckProcessResult;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * @author thanh_nx
 *
 *         勤務予定の取消処理
 */
public class SCApplicationCancellationProcess {

	public static SCCancelProcessOneDayOutput processSchedule(Require require, String cid, Application app,
			GeneralDate date, int closureId, ReflectStatusResult statusWorkSchedule, NotUseAtr dbRegisterClassfi, EmploymentHistShareImport empHist) {

		// [input. 処理中の申請. 事前事後区分]をチェック
		// [input.反映状態.反映状態]をチェック
		if (app.getPrePostAtr() == PrePostAtr.POSTERIOR  || statusWorkSchedule.getReflectStatus() != ReflectedState.REFLECTED) {
			// [input.勤務予定の反映状態.反映状態]を「取消済」にする
			statusWorkSchedule.setReflectStatus(ReflectedState.CANCELED);
			return new SCCancelProcessOneDayOutput(statusWorkSchedule, Optional.empty(), AtomTask.none());
		}

		// DB登録するか区分をチェック
		if (dbRegisterClassfi == NotUseAtr.USE) {
			// [申請反映実行条件]を取得する
			Optional<AppReflectExecutionCondition> appReflectCond = require.findAppReflectExecCond(cid);

			// [勤務予定が確定状態でも反映する]をチェック
			if (appReflectCond.isPresent() && appReflectCond.get().getEvenIfScheduleConfirmed() == NotUseAtr.NOT_USE) {

				// 事前チェック処理
				PreCheckProcessResult preCheck = PreCheckProcessWorkSchedule.preCheck(require, cid, app, closureId,
						false, statusWorkSchedule, date, Arrays.asList(new SEmpHistImport(empHist.getEmployeeId(),
								empHist.getEmploymentCode(), "", empHist.getPeriod())));

				if (preCheck.getProcessFlag() == NotUseAtr.NOT_USE) {
					return new SCCancelProcessOneDayOutput(preCheck.getReflectStatus(), Optional.empty(), AtomTask.none());
				}
			}
		}

		// 勤務予定の取消
		RQRecoverAppReflectImport result = require.process(ConvertApplicationToShare.toOnlyAppliction(app), date,
				statusWorkSchedule, dbRegisterClassfi);
		return new SCCancelProcessOneDayOutput(result.getReflectStatus(), result.getWorkRecord(),
				result.getAtomTask());

	}

	public static interface Require extends PreCheckProcessWorkSchedule.Require {
		/**
		 * require{ 申請反映実行条件を取得する(会社ID) ｝
		 */
		// AppReflectExeConditionRepository
		public Optional<AppReflectExecutionCondition> findAppReflectExecCond(String companyId);

		// RecoverSCBeforeAppReflectAdapter
		public RQRecoverAppReflectImport process(ApplicationShare application, GeneralDate date,
				ReflectStatusResult reflectStatus, NotUseAtr dbRegisterClassfi);
	}
}
