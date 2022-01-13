package nts.uk.ctx.at.request.infra.entity.application.overtime;

import lombok.NoArgsConstructor;
import nts.arc.primitive.PrimitiveValueBase;
import nts.arc.time.clock.ClockHourMinute;
import nts.arc.time.clock.ClockHourMinuteSpan;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeHour;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeNumber;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeReason;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeWorkMultipleTimes;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "KRQDT_APP_OVERTIME_MULTI_TIMES")
@NoArgsConstructor
public class KrqdtAppOvertimeMultiTimes extends ContractUkJpaEntity {
    @EmbeddedId
    public KrqdtAppOvertimeMultiTimesPK pk;

    @Column(name = "START_TIME")
    public int startTime;

    @Column(name = "END_TIME")
    public int endTime;

    @Column(name = "FIXED_REASON")
    public Integer fixedReason;

    @Column(name = "APP_REASON")
    public String applyReason;

    @ManyToOne
    @PrimaryKeyJoinColumns({
            @PrimaryKeyJoinColumn(name="CID", referencedColumnName="CID"),
            @PrimaryKeyJoinColumn(name="APP_ID", referencedColumnName="APP_ID")
    })
    public KrqdtAppOverTime appOvertime;

    @Override
    protected Object getKey() {
        return pk;
    }

    public static OvertimeWorkMultipleTimes toDomain(List<KrqdtAppOvertimeMultiTimes> entities) {
        List<OvertimeHour> overtimeHours = entities.stream().map(e -> new OvertimeHour(
                new OvertimeNumber(e.pk.overtimeNumber),
                TimeSpanForCalc.create(ClockHourMinuteSpan.create(new ClockHourMinute(e.startTime), new ClockHourMinute(e.endTime)))
        )).collect(Collectors.toList());
        List<OvertimeReason> overtimeReasons = entities.stream().map(e -> new OvertimeReason(
                new OvertimeNumber(e.pk.overtimeNumber),
                e.fixedReason == null ? Optional.empty() : Optional.of(new AppStandardReasonCode(e.fixedReason)),
                e.applyReason == null ? Optional.empty() : Optional.of(new AppReason(e.applyReason))
        )).collect(Collectors.toList());
        return new OvertimeWorkMultipleTimes(overtimeHours, overtimeReasons);
    }

    public static List<KrqdtAppOvertimeMultiTimes> fromDomain(String companyId, String appId, OvertimeWorkMultipleTimes domain) {
        List<KrqdtAppOvertimeMultiTimes> entites = new ArrayList<>();
        domain.getOvertimeHours().forEach(i -> {
            KrqdtAppOvertimeMultiTimes e = new KrqdtAppOvertimeMultiTimes();
            e.pk = new KrqdtAppOvertimeMultiTimesPK(companyId, appId, i.getOvertimeNumber().v());
            e.startTime = i.getOvertimeHours().start();
            e.endTime = i.getOvertimeHours().end();
            domain.getOvertimeReasons().stream().filter(j -> j.getOvertimeNumber().equals(i.getOvertimeNumber())).findFirst().ifPresent(reason -> {
                e.fixedReason = reason.getFixedReasonCode().map(PrimitiveValueBase::v).orElse(null);
                e.applyReason = reason.getApplyReason().map(PrimitiveValueBase::v).orElse(null);
            });
            entites.add(e);
        });
        return entites;
    }
}
