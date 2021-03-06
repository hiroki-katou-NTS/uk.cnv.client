package nts.uk.ctx.at.record.app.find.monthly.nursingleave;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.monthly.nursingleave.dto.AggrResultOfChildCareNurseDto;
import nts.uk.ctx.at.record.app.find.monthly.nursingleave.dto.ChildCareNurseAggrPeriodDaysInfoDto;
import nts.uk.ctx.at.record.app.find.monthly.nursingleave.dto.ChildCareNurseAggrPeriodInfoDto;
import nts.uk.ctx.at.record.app.find.monthly.nursingleave.dto.ChildCareNurseErrorsDto;
import nts.uk.ctx.at.record.app.find.monthly.nursingleave.dto.ChildCareNurseRemainingNumberDto;
import nts.uk.ctx.at.record.app.find.monthly.nursingleave.dto.ChildCareNurseStartdateDaysInfoDto;
import nts.uk.ctx.at.record.app.find.monthly.nursingleave.dto.ChildCareNurseStartdateInfoDto;
import nts.uk.ctx.at.record.app.find.monthly.nursingleave.dto.ChildCareNurseUsedNumberDto;
import nts.uk.ctx.at.record.app.find.monthly.nursingleave.dto.KDL051ProcessDto;
import nts.uk.ctx.at.record.app.find.monthly.nursingleave.dto.TempChildCareManagementDto;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.care.GetRemainingNumberCareService;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare.AggrResultOfChildCareNurse;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare.ChildCareNurseRequireImplFactory;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare.GetRemainingNumberChildCareService;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.care.interimdata.TempCareManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.care.interimdata.TempCareManagementRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.childcare.interimdata.TempChildCareManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.childcare.interimdata.TempChildCareManagementRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class ChildCareNusingLeaveFinder {

	@Inject
	private RecordDomRequireService requireService;

	@Inject
	private ChildCareNurseRequireImplFactory childCareNurseRequireImplFactory;

	@Inject
	private TempChildCareManagementRepository childCareManaRepo;

	@Inject
	private TempCareManagementRepository tempCareManagementRepo;

	/**
	 * UKDesign.UniversalK.??????.KDL_???????????????.KDL051_?????????????????????????????????.??????????????????.?????????????????????.?????????????????????
	 * @param eId
	 * @return
	 */
	public KDL051ProcessDto changeEmployee(String eId) {

		// val require = requireService.createRequire();
		val require = childCareNurseRequireImplFactory.createRequireImpl();

		val cacheCarrier = new CacheCarrier();

		//	?????????????????????????????????????????????????????????????????????????????????????????????
		val closureStartOpt = GetClosureStartForEmployee.algorithm(require, cacheCarrier, eId);
		DatePeriod datePeriod = new DatePeriod(closureStartOpt.get(), closureStartOpt.get().addYears(1).addDays(-1));

		// ???????????????????????????????????????????????????????????????????????????????????????
		AggrResultOfChildCareNurse resultOfChildCareNurse = GetRemainingNumberChildCareService.getChildCareRemNumWithinPeriod(
				AppContexts.user().companyId(),
				eId,
				datePeriod,
				InterimRemainMngMode.OTHER,
				GeneralDate.today(),
				Optional.empty(),
				new ArrayList<>(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				cacheCarrier,
				require);

		// convert to Dto
		List<ChildCareNurseErrorsDto> listEr = resultOfChildCareNurse.getChildCareNurseErrors().stream().map(item -> ChildCareNurseErrorsDto.builder()
				.usedNumber(ChildCareNurseUsedNumberDto.builder()
								.usedDay(item.getUsedNumber().getUsedDay().v())
								.usedTimes(item.getUsedNumber().getUsedTimes().map(x ->x.v()).orElse(0))
								.build())
				.limitDays(item.getLimitDays().v())
				.ymd(item.getYmd().toString())
				.build()).collect(Collectors.toList());

		ChildCareNurseUsedNumberDto usedNumber = ChildCareNurseUsedNumberDto.builder()
				.usedDay(resultOfChildCareNurse.getAsOfPeriodEnd().getUsedDay().v())
				.usedTimes(resultOfChildCareNurse.getAsOfPeriodEnd().getUsedTimes().map(x -> x.v()).orElse(0))
				.build();
		// this year
		ChildCareNurseUsedNumberDto usedDays = ChildCareNurseUsedNumberDto.builder()
				.usedDay(resultOfChildCareNurse.getStartdateDays().getThisYear().getUsedDays().getUsedDay().v())
				.usedTimes(resultOfChildCareNurse.getStartdateDays().getThisYear().getUsedDays().getUsedTimes().map(x -> x.v()).orElse(0))
				.build();
		ChildCareNurseRemainingNumberDto remainingNumber = ChildCareNurseRemainingNumberDto.builder()
				.usedDays(resultOfChildCareNurse.getStartdateDays().getThisYear().getRemainingNumber().getRemainDay().v())
				.usedTime(resultOfChildCareNurse.getStartdateDays().getThisYear().getRemainingNumber().getRemainTimes().map(x -> x.v()).orElse(0))
				.build();
		ChildCareNurseStartdateInfoDto thisYear = ChildCareNurseStartdateInfoDto.builder()
				.usedDays(usedDays)
				.remainingNumber(remainingNumber)
				.limitDays(resultOfChildCareNurse.getStartdateDays().getThisYear().getLimitDays().v())
				.build();
		ChildCareNurseStartdateDaysInfoDto startDateDays = ChildCareNurseStartdateDaysInfoDto.builder()
				.thisYear(thisYear)
				.build();
		// this year
		ChildCareNurseUsedNumberDto aggrPeriodUsedNumberThisYear = ChildCareNurseUsedNumberDto.builder()
				.usedDay(resultOfChildCareNurse.getAggrperiodinfo().getThisYear().getUsedNumber().getUsedDay().v())
				.usedTimes(resultOfChildCareNurse.getAggrperiodinfo().getThisYear().getUsedNumber().getUsedTimes().map(x -> x.v()).orElse(0))
				.build();
		ChildCareNurseAggrPeriodInfoDto aggrPeriodInfoThisYear = ChildCareNurseAggrPeriodInfoDto.builder()
				.usedCount(resultOfChildCareNurse.getAggrperiodinfo().getThisYear().getUsedTimes().v())
				.usedDays(resultOfChildCareNurse.getAggrperiodinfo().getThisYear().getUsedDays().v())
				.aggrPeriodUsedNumber(aggrPeriodUsedNumberThisYear)
				.build();
		ChildCareNurseAggrPeriodDaysInfoDto aggrPeriodDaysInfo = ChildCareNurseAggrPeriodDaysInfoDto.builder()
				.thisYear(aggrPeriodInfoThisYear)
				.build();

		AggrResultOfChildCareNurseDto dataRes = AggrResultOfChildCareNurseDto.builder()
				.childCareNurseErrors(listEr)
				.asOfPeriodEnd(usedNumber)
				.startdateDays(startDateDays)
				.startDateAtr(resultOfChildCareNurse.isStartDateAtr())
				.aggrperiodinfo(aggrPeriodDaysInfo)
				.build();

		//	?????????????????????[NO.685]????????????????????????????????????????????????????????????????????????
		List<TempChildCareManagement>  lstChildCareMana = this.childCareManaRepo.findByPeriodOrderByYmd(eId, datePeriod);
		List<TempChildCareManagementDto> lstChildCareManaResult = lstChildCareMana.stream().map(item -> TempChildCareManagementDto.builder()
					.usedDay(item.getUsedNumber().getUsedDay().v())
					.usedTimes(item.getUsedNumber().getUsedTimes().map(x->x.v()).orElse(0))
					.creatorAtr(item.getCreatorAtr().name)
					.ymd(item.getYmd().toString())
					.build())
					.collect(Collectors.toList());
		KDL051ProcessDto result = KDL051ProcessDto.builder()
				.aggrResultOfChildCareNurse(dataRes)
				.lstChildCareMana(lstChildCareManaResult)
				.build();
		return result;
	}

	/**
	 * UKDesign.UniversalK.??????.KDL_???????????????.KDL052_???????????????????????????.??????????????????.?????????????????????.?????????????????????
	 * @param eId
	 * @return
	 */
	public KDL051ProcessDto changeEmployeeKDL052(String eId) {
		//val require = requireService.createRequire();
		val require = childCareNurseRequireImplFactory.createRequireImpl();
		val cacheCarrier = new CacheCarrier();

		//	?????????????????????????????????????????????????????????????????????????????????????????????
		val closureStartOpt = GetClosureStartForEmployee.algorithm(require, cacheCarrier, eId);
		DatePeriod datePeriod = new DatePeriod(closureStartOpt.get(), closureStartOpt.get().addYears(1).addDays(-1));

		// ?????????????????????????????????????????????????????????????????????????????????
		AggrResultOfChildCareNurse resultOfChildCareNurse = GetRemainingNumberCareService.getCareRemNumWithinPeriod(
				AppContexts.user().companyId(),
				eId, datePeriod, InterimRemainMngMode.OTHER, GeneralDate.today(),
				Optional.empty(), new ArrayList<>(),
				Optional.empty(), Optional.empty(), Optional.empty(),
				cacheCarrier,
				require);

		// convert to Dto
		List<ChildCareNurseErrorsDto> listEr = resultOfChildCareNurse.getChildCareNurseErrors().stream().map(item -> ChildCareNurseErrorsDto.builder()
				.usedNumber(ChildCareNurseUsedNumberDto.builder()
								.usedDay(item.getUsedNumber().getUsedDay().v())
								.usedTimes(item.getUsedNumber().getUsedTimes().map(x ->x.v()).orElse(0))
								.build())
				.limitDays(item.getLimitDays().v())
				.ymd(item.getYmd().toString())
				.build()).collect(Collectors.toList());
		ChildCareNurseUsedNumberDto usedNumber = ChildCareNurseUsedNumberDto.builder()
				.usedDay(resultOfChildCareNurse.getAsOfPeriodEnd().getUsedDay().v())
				.usedTimes(resultOfChildCareNurse.getAsOfPeriodEnd().getUsedTimes().map(x -> x.v()).orElse(0))
				.build();
		// this year
		ChildCareNurseUsedNumberDto usedDays = ChildCareNurseUsedNumberDto.builder()
				.usedDay(resultOfChildCareNurse.getStartdateDays().getThisYear().getUsedDays().getUsedDay().v())
				.usedTimes(resultOfChildCareNurse.getStartdateDays().getThisYear().getUsedDays().getUsedTimes().map(x -> x.v()).orElse(0))
				.build();
		ChildCareNurseRemainingNumberDto remainingNumber = ChildCareNurseRemainingNumberDto.builder()
				.usedDays(resultOfChildCareNurse.getStartdateDays().getThisYear().getRemainingNumber().getRemainDay().v())
				.usedTime(resultOfChildCareNurse.getStartdateDays().getThisYear().getRemainingNumber().getRemainTimes().map(x -> x.v()).orElse(0))
				.build();
		ChildCareNurseStartdateInfoDto thisYear = ChildCareNurseStartdateInfoDto.builder()
				.usedDays(usedDays)
				.remainingNumber(remainingNumber)
				.limitDays(resultOfChildCareNurse.getStartdateDays().getThisYear().getLimitDays().v())
				.build();
		ChildCareNurseStartdateDaysInfoDto startDateDays = ChildCareNurseStartdateDaysInfoDto.builder()
				.thisYear(thisYear)
				.build();

		// this year
		ChildCareNurseUsedNumberDto aggrPeriodUsedNumberThisYear = ChildCareNurseUsedNumberDto.builder()
				.usedDay(resultOfChildCareNurse.getAggrperiodinfo().getThisYear().getUsedNumber().getUsedDay().v())
				.usedTimes(resultOfChildCareNurse.getAggrperiodinfo().getThisYear().getUsedNumber().getUsedTimes().map(x -> x.v()).orElse(0))
				.build();
		ChildCareNurseAggrPeriodInfoDto aggrPeriodInfoThisYear = ChildCareNurseAggrPeriodInfoDto.builder()
				.usedCount(resultOfChildCareNurse.getAggrperiodinfo().getThisYear().getUsedTimes().v())
				.usedDays(resultOfChildCareNurse.getAggrperiodinfo().getThisYear().getUsedDays().v())
				.aggrPeriodUsedNumber(aggrPeriodUsedNumberThisYear)
				.build();
		ChildCareNurseAggrPeriodDaysInfoDto aggrPeriodDaysInfo = ChildCareNurseAggrPeriodDaysInfoDto.builder()
				.thisYear(aggrPeriodInfoThisYear)
				.build();

		AggrResultOfChildCareNurseDto dataRes = AggrResultOfChildCareNurseDto.builder()
				.childCareNurseErrors(listEr)
				.asOfPeriodEnd(usedNumber)
				.startdateDays(startDateDays)
				.startDateAtr(resultOfChildCareNurse.isStartDateAtr())
				.aggrperiodinfo(aggrPeriodDaysInfo)
				.build();

		//	?????????????????????[NO.685]????????????????????????????????????????????????????????????????????????
		List<TempCareManagement>  lstChildCareMana = this.tempCareManagementRepo.findByPeriodOrderByYmd(eId, datePeriod);
		List<TempChildCareManagementDto> lstChildCareManaResult = lstChildCareMana.stream().map(item -> TempChildCareManagementDto.builder()
					.usedDay(item.getUsedNumber().getUsedDay().v())
					.usedTimes(item.getUsedNumber().getUsedTimes().map(x->x.v()).orElse(0))
					.creatorAtr(item.getCreatorAtr().name)
					.ymd(item.getYmd().toString())
					.build())
					.collect(Collectors.toList());
		KDL051ProcessDto result = KDL051ProcessDto.builder()
				.aggrResultOfChildCareNurse(dataRes)
				.lstChildCareMana(lstChildCareManaResult)
				.build();
		return result;
	}
}
