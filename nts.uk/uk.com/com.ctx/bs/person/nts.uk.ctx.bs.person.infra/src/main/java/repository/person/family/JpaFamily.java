package repository.person.family;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import entity.person.family.BpsmtFamily;
import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.person.dom.person.family.Family;
import nts.uk.ctx.bs.person.dom.person.family.FamilyRepository;

@Stateless
public class JpaFamily extends JpaRepository implements FamilyRepository {

	public final String GET_ALL_BY_PID = "SELECT c FROM BpsmtFamily c WHERE c.pid = :pid";
	
	private static final String SELECT_FAMILY_BY_ID = "SELECT c FROM BpsmtFamily c WHERE c.ppsmtFamilyPk.familyId = :familyId";

	private List<Family> toListFamily(List<BpsmtFamily> listEntity) {
		List<Family> lstFamily = new ArrayList<>();
		if (!listEntity.isEmpty()) {
			listEntity.stream().forEach(c -> {
				Family family = toDomainFamily(c);

				lstFamily.add(family);
			});
		}
		return lstFamily;
	}

	private Family toDomainFamily(BpsmtFamily entity) {
		val domain = Family.createFromJavaType(
				entity.birthday,
				entity.deathDate, 
				entity.entryDate, 
				entity.expDate, 
				entity.ppsmtFamilyPk.familyId, 
				entity.name,
				entity.NameKana,
				entity.nameMultiLang,
				entity.nameMultiLangKana,
				entity.nameRomaji,
				entity.nameRomajiKana, 
				entity.nationality,
				entity.occupationName, 
				entity.pid, 
				entity.relationShip, 
				entity.SupportCareType,
				entity.todukedeName,
				entity.TogSepDivType, 
				entity.workStudentType);
		return domain;
	}

	@Override
	public Family getFamilyById(String familyId) {
		Optional<Family> family = this.queryProxy().query(SELECT_FAMILY_BY_ID, BpsmtFamily.class)
				.setParameter("familyId", familyId).getSingle(x -> toDomainFamily(x));
		return family.isPresent()?family.get():null;
	}

	@Override
	public List<Family> getListByPid(String pid) {
		List<BpsmtFamily> listEntity = this.queryProxy().query(GET_ALL_BY_PID, BpsmtFamily.class)
				.setParameter("pid", pid).getList();
		return toListFamily(listEntity);
	}

}
