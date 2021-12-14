package nts.uk.ctx.at.request.ac.record.agreement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.pub.monthly.agreement.export.AgreMaxAverageTimeMultiExport;
import nts.uk.ctx.at.record.pub.monthly.agreement.export.AgreementTimeBreakdownExport;
import nts.uk.ctx.at.record.pub.monthly.agreement.export.AgreementTimeExport;
import nts.uk.ctx.at.record.pub.monthly.agreement.export.AgreementTimeOfManagePeriodExport;
import nts.uk.ctx.at.record.pub.monthly.agreement.export.AgreementTimeOfMonthlyExport;
import nts.uk.ctx.at.record.pub.monthly.agreement.export.AgreementTimeYearExport;
import nts.uk.ctx.at.record.pub.monthlyprocess.agreement.GetAgreementTimePub;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreeTimeYearImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementTimeAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementTimeImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementTimeImport_Old;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeYear;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreMaxAverageTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreMaxAverageTimeMulti;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreMaxTimeStatusOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreTimeYearStatusOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeBreakdown;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfYear;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeStatusOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeYear;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.ScheRecAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.AgreementOneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.AgreementOneYearTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.OneYearErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.OneYearTime;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;

@Stateless
public class AgreementTimeAdapterImpl implements AgreementTimeAdapter {
	
	@Inject
	private GetAgreementTimePub getAgreementTimePub;
	
//	@Inject
//	private CheckAgreementTimeStatusPub checkAgreementTimeStatusPub;
	
//	@Inject
//	private GetAgreementPeriodPub getAgreementPeriodPub;
	
	@Override
	public List<AgreementTimeImport_Old> getAgreementTime(String companyId, List<String> employeeIds, YearMonth yearMonth,
			ClosureId closureId) {
		
		/** TODO: 36協定時間対応により、コメントアウトされた */
		return new ArrayList<>();
//		return getAgreementTimePub.get(companyId, employeeIds, yearMonth, closureId).stream()
//			.map(x -> new AgreementTimeImport(
//					x.getEmployeeId(), 
//					x.getConfirmed().map(y -> new AgreeTimeOfMonthExport(
//							y.getAgreementTime().v(), 
//							y.getLimitErrorTime().v(), 
//							y.getLimitAlarmTime().v(), 
//							y.getExceptionLimitErrorTime().map(z -> z.v()), 
//							y.getExceptionLimitAlarmTime().map(z -> z.v()), 
//							y.getStatus().value)), 
//					x.getAfterAppReflect().map(y -> new AgreeTimeOfMonthExport(
//							y.getAgreementTime().v(), 
//							y.getLimitErrorTime().v(), 
//							y.getLimitAlarmTime().v(), 
//							y.getExceptionLimitErrorTime().map(z -> z.v()), 
//							y.getExceptionLimitAlarmTime().map(z -> z.v()), 
//							y.getStatus().value)),
//					x.getConfirmedMax().map(y -> new AgreMaxTimeOfMonthExport(
//							y.getAgreementTime().v(), 
//							y.getMaxTime().v(), 
//							y.getStatus().value)),
//					x.getAfterAppReflectMax().map(y -> new AgreMaxTimeOfMonthExport(
//							y.getAgreementTime().v(), 
//							y.getMaxTime().v(), 
//							y.getStatus().value)),
//					x.getErrorMessage()))
//			.collect(Collectors.toList());
	}

	@Override
	public Optional<AgreeTimeYearImport> getYear(String companyId, String employeeId, YearMonthPeriod period,
			GeneralDate criteria) {
		
		/** TODO: 36協定時間対応により、コメントアウトされた */
		return Optional.empty();
//		return getAgreementTimePub.getYear(companyId, employeeId, period, criteria)
//				.map(x -> new AgreeTimeYearImport(x.getLimitTime().v(), x.getRecordTime().v(), x.getStatus().value));
	}

	@Override
	public AgreTimeYearStatusOfMonthly timeYear(AgreementTimeYear agreementTimeYear, Optional<AttendanceTimeYear> requestTimeOpt) {
		
		/** TODO: 36協定時間対応により、コメントアウトされた */
		return null;
//		return checkAgreementTimeStatusPub.timeYear(agreementTimeYear, requestTimeOpt);
	}

	/** TODO: 36協定時間対応により、コメントアウトされた */
//	@Override
//	public AgreMaxTimeStatusOfMonthly maxTime(AttendanceTimeMonth agreementTime, AgreementOneMonthTime maxTime,
//			Optional<AttendanceTimeMonth> requestTimeOpt) {
//		
//		return checkAgreementTimeStatusPub.maxTime(agreementTime, maxTime, requestTimeOpt);
//	}

	@Override
	public AgreMaxAverageTimeMulti maxAverageTimeMulti(String companyId, AgreMaxAverageTimeMulti sourceTime,
			Optional<AttendanceTime> requestTimeOpt, Optional<GeneralDate> requestDateOpt) {
		
		/** TODO: 36協定時間対応により、コメントアウトされた */
		return null;
//		return checkAgreementTimeStatusPub.maxAverageTimeMulti(companyId, sourceTime, requestTimeOpt, requestDateOpt);
	}

	@Override
	public Optional<AgreMaxAverageTimeMulti> getMaxAverageMulti(String companyId, String employeeId,
			YearMonth yearMonth, GeneralDate criteria) {
		
		/** TODO: 36協定時間対応により、コメントアウトされた */
		return Optional.empty();
//		return getAgreementTimePub.getMaxAverageMulti(companyId, employeeId, yearMonth, criteria);
	}

	@Override
	public AgreementTimeOfManagePeriod getAgreementTimeOfManagePeriod(
			String sid,
			YearMonth ym,
			List<IntegrationOfDaily> dailyRecord,
			GeneralDate baseDate,
			ScheRecAtr scheRecAtr) {
		AgreementTimeOfManagePeriodExport export = getAgreementTimePub.calcAgreementTime(sid, ym, dailyRecord, baseDate, scheRecAtr);
		AgreementTimeBreakdownExport agreementTimeBreakdownExport = export.getBreakdown();
		AgreementTimeBreakdown agreementTimeBreakdown = AgreementTimeBreakdown.of(
				new AttendanceTimeMonth(agreementTimeBreakdownExport.getOverTime()),
				new AttendanceTimeMonth(agreementTimeBreakdownExport.getTransferOverTime()),
				new AttendanceTimeMonth(agreementTimeBreakdownExport.getIllegalHolidayWorkTime()),
				new AttendanceTimeMonth(agreementTimeBreakdownExport.getIllegaltransferTime()),
				new AttendanceTimeMonth(agreementTimeBreakdownExport.getFlexLegalTime()),
				new AttendanceTimeMonth(agreementTimeBreakdownExport.getFlexIllegalTime()),
				new AttendanceTimeMonth(agreementTimeBreakdownExport.getWithinPrescribedPremiumTime()),
				new AttendanceTimeMonth(agreementTimeBreakdownExport.getWeeklyPremiumTime()),
				new AttendanceTimeMonth(agreementTimeBreakdownExport.getMonthlyPremiumTime()),
				new AttendanceTimeMonth(agreementTimeBreakdownExport.getTemporaryTime()),
				new AttendanceTimeMonth(agreementTimeBreakdownExport.getLegalHolidayWorkTime()),
				new AttendanceTimeMonth(agreementTimeBreakdownExport.getLegalTransferTime()));
				
		
		return AgreementTimeOfManagePeriod.builder()
					.agreementTime(convertAgreementTimeOfMonthly(export.getAgreementTime()))
					.sid(export.getSid())
					.status(EnumAdaptor.valueOf(export.getStatus(), AgreementTimeStatusOfMonthly.class))
					.agreementTimeBreakDown(agreementTimeBreakdown)
					.yearMonth(export.getYm())
					.legalMaxTime(convertAgreementTimeOfMonthly(export.getLegalMaxTime()))
					.build();
			
			
	}
	
	public AgreementTimeOfMonthly convertAgreementTimeOfMonthly(AgreementTimeOfMonthlyExport export) {
		OneMonthErrorAlarmTime oneMonthErrorAlarmTime = OneMonthErrorAlarmTime.of(
				new AgreementOneMonthTime(export.getThreshold().getErrorTime()),
				new AgreementOneMonthTime(export.getThreshold().getAlarmTime()));
		AgreementOneMonthTime upperLimit = new AgreementOneMonthTime(export.getThreshold().getUpperLimit());
		
		return AgreementTimeOfMonthly.of(
				new AttendanceTimeMonth(export.getAgreementTime()), 
				OneMonthTime.createWithCheck(oneMonthErrorAlarmTime, upperLimit));
	}

	/** TODO: 36協定時間対応により、コメントアウトされた */
//	@Override
//	public Optional<YearMonthPeriod> containsDate(String companyID, GeneralDate criteria) {
//		return getAgreementPeriodPub.containsDate(companyID, criteria, Optional.empty());
//	}

	/** TODO: 36協定時間対応により、コメントアウトされた */
	@Override
	public AgreementTimeImport getAverageAndYear(String companyId, String employeeId, YearMonth averageMonth,
			GeneralDate criteria, ScheRecAtr scheRecAtr) {
		AgreementTimeExport agreementTimeExport = getAgreementTimePub.getAverageAndYear(companyId, employeeId, averageMonth, criteria, scheRecAtr);
		AgreementTimeYear agreementTimeYear = new AgreementTimeYear();
		AgreMaxAverageTimeMulti agreMaxAverageTimeMulti = new AgreMaxAverageTimeMulti();
		if(agreementTimeExport.getAgreementTimeYear().isPresent()) {
			AgreementTimeYearExport agreementTimeYearExport = agreementTimeExport.getAgreementTimeYear().get();
			agreementTimeYear = AgreementTimeYear.of(
					AgreementTimeOfYear.of(
							new AgreementOneYearTime(agreementTimeYearExport.getLimitTime().getAgreementTime()),
							OneYearTime.createWithCheck(
									OneYearErrorAlarmTime.of(
											new AgreementOneYearTime(agreementTimeYearExport.getLimitTime().getThreshold().getErrorTime()),
											new AgreementOneYearTime(agreementTimeYearExport.getLimitTime().getThreshold().getAlarmTime())),
									new AgreementOneYearTime(agreementTimeYearExport.getLimitTime().getThreshold().getUpperLimit()))),
					AgreementTimeOfYear.of(
							new AgreementOneYearTime(agreementTimeYearExport.getRecordTime().getAgreementTime()),
							OneYearTime.createWithNoUpperLimit(
									OneYearErrorAlarmTime.of(
											new AgreementOneYearTime(agreementTimeYearExport.getRecordTime().getThreshold().getErrorTime()),
											new AgreementOneYearTime(agreementTimeYearExport.getRecordTime().getThreshold().getAlarmTime())))), 
					EnumAdaptor.valueOf(agreementTimeYearExport.getStatus(), AgreementTimeStatusOfMonthly.class));
		}
		if(agreementTimeExport.getAgreMaxAverageTimeMulti().isPresent()) {
			AgreMaxAverageTimeMultiExport agreMaxAverageTimeMultiExport = agreementTimeExport.getAgreMaxAverageTimeMulti().get();
			agreMaxAverageTimeMulti = AgreMaxAverageTimeMulti.of(
					OneMonthErrorAlarmTime.of(
							new AgreementOneMonthTime(agreMaxAverageTimeMultiExport.getErrorTime()),
							new AgreementOneMonthTime(agreMaxAverageTimeMultiExport.getAlarmTime())),
					agreMaxAverageTimeMultiExport.getAverageTimes().stream()
							.map(x -> AgreMaxAverageTime.of(
									x.getPeriod(),
									new AttendanceTimeYear(x.getTotalTime()),
									new AttendanceTimeMonth(x.getAverageTime()),
									EnumAdaptor.valueOf(x.getStatus(), AgreMaxTimeStatusOfMonthly.class)))
							.collect(Collectors.toList()));
		}
		return new AgreementTimeImport(agreementTimeYear, agreMaxAverageTimeMulti);
	}

}
