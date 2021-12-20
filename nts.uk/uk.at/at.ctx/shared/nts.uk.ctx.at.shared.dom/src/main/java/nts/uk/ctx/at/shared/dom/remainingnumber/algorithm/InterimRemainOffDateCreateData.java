package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.UsedDays;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.care.interimdata.TempCareManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.childcare.interimdata.TempChildCareManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialholidaymng.interim.InterimSpecialHolidayMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.AppTimeType;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.CareType;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.CareUseDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.CompanyHolidayMngSetting;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.DayoffTranferInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.EmploymentHolidayMngSetting;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.InforFormerRemainData;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.OccurrenceUseDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.SpecialHolidayUseDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.TranferTimeInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.VacationTimeInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.VacationTimeUseInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.VacationUsageTimeDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.WorkTypeRemainInfor;
import nts.uk.ctx.at.shared.dom.schedule.WorkingDayCategory;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.FuriClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NumberOfDaySuspension;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayCode;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CheckDateForManageCmpLeaveService;
import nts.uk.ctx.at.shared.dom.workingcondition.service.WorkingConditionService;
import nts.uk.ctx.at.shared.dom.worktime.common.CompensatoryOccurrenceDivision;
import nts.uk.ctx.at.shared.dom.worktime.common.GetSubHolOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.SubHolTransferSet;
import nts.uk.ctx.at.shared.dom.worktime.common.subholtransferset.GetDesignatedTime;
import nts.uk.ctx.at.shared.dom.worktime.service.WorkTimeIsFluidWork;
import nts.uk.ctx.at.shared.dom.worktype.HolidayAtr;
import nts.uk.ctx.at.shared.dom.worktype.WorkAtr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeSet;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeSetCheck;
import nts.uk.ctx.at.shared.dom.worktype.specialholidayframe.SpecialHdFrameNo;

public class InterimRemainOffDateCreateData {

	/**
	 * 指定日の暫定残数管理データを作成する
	 * @param cid
	 * @param sid
	 * @param baseDate
	 * @param dayOffTimeIsUse: 時間代休利用: True: 利用, False: 未利用
	 * @param detailData
	 * @param callFunction
	 * @return
	 */
	public static Optional<DailyInterimRemainMngData> createData(RequireM9 require, String cid, String sid, GeneralDate baseDate, boolean dayOffTimeIsUse,
			InterimRemainCreateInfor detailData, CompanyHolidayMngSetting comHolidaySetting, EmploymentHolidayMngSetting employmentHolidaySetting, CallFunction callFunction) {
		//残数作成元情報を作成する
		Optional<InforFormerRemainData> formerRemainData = createInforFormerRemainData(require, cid, sid, baseDate, detailData, dayOffTimeIsUse,
				comHolidaySetting, employmentHolidaySetting, callFunction);

		if (!formerRemainData.isPresent()) {
			return Optional.empty();
		}

		//残数作成元情報から暫定残数管理データを作成する
		DailyInterimRemainMngData createDataInterimRemain = createDataInterimRemain(require, formerRemainData.get());
		return Optional.of(createDataInterimRemain);
	}

	/**
	 * 時間休暇使用時間を作成
	 * @param require
	 *
	 * @param cid 会社ID
	 * @param sid 社員ID
	 * @param baseDate 年月日
	 * @param detailData INPUT．予定 ,INPUT．実績 ,INPUT．申請(List)
	 * @param formerRemainData 残数作成元情報
	 * @return
	 */
	private static List<VacationTimeInfor> createUsageHolidayTimes(RequireM9 require, String cid, String sid, GeneralDate baseDate,
			InterimRemainCreateInfor detailData) {
		// 時間休暇情報の元情報作成する
		List<VacationTimeInfor> timeInfos = createOriginUsageHolidayTimes(require, cid, sid, baseDate, detailData);
		// 作成した時間休暇使用時間をマージする
		return mergeUsageHolidayTimes(timeInfos);
	}

	/**
	 * 作成した時間休暇使用時間をマージする
	 *
	 * @param timeInfos 時間休暇使用時間
	 * @param formerRemainData 残数作成元情報
	 * @return
	 */
	private static List<VacationTimeInfor> mergeUsageHolidayTimes(List<VacationTimeInfor> timeInfos) {

		//時間休暇を作成
		List<VacationTimeInfor> vacTimes = createTempList();

		//INPUT．時間休暇使用時間でループ
		timeInfos.forEach(timeInfo -> {
			//時間が０より大きいか
			if(!timeInfo.getVacationUsageTimeDetails()
						.stream().filter(uTime -> uTime.getTimes() > 0)
						.collect(Collectors.toList()).isEmpty()){
				// => 存在する => 時間休暇使用時間を上書きする
				vacTimes.stream()
						.filter(vacTime -> vacTime.getTimeType().equals(timeInfo.getTimeType())).findFirst()
						.ifPresent(vacTime -> {
							//作成元区分 ← 処理中の時間休暇使用時間．作成元区分
							vacTime.setCreateData(timeInfo.getCreateData());
							//勤務種類コード← 処理中の時間休暇使用時間．勤務種類コード
							vacTime.setWorkTypeCode(timeInfo.getWorkTypeCode());
							//時間 ← 処理中の時間休暇使用時間．時間
							vacTime.setVacationUsageTimeDetails(timeInfo.getVacationUsageTimeDetails());
						});
			}
		});

		return vacTimes.stream().filter(vac -> vac.getCreateData() != null).collect(Collectors.toList());
	}

	private static List<VacationTimeInfor> createTempList() {

		List<VacationTimeInfor> result = new ArrayList<VacationTimeInfor>();

		for (AppTimeType type : AppTimeType.values()) {

			result.add(new VacationTimeInfor(type, null, null, Collections.emptyList()));
		}
		return result;
	}

	/**
	 * 時間休暇情報の元情報作成する
	 * @param require
	 *
	 * @param cid 会社ID
	 * @param sid 社員ID
	 * @param baseDate 年月日
	 * @param detailData INPUT．予定 ,INPUT．実績 ,INPUT．申請(List)
	 * @return
	 */
	private static List<VacationTimeInfor> createOriginUsageHolidayTimes(RequireM9 require, String cid, String sid,
			GeneralDate baseDate, InterimRemainCreateInfor detailData) {
		// 時間年休使用時間の空のリストを作成
		List<VacationTimeInfor> timeInfos = new ArrayList<VacationTimeInfor>();
		// 予定から時間休暇使用時間を作成する
		List<VacationTimeUseInfor> scheLstVacationTimeInfor = detailData.getScheData()
				.map(x -> x.getLstVacationTimeInfor()).orElse(Collections.emptyList());

		List<VacationTimeInfor> scheTimeInfos = createVacationUsageTime(require, cid, CreateAtr.SCHEDULE,
				scheLstVacationTimeInfor, detailData.getScheData().map(x -> x.getWorkTypeCode()).orElse(null));

		timeInfos.addAll(scheTimeInfos);

		// 実績から時間休暇使用時間を作成する

		List<VacationTimeUseInfor> recordLstVacationTimeInfor = detailData.getRecordData()
				.map(x -> x.getLstVacationTimeInfor()).orElse(Collections.emptyList());

		List<VacationTimeInfor> recordTimeInfos = createVacationUsageTime(require, cid, CreateAtr.RECORD,
				recordLstVacationTimeInfor, detailData.getRecordData().map(x -> x.getWorkTypeCode()).orElse(null));

		timeInfos.addAll(recordTimeInfos);

		// 申請から時間休暇使用時間を作成する

		List<VacationTimeInfor> appTimeInfos = createVacationUsageTimeFromApp(require, cid, sid, baseDate,
				detailData.getAppData());

		timeInfos.addAll(appTimeInfos);

		return timeInfos;
	}

	/**
	 * 申請から時間休暇使用時間を作成する
	 *
	 * @param require
	 * @param cid 会社ID
	 * @param sid 社員ID
	 * @param baseDate 年月日
	 * @param appData 残数作成元情報（申請）（List）
	 * @return
	 */
	private static List<VacationTimeInfor> createVacationUsageTimeFromApp(RequireM9 require, String cid, String sid,
			GeneralDate baseDate, List<AppRemainCreateInfor> appData) {
		List<VacationTimeInfor> timeInfos = new ArrayList<VacationTimeInfor>();

		// 時間休暇申請の申請を抽出
		appData.stream().filter(x -> x.getSid().equals(sid) && x.getAppDate().equals(baseDate)
				&& x.getAppType().equals(ApplicationType.ANNUAL_HOLIDAY_APPLICATION)).sorted((a, b) -> a.getInputDate().compareTo(b.getInputDate())).forEach(x -> {

					CreateAtr createAtr = x.getPrePosAtr().equals(PrePostAtr.PREDICT) ? CreateAtr.APPBEFORE
							: CreateAtr.APPAFTER;

					List<VacationTimeUseInfor> appLstVacationTimeInfor = x.getVacationTimes();
					// 時間休暇使用時間を作成
					timeInfos.addAll(createVacationUsageTime(require, cid, createAtr, appLstVacationTimeInfor,
							x.getWorkTypeCode().map(wktype -> wktype).orElse("DMY")));
				});

		return timeInfos;
	}

	/**
	 * 残数作成元情報を作成する
	 * @param sid
	 * @param baseDate
	 * @param detailData
	 * @param callFunction
	 * @return
	 */
	public static Optional<InforFormerRemainData> createInforFormerRemainData(
			RequireM9 require, String cid, String sid, GeneralDate baseDate,
			InterimRemainCreateInfor detailData, boolean dayOffTimeIsUse, CompanyHolidayMngSetting comHolidaySetting,
			EmploymentHolidayMngSetting employmentHolidaySetting, CallFunction callFunction) {
		Optional<RemainNumberWtSubsLeavTransInfo> outputData = Optional.empty();
		//最新の勤務種類変更を伴う申請を抽出する
		AppRemainCreateInfor appWithWorkType = getAppWithWorkType(detailData.getAppData(), sid, baseDate);
		if(appWithWorkType == null) {
			//実績をチェックする
			if(detailData.getRecordData().isPresent()) {
				//実績から残数作成元情報を設定する
				outputData = createInterimDataFromRecord(require, cid, detailData.getRecordData().get(), dayOffTimeIsUse);
			} else {
				//予定をチェックする
				if(detailData.getScheData().isPresent()) {
					//予定から残数作成元情報を設定する
					outputData = createInterimDataFromSche(require, cid, detailData.getScheData().get(), dayOffTimeIsUse);
				}
			}
		} else {
			//最新の勤務種類変更を伴う申請から残数作成元情報を設定する
			CreateAtr createAtr = appWithWorkType.getPrePosAtr().equals(PrePostAtr.PREDICT)  ? CreateAtr.APPBEFORE : CreateAtr.APPAFTER;
			outputData = createInterimDataFromApp(require, cid, sid, baseDate, detailData, appWithWorkType, dayOffTimeIsUse,
					createAtr);
		}
		
		//時間休暇使用時間を作成
		List<VacationTimeInfor>vacationTime = createUsageHolidayTimes(require, cid, sid, baseDate, detailData);
		if(!outputData.isPresent() && vacationTime.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(new InforFormerRemainData(sid,
				baseDate,
				dayOffTimeIsUse,
				outputData.flatMap(x -> x.getWorkTypeRemain()),
				vacationTime,
				outputData.flatMap(x -> x.getDayOffTranfer()),
				comHolidaySetting,
				employmentHolidaySetting));
	}

	/**
	 * 実績から残数作成元情報を設定する
	 *
	 * @param require
	 * @param cid
	 *            会社ID
	 * @param recordData
	 *            残数作成元情報(実績)
	 * @param outputData
	 *            残数作成元情報
	 * @return 残数作成元情報
	 */
	public static Optional<RemainNumberWtSubsLeavTransInfo> createInterimDataFromRecord(RequireM9 require, String cid,
			RecordRemainCreateInfor recordData, boolean dayOffTimeIsUse) {
		//アルゴリズム「勤務種類別残数情報を作成する」を実行する
		Optional<WorkTypeRemainInfor> remainInfor = createWorkTypeRemainInfor(require,
																			cid,
																			CreateAtr.RECORD,
																			recordData.getWorkTypeCode(),
																			Optional.ofNullable(recordData.getTimeDigestionUsageInfor()),
																			recordData.getNumberDaySuspension(),
																			recordData.getWorkTimeCode(),
																			recordData.getSid(),
																			recordData.getYmd());
		if(!remainInfor.isPresent()) {
			return Optional.empty();
		}

		//勤務種類別残数情報を設定する
		//アルゴリズム「実績から代休振替情報を設定する」を実行する
		Optional<DayoffTranferInfor> dayOffInfor = createDayOffTranferFromRecord(require, cid, recordData.getSid(),
				recordData.getYmd(), CreateAtr.RECORD, recordData, dayOffTimeIsUse);
		return Optional.of(new RemainNumberWtSubsLeavTransInfo(remainInfor, dayOffInfor));
	}

	/**
	 * 時間休暇使用時間を作成する
	 * @param require
	 *
	 * @param workTypeCode
	 *            勤務種類コード
	 * @param vacationTimes
	 *            List<時間休暇使用情報>
	 * @param createAtr
	 *            作成元区分
	 * @param cid
	 *            会社ID
	 */
	private static List<VacationTimeInfor> createVacationUsageTime(RequireM8 require, String cid, CreateAtr createAtr, List<VacationTimeUseInfor> vacationTimes, String workTypeCode) {

		List<VacationTimeInfor> result = new ArrayList<VacationTimeInfor>();
		//時間休暇使用時間を作成

		vacationTimes.forEach(vacationTime -> {
			HolidayType[] types = HolidayType.values();

			for (int i = 0; i < types.length; i++) {
				AttendanceTime useTime =  getUseTime(vacationTime , types[i]);
				//時間休暇使用時間を設定する

				VacationTimeInfor vacation =  SetVacationUsageTime(require,cid, vacationTime, createAtr, types[i], useTime, vacationTime.getSpcVacationFrameNo(),
						workTypeCode);
				if (vacation != null) {
					result.add(vacation);
				}
			}

		});
		return result;
	}

	private static AttendanceTime getUseTime(VacationTimeUseInfor vacationTime, HolidayType holidayType) {
		switch (holidayType) {
		/** 年休 */
		case ANNUAL:
			return vacationTime.getNenkyuTime();
		/** 代休 */
		case SUBSTITUTE:
			return vacationTime.getKyukaTime();
		/** 特休 */
		case SPECIAL:
			return vacationTime.getSpecialHolidayUseTime();
		/** 60H超休 */
		case SIXTYHOUR:
			return vacationTime.getHChoukyuTime();
		/** 子の看護 */
		case CHILDCARE:
			return vacationTime.getTimeChildCareHolidayUseTime();
		/** 介護 */
		case CARE:
			return vacationTime.getTimeCareHolidayUseTime();
		default:
			return new AttendanceTime(0);
		}
	}

	/**
	 * 時間年休使用時間を設定する
	 * @param require
	 *
	 * @param workTypeCode
	 * @param optional
	 * @param useTime
	 * @param type 休暇種類
	 * @param createAtr
	 * @param vacationTime
	 * @param cid
	 * @return
	 */
	private static VacationTimeInfor SetVacationUsageTime(RequireM8 require, String cid,
			VacationTimeUseInfor vacationTime, CreateAtr createAtr, HolidayType type, AttendanceTime useTime,
			Optional<SpecialHdFrameNo> specialNo, String workTypeCode) {

		if (useTime.v() <= 0) {
			// 使用時間 <= 0
			return null;
		}
		Integer special = null;
		if (type.equals(HolidayType.SPECIAL)) {
			// 休暇種類 = 特休
			// 特休コード取得
			special = getSpecialByNo(require, cid, specialNo);
			if (special == null) {
				// 特別休暇＝設定なし
				return null;
			}
		}
		// 時間年休使用時間を設定する
		return new VacationTimeInfor(vacationTime.getTimeType(), createAtr, workTypeCode,
				Arrays.asList(new VacationUsageTimeDetail(type, useTime.v(), Optional.ofNullable(special))));
	}

	/**
	 * 特別休暇枠NOから特別休暇を取得する
	 *
	 * @param require
	 * @param cid 会社ID
	 * @param specialNo 特別休暇枠NO
	 * @return
	 */
	private static Integer getSpecialByNo(RequireM8 require, String cid, Optional<SpecialHdFrameNo> specialNo) {
		if(!specialNo.isPresent()){
			return null;
		}
		List<Integer> specialHolidays = require.getSpecialHolidayNumber(cid, specialNo.get().v());
		if(specialHolidays.isEmpty()){
			return null;
		}

		return specialHolidays.get(0);
	}

	/**
	 * 勤務種類別残数情報を作成する
	 *
	 * @param cid
	 *            会社ID
	 * @param createAtr
	 *            作成元区分
	 * @param workTypeCode
	 *            勤務種類コード
	 * @param numberOfDayOpt
	 *            振休振出として扱う日数
	 * @param timedigOpt
	 *            時間消化使用情報
	 * @return List<勤務種類別残数情報>
	 */
	public static Optional<WorkTypeRemainInfor> createWorkTypeRemainInfor(
			RequireM9 require,
			String cid,
			CreateAtr createAtr,
			String workTypeCode,
			Optional<TimeDigestionUsageInfor> timedigOpt,
			Optional<NumberOfDaySuspension> numberOfDayOpt,
			Optional<String> wkTimeCd,String sid,
			GeneralDate ymd) {
		WorkTypeRemainInfor outputData = new WorkTypeRemainInfor(null, null, null, new ArrayList<>(), new ArrayList<>(),
				new ArrayList<>());
		
		//ドメインモデル「勤務種類」を取得する
		Optional<WorkType> optWorkTypeData = require.workType(cid, workTypeCode);

		if(!optWorkTypeData.isPresent()) {
			return Optional.empty();
		}
		WorkType workType = optWorkTypeData.get();
		//勤務種類別残数情報を作成する
		outputData.setCreateData(createAtr);
		outputData.setWorkTypeCode(workTypeCode);
		outputData.setOccurrenceDetailData(createDetailData());

		if(workType.getDailyWork().isOneDay()) {
			//アルゴリズム「残数発生使用明細を作成する」を実行する (Thực hiện thuật toán "Tạo chi tiết sử dụng phát sinh số lượng tồn")
			return Optional.of(createWithOneDayWorkType(require, cid, workType, WorkAtr.OneDay, 1, outputData, timedigOpt, numberOfDayOpt,
					workTypeCode,wkTimeCd,sid,ymd));
		} else {

			//振休振出として扱う日数の内休暇系の勤務種類の日数を求める
			TotalNumberOfDay totalNumberOfDay = getNumberOfHoliday(numberOfDayOpt, workType, outputData);

			Optional<WorkTypeRemainInfor> morning = Optional.empty();
			Optional<WorkTypeRemainInfor> after =  Optional.empty();;
			//午前
			WorkTypeClassification workTypeMorning = workType.getDailyWork().getMorning();
			if(!judgmentType(workTypeMorning, numberOfDayOpt).equals(JudgmentTypeOfWorkType.REMAINOCCNOTCOVER)) {
				//アルゴリズム「残数発生使用明細を作成する」を実行する (Thực hiện thuật toán 「残数発生使用明細を作成する」 )
				numberOfDayOpt.ifPresent(c->c.setDays(new UsedDays(totalNumberOfDay.getTotalNumberMorning())));
				morning = Optional.ofNullable(createWithOneDayWorkType(require, cid, workType, WorkAtr.Monring, 0.5, outputData, timedigOpt, numberOfDayOpt,
						workTypeCode,wkTimeCd,sid,ymd));
			}
			//午後
			WorkTypeClassification workTypAfternoon = workType.getDailyWork().getAfternoon();
			if(!judgmentType(workTypAfternoon, numberOfDayOpt).equals(JudgmentTypeOfWorkType.REMAINOCCNOTCOVER)) {
				//アルゴリズム「残数発生使用明細を作成する」を実行する(Thực hiện thuật toán 「残数発生使用明細を作成する」 )
				numberOfDayOpt.ifPresent(c->c.setDays(new UsedDays(totalNumberOfDay.getTotalNumberAfternoon())));
				after =  Optional.ofNullable(createWithOneDayWorkType(require, cid, workType, WorkAtr.Afternoon, 0.5, outputData, timedigOpt, numberOfDayOpt,
						workTypeCode,wkTimeCd,sid,ymd));
			}
			//午前と午後で同じ勤務種類の残数発生明細をまとめる
			return totalMorningAndAfternoonRemain(morning, after);
		}
	}

	/**
	 * 午前と午後で勤務種類別残数情報をまとめる
	 *
	 * @param after
	 *            午前の残数発生使用明細
	 * @param morning
	 *            午後の残数発生使用明細
	 */
	private static Optional<WorkTypeRemainInfor> totalMorningAndAfternoonRemain(Optional<WorkTypeRemainInfor> morning, Optional<WorkTypeRemainInfor> after) {

		//午前と午後で同じ勤務種類の残数発生明細をまとめる
		morning = totalMorningAndAfternoonOccurrence(morning,after);
		//午前と午後で同じ特別休暇コードの特休使用明細をまとめる
		morning = totalMorningAndAfternoonSpecialHoliday(morning,after);
		//午前と午後で同じ介護看護区分の子の看護介護使用明細をまとめる
		morning = totalMorningAndAfternoonCare(morning,after);

		return morning;
	}
	
	/**
	 * 午前と午後で同じ勤務種類の残数発生明細をまとめる
	 * @param morning
	 * @param after
	 */
	private static Optional<WorkTypeRemainInfor> totalMorningAndAfternoonOccurrence(Optional<WorkTypeRemainInfor> morning, Optional<WorkTypeRemainInfor> after){
		for (val wkType : TargetWorkTypes()) {

			Optional<OccurrenceUseDetail> morningType = morning.flatMap(x-> x.getOccurrence(wkType));
			Optional<OccurrenceUseDetail> afterNoonType = after.flatMap(x-> x.getOccurrence(wkType));

			if (morningType.isPresent() && afterNoonType.isPresent()) {
				morningType.get().setDays(1);
			}
			if (morning.isPresent() && !morningType.isPresent() && afterNoonType.isPresent()) {
				morning.get().getOccurrenceDetailData().add(afterNoonType.get());
			}
			
			if(!morning.isPresent() && afterNoonType.isPresent()){
				morning = Optional.of(after.get().clone());
			}
			
		}
		
		return morning;
	}
	
	/**
	 * 午前と午後で同じ特別休暇コードの特休使用明細をまとめる
	 * @param morning
	 * @param after
	 */
	private static Optional<WorkTypeRemainInfor> totalMorningAndAfternoonSpecialHoliday(Optional<WorkTypeRemainInfor> morning, Optional<WorkTypeRemainInfor> after){
		for(val code: SpecialHolidayCode.getCodeList()){ 
			Optional<SpecialHolidayUseDetail> morningCode = morning.flatMap(x-> x.getSpecialHolidayUseDetail(code));
			Optional<SpecialHolidayUseDetail> afterCode = after.flatMap(x-> x.getSpecialHolidayUseDetail(code));
				
			if(morningCode.isPresent() && afterCode.isPresent()){
				morningCode.get().setDays(1);
			}
			if(morning.isPresent() && !morningCode.isPresent() && afterCode.isPresent()){
				morning.get().getSpeHolidayDetailData().add(afterCode.get());
			}
			
			if(!morning.isPresent() && afterCode.isPresent()){
				morning = Optional.of(after.get().clone());
			}
		}
		
		return morning;
	}
	
	/**
	 * 午前と午後で同じ介護看護区分の子の看護介護使用明細をまとめる
	 * @param morning
	 * @param after
	 */
	private static Optional<WorkTypeRemainInfor> totalMorningAndAfternoonCare(Optional<WorkTypeRemainInfor> morning, Optional<WorkTypeRemainInfor> after){
		
		for(val careType :Arrays.asList(CareType.values())){
			Optional<CareUseDetail> morningCare = morning.flatMap(x -> x.getCareUseDetail(careType));
			Optional<CareUseDetail> afterCare = after.flatMap(x -> x.getCareUseDetail(careType));		
			
			if(morningCare.isPresent() && afterCare.isPresent()){
				morningCare.get().setDays(1);
			}
			if(morning.isPresent() && !morningCare.isPresent() && afterCare.isPresent()){
				morning.get().getChildCareDetailData().add(afterCare.get());
			}
			
			if(!morning.isPresent() && afterCare.isPresent()){
				morning = Optional.of(after.get().clone());
			}
			
		}
		return morning;
	}

	

	/**
	 * 対象勤務種類
	 */

	private static List<WorkTypeClassification> TargetWorkTypes() {

		List<WorkTypeClassification> result = new ArrayList<WorkTypeClassification>();
		result.add(WorkTypeClassification.AnnualHoliday);
		result.add(WorkTypeClassification.YearlyReserved);
		result.add(WorkTypeClassification.SubstituteHoliday);
		result.add(WorkTypeClassification.Pause);
		//không có 公休
		result.add(WorkTypeClassification.HolidayWork);
		result.add(WorkTypeClassification.Shooting);
		return result;

	}

	/**
	 * 振休振出として扱う日数の内休暇系の勤務種類の日数を求める
	 * @param workTypAfternoon
	 * @param workTypeMorning
	 * @param workType
	 * @param numberOfDay
	 */
	private static TotalNumberOfDay getNumberOfHoliday(Optional<NumberOfDaySuspension> numberOfDay, WorkType workType,
			WorkTypeRemainInfor outputData) {

		TotalNumberOfDay result = new TotalNumberOfDay();
		numberOfDay.ifPresent(x -> {
			if (x.getClassifiction().equals(FuriClassifi.DRAWER)) {
				// 1日の休暇系日数合計＝0
				double totalHoliday = 0;
				// 午前の勤務種類が休暇系か
				if (isHasWorkClass(workType, WorkAtr.Monring)) {
					totalHoliday = totalHoliday + 0.5;
				}
				// 午後の勤務種類が休暇系か
				if (isHasWorkClass(workType, WorkAtr.Afternoon)) {
					totalHoliday = totalHoliday + 0.5;
				}
				// INPUT．振休振出として扱う．日数 = 1 and 1日の休暇系日数合計 = 1
				if (Double.compare(totalHoliday, 1) == 0 && Double.compare(x.getDays().v(), 1) == 0) {
					result.setTotalNumberMorning(0.5);
					result.setTotalNumberAfternoon(0.5);
				}

				if ((Double.compare(totalHoliday, 0.5) == 0 && Double.compare(x.getDays().v(), 1) == 0)
						|| (Double.compare(totalHoliday, 1) == 0 && Double.compare(x.getDays().v(), 0.5) == 0)) {
					boolean isMorning = isHasWorkClass(workType, WorkAtr.Monring);
					boolean isAfternoon = isHasWorkClass(workType, WorkAtr.Afternoon);

					if ((isMorning && !isAfternoon) || (isMorning && isAfternoon)) {
						result.setTotalNumberMorning(0.5);
						result.setTotalNumberAfternoon(0);
					}
					if ((!isMorning && isAfternoon)) {
						result.setTotalNumberMorning(0);
						result.setTotalNumberAfternoon(0.5);
					}

				}

				// 1日の休暇系日数合計 = 0
				if (Double.compare(totalHoliday, 0) == 0) {
					result.setTotalNumberMorning(0);
					result.setTotalNumberAfternoon(0);
				}

			} else {
				if (x.getClassifiction().equals(FuriClassifi.SUSPENSION)) {
					result.setTotalNumberAfternoon(x.getDays().v());
					result.setTotalNumberMorning(x.getDays().v());
				}
			}
		});
		return result;
	}

	/**
	 * 残数発生使用明細を作成する
	 *
	 * @param require
	 * @param cid
	 *            会社ID
	 * @param workType
	 *            勤務種類
	 * @param day
	 *            日数
	 * @param workAtr
	 * @param dataOutput
	 * @param timedigOpt
	 *            時間消化使用情報
	 * @param furiClassifi
	 *            振休振出として扱う区分
	 * @param usedDays
	 *            振休振出として扱う日数
	 * @return
	 */
	public static WorkTypeRemainInfor createWithOneDayWorkType(RequireM9 require, String cid, WorkType workType, WorkAtr workAtr,
			double day, WorkTypeRemainInfor dataOutput, Optional<TimeDigestionUsageInfor> timedigOpt,
			Optional<NumberOfDaySuspension> numberOfDayOpt, String wkTypeCd, Optional<String> wkTimeCd, String sid,
			GeneralDate ymd) {

		WorkTypeRemainInfor result = new WorkTypeRemainInfor(dataOutput.getWorkTypeCode(),
				dataOutput.getWorkTypeClass(), dataOutput.getCreateData(), createDetailData(),
				dataOutput.getSpeHolidayDetailData(), dataOutput.getChildCareDetailData());

		WorkTypeClassification wkClasssifi =workType.getDailyWork().getWorkTypeClassification(workAtr); 
		result.setWorkTypeClass(wkClasssifi);
		//アルゴリズム「残数発生使用対象の勤務種類の分類かを判定」を実行する
		JudgmentTypeOfWorkType judmentType = judgmentType(wkClasssifi, numberOfDayOpt);
		if(judmentType == JudgmentTypeOfWorkType.REMAINOCCNOTCOVER) {
			return result;
		}

		//振休振出として扱う日数をチェックする
		if(numberOfDayOpt.isPresent()) {
			checkNumberDays(cid, workAtr,numberOfDayOpt.get(), workType, result);
		}
		List<WorkTypeSet> workTypeSetList = workType.getWorkTypeSetList();


		//勤務種類の分類をチェックする
		switch (wkClasssifi) {
		case Absence:
		case SpecialHoliday:
			// INPUT.勤務種類の分類＝特別休暇 or 欠勤
			// 特休使用明細を設定する
			setSpecialHoliday(require, cid, day, workTypeSetList, workAtr, wkClasssifi, result);
	
			// 子の看護介護の残数発生使用明細を設定する
			setCare(require, cid, day, workTypeSetList, result);
			break;

		case Holiday:
		case Pause:
		case Shooting:
			// 公休の残数発生使用明細を設定する
			setNumberHolidays(cid, workType, workAtr, result, day);
			if (wkClasssifi.equals(WorkTypeClassification.Pause)
					|| wkClasssifi.equals(WorkTypeClassification.Shooting)) {
				// 勤務種類の分類に対応する残数発生使用明細を設定する
				setData(result, day, wkClasssifi);
			}
			break;

		case TimeDigestVacation:
			// 時間休暇使用時間詳細を設定する
			setTimeVacation(timedigOpt, result);
			// 勤務種類の分類に対応する残数発生使用明細を設定する
			setData(result, day, wkClasssifi);
			break;
		case SubstituteHoliday:
			// 時間代休の時間を作成する
			CreateTimeOff(require, cid, sid, ymd, wkTypeCd, wkTimeCd, wkClasssifi, result);
			// 勤務種類の分類に対応する残数発生使用明細を設定する
			setData(result, day, wkClasssifi);
			break;
		default:
			// 勤務種類の分類に対応する残数発生使用明細を設定する
			setData(result, day, result.getWorkTypeClass());
			break;
			}

		return result;
	}

	/**
	 * 時間代休の時間を作成
	 * @param require
	 *
	 * @param cid
	 * @param sid
	 * @param ymd
	 * @param wkTypeCd
	 * @param wkTimeCd
	 * @param wkClasssifi
	 * @param result
	 */
	private static void CreateTimeOff(RequireM9 require, String cid, String sid, GeneralDate ymd, String wkTypeCd,
			Optional<String> wkTimeCd, WorkTypeClassification wkClasssifi, WorkTypeRemainInfor result) {
		// 就業時間帯コードを判断
		String workTimeCode = determineWorkTimeCode(require, cid, sid, ymd, wkTypeCd, wkTimeCd);
		// 所定時間帯を取得
		// 所定時間を取得
		Integer predTime = WorkTimeIsFluidWork.getTimeByWorkTimeTypeCode(require, workTimeCode, wkTypeCd);
		// 取得した時間を設定
		List<OccurrenceUseDetail> occurrenceDetailData = result.getOccurrenceDetailData();
		occurrenceDetailData.forEach(x -> {
			if (x.getWorkTypeAtr().equals(WorkTypeClassification.SubstituteHoliday)) {
				x.setSubstituteHolidayTime(Optional.ofNullable(new AttendanceTime(predTime)));
			}
		});
		result.setOccurrenceDetailData(occurrenceDetailData);
	}

	/**
	 * 就業時間帯コードを判断
	 * @param require
	 *
	 * @param cid
	 * @param sid
	 * @param ymd
	 * @param wkTypeCd
	 * @param wkTimeCd
	 * @return 就業時間帯コード
	 */
	private static String determineWorkTimeCode(RequireM8 require, String cid, String sid, GeneralDate ymd, String wkTypeCd,
			Optional<String> wkTimeCd) {
		//就業時間帯コードがあるか
		if(wkTimeCd.isPresent()){
			return wkTimeCd.get();
		}
		// 出勤時の勤務情報を取得する

		return require.getHolidayWorkScheduleNew(cid, sid, ymd, wkTypeCd,WorkingDayCategory.workingDay)
				.map(x -> x.getWorkTimeCodeNotNull().map(y->y.v()).orElse(null)).orElse(null);

	}
	
	/**
	 * 特休使用明細を設定する
	 * @param require
	 * @param cid 会社ID
	 * @param day　日数
	 * @param workTypeSetList　勤務種類
	 * @param workAtr　workAtr
	 * @param wkClasssifi 勤務種類の分類
	 * @param dataOutput　勤務種類別残数情報
	 */
	private static void setSpecialHoliday(RequireM8 require, String cid, double day, List<WorkTypeSet> workTypeSetList,
			WorkAtr workAtr, WorkTypeClassification wkClasssifi, WorkTypeRemainInfor dataOutput){
		
		List<SpecialHolidayUseDetail> lstSpeUseDetail = new ArrayList<>(dataOutput.getSpeHolidayDetailData());
		
		workTypeSetList.stream().filter(x -> x.getWorkAtr().equals(workAtr)).findFirst().ifPresent(x -> {

			if(wkClasssifi.equals(WorkTypeClassification.Absence)){
				List<Integer> absenceSpecialCd = require.getAbsenceNumber(cid, x.getSumAbsenseNo());
				if (!absenceSpecialCd.isEmpty()) {
					SpecialHolidayUseDetail detailData = new SpecialHolidayUseDetail(absenceSpecialCd.get(0), day);
					lstSpeUseDetail.add(detailData);
				}
			}else if(wkClasssifi.equals(WorkTypeClassification.SpecialHoliday)){
				List<Integer> holidaySpecialCd = require.getSpecialHolidayNumber(cid, x.getSumSpHodidayNo());
				if (!holidaySpecialCd.isEmpty()) {
					SpecialHolidayUseDetail detailData = new SpecialHolidayUseDetail(holidaySpecialCd.get(0), day);
					lstSpeUseDetail.add(detailData);
				}
			}
		});
		dataOutput.setSpeHolidayDetailData(lstSpeUseDetail);
	}

	/**
	 * 子の看護介護の残数発生使用明細を設定する
	 *
	 * @param dataOutput
	 * @param cid
	 *            会社ID
	 * @param workTypeSetList
	 *            一日の勤務
	 * @param day
	 *            日数
	 * @param require
	 */
	private static void setCare(RequireM8 require, String cid, double day, List<WorkTypeSet> workTypeSetList,
			WorkTypeRemainInfor dataOutput) {

		workTypeSetList.forEach(x -> {
			// 子の看護介護の勤務種類か判断する

			CheckCareResult checkResult = require.checkCare(x, cid);
			if (checkResult.isCare()) {
				dataOutput.getChildCareDetailData().add(new CareUseDetail(checkResult.getCareType(), day));
			}
		});

	}

	/**
	 * 公休の残数発生使用明細を設定する
	 *
	 * @param cid
	 * @param workType
	 * @param workAtr
	 * @param dataOutput
	 * @param day
	 */
	private static void setNumberHolidays(String cid, WorkType workType, WorkAtr workAtr,
			WorkTypeRemainInfor dataOutput, double day) {
		//公休消化するか確認
		if(checkIsHoliday(workType, workAtr)){
			dataOutput.getOccurrenceDetailData().forEach(x -> {
				if (x.getWorkTypeAtr().equals(WorkTypeClassification.Holiday)) {
					x.setUseAtr(true);
					x.setDays(day);
				}
			});
		}
	}

	/**
	 * 時間休暇使用時間明細を設定する
	 *
	 * @param timedigOpt
	 *            時間消化使用情報
	 * @param dataOutput
	 */
	private static void setTimeVacation(Optional<TimeDigestionUsageInfor> timedigOpt, WorkTypeRemainInfor dataOutput) {
		dataOutput.getOccurrenceDetailData().forEach(x -> {
			x.setVacationUsageTimeDetails(createVacationUsageTimeDetails(timedigOpt));
		});
	}

	private static List<VacationUsageTimeDetail> createVacationUsageTimeDetails(
			Optional<TimeDigestionUsageInfor> timedigOpt) {

		List<VacationUsageTimeDetail> result = new ArrayList<VacationUsageTimeDetail>();

		result.add(new VacationUsageTimeDetail(HolidayType.ANNUAL, timedigOpt.map(x -> x.getNenkyuTime()).orElse(0),
				Optional.empty()));

		result.add(new VacationUsageTimeDetail(HolidayType.SUBSTITUTE, timedigOpt.map(x -> x.getKyukaTime()).orElse(0),
				Optional.empty()));

		result.add(new VacationUsageTimeDetail(HolidayType.SIXTYHOUR,
				timedigOpt.map(x -> x.getHChoukyuTime()).orElse(0), Optional.empty()));

		result.add(new VacationUsageTimeDetail(HolidayType.CHILDCARE,
				timedigOpt.map(x -> x.getChildCareTime()).orElse(0), Optional.empty()));

		result.add(new VacationUsageTimeDetail(HolidayType.CARE, timedigOpt.map(x -> x.getLongCareTime()).orElse(0),
				Optional.empty()));

		return result;
	}

	/**
	 * 振休振出として扱う日数をチェックする - thực ra hàm này là hàm set nhưng tiếng nhật tên như
	 * vậy nên cứ để như thế
	 *
	 * @param cid
	 *            会社ID
	 * @param workClass
	 *            勤務種類の分類
	 * @param workAtr
	 * @param furiClassifi
	 *            振休振出として扱う区分
	 * @param usedDays
	 *            振休振出として扱う日数
	 * @param workType
	 * @param dataOutput
	 *            勤務種類別残数情報
	 */
	private static void checkNumberDays(String cid, 
			WorkAtr workAtr, NumberOfDaySuspension numberOfDayOpt, WorkType workType, WorkTypeRemainInfor dataOutput) {

		if (numberOfDayOpt.isSuspension() && numberOfDayOpt.isUseable()
				&& workType.getDailyWork().getWorkTypeClassification(workAtr).isHolidayWork()) {
			// 区分 ＝ 振休
			dataOutput.getOccurrenceDetailData().forEach(x -> {
				if (x.getWorkTypeAtr().equals(WorkTypeClassification.Pause)) {
					x.setUseAtr(true);
					x.setDays(numberOfDayOpt.getDays().v());
				}
			});
		}

		if (numberOfDayOpt.isDrawer() && numberOfDayOpt.isUseable() && isHasWorkClass(workType, workAtr)) {
			// 区分 = 振出
			dataOutput.getOccurrenceDetailData().forEach(x -> {
				if (x.getWorkTypeAtr().equals(WorkTypeClassification.Shooting)) {
					x.setUseAtr(true);
					x.setDays(numberOfDayOpt.getDays().v());
				}
			});

		}

	}

	/**
	 * 勤務種類が休暇系か判断
	 *
	 * @param workAtr
	 *            workAtr
	 * @param workType
	 *            勤務種類の分類
	 * @param wkClass
	 *            勤務種類の分類
	 */

	private static boolean isHasWorkClass(WorkType workType, WorkAtr workAtr) {

		// 勤務種類が休暇系か
		val wkClass = workType.getDailyWork().getWorkTypeClassification(workAtr);
		if (wkClass.isHolidayWorkType()) {
			// 勤務種類 = 休暇系
			if (wkClass.isHoliday()) {
				// 勤務種類 = 休日
				// 公休かチェック
				return decideDigestHolidays(workType, workAtr);
			}
			// その他
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 公休消化するか判断
	 *
	 * @param workType
	 * @param workAtr
	 * @return
	 */
	private static boolean decideDigestHolidays(WorkType workType, WorkAtr workAtr) {

		if (workAtr.equals(WorkAtr.OneDay)) {
			// 公休消化するか判断（１日）
			return checkIsHoliday(workType, WorkAtr.OneDay);
		}

		return checkIsHoliday(workType, WorkAtr.Monring) || checkIsHoliday(workType, WorkAtr.Afternoon);

	}

	/**
	 * 公休消化するか確認
	 *
	 * @param workType
	 * @param workAtr
	 * @return
	 */
	private static boolean checkIsHoliday(WorkType workType, WorkAtr workAtr) {

		Optional<HolidayAtr> holidays = workType.getWorkTypeSetByAtr(workAtr).map(x -> x.getHolidayAtr());

		if (!holidays.isPresent()) {
			return false;
		}
		
		return workType.getWorkTypeSetByAtr(workAtr).map(
						x -> x.getDigestPublicHd().equals(WorkTypeSetCheck.CHECK))
				.orElse(false);
	}

	/**
	 * 実績から代休振替情報を作成する
	 * @param recordData
	 * @param dayOffTimeIsUse 時間代休利用
	 * @return
	 */
	public static Optional<DayoffTranferInfor> createDayOffTranferFromRecord(RequireM9 require, String cid, String sid, GeneralDate date, 
			CreateAtr createAtr, RecordRemainCreateInfor recordData,
			boolean dayOffTimeIsUse) {
		
		//代休振替情報を作成する
		Optional<WorkType> workType = require.workType(cid, recordData.getWorkTypeCode());
		if(!workType.isPresent() || !workType.get().isSubstituteHolidayOccurs()) {
			return Optional.empty();
		}
		
		//アルゴリズム「代休振替時間を算出する」を実行する
		Optional<TranferTimeInfor> tranferBreakTime = Optional.empty();
		Optional<TranferTimeInfor> tranferOvertime = Optional.empty();
		if (workType.get().isHolidayWork()) {
			tranferBreakTime = calDayoffTranferTime(require, cid, sid, date, createAtr,
					recordData.getWorkTimeCode(), recordData.getTransferTotal(), DayoffChangeAtr.BREAKTIME);
		} else {
			// アルゴリズム「実績から振替残業時間を作成する」を実行する
			tranferOvertime = calDayoffTranferTime(require, cid, sid, date, createAtr,
					recordData.getWorkTimeCode(), recordData.getTransferOvertimesTotal(), DayoffChangeAtr.OVERTIME);
		}
		
		if(tranferBreakTime.isPresent() || tranferOvertime.isPresent())
			return Optional.of(new DayoffTranferInfor(recordData.getWorkTimeCode(), tranferBreakTime, tranferOvertime));
		else
			return Optional.empty();
	}

	/**
	 * 残数作成元情報から暫定残数管理データを作成する
	 * @param inforData
	 * @return
	 */
	public static DailyInterimRemainMngData createDataInterimRemain(RequireM6 require, InforFormerRemainData inforData) {
		DailyInterimRemainMngData outputData = DailyInterimRemainMngData.createEmpty(inforData.getYmd());
		List<OccurrenceUseDetail> occList = inforData.getWorkTypeRemain().map(x -> x.getOccurrenceDetailData()).orElse(new ArrayList<>());

		occList.stream().filter(x -> x.getDays() > 0).forEach(x -> {
			WorkTypeClassification wkCls = x.getWorkTypeAtr();

			switch (wkCls) {
			// 振出
			case Shooting:
				TempRemainCreateEachData.createInterimRecData(require, inforData, wkCls, outputData);
				break;
			// 休日
			case Holiday:
				TempRemainCreateEachData.createInterimHolidayData(inforData, wkCls, outputData);
				break;
			// 振休
			case Pause:
				TempRemainCreateEachData.createInterimAbsData(inforData, wkCls, outputData);
				break;
			// 代休
			case SubstituteHoliday:
				TempRemainCreateEachData.createInterimDayOffData(inforData, wkCls, outputData);
				break;
			// 年休
			case AnnualHoliday:
				TempRemainCreateEachData.createInterimAnnualHoliday(inforData, wkCls, outputData);
				break;
			// 積立年休
			case YearlyReserved:
				TempRemainCreateEachData.createInterimReserveHoliday(inforData, wkCls, outputData);
				break;
			// 時間消化
			case TimeDigestVacation:
				TempRemainCreateEachData.createInterimDigestVacation(inforData, wkCls, outputData);
				break;

			default:
				break;
			}
		});

		//暫定残数管理データ(List)を作成する
		inforData.getWorkTypeRemain().ifPresent(workTypeInfor ->{
			WorkTypeClassification wkCls = workTypeInfor.getWorkTypeClass();
			// 各種暫定残数管理データを作成する

			List<CareUseDetail> cares = workTypeInfor.getChildCareDetailData();
			if (!cares.isEmpty()) {
				for (CareUseDetail care : cares) {
					switch (care.getCareType()) {
					// 子の看護
					case ChildNursing:
						TempRemainCreateEachData.createInterimChildNursing(inforData, care, wkCls, outputData);
						break;
					// 介護
					case Nursing:
						TempRemainCreateEachData.createInterimNursing(inforData, care, wkCls, outputData);
						break;
					}
				}
			}

			if (!workTypeInfor.getSpeHolidayDetailData().isEmpty()) {
				TempRemainCreateEachData.createInterimSpecialHoliday(inforData, wkCls, outputData, workTypeInfor);
			}
		});

		
		//残数作成元情報.代休振替 == emptyを確認
		if(inforData.getDayOffTranfer().isPresent()) {
			TempRemainCreateEachData.createInterimBreak(require, inforData, outputData);
		}

		//時間暫定残数管理データを作成する -> 作成された暫定残数管理データをListに追加する

		// 時間年休
		List<TempAnnualLeaveMngs> annual = TempRemainCreateEachData.createAnnualHolidayTime(inforData);
		outputData.getRecAbsData().addAll(annual);
		outputData.getAnnualHolidayData().addAll(annual);
		// 時間代休
		List<InterimDayOffMng> sub = TempRemainCreateEachData.createSubstituteHolidayTime(inforData);
		outputData.getRecAbsData().addAll(sub);
		outputData.getDayOffData().addAll(sub);
		// 時間特休
		List<InterimSpecialHolidayMng> special = TempRemainCreateEachData.createSpecialHolidayTime(inforData);
		outputData.getRecAbsData().addAll(special);
		outputData.getSpecialHolidayData().addAll(special);
		// 60H超休
		outputData.getRecAbsData().addAll(TempRemainCreateEachData.createHolidayOver60hTime(inforData));
		// 時間子の看護
		List<TempChildCareManagement> childCare = TempRemainCreateEachData.createChildCareTime(inforData);
		outputData.getRecAbsData().addAll(childCare);
		outputData.getChildCareData().addAll(childCare);
		// 時間介護
		List<TempCareManagement> care = TempRemainCreateEachData.createCareTime(inforData);
		outputData.getRecAbsData().addAll(care);
		outputData.getCareData().addAll(care);

		return outputData;
	}





	/**
	 * 予定から残数作成元情報を設定する
	 * @param scheData
	 * @param outputData
	 * @return
	 */
	public static Optional<RemainNumberWtSubsLeavTransInfo> createInterimDataFromSche(RequireM9 require, String cid, ScheRemainCreateInfor scheData, boolean dayOffTimeIsUse) {
		//アルゴリズム「勤務種類別残数情報を作成する」を実行する
		Optional<WorkTypeRemainInfor> remainInfor = createWorkTypeRemainInfor(require,
																			cid,
																			CreateAtr.SCHEDULE,
																			scheData.getWorkTypeCode(),
																			Optional.ofNullable(scheData.getTimeDigestionUsageInfor()),
																			scheData.getNumberDaySuspension(),
																			scheData.getWorkTimeCode(),
																			scheData.getSid(),
																			scheData.getYmd());

		//勤務種類別残数情報を設定する
		if(!remainInfor.isPresent()) {
			return Optional.empty();
		}
		//アルゴリズム「就業時間帯から代休振替情報を作成する」を実行する
		Optional<DayoffTranferInfor> tranferData = remainInfor.isPresent() ? createDayoffFromWorkTime(require, cid, scheData.getSid(),
				scheData.getYmd(), remainInfor.get().getWorkTypeCode(), scheData.getWorkTimeCode(), scheData.getTransferTotal(), CreateAtr.SCHEDULE, scheData.getTransferOvertimesTotal(), dayOffTimeIsUse) : Optional.empty();
		return Optional.of(new RemainNumberWtSubsLeavTransInfo(remainInfor, tranferData));
	}

	/**
	 * 就業時間帯から代休振替情報を作成する
	 *
	 * @param cid:             会社ID
	 * @param remainInfor
	 * @param workTimeCode
	 * @param isTranferTime:   True: 設定あり, False: 設定なし
	 * @param timeSetting:     休出振替可能時間
	 * @param timeOverSetting: 実績残業振替時間
	 * @param dayOffTimeIsUse: 時間代休利用
	 * @return
	 */
	public static Optional<DayoffTranferInfor> createDayoffFromWorkTime(RequireM5 require, String cid,
			String employeeId, GeneralDate date, String workTypeCode, Optional<String> workTimeCode,
			int timeSetting, CreateAtr createAtr, int timeOverSetting, boolean dayOffTimeIsUse) {

		//アルゴリズム「代休を発生させる勤務種類かを判定する」を実行する
		Optional<WorkType> workType = require.workType(cid, workTypeCode);
		if(!workType.isPresent() || !workType.get().isSubstituteHolidayOccurs()) {
			return Optional.empty();
		}
		
		//振替休出時間を作成する
		Optional<TranferTimeInfor> transferBreak = Optional.empty();
		Optional<TranferTimeInfor> transferOver= Optional.empty();
		
		if (workType.get().isHolidayWork()) {
			transferBreak = calDayoffTranferTime(require, cid, employeeId, date, createAtr, workTimeCode, timeSetting, DayoffChangeAtr.BREAKTIME);
		} else {
			// 振替残業時間を作成する
			transferOver = calDayoffTranferTime(require, cid, employeeId, date, createAtr, workTimeCode, timeOverSetting, DayoffChangeAtr.OVERTIME); 
		}
		if (transferBreak.isPresent() || transferOver.isPresent()) {
			return Optional.of(new DayoffTranferInfor(workTimeCode, transferBreak, transferOver));
		}

		return Optional.empty();
	}

	/**
	 * 代休振替時間を算出する(正しい処理) ※timeSettig = 振替後の時間
	 * @param workTimeCode
	 * @param timeSetting
	 * @param dayoffChange
	 * @return
	 */
	public static Optional<TranferTimeInfor> calDayoffTranferTime(
			RequireM4 require, String cid, String sid, GeneralDate date, CreateAtr createAtr,Optional<String> workTimeCode, int timeSetting, DayoffChangeAtr dayoffChange) {
		
		if(!CheckDateForManageCmpLeaveService.check(require, cid, sid, date)){
			return Optional.empty();
		}
		
		//アルゴリズム「代休振替設定を取得」を実行する
		Optional<SubHolTransferSet> optDayOffTranferSetting = GetSubHolOccurrenceSetting.process(require, cid,
				workTimeCode,
				dayoffChange == DayoffChangeAtr.BREAKTIME ? CompensatoryOccurrenceDivision.WorkDayOffTime
						: CompensatoryOccurrenceDivision.FromOverTime);
		if(!optDayOffTranferSetting.isPresent()) {
			return Optional.empty();
		}
		
		return Optional.of(new TranferTimeInfor(createAtr, timeSetting,
				optDayOffTranferSetting.get().getTransferDays(new AttendanceTime(timeSetting))));
	}

	/**
	 * 最新の勤務種類変更を伴う申請から残数作成元情報を設定する
	 * @param appInfor
	 * @param recordRemainCreateInfor
	 * @param outputData
	 * @param dayOffTimeIsUse
	 * @return
	 */
	public static Optional<RemainNumberWtSubsLeavTransInfo> createInterimDataFromApp(RequireM9 require, String cid, 
			String sid, GeneralDate date, InterimRemainCreateInfor createInfo, AppRemainCreateInfor appInfor, 
			boolean dayOffTimeIsUse, CreateAtr createAtr) {
		String workTypeCode = appInfor.getWorkTypeCode().map(x -> x).orElse("");

		//#116773
		Optional<NumberOfDaySuspension> numberDaySuspension = createInfo.getRecordData()
				.map(x -> x.getNumberDaySuspension())
				.orElse(createInfo.getScheData().map(x -> x.getNumberDaySuspension()).orElse(Optional.empty()));

		//アルゴリズム「勤務種類別残数情報を作成する」を実行する
		Optional<WorkTypeRemainInfor> remainInfor = createWorkTypeRemainInfor(require, cid, createAtr, workTypeCode,
				appInfor.getTimeDigestionUsageInfor(), numberDaySuspension ,
				appInfor.getWorkTimeCode(),appInfor.getSid(),appInfor.getAppDate());
		if(!remainInfor.isPresent()) {
			return Optional.empty();
		}

		//勤務種類別残数情報を設定する
		//申請種類をチェックする
		// 休日出勤申請から代休振替情報を作成する
		Optional<DayoffTranferInfor> tranferInforFromHoliday = createDayoffFromWorkTime(require, cid, sid, date,
				workTypeCode, appInfor.getWorkTimeCode(), appInfor.getAppBreakTimeTotal().orElse(0), createAtr,
				appInfor.getAppOvertimeTimeTotal().orElse(0), dayOffTimeIsUse);
		return Optional.of(new RemainNumberWtSubsLeavTransInfo(remainInfor, tranferInforFromHoliday));
	}


	/**
	 * 最新の勤務種類変更を伴う申請を抽出する
	 * @param lstAppData
	 * @return
	 */
	private static AppRemainCreateInfor getAppWithWorkType(List<AppRemainCreateInfor> lstAppData, String sid, GeneralDate baseDate){
		if(lstAppData.isEmpty()) {
			return null;
		}
		//残数関連の申請を抽出する
		List<AppRemainCreateInfor> lstAppInfor = lstAppData.stream()
				.filter(x -> x.getSid().equals(sid)
						&& x.getWorkTypeCode().isPresent()
						&& (x.getAppDate().equals(baseDate)
								|| (x.getStartDate().isPresent()
										&& x.getEndDate().isPresent()
										&& x.getStartDate().get().beforeOrEquals(baseDate)
										&& x.getEndDate().get().afterOrEquals(baseDate)
										)
								)
						)
				.collect(Collectors.toList());
		if(lstAppInfor.isEmpty()) {
			return null;
		}
		lstAppInfor = lstAppInfor.stream().sorted((a, b) -> a.getInputDate().compareTo(b.getInputDate()))
				.collect(Collectors.toList());
		AppRemainCreateInfor appInfor = lstAppInfor.get(lstAppInfor.size() - 1);
		return appInfor;

	}

	/**
	 * 残数発生使用対象の勤務種類の分類かを判定
	 * @param typeAtr
	 * @return
	 */
	private static JudgmentTypeOfWorkType judgmentType(WorkTypeClassification typeAtr, Optional<NumberOfDaySuspension> numberOfDayOpt) {
		if(typeAtr == WorkTypeClassification.Absence
				|| typeAtr == WorkTypeClassification.AnnualHoliday
				|| typeAtr == WorkTypeClassification.Pause
				|| typeAtr == WorkTypeClassification.SpecialHoliday
				|| typeAtr == WorkTypeClassification.SubstituteHoliday
				|| typeAtr == WorkTypeClassification.TimeDigestVacation
				|| typeAtr == WorkTypeClassification.YearlyReserved
				|| typeAtr == WorkTypeClassification.Holiday) {
			return JudgmentTypeOfWorkType.REMAIN;
		}
		
		if (typeAtr == WorkTypeClassification.Shooting) {
			return JudgmentTypeOfWorkType.REMAINOCC;
		}
		
		if(numberOfDayOpt.isPresent()) {
			return JudgmentTypeOfWorkType.REMAIN;
		}
		
		return JudgmentTypeOfWorkType.REMAINOCCNOTCOVER;
	}

	/**
	 * 勤務種類別残数情報を作成する
	 * @return
	 */
	private static List<OccurrenceUseDetail> createDetailData() {
		List<OccurrenceUseDetail> occurrenceDetailData = new ArrayList<>();
		OccurrenceUseDetail detailData = new OccurrenceUseDetail(0, false, WorkTypeClassification.AnnualHoliday);
		occurrenceDetailData.add(detailData);
		detailData = new OccurrenceUseDetail(0, false, WorkTypeClassification.YearlyReserved);
		occurrenceDetailData.add(detailData);
		detailData = new OccurrenceUseDetail(0, false, WorkTypeClassification.SubstituteHoliday);
		occurrenceDetailData.add(detailData);
		detailData = new OccurrenceUseDetail(0, false, WorkTypeClassification.Pause);
		occurrenceDetailData.add(detailData);
		detailData = new OccurrenceUseDetail(0, false, WorkTypeClassification.TimeDigestVacation);
		occurrenceDetailData.add(detailData);
		detailData = new OccurrenceUseDetail(0, false, WorkTypeClassification.Holiday);
		occurrenceDetailData.add(detailData);
		detailData = new OccurrenceUseDetail(0, false, WorkTypeClassification.HolidayWork);
		occurrenceDetailData.add(detailData);
		detailData = new OccurrenceUseDetail(0, false, WorkTypeClassification.Shooting);
		occurrenceDetailData.add(detailData);
		return occurrenceDetailData;
	}
  
	private static List<WorkTypeClassification> lstZansu(){
		List<WorkTypeClassification> lstOutput = new ArrayList<>();
		lstOutput.add(WorkTypeClassification.AnnualHoliday);
		lstOutput.add(WorkTypeClassification.YearlyReserved);
		lstOutput.add(WorkTypeClassification.SubstituteHoliday);
		lstOutput.add(WorkTypeClassification.Absence);
		lstOutput.add(WorkTypeClassification.Pause);
		lstOutput.add(WorkTypeClassification.TimeDigestVacation);
		lstOutput.add(WorkTypeClassification.HolidayWork);
		lstOutput.add(WorkTypeClassification.Shooting);
		lstOutput.add(WorkTypeClassification.SpecialHoliday);
		lstOutput.add(WorkTypeClassification.Holiday);
		return lstOutput;
	}
	/**
	 * 勤務種類の分類に対応する残数発生使用明細を設定する
	 * @param dataOutput
	 * @param days
	 * @param workTypeClass
	 * @return
	 */
	private static void setData(WorkTypeRemainInfor dataOutput, double days, WorkTypeClassification workTypeClass) {
		dataOutput.setOccurrenceDetailData(dataOutput.getOccurrenceDetailData().stream().map(x -> {
			if (x.getWorkTypeAtr().equals(workTypeClass)) {
				OccurrenceUseDetail useDetail = new OccurrenceUseDetail(days, true, x.getWorkTypeAtr());
				useDetail.setVacationUsageTimeDetails(x.getVacationUsageTimeDetails());
				useDetail.setSubstituteHolidayTime(x.getSubstituteHolidayTime());
				return useDetail;
			}
			return x;
		}).collect(Collectors.toList()));
	}

	public static interface RequireM9 extends RequireM6, RequireM7 {

	}

	public static interface RequireM8 {

		List<Integer> getSpecialHolidayNumber(String cid, int sphdSpecLeaveNo);
		
		List<Integer> getAbsenceNumber(String cid, int absenseNo);

		CheckCareResult checkCare(WorkTypeSet wkSet, String cid);

		Optional<WorkInformation> getHolidayWorkSchedule(String companyId, String employeeId, GeneralDate baseDate, String workTypeCode);
		
		Optional<WorkInformation> getHolidayWorkScheduleNew(String companyId, String employeeId, GeneralDate baseDate, String workTypeCode, WorkingDayCategory workingDayCategory);
		
	}

	public static interface RequireM7 extends RequireM1, RequireM3 {

	}

	public static interface RequireM6 extends TempRemainCreateEachData.RequireM1, RequireM1, RequireM5 {
	}

	public static interface RequireM5 extends WorkTimeIsFluidWork.RequireM1, RequireM4 {
	}

	public static interface RequireM4 extends GetDesignatedTime.RequireM2, GetSubHolOccurrenceSetting.Require, CheckDateForManageCmpLeaveService.Require {
	}

	public static interface RequireM3 extends RequireM2, RequireM5 {
	}

	public static interface RequireM2 extends WorkingConditionService.RequireM1 {
	}


	public static interface RequireM1 extends RequireM8 {

		Optional<WorkType> workType(String companyId, String workTypeCd);
	}
}
