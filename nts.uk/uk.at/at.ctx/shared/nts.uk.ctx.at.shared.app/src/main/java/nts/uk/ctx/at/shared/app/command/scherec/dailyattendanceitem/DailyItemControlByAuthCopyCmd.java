package nts.uk.ctx.at.shared.app.command.scherec.dailyattendanceitem;

import java.util.List;

import lombok.Value;

/**
 * 
 * @author xuannt
 *
 */
@Value
public class DailyItemControlByAuthCopyCmd {
	/** 対象ロール：ロールID*/
	private String roleID;
	/**複写先リスト：List＜ロールID＞*/
	private List<String> destinationList;
}
