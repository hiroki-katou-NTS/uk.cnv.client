package nts.uk.ctx.at.request.app.find.dialog.annualholiday;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.annualholiday.AnnLeaveRemainAdapter;
import nts.uk.ctx.at.request.dom.application.annualholiday.AnnualLeaveInfoExport;
import nts.uk.ctx.at.request.dom.application.annualholiday.AnnualLeaveRemainingNumberExport;
import nts.uk.ctx.at.request.dom.application.annualholiday.GetPeriodPrvNextGrantDateAdapter;
import nts.uk.ctx.at.request.dom.application.annualholiday.GrantPeriodDto;
import nts.uk.ctx.at.request.dom.application.annualholiday.ReNumAnnLeaReferenceDateExport;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.require.RemainNumberTempRequireService;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AbsenceTenProcess;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AnnualHolidaySetOutput;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
/**
 * 
 * @author phongtq
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL020_年休ダイアログ.アルゴリズム.社員の年休残数詳細情報を取得
 * 社員の年休残数詳細情報を取得
 *
 */
@Stateless
public class GetInforRemainAnnualHoliday {
	
	@Inject
	private RemainNumberTempRequireService requireService;
	
	@Inject
	private AnnLeaveRemainAdapter annAdapter;
	
	@Inject 
	private GetPeriodPrvNextGrantDateAdapter dateAdapter;
	
	@Inject
	private GetListAnnualLeave annualLeave;
	
	@Inject
	private AdjustRemainingNumberInformation numberInformation;
	
	public InforAnnualHolidaysAccHolidayDto getGetInforRemainAnnualHoliday(GeneralDate baseDate, String sID) {
		val require = requireService.createRequire();
		String cId = AppContexts.user().companyId();
		
		// 10-1.年休の設定を取得する
		AnnualHolidaySetOutput annualHd = AbsenceTenProcess.getSettingForAnnualHoliday(require, cId);
		InforAnnualHolidaysAccHolidayDto accHolidayDto = new InforAnnualHolidaysAccHolidayDto(new ArrayList<>(), new ArrayList<>(), 
				false, "", "", "", false, "", "");
		List<DetailsAnnuaAccumulatedHoliday> annuaAccumulatedHoliday = new ArrayList<>();
		// 年休管理するかないかチェック (取得した年休管理区分をチェック)
		if (annualHd.isYearHolidayManagerFlg()) { 
			// if Trueの場合
			// No.198 基準日時点の年休残数を取得する 
			ReNumAnnLeaReferenceDateExport reNumAnnLeave = annAdapter.getReferDateAnnualLeaveRemainNumber(sID, baseDate);
			/*
			 * ReNumAnnLeaReferenceDateExport reNumAnnLeave = new
			 * ReNumAnnLeaReferenceDateExport( new
			 * AnnualLeaveInfoExport(GeneralDate.today(), new
			 * AnnualLeaveRemainingNumberExport(5, 10)) , new ArrayList<>(), 0.5, 10);
			 */
			// 残数情報を調整 
			accHolidayDto = numberInformation.adjustRemainingNumberInformation(reNumAnnLeave, annualHd.isSuspensionTimeYearFlg());
			
			// 指定した年月日を基準に、前回付与日から次回付与日までの期間を取得
			Optional<GrantPeriodDto> periodGrantDate = dateAdapter.getPeriodYMDGrant(cId, sID, GeneralDate.today(), null, Optional.empty());
			// getPeriodFromPreviousToNextGrantDate.getPeriodYMDGrant(cid, employeeId, designatedDate, null, null).map(GrantPeriodDto::getPeriod);
			
			if (periodGrantDate.isPresent()) {
			// 返す「基準日時点年休残数」をセット
				// ・年休・積休残数詳細情報DTO．時間年休の年間上限開始日　＝　取得した期間．開始日
				accHolidayDto.setAnnLimitStart(periodGrantDate.get().getPeriod().start().toString());
				
				// ・年休・積休残数詳細情報DTO．時間年休の年間上限終了日　＝　取得した期間．終了日
				accHolidayDto.setAnnLimitEnd(periodGrantDate.get().getPeriod().end().toString());
				
				// ・年休・積休残数詳細情報DTO．次回付与予定日　＝　取得した期間．開始日
				if (periodGrantDate.get().getNextGrantDate().isPresent())
				accHolidayDto.setNextScheDate(TextResource.localize("KDL020_67", periodGrantDate.get().getNextGrantDate().get().toString(), this.getDayOfJapan(periodGrantDate.get().getNextGrantDate().get().dayOfWeek())));
			}
			// 年休消化一覧を取得
			annuaAccumulatedHoliday = annualLeave.getListAnnualLeave(sID);
		}
		
		accHolidayDto.setLstAnnAccHoliday(annuaAccumulatedHoliday);
		
		// 年休・積休残数詳細情報DTOの値を返す - toDo
			// 年休・積休残数詳細情報DTO．年休管理区分　＝　取得した年休管理区分
			accHolidayDto.setAnnAccManaAtr(annualHd.isYearHolidayManagerFlg());
			
			// 年休・積休残数詳細情報DTO．時間年休管理区分　＝　取得した時間年休管理区分
			accHolidayDto.setAnnManaAtr(annualHd.isSuspensionTimeYearFlg());
		
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
