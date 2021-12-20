package nts.uk.ctx.at.request.dom.applicationreflect.algorithm.reflectprocess;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.enums.EnumAdaptor;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.scherec.convert.ConvertApplicationToShare;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExecutionCondition;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.PreCheckProcessWorkSchedule;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.PreCheckProcessWorkSchedule.PreCheckProcessResult;
import nts.uk.ctx.at.request.dom.applicationreflect.object.PreApplicationWorkScheReflectAttr;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * @author thanh_nx
 *
 *         勤務予定への反映処理
 */
public class ProcessReflectWorkSchedule {

	public static Pair<ReflectStatusResult, Optional<AtomTask>> processReflect(Require require, String companyId,
			int closureId, Application application, boolean isCalWhenLock,
			GeneralDate targetDate, ReflectStatusResult statusWorkSchedule, List<SEmpHistImport> empHist, String execId) {

		// [input. 処理中の申請. 事前事後区分]をチェック
		if (application.getPrePostAtr() == PrePostAtr.POSTERIOR) {
			// 勤務予定の反映状態を「反映済み」にする
			statusWorkSchedule.setReflectStatus(ReflectedState.REFLECTED);
			return Pair.of(statusWorkSchedule, Optional.empty());
		}

		// [申請反映実行条件]を取得する
		Optional<AppReflectExecutionCondition> appReFlectExec = require.findAppReflectExecCond(companyId);
		/** [事前申請を勤務予定に反映する]をチェック */
		if (!appReFlectExec.isPresent() || appReFlectExec.get().getApplyBeforeWorkSchedule() == PreApplicationWorkScheReflectAttr.NOT_REFLECT) {
			statusWorkSchedule.setReflectStatus(ReflectedState.REFLECTED);
			return Pair.of(statusWorkSchedule, Optional.empty());
		}

		// [勤務予定が確定状態でも反映する]をチェック
		if (appReFlectExec.get().getEvenIfScheduleConfirmed() == NotUseAtr.NOT_USE) {
			// 事前チェック処理
			PreCheckProcessResult preCheckProcessResult = PreCheckProcessWorkSchedule.preCheck(require, companyId,
					application, closureId, isCalWhenLock, statusWorkSchedule, targetDate, empHist);
			if (preCheckProcessResult.getProcessFlag() == NotUseAtr.NOT_USE)
				return Pair.of(statusWorkSchedule, Optional.empty());
		}

		// 勤務予定に反映
		Pair<ReflectStatusResult, AtomTask> result = require.process(ConvertApplicationToShare.toAppliction(application, targetDate), targetDate,
				statusWorkSchedule,
				appReFlectExec.get().getApplyBeforeWorkSchedule().value, execId);
		return Pair.of(statusResult(result.getLeft(), statusWorkSchedule), Optional.of(result.getRight()));

	}

	public static ReflectStatusResult statusResult(ReflectStatusResult share, ReflectStatusResult result) {
		result.setReflectStatus(EnumAdaptor.valueOf(share.getReflectStatus().value, ReflectedState.class));
		return result;
	}

	public static interface Require extends PreCheckProcessWorkSchedule.Require {

		/**
		 * require{ 申請反映実行条件を取得する(会社ID) ｝
		 */
		public Optional<AppReflectExecutionCondition> findAppReflectExecCond(String companyId);

//		/**
//		 * 
//		 * require{ 申請反映設定を取得する(会社ID、申請種類） }
//		 * RequestSettingRepository.getAppReflectionSetting
//		 */
//		public Optional<AppReflectionSetting> getAppReflectionSetting(String companyId, ApplicationType appType);

		// ReflectApplicationWorkScheduleAdapter
		public Pair<ReflectStatusResult, AtomTask> process(ApplicationShare application, GeneralDate date,
				ReflectStatusResult reflectStatus, int preAppWorkScheReflectAttr, String execId);

	}

}
