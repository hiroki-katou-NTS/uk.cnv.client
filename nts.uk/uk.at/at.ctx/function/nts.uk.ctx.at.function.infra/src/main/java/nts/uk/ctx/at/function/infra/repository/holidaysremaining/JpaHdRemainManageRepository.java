package nts.uk.ctx.at.function.infra.repository.holidaysremaining;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.error.BusinessException;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.function.dom.holidaysremaining.HolidaysRemainingManagement;
import nts.uk.ctx.at.function.dom.holidaysremaining.ItemOutputForm;
import nts.uk.ctx.at.function.dom.holidaysremaining.repository.HolidaysRemainingManagementRepository;
import nts.uk.ctx.at.function.infra.entity.holidaysremaining.KfnmtRptRemlstOutitem;
import nts.uk.ctx.at.function.infra.entity.holidaysremaining.KfnmtHdRemainManagePk;
import nts.uk.ctx.at.function.infra.entity.holidaysremaining.KfnmtRptRemlstOuthdsp;
import nts.uk.ctx.at.function.infra.entity.holidaysremaining.KfnmtSpecialHolidayPk;

@Stateless
public class JpaHdRemainManageRepository extends JpaRepository implements HolidaysRemainingManagementRepository {

	private static final String SELECT_ALL_QUERY_STRING = "SELECT f FROM KfnmtRptRemlstOutitem f";
	private static final String SELECT_BY_KEY_STRING = SELECT_ALL_QUERY_STRING
			+ " WHERE  f.hdRemainManagePk.cid =:cid AND  f.hdRemainManagePk.cd =:cd ";
	private static final String SELECT_BY_COMPANY_ID = SELECT_ALL_QUERY_STRING
			+ " WHERE  f.hdRemainManagePk.cid =:cid ";

	@Override
	public Optional<HolidaysRemainingManagement> getHolidayManagerByCidAndExecCd(String companyID, String code) {
		return this.queryProxy().query(SELECT_BY_KEY_STRING, KfnmtRptRemlstOutitem.class).setParameter("cd", code)
				.setParameter("cid", companyID).getSingle(JpaHdRemainManageRepository::toDomain);
	}

	@Override
	public void insert(HolidaysRemainingManagement domain) {
		Optional<HolidaysRemainingManagement> duplicateDomain = getHolidayManagerByCidAndExecCd(domain.getCompanyID(),
				domain.getCode().v());
		if (duplicateDomain.isPresent())
			throw new BusinessException("Msg_3");

		this.commandProxy().insert(this.toEntity(domain));
	}

	@Override
	public void update(HolidaysRemainingManagement domain) {
		KfnmtRptRemlstOutitem updateData = this.toEntity(domain);
		Optional<KfnmtRptRemlstOutitem> optOldData = this.queryProxy().find(updateData.hdRemainManagePk,
				KfnmtRptRemlstOutitem.class);
		if (optOldData.isPresent()) {
			KfnmtRptRemlstOutitem oldData = optOldData.get();
			oldData.name = updateData.name;
			oldData.yearlyHoliday = updateData.yearlyHoliday;
			oldData.insideHalfDay = updateData.insideHalfDay;
			oldData.insideHours = updateData.insideHours;
			oldData.yearlyReserved = updateData.yearlyReserved;
			oldData.outItemSub = updateData.outItemSub;
			oldData.representSub = updateData.representSub;
			oldData.remainChargeSub = updateData.remainChargeSub;
			oldData.pauseItem = updateData.pauseItem;
			oldData.undigestedPause = updateData.undigestedPause;
			oldData.numRemainPause = updateData.numRemainPause;
			oldData.outputItemsHolidays = updateData.outputItemsHolidays;
			oldData.outputHolidayForward = updateData.outputHolidayForward;
			oldData.monthlyPublic = updateData.monthlyPublic;
			oldData.childCareLeave = updateData.childCareLeave;
			oldData.nursingCareLeave = updateData.nursingCareLeave;
			oldData.kfnmtSpecialHolidays = updateData.kfnmtSpecialHolidays;
			this.commandProxy().update(oldData);
		}
	}

	@Override
	public void remove(String companyId, String code) {
		KfnmtHdRemainManagePk kfnmtSpecialHolidayPk = new KfnmtHdRemainManagePk(companyId, code);
		Optional<KfnmtRptRemlstOutitem> optOldData = this.queryProxy().find(kfnmtSpecialHolidayPk,
				KfnmtRptRemlstOutitem.class);
		if (optOldData.isPresent()) {
			this.commandProxy().remove(KfnmtRptRemlstOutitem.class, kfnmtSpecialHolidayPk);
		}
	}

	@Override
	public List<HolidaysRemainingManagement> getHolidayManagerLogByCompanyId(String companyId) {
		return this.queryProxy().query(SELECT_BY_COMPANY_ID, KfnmtRptRemlstOutitem.class).setParameter("cid", companyId)
				.getList(JpaHdRemainManageRepository::toDomain);
	}

	private KfnmtRptRemlstOutitem toEntity(HolidaysRemainingManagement domain) {
		return new KfnmtRptRemlstOutitem(new KfnmtHdRemainManagePk(domain.getCompanyID(), domain.getCode().v()),
				domain.getName().v(), domain.getListItemsOutput().getAnnualHoliday().isYearlyHoliday() ? 1 : 0,
				domain.getListItemsOutput().getAnnualHoliday().isInsideHalfDay() ? 1 : 0,
				domain.getListItemsOutput().getAnnualHoliday().isInsideHours() ? 1 : 0,
				domain.getListItemsOutput().getYearlyReserved().isYearlyReserved() ? 1 : 0,
				domain.getListItemsOutput().getSubstituteHoliday().isOutputItemSubstitute() ? 1 : 0,
				domain.getListItemsOutput().getSubstituteHoliday().isRepresentSubstitute() ? 1 : 0,
				domain.getListItemsOutput().getSubstituteHoliday().isRemainingChargeSubstitute() ? 1 : 0,
				domain.getListItemsOutput().getPause().isPauseItem() ? 1 : 0,
				domain.getListItemsOutput().getPause().isUndigestedPause() ? 1 : 0,
				domain.getListItemsOutput().getPause().isNumberRemainingPause() ? 1 : 0,
				domain.getListItemsOutput().getHolidays().isOutputItemsHolidays() ? 1 : 0,
				domain.getListItemsOutput().getHolidays().isOutputHolidayForward() ? 1 : 0,
				domain.getListItemsOutput().getHolidays().isMonthlyPublic() ? 1 : 0,
				domain.getListItemsOutput().getChildNursingVacation().isChildNursingLeave() ? 1 : 0,
				domain.getListItemsOutput().getNursingcareLeave().isNursingLeave() ? 1
						: 0,
				domain.getListItemsOutput().getSpecialHoliday().stream()
						.map(itemDetai -> new KfnmtRptRemlstOuthdsp(
								new KfnmtSpecialHolidayPk(domain.getCompanyID(), domain.getCode().v(), itemDetai),
								null))
						.collect(Collectors.toList()));
	}

	private static HolidaysRemainingManagement toDomain(KfnmtRptRemlstOutitem entity) {
		return new HolidaysRemainingManagement(entity.hdRemainManagePk.cid, entity.hdRemainManagePk.cd, entity.name,
				new ItemOutputForm(entity.nursingCareLeave > 0, entity.remainChargeSub > 0, entity.representSub > 0,
						entity.outItemSub > 0, entity.outputHolidayForward > 0, entity.monthlyPublic > 0,
						entity.outputItemsHolidays > 0, entity.childCareLeave > 0, entity.yearlyHoliday > 0,
						entity.insideHours > 0, entity.insideHalfDay > 0, entity.numRemainPause > 0,
						entity.undigestedPause > 0, entity.pauseItem > 0, entity.yearlyReserved > 0,
						entity.kfnmtSpecialHolidays.stream()
								.map(itemDetail -> itemDetail.kfnmtSpecialHolidayPk.specialCd)
								.collect(Collectors.toList())));

	}
}
