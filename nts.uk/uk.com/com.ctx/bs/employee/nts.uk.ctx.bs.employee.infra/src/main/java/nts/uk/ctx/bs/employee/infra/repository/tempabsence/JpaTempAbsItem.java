package nts.uk.ctx.bs.employee.infra.repository.tempabsence;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.transaction.Transactional;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsItemRepository;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsenceHisItem;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.AfterChildbirth;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.AnyLeave;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.CareHoliday;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.ChildCareHoliday;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.Leave;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.MidweekClosure;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.SickLeave;
import nts.uk.ctx.bs.employee.infra.entity.temporaryabsence.BsymtTempAbsHisItem;

@Stateless
@Transactional
public class JpaTempAbsItem extends JpaRepository implements TempAbsItemRepository {
	
	@Override
	public Optional<TempAbsenceHisItem> getItemByHitoryID(String historyId) {
		Optional<BsymtTempAbsHisItem> option = this.queryProxy().find(historyId, BsymtTempAbsHisItem.class);
		if (option.isPresent()) {
			return Optional.of(toDomain(option.get()));
		}
		return Optional.empty();
	}
	
	@Override
	public List<TempAbsenceHisItem> getListItemByEmpId(String employeeId) {
		// TODO
		return null;
	}
	
	private TempAbsenceHisItem toDomain(BsymtTempAbsHisItem ent) {
		Boolean multiple = ent.multiple == null ? null : ent.multiple == 1;
		Boolean sameFamily = ent.sameFamily == null ? null : ent.sameFamily == 1;
		Boolean spouseIsLeave = ent.spouseIsLeave == null ? null : ent.spouseIsLeave == 1;
		return TempAbsenceHisItem.createTempAbsenceHisItem(ent.tempAbsFrameNo, ent.histId, ent.sid,
				ent.remarks, Integer.valueOf(ent.soInsPayCategory), multiple, ent.familyMemberId, sameFamily,
				ent.childType, ent.createDate, spouseIsLeave, ent.sameFamilyDays);
	}

	@Override
	public void add(TempAbsenceHisItem domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	@Override
	public void update(TempAbsenceHisItem domain) {
		Optional<BsymtTempAbsHisItem> tempAbs = this.queryProxy().find(domain.getHistoryId(),
				BsymtTempAbsHisItem.class);

		if (!tempAbs.isPresent()) {
			throw new RuntimeException("invalid TempAbsenceHisItem");
		}
		updateEntity(domain, tempAbs.get());

		this.commandProxy().update(tempAbs.get());
	}

	@Override
	public void delete(String histId) {
		Optional<BsymtTempAbsHisItem> tempAbs = this.queryProxy().find(histId, BsymtTempAbsHisItem.class);

		if (!tempAbs.isPresent()) {
			throw new RuntimeException("invalid TempAbsenceHisItem");
		}

		this.commandProxy().remove(BsymtTempAbsHisItem.class, histId);
	}

	/**
	 * Covert from domain to entity
	 * 
	 * @param domain
	 * @return
	 */
	private BsymtTempAbsHisItem toEntity(TempAbsenceHisItem domain) {
		int tempAbsenceFrNo = domain.getTempAbsenceFrNo().v().intValue();
		switch (tempAbsenceFrNo) {
		case 1:
			Leave leave = (Leave) domain;
			return new BsymtTempAbsHisItem(leave.getHistoryId(), leave.getEmployeeId(),
					tempAbsenceFrNo, leave.getRemarks().v(), leave.getSoInsPayCategory());
		case 2:
			MidweekClosure midweek = (MidweekClosure) domain;
			return new BsymtTempAbsHisItem(midweek.getHistoryId(), midweek.getEmployeeId(),
					tempAbsenceFrNo, midweek.getRemarks().v(), midweek.getSoInsPayCategory(),
					midweek.getMultiple());
		case 3:
			AfterChildbirth childBirth = (AfterChildbirth) domain;
			return new BsymtTempAbsHisItem(childBirth.getHistoryId(), childBirth.getEmployeeId(),
					tempAbsenceFrNo, childBirth.getRemarks().v(),
					childBirth.getSoInsPayCategory(),childBirth.getFamilyMemberId());
		case 4:
			ChildCareHoliday childCare = (ChildCareHoliday) domain;
			return new BsymtTempAbsHisItem(childCare.getHistoryId(), childCare.getEmployeeId(),
					tempAbsenceFrNo, childCare.getRemarks().v(), childCare.getSoInsPayCategory(), childCare.getSameFamily(), childCare.getChildType(),childCare.getFamilyMemberId(), 
					childCare.getCreateDate(), childCare.getSpouseIsLeave());
		case 5:
			CareHoliday careLeave = (CareHoliday) domain;
			return new BsymtTempAbsHisItem(careLeave.getHistoryId(), careLeave.getEmployeeId(),
					tempAbsenceFrNo, careLeave.getRemarks().v(), careLeave.getSoInsPayCategory(), careLeave.getSameFamily() ,
					careLeave.getSameFamilyDays(), careLeave.getFamilyMemberId());
		case 6:
			SickLeave sickLeave = (SickLeave) domain;
			return new BsymtTempAbsHisItem(sickLeave.getHistoryId(), sickLeave.getEmployeeId(),
					tempAbsenceFrNo, sickLeave.getRemarks().v(), sickLeave.getSoInsPayCategory());
		case 7:
			AnyLeave anyLeave = (AnyLeave) domain;
			return new BsymtTempAbsHisItem(anyLeave.getHistoryId(), anyLeave.getEmployeeId(),
					tempAbsenceFrNo, anyLeave.getRemarks().v(), anyLeave.getSoInsPayCategory());
		default:
			return null;
		}

	}

	/**
	 * Update entity from domain
	 * 
	 * @param domain
	 * @return
	 */
	private void updateEntity(TempAbsenceHisItem domain, BsymtTempAbsHisItem entity) {
		// Common value
//		entity.histId = domain.getHistoryId();
		if (domain.getTempAbsenceFrNo() != null){
			entity.tempAbsFrameNo = domain.getTempAbsenceFrNo().v().intValue();
		}
		if (domain.getRemarks() != null){
			entity.remarks = domain.getRemarks().v();
		}
		if (domain.getSoInsPayCategory() != null){
			entity.soInsPayCategory = domain.getSoInsPayCategory();
		}

		switch (domain.getTempAbsenceFrNo().v().intValue()) {
		case 1:
			break;
		case 2:
			MidweekClosure midweek = (MidweekClosure) domain;
			if (midweek.getMultiple() != null){
				entity.multiple = midweek.getMultiple() ? 1 : 0;
			}
			break;
		case 3:
			AfterChildbirth childBirth = (AfterChildbirth) domain;
			if (childBirth.getFamilyMemberId() != null){
				entity.familyMemberId = childBirth.getFamilyMemberId();
			}
			break;
		case 4:
			ChildCareHoliday childCare = (ChildCareHoliday) domain;
			if (childCare.getSameFamily() != null){
				entity.sameFamily = childCare.getSameFamily() ? 1 : 0;
			}
			if (childCare.getChildType() != null){
				entity.childType = childCare.getChildType();
			}
			if (childCare.getFamilyMemberId() != null){
				entity.familyMemberId = childCare.getFamilyMemberId();
			}
			if (childCare.getCreateDate() != null){
				entity.createDate = childCare.getCreateDate();
			}
			if (childCare.getSpouseIsLeave() != null){
				entity.spouseIsLeave = childCare.getSpouseIsLeave() ? 1 : 0;
			}
			break;
		case 5:
			CareHoliday careLeave = (CareHoliday) domain;
			if (careLeave.getSameFamily() != null){
				entity.sameFamily = careLeave.getSameFamily() ? 1 : 0;
			}
			if (careLeave.getSameFamilyDays() != null){
				entity.sameFamilyDays = careLeave.getSameFamilyDays();
			}
			if (careLeave.getFamilyMemberId() != null){
				entity.familyMemberId = careLeave.getFamilyMemberId();
			}
			break;
		case 6:
			break;
		case 7:
			break;
		default:
		}

	}

}
