package nts.uk.ctx.at.shared.app.find.scherec.monthlyattdcal.aggr.vtotalmethod;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.WorkDaysNumberOnLeaveCount;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkDaysNumberOnLeaveCountDto {

	// 会社ID
	private final String cid;

	// カウントする休暇一覧
	private final List<Integer> countedLeaveList;
	
	public static WorkDaysNumberOnLeaveCountDto fromDomain(WorkDaysNumberOnLeaveCount domain) {
		List<Integer> countedLeaveList = domain.getCountedLeaveList().stream()
				.map(data -> data.value).collect(Collectors.toList());
		return new WorkDaysNumberOnLeaveCountDto(domain.getCid(), countedLeaveList);
	}
}
