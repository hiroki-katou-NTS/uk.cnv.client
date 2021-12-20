package nts.uk.ctx.at.request.app.find.dialog.annualholiday;

import java.util.List;

import lombok.Data;

/**
 * 年休・積休残数詳細情報DTO
 * 
 * @author phongtq
 *
 */
@Data
public class InforAnnualHolidaysAccHolidayDto {
	/** 年休・積休残数一覧  (年休・積休残数詳細) */
	private List<AnnualAccumulatedHoliday> lstRemainAnnAccHoliday;
	
	/** 年休・積休消化一覧  (年休・積休消化詳細) */
	private List<DetailsAnnuaAccumulatedHoliday> lstAnnAccHoliday;
	
	/** 年休・積休管理区分 */
	private boolean annAccManaAtr;
	
	/** 時間年休の年間上限時間 */
	private String annMaxTime;
	
	/** 時間年休の年間上限開始日 */
	private String annLimitStart;
	
	/** 時間年休の年間上限終了日 */
	private String annLimitEnd;
	
	/** 時間年休管理区分 */
	private boolean annManaAtr;
	
	/** 次回付与予定日 */
	private String nextScheDate;
	
	/** 現時点残数 */
	private String currentRemainNum;

	public InforAnnualHolidaysAccHolidayDto(List<AnnualAccumulatedHoliday> lstRemainAnnAccHoliday,
			List<DetailsAnnuaAccumulatedHoliday> lstAnnAccHoliday, boolean annAccManaAtr, String annMaxTime,
			String annLimitStart, String annLimitEnd, boolean annManaAtr, String nextScheDate,
			String currentRemainNum) {
		super();
		this.lstRemainAnnAccHoliday = lstRemainAnnAccHoliday;
		this.lstAnnAccHoliday = lstAnnAccHoliday;
		this.annAccManaAtr = annAccManaAtr;
		this.annMaxTime = annMaxTime;
		this.annLimitStart = annLimitStart;
		this.annLimitEnd = annLimitEnd;
		this.annManaAtr = annManaAtr;
		this.nextScheDate = nextScheDate;
		this.currentRemainNum = currentRemainNum;
	}
}

