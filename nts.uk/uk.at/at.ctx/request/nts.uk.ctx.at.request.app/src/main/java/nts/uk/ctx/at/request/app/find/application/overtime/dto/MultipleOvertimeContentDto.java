package nts.uk.ctx.at.request.app.find.application.overtime.dto;

import lombok.Data;
import nts.arc.primitive.PrimitiveValueBase;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeHour;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeNumber;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeReason;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeWorkMultipleTimes;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.shr.com.time.TimeWithDayAttr;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class MultipleOvertimeContentDto {
    private int frameNo;
    private Integer startTime;
    private Integer endTime;
    private Integer fixedReasonCode;
    private String appReason;

    public static List<MultipleOvertimeContentDto> toDto(OvertimeWorkMultipleTimes overtimeWorkMultipleTimes) {
        List<MultipleOvertimeContentDto> result = new ArrayList<>();
        overtimeWorkMultipleTimes.getOvertimeHours().forEach(i -> {
            MultipleOvertimeContentDto tmp = new MultipleOvertimeContentDto();
            tmp.setFrameNo(i.getOvertimeNumber().v());
            tmp.setStartTime(i.getOvertimeHours().getStart().v());
            tmp.setEndTime(i.getOvertimeHours().getEnd().v());
            result.add(tmp);
        });
        overtimeWorkMultipleTimes.getOvertimeReasons().forEach(i -> {
            result.forEach(j -> {
                if (i.getOvertimeNumber().v() == j.getFrameNo()) {
                    j.setFixedReasonCode(i.getFixedReasonCode().map(PrimitiveValueBase::v).orElse(null));
                    j.setAppReason(i.getApplyReason().map(PrimitiveValueBase::v).orElse(null));
                }
            });
        });
        return result;
    }

    public static OvertimeWorkMultipleTimes toDomain(List<MultipleOvertimeContentDto> dtos) {
        if (CollectionUtil.isEmpty(dtos)) return new OvertimeWorkMultipleTimes(new ArrayList<>(), new ArrayList<>());
        List<OvertimeHour> overtimeHours = dtos.stream()
                .map(i -> new OvertimeHour(
                        new OvertimeNumber(i.getFrameNo()),
                        new TimeSpanForCalc(
                                new TimeWithDayAttr(i.getStartTime()),
                                new TimeWithDayAttr(i.getEndTime())
                        )
                )).collect(Collectors.toList());
        List<OvertimeReason> overtimeReasons = dtos.stream()
                .filter(i -> i.getFixedReasonCode() != null || !StringUtils.isEmpty(i.getAppReason()))
                .map(i -> new OvertimeReason(
                        new OvertimeNumber(i.getFrameNo()),
                        Optional.ofNullable(i.getFixedReasonCode() == null ? null : new AppStandardReasonCode(i.getFixedReasonCode())),
                        Optional.ofNullable(StringUtils.isEmpty(i.getAppReason()) ? null : new AppReason(i.getAppReason()))
                )).collect(Collectors.toList());
        return new OvertimeWorkMultipleTimes(overtimeHours, overtimeReasons);
    }
}
