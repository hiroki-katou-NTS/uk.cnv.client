package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattendanceitem;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.ControlOfAttendanceItems;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.primitivevalue.HeaderBackgroundColor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "KSHMT_DAY_ATD_CTR")
public class KshmtDayAtdCtr extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KshmtDayAtdCtrPK kshmtDayAtdCtrPK;

	@Column(name = "HEADER_BACKGROUND_COLOR")
	public String headerBgColorOfDailyPer;

	@Override
	protected Object getKey() {
		return kshmtDayAtdCtrPK;
	}

	public KshmtDayAtdCtr(KshmtDayAtdCtrPK kshmtDayAtdCtrPK, String headerBgColorOfDailyPer) {
		super();
		this.kshmtDayAtdCtrPK = kshmtDayAtdCtrPK;
		this.headerBgColorOfDailyPer = headerBgColorOfDailyPer;
	}

	public static KshmtDayAtdCtr toEntity(ControlOfAttendanceItems domain) {
		return new KshmtDayAtdCtr(
				new KshmtDayAtdCtrPK(domain.getCompanyID(), domain.getItemDailyID()),
				domain.getHeaderBgColorOfDailyPer().isPresent() ? domain.getHeaderBgColorOfDailyPer().get().v() : null
		);
	}

	public ControlOfAttendanceItems toDomain() {
		return new ControlOfAttendanceItems(
				this.kshmtDayAtdCtrPK.companyID,
				this.kshmtDayAtdCtrPK.itemDailyID,
				(this.headerBgColorOfDailyPer != null) ? new HeaderBackgroundColor(this.headerBgColorOfDailyPer) : null
		);
	}

}
