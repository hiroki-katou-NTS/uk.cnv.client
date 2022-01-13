package nts.uk.ctx.at.request.dom.applicationreflect.algorithm.reflectprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.enums.EnumAdaptor;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.scherec.convert.ConvertApplicationToShare;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExecutionCondition;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.PreCheckProcessWorkRecord;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.PreCheckProcessWorkSchedule.PreCheckProcessResult;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * @author thanh_nx
 *
 *         勤務実績への反映処理
 */
public class ProcessReflectWorkRecord {

	public static Pair<ReflectStatusResult, Optional<AtomTask>> processReflect(Require require, String companyId,
			int closureId, Application application, boolean isCalWhenLock,
			GeneralDate targetDate, ReflectStatusResult statusWorkRecord, List<SEmpHistImport> empHist, String execId) {

		// [申請反映実行条件]を取得する
		Optional<AppReflectExecutionCondition> appReFlectExec = require.findAppReflectExecCond(companyId);

		// [勤務実績が確定状態でも反映する]をチェック
		if (!appReFlectExec.isPresent() || appReFlectExec.get().getEvenIfWorkRecordConfirmed() == NotUseAtr.NOT_USE) {
			// 事前チェック処理
			PreCheckProcessResult preCheckResult = PreCheckProcessWorkRecord.preCheck(require, companyId, application,
					closureId, isCalWhenLock, statusWorkRecord, targetDate, empHist);
			if (preCheckResult.getProcessFlag() == NotUseAtr.NOT_USE) {
				return Pair.of(preCheckResult.getReflectStatus(), Optional.empty());
			}
		}

		List<AtomTask> tasks = new ArrayList<>();
		GeneralDateTime reflectTime = GeneralDateTime.now();
		// 勤務実績に反映 -- in process
		ReflectedState before = EnumAdaptor.valueOf(statusWorkRecord.getReflectStatus().value, ReflectedState.class);
		Pair<ReflectStatusResult, Optional<AtomTask>> result = require
				.processWork(ConvertApplicationToShare.toAppliction(application, targetDate), targetDate, statusWorkRecord, reflectTime, execId);
		result.getRight().ifPresent(x -> tasks.add(x));
		// 申請理由の反映-- in process chua co don xin lam them
		Optional<AtomTask> task = ReflectApplicationReason.reflectReason(require, application, targetDate, reflectTime, execId, before);
		task.ifPresent(x -> tasks.add(x));

		return Pair.of(ProcessReflectWorkSchedule.statusResult(result.getLeft(), statusWorkRecord), Optional.of(AtomTask.bundle(tasks)));
	}

	public static interface Require extends PreCheckProcessWorkRecord.Require, ReflectApplicationReason.Require {

		/**
		 * require{ 申請反映実行条件を取得する(会社ID) ｝
		 */
		public Optional<AppReflectExecutionCondition> findAppReflectExecCond(String companyId);

//		/**
//		 * 
//		 * require{ 社員の作業データ設定を取得する(社員ID） }
//		 * 
//		 */
//		public Optional<EmployeeWorkDataSetting> getEmpWorkDataSetting(String employeeId);

		// ReflectApplicationWorkRecordAdapter
		public Pair<ReflectStatusResult, Optional<AtomTask>> processWork(ApplicationShare application, GeneralDate date,
				ReflectStatusResult reflectStatus, GeneralDateTime reflectTime, String execId);
	}
}
