package nts.uk.ctx.at.record.pubimp.cancelapplication;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.adapter.request.application.state.RCReasonNotReflect;
import nts.uk.ctx.at.record.dom.adapter.request.application.state.RCReasonNotReflectDaily;
import nts.uk.ctx.at.record.dom.adapter.request.application.state.RCReflectStatusResult;
import nts.uk.ctx.at.record.dom.adapter.request.application.state.RCReflectedState;
import nts.uk.ctx.at.record.dom.applicationcancel.removeappreflect.RCRecoverAppReflectOutput;
import nts.uk.ctx.at.record.dom.applicationcancel.removeappreflect.RecoverWorkRecordBeforeAppReflect;
import nts.uk.ctx.at.record.dom.daily.DailyRecordAdUpService;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.CalculateDailyRecordServiceCenter;
import nts.uk.ctx.at.record.pub.appreflect.export.RCReasonNotReflectDailyExport;
import nts.uk.ctx.at.record.pub.appreflect.export.RCReasonNotReflectExport;
import nts.uk.ctx.at.record.pub.appreflect.export.RCReflectStatusResultExport;
import nts.uk.ctx.at.record.pub.appreflect.export.RCReflectedStateExport;
import nts.uk.ctx.at.record.pub.cancelapplication.RCRecoverAppReflectOutputExport;
import nts.uk.ctx.at.record.pub.cancelapplication.RecoverWorkRecordBeforeAppReflectPub;
import nts.uk.ctx.at.shared.dom.dailyattdcal.converter.DailyRecordShareFinder;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistory;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistoryRepo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ICorrectionAttendanceRule;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateOption;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class RecoverWorkRecordBeforeAppReflectPubImpl implements RecoverWorkRecordBeforeAppReflectPub {

	@Inject
	private DailyRecordConverter dailyRecordConverter;

	@Inject
	private DailyRecordShareFinder dailyRecordShareFinder;

	@Inject
	private ICorrectionAttendanceRule correctionAfterTimeChange;

	@Inject
	private CalculateDailyRecordServiceCenter calculateDailyRecordServiceCenter;

	@Inject
	private DailyRecordAdUpService dailyRecordAdUpService;

	@Inject
	private ApplicationReflectHistoryRepo applicationReflectHistoryRepo;

	@Override
	public RCRecoverAppReflectOutputExport process(ApplicationShare application, GeneralDate date,
			RCReflectStatusResultExport reflectStatus, NotUseAtr dbRegisterClassfi) {
		RequireImpl requireImpl = new RequireImpl(dailyRecordConverter,
				dailyRecordShareFinder, correctionAfterTimeChange, calculateDailyRecordServiceCenter,
				dailyRecordAdUpService, applicationReflectHistoryRepo);
		RCRecoverAppReflectOutput output = RecoverWorkRecordBeforeAppReflect.process(requireImpl, application, date,
				convertToShare(reflectStatus), dbRegisterClassfi);
		return new RCRecoverAppReflectOutputExport(convertToExport(output.getReflectStatus()), output.getWorkRecord(),
				output.getAtomTask());
	}

	private RCReflectStatusResult convertToShare(RCReflectStatusResultExport reflectStatus) {

		return new RCReflectStatusResult(
				EnumAdaptor.valueOf(reflectStatus.getReflectStatus().value, RCReflectedState.class),
				reflectStatus.getReasonNotReflectWorkRecord() == null ? null
						: EnumAdaptor.valueOf(reflectStatus.getReasonNotReflectWorkRecord().value,
								RCReasonNotReflectDaily.class),
				reflectStatus.getReasonNotReflectWorkSchedule() == null ? null
						: EnumAdaptor.valueOf(reflectStatus.getReasonNotReflectWorkSchedule().value,
								RCReasonNotReflect.class));
	}

	private RCReflectStatusResultExport convertToExport(RCReflectStatusResult reflectStatus) {

		return new RCReflectStatusResultExport(
				EnumAdaptor.valueOf(reflectStatus.getReflectStatus().value, RCReflectedStateExport.class),
				reflectStatus.getReasonNotReflectWorkRecord() == null ? null
						: EnumAdaptor.valueOf(reflectStatus.getReasonNotReflectWorkRecord().value,
								RCReasonNotReflectDailyExport.class),
				reflectStatus.getReasonNotReflectWorkSchedule() == null ? null
						: EnumAdaptor.valueOf(reflectStatus.getReasonNotReflectWorkSchedule().value,
								RCReasonNotReflectExport.class));
	}

	@AllArgsConstructor
	public class RequireImpl implements RecoverWorkRecordBeforeAppReflect.Require {

		private final DailyRecordConverter dailyRecordConverter;

		private final DailyRecordShareFinder dailyRecordShareFinder;

		private final ICorrectionAttendanceRule correctionAfterTimeChange;

		private final CalculateDailyRecordServiceCenter calculateDailyRecordServiceCenter;

		private final DailyRecordAdUpService dailyRecordAdUpService;
		
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
		public Optional<IntegrationOfDaily> findDaily(String employeeId, GeneralDate date) {
			return dailyRecordShareFinder.find(employeeId, date);
		}

		@Override
		public IntegrationOfDaily correct(IntegrationOfDaily domainDaily, ChangeDailyAttendance changeAtt) {
			return correctionAfterTimeChange.process(domainDaily, changeAtt);
		}

		@Override
		public List<IntegrationOfDaily> calculateForRecord(List<IntegrationOfDaily> integrationOfDaily,
				ExecutionType reCalcAtr) {
			return calculateDailyRecordServiceCenter.calculatePassCompanySetting(CalculateOption.asDefault(),
					integrationOfDaily, Optional.empty(), reCalcAtr);
		}

		@Override
		public void addAllDomain(IntegrationOfDaily domain) {
			dailyRecordAdUpService.addAllDomain(domain);
		}

		@Override
		public void updateAppReflectHist(String sid, String appId, GeneralDate baseDate,
				ScheduleRecordClassifi classification, boolean flagRemove) {
			applicationReflectHistoryRepo.updateAppReflectHist(sid, appId, baseDate, classification, flagRemove);
		}


	}
}
