package nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消化一覧
 * @author tutk
 *
 */
@Data
@NoArgsConstructor
public class DigestionItem {

	// 消化数
	private String digestionCount;

	// 消化日
	private String digestionDate;

	// 消化日状況
	private String digestionDateStatus;

	public DigestionItem(String digestionCount, String digestionDate, String digestionDateStatus) {
		super();
		this.digestionCount = digestionCount;
		this.digestionDate = digestionDate;
		this.digestionDateStatus = digestionDateStatus;
	}
	
}
