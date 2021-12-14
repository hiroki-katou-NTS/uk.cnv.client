package nts.uk.ctx.at.request.dom.cancelapplication.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.DailyAttendanceUpdateStatus;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflect;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflectDaily;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.ReflectionStatusOfDay;
import nts.uk.ctx.at.request.dom.applicationreflect.object.OneDayReflectStatusOutput;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * @author thanh_nx
 *
 *
 *         申請の取消処理
 */
public class ApplicationCancellationProcess {

	public static AppCancellationProcessOutput cancelProcess(Require require, String cid, Application application,
			NotUseAtr dbRegisterClassfi) {

		// 社員の雇用履歴を全て取得する
		List<EmploymentHistShareImport> lstEmpHist = require.findEmpHistbySid(application.getEmployeeID());

		// 社員に対応する締め開始日を取得する
		CacheCarrier cache = new CacheCarrier();
		Optional<GeneralDate> clsDateOpt = GetClosureStartForEmployee.algorithm(require, cache,
				application.getEmployeeID());
		if (!clsDateOpt.isPresent()) {
			return new AppCancellationProcessOutput(application, new ArrayList<>(), new ArrayList<>(), AtomTask.none());
		}

		// 処理期間を取得する
		DatePeriod periodProcess = null;
		if (application.getOpAppStartDate().isPresent() && application.getOpAppEndDate().isPresent()) {
			periodProcess = new DatePeriod(application.getOpAppStartDate().get().getApplicationDate(),
					application.getOpAppEndDate().get().getApplicationDate());
		} else {
			periodProcess = new DatePeriod(application.getAppDate().getApplicationDate(),
					application.getAppDate().getApplicationDate());
		}

		List<IntegrationOfDaily> lstSchedule = new ArrayList<>(), lstWorkRecord = new ArrayList<>();
		List<GeneralDate> lstRemove = new ArrayList<>();
		List<AtomTask> lstAtomtask = new ArrayList<AtomTask>();
		for (GeneralDate dateInProcess : periodProcess.datesBetween()) {
			// 処理対象日のチェック
			if (dateInProcess.before(clsDateOpt.get())) {
				// [対象日の反映状態]に理由をセットする
				application.getReflectionStatus().getListReflectionStatusOfDay().replaceAll(x -> {
					if (x.getTargetDate().equals(dateInProcess)) {
						x.setOpUpdateStatusAppCancel(Optional.of(x.getOpUpdateStatusAppCancel().map(y -> {
							y.setOpReasonActualCantReflect(
									Optional.of(ReasonNotReflectDaily.TIGHTENING_PROCESS_COMPLETED));
							y.setOpReasonScheCantReflect(Optional.of(ReasonNotReflect.TIGHTENING_PROCESS_COMPLETED));
							return y;
						}).orElse(DailyAttendanceUpdateStatus.createNew(null, null,
								ReasonNotReflectDaily.TIGHTENING_PROCESS_COMPLETED.value,
								ReasonNotReflect.TIGHTENING_PROCESS_COMPLETED.value))));
						return x;
					}
					return x;
				});
				continue;
			}

			// 取得した所属雇用履歴（List）から、処理対象日に対応する所属雇用履歴を取得
			EmploymentHistShareImport empHist = lstEmpHist.stream().filter(x -> x.getPeriod().contains(dateInProcess))
					.findFirst().orElse(null);
			if (empHist == null) {
				continue;
			}

			// 締めIDを取得する
			Optional<ClosureEmployment> clsEmpOpt = require.findByEmploymentCD(cid, empHist.getEmploymentCode());
			if (!clsEmpOpt.isPresent()) {
				continue;
			}

			// 1日分の取消処理]
			CancelProcessOneDayOutput oneDayOut = processOneDay(require, cid, application, dateInProcess,
					clsEmpOpt.get().getClosureId(), dbRegisterClassfi, empHist);
			lstRemove.add(dateInProcess);
			lstAtomtask.add(oneDayOut.getTask());
			// 1日の反映状態を[対象日の反映状態]にセットする
			application.getReflectionStatus().getListReflectionStatusOfDay().replaceAll(x -> {
				if (x.getTargetDate().equals(dateInProcess)) {
					//予定反映状態＝勤務予定の反映状態.反映状態
					x.setScheReflectStatus(oneDayOut.getOneDayReflect().getStatusWorkSchedule().getReflectStatus());
					//実績反映状態＝勤務実績の反映状態.反映状態
					x.setActualReflectStatus(oneDayOut.getOneDayReflect().getStatusWorkRecord().getReflectStatus());
					if (x.getOpUpdateStatusAppCancel().isPresent()) {
						//申請取消の更新状態. 実績反映不可理由＝勤務実績の反映状態.日別実績反映不可理由
						x.getOpUpdateStatusAppCancel().get().setOpReasonActualCantReflect(Optional.ofNullable(
								oneDayOut.getOneDayReflect().getStatusWorkRecord().getReasonNotReflectWorkRecord()));
						//申請取消の更新状態. 予定反映不可理由＝勤務予定の反映状態.予定反映不可理由
						x.getOpUpdateStatusAppCancel().get().setOpReasonScheCantReflect(Optional.ofNullable(oneDayOut
								.getOneDayReflect().getStatusWorkSchedule().getReasonNotReflectWorkSchedule()));
					} else {
						x.setOpUpdateStatusAppCancel(
								Optional.of(new DailyAttendanceUpdateStatus(Optional.empty(), Optional.empty(),
										Optional.ofNullable(oneDayOut.getOneDayReflect().getStatusWorkRecord()
												.getReasonNotReflectWorkRecord()),
										Optional.ofNullable(oneDayOut.getOneDayReflect().getStatusWorkSchedule()
												.getReasonNotReflectWorkSchedule()))));
					}

					GeneralDateTime now = GeneralDateTime.now();
					//勤務予定の反映状態.反映状態＝[取消済]の場合のみ
					if (oneDayOut.getOneDayReflect().getStatusWorkSchedule()
							.getReflectStatus() == ReflectedState.CANCELED) {
						x.getOpUpdateStatusAppCancel().get().setOpScheReflectDateTime(Optional.of(now));
					}

					//　勤務実績の反映状態.反映状態＝[取消済]の場合のみ　
					if (oneDayOut.getOneDayReflect().getStatusWorkRecord()
							.getReflectStatus() == ReflectedState.CANCELED) {
						x.getOpUpdateStatusAppCancel().get().setOpActualReflectDateTime(Optional.of(now));
					}
				}
				return x;
			});

		}

		AtomTask atomTask = AtomTask.none();
		// DB登録するか区分をチェック
		if (dbRegisterClassfi == NotUseAtr.USE) {
			lstAtomtask.add(AtomTask.of(() -> {
				// 申請の反映状態の更新
				require.updateApp(application);

				// 暫定データの登録
				require.registerRemain(cid, application.getEmployeeID(), lstRemove);
			}));
			atomTask = AtomTask.bundle(lstAtomtask);
		}

		return new AppCancellationProcessOutput(application, lstSchedule, lstWorkRecord, atomTask);
	}

	// 1日分の取消処理
	private static CancelProcessOneDayOutput processOneDay(Require require, String cid, Application app,
			GeneralDate date, int closureId, NotUseAtr dbRegisterClassfi, EmploymentHistShareImport empHist) {
		// [対象日の反映状態]の内容を<output>1日の反映状態にセット
		ReflectionStatusOfDay statusOfDay = app.getReflectionStatus().getListReflectionStatusOfDay().stream()
				.filter(x -> x.getTargetDate().equals(date)).findFirst().orElse(null);
		OneDayReflectStatusOutput oneDayReflect = new OneDayReflectStatusOutput(
				new ReflectStatusResult(statusOfDay.getScheReflectStatus(), null,
						statusOfDay.getOpUpdateStatusAppCancel().filter(x -> x.getOpReasonScheCantReflect().isPresent())
								.map(x -> x.getOpReasonScheCantReflect().get()).orElse(null)),
				new ReflectStatusResult(statusOfDay.getActualReflectStatus(),
						statusOfDay.getOpUpdateStatusAppCancel()
								.filter(x -> x.getOpReasonActualCantReflect().isPresent())
								.map(x -> x.getOpReasonActualCantReflect().get()).orElse(null),
						null));
		// 勤務予定の取消処理
		SCCancelProcessOneDayOutput scOutPut = SCApplicationCancellationProcess.processSchedule(require, cid, app, date,
				closureId, oneDayReflect.getStatusWorkSchedule(), dbRegisterClassfi, empHist);

		// 勤務実績の取消処理
		RCCancelProcessOneDayOutput rcOutPut = RCApplicationCancellationProcess.processRecord(require, cid, app, date,
				closureId, oneDayReflect.getStatusWorkRecord(), dbRegisterClassfi, empHist);
		AtomTask atomtask = AtomTask.of(scOutPut.getAtomTask()).then(rcOutPut.getAtomTask());

		return new CancelProcessOneDayOutput(
				new OneDayReflectStatusOutput(scOutPut.getStatusWorkSchedule(), rcOutPut.getStatusWorkRecord()),
				scOutPut.getSchedule(), rcOutPut.getWorkRecord(), atomtask);
	}

	public static interface Require extends GetClosureStartForEmployee.RequireM1,
			SCApplicationCancellationProcess.Require, RCApplicationCancellationProcess.Require {

		// ShareEmploymentAdapter.findByEmployeeIdOrderByStartDate
		public List<EmploymentHistShareImport> findEmpHistbySid(String employeeId);

		// ClosureEmploymentRepository
		public Optional<ClosureEmployment> findByEmploymentCD(String companyID, String employmentCD);

		// ApplicationRepository.update
		public void updateApp(Application application);

		// InterimRemainDataMngRegisterDateChange.registerDateChange
		public void registerRemain(String cid, String sid, List<GeneralDate> lstDate);
	}
}
