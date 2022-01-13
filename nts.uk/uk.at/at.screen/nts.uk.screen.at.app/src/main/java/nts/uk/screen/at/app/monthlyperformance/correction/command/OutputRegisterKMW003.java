package nts.uk.screen.at.app.monthlyperformance.correction.command;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutputRegisterKMW003 {
	private String employeeId;
	
	private List<ItemValue> itemChangeByCal = new ArrayList<>();
}
