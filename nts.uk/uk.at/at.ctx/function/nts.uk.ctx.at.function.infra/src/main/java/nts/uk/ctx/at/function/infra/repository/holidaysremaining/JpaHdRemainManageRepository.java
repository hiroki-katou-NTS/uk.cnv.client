package nts.uk.ctx.at.function.infra.repository.holidaysremaining;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.function.dom.holidaysremaining.HolidaysRemainingManagement;
import nts.uk.ctx.at.function.dom.holidaysremaining.ItemOutputForm;
import nts.uk.ctx.at.function.dom.holidaysremaining.Overtime;
import nts.uk.ctx.at.function.dom.holidaysremaining.repository.HolidaysRemainingManagementRepository;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.ItemSelectionEnum;
import nts.uk.ctx.at.function.infra.entity.holidaysremaining.KfnmtRptHdRemainOut;
import nts.uk.ctx.at.function.infra.entity.holidaysremaining.KfnmtRptHdRemainHdsp;
import nts.uk.ctx.at.function.infra.entity.holidaysremaining.KfnmtRptHdRemainHdspPk;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaHdRemainManageRepository extends JpaRepository implements HolidaysRemainingManagementRepository {

    private static final String SELECT_ALL_QUERY_STRING = "SELECT f FROM KfnmtRptHdRemainOut f";
    private static final String SELECT_BY_KEY_STRING = SELECT_ALL_QUERY_STRING
            + " WHERE  f.cid =:cid AND  f.cd =:cd ";
    private static final String SELECT_BY_COMPANY_ID = SELECT_ALL_QUERY_STRING
            + " WHERE  f.cid =:cid ";
    private static final String DELETE_BY_LAYOUTID_CID = " DELETE FROM KfnmtRptHdRemainHdsp f " +
            " WHERE f.cid = :cid " +
            " AND f.kfnmtRptHdRemainHdspPk.layoutId = :layoutId ";

    private static final String DELETE_BY_LAYOUTID = " DELETE FROM KfnmtRptHdRemainOut f " +
            " WHERE f.layoutId = :layoutId ";


    @Override
    public Optional<HolidaysRemainingManagement> getHolidayManagerByCidAndExecCd(String companyID, String code) {
        return this.queryProxy().query(SELECT_BY_KEY_STRING, KfnmtRptHdRemainOut.class).setParameter("cd", code)
                .setParameter("cid", companyID).getSingle(JpaHdRemainManageRepository::toDomain);
    }

    @Override
    public Optional<HolidaysRemainingManagement> getHolidayManagerByLayOutId(String layOutId) {
        Optional<KfnmtRptHdRemainOut> optEntity = this.queryProxy().find(layOutId, KfnmtRptHdRemainOut.class);
        return optEntity.map(JpaHdRemainManageRepository::toDomain);

    }

    @Override
    public void insert(HolidaysRemainingManagement domain) {
        Optional<KfnmtRptHdRemainOut> duplicateDomain =
                this.queryProxy().find(domain.getLayOutId(), KfnmtRptHdRemainOut.class);
        if (duplicateDomain.isPresent())
            throw new BusinessException("Msg_3");
        this.commandProxy().insert(this.toEntity(domain));
    }

    @Override
    public void update(HolidaysRemainingManagement domain) {
        KfnmtRptHdRemainOut updateData = this.toEntity(domain);
        Optional<KfnmtRptHdRemainOut> optOldData = this.queryProxy().find(updateData.getKey(),
                KfnmtRptHdRemainOut.class);
        if (optOldData.isPresent()) {
            this.commandProxy().update(updateData);
        }
    }

    @Override
    public void remove(String companyId, String code) {

    }

    @Override
    public void remove(String layOutId) {
        Optional<KfnmtRptHdRemainOut> optEntity = this.queryProxy().find(layOutId, KfnmtRptHdRemainOut.class);
        if(optEntity.isPresent()){
            KfnmtRptHdRemainOut entity = optEntity.get();
            this.commandProxy().remove(KfnmtRptHdRemainOut.class,entity.layoutId);
        }
    }

    @Override
    public List<HolidaysRemainingManagement> getHolidayManagerLogByCompanyId(String companyId) {
        return this.queryProxy().query(SELECT_BY_COMPANY_ID, KfnmtRptHdRemainOut.class).setParameter("cid", companyId)
                .getList(JpaHdRemainManageRepository::toDomain);
    }

    private KfnmtRptHdRemainOut toEntity(HolidaysRemainingManagement domain) {
        return new KfnmtRptHdRemainOut(
                domain.getLayOutId(),
                domain.getItemSelectionCategory().value,
                domain.getEmployeeId().isPresent() ? domain.getEmployeeId().get() : null,
                domain.getCompanyID(),
                domain.getCode().v(),
                domain.getName().v(),
                domain.getListItemsOutput().getAnnualHoliday().isYearlyHoliday() ? 1 : 0,
                domain.getListItemsOutput().getAnnualHoliday().isInsideHalfDay() ? 1 : 0,
                domain.getListItemsOutput().getAnnualHoliday().isInsideHours() ? 1 : 0,
                domain.getListItemsOutput().getYearlyReserved().isYearlyReserved() ? 1 : 0,
                domain.getListItemsOutput().getSubstituteHoliday().isOutputItemSubstitute() ? 1 : 0,
                domain.getListItemsOutput().getSubstituteHoliday().isRepresentSubstitute() ? 1 : 0,
                domain.getListItemsOutput().getSubstituteHoliday().isRemainingChargeSubstitute() ? 1 : 0,
                domain.getListItemsOutput().getPause().isPauseItem() ? 1 : 0,
                domain.getListItemsOutput().getPause().isUndigestedPause() ? 1 : 0,
                domain.getListItemsOutput().getPause().isNumberRemainingPause() ? 1 : 0,
                domain.getListItemsOutput().getOutOfTime().isOvertimeItem() ? 1 : 0,
                domain.getListItemsOutput().getOutOfTime().isOvertimeRemaining() ? 1 : 0,
                domain.getListItemsOutput().getOutOfTime().isOvertimeOverUndigested() ? 1 : 0,
                domain.getListItemsOutput().getHolidays().isOutputItemsHolidays() ? 1 : 0,
                domain.getListItemsOutput().getHolidays().isOutputHolidayForward() ? 1 : 0,
                domain.getListItemsOutput().getHolidays().isMonthlyPublic() ? 1 : 0,
                domain.getListItemsOutput().getChildNursingVacation().isChildNursingLeave() ? 1 : 0,
                domain.getListItemsOutput().getNursingcareLeave().isNursingLeave() ? 1 : 0,
                domain.getListItemsOutput().getSpecialHoliday().stream()
                        .map(itemDetai -> new KfnmtRptHdRemainHdsp(
                                new KfnmtRptHdRemainHdspPk(
                                        domain.getLayOutId(),
                                        itemDetai),
                                domain.getCompanyID(),
                                domain.getItemSelectionCategory().value,
                                domain.getEmployeeId().isPresent() ? domain.getEmployeeId().get() : null,
                                domain.getCode().v(),
                                null))
                        .collect(Collectors.toList()));
    }

    private static HolidaysRemainingManagement toDomain(KfnmtRptHdRemainOut entity) {
        return new HolidaysRemainingManagement(
                entity.cid,
                entity.cd,
                entity.name,
                new ItemOutputForm(
                        Boolean.valueOf(entity.nursingCareLeave).compareTo(false) > 0,
                        Boolean.valueOf(entity.remainChargeSub).compareTo(false) > 0,
                        Boolean.valueOf(entity.representSub).compareTo(false) > 0,
                        Boolean.valueOf(entity.outItemSub).compareTo(false) > 0,
                        Boolean.valueOf(entity.outputHolidayForward).compareTo(false) > 0,
                        Boolean.valueOf(entity.monthlyPublic).compareTo(false) > 0,
                        Boolean.valueOf(entity.outputItemsHolidays).compareTo(false) > 0,
                        Boolean.valueOf(entity.childCareLeave).compareTo(false) > 0,
                        Boolean.valueOf(entity.yearlyHoliday).compareTo(false) > 0,
                        Boolean.valueOf(entity.insideHours).compareTo(false) > 0,
                        Boolean.valueOf(entity.insideHalfDay).compareTo(false) > 0,
                        Boolean.valueOf(entity.numRemainPause).compareTo(false) > 0,
                        Boolean.valueOf(entity.undigestedPause).compareTo(false) > 0,
                        Boolean.valueOf(entity.pauseItem).compareTo(false) > 0,
                        Boolean.valueOf(entity.hD60HItem).compareTo(false) > 0,
                        Boolean.valueOf(entity.hD60HRemain).compareTo(false) > 0,
                        Boolean.valueOf(entity.hD60HUndigested).compareTo(false) > 0,
                        Boolean.valueOf(entity.yearlyReserved).compareTo(false) > 0,
                        entity.kfnmtSpecialHolidays.stream()
                                .map(itemDetail -> itemDetail.kfnmtRptHdRemainHdspPk.specialCd)
                                .collect(Collectors.toList())),
                entity.layoutId,
                EnumAdaptor.valueOf(entity.itemSelType, ItemSelectionEnum.class),
                entity.sid != null ? Optional.of(entity.sid) : Optional.empty()
        );

    }
}
