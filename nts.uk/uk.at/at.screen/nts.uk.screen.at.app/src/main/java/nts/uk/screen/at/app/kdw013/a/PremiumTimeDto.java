package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PremiumTimeDto {

	// 割増時間NO - primitive value
	private Integer premiumTimeNo;

	// 割増時間
	private Integer premitumTime;

	/** 割増金額 */
	private Integer premiumAmount;

	/** 単価 **/
	private Integer unitPrice;

	public static PremiumTimeDto fromDomain(PremiumTime domain) {

		return new PremiumTimeDto(
				domain.getPremiumTimeNo() == null ? null : domain.getPremiumTimeNo().value,
				domain.getPremitumTime() == null ? null : domain.getPremitumTime().v(),
				domain.getPremiumAmount() == null ? null : domain.getPremiumAmount().v(),
				domain.getUnitPrice() == null ? null : domain.getUnitPrice().v());
	}

}
