package nts.uk.ctx.at.record.dom.remainingnumber.holidayover60h.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.record.dom.monthly.vacation.holidayover60h.HolidayOver60h;
import nts.uk.ctx.at.record.dom.remainingnumber.holidayover60h.export.param.AggrResultOfHolidayOver60h;
import nts.uk.ctx.at.record.dom.remainingnumber.holidayover60h.export.param.HolidayOver60hInfo;
import nts.uk.ctx.at.record.dom.remainingnumber.holidayover60h.export.param.HolidayOver60hRemainingNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.GrantRemainRegisterType;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveNumberInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.holidayover60h.empinfo.grantremainingdata.HolidayOver60hGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.holidayover60h.interim.TmpHolidayOver60hMng;

@Stateless
public class GetHolidayOver60hRemNumWithinPeriodImpl implements GetHolidayOver60hRemNumWithinPeriod {

	/**
	 * ????????????60H?????????????????????
	 * @param require Require
	 * @param cacheCarrier CacheCarrier
	 * @param companyId ??????ID
	 * @param employeeId ??????ID
	 * @param aggrPeriod ????????????
	 * @param mode ????????????????????????
	 * @param criteriaDate ?????????
	 * @param isOverWriteOpt ??????????????????
	 * @param forOverWriteListOpt ?????????????????????60H?????????????????????
	 * @param prevAnnualLeaveOpt ?????????60H?????????????????????
	 * @return 60H?????????????????????
	 */
	public AggrResultOfHolidayOver60h algorithm(
			GetHolidayOver60hRemNumWithinPeriod.RequireM1 require,
			CacheCarrier cacheCarrier,
			String companyId,
			String employeeId,
			DatePeriod aggrPeriod,
			InterimRemainMngMode mode,
			GeneralDate criteriaDate,
			Optional<Boolean> isOverWriteOpt,
			Optional<List<TmpHolidayOver60hMng>> forOverWriteList,
			Optional<AggrResultOfHolidayOver60h> prevHolidayOver60h) {

		AggrResultOfHolidayOver60h result = this.createSampleData();
		return result;

	}

	/**
	 * Require
	 * @author masaaki_jinno
	 *
	 */
	public static class GetHolidayOver60hRemNumWithinPeriodRequireM1 implements GetHolidayOver60hRemNumWithinPeriod.RequireM1 {
		public GetHolidayOver60hRemNumWithinPeriodRequireM1(){
		}
	}


	/**
	 * ???????????????????????????
	 * @return
	 */
	private AggrResultOfHolidayOver60h createSampleData(){

		AggrResultOfHolidayOver60h result = new AggrResultOfHolidayOver60h();

		// ????????????
//		result.setUsedTimes(new UsedTimes(3));

		//????????????????????????
		HolidayOver60hInfo holidayOver60hInfo_1 = createHolidayOver60hInfo_1();
		result.setAsOfPeriodEnd(Finally.of(holidayOver60hInfo_1));

		//?????????????????????????????????
		HolidayOver60hInfo holidayOver60hInfo_2 = createHolidayOver60hInfo_2();
		result.setAsOfStartNextDayOfPeriodEnd(Finally.of(holidayOver60hInfo_2));

		//???????????????
		HolidayOver60hInfo holidayOver60hInfo_3 = createHolidayOver60hInfo_3();
		result.setLapsed(Optional.of(holidayOver60hInfo_3));

		return result;
	}

	private HolidayOver60hInfo createHolidayOver60hInfo_1(){

		// ??????H????????????
		HolidayOver60hInfo holidayOver60hInfo = new HolidayOver60hInfo();

		// ?????????
		holidayOver60hInfo.setYmd(GeneralDate.ymd(2020, 8, 31));

		// ??????
		HolidayOver60hRemainingNumber ???olidayOver60hRemainingNumber
			 = new HolidayOver60hRemainingNumber();

		// 60H?????? ??????????????????
		{
			HolidayOver60h holidayOver60h = new HolidayOver60h();
			holidayOver60h.setUsedTime(new AnnualLeaveUsedTime(180));
			holidayOver60h.setRemainingTime(new AnnualLeaveRemainingTime(300));

			???olidayOver60hRemainingNumber.setHolidayOver60hWithMinus(holidayOver60h);
		}
		// 60H?????? ??????????????????
		{
			HolidayOver60h holidayOver60h = new HolidayOver60h();
			holidayOver60h.setUsedTime(new AnnualLeaveUsedTime(180));
			holidayOver60h.setRemainingTime(new AnnualLeaveRemainingTime(300));

			???olidayOver60hRemainingNumber.setHolidayOver60hNoMinus(holidayOver60h);
		}

		//?????????????????????
		AnnualLeaveRemainingTime carryForwardTimes = new AnnualLeaveRemainingTime(300);
		???olidayOver60hRemainingNumber.setCarryForwardTimes(carryForwardTimes);

		/** ???????????? */
		AnnualLeaveRemainingTime holidayOver60hUndigestNumber
			= new AnnualLeaveRemainingTime(60);
		???olidayOver60hRemainingNumber.setHolidayOver60hUndigestNumber(holidayOver60hUndigestNumber);

		// ??????????????????
		holidayOver60hInfo.setRemainingNumber(???olidayOver60hRemainingNumber);

		/** ????????????????????? */
		ArrayList<HolidayOver60hGrantRemainingData> grantRemainingList
			= new ArrayList<HolidayOver60hGrantRemainingData>();

		{
			// 1??????
			{
				HolidayOver60hGrantRemainingData holidayOver60hGrantRemaining
					= new HolidayOver60hGrantRemainingData();
				holidayOver60hGrantRemaining.setEmployeeId("ca294040-910f-4a42-8d90-2bd02772697c");
				holidayOver60hGrantRemaining.setGrantDate(GeneralDate.ymd(2020, 5, 15));
				holidayOver60hGrantRemaining.setDeadline(GeneralDate.ymd(2020, 8, 15));
				holidayOver60hGrantRemaining.setExpirationStatus(LeaveExpirationStatus.EXPIRED);
				holidayOver60hGrantRemaining.setRegisterType(GrantRemainRegisterType.MONTH_CLOSE);

				// ??????
				LeaveNumberInfo leaveNumberInfo = new LeaveNumberInfo();
				//????????????????????????
				leaveNumberInfo.getGrantNumber().setMinutes(Optional.of(new LeaveGrantTime(300)));
				//?????????????????????
				leaveNumberInfo.getUsedNumber().setMinutes(Optional.of(new LeaveUsedTime(240)));
				//?????????????????????
				leaveNumberInfo.getRemainingNumber().setMinutes(Optional.of(new LeaveRemainingTime(60)));
				// ??????
				holidayOver60hGrantRemaining.setDetails(leaveNumberInfo);
				// ??????
				grantRemainingList.add(holidayOver60hGrantRemaining);
			}
			{
				HolidayOver60hGrantRemainingData holidayOver60hGrantRemaining
					= new HolidayOver60hGrantRemainingData();
				holidayOver60hGrantRemaining.setEmployeeId("ca294040-910f-4a42-8d90-2bd02772697c");
				holidayOver60hGrantRemaining.setGrantDate(GeneralDate.ymd(2020, 7, 1));
				holidayOver60hGrantRemaining.setDeadline(GeneralDate.ymd(2020, 10, 1));
				holidayOver60hGrantRemaining.setExpirationStatus(LeaveExpirationStatus.AVAILABLE);
				holidayOver60hGrantRemaining.setRegisterType(GrantRemainRegisterType.MONTH_CLOSE);

				// ???????????????
				LeaveNumberInfo leaveNumberInfo = new LeaveNumberInfo();
				//????????????????????????
				leaveNumberInfo.getGrantNumber().setMinutes(Optional.of(new LeaveGrantTime(360)));
				//?????????????????????
				leaveNumberInfo.getUsedNumber().setMinutes(Optional.of(new LeaveUsedTime(60)));
				//?????????????????????
				leaveNumberInfo.getRemainingNumber().setMinutes(Optional.of(new LeaveRemainingTime(300)));
				// ??????
				holidayOver60hGrantRemaining.setDetails(leaveNumberInfo);
				// ??????
				grantRemainingList.add(holidayOver60hGrantRemaining);
			}
			{
				HolidayOver60hGrantRemainingData holidayOver60hGrantRemainingData
					= new HolidayOver60hGrantRemainingData();
				holidayOver60hGrantRemainingData.setEmployeeId("ca294040-910f-4a42-8d90-2bd02772697c");
				holidayOver60hGrantRemainingData.setGrantDate(GeneralDate.ymd(2020, 8, 1));
				holidayOver60hGrantRemainingData.setDeadline(GeneralDate.ymd(2020, 11, 1));
				holidayOver60hGrantRemainingData.setExpirationStatus(LeaveExpirationStatus.AVAILABLE);
				holidayOver60hGrantRemainingData.setRegisterType(GrantRemainRegisterType.MONTH_CLOSE);

				// ???????????????
				LeaveNumberInfo leaveNumberInfo = new LeaveNumberInfo();
				//????????????????????????
				leaveNumberInfo.getGrantNumber().setMinutes(Optional.of(new LeaveGrantTime(240)));
				//?????????????????????
				leaveNumberInfo.getUsedNumber().setMinutes(Optional.of(new LeaveUsedTime(0)));
				//?????????????????????
				leaveNumberInfo.getRemainingNumber().setMinutes(Optional.of(new LeaveRemainingTime(240)));
				// ??????
				holidayOver60hGrantRemainingData.setDetails(leaveNumberInfo);
				// ??????
				grantRemainingList.add(holidayOver60hGrantRemainingData);
			}
			holidayOver60hInfo.setGrantRemainingDataList(grantRemainingList);
		}

		return holidayOver60hInfo;
	}

	private HolidayOver60hInfo createHolidayOver60hInfo_2(){

		// ??????H????????????
		HolidayOver60hInfo holidayOver60hInfo = new HolidayOver60hInfo();

		// ?????????
		holidayOver60hInfo.setYmd(GeneralDate.ymd(2020, 9, 1));

		// ??????
		HolidayOver60hRemainingNumber ???olidayOver60hRemainingNumber
			 = new HolidayOver60hRemainingNumber();

		// 60H?????? ??????????????????
		{
			HolidayOver60h holidayOver60h = new HolidayOver60h();
			holidayOver60h.setUsedTime(new AnnualLeaveUsedTime(180));
			holidayOver60h.setRemainingTime(new AnnualLeaveRemainingTime(600));

			???olidayOver60hRemainingNumber.setHolidayOver60hWithMinus(holidayOver60h);
		}
		// 60H?????? ??????????????????
		{
			HolidayOver60h holidayOver60h = new HolidayOver60h();
			holidayOver60h.setUsedTime(new AnnualLeaveUsedTime(180));
			holidayOver60h.setRemainingTime(new AnnualLeaveRemainingTime(600));

			???olidayOver60hRemainingNumber.setHolidayOver60hNoMinus(holidayOver60h);
		}

		//?????????????????????
		AnnualLeaveRemainingTime carryForwardTimes = new AnnualLeaveRemainingTime(300);
		???olidayOver60hRemainingNumber.setCarryForwardTimes(carryForwardTimes);

		/** ???????????? */
		AnnualLeaveRemainingTime holidayOver60hUndigestNumber
			= new AnnualLeaveRemainingTime(60);
		???olidayOver60hRemainingNumber.setHolidayOver60hUndigestNumber(holidayOver60hUndigestNumber);

		// ??????????????????
		holidayOver60hInfo.setRemainingNumber(???olidayOver60hRemainingNumber);

		/** ????????????????????? */
		ArrayList<HolidayOver60hGrantRemainingData> grantRemainingList
			= new ArrayList<HolidayOver60hGrantRemainingData>();

		{
			// 1??????
			{
				HolidayOver60hGrantRemainingData holidayOver60hGrantRemaining
					= new HolidayOver60hGrantRemainingData();
				holidayOver60hGrantRemaining.setEmployeeId("ca294040-910f-4a42-8d90-2bd02772697c");
				holidayOver60hGrantRemaining.setGrantDate(GeneralDate.ymd(2020, 5, 15));
				holidayOver60hGrantRemaining.setDeadline(GeneralDate.ymd(2020, 8, 15));
				holidayOver60hGrantRemaining.setExpirationStatus(LeaveExpirationStatus.EXPIRED);
				holidayOver60hGrantRemaining.setRegisterType(GrantRemainRegisterType.MONTH_CLOSE);

				// ??????
				LeaveNumberInfo leaveNumberInfo = new LeaveNumberInfo();
				//????????????????????????
				leaveNumberInfo.getGrantNumber().setMinutes(Optional.of(new LeaveGrantTime(300)));
				//?????????????????????
				leaveNumberInfo.getUsedNumber().setMinutes(Optional.of(new LeaveUsedTime(240)));
				//?????????????????????
				leaveNumberInfo.getRemainingNumber().setMinutes(Optional.of(new LeaveRemainingTime(60)));
				// ??????
				holidayOver60hGrantRemaining.setDetails(leaveNumberInfo);
				// ??????
				grantRemainingList.add(holidayOver60hGrantRemaining);
			}
			// 2??????
			{
				HolidayOver60hGrantRemainingData holidayOver60hGrantRemaining
					= new HolidayOver60hGrantRemainingData();
				holidayOver60hGrantRemaining.setEmployeeId("ca294040-910f-4a42-8d90-2bd02772697c");
				holidayOver60hGrantRemaining.setGrantDate(GeneralDate.ymd(2020, 7, 1));
				holidayOver60hGrantRemaining.setDeadline(GeneralDate.ymd(2020, 10, 1));
				holidayOver60hGrantRemaining.setExpirationStatus(LeaveExpirationStatus.AVAILABLE);
				holidayOver60hGrantRemaining.setRegisterType(GrantRemainRegisterType.MONTH_CLOSE);

				// ???????????????
				LeaveNumberInfo leaveNumberInfo = new LeaveNumberInfo();
				//????????????????????????
				leaveNumberInfo.getGrantNumber().setMinutes(Optional.of(new LeaveGrantTime(360)));
				//?????????????????????
				leaveNumberInfo.getUsedNumber().setMinutes(Optional.of(new LeaveUsedTime(60)));
				//?????????????????????
				leaveNumberInfo.getRemainingNumber().setMinutes(Optional.of(new LeaveRemainingTime(180)));
				// ??????
				holidayOver60hGrantRemaining.setDetails(leaveNumberInfo);
				// ??????
				grantRemainingList.add(holidayOver60hGrantRemaining);
			}
			// 3??????
			{
				HolidayOver60hGrantRemainingData holidayOver60hGrantRemaining
					= new HolidayOver60hGrantRemainingData();
				holidayOver60hGrantRemaining.setEmployeeId("ca294040-910f-4a42-8d90-2bd02772697c");
				holidayOver60hGrantRemaining.setGrantDate(GeneralDate.ymd(2020, 8, 1));
				holidayOver60hGrantRemaining.setDeadline(GeneralDate.ymd(2020, 11, 1));
				holidayOver60hGrantRemaining.setExpirationStatus(LeaveExpirationStatus.AVAILABLE);
				holidayOver60hGrantRemaining.setRegisterType(GrantRemainRegisterType.MONTH_CLOSE);

				// ???????????????
				LeaveNumberInfo leaveNumberInfo = new LeaveNumberInfo();
				//????????????????????????
				leaveNumberInfo.getGrantNumber().setMinutes(Optional.of(new LeaveGrantTime(240)));
				//?????????????????????
				leaveNumberInfo.getUsedNumber().setMinutes(Optional.of(new LeaveUsedTime(0)));
				//?????????????????????
				leaveNumberInfo.getRemainingNumber().setMinutes(Optional.of(new LeaveRemainingTime(240)));
				// ??????
				holidayOver60hGrantRemaining.setDetails(leaveNumberInfo);
				// ??????
				grantRemainingList.add(holidayOver60hGrantRemaining);
			}
			// 4??????
			{
				HolidayOver60hGrantRemainingData holidayOver60hGrantRemaining
					= new HolidayOver60hGrantRemainingData();
				holidayOver60hGrantRemaining.setEmployeeId("ca294040-910f-4a42-8d90-2bd02772697c");
				holidayOver60hGrantRemaining.setGrantDate(GeneralDate.ymd(2020, 9, 1));
				holidayOver60hGrantRemaining.setDeadline(GeneralDate.ymd(2020, 12, 1));
				holidayOver60hGrantRemaining.setExpirationStatus(LeaveExpirationStatus.AVAILABLE);
				holidayOver60hGrantRemaining.setRegisterType(GrantRemainRegisterType.MONTH_CLOSE);

				// ???????????????
				LeaveNumberInfo leaveNumberInfo = new LeaveNumberInfo();
				//????????????????????????
				leaveNumberInfo.getGrantNumber().setMinutes(Optional.of(new LeaveGrantTime(300)));
				//?????????????????????
				leaveNumberInfo.getUsedNumber().setMinutes(Optional.of(new LeaveUsedTime(0)));
				//?????????????????????
				leaveNumberInfo.getRemainingNumber().setMinutes(Optional.of(new LeaveRemainingTime(300)));
				// ??????
				holidayOver60hGrantRemaining.setDetails(leaveNumberInfo);
				// ??????
				grantRemainingList.add(holidayOver60hGrantRemaining);
			}
			holidayOver60hInfo.setGrantRemainingDataList(grantRemainingList);
		}

		return holidayOver60hInfo;
	}

	private HolidayOver60hInfo createHolidayOver60hInfo_3(){

		// ??????H????????????
		HolidayOver60hInfo holidayOver60hInfo = new HolidayOver60hInfo();

		// ?????????
		holidayOver60hInfo.setYmd(GeneralDate.ymd(2020, 8, 15));

		// ??????
		HolidayOver60hRemainingNumber ???olidayOver60hRemainingNumber
			 = new HolidayOver60hRemainingNumber();

		// 60H?????? ??????????????????
		{
			HolidayOver60h holidayOver60h = new HolidayOver60h();
			holidayOver60h.setUsedTime(new AnnualLeaveUsedTime(60));
			holidayOver60h.setRemainingTime(new AnnualLeaveRemainingTime(480));

			???olidayOver60hRemainingNumber.setHolidayOver60hWithMinus(holidayOver60h);
		}
		// 60H?????? ??????????????????
		{
			HolidayOver60h holidayOver60h = new HolidayOver60h();
			holidayOver60h.setUsedTime(new AnnualLeaveUsedTime(60));
			holidayOver60h.setRemainingTime(new AnnualLeaveRemainingTime(480));

			???olidayOver60hRemainingNumber.setHolidayOver60hNoMinus(holidayOver60h);
		}

		//?????????????????????
		AnnualLeaveRemainingTime carryForwardTimes = new AnnualLeaveRemainingTime(300);
		???olidayOver60hRemainingNumber.setCarryForwardTimes(carryForwardTimes);

		/** ???????????? */
		AnnualLeaveRemainingTime holidayOver60hUndigestNumber
			= new AnnualLeaveRemainingTime(0);
		???olidayOver60hRemainingNumber.setHolidayOver60hUndigestNumber(holidayOver60hUndigestNumber);

		// ??????????????????
		holidayOver60hInfo.setRemainingNumber(???olidayOver60hRemainingNumber);

		/** ????????????????????? */
		ArrayList<HolidayOver60hGrantRemainingData> grantRemainingList
			= new ArrayList<HolidayOver60hGrantRemainingData>();

		{
			// 1??????
			{
				HolidayOver60hGrantRemainingData holidayOver60hGrantRemaining
					= new HolidayOver60hGrantRemainingData();
				holidayOver60hGrantRemaining.setEmployeeId("ca294040-910f-4a42-8d90-2bd02772697c");
				holidayOver60hGrantRemaining.setGrantDate(GeneralDate.ymd(2020, 5, 15));
				holidayOver60hGrantRemaining.setDeadline(GeneralDate.ymd(2020, 8, 15));
				holidayOver60hGrantRemaining.setExpirationStatus(LeaveExpirationStatus.AVAILABLE);
				holidayOver60hGrantRemaining.setRegisterType(GrantRemainRegisterType.MONTH_CLOSE);

				// ??????
				LeaveNumberInfo leaveNumberInfo = new LeaveNumberInfo();
				//????????????????????????
				leaveNumberInfo.getGrantNumber().setMinutes(Optional.of(new LeaveGrantTime(300)));
				//?????????????????????
				leaveNumberInfo.getUsedNumber().setMinutes(Optional.of(new LeaveUsedTime(240)));
				//?????????????????????
				leaveNumberInfo.getRemainingNumber().setMinutes(Optional.of(new LeaveRemainingTime(60)));
				// ??????
				holidayOver60hGrantRemaining.setDetails(leaveNumberInfo);
				// ??????
				grantRemainingList.add(holidayOver60hGrantRemaining);
			}
			{
				HolidayOver60hGrantRemainingData holidayOver60hGrantRemaining
					= new HolidayOver60hGrantRemainingData();
				holidayOver60hGrantRemaining.setEmployeeId("ca294040-910f-4a42-8d90-2bd02772697c");
				holidayOver60hGrantRemaining.setGrantDate(GeneralDate.ymd(2020, 7, 1));
				holidayOver60hGrantRemaining.setDeadline(GeneralDate.ymd(2020, 10, 1));
				holidayOver60hGrantRemaining.setExpirationStatus(LeaveExpirationStatus.AVAILABLE);
				holidayOver60hGrantRemaining.setRegisterType(GrantRemainRegisterType.MONTH_CLOSE);

				// ???????????????
				LeaveNumberInfo leaveNumberInfo = new LeaveNumberInfo();
				//????????????????????????
				leaveNumberInfo.getGrantNumber().setMinutes(Optional.of(new LeaveGrantTime(360)));
				//?????????????????????
				leaveNumberInfo.getUsedNumber().setMinutes(Optional.of(new LeaveUsedTime(180)));
				//?????????????????????
				leaveNumberInfo.getRemainingNumber().setMinutes(Optional.of(new LeaveRemainingTime(180)));
				// ??????
				holidayOver60hGrantRemaining.setDetails(leaveNumberInfo);
				// ??????
				grantRemainingList.add(holidayOver60hGrantRemaining);
			}
			{
				HolidayOver60hGrantRemainingData holidayOver60hGrantRemaining
					= new HolidayOver60hGrantRemainingData();
				holidayOver60hGrantRemaining.setEmployeeId("ca294040-910f-4a42-8d90-2bd02772697c");
				holidayOver60hGrantRemaining.setGrantDate(GeneralDate.ymd(2020, 8, 1));
				holidayOver60hGrantRemaining.setDeadline(GeneralDate.ymd(2020, 11, 1));
				holidayOver60hGrantRemaining.setExpirationStatus(LeaveExpirationStatus.EXPIRED);
				holidayOver60hGrantRemaining.setRegisterType(GrantRemainRegisterType.MONTH_CLOSE);

				// ???????????????
				LeaveNumberInfo leaveNumberInfo = new LeaveNumberInfo();
				//????????????????????????
				leaveNumberInfo.getGrantNumber().setMinutes(Optional.of(new LeaveGrantTime(240)));
				//?????????????????????
				leaveNumberInfo.getUsedNumber().setMinutes(Optional.of(new LeaveUsedTime(0)));
				//?????????????????????
				leaveNumberInfo.getRemainingNumber().setMinutes(Optional.of(new LeaveRemainingTime(240)));
				// ??????
				holidayOver60hGrantRemaining.setDetails(leaveNumberInfo);
				// ??????
				grantRemainingList.add(holidayOver60hGrantRemaining);
			}
			holidayOver60hInfo.setGrantRemainingDataList(grantRemainingList);
		}

		return holidayOver60hInfo;
	}
}


