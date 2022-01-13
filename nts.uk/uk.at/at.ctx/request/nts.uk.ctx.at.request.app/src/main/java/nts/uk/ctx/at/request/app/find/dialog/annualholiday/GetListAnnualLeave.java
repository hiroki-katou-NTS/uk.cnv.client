package nts.uk.ctx.at.request.app.find.dialog.annualholiday;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.application.annualholiday.GetAnnualHolidayInforAdapter;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param.ReferenceAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.InterimRemain;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
/**
 * 年休消化一覧を取得
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL020_年休ダイアログ.アルゴリズム.社員の年休残数詳細情報を取得.年休消化一覧を取得
 * @author phongtq
 *
 */

@Stateless
public class GetListAnnualLeave {
	@Inject
	private GetAnnualHolidayInforAdapter adapter;
	
	public List<DetailsAnnuaAccumulatedHoliday> getListAnnualLeave(String sID){
		
		String cid = AppContexts.user().companyId();
		DatePeriod datePeriod = new DatePeriod(GeneralDate.today().addMonths(-2), GeneralDate.fromString("9999/12/31", "yyyy/MM/dd"));
		// [NO.551]期間内の年休使用明細を取得する
		List<TempAnnualLeaveMngs> annualHolidayData = new ArrayList<>();
		val lstRemain = adapter.lstRemainData(cid, sID, datePeriod, ReferenceAtr.APP_AND_SCHE);
		lstRemain.forEach(z -> {
			annualHolidayData.addAll(z.getData().getAnnualHolidayData());
		});		
		
		// List＜年休消化詳細＞を作成
		List<DetailsAnnuaAccumulatedHoliday> lstDetailAnn = new ArrayList<>();
		if (!lstRemain.isEmpty()) {
			annualHolidayData.forEach(x -> {
			// 取得した暫定年休管理データをチェック
				DetailsAnnuaAccumulatedHoliday detailAnn = new DetailsAnnuaAccumulatedHoliday("", "", "");
				InterimRemain remain = (InterimRemain)x;
				// ループ中の暫定年休管理データ．暫定残数管理データ．対象日　＞　システム日付　の場合 ー＞年休消化詳細．年休消化状況　＝　＃KDL020_63
				if (remain.getYmd().after(GeneralDate.today())) {
					// 年休消化詳細．年休消化状況をセット - 年休消化状況 
					detailAnn.setAnnualHolidayStatus(TextResource.localize("KDL020_63"));
				}
				String textSysDate = this.getDayOfJapan(remain.getYmd().dayOfWeek());
				// 年休消化詳細．消化日をセット
				detailAnn.setDigestionDate(TextResource.localize("KDL020_64", 
						remain.getYmd() + "", // ループ中の暫定年休管理データ．暫定残数管理データ．対象日
						textSysDate)); // ループ中の暫定年休管理データ．暫定残数管理データ．対象日　の曜日
				
				//年休消化詳細．使用数　＝　ループ中の暫定年休管理データ．暫定残数管理データ．年休使用数．使用日数　+　＃KDL020_66 +　ループ中の暫定年休管理データ．暫定残数管理データ．年休使用数．使用時間
				String minu = "";
				String usedNumText = "";
				if (x.getUsedNumber().getUsedTime().isPresent()) {
					minu = String.valueOf(x.getUsedNumber().getUsedTime().get().v() % 60).length() > 1 ? 
							String.valueOf(x.getUsedNumber().getUsedTime().get().v() % 60)
							: 0 + String.valueOf(x.getUsedNumber().getUsedTime().get().v() % 60);
					
					usedNumText = String.valueOf(x.getUsedNumber().getUsedTime().get().v() / 60) + ":" + minu;
				}
				
				String textNumberOfUse = x.getUsedNumber().getUsedDayNumberOrZero().v() + TextResource.localize("KDL020_66") + usedNumText;
				
				// 年休消化詳細．使用数をセット
				detailAnn.setNumberOfUse(textNumberOfUse);
				
				// List＜年休消化詳細＞に年休消化詳細を追加
				lstDetailAnn.add(detailAnn);
				
			});
		}
		
		return lstDetailAnn;
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
