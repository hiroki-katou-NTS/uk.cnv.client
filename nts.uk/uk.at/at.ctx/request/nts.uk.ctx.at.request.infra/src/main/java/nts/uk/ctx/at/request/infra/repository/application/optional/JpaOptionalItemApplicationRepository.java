package nts.uk.ctx.at.request.infra.repository.application.optional;


import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplication;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplicationRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.optionalitemappsetting.OptionalItemApplicationTypeCode;
import nts.uk.ctx.at.request.infra.entity.application.KrqdtApplication;
import nts.uk.ctx.at.request.infra.entity.application.optional.KrqdtAppAnyv;
import nts.uk.ctx.at.request.infra.entity.application.optional.KrqdtAppAnyvPk;
import nts.uk.ctx.at.shared.dom.scherec.anyitem.AnyItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemAmount;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemValue;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Transactional
public class JpaOptionalItemApplicationRepository extends JpaRepository implements OptionalItemApplicationRepository {

    private static final String FIND_BY_APP_ID = "SELECT *  "
            + "FROM KRQDT_APP_ANYV as a INNER JOIN KRQDT_APPLICATION as b ON a.APP_ID = b.APP_ID"
            + " WHERE a.APP_ID = @appID AND a.CID = @companyId ORDER BY a.ANYV_NO ASC";

    private static final String FIND_OPTIONAL_ITEM_ENTITY = "SELECT a FROM KrqdtAppAnyv a where a.KrqdtAppAnyvPk.companyID = :cId" +
            " and a.KrqdtAppAnyvPk.appID = :appId";

    private static final String FIND_APPLICATION = "SELECT a FROM KrqdtApplication a where a.pk.companyID = :cId" +
            " and a.pk.appID = :appId";

    @Override
    public void save(OptionalItemApplication optItemApp) {
        List<KrqdtAppAnyv> entities = toEntity(optItemApp);
        this.commandProxy().insertAll(entities);
    }

    @Override
    public void  update(OptionalItemApplication domain, Application application) {
        String cid = AppContexts.user().companyId();
        Optional<KrqdtApplication> applicationEntity = this.queryProxy().query(FIND_APPLICATION, KrqdtApplication.class)
                .setParameter("cId", cid)
                .setParameter("appId", domain.getAppID()).getSingle();
        if (applicationEntity.isPresent()) {
            KrqdtApplication krqdtApplication = applicationEntity.get();
            krqdtApplication.setOpAppReason(application.getOpAppReason().isPresent() ? application.getOpAppReason().get().v() : null);
            krqdtApplication.setOpAppStandardReasonCD(application.getOpAppStandardReasonCD().isPresent() ? application.getOpAppStandardReasonCD().get().v() : null);
            this.commandProxy().update(applicationEntity.get());
        }
        List<KrqdtAppAnyv> entityList = this.queryProxy().query(FIND_OPTIONAL_ITEM_ENTITY, KrqdtAppAnyv.class)
                .setParameter("cId", cid)
                .setParameter("appId", domain.getAppID())
                .getList();
        if (entityList.size() > 0) {
            this.commandProxy().removeAll(entityList);
            this.getEntityManager().flush();
        }
        List<KrqdtAppAnyv> entities = toEntity(domain);
        this.commandProxy().insertAll(entities);
    }

    @Override
    public Optional<OptionalItemApplication> getByAppId(String companyId, String appId) {
        List<OptionalItemApplication> optionalItemApplications = new NtsStatement(FIND_BY_APP_ID, this.jdbcProxy())
                .paramString("appID", appId)
                .paramString("companyId", companyId)
                .getList(c -> toDomain(c));
        Optional<OptionalItemApplication> optionalItemApplication = optionalItemApplications.stream().findFirst();
        if (optionalItemApplication.isPresent()) {
            List<AnyItemValue> collect = optionalItemApplications.stream().map(item -> item.getOptionalItems().get(0)).collect(Collectors.toList());
            optionalItemApplication.get().setOptionalItems(collect);
        }
        return optionalItemApplication;
    }

    @Override
    public void remove(OptionalItemApplication optItemApp) {
        List<KrqdtAppAnyv> entities = this.toEntity(optItemApp);
        List<KrqdtAppAnyvPk> keys = entities.stream().map(i -> new KrqdtAppAnyvPk(
                i.getKrqdtAppAnyvPk().companyID,
                i.getKrqdtAppAnyvPk().appID,
                i.getKrqdtAppAnyvPk().anyvCd,
                i.getKrqdtAppAnyvPk().anyvNo
        )).collect(Collectors.toList());
        this.commandProxy().removeAll(KrqdtAppAnyv.class, keys);
    }

    private OptionalItemApplication toDomain(NtsResultRecord res) {
        AnyItemValue anyItemValue = new AnyItemValue(new AnyItemNo(res.getInt("ANYV_NO")),
                Optional.ofNullable(res.getBigDecimal("COUNT_VAL") != null ? new AnyItemTimes(res.getBigDecimal("COUNT_VAL")) : null),
                Optional.ofNullable(res.getInt("MONEY_VAL") != null ? new AnyItemAmount(res.getInt("MONEY_VAL")) : null),
                Optional.ofNullable(res.getInt("TIME_VAL") != null ? new AnyItemTime(res.getInt("TIME_VAL")) : null)
        );
        OptionalItemApplication application = new OptionalItemApplication(new OptionalItemApplicationTypeCode(res.getString("ANYV_CD")), Arrays.asList(anyItemValue));
        application.setAppType(EnumAdaptor.valueOf(res.getInt("APP_TYPE"), ApplicationType.class));
        return application;
    }

    private List<KrqdtAppAnyv> toEntity(OptionalItemApplication domain) {
        String cid = AppContexts.user().companyId();
        List<KrqdtAppAnyv> entities = new ArrayList<>();
        domain.getOptionalItems().forEach(anyItemValue -> {
            if (anyItemValue.getTimes().isPresent() || anyItemValue.getTime().isPresent() || anyItemValue.getAmount().isPresent()) {
                KrqdtAppAnyv entity = new KrqdtAppAnyv();
                entity.setKrqdtAppAnyvPk(new KrqdtAppAnyvPk(
                        cid,
                        domain.getAppID(),
                        Integer.parseInt(domain.getCode().v()),
                        anyItemValue.getItemNo().v()
                ));
                entity.setTimes(anyItemValue.getTimes().isPresent() ? anyItemValue.getTimes().get().v() : null);
                entity.setTime(anyItemValue.getTime().isPresent() ? anyItemValue.getTime().get().v() : null);
                entity.setMoneyValue(anyItemValue.getAmount().isPresent() ? anyItemValue.getAmount().get().v() : null);
                entities.add(entity);
            }
        });
        return entities;
    }

}
