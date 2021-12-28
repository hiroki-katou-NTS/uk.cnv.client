package nts.uk.ctx.at.request.app.command.application.overtime;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.app.find.application.ApplicationDto;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;

@NoArgsConstructor
public class AppOverTimeCommand {
	// 残業区分
	public Integer overTimeClf;
	
	// 申請時間
	public ApplicationTimeCommand applicationTime;
	
	// 休憩時間帯
	public List<TimeZoneWithWorkNoCommand> breakTimeOp;
	
	// 勤務時間帯
	public List<TimeZoneWithWorkNoCommand> workHoursOp;
	
	// 勤務情報
	public WorkInformationCommand workInfoOp;
	
	public ApplicationDto application;
	
	public AppOverTimeCommand(
			Integer overTimeClf,
			ApplicationTimeCommand applicationTime,
			List<TimeZoneWithWorkNoCommand> breakTimeOp,
			List<TimeZoneWithWorkNoCommand> workHoursOp,
			WorkInformationCommand workInfoOp
			) {
		
		this.overTimeClf = overTimeClf;
		this.applicationTime = applicationTime;
		this.breakTimeOp = breakTimeOp;
		this.workHoursOp = workHoursOp;
		this.workInfoOp = workInfoOp;
		
	}
	public Application toDomainApplication() {
		return application.toDomain();
	}
	public AppOverTime toDomain() {
		
		
		return new AppOverTime(
				EnumAdaptor.valueOf(overTimeClf, OvertimeAppAtr.class),
				applicationTime.toDomain(),
				CollectionUtil.isEmpty(breakTimeOp) ?
						Optional.empty() : 
						Optional.of(breakTimeOp.stream()
									.map(x -> x.toDomain())
									.collect(Collectors.toList())),
				CollectionUtil.isEmpty(workHoursOp) ?
						Optional.empty() : 
							Optional.of(workHoursOp.stream()
									.map(x -> x.toDomain())
									.collect(Collectors.toList())),
				workInfoOp == null ? Optional.empty() : Optional.of(workInfoOp.toDomain()),
				Optional.empty()
		);
	}
}
