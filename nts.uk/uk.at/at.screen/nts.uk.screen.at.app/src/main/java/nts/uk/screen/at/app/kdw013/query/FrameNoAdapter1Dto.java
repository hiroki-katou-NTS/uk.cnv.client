package nts.uk.screen.at.app.kdw013.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.FrameNoAdapterDto;

/**
 * @author thanhpv
 * 
 */
@NoArgsConstructor
@Getter
public class FrameNoAdapter1Dto {
	
	public Integer attendanceItemId;
	
	public Integer frameNo;
	
	public Integer frameCategory;

	public FrameNoAdapter1Dto(FrameNoAdapterDto domain) {
		super();
		this.attendanceItemId = domain.getAttendanceItemId();
		this.frameNo = domain.getFrameNo();
		this.frameCategory = domain.getFrameCategory();
	}

}
