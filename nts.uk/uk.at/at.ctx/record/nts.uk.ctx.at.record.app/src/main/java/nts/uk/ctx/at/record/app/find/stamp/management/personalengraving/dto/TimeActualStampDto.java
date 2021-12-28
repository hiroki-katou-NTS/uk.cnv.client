package nts.uk.ctx.at.record.app.find.stamp.management.personalengraving.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;

/**
 * @author anhdt
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeActualStampDto {

	private Integer numberOfReflectionStamp;

	private Integer actualTimeWithDay;
	private String actualLocationCode;
	private Integer actualStampSourceInfo;

	private Integer stampTimeWithDay;
	private String stampLocationCode;
	private Integer stampStampSourceInfo;

	public TimeActualStampDto(Optional<TimeActualStamp> oDomain) {
		if (oDomain.isPresent()) {
			TimeActualStamp domain = oDomain.get();

			Optional<WorkStamp> oActualWt = domain.getActualStamp();
			if (oActualWt.isPresent()) {
				WorkStamp actualWt = oActualWt.get();
				this.actualTimeWithDay = actualWt.getTimeDay().getTimeWithDay().isPresent()
						? actualWt.getTimeDay().getTimeWithDay().get().v() : null;
				this.actualLocationCode = actualWt.getLocationCode().isPresent() ? actualWt.getLocationCode().get().v()
						: null;
				this.actualStampSourceInfo = actualWt.getTimeDay().getReasonTimeChange().getTimeChangeMeans().value;
			}

			Optional<WorkStamp> oStampWt = domain.getStamp();
			if (oStampWt.isPresent()) {
				WorkStamp stampWt = oStampWt.get();
				this.stampTimeWithDay = stampWt.getTimeDay().getTimeWithDay().isPresent()
						? stampWt.getTimeDay().getTimeWithDay().get().v() : null;
				this.stampLocationCode = stampWt.getLocationCode().isPresent() ? stampWt.getLocationCode().get().v()
						: null;
				this.stampStampSourceInfo = stampWt.getTimeDay().getReasonTimeChange().getTimeChangeMeans().value;
			}

		}
	}

}
