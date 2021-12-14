package nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.layer.dom.AggregateRoot;

/**
 * 公休付与残数データ
 * 
 * @author Hop.NT
 *
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PublicHolidayRemain extends AggregateRoot {

	private String cID;
	// 社員ID
	private String sID;

	// 残数
	private RemainNumber remainNumber;

	public PublicHolidayRemain(String cid, String sid, BigDecimal numberDaysRemain) {
		this.cID = cid;
		this.sID = sid;
		if (numberDaysRemain == null) {
			this.remainNumber = new RemainNumber(BigDecimal.ZERO);
		} else {
			this.remainNumber = new RemainNumber(numberDaysRemain);
		}

	}
	public static PublicHolidayRemain creatFromJavaType(String cID, String sID, RemainNumber remainNumber){
		PublicHolidayRemain domain = new PublicHolidayRemain();
		domain.cID = cID;
		domain.sID = sID;
		domain.remainNumber = remainNumber;
		return domain;
	}
}
