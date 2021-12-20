package nts.uk.ctx.at.function.infra.repository.alarmworkplace.checkcondition;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.primitive.PrimitiveValueBase;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.AlarmCheckConditionCode;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.AlarmCheckCdtWkpCtgRepository;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.AlarmCheckCdtWorkplaceCategory;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.ExtractionCondition;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.WorkplaceCategory;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.applicationapproval.AlarmAppApprovalCheckCdt;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.basic.AlarmMasterBasicCheckCdt;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.daily.AlarmMasterDailyCheckCdt;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.monthly.AlarmMonthlyCheckCdt;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.schedule.AlarmScheduleCheckCdt;
import nts.uk.ctx.at.function.dom.alarmworkplace.checkcondition.workplace.AlarmMasterWkpCheckCdt;
import nts.uk.ctx.at.function.infra.entity.alarmworkplace.checkcondition.KfnmtCatMapEachType;
import nts.uk.ctx.at.function.infra.entity.alarmworkplace.checkcondition.KfnmtCatMapEachTypePk;
import nts.uk.ctx.at.function.infra.entity.alarmworkplace.checkcondition.KfnmtWkpAlstchkConcat;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class JpaAlarmWkpAlstchkConcatRepository extends JpaRepository implements AlarmCheckCdtWkpCtgRepository {

    // Get Main Table
    private static final String GET_ALL = "select f from KfnmtWkpAlstchkConcat f";
    private static final String GET_BY_ID = GET_ALL + " where f.pk.companyID = :cid and f.pk.category = :ctg and f.pk.categoryItemCD = :ctgCD";
    private static final String GET_BY_CATEGORY_ID = GET_ALL + " where f.pk.companyID = :cid and f.pk.category = :ctg";
    private static final String GET_BY_CATEGORY_ID_AND_CODES = GET_ALL + " where f.pk.companyID = :cid and f.pk.category = :ctg " +
            " and f.pk.categoryItemCD IN :ctgCDs";

    // Get Sub Table
    private static final String GET_ALL_SUB = "select f from KfnmtCatMapEachType f";
    private static final String GET_SUB_BY_CTG = GET_ALL_SUB + " where f.pk.companyID = :cid and f.pk.category = :ctg";
    private static final String GET_SUB_BY_CTG_AND_CODES  = GET_ALL_SUB + " where f.pk.companyID = :cid and f.pk.category = :ctg " +
            " and f.pk.categoryItemCD IN :ctgCDs";
    private static final String GET_SUB_BY_CTG_AND_CODE  = GET_ALL_SUB + " where f.pk.companyID = :cid and f.pk.category = :ctg " +
            " and f.pk.categoryItemCD = :ctgCD";

    @Override
    public Optional<AlarmCheckCdtWorkplaceCategory> getByID(int category, String categoryItemCD) {
        Optional<KfnmtWkpAlstchkConcat> entity = this.queryProxy().query(GET_BY_ID, KfnmtWkpAlstchkConcat.class)
                .setParameter("cid", AppContexts.user().companyId())
                .setParameter("ctg", category)
                .setParameter("ctgCD", categoryItemCD)
                .getSingle();
        if (entity.isPresent()) {
            List<KfnmtCatMapEachType> entitySub = this.queryProxy()
                    .query(GET_SUB_BY_CTG_AND_CODE, KfnmtCatMapEachType.class)
                    .setParameter("cid", AppContexts.user().companyId())
                    .setParameter("ctg", category)
                    .setParameter("ctgCD", categoryItemCD)
                    .getList();
            return Optional.ofNullable(this.toDomain(Arrays.asList(entity.get()), entitySub).get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<AlarmCheckCdtWorkplaceCategory> getByCategory(WorkplaceCategory category) {
        List<KfnmtWkpAlstchkConcat> entityMain = this.queryProxy().query(GET_BY_CATEGORY_ID, KfnmtWkpAlstchkConcat.class)
                .setParameter("cid", AppContexts.user().companyId())
                .setParameter("ctg", category.value)
                .getList();
        List<KfnmtCatMapEachType> entitySub = this.queryProxy().query(GET_SUB_BY_CTG, KfnmtCatMapEachType.class)
                .setParameter("cid", AppContexts.user().companyId())
                .setParameter("ctg", category.value)
                .getList();
        return this.toDomain(entityMain, entitySub);
    }

    @Override
    public List<AlarmCheckCdtWorkplaceCategory> getBy(WorkplaceCategory category, List<AlarmCheckConditionCode> codes) {
        List<String> ctgCD = codes.stream().map(PrimitiveValueBase::v).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ctgCD)) return new ArrayList<>();

        List<KfnmtWkpAlstchkConcat> entityMain = this.queryProxy().query(GET_BY_CATEGORY_ID_AND_CODES, KfnmtWkpAlstchkConcat.class)
                .setParameter("cid", AppContexts.user().companyId())
                .setParameter("ctg", category.value)
                .setParameter("ctgCDs", ctgCD)
                .getList();
        List<KfnmtCatMapEachType> entitySub = this.queryProxy().query(GET_SUB_BY_CTG_AND_CODES, KfnmtCatMapEachType.class)
                .setParameter("cid", AppContexts.user().companyId())
                .setParameter("ctg", category.value)
                .setParameter("ctgCDs", ctgCD)
                .getList();
        return this.toDomain(entityMain, entitySub);
    }

    private List<AlarmCheckCdtWorkplaceCategory> toDomain(List<KfnmtWkpAlstchkConcat> entityMain, List<KfnmtCatMapEachType> entitySub) {
        Map<String, List<KfnmtCatMapEachType>> mapEntitySub = entitySub.stream().collect(Collectors.groupingBy(KfnmtCatMapEachType::getCategoryItemCD));
        List<String> errAlarmCheckIDs = new ArrayList<>();

        return entityMain.stream().map(i -> {
            WorkplaceCategory ctg = EnumAdaptor.valueOf(i.getPk().category, WorkplaceCategory.class);
            ExtractionCondition condition = null;
            List<String> extractionIDs = new ArrayList<>();
            List<String> optionalIDs = new ArrayList<>();
            if (mapEntitySub.containsKey(i.getPk().categoryItemCD)){
                List<KfnmtCatMapEachType> subItems = mapEntitySub.get(i.getPk().categoryItemCD);
                extractionIDs = subItems.stream().filter(x -> BooleanUtils.toBoolean(x.getPk().fixedOp))
                        .map(x -> x.getPk().wkAlarmCheckId).collect(Collectors.toList());
                optionalIDs = subItems.stream().filter(x -> !BooleanUtils.toBoolean(x.getPk().fixedOp))
                        .map(x -> x.getPk().wkAlarmCheckId).collect(Collectors.toList());
            }

            switch (ctg) {
                case MASTER_CHECK_BASIC: {
                    condition = new AlarmMasterBasicCheckCdt(extractionIDs);
                    break;
                }
                case MASTER_CHECK_WORKPLACE: {
                    condition = new AlarmMasterWkpCheckCdt(extractionIDs);
                    break;
                }
                case MASTER_CHECK_DAILY: {
                    condition = new AlarmMasterDailyCheckCdt(extractionIDs);
                    break;
                }
                case SCHEDULE_DAILY: {
                    condition = new AlarmScheduleCheckCdt(extractionIDs, optionalIDs);
                    break;
                }
                case MONTHLY: {
                    condition = new AlarmMonthlyCheckCdt(extractionIDs, optionalIDs);
                    break;
                }
                case APPLICATION_APPROVAL: {
                    condition = new AlarmAppApprovalCheckCdt(extractionIDs);
                    break;
                }
            }

            return new AlarmCheckCdtWorkplaceCategory(
                    i.getPk().category,
                    i.getPk().categoryItemCD,
                    i.getAlarmCdtName(),
                    condition
            );
        }).collect(Collectors.toList());

    }

    @Override
    public void register(AlarmCheckCdtWorkplaceCategory domain) {
        // Insert main table
        this.commandProxy().insert(KfnmtWkpAlstchkConcat.toEntity(domain));
        // Insert sub table
        this.commandProxy().insertAll(KfnmtCatMapEachType.toEntity(domain));
    }

    @Override
    public void update(AlarmCheckCdtWorkplaceCategory domain) {
        // Update main table
        this.commandProxy().update(KfnmtWkpAlstchkConcat.toEntity(domain));
        // Update sub table
        List<KfnmtCatMapEachType> entitySub = this.queryProxy()
                .query(GET_SUB_BY_CTG_AND_CODE, KfnmtCatMapEachType.class)
                .setParameter("cid", AppContexts.user().companyId())
                .setParameter("ctg", domain.getCategory().value)
                .setParameter("ctgCD", domain.getCode().v())
                .getList();
        this.commandProxy().removeAll(KfnmtCatMapEachType.class, entitySub.stream().map(KfnmtCatMapEachType::getPk).collect(Collectors.toList()));

        List<KfnmtCatMapEachType> entitiesSubAfterUpdate = KfnmtCatMapEachType.toEntity(domain);
        this.commandProxy().insertAll(entitiesSubAfterUpdate);

    }

    @Override
    public void delete(AlarmCheckCdtWorkplaceCategory domain) {
        List<KfnmtCatMapEachType> subEntities = KfnmtCatMapEachType.toEntity(domain);
        // Remove main table
        this.commandProxy().remove(KfnmtWkpAlstchkConcat.class, KfnmtWkpAlstchkConcat.toEntity(domain).pk);
        // Remove sub table
        this.commandProxy().removeAll(KfnmtCatMapEachType.class, subEntities.stream().map(KfnmtCatMapEachType::getPk).collect(Collectors.toList()));
    }

}
