package nts.uk.ctx.office.infra.repository.equipment;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassification;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationCode;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationName;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationRepository;
import nts.uk.ctx.office.infra.entity.equipment.OfidtEquipmentCls;
import nts.uk.ctx.office.infra.entity.equipment.OfidtEquipmentClsPK;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaEquipmentClassificationRepo extends JpaRepository implements EquipmentClassificationRepository {

	private static final String FIND_ALL = "SELECT e FROM OfidtEquipmentCls e WHERE e.pk.contractCd = :contractCd";
	private static final String FIND_BY_CLS_CD = "SELECT e FROM OfidtEquipmentCls e WHERE e.pk.contractCd = :contractCd AND e.pk.code = :code";
	private static final String FIND_FROM_CLS_CDS = "SELECT e FROM OfidtEquipmentCls e WHERE e.pk.contractCd = :contractCd AND e.pk.code IN :codeList";
	private static final String FIND_BY_CONTRACT_CD = "SELECT e FROM OfidtEquipmentCls e WHERE e.pk.contractCd = :contractCd ORDER BY e.pk.code ASC";
	
	private EquipmentClassification toDomain(OfidtEquipmentCls entity) {
		return new EquipmentClassification(
			new EquipmentClassificationCode(entity.getPk().getCode()),
			new EquipmentClassificationName(entity.getName())
		);
	}
	
	private OfidtEquipmentCls toEntity(EquipmentClassification domain) {
		OfidtEquipmentCls entity = new OfidtEquipmentCls();
		OfidtEquipmentClsPK pk = new OfidtEquipmentClsPK();
		pk.setContractCd(AppContexts.user().contractCode());
		pk.setCode(domain.getCode().v());
		entity.setPk(pk);
		entity.setName(domain.getName().v());
		return entity;
	}
	
	@Override
	public void insert(EquipmentClassification domain) {
		OfidtEquipmentCls entity = this.toEntity(domain);
		this.commandProxy().insert(entity);
	}

	@Override
	public void update(EquipmentClassification domain) {
		OfidtEquipmentCls entity = this.toEntity(domain);
		this.commandProxy().update(entity);
	}

	@Override
	public void delete(String contractCd, String equipmentClsCd) {
		OfidtEquipmentClsPK pk = new OfidtEquipmentClsPK(contractCd, equipmentClsCd);
		this.commandProxy().remove(OfidtEquipmentCls.class, pk);
	}

	@Override
	public List<EquipmentClassification> getAll(String contractCd) {
		return this.queryProxy()
			.query(FIND_ALL, OfidtEquipmentCls.class)
			.setParameter("contractCd", contractCd)
			.getList(e -> toDomain(e));
	}

	@Override
	public Optional<EquipmentClassification> getByClassificationCode(String contractCd, String clsCd) {
		return this.queryProxy()
				.query(FIND_BY_CLS_CD, OfidtEquipmentCls.class)
				.setParameter("contractCd", contractCd)
				.setParameter("code", clsCd)
				.getSingle()
				.map(e -> this.toDomain(e));
	}
	
	@Override
	public List<EquipmentClassification> getFromClsCodeList(String contractCd, List<String> clsCds) {
		return this.queryProxy()
				.query(FIND_FROM_CLS_CDS, OfidtEquipmentCls.class)
				.setParameter("contractCd", contractCd)
				.setParameter("codeList", clsCds)
				.getList(e -> this.toDomain(e));
	}

	@Override
	public Optional<EquipmentClassification> getFirst(String contractCd) {
		return this.queryProxy().query(FIND_BY_CONTRACT_CD, OfidtEquipmentCls.class)
				.setParameter("contractCd", contractCd)
				.getList(this::toDomain)
				.stream().findFirst();
	}

	@Override
	public Optional<EquipmentClassification> getByOptionalCode(String contractCd, Optional<String> optClsCd) {
		// if　コード.isEmpty()
		if (!optClsCd.isPresent()) {
			// return　[7] 最小の設備分類を取得する
			return this.getFirst(contractCd);
		}
		// $設備分類　＝　[5] Get(契約コード、コード)
		Optional<EquipmentClassification> optEquipmentCls = this.getByClassificationCode(contractCd, optClsCd.get());
		// if　$設備分類.isEmpty()
		if (!optEquipmentCls.isPresent()) {
			// return　[7] 最小の設備分類を取得する
			return this.getFirst(contractCd);
		}
		// return　$設備分類
		return optEquipmentCls;
	}
	
}

