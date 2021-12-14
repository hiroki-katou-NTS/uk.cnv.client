package nts.uk.ctx.at.schedule.pubimp.appremove;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.schedule.dom.appreflectprocess.change.state.SCReasonNotReflect;
import nts.uk.ctx.at.schedule.dom.appreflectprocess.change.state.SCReasonNotReflectDaily;
import nts.uk.ctx.at.schedule.dom.appreflectprocess.change.state.SCReflectStatusResult;
import nts.uk.ctx.at.schedule.dom.appreflectprocess.change.state.SCReflectedState;
import nts.uk.ctx.at.schedule.dom.appremove.RecoverWorkScheduleBeforeAppReflect;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.appremove.RecoverSCBeforeAppReflectPub;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.appremove.SCRecoverAppReflectExport;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.export.SCReasonNotReflectDailyExport;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.export.SCReasonNotReflectExport;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.export.SCReflectStatusResultExport;
import nts.uk.ctx.at.schedule.pub.appreflectprocess.export.SCReflectedStateExport;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistory;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistoryRepo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ICorrectionAttendanceRule;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateDailyRecordServiceCenterNew;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateOption;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * @author thanh_nx
 *
 */
@Stateless
public class RecoverSCBeforeAppReflectPubImpl implements RecoverSCBeforeAppReflectPub {

	@Inject
	private DailyRecordConverter dailyRecordConverter;

	@Inject
	private ICorrectionAttendanceRule correctionAfterTimeChange;

	@Inject
	private CalculateDailyRecordServiceCenterNew calculateDailyRecordServiceCenter;

	@Inject
	private WorkScheduleRepository workScheduleRepository;
	
	@Inject
	private ApplicationReflectHistoryRepo applicationReflectHistoryRepo;

	@Override
	public SCRecoverAppReflectExport process(Object application, GeneralDate date,
			SCReflectStatusResultExport reflectStatus, NotUseAtr dbRegisterClassfi) {
		RequireImpl impl = new RequireImpl(dailyRecordConverter, correctionAfterTimeChange,
				calculateDailyRecordServiceCenter, workScheduleRepository, applicationReflectHistoryRepo);
		val result = RecoverWorkScheduleBeforeAppReflect.process(impl, (ApplicationShare) application, date,
				convertToShare(reflectStatus), dbRegisterClassfi);
		return new SCRecoverAppReflectExport(convertToExport(result.getReflectStatus()), result.getSchedule().map(x -> (Object)x),
				result.getAtomTask());
	}

	private SCReflectStatusResult convertToShare(SCReflectStatusResultExport reflectStatus) {

		return new SCReflectStatusResult(
				EnumAdaptor.valueOf(reflectStatus.getReflectStatus().value, SCReflectedState.class),
				reflectStatus.getReasonNotReflectWorkRecord() == null ? null
						: EnumAdaptor.valueOf(reflectStatus.getReasonNotReflectWorkRecord().value,
								SCReasonNotReflectDaily.class),
				reflectStatus.getReasonNotReflectWorkSchedule() == null ? null
						: EnumAdaptor.valueOf(reflectStatus.getReasonNotReflectWorkSchedule().value,
								SCReasonNotReflect.class));
	}

	private SCReflectStatusResultExport convertToExport(SCReflectStatusResult reflectStatus) {

		return new SCReflectStatusResultExport(
				EnumAdaptor.valueOf(reflectStatus.getReflectStatus().value, SCReflectedStateExport.class),
				reflectStatus.getReasonNotReflectWorkRecord() == null ? null
						: EnumAdaptor.valueOf(reflectStatus.getReasonNotReflectWorkRecord().value,
								SCReasonNotReflectDailyExport.class),
				reflectStatus.getReasonNotReflectWorkSchedule() == null ? null
						: EnumAdaptor.valueOf(reflectStatus.getReasonNotReflectWorkSchedule().value,
								SCReasonNotReflectExport.class));
	}

	@AllArgsConstructor
	public class RequireImpl implements RecoverWorkScheduleBeforeAppReflect.Require {

		private final DailyRecordConverter dailyRecordConverter;
		
		private final ICorrectionAttendanceRule correctionAfterTimeChange;

		private final CalculateDailyRecordServiceCenterNew calculateDailyRecordServiceCenter;

		private final WorkScheduleRepository workScheduleRepository;

		private final ApplicationReflectHistoryRepo applicationReflectHistoryRepo;

		@Override
		public List<ApplicationReflectHistory> findAppReflectHistAfterMaxTime(String sid, GeneralDate baseDate,
				ScheduleRecordClassifi classification, boolean flgRemove, GeneralDateTime reflectionTime) {
			return applicationReflectHistoryRepo.findAppReflectHistAfterMaxTime(sid, baseDate, classification, flgRemove, reflectionTime);
		}

		@Override
		public DailyRecordToAttendanceItemConverter createDailyConverter() {
			return dailyRecordConverter.createDailyConverter();
		}

		@Override
		public List<ApplicationReflectHistory> findAppReflectHist(String sid, String appId, GeneralDate baseDate,
				ScheduleRecordClassifi classification, boolean flgRemove) {
			return applicationReflectHistoryRepo.findAppReflectHist(sid, appId, baseDate, classification, flgRemove);
		}

		@Override
		public List<ApplicationReflectHistory> findAppReflectHistDateCond(String sid, GeneralDate baseDate,
				ScheduleRecordClassifi classification, boolean flgRemove, GeneralDateTime reflectionTime) {
			return applicationReflectHistoryRepo.findAppReflectHistDateCond(sid, baseDate, classification, flgRemove, reflectionTime);
		}

		@Override
		public Optional<WorkSchedule> get(String employeeID, GeneralDate ymd) {
			return workScheduleRepository.get(employeeID, ymd);
		}

		@Override
		public List<IntegrationOfDaily> calculateForSchedule(ExecutionType type,
				List<IntegrationOfDaily> integrationOfDaily) {
			return calculateDailyRecordServiceCenter.calculatePassCompanySetting(CalculateOption.asDefault(),
					integrationOfDaily, type);
		}

		@Override
		public void insertSchedule(WorkSchedule workSchedule) {
			workScheduleRepository.delete(workSchedule.getEmployeeID(), workSchedule.getYmd());
			workScheduleRepository.insert(workSchedule);
		}

		@Override
		public void updateAppReflectHist(String sid, String appId, GeneralDate baseDate,
				ScheduleRecordClassifi classification, boolean flagRemove) {
			applicationReflectHistoryRepo.updateAppReflectHist(sid, appId, baseDate, classification, flagRemove);
		}

		@Override
		public IntegrationOfDaily correct(IntegrationOfDaily domainDaily, ChangeDailyAttendance changeAtt) {
			return correctionAfterTimeChange.process(domainDaily, changeAtt);
		}

	}
}
