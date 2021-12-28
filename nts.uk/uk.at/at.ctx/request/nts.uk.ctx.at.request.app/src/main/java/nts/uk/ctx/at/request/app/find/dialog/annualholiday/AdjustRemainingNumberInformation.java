package nts.uk.ctx.at.request.app.find.dialog.annualholiday;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import nts.uk.ctx.at.request.dom.application.annualholiday.ReNumAnnLeaReferenceDateExport;
import nts.uk.shr.com.i18n.TextResource;

/**
 * 残数情報を調整
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL020_年休ダイアログ.アルゴリズム.社員の年休残数詳細情報を取得.残数情報を調整
 * @author phongtq
 *
 */
@Stateless
public class AdjustRemainingNumberInformation {
	public InforAnnualHolidaysAccHolidayDto adjustRemainingNumberInformation(ReNumAnnLeaReferenceDateExport reNumAnnLeave, boolean suspensionTimeYearFlg) {
		// 年休残数詳細情報DTOを作成
		InforAnnualHolidaysAccHolidayDto accHolidayDto = new InforAnnualHolidaysAccHolidayDto(new ArrayList<>(), new ArrayList<>(), false, "", "", "", false, "", "");
		
		// 年休残数詳細情報DTO．現時点残数　＝　Input．基準日時点年休残数．年休残日数　+　＃KDL020_14　+　Input．時間年休管理区分(true, false) == True　：　Input．基準日時点年休残数．年休残時間　：　Empty
		String annualLeaveGrantDay = reNumAnnLeave.getRemainingDays().toString() + TextResource.localize("KDL020_14");
		String annualLeaveGrantTime = suspensionTimeYearFlg ?
				this.getHoursMinu(reNumAnnLeave.getRemainingTime().intValue()) : ""; // NOTE：Input．基準日時点年休残数．年休残時間がある場合Input．基準日時点年休残数．年休残時間を交換する  例：120　ー＞　2:00
		// 年休残数詳細情報DTO．現時点残数をセット
		accHolidayDto.setCurrentRemainNum(annualLeaveGrantDay + annualLeaveGrantTime);
		
		
		// 年休残数詳細情報DTO．時間年休の年間上限時間　＝　＃KDL020_65 - 内容：{0}利用可
		//　{0}　＝　Input．基準日時点年休残数．年休情報．上限データ.上限残時間
		int annMaxTime = reNumAnnLeave.getAnnualLeaveRemainNumberExport().
				getAnnualLeaveMaxDataExport().getTimeAnnualLeaveMaxremainingMinutes().intValue();
		String annMaxTimeText = this.getHoursMinu(annMaxTime);
		
		// 年休残数詳細情報DTO．時間年休の年間上限時間をセット
		accHolidayDto.setAnnMaxTime(TextResource.localize("KDL020_65", annMaxTimeText));
		
		// List＜年休残数詳細＞を作成
		List<AnnualAccumulatedHoliday> lstRemainAnnAccHoliday = new ArrayList<>();
		
		// 取得した基準日時点年休残数．年休付与残数データをチェック 
		if (!reNumAnnLeave.getAnnualLeaveGrantExports().isEmpty()) {
			reNumAnnLeave.getAnnualLeaveGrantExports().forEach(x -> {
				// 年休残数詳細を作成
				AnnualAccumulatedHoliday remainAnnAccHoliday = new AnnualAccumulatedHoliday("", "", "", "", "");
				
				//　{0}　＝　ループ中の休暇付与残数データ．付与日
				String grantDate = x.getGrantDate().toString();
				//　{1}　＝　ループ中の休暇付与残数データ．付与日　の曜日
				String grantDateText = this.getDayOfJapan(x.getGrantDate().dayOfWeek());
				// 年休残数詳細．付与日をセット - 内容：{0}({1})
				remainAnnAccHoliday.setGrandDate(TextResource.localize("KDL020_64", grantDate, grantDateText));
				
				// 年休残数詳細．付与数　＝　ループ中の休暇付与残数データ．付与日数 +　＃KDL020_66　+　Input．時間年休管理区分(true, false) == True ? ループ中の休暇付与残数データ．付与時間 : Empty
				String grantNumber = (x.getGrantNumber().toString() + TextResource.localize("KDL020_66")) + 
						(suspensionTimeYearFlg ? this.getHoursMinu(x.getGrantNumberMinutes().intValue()) : "");
				// 年休残数詳細．付与数をセット
				remainAnnAccHoliday.setNumberGrants(grantNumber);
				
				// 年休残数詳細．使用数　＝　ループ中の休暇付与残数データ．使用日数 +　＃KDL020_66　+　　Input．時間年休管理区分(true, false) == True ? 　ループ中の休暇付与残数データ．使用時間
				// NOTE：ループ中の休暇付与残数データ．使用時間　がある場合交換する - 例：120　ー＞2:00
				String numberOfUse = (x.getDaysUsedNo().toString() + TextResource.localize("KDL020_66")) + 
						(suspensionTimeYearFlg ? this.getHoursMinu(x.getUsedMinutes().intValue()) : "");

				// 年休残数詳細．使用数をセット
				remainAnnAccHoliday.setNumberOfUse(numberOfUse);
				
				// 年休残数詳細．残数を　＝　ループ中の休暇付与残数データ．残日数　 +　＃KDL020_66　+　　Input．時間年休管理区分(true, false) == True ? 　ループ中の休暇付与残数データ．数時間
				// NOTE：ループ中の休暇付与残数データ．数時間　がある場合交換する - 例：120　ー＞2:00
				String numberOfRemain = (x.getRemainDays().toString() + TextResource.localize("KDL020_66")) + 
						(suspensionTimeYearFlg ? this.getHoursMinu(x.getRemainMinutes().intValue()) : "");
				// 年休残数詳細．残数をセット
				remainAnnAccHoliday.setNumberOfRemain(numberOfRemain);
				
				// 年休残数詳細．有効期限　＝　＃KDL020_64 - 内容：{0}({1})
				//　{0}　＝　ループ中の休暇付与残数データ．期限日
				//　{1}　＝　ループ中の休暇付与残数データ．期限日　の曜日
				String dateOfExpiry = TextResource.localize("KDL020_64", x.getDeadline().toString(), this.getDayOfJapan(x.getDeadline().dayOfWeek()));
				
				// 年休残数詳細．有効期限をセット
				remainAnnAccHoliday.setDateOfExpiry(dateOfExpiry);
				
				lstRemainAnnAccHoliday.add(remainAnnAccHoliday);
			});
			
			accHolidayDto.setLstRemainAnnAccHoliday(lstRemainAnnAccHoliday);
		}
		
		return accHolidayDto;
	}
	
	private String getHoursMinu(int time) {
		String minu = String.valueOf(time % 60).length() > 1 ? String.valueOf(time % 60) : 0 + String.valueOf(time % 60);
		String result = String.valueOf(time / 60) + ":" + minu;
		return result;
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
}
