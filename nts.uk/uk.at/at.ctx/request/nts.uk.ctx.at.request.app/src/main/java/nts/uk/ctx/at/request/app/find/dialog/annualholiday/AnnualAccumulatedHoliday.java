package nts.uk.ctx.at.request.app.find.dialog.annualholiday;

import lombok.Data;

/**
 * 年休・積休残数詳細
 * @author phongtq
 *
 */

@Data
public class AnnualAccumulatedHoliday {
	
	/** 付与数 */
	private String numberGrants;
	
	/** 付与日 */
	private String grandDate;
	
	/** 使用数 */
	private String numberOfUse;
	
	/** 有効期限 */
	private String dateOfExpiry;
	
	/** 残数 */
	private String numberOfRemain;

	public AnnualAccumulatedHoliday(String numberGrants, String grandDate, String numberOfUse, String dateOfExpiry,
			String numberOfRemain) {
		super();
		this.numberGrants = numberGrants;
		this.grandDate = grandDate;
		this.numberOfUse = numberOfUse;
		this.dateOfExpiry = dateOfExpiry;
		this.numberOfRemain = numberOfRemain;
	}
}
