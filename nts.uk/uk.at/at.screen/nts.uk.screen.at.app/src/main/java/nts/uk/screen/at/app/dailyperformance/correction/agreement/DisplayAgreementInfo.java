package nts.uk.screen.at.app.dailyperformance.correction.agreement;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.standardtime.AgreementDomainService;
import nts.uk.ctx.at.record.dom.standardtime.repository.AgreementOperationSettingRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.DaiPerformanceFun;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementExcessInfoImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.standardtime.AgreementMonthSettingAdapter;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriodRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeStatusOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.setting.AgreementOperationSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting.BasicAgreementSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting.BasicAgreementSettingForCalc;
import nts.uk.screen.at.app.dailyperformance.correction.finddata.IFindData;

/**
 * @author thanhnx
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DisplayAgreementInfo {
	
	private static String ERROR = "state-error text-error";
	
	private static String ALARM = "state-alarm text-alarm";
	
	private static String EXCEPTION = "state-exception";
	
	private static String EX_SPECIAL = "state-ex-special text-ex-special";
	
	private static final String FORMAT_HH_MM = "%d:%02d";
	

	@Inject
	private IFindData findata;
	
	@Inject
	private AgreementTimeOfManagePeriodRepository agreementTimeOfManagePeriodRepository;
	
	@Inject
	private AgreementOperationSettingRepository agreementOperationSettingRepository;
	
	@Inject
	private AgreementMonthSettingAdapter agreementMonthSettingAdapter;
	
	@Inject
	private RecordDomRequireService requireService;
		
	//36協定情報を表示する
	public AgreementInfomationDto displayAgreementInfo(String companyId, String employeeId, int year, int month){
		
		//取得しているドメインモデル「日別実績の修正の機能．36協定情報を表示する」をチェックする
		Optional<DaiPerformanceFun> funOpt = findata.getDailyPerformFun(companyId);
		if(!funOpt.isPresent() || funOpt.get().getDisp36Atr() == 0) return AgreementInfomationDto.builder().showAgreement(false).build();
		
		AgreementInfomationDto result = new AgreementInfomationDto();
		
		result.setShowAgreement(true);
		Optional<AgreementOperationSetting> agreeOperationOpt = agreementOperationSettingRepository.find(companyId);
		//年月を指定して管理期間の36協定時間を取得する
		//List<AgreementTimeOfManagePeriod> lstAgree = getCoordinatedHours(companyId, Arrays.asList(employeeId), YearMonth.of(year, month), agreeOperationOpt);
		//Optional<AgreementTimeOfManagePeriod> agreeTimeOpt = lstAgree.isEmpty() ? Optional.empty() : Optional.of(lstAgree.get(0));
		Optional<AgreementTimeOfManagePeriod> agreeTimeOpt = agreementTimeOfManagePeriodRepository.find(employeeId, YearMonth.of(year, month));
		
		if (agreeOperationOpt.isPresent()) {
			DatePeriod datePeriod = agreeOperationOpt.get().getAggregatePeriodByYearMonth(YearMonth.of(year, month));
			BasicAgreementSettingForCalc basicAgreementSettingForCalc= AgreementDomainService.getBasicSet(requireService.createRequire(), 
																				companyId, employeeId, datePeriod.end(), YearMonth.of(year, month));
			// 年度指定して36協定基本設定を取得する
			BasicAgreementSetting  basicAgreementSetting= basicAgreementSettingForCalc.getBasicSetting();
			result.setMaxNumber(Integer.toString(basicAgreementSetting.getOverMaxTimes().value));
		}
		
		
		if (agreeTimeOpt.isPresent()) {
			/** TODO: 36協定時間対応により、コメントアウトされた */
			result.setCssAgree(convertStateCell(agreeTimeOpt.get().getStatus().value));
			result.setAgreementTime36(agreeTimeOpt.get().getAgreementTime().getAgreementTime() != null
							? convertTime(agreeTimeOpt.get().getAgreementTime().getAgreementTime().v())
							: "0:00");
			
			
			// [No.605]年月を指定して年間超過回数の取得
			AgreementExcessInfoImport agreementExcessInfoImport = agreementMonthSettingAdapter.getDataRq605(employeeId, YearMonth.of(year, month));
			result.setExcessFrequency(Integer.toString(agreementExcessInfoImport.getExcessTimes()));
			
			Integer valueMaxTime = agreeTimeOpt.get().getLegalMaxTime() == null ? 0 : agreeTimeOpt.get().getLegalMaxTime().getAgreementTime().v();							
									
			result.setMaxTime(convertTime(valueMaxTime));
			agreeTimeOpt.get().getBreakdown().getOverTime().v();
			
			if (Integer.parseInt(result.getExcessFrequency()) > Integer.parseInt(result.getMaxNumber())) {
				result.setCssFrequency(ERROR);
			}else if(Integer.parseInt(result.getExcessFrequency()) == Integer.parseInt(result.getMaxNumber())) {
				result.setCssFrequency(ALARM);
			}
		}
		
		/** TODO: 36協定時間対応により、コメントアウトされた */
//		result.setMaxNumber(agreeOperationOpt.isPresent()
//				? String.valueOf(agreeOperationOpt.get().getStartingMonth().value)
//				: "0");
//		Optional<AgreementExcessInfo> agreeExcessOpt = agreeTimeOpt.isPresent()
//				?  agreeTimeOpt.get().getNumberOverrunByYearMonth(employeeId,  YearMonth.of(year, month),
//						agreeOperationOpt)
//				: Optional.empty();
//		result.setExcessFrequency(String.valueOf(agreeTimeOpt.isPresent() ? agreeTimeOpt.get().getStatus().value : 0 ));
		

		return result;
	}
	
	public List<AgreementTimeOfManagePeriod> getCoordinatedHours(String companyId, List<String> employeeIds,
			YearMonth yearMonth, Optional<AgreementOperationSetting> agreementOperaSetOpt) {
		Optional<AgreementOperationSetting> agreeOperationOpt = agreementOperaSetOpt.isPresent() ? agreementOperaSetOpt
				: agreementOperationSettingRepository.find(companyId);
		if (!agreeOperationOpt.isPresent())
			return Collections.emptyList();
		YearMonth yearMonthChange = agreeOperationOpt.get().getYearMonthOfAgreementPeriod(yearMonth);
		List<AgreementTimeOfManagePeriod> agreeTimes = agreementTimeOfManagePeriodRepository
				.findByEmployees(employeeIds, yearMonthChange);
		return agreeTimes;
	}
	
	private String convertStateCell(int value) {
		if (value == AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR_SP.value
				|| value == AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ALARM_SP.value) {
			return EXCEPTION;
		} else if (value == AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR.value
				|| value == AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ERROR.value) {
			// text error
			return ERROR;
		} else if (value == AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ALARM.value
				|| value == AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ALARM.value) {
			// text alarm
			return ALARM;
		} else if(value == AgreementTimeStatusOfMonthly.EXCESS_BG_GRAY.value) {
			return EX_SPECIAL;
		} else {
			return "";
		}
	}
	
	private String convertTime(Integer minute){
		if(minute == null) minute = 0;
		int hours = minute / 60;
		int minutes = Math.abs(minute) % 60;
		return (minute < 0 && hours == 0) ?  "-"+String.format(FORMAT_HH_MM, hours, minutes) : String.format(FORMAT_HH_MM, hours, minutes);
	}
}
