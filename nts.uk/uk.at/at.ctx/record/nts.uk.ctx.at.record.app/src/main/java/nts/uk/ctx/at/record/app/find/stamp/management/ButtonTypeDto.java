package nts.uk.ctx.at.record.app.find.stamp.management;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ButtonTypeDto {
	/** 予約区分 */
	private int reservationArt;
	
	/** 打刻種類 */
	private StampTypeDto stampType;
}
