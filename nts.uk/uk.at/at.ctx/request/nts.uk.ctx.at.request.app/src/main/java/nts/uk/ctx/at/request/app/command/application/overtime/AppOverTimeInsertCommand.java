package nts.uk.ctx.at.request.app.command.application.overtime;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.app.command.application.common.ApplicationInsertCmd;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.overtime.*;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.shr.com.time.TimeWithDayAttr;
import org.apache.commons.lang3.StringUtils;


@NoArgsConstructor
public class AppOverTimeInsertCommand {
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

	public List<MultipleOvertimeContentCommand> multipleOvertimeContents;
	
	public ApplicationInsertCmd application;

	public AppOverTime toDomain() {
		OvertimeAppAtr overtimeAppAtr = EnumAdaptor.valueOf(overTimeClf, OvertimeAppAtr.class);
		return new AppOverTime(
				overtimeAppAtr,
				applicationTime == null ? null : applicationTime.toDomain(),
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
				overtimeAppAtr != OvertimeAppAtr.MULTIPLE_OVERTIME || CollectionUtil.isEmpty(multipleOvertimeContents)
						? Optional.empty()
						: Optional.of(OvertimeWorkMultipleTimes.create(
								multipleOvertimeContents.stream()
										.map(i -> new OvertimeHour(
												new OvertimeNumber(i.getFrameNo()),
												new TimeSpanForCalc(new TimeWithDayAttr(i.getStartTime()), new TimeWithDayAttr(i.getEndTime()))
										)).collect(Collectors.toList()),
								multipleOvertimeContents.stream()
										.filter(i -> i.getFixedReasonCode() != null || !StringUtils.isEmpty(i.getAppReason()))
										.map(i -> new OvertimeReason(
												new OvertimeNumber(i.getFrameNo()),
												Optional.ofNullable(i.getFixedReasonCode() == null ? null : new AppStandardReasonCode(i.getFixedReasonCode())),
												Optional.ofNullable(StringUtils.isEmpty(i.getAppReason()) ? null : new AppReason(i.getAppReason()))
										)).collect(Collectors.toList())
						))
		);
	}
}
