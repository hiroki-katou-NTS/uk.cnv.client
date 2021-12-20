package nts.uk.ctx.at.request.app.find.dialog.suspensionholiday;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
/**
 * 社員の年休残数詳細情報を取得
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL020_年休ダイアログ.アルゴリズム.社員の年休残数詳細情報を取得
 * @author Admin
 *
 */
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.app.find.dialog.annualholiday.DetailsAnnuaAccumulatedHoliday;
import nts.uk.ctx.at.request.app.find.dialog.annualholiday.InforAnnualHolidaysAccHolidayDto;
import nts.uk.ctx.at.request.dom.application.annualholiday.GetRsvLeaRemNumUsageDetailAdapter;
import nts.uk.ctx.at.request.dom.application.annualholiday.TmpReserveLeaveMngExport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.rsvleamanager.ReserveLeaveManagerApdater;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.rsvleamanager.rsvimport.RsvLeaCriterialDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.require.RemainNumberTempRequireService;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AbsenceTenProcess;
import nts.uk.shr.com.i18n.TextResource;
@Stateless
public class GetInforNumberRemainEmployees {
	
	@Inject
	private RemainNumberTempRequireService requireService;
	
	@Inject
	private ReserveLeaveManagerApdater rsvLeaMngApdater;
	
	@Inject
	private CreateInforNumberRemainDays days;
	
	@Inject
	private GetRsvLeaRemNumUsageDetailAdapter adapter;
	
	public InforAnnualHolidaysAccHolidayDto getInforNumberRemainEmployees(String cID, String sID) {
		val require = requireService.createRequire();
        val cacheCarrier = new CacheCarrier();
        
        InforAnnualHolidaysAccHolidayDto accHolidayDto = new InforAnnualHolidaysAccHolidayDto(new ArrayList<>(), new ArrayList<>(), 
				false, "", "", "", false, "", "");
        
		// 10-4.積立年休の設定を取得する
		boolean isRetentionManage = AbsenceTenProcess.getSetForYearlyReserved(require, cacheCarrier, cID, sID, GeneralDate.today());
		
		// 取得した積立年休管理区分をチェック
		if (isRetentionManage) {
			// 基準日時点の積立年休残数を取得する
			Optional<RsvLeaCriterialDate> stock = rsvLeaMngApdater.getRsvLeaveManagerData(sID, GeneralDate.today());
			
			// 期間内の積立年休使用明細を取得する
			DatePeriod datePeriod = new DatePeriod(GeneralDate.today().addMonths(-2), GeneralDate.fromString("9999/12/31", "yyyy/MM/dd"));
			List<TmpReserveLeaveMngExport> lstMngEx = adapter.getRsvLeaRemNumUsageDetail(sID, datePeriod);
			
			// 積休残数詳細情報を作成 
			accHolidayDto = days.createInforNumberRemainDays(stock);
			
			// 年休・積休消化詳細を作成して年休・積休残数詳細情報DTOにセット
			List<DetailsAnnuaAccumulatedHoliday> lstAnnAccHoliday = new ArrayList<>();
			
			// 取得したList＜暫定積立年休管理データ＞から作成する
			lstMngEx.forEach(x -> {
				DetailsAnnuaAccumulatedHoliday annAccHoliday = new DetailsAnnuaAccumulatedHoliday("", "", "");
				
				String textAnnualHolidayStatus = "";
				// 年休消化状況　＝　暫定残数管理データ．対象日．年月日　＞　システム日付　？　＃KDL029_41　：　Null
				if (x.getYmd().after(GeneralDate.today())) {
					textAnnualHolidayStatus = TextResource.localize("KDL029_41");
				}
				annAccHoliday.setAnnualHolidayStatus(textAnnualHolidayStatus);
				
				// 消化日　＝　#KDL029_42 - 内容：　{0}({1})
				//{0} = 暫定残数管理データ．対象日．年月日
				//{1} = 暫定残数管理データ．対象日．年月日の曜日
				String textDig = days.getDayOfJapan(x.getYmd().dayOfWeek());
				annAccHoliday.setDigestionDate(TextResource.localize("KDL029_42", x.getYmd().toString("yyyy/MM/dd"), textDig));
				       
				// 使用数　＝　暫定積立年休管理データ．使用数　　+　＃KDL020_14
				annAccHoliday.setNumberOfUse(x.getUseDays().toString() + TextResource.localize("KDL020_14"));
				lstAnnAccHoliday.add(annAccHoliday);
			});
			accHolidayDto.setLstAnnAccHoliday(lstAnnAccHoliday);
		}
		// 年休・積休管理区分　＝　取得した積立年休管理区分
		accHolidayDto.setAnnAccManaAtr(isRetentionManage);
		return accHolidayDto;
	}
}
