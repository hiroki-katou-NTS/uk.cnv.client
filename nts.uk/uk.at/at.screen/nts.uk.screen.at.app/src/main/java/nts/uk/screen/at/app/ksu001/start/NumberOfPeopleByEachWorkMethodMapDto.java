package nts.uk.screen.at.app.ksu001.start;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter.NumberOfPeopleByEachWorkMethod;
import nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal.WorkInfo;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class NumberOfPeopleByEachWorkMethodMapDto {

	public GeneralDate date;
	
	public List<NumberOfPeopleByEachWorkMethod<WorkInfo>> peopleMethod = Collections.emptyList();
}
