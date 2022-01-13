package nts.uk.ctx.at.shared.infra.entity.scherec.monthlyattendanceitem;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.primitivevalue.HeaderBackgroundColor;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.ControlOfMonthlyItems;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "KSHMT_MON_ITEM_CONTROL")
public class KshmtMonItemControl   extends ContractUkJpaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KshmtMonItemControlPK krcmtControlOfMonthlyItemsPK;
	
	@Column(name = "HEADER_BACKGROUND_COLOR")
	public String headerBgColorOfMonthlyPer;
	
	@Override
	protected Object getKey() {
		return krcmtControlOfMonthlyItemsPK;
	}

	public KshmtMonItemControl(KshmtMonItemControlPK krcmtControlOfMonthlyItemsPK, String headerBgColorOfMonthlyPer) {
		super();
		this.krcmtControlOfMonthlyItemsPK = krcmtControlOfMonthlyItemsPK;
		this.headerBgColorOfMonthlyPer = headerBgColorOfMonthlyPer;
	}
	
	public static KshmtMonItemControl toEntity(ControlOfMonthlyItems domain) {
		return new KshmtMonItemControl(
				new KshmtMonItemControlPK(
						domain.getCompanyId(),
						domain.getItemMonthlyId()
				),
				domain.getHeaderBgColorOfMonthlyPer().isPresent()?domain.getHeaderBgColorOfMonthlyPer().get().v():null
		);
	}

	public ControlOfMonthlyItems toDomain() {
		return new ControlOfMonthlyItems(
				this.krcmtControlOfMonthlyItemsPK.companyID,
				this.krcmtControlOfMonthlyItemsPK.itemMonthlyID,
				this.headerBgColorOfMonthlyPer != null ? new HeaderBackgroundColor(this.headerBgColorOfMonthlyPer) : null
		);
	}
	
}
