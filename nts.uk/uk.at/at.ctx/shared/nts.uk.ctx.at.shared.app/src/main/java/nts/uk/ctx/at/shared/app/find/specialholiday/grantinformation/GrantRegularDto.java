package nts.uk.ctx.at.shared.app.find.specialholiday.grantinformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantRegular;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GrantRegularDto {

	/** 付与するタイミングの種類 */
	private int typeTime;

	/** 付与基準日 */
	private Integer grantDate;
	
	/** 	指定日付与 */
	private FixGrantDateDto fixGrantDate;

	/** 	付与日テーブル参照付与 */
	private GrantDeadlineDto grantPeriodic;
	
	/** 	期間付与 */
	private PeriodGrantDateDto periodGrantDate;
	
	public static GrantRegularDto fromDomain(GrantRegular domain) {
		
		return new GrantRegularDto(
				domain.getTypeTime().value,
				domain.getGrantDate().isPresent() ? domain.getGrantDate().get().value : null,
				domain.getFixGrantDate().isPresent() ? FixGrantDateDto.fromDomain(domain.getFixGrantDate().get()) : null,
				domain.getGrantPeriodic().isPresent() ? GrantDeadlineDto.fromDomain(domain.getGrantPeriodic().get().getGrantDeadline()) : null,
				domain.getPeriodGrantDate().isPresent() ? PeriodGrantDateDto.fromDomain(domain.getPeriodGrantDate().get()) : null);
	}
	
}
