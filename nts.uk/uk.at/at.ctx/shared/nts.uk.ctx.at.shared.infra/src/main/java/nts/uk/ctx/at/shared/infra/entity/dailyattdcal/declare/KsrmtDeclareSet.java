package nts.uk.ctx.at.shared.infra.entity.dailyattdcal.declare;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * エンティティ：申告設定
 * @author shuichi_ishida
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSRMT_DECLARE_SET")
public class KsrmtDeclareSet extends UkJpaEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	/** 会社ID */
	@Id
	@Column(name = "CID")
	public String companyId;
	
	/** 申告利用区分 */
	@Column(name = "USAGE_ATR")
	public int usageAtr;
	
	/** 枠設定 */
	@Column(name = "FRAME_SET")
	public int frameSet;
	
	/** 深夜時間自動計算 */
	@Column(name = "MN_AUTOCALC")
	public int mnAutoCalc;
	
	/** 早出残業枠 */
	@Column(name = "EARLY_OT_FRAME")
	public Integer earlyOtFrame;
	
	/** 早出残業深夜枠 */
	@Column(name = "EARLY_OT_MN_FRAME")
	public Integer earlyOtMnFrame;
	
	/** 普通残業枠 */
	@Column(name = "OVERTIME_FRAME")
	public Integer overtimeFrame;
	
	/** 普通残業深夜枠 */
	@Column(name = "OVERTIME_MN_FRAME")
	public Integer overtimeMnFrame;
	
	/** 休出法定内枠 */
	@Column(name = "HDWK_STAT_FRAME")
	public Integer hdwkStatFrame;
	
	/** 休出法定外枠 */
	@Column(name = "HDWK_NOT_STAT_FRAME")
	public Integer hdwkNotStatFrame;
	
	/** 休出法定外祝日枠 */
	@Column(name = "HDWK_NOT_STAT_HD_FRAME")
	public Integer hdwkNotStatHdFrame;
	
	/** 休出深夜法定内枠 */
	@Column(name = "HDWK_MN_STAT_FRAME")
	public Integer hdwkMnStatFrame;
	
	/** 休出深夜法定外枠 */
	@Column(name = "HDWK_MN_NOT_STAT_FRAME")
	public Integer hdwkMnNotStatFrame;
	
	/** 休出深夜法定外祝日枠 */
	@Column(name = "HDWK_MN_NOT_STAT_HD_FRAME")
	public Integer hdwkMnNotStatHdFrame;
	
	@Override
	protected Object getKey() {
		return this.companyId;
	}
}
