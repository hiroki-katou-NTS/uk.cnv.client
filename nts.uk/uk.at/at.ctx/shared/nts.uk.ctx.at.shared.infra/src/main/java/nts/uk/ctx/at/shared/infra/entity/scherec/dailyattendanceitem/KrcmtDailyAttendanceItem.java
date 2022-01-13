package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattendanceitem;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author nampt
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCMT_DAY_ATTENDANCE_ITEM")
public class KrcmtDailyAttendanceItem extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcmtDailyAttendanceItemPK krcmtDailyAttendanceItemPK;

	/* 勤怠項目名称 */
	@Basic(optional = false)
	@Column(name = "ATTENDANCE_ITEM_NAME")
	public String attendanceItemName;
	
	/* 表示番号 */
	@Basic(optional = false)
	@Column(name = "DISPLAY_NUMBER")
	public int displayNumber;
	
	/* ユーザーが値を変更できる */
	@Basic(optional = false)
	@Column(name = "USER_CAN_SET")
	public int userCanSet;
	
	/* 名称の改行位置 */
	@Basic(optional = false)
	@Column(name = "LINE_BREAK_POSITION")
	public int nameLineFeedPosition;
	
	/* 属性 */
	@Basic(optional = false)
	@Column(name = "DAILY_ATTENDANCE_ATR")
	public int dailyAttendanceAtr;
	
	/* Group */
	@Basic(optional = true)
	@Column(name = "TYPE_OF_MASTER")
	public Integer typeOfMaster;
	
	@Basic(optional = true)
	@Column(name = "PRIMITIVE_VALUE")
	public Integer primitiveValue;
	
	@Basic(optional = true)
	@Column(name = "DISPLAY_NAME")
	public String displayName;
	

	@Override
	protected Object getKey() {
		return this.krcmtDailyAttendanceItemPK;
	}

	public static KrcmtDailyAttendanceItem toEntity(DailyAttendanceItem domain) {
		return new KrcmtDailyAttendanceItem(
				new KrcmtDailyAttendanceItemPK(domain.getCompanyId(), domain.getAttendanceItemId()),
				domain.getAttendanceName().v(), domain.getDisplayNumber(), domain.getUserCanUpdateAtr().value,
				domain.getNameLineFeedPosition(), domain.getDailyAttendanceAtr().value,
				domain.getMasterType().map(x -> x.value).orElse(null),
				domain.getPrimitiveValue().map(x -> x.value).orElse(null),
				domain.getDisplayName().isPresent() ? domain.getDisplayName().get().v() : null);
	}
}
