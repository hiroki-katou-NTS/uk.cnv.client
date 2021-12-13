package nts.uk.ctx.at.function.app.command.holidaysremaining;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.com.context.AppContexts;

@AllArgsConstructor
@Getter
@Setter
public class HdRemainManageCommand {
	/**
	 * 出力レイアウトID
	 */
	private String layoutId;

	/**
	 * 項目選択区分
	 */
	private int itemSelType;

	/**
	 *
	 */
	private String sid;


	/**
	 * 会社ID
	 */
	private String cid;

	/**
	 * コード
	 */
	private String cd;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 介護休暇の項目を出力する
	 */
	private boolean nursingLeave;

	/**
	 * 代休残数を出力する
	 */
	private boolean remainingChargeSubstitute;

	/**
	 * 代休未消化出力する
	 */
	private boolean representSubstitute;
	/**
	 * 代休の項目を出力する
	 */
	private boolean outputItemSubstitute;

	/**
	 * 公休繰越数を出力する
	 */
	private boolean outputHolidayForward;
	/**
	 * 公休月度残を出力する
	 */
	private boolean monthlyPublic;
	/**
	 * 公休の項目を出力する
	 */
	private boolean outputItemsHolidays;

	/**
	 * childNursingLeave
	 */
	private boolean childNursingLeave;

	/**
	 * 年休の項目出力する
	 */
	private boolean yearlyHoliday;

	/**
	 * 内時間年休残数を出力する
	 */
	private boolean insideHours;

	/**
	 * ★内半日年休を出力する
	 */
	private boolean insideHalfDay;

	/**
	 * 振休残数を出力する
	 */
	private boolean numberRemainingPause;

	/**
	 * 時間外超過項目を出力する
	 */
	private boolean hd60HItem;

	/**
	 * 時間外超過項目を出力する
	 */
	private boolean hd60HRemain;
	/**
	 * 時間外超過項目を出力する
	 */
	private boolean hd60HUndigested;
	/**
	 * 振休未消化を出力する
	 */
	private boolean unDigestedPause;
	/**
	 * 振休の項目を出力する
	 */
	private boolean pauseItem;

	/**
	 * 積立年休の項目を出力する
	 */
	private boolean yearlyReserved;

	private List<Integer> listSpecialHoliday;

}
