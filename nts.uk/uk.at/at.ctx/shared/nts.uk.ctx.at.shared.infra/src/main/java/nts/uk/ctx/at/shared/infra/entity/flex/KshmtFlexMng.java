package nts.uk.ctx.at.shared.infra.entity.flex;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;


/**
 * @author HoangNDH
 * The persistent class for the KSHMT_FLEX_MNG database table.
 * フレックス勤務の設定
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="KSHMT_FLEX_MNG")
public class KshmtFlexMng extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private KshstFlexWorkSettingPK id;
	
	/** フレックス勤務を管理する */
	@Column(name="MANAGE_FLEX_WORK")
	private boolean manageFlexWork;

	@Override
	protected Object getKey() {
		return id;
	}

}