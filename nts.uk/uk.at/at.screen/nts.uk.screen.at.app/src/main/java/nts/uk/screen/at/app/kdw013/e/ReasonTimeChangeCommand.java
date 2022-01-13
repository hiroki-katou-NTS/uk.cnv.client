package nts.uk.screen.at.app.kdw013.e;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.EngravingMethod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;

@AllArgsConstructor
@Getter
public class ReasonTimeChangeCommand {

	// 時刻変更手段
	@Setter
	private Integer timeChangeMeans;

	// 打刻方法
	@Setter
	private Integer engravingMethod;

	public ReasonTimeChange toDomain() {

		return new ReasonTimeChange(EnumAdaptor.valueOf(this.getTimeChangeMeans(), TimeChangeMeans.class),
				Optional.ofNullable(this.getEngravingMethod() == null ? null
						: EnumAdaptor.valueOf(this.getEngravingMethod(), EngravingMethod.class)));
	}

}
