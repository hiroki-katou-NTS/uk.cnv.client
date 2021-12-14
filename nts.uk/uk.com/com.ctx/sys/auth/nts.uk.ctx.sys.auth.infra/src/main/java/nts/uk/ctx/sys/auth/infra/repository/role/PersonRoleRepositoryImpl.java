package nts.uk.ctx.sys.auth.infra.repository.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.auth.dom.role.personrole.PersonRole;
import nts.uk.ctx.sys.auth.dom.role.personrole.PersonRoleRepository;
import nts.uk.ctx.sys.auth.infra.entity.role.SacmtRolePerson;

@Stateless
public class PersonRoleRepositoryImpl extends JpaRepository implements PersonRoleRepository {

	
	/**
	 * JPQL: find (without where)
	 */
	private static final String FIND_NO_WHERE = "SELECT e FROM SacmtRolePerson e";

	/**
	 * JPQL: find by role id
	 */
	private static final String FIND_BY_ROLE_ID = FIND_NO_WHERE + " WHERE e.roleId = :roleId ";

	/**
	 * JPQL: find by list role id
	 */
	private static final String FIND_BY_LIST_ROLE_ID = FIND_NO_WHERE + " WHERE e.roleId IN :roleIds ";

	@Override
	public Optional<PersonRole> find(String roleId) {
		SacmtRolePerson entity = this.queryProxy().query(FIND_BY_ROLE_ID, SacmtRolePerson.class)
				.setParameter("roleId", roleId).getSingleOrNull();
		PersonRole domain = null;
		if (entity != null) {
			domain = toDomain(entity);
		}
		return Optional.ofNullable(domain);
	}

	private static PersonRole toDomain(SacmtRolePerson entity) {
		PersonRole domain = new PersonRole();
		domain.setRoleId(entity.getRoleId());
		domain.setReferFutureDate(entity.isReferFutureDate());
		return domain;
	}

	@Override
	public List<PersonRole> find(List<String> roleIds) {
		List<PersonRole> result = new ArrayList<>();
		if(roleIds != null){
			CollectionUtil.split(roleIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
				result.addAll(this.queryProxy().query(FIND_BY_LIST_ROLE_ID, SacmtRolePerson.class)
					.setParameter("roleIds", subList).getList().stream().map(x -> toDomain(x)).collect(Collectors.toList()));
			});
		}
		return result;
	}

	public void insert(PersonRole personRole) {
		this.commandProxy().insert(toEntity(personRole));
	}

	@Override
	public void update(PersonRole personRole) {
		SacmtRolePerson updateEntity = this.queryProxy().find(personRole.getRoleId(), SacmtRolePerson.class).get();
		updateEntity.setReferFutureDate(personRole.getReferFutureDate());
		this.commandProxy().update(updateEntity);
	}

	@Override
	public void remove(String roleId) {	
			this.commandProxy().remove(SacmtRolePerson.class, roleId);
	}

	
	private static SacmtRolePerson  toEntity(PersonRole personRole){
		SacmtRolePerson entity = new SacmtRolePerson();
		entity.setRoleId(personRole.getRoleId());
		entity.setReferFutureDate(personRole.getReferFutureDate());
		return entity;
	}

	
}
