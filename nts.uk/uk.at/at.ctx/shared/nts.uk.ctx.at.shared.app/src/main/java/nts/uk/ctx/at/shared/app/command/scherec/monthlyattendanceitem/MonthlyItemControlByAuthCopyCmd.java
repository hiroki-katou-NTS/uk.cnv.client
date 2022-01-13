package nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem;

import java.util.List;

import lombok.Value;

/**
 * 
 * @author xuannt
 *
 */
@Value
public class MonthlyItemControlByAuthCopyCmd {
	/** 対象ロール：ロールID*/
	private String roleID;
	/**複写先リスト：List＜ロールID＞*/
	private List<String> destinationList;
}
