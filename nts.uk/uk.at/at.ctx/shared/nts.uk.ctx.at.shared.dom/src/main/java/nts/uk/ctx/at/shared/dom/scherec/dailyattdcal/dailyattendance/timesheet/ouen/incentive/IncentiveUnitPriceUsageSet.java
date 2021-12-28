package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.incentive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;

@Getter
/** インセンティブ単価使用設定 */
public class IncentiveUnitPriceUsageSet extends AggregateRoot {

	/** 会社ID: 会社ID */
	private String cid;
	
	/** 単価設定単位: 単価設定単位 */
	private UnitPriceUsageUnit unit;
	
	private IncentiveUnitPriceUsageSet(String cid, UnitPriceUsageUnit unit) {
		super();
		this.cid = cid;
		this.unit = unit;
	}

	public static IncentiveUnitPriceUsageSet create(String cid, UnitPriceUsageUnit unit) {
		
		return new IncentiveUnitPriceUsageSet(cid, unit);
	}

	public static IncentiveUnitPriceUsageSet create(String cid, int unit) {
		
		return new IncentiveUnitPriceUsageSet(cid, EnumAdaptor.valueOf(unit, UnitPriceUsageUnit.class));
	}

	@AllArgsConstructor
	public static enum UnitPriceUsageUnit {
		
		/** 会社　*/
		COMPANY(1),
		
		/** 職場　*/
		WORKPLACE(2),
		
		/** 場所　*/
		WORKLOCATION(3);
		
		public final int value;
		
		public boolean isCompany() {
			return this.equals(COMPANY);
		}
		
		public boolean isWorkPlace() {
			return this.equals(WORKPLACE);
		}
		
		public boolean isWorkLocation() {
			return this.equals(WORKLOCATION);
		}
	}
}
