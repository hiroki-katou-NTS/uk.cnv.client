package nts.uk.ctx.at.function.infra.entity.alarmworkplace.checkcondition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.AlarmCheckCdtWorkplaceCategory;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.applicationapproval.AlarmAppApprovalCheckCdt;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.basic.AlarmMasterBasicCheckCdt;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.daily.AlarmMasterDailyCheckCdt;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.monthly.AlarmMonthlyCheckCdt;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.schedule.AlarmScheduleCheckCdt;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.workplace.AlarmMasterWkpCheckCdt;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Entity
@Getter
@NoArgsConstructor
@Table(name = "KFNMT_CAT_MAP_EACHTYPE")
public class KfnmtCatMapEachType extends UkJpaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    public KfnmtCatMapEachTypePk pk;

    @Column(name = "CONTRACT_CD")
    public String contractCD;

    @Override
    protected Object getKey() {
        return this.pk;
    }

    public String getCategoryItemCD() {
        return this.pk.categoryItemCD;
    }

    public static List<KfnmtCatMapEachType> toEntity(AlarmCheckCdtWorkplaceCategory domain) {
        List<KfnmtCatMapEachType> entities = new ArrayList<>();
        List<String> alarmCheckIds = new ArrayList<>();
        List<String> optionalIds = new ArrayList<>();
        switch (domain.getCategory()) {
            case MASTER_CHECK_BASIC: {
                alarmCheckIds = ((AlarmMasterBasicCheckCdt) domain.getCondition()).getAlarmCheckWkpID();
                break;
            }
            case MASTER_CHECK_WORKPLACE: {
                alarmCheckIds = ((AlarmMasterWkpCheckCdt) domain.getCondition()).getAlarmCheckWkpID();
                break;
            }
            case MASTER_CHECK_DAILY: {
                alarmCheckIds = ((AlarmMasterDailyCheckCdt) domain.getCondition()).getAlarmCheckWkpID();
                break;
            }
            case SCHEDULE_DAILY: {
                alarmCheckIds = ((AlarmScheduleCheckCdt) domain.getCondition()).getAlarmCheckWkpID();
                optionalIds = ((AlarmScheduleCheckCdt) domain.getCondition()).getListOptionalIDs();
                break;
            }
            case MONTHLY: {
                alarmCheckIds = ((AlarmMonthlyCheckCdt) domain.getCondition()).getAlarmCheckWkpID();
                optionalIds = ((AlarmMonthlyCheckCdt) domain.getCondition()).getListOptionalIDs();
                break;
            }
            case APPLICATION_APPROVAL: {
                alarmCheckIds = ((AlarmAppApprovalCheckCdt) domain.getCondition()).getAlarmCheckWkpID();
                break;
            }
        }
        alarmCheckIds.forEach(i -> {
            KfnmtCatMapEachTypePk key = new KfnmtCatMapEachTypePk(
                    AppContexts.user().companyId(),
                    domain.getCategory().value,
                    domain.getCode().v(),
                    i,
                    BooleanUtils.toInteger(true)
            );
            entities.add(new KfnmtCatMapEachType(
                    key,
                    AppContexts.user().contractCode()
            ));
        });
        optionalIds.forEach(i -> {
            KfnmtCatMapEachTypePk key = new KfnmtCatMapEachTypePk(
                    AppContexts.user().companyId(),
                    domain.getCategory().value,
                    domain.getCode().v(),
                    i,
                    BooleanUtils.toInteger(false)
            );
            entities.add(new KfnmtCatMapEachType(
                    key,
                    AppContexts.user().contractCode()
            ));
        });
        return entities;
    }
}
