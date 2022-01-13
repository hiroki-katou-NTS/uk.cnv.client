package nts.uk.ctx.at.request.app.find.dialog.suspensionholiday;
/**
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL029_積休ダイアログ.アルゴリズム.社員の積休残数詳細情報を取得.積休残数詳細情報を作成.積休残数詳細情報を作成
 * @author phongtq
 *
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.app.find.dialog.annualholiday.AnnualAccumulatedHoliday;
import nts.uk.ctx.at.request.app.find.dialog.annualholiday.InforAnnualHolidaysAccHolidayDto;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.rsvleamanager.rsvimport.RsvLeaCriterialDate;
import nts.uk.shr.com.i18n.TextResource;
@Stateless
public class CreateInforNumberRemainDays {
	public InforAnnualHolidaysAccHolidayDto createInforNumberRemainDays(Optional<RsvLeaCriterialDate> stock) {
		// 年休・積休残数詳細情報DTOを作成
		InforAnnualHolidaysAccHolidayDto accHolidayDto = new InforAnnualHolidaysAccHolidayDto(new ArrayList<>(), new ArrayList<>(), 
				false, "", "", "", false, "", "");
		
		// 年休・積休残数詳細情報DTO．現時点残数をセット
		// 現時点残数　＝　Input．基準日時点積立年休残数．積立年休残日数　+　＃KDL020_14
		accHolidayDto.setCurrentRemainNum(stock.get().getRemainingDays().toString() + TextResource.localize("KDL020_14"));
		
		// List＜年休・積休残数詳細＞を作成
		List<AnnualAccumulatedHoliday> lstRemainAnnAccHoliday = new ArrayList<>();
		
		// Input．基準日時点積立年休残数．積立年休付与残数データをチェック
		if (!stock.get().getGrantRemainingList().isEmpty()) {
			stock.get().getGrantRemainingList().forEach(x -> {
				
				// ・付与日　＝　ループ中の休暇付与残数データ．付与日
				String grandDate = x.getGrantDate();
				
				// ・付与数　＝　ループ中の休暇付与残数データ．明細．付与数．日数　+　＃KDL020_14
				String numberGrants = x.getGrantNumber().toString() + TextResource.localize("KDL020_14");
				
				// ・使用数　＝　ループ中の休暇付与残数データ．明細．使用数．日数　+　＃KDL020_14
				String numberOfUse = x.getUsedNumber() + TextResource.localize("KDL020_14");
				
				// ・残数　＝　ループ中の休暇付与残数データ．明細．残数．日数　+　＃KDL020_14
				String numberOfRemain = x.getRemainingNumber() + TextResource.localize("KDL020_14");
				
				// ・有効期限　＝　#KDL029_42 - 内容：　{0}({1})
				// {0} =  ループ中の休暇付与残数データ．期限日, {1} =  ループ中の休暇付与残数データ．期限日の曜日
				GeneralDate deadLine = GeneralDate.fromString(x.getDeadline(), "yyyy/MM/dd");
				String textDeadLine = this.getDayOfJapan(deadLine.dayOfWeek());
				String dateOfExpiry = TextResource.localize("KDL029_42", x.getDeadline(), textDeadLine);
				
				// 年休・積休残数詳細を作成
				AnnualAccumulatedHoliday annualHoliday = new AnnualAccumulatedHoliday(numberGrants, grandDate, 
						numberOfUse, dateOfExpiry, numberOfRemain);
				
				lstRemainAnnAccHoliday.add(annualHoliday);

			});
			accHolidayDto.setLstRemainAnnAccHoliday(lstRemainAnnAccHoliday);
		}
		
		return accHolidayDto;
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
