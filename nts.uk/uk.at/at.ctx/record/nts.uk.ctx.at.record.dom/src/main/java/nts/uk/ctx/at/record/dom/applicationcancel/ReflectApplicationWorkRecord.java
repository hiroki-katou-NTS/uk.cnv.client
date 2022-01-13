package nts.uk.ctx.at.record.dom.applicationcancel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.adapter.request.application.state.RCReflectStatusResult;
import nts.uk.ctx.at.record.dom.adapter.request.application.state.RCReflectedState;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailywork.worktime.empwork.EmployeeWorkDataSetting;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ReflectedStateShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.StampRequestModeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.AppRecordImageShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.CreateApplicationReflectionHist;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.RCCreateDailyAfterApplicationeReflect;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.CorrectDailyAttendanceService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateOption;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;

/**
 * @author thanh_nx
 *
 *         [RQ665]申請を勤務実績へ反映する
 */
public class ReflectApplicationWorkRecord {

	public static Pair<RCReflectStatusResult, Optional<AtomTask>> process(Require require,  String cid, ApplicationShare application,
			GeneralDate date, RCReflectStatusResult reflectStatus, GeneralDateTime reflectTime, String execId) {
		// [input.申請.打刻申請モード]をチェック
		GeneralDate dateTarget = date;
		Optional<Stamp> stamp = Optional.empty();
		if (application.getOpStampRequestMode().isPresent()
				&& application.getOpStampRequestMode().get() == StampRequestModeShare.STAMP_ONLINE_RECORD) {
			// レコーダイメージ申請の対象日を取得する
			Pair<Optional<GeneralDate>, Optional<Stamp>> dateOpt = GetTargetDateRecordApplication.getTargetDate(require,
					cid, (AppRecordImageShare) application);
			if (dateOpt.getLeft().isPresent()) {
				dateTarget = dateOpt.getLeft().get();
				stamp = dateOpt.getRight();
			}
		}

		// 勤務実績から日別実績(work）を取得する
		Optional<IntegrationOfDaily> domainDaily = require.findDaily(application.getEmployeeID(), dateTarget);
		if (!domainDaily.isPresent())
			return Pair.of(reflectStatus, Optional.empty());

		// input.日別勤怠(work）を[反映前の日別勤怠(work)]へコピーして保持する
		IntegrationOfDaily domainBeforeReflect = createDailyDomain(require, domainDaily.get());

		// 日別勤怠(申請取消用Work）を作成して、日別勤怠(work）をコピーする
		DailyRecordOfApplication dailyRecordApp = new DailyRecordOfApplication(new ArrayList<>(),
				ScheduleRecordClassifi.RECORD, createDailyDomain(require, domainDaily.get()));

		// 申請.打刻申請モードをチェック
		ChangeDailyAttendance changeAtt;
		if (application.getOpStampRequestMode().isPresent()
				&& application.getOpStampRequestMode().get() == StampRequestModeShare.STAMP_ONLINE_RECORD) {
			changeAtt = new ChangeDailyAttendance(true, true, false, false, ScheduleRecordClassifi.RECORD, true);
			/// 打刻申請（NRモード）を反映する -- itemId
			TimeStampApplicationNRMode.process(require, cid, dateTarget,
					(AppRecordImageShare) application, dailyRecordApp, stamp, changeAtt);
		} else {
			/// 申請の反映（勤務実績） in process
			val affterReflect = RCCreateDailyAfterApplicationeReflect.process(require, cid, application, dailyRecordApp, dateTarget);

			changeAtt = ChangeDailyAttendance.createChangeDailyAtt(affterReflect.getLstItemId(), ScheduleRecordClassifi.RECORD);
		}

		// 日別実績の補正処理 --- create default ???? sau xu ly phan anh check lai
		IntegrationOfDaily domainCorrect = CorrectDailyAttendanceService.processAttendanceRule(require,
				dailyRecordApp.getDomain(), changeAtt);

		// 振休振出として扱う日数を補正する
		WorkInfoOfDailyAttendance workInfoAfter = CorrectDailyAttendanceService.correctFurikyu(require,
				domainBeforeReflect.getWorkInformation(), domainCorrect.getWorkInformation());
		domainCorrect.setWorkInformation(workInfoAfter);
		dailyRecordApp.setDomain(domainCorrect);

		// 日別実績の修正からの計算 -- co xu ly tinh toan khac ko hay cua lich
		List<IntegrationOfDaily> lstAfterCalc = require.calculateForSchedule(CalculateOption.asDefault(),
				Arrays.asList(domainCorrect), Optional.empty(), ExecutionType.NORMAL_EXECUTION);
		if (!lstAfterCalc.isEmpty()) {
			dailyRecordApp.setDomain(lstAfterCalc.get(0));
		}

		ReflectedStateShare before = EnumAdaptor.valueOf(reflectStatus.getReflectStatus().value, ReflectedStateShare.class);
		AtomTask task = AtomTask.of(() -> {
			//エラーで本人確認と上司承認を解除する
			require.removeConfirmApproval(Arrays.asList(dailyRecordApp.getDomain().getDomain()));
			
			// 勤務実績の更新
			require.addAllDomain(dailyRecordApp.getDomain(), true);

			// 申請反映履歴を作成する
			CreateApplicationReflectionHist.create(require, application.getAppID(), ScheduleRecordClassifi.RECORD,
					dailyRecordApp, domainBeforeReflect, reflectTime, execId, before);

		});
		// [input.勤務実績の反映状態]を「反映済み」に更新する
		reflectStatus.setReflectStatus(RCReflectedState.REFLECTED);

		return Pair.of(reflectStatus, Optional.of(task));
	}
	
	private static IntegrationOfDaily createDailyDomain(Require require, IntegrationOfDaily domainDaily) {
		DailyRecordToAttendanceItemConverter converter = require.createDailyConverter();
		converter.setData(domainDaily).employeeId(domainDaily.getEmployeeId()).workingDate(domainDaily.getYmd());
		return converter.toDomain();
	}

	public static interface Require extends GetTargetDateRecordApplication.Require,
			CorrectDailyAttendanceService.Require, CreateApplicationReflectionHist.Require,
			TimeStampApplicationNRMode.Require, RCCreateDailyAfterApplicationeReflect.Require {
		/**
		 * 
		 * require{ 社員の作業データ設定を取得する(社員ID） }
		 * 
		 */
		public Optional<EmployeeWorkDataSetting> getEmpWorkDataSetting(String employeeId);

		// DailyRecordShareFinder
		public Optional<IntegrationOfDaily> findDaily(String employeeId, GeneralDate date);

		// ConvertDailyRecordToAd
		public DailyRecordToAttendanceItemConverter createDailyConverter();

		// CalculateDailyRecordServiceCenter
		public List<IntegrationOfDaily> calculateForSchedule(CalculateOption calcOption,
				List<IntegrationOfDaily> integrationOfDaily, Optional<ManagePerCompanySet> companySet, ExecutionType reCalcAtr);

		// DailyRecordAdUpService
		public void addAllDomain(IntegrationOfDaily domain, boolean removeError);
		
		//DailyRecordAdUpService
		public void removeConfirmApproval(List<IntegrationOfDaily> domainDaily);
	}
}
