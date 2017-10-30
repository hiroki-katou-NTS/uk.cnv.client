package nts.uk.ctx.at.schedule.app.command.budget.schedulevertical.fixedverticalsetting;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * 
 * @author phongtq
 *
 */
@Data
@AllArgsConstructor
public class AddVerticalCntSettingCommand {
	
	/* 特別休暇コード */
	private int fixedItemAtr;
	
	private List<Integer> verticalCountNo;
}
