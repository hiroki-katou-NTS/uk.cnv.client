package nts.uk.ctx.at.function.ac.periodofspecialleave;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.primitive.PrimitiveValueBase;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.ComplileInPeriodOfSpecialLeaveAdapter;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.SpecialHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.SpecialVacationImported;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.SpecialVacationImportedKdr;
import nts.uk.ctx.at.record.dom.monthly.vacation.specialholiday.monthremaindata.export.SpecialHolidayRemainDataOutput;
import nts.uk.ctx.at.record.dom.monthly.vacation.specialholiday.monthremaindata.export.SpecialHolidayRemainDataSevice;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.ComplileInPeriodOfSpecialLeaveParam;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.InPeriodOfSpecialLeaveResultInfor;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.export.SpecialLeaveManagementService;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class ComplileInPeriodOfSpecialLeaveFinder implements ComplileInPeriodOfSpecialLeaveAdapter {

	@Inject
	private SpecialHolidayRemainDataSevice specialHolidayRemainDataSevice;

//	@Inject
//	private SpecialLeaveGrantRepository specialLeaveGrantRepo;
//
//	@Inject
//	private ShareEmploymentAdapter shareEmploymentAdapter;
//
//	@Inject
//	private EmpEmployeeAdapter empEmployeeAdapter;
//
//	@Inject
//	private GrantDateTblRepository grantDateTblRepo;
//
//	@Inject
//	private AnnLeaEmpBasicInfoRepository annLeaEmpBasicInfoRepo;
//
//	@Inject
//	private SpecialHolidayRepository specialHolidayRepo;
//
//	@Inject
//	private InterimSpecialHolidayMngRepository interimSpecialHolidayMngRepo;
//
//	@Inject
//	private SpecialLeaveBasicInfoRepository specialLeaveBasicInfoRepo;

	@Inject
	private RecordDomRequireService recordDomRequireService;

	@Override
	public SpecialVacationImported complileInPeriodOfSpecialLeave(String cid, String sid, DatePeriod complileDate,
			boolean mode, GeneralDate baseDate, int specialLeaveCode, boolean mngAtr) {
		// requestList273
		ComplileInPeriodOfSpecialLeaveParam param = new ComplileInPeriodOfSpecialLeaveParam(cid, sid,
				complileDate, mode, baseDate, specialLeaveCode, mngAtr,
				false, new ArrayList<>(),Optional.empty());
		InPeriodOfSpecialLeaveResultInfor specialLeave = SpecialLeaveManagementService
				.complileInPeriodOfSpecialLeave(
//						SpecialLeaveManagementService.createRequireM5(specialLeaveGrantRepo, shareEmploymentAdapter,
//								empEmployeeAdapter, grantDateTblRepo, annLeaEmpBasicInfoRepo, specialHolidayRepo,
//								interimSpecialHolidayMngRepo, interimRemainRepo, specialLeaveBasicInfoRepo),
						recordDomRequireService.createRequire(),
						new CacheCarrier(), param);
//				.getAggSpecialLeaveResult();
		if (specialLeave == null)
			return null;

		double use_before =  specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getUsedNumberInfo()
				.getUsedNumberBeforeGrant().getUseDays().map(x -> x.v()).orElse(0.0);

		double remain_before =  specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getRemainingNumberInfo()
				.getRemainingNumberBeforeGrant().getDayNumberOfRemain().v();

		double use_after = 0.0;
		double remain_after = 0.0;
		boolean existAfter = specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus()
				.getRemainingNumberInfo().getRemainingNumberAfterGrantOpt().isPresent();
		if ( existAfter ) {
//			remain_after = specialLeave.getAsOfPeriodEnd()
//				.getRemainingNumber().getSpecialLeaveWithMinus()
//				.getRemainingNumberInfo().getRemainingNumberAfterGrantOpt().get().getDayNumberOfRemain().v();
			use_after = specialLeave.getAsOfPeriodEnd()
					.getRemainingNumber().getSpecialLeaveWithMinus().getUsedNumberInfo()
					.getUsedNumberAfterGrantOpt().get().getUseDays().map(x -> x.v()).orElse(0.0);
			remain_after = specialLeave.getAsOfPeriodEnd()
					.getRemainingNumber().getSpecialLeaveWithMinus().getRemainingNumberInfo()
					.getRemainingNumberAfterGrantOpt().get().getDayNumberOfRemain().v();
		}

		return new SpecialVacationImported(
				// 要修正 jinno　NO273
				//specialLeave.getAsOfPeriodEnd().getRemainingNumber().getSpecialLeaveWithMinus().
				0.0,
				0.0,
				existAfter? use_after : use_before,
				existAfter? remain_after : remain_before,
				0.0, 0.0, 0.0, 0.0);
	}

	@Override
	public List<SpecialHolidayImported> getSpeHoliOfConfirmedMonthly(String sid, YearMonth startMonth,
			YearMonth endMonth) {
		// requestList263
		List<SpecialHolidayRemainDataOutput> lstSpeHoliOfConfirmedMonthly = specialHolidayRemainDataSevice
				.getSpeHoliOfConfirmedMonthly(sid, startMonth, endMonth);

		if (lstSpeHoliOfConfirmedMonthly == null)
			return null;
		List<SpecialHolidayImported> lstSpecialHoliday = new ArrayList<>();
		lstSpeHoliOfConfirmedMonthly.forEach(item -> {
			SpecialHolidayImported specialHoliday = new SpecialHolidayImported(item.getYm(), item.getUseDays(),
					item.getUseTimes(), item.getRemainDays(), item.getRemainTimes());
			lstSpecialHoliday.add(specialHoliday);
		});
		return lstSpecialHoliday;
	}

	@Override
	public List<SpecialHolidayImported> getSpeHoliOfConfirmedMonthly(String sid, YearMonth startMonth,
			YearMonth endMonth, List<Integer> listSpeCode) {

		// requestList263 with speCode
				List<SpecialHolidayRemainDataOutput> lstSpeHoliOfConfirmedMonthly = specialHolidayRemainDataSevice
						.getSpeHoliOfPeriodAndCodes(sid, startMonth, endMonth, listSpeCode);

				if (lstSpeHoliOfConfirmedMonthly == null)
					return null;
				List<SpecialHolidayImported> lstSpecialHoliday = new ArrayList<>();
				lstSpeHoliOfConfirmedMonthly.forEach(item -> {
					SpecialHolidayImported specialHoliday = new SpecialHolidayImported(item.getYm(), item.getUseDays(),
							item.getUseTimes(), item.getRemainDays(), item.getRemainTimes());
					lstSpecialHoliday.add(specialHoliday);
				});
				return lstSpecialHoliday;
	}
	/**
	 * @author hoatt
	 * Doi ung response KDR001
	 * RequestList263 ver2
	 * @param sid
	 * @param startMonth
	 * @param endMonth
	 * @return
	 */
	@Override
	public List<SpecialHolidayImported> getSpeHdOfConfMonVer2(String sid, YearMonth startMonth, YearMonth endMonth) {
//		List<SpecialHolidayRemainDataOutput> lstSpeHdOfConfMonthly = specialHolidayRemainDataSevice
//				.getSpeHdOfConfMonVer2(sid, startMonth, endMonth);
//		if (lstSpeHdOfConfMonthly == null)
//			return null;
		List<SpecialHolidayImported> lstSpecHd = new ArrayList<>();
//		lstSpeHdOfConfMonthly.forEach(item -> {
//			SpecialHolidayImported specialHoliday = new SpecialHolidayImported(item.getYm(), item.getUseDays(),
//					item.getUseTimes(), item.getRemainDays(), item.getRemainTimes());
//			lstSpecHd.add(specialHoliday);
//		});
		return lstSpecHd;
	}

	@Override
	public SpecialVacationImportedKdr get273New(String cid, String sid, DatePeriod complileDate,
												boolean mode, GeneralDate baseDate, int specialLeaveCode, boolean mngAtr) {
		// requestList273
		ComplileInPeriodOfSpecialLeaveParam param = new ComplileInPeriodOfSpecialLeaveParam(
				cid,
				sid,
				complileDate,
				mode,
				baseDate,
				specialLeaveCode,
				mngAtr,
				false,
				new ArrayList<>(),
				Optional.empty());
		InPeriodOfSpecialLeaveResultInfor specialLeave = SpecialLeaveManagementService
				.complileInPeriodOfSpecialLeave(
						recordDomRequireService.createRequire(),
						new CacheCarrier(), param);
		if (specialLeave == null)
			return null;
		Double grantDays = null;
		Integer grantTime = null;
		if(!specialLeave.getAsOfPeriodEnd().getGrantRemainingDataList().isEmpty()){
			 grantDays = specialLeave.getAsOfPeriodEnd().getGrantRemainingDataList().stream()
					.mapToDouble(e->e.getDetails().getGrantNumber().getDays().v()).sum();

			 grantTime = specialLeave.getAsOfPeriodEnd().getGrantRemainingDataList().stream()
					.mapToInt(e->e.getDetails().getGrantNumber().getMinutes().isPresent()?
							e.getDetails().getGrantNumber().getMinutes().get().v():0).sum();
		}
		Double usedDate =  specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getUsedNumberInfo()
				.getUsedNumber().getUseDays().map(PrimitiveValueBase::v).orElse(null);
		Double usedDateBf = specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getUsedNumberInfo()
				.getUsedNumberBeforeGrant().getUseDays().map(x -> x.v()).orElse(null);

		val usedDateAfOpt = specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getUsedNumberInfo()
				.getUsedNumberAfterGrantOpt();

		Double usedDateAf = usedDateAfOpt.isPresent()?
				(usedDateAfOpt.get().getUseDays().isPresent()?usedDateAfOpt.get().getUseDays().get().v():null):null;


		Double remainDate =  specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getRemainingNumberInfo()
				.getRemainingNumber().dayNumberOfRemain.v();

		Double remainDateBf =  specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getRemainingNumberInfo()
				.getRemainingNumberBeforeGrant().dayNumberOfRemain.v();

		val remainDateAfOpt =  specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getRemainingNumberInfo()
				.getRemainingNumberAfterGrantOpt();

		Double remainDateAf =  remainDateAfOpt.isPresent()?remainDateAfOpt
				.get().dayNumberOfRemain.v() : null;


		val usedHoursOpt =  specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getUsedNumberInfo()
				.getUsedNumber().getUseTimes();
		Integer usedHours =  usedHoursOpt.isPresent()?usedHoursOpt.get().getUseTimes().v():null;

		val usedHoursBfOpt = specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getUsedNumberInfo()
				.getUsedNumberBeforeGrant().getUseTimes();
		Integer usedHoursBf = usedHoursBfOpt.isPresent()? usedHoursBfOpt.get().getUseTimes().v() :null;


		val usedHoursAfOpt = specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getUsedNumberInfo()
				.getUsedNumberAfterGrantOpt();

		Integer usedHoursAf = usedHoursAfOpt.isPresent()?
				(usedHoursAfOpt.get().getUseTimes().isPresent()? (usedHoursAfOpt.get().getUseTimes().isPresent()?
						usedHoursAfOpt.get().getUseTimes().get().getUseTimes().v():null):null):null;


		val  remainHoursOpt =  specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getRemainingNumberInfo()
				.getRemainingNumber().timeOfRemain;
		Integer remainHours = remainHoursOpt.isPresent()?remainHoursOpt.get().v():null;

		val remainHoursBfOpt =  specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getRemainingNumberInfo()
				.getRemainingNumberBeforeGrant().timeOfRemain;
		Integer remainHoursBf = remainHoursBfOpt.isPresent()?remainHoursBfOpt.get().v():null;

		val remainHoursAfOpt =  specialLeave.getAsOfPeriodEnd()
				.getRemainingNumber().getSpecialLeaveWithMinus().getRemainingNumberInfo()
				.getRemainingNumberAfterGrantOpt();

		Integer remainHoursAf =  remainHoursAfOpt.isPresent()? (remainHoursAfOpt
				.get().getTimeOfRemain().isPresent()?remainHoursAfOpt
				.get().getTimeOfRemain().get().v():null) : null;

		return new SpecialVacationImportedKdr( grantDays,grantTime,usedDate, usedDateBf,usedDateAf,remainDate
				,remainDateBf, remainDateAf, usedHours, usedHoursBf, usedHoursAf,remainHours,remainHoursBf,remainHoursAf);
	}
}
