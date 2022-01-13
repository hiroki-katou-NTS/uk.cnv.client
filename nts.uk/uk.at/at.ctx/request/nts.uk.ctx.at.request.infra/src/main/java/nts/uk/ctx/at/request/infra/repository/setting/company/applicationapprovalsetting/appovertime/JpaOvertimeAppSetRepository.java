package nts.uk.ctx.at.request.infra.repository.setting.company.applicationapprovalsetting.appovertime;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.FlexWorkAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeQuotaSetUse;
import nts.uk.ctx.at.request.infra.entity.setting.company.applicationapprovalsetting.appovertime.KrqmtAppOvertime;
import nts.uk.ctx.at.request.infra.entity.setting.company.applicationapprovalsetting.appovertime.KrqmtAppOvertimeFrame;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.otworkapply.OtWorkAppReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.otworkapply.OtWorkAppReflectRepository;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import org.apache.commons.lang3.BooleanUtils;

import javax.ejb.Stateless;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Stateless
public class JpaOvertimeAppSetRepository extends JpaRepository implements OvertimeAppSetRepository, OtWorkAppReflectRepository {

    @Override
    public Optional<OvertimeAppSet> findSettingByCompanyId(String companyId) {
        return this.queryProxy().find(companyId, KrqmtAppOvertime.class).map(KrqmtAppOvertime::toOvertimeAppSetDomain);
    }

    @Override
    public Optional<OtWorkAppReflect> findReflectByCompanyId(String companyId) {
        return this.queryProxy().find(companyId, KrqmtAppOvertime.class).map(KrqmtAppOvertime::toOvertimeWorkAppReflect);
    }

    @Override
    public void saveOvertimeAppSet(OvertimeAppSet overtimeAppSet, OtWorkAppReflect overtimeWorkAppReflect) {
        Optional<KrqmtAppOvertime> optEntity = this.queryProxy().find(overtimeAppSet.getCompanyID(), KrqmtAppOvertime.class);
        if (optEntity.isPresent()) {
            KrqmtAppOvertime entity = optEntity.get();
            entity.setPreExcessAtr(overtimeAppSet.getOvertimeLeaveAppCommonSet().getPreExcessDisplaySetting().value);
            entity.setExtraTimeExcessAtr(overtimeAppSet.getOvertimeLeaveAppCommonSet().getExtratimeExcessAtr().value);
            entity.setExtraTimeDisplayAtr(overtimeAppSet.getOvertimeLeaveAppCommonSet().getExtratimeDisplayAtr().value);
            entity.setAtdExcessAtr(overtimeAppSet.getOvertimeLeaveAppCommonSet().getPerformanceExcessAtr().value);
            entity.setAtdExcessOverrideAtr(overtimeAppSet.getOvertimeLeaveAppCommonSet().getOverrideSet().value);
            entity.setInstructExcessAtr(overtimeAppSet.getOvertimeLeaveAppCommonSet().getCheckOvertimeInstructionRegister().value);
            entity.setDvgcExcessAtr(overtimeAppSet.getOvertimeLeaveAppCommonSet().getCheckDeviationRegister().value);
            entity.setInstructRequiredAtr(BooleanUtils.toInteger(overtimeAppSet.getApplicationDetailSetting().getRequiredInstruction()));
            entity.setPreRequiredAtr(overtimeAppSet.getApplicationDetailSetting().getPreRequireSet().value);
            entity.setTimeInputUseAtr(overtimeAppSet.getApplicationDetailSetting().getTimeInputUse().value);
            entity.setTimeCalUseAtr(overtimeAppSet.getApplicationDetailSetting().getTimeCalUse().value);
            entity.setWorkTimeIniAtr(overtimeAppSet.getApplicationDetailSetting().getAtworkTimeBeginDisp().value);
            entity.setEndWorkTimeIniAtr(BooleanUtils.toInteger(overtimeAppSet.getApplicationDetailSetting().isDispSystemTimeWhenNoWorkTime()));
            entity.setAtdWorkReflectAtr(overtimeWorkAppReflect.getReflectActualWorkAtr().value);
            entity.setPreWorkReflectAtr(overtimeWorkAppReflect.getBefore().getReflectWorkInfoAtr().value);
            entity.setPreInputTimeReflectAtr(overtimeWorkAppReflect.getBefore().getReflectActualOvertimeHourAtr().value);
            entity.setPreBreakTimeReflectAtr(overtimeWorkAppReflect.getBefore().getBreakLeaveApplication().getBreakReflectAtr().value);
            entity.setPostWorkTimeReflectAtr(overtimeWorkAppReflect.getAfter().getWorkReflect().value);
            entity.setPostBpTimeReflectAtr(overtimeWorkAppReflect.getAfter().getOthersReflect().getReflectPaytimeAtr().value);
//            entity.setPostAnyvTimeReflectAtr(overtimeWorkAppReflect.getAfter().getOthersReflect().getReflectOptionalItemsAtr().value);
            entity.setPostDvgcReflectAtr(overtimeWorkAppReflect.getAfter().getOthersReflect().getReflectDivergentReasonAtr().value);
            entity.setPostBreakTimeReflectAtr(overtimeWorkAppReflect.getAfter().getBreakLeaveApplication().getBreakReflectAtr().value);
            this.commandProxy().update(entity);
        } else {
            this.commandProxy().insert(KrqmtAppOvertime.create(overtimeAppSet, overtimeWorkAppReflect));
        }
    }

    @Override
    public List<OvertimeQuotaSetUse> getOvertimeQuotaSetting(String companyId) {
        String sql = "Select a from KrqmtAppOvertimeFrame a where a.pk.companyId = :companyId";
        List<KrqmtAppOvertimeFrame> entities = this.queryProxy().query(sql, KrqmtAppOvertimeFrame.class).setParameter("companyId", companyId).getList();
        return KrqmtAppOvertimeFrame.toDomains(entities);
    }

    @Override
    public Optional<OvertimeQuotaSetUse> getOvertimeQuotaSetting(String companyId, OvertimeAppAtr overtimeAppAtr, FlexWorkAtr flexWorkAtr) {
        String sql = "Select a from KrqmtAppOvertimeFrame a where a.pk.companyId = :companyId AND a.pk.overtimeAtr = :overtimeAtr AND a.pk.flexAtr = :flexAtr";
        List<KrqmtAppOvertimeFrame> entities = this.queryProxy().query(sql, KrqmtAppOvertimeFrame.class)
                .setParameter("companyId", companyId)
                .setParameter("overtimeAtr", overtimeAppAtr.value)
                .setParameter("flexAtr", flexWorkAtr.value)
                .getList();
        if (entities.isEmpty()) return Optional.empty();
        return Optional.of(KrqmtAppOvertimeFrame.toDomains(entities).get(0));
    }

    @Override
    public void saveOvertimeQuotaSet(String companyId, OvertimeQuotaSetUse overtimeQuotaSet) {
        Optional<KrqmtAppOvertime> optEntity = this.queryProxy().find(companyId, KrqmtAppOvertime.class);
        if (optEntity.isPresent()) {
            KrqmtAppOvertime entity = optEntity.get();
            entity.getOvertimeFrames().removeIf(e -> e.getPk().overtimeAtr == overtimeQuotaSet.getOvertimeAppAtr().value && e.getPk().flexAtr == overtimeQuotaSet.getFlexWorkAtr().value);
//            if (overtimeQuotaSet.getOvertimeAppAtr() != OvertimeAppAtr.EARLY_NORMAL_OVERTIME) {
//                entity.getOvertimeFrames().removeIf(s -> s.getPk().overtimeAtr == OvertimeAppAtr.EARLY_NORMAL_OVERTIME.value
//                        && s.getPk().flexAtr == overtimeQuotaSet.getFlexWorkAtr().value
//                        && overtimeQuotaSet.getTargetOvertimeLimit().stream().map(OverTimeFrameNo::v).anyMatch(v -> v == s.getTargetFrame())
//                );
//            }
            this.commandProxy().update(entity);
            this.getEntityManager().flush();
        }
        List<KrqmtAppOvertimeFrame> entities = KrqmtAppOvertimeFrame.fromDomains(companyId, Collections.singletonList(overtimeQuotaSet));
//        if (overtimeQuotaSet.getOvertimeAppAtr() != OvertimeAppAtr.EARLY_NORMAL_OVERTIME) {
//            entities.addAll(KrqmtAppOvertimeFrame.fromDomains(
//                    companyId,
//                    Collections.singletonList(
//                            new OvertimeQuotaSetUse(
//                                    OvertimeAppAtr.EARLY_NORMAL_OVERTIME,
//                                    overtimeQuotaSet.getFlexWorkAtr(),
//                                    overtimeQuotaSet.getTargetOvertimeLimit()
//                            )
//                    )
//            ));
//        }
        this.commandProxy().insertAll(entities);
    }
}
