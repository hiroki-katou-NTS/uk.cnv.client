package nts.uk.ctx.at.record.infra.repository.remainingnumber;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.remainingnumber.subhdmana.LeaveComDayOffManaRepository;
import nts.uk.ctx.at.record.dom.remainingnumber.subhdmana.LeaveComDayOffManagement;
import nts.uk.ctx.at.record.infra.entity.remainingnumber.subhdmana.KrcmtLeaveDayOffMana;
import nts.uk.ctx.at.record.infra.entity.remainingnumber.subhdmana.KrcmtLeaveDayOffManaPK;

@Stateless
public class JpaLeaveComDayOffManaRepository extends JpaRepository implements LeaveComDayOffManaRepository{

	@Override
	public void add(LeaveComDayOffManagement domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	@Override
	public void update(LeaveComDayOffManagement domain) {
		KrcmtLeaveDayOffManaPK key = new KrcmtLeaveDayOffManaPK(domain.getLeaveID(), domain.getComDayOffID());
		Optional<KrcmtLeaveDayOffMana> existed = this.queryProxy().find(key, KrcmtLeaveDayOffMana.class);
		if (existed.isPresent()){
			this.commandProxy().update(toEntity(domain));
		}
	}

	@Override
	public void delete(String leaveID, String comDayOffID) {
		KrcmtLeaveDayOffManaPK key = new KrcmtLeaveDayOffManaPK(leaveID, comDayOffID);
		Optional<KrcmtLeaveDayOffMana> existed = this.queryProxy().find(key, KrcmtLeaveDayOffMana.class);
		if (existed.isPresent()){
			this.commandProxy().remove(KrcmtLeaveDayOffMana.class, key);
		}
		
	}
	
	/** 
	 * Convert from enity to domain
	 * @param entity
	 * @return
	 */
	private LeaveComDayOffManagement toDomain(KrcmtLeaveDayOffMana entity){
		return new LeaveComDayOffManagement(entity.krcmtLeaveDayOffManaPK.leaveID, entity.krcmtLeaveDayOffManaPK.comDayOffID, entity.usedDays, entity.usedHours, entity.targetSelectionAtr);
	}
	
	/**
	 * Convert from domain to entity
	 * @param domain
	 * @return
	 */
	private KrcmtLeaveDayOffMana toEntity(LeaveComDayOffManagement domain){
		KrcmtLeaveDayOffManaPK key = new KrcmtLeaveDayOffManaPK(domain.getLeaveID(), domain.getComDayOffID());
		return new KrcmtLeaveDayOffMana(key, domain.getUsedDays().v(), domain.getUsedHours().v(), domain.getTargetSelectionAtr().value);
	}


}
