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
 * 社員の子の看護介護残数詳細情報を取得
 * UKDesign.UniversalK.就業.KDL_ダイアログ.残数確認ダイアログ共通.子の看護・介護.アルゴリズム.社員の子の看護介護残数詳細情報を取得
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
	 * @param companyId       会社ID
	 * @param employeeId      社員ID
	 * @param nursingCategory 介護看護区分
	 */
	public NursingAndChildNursingRemainDto get(String companyId, String employeeId, NursingCategory nursingCategory) {

		String KDL051_33 = TextResource.localize("KDL051_33");
		String KDL051_31 = TextResource.localize("KDL051_31");
		String KDL051_32 = TextResource.localize("KDL051_32");

		NursingAndChildNursingRemainDto dataResult = new NursingAndChildNursingRemainDto();

		// 子の看護・介護管理区分を取得する
		AcquireNursingAndChildNursingDto acquireNursingAndChildNursingDto = acquireNursingAndChildNursing.get(companyId,
				employeeId, nursingCategory);

		// 取得した管理区分をチェック
		if (acquireNursingAndChildNursingDto.isManagementSection()) {

			// 管理期間を計算する
			DatePeriod period = acquireNursingAndChildNursingDto.getNursingLeaveSetting().get()
					.calcManagementPeriod(GeneralDate.today());
			ChildCareNurseRequireImpl require = childCareNurseRequireImplFactory.createRequireImpl();
			// 期間の上限日数を取得する
			ChildCareNurseUpperLimitPeriod childCareNurseUpperLimitPeriod = acquireNursingAndChildNursingDto
					.getNursingCareLeaveRemainingInfo().get()
					.childCareNurseUpperLimitPeriod(companyId, employeeId, period, GeneralDate.today(), require).get(0);

			List<TempChildCareNurseManagement> listTempChildCareNurseManagement = new ArrayList<>();

			AggrResultOfChildCareNurse aggrResultOfChildCareNurse = null;
			if (nursingCategory == NursingCategory.ChildNursing) {// 子の看護

				// DomainService「基準日時点の子の看護残数を取得する」を実行する
				aggrResultOfChildCareNurse = getRemainingNumberChildCare.getRemainingNumberChildCare(companyId,
						employeeId, GeneralDate.today());
				
				GetUsageDetailChildCareService.Require reuqireGetUsageDetailChildCareService = new GetUsageDetailChildCareServiceImpl(requireService);
				// 「DS_期間内の子の看護使用明細を取得する」を実行する
				List<TempChildCareManagement> listTempChildCareManagement = GetUsageDetailChildCareService
						.getUsageDetailCareService(companyId, employeeId, period, ReferenceAtr.APP_AND_SCHE,
								reuqireGetUsageDetailChildCareService);
				listTempChildCareNurseManagement = listTempChildCareManagement.stream().map(c-> (TempChildCareNurseManagement) c).collect(Collectors.toList());

			} else if (nursingCategory == NursingCategory.Nursing) {// 介護

				// DomainService「基準日時点の介護残数を取得する」を実行する
				aggrResultOfChildCareNurse = getRemainingNumberNursing.getRemainingNumberNursing(companyId, employeeId,
						GeneralDate.today());

				GetUsageDetailCareService.Require requireGetUsageDetailCareService = new GetUsageDetailCareServiceImpl(requireService);
				// DomainService「DS_期間内の介護使用明細を取得する」
				List<TempCareManagement> listTempCareManagement = GetUsageDetailCareService.getUsageDetailCareService(
						companyId, employeeId, period, ReferenceAtr.APP_AND_SCHE, requireGetUsageDetailCareService);
				listTempChildCareNurseManagement = listTempCareManagement.stream().map(c-> (TempChildCareNurseManagement) c).collect(Collectors.toList());
				
			}
			

			// List＜消化詳細＞を作成
			List<DigestionDetails> listDigestionDetails = new ArrayList<>();
			for (TempChildCareNurseManagement tempChildCareNurseManagement : listTempChildCareNurseManagement) {
				
				
				// ・管理データ．使用数．日数　＞　０　の場合
				if (tempChildCareNurseManagement.getUsedNumber().getUsedDay().v() > 0.0) {
					DigestionDetails usedDay = new DigestionDetails();
					//管理データ．暫定残数管理データ．年月日　＞　システム日付
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
				// ・管理データ．使用数．時間　＞　０　の場合
				if (tempChildCareNurseManagement.getUsedNumber().getUsedTimes().isPresent() && tempChildCareNurseManagement.getUsedNumber().getUsedTimes().get().v() > 0) {
					DigestionDetails usedTime = new DigestionDetails();
					//管理データ．暫定残数管理データ．年月日　＞　システム日付
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
			// 看護・介護残数DTOを作成
			// set 年間上限数
			// 取得した子の看護介護休暇集計結果．起算日からの休暇情報．本年．残数．日数
			Double limitDays = (double) aggrResultOfChildCareNurse.getStartdateDays().getThisYear().getRemainingNumber().getRemainDay().v(); //＃121581
			String v1 ="";
			String v2 ="";
			//取得した介護看護休暇設定．時間介護看護設定．管理区分
			if(acquireNursingAndChildNursingDto.getNursingLeaveSetting().get().getTimeCareNursingSetting().getManageDistinct() == ManageDistinct.YES ) {
				v1 = TextResource.localize("KDL051_31");
				//子の看護介護休暇集計結果．起算日からの休暇情報．本年．残数．時間、
				v2 = convertTime(aggrResultOfChildCareNurse.getStartdateDays().getThisYear().getRemainingNumber().getRemainTimes().isPresent()? 
						aggrResultOfChildCareNurse.getStartdateDays().getThisYear().getRemainingNumber().getRemainTimes().get().v():0);
				
			}
			String KDL051_30 = TextResource.localize("KDL051_30", limitDays.toString(), v1, v2);
			dataResult.setMaxNumberOfYear(KDL051_30);
			// set 上限制限開始日
			dataResult.setUpperLimitStartDate(childCareNurseUpperLimitPeriod.getPeriod().start().toString());
			// set 上限制限終了日
			dataResult.setUpperLimitEndDate(childCareNurseUpperLimitPeriod.getPeriod().end().toString());
			// set 上限日数
			String KDL051_34 = TextResource.localize("KDL051_34", Double.valueOf(aggrResultOfChildCareNurse.getStartdateDays().getThisYear().getLimitDays().v()).toString());
			dataResult.setMaxNumberOfDays(KDL051_34);
			// set 使用数
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
			// set 消化詳細一覧
			dataResult.setListDigestionDetails(listDigestionDetails);
		}

		// 看護・介護残数DTO．管理区分 に 取得した介護看護休暇設定．管理区分をセット
		// set 上限日数
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
			return "月";
		case 2:
			return "火";
		case 3:
			return "水";
		case 4:
			return "木";
		case 5:
			return "金";
		case 6:
			return "土";
		default:
			return "日";
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
