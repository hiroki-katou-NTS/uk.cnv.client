package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPrice;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPricePerNumber;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

/**
 * 社員単価履歴項目
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSRMT_UNIT_PRICE_ITEM_SYA")
public class KsrmtUnitPriceItemSya extends ContractCompanyUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	public KsrmtUnitPriceItemSyaPk pk;
	
	/** 社員ID **/
	@Column(name = "SID")
	public String sId;
	
	/** 社員時間単価 **/
	@Column(name = "HOURLY_UNIT_PRICE")
	public int hourlyUnitPrice;
	
	@Override
	protected Object getKey() {
		return this.pk;
	}
	
	public static Optional<EmployeeUnitPriceHistoryItem> toDomain(List<KsrmtUnitPriceItemSya> entity) {
		List<UnitPricePerNumber> unitPrices = entity.stream()
				.map(e -> new UnitPricePerNumber(UnitPrice.valueOf(e.pk.unitPriceNo), new WorkingHoursUnitPrice(e.hourlyUnitPrice)))
				.collect(Collectors.toList());
		
		return entity.stream()
				.map(e -> new EmployeeUnitPriceHistoryItem(e.sId, e.pk.histId, unitPrices))
				.findFirst();
	}
}
