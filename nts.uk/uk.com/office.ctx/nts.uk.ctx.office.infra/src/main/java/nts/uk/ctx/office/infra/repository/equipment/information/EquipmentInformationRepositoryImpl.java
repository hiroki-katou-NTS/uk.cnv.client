package nts.uk.ctx.office.infra.repository.equipment.information;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformationRepository;
import nts.uk.ctx.office.infra.entity.equipment.information.OfidtEquipment;
import nts.uk.ctx.office.infra.entity.equipment.information.OfidtEquipmentPK;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class EquipmentInformationRepositoryImpl extends JpaRepository
												implements EquipmentInformationRepository {
	
	private static final String FIND_BY_CID = "SELECT t FROM OfidtEquipment t "
			+ "WHERE t.pk.cid = :cid ";
	
	private static final String FIND_BY_CID_AND_CLS_CODE = "SELECT t FROM OfidtEquipment t "
			+ "WHERE t.pk.cid = :cid AND t.equipmentClsCode = :clsCode ";
	
	private static final String FIND_BY_CID_AND_CODES = "SELECT t FROM OfidtEquipment t "
			+ "WHERE t.pk.cid = :cid AND t.pk.code IN :codes ";
	
	private static final String FIND_BY_CID_AND_CLS_CODE_AND_DATE = FIND_BY_CID_AND_CLS_CODE
			+ "AND t.effectiveStartDate <= :date AND t.effectiveEndDate >= :date";
	
	private static final String FIND_BY_CLS_CODE = "SELECT t FROM OfidtEquipment t "
			+ "WHERE t.equipmentClsCode = :clsCode";
	
	public OfidtEquipment toEntity(EquipmentInformation domain) {
		OfidtEquipment entity = new OfidtEquipment();
		domain.setMemento(entity);
		entity.getPk().setCid(AppContexts.user().companyId());
		return entity;
	}

	@Override
	public void insert(EquipmentInformation domain) {
		this.commandProxy().insert(this.toEntity(domain));
	}

	@Override
	public void update(EquipmentInformation domain) {
		this.commandProxy().update(this.toEntity(domain));
	}

	@Override
	public void delete(String cid, String code) {
		this.commandProxy().remove(OfidtEquipment.class, new OfidtEquipmentPK(cid, code));
	}

	@Override
	public List<EquipmentInformation> findByCid(String cid) {
		return this.queryProxy().query(FIND_BY_CID, OfidtEquipment.class)
				.setParameter("cid", cid)
				.getList(EquipmentInformation::createFromMemento);
	}

	@Override
	public Optional<EquipmentInformation> findByPk(String cid, String code) {
		return this.queryProxy().find(new OfidtEquipmentPK(cid, code), OfidtEquipment.class)
				.map(EquipmentInformation::createFromMemento);
	}

	@Override
	public List<EquipmentInformation> findByCidAndClsCode(String cid, String equipmentClsCode) {
		return this.queryProxy().query(FIND_BY_CID_AND_CLS_CODE, OfidtEquipment.class)
				.setParameter("cid", cid).setParameter("clsCode", equipmentClsCode)
				.getList(EquipmentInformation::createFromMemento);
	}

	@Override
	public List<EquipmentInformation> findByCidAndCodes(String cid, List<String> codes) {
		return this.queryProxy().query(FIND_BY_CID_AND_CODES, OfidtEquipment.class)
				.setParameter("cid", cid).setParameter("codes", codes)
				.getList(EquipmentInformation::createFromMemento);
	}

	@Override
	public List<EquipmentInformation> findByClsCodeAndDate(String cid, String equipmentClsCode, GeneralDate date) {
		return this.queryProxy().query(FIND_BY_CID_AND_CLS_CODE_AND_DATE, OfidtEquipment.class)
				.setParameter("cid", cid).setParameter("clsCode", equipmentClsCode)
				.setParameter("date", date)
				.getList(EquipmentInformation::createFromMemento);
	}

	@Override
	public List<EquipmentInformation> findByCode(String equipmentClsCode) {
		return this.queryProxy().query(FIND_BY_CLS_CODE, OfidtEquipment.class)
				.setParameter("clsCode", equipmentClsCode).getList(EquipmentInformation::createFromMemento);
	}

}
