package nts.uk.ctx.at.record.app.find.stamp.management;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * 
 * @author phongtq
 *
 */
@Data
@AllArgsConstructor
public class StampTypeDto {
	/** 勤務種類を半休に変更する */
	private boolean changeHalfDay;

	/** 外出区分 */
	private Optional<Integer> goOutArt;
	
	/** 所定時刻セット区分 */
	private Integer setPreClockArt;
	
	/** 時刻変更区分 */
	private Integer changeClockArt;
	
	/** 計算区分変更対象 */
	private Integer changeCalArt;
}
