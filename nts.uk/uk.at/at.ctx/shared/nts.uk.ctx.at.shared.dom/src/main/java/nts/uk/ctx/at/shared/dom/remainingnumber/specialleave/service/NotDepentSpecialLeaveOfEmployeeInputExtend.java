package nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveAppSetting;
import nts.arc.time.calendar.period.DatePeriod;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class NotDepentSpecialLeaveOfEmployeeInputExtend{
	private Optional<String> sid;
	/**	会社ID */
	private String cid;
	/**	期間 */
	private DatePeriod datePeriod;
	/** 特別休暇コード	 */
	private int specialLeaveCode;
	/** 特別休暇付与基準日	 */
	private GeneralDate speGrantDate;
	/** 年休付与基準日	 */
	private GeneralDate annGrantDate;
	/** 入社年月日	 */
	private GeneralDate inputDate;
	/** 単一フラグ
	 * TRUE　→　期間中の初めての付与のみ取得
	 * FALSE　→　期間中の全ての付与を取得 */
	private boolean signFlg;
	/** 特別休暇適用設定	 */
	private SpecialLeaveAppSetting specialSetting;
	/** 固定付与日数	 */
	private Optional<Double> grantDays;
	/**	付与テーブルコード */
	private Optional<String> grantTableCd;

}
