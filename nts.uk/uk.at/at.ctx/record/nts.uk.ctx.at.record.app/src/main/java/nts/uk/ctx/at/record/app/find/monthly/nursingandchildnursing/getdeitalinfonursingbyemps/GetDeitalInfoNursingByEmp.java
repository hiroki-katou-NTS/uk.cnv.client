package nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.getdeitalinfonursingbyemps;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.acquirenursingandchildnursing.AcquireNursingAndChildNursing;
import nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.acquirenursingandchildnursing.AcquireNursingAndChildNursingDto;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.GetHolidayDetailByPeriod;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.care.GetRemainingNumberChildCare;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.care.GetRemainingNumberNursing;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.care.GetUsageDetailCareService;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare.AggrResultOfChildCareNurse;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare.ChildCareNurseRequireImpl;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare.ChildCareNurseRequireImplFactory;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare.GetUsageDetailChildCareService;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyInterimRemainMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.ReferenceAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.care.interimdata.TempCareManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.childcare.interimdata.TempChildCareManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.childcare.interimdata.TempChildCareNurseManagement;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.ChildCareNurseUpperLimitPeriod;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingCategory;
import nts.uk.shr.com.i18n.TextResource;

/**
 * ??????????????????????????????????????????????????????
 * UKDesign.UniversalK.??????.KDL_???????????????.?????????????????????????????????.?????????????????????.??????????????????.??????????????????????????????????????????????????????
 * 
 * @author tutk
 *
 */
@Stateless
public class GetDeitalInfoNursingByEmp {

	@Inject
	private AcquireNursingAndChildNursing acquireNursingAndChildNursing;

	@Inject
	private GetRemainingNumberChildCare getRemainingNumberChildCare;

	@Inject
	private ChildCareNurseRequireImplFactory childCareNurseRequireImplFactory;

	@Inject
	private GetRemainingNumberNursing getRemainingNumberNursing; // 726
	
	@Inject
	private RecordDomRequireService requireService;

	/**
	 * @param companyId       ??????ID
	 * @param employeeId      ??????ID
	 * @param nursingCategory ??????????????????
	 */
	public NursingAndChildNursingRemainDto get(String companyId, String employeeId, NursingCategory nursingCategory) {

		String KDL051_33 = TextResource.localize("KDL051_33");
		String KDL051_31 = TextResource.localize("KDL051_31");
		String KDL051_32 = TextResource.localize("KDL051_32");

		NursingAndChildNursingRemainDto dataResult = new NursingAndChildNursingRemainDto();

		// ????????????????????????????????????????????????
		AcquireNursingAndChildNursingDto acquireNursingAndChildNursingDto = acquireNursingAndChildNursing.get(companyId,
				employeeId, nursingCategory);

		// ???????????????????????????????????????
		if (acquireNursingAndChildNursingDto.isManagementSection()) {

			// ???????????????????????????
			DatePeriod period = acquireNursingAndChildNursingDto.getNursingLeaveSetting().get()
					.calcManagementPeriod(GeneralDate.today());
			ChildCareNurseRequireImpl require = childCareNurseRequireImplFactory.createRequireImpl();
			// ????????????????????????????????????
			ChildCareNurseUpperLimitPeriod childCareNurseUpperLimitPeriod = acquireNursingAndChildNursingDto
					.getNursingCareLeaveRemainingInfo().get()
					.childCareNurseUpperLimitPeriod(companyId, employeeId, period, GeneralDate.today(), require).get(0);

			List<TempChildCareNurseManagement> listTempChildCareNurseManagement = new ArrayList<>();

			AggrResultOfChildCareNurse aggrResultOfChildCareNurse = null;
			if (nursingCategory == NursingCategory.ChildNursing) {// ????????????

				// DomainService????????????????????????????????????????????????????????????????????????
				aggrResultOfChildCareNurse = getRemainingNumberChildCare.getRemainingNumberChildCare(companyId,
						employeeId, GeneralDate.today());
				
				GetUsageDetailChildCareService.Require reuqireGetUsageDetailChildCareService = new GetUsageDetailChildCareServiceImpl(requireService);
				// ???DS_?????????????????????????????????????????????????????????????????????
				List<TempChildCareManagement> listTempChildCareManagement = GetUsageDetailChildCareService
						.getUsageDetailCareService(companyId, employeeId, period, ReferenceAtr.APP_AND_SCHE,
								reuqireGetUsageDetailChildCareService);
				listTempChildCareNurseManagement = listTempChildCareManagement.stream().map(c-> (TempChildCareNurseManagement) c).collect(Collectors.toList());

			} else if (nursingCategory == NursingCategory.Nursing) {// ??????

				// DomainService??????????????????????????????????????????????????????????????????
				aggrResultOfChildCareNurse = getRemainingNumberNursing.getRemainingNumberNursing(companyId, employeeId,
						GeneralDate.today());

				GetUsageDetailCareService.Require requireGetUsageDetailCareService = new GetUsageDetailCareServiceImpl(requireService);
				// DomainService???DS_????????????????????????????????????????????????
				List<TempCareManagement> listTempCareManagement = GetUsageDetailCareService.getUsageDetailCareService(
						companyId, employeeId, period, ReferenceAtr.APP_AND_SCHE, requireGetUsageDetailCareService);
				listTempChildCareNurseManagement = listTempCareManagement.stream().map(c-> (TempChildCareNurseManagement) c).collect(Collectors.toList());
				
			}
			

			// List???????????????????????????
			List<DigestionDetails> listDigestionDetails = new ArrayList<>();
			for (TempChildCareNurseManagement tempChildCareNurseManagement : listTempChildCareNurseManagement) {
				
				
				// ???????????????????????????????????????????????????????????????
				if (tempChildCareNurseManagement.getUsedNumber().getUsedDay().v() > 0.0) {
					DigestionDetails usedDay = new DigestionDetails();
					//????????????????????????????????????????????????????????????????????????????????????
					if(tempChildCareNurseManagement.getYmd().after(GeneralDate.today())) {
						usedDay.setDigestionStatus(KDL051_33);
					}
					usedDay.setDigestionDate(
							TextResource.localize("KDL051_36", tempChildCareNurseManagement.getYmd().toString(),
									this.getDayOfJapan(tempChildCareNurseManagement.getYmd().dayOfWeek())));
					usedDay.setNumberOfUse(TextResource.localize("KDL051_34",
							tempChildCareNurseManagement.getUsedNumber().getUsedDay().v().toString()));
					listDigestionDetails.add(usedDay);
				}
				// ???????????????????????????????????????????????????????????????
				if (tempChildCareNurseManagement.getUsedNumber().getUsedTimes().isPresent() && tempChildCareNurseManagement.getUsedNumber().getUsedTimes().get().v() > 0) {
					DigestionDetails usedTime = new DigestionDetails();
					//????????????????????????????????????????????????????????????????????????????????????
					if(tempChildCareNurseManagement.getYmd().after(GeneralDate.today())) {
						usedTime.setDigestionStatus(KDL051_33);
					}
					usedTime.setDigestionDate(
							TextResource.localize("KDL051_36", tempChildCareNurseManagement.getYmd().toString(),
									this.getDayOfJapan(tempChildCareNurseManagement.getYmd().dayOfWeek())));
					usedTime.setNumberOfUse(convertTime(tempChildCareNurseManagement.getUsedNumber().getUsedTimes().get().v()));
					listDigestionDetails.add(usedTime);
				}
				
			}
			// ?????????????????????DTO?????????
			// set ???????????????
			// ????????????????????????????????????????????????????????????????????????????????????????????????????????????
			Double limitDays = (double) aggrResultOfChildCareNurse.getStartdateDays().getThisYear().getRemainingNumber().getRemainDay().v(); //???121581
			String v1 ="";
			String v2 ="";
			//??????????????????????????????????????????????????????????????????????????????
			if(acquireNursingAndChildNursingDto.getNursingLeaveSetting().get().getTimeCareNursingSetting().getManageDistinct() == ManageDistinct.YES ) {
				v1 = TextResource.localize("KDL051_31");
				//???????????????????????????????????????????????????????????????????????????????????????????????????
				v2 = convertTime(aggrResultOfChildCareNurse.getStartdateDays().getThisYear().getRemainingNumber().getRemainTimes().isPresent()? 
						aggrResultOfChildCareNurse.getStartdateDays().getThisYear().getRemainingNumber().getRemainTimes().get().v():0);
				
			}
			String KDL051_30 = TextResource.localize("KDL051_30", limitDays.toString(), v1, v2);
			dataResult.setMaxNumberOfYear(KDL051_30);
			// set ?????????????????????
			dataResult.setUpperLimitStartDate(childCareNurseUpperLimitPeriod.getPeriod().start().toString());
			// set ?????????????????????
			dataResult.setUpperLimitEndDate(childCareNurseUpperLimitPeriod.getPeriod().end().toString());
			// set ????????????
			String KDL051_34 = TextResource.localize("KDL051_34", Double.valueOf(aggrResultOfChildCareNurse.getStartdateDays().getThisYear().getLimitDays().v()).toString());
			dataResult.setMaxNumberOfDays(KDL051_34);
			// set ?????????
			String value0 = aggrResultOfChildCareNurse.getStartdateDays().getThisYear().getUsedDays().getUsedDay().v().toString();
			String value1 = "";
			String value2 = "";
			String value3 = "";
			if ( acquireNursingAndChildNursingDto.getNursingLeaveSetting().get().getTimeCareNursingSetting().getManageDistinct() == ManageDistinct.YES) { //120688
				value1 = KDL051_31;
				value2 = convertTime(aggrResultOfChildCareNurse.getStartdateDays().getThisYear().getUsedDays().getUsedTimes().isPresent()?
						aggrResultOfChildCareNurse.getStartdateDays().getThisYear().getUsedDays().getUsedTimes().get().v():0);
				value3 = KDL051_32;
			}
			String KDL051_35 = TextResource.localize("KDL051_35", value0, value1, value2, value3);
			dataResult.setNumberOfUse(KDL051_35);
			// set ??????????????????
			dataResult.setListDigestionDetails(listDigestionDetails);
		}

		// ?????????????????????DTO??????????????? ??? ???????????????????????????????????????????????????????????????
		// set ????????????
		dataResult.setManagementSection(acquireNursingAndChildNursingDto.isManagementSection());

		return dataResult;
	}

	private String convertTime(Integer time) {
		if (time == null) {
			return "";
		}
		String m = String.valueOf(time % 60).length() > 1 ? String.valueOf(time % 60) : 0 + String.valueOf(time % 60);
		String timeString = String.valueOf(time / 60) + ":" + m;
		return timeString;
	}

	public String getDayOfJapan(int day) {
		switch (day) {
		case 1:
			return "???";
		case 2:
			return "???";
		case 3:
			return "???";
		case 4:
			return "???";
		case 5:
			return "???";
		case 6:
			return "???";
		default:
			return "???";
		}
	}

	@AllArgsConstructor
	private class GetUsageDetailChildCareServiceImpl implements GetUsageDetailChildCareService.Require {
		
		@Inject
		private RecordDomRequireService requireService;

		@Override
		public List<DailyInterimRemainMngData> getHolidayDetailByPeriod(String companyId, String employeeId,
				DatePeriod period, ReferenceAtr referenceAtr) {
			val requireA = requireService.createRequire();
			List<DailyInterimRemainMngData> data = GetHolidayDetailByPeriod.getHolidayDetailByPeriod(requireA, companyId, employeeId, period, referenceAtr);
			return data;
		}
	}
	
	@AllArgsConstructor
	private class GetUsageDetailCareServiceImpl implements GetUsageDetailCareService.Require {
		
		@Inject
		private RecordDomRequireService requireService;

		@Override
		public List<DailyInterimRemainMngData> getHolidayDetailByPeriod(String companyId, String employeeId,
				DatePeriod period, ReferenceAtr referenceAtr) {
			val requireA = requireService.createRequire();
			List<DailyInterimRemainMngData> data = GetHolidayDetailByPeriod.getHolidayDetailByPeriod(requireA, companyId, employeeId, period, referenceAtr);
			return data;
		}
	}
	

}
