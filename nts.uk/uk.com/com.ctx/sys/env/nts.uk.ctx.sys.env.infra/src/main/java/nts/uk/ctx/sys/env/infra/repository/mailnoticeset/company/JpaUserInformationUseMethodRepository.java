package nts.uk.ctx.sys.env.infra.repository.mailnoticeset.company;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.UserInformationUseMethod;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.UserInformationUseMethodRepository;
import nts.uk.ctx.sys.env.infra.entity.mailnoticeset.company.SevmtUserInfoUse;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;

import java.util.Optional;

@Stateless
public class JpaUserInformationUseMethodRepository extends JpaRepository implements UserInformationUseMethodRepository {

	// Select by cid
	private static final String SELECT_BY_CID = "SELECT m FROM SevmtUserInfoUse m WHERE m.cId = :cId";

	@Override
	public void insert(UserInformationUseMethod domain) {
		/**
		 * Step create(ユーザ情報の使用方法)
		 */
		SevmtUserInfoUse entity = JpaUserInformationUseMethodRepository.toEntity(domain);
		entity.setVersion(0);
		/**
		 * Step persist()
		 */
		this.commandProxy().insert(entity);
	}

	@Override
	public void update(UserInformationUseMethod domain) {
		SevmtUserInfoUse entity = JpaUserInformationUseMethodRepository.toEntity(domain);
		
		/**
		 * Step get(ログイン会社ID)
		 */
		SevmtUserInfoUse oldEntity = this.queryProxy().find(entity.getCId(), SevmtUserInfoUse.class).get();
		
		/**
		 * Step set(INPUT.ユーザ情報の利用方法)
		 */
		oldEntity.setUseOfProfile(entity.getUseOfProfile());
		oldEntity.setUseOfPassword(entity.getUseOfPassword());
		oldEntity.setUseOfNotice(entity.getUseOfNotice());
		oldEntity.setUseOfLanguage(entity.getUseOfLanguage());
		oldEntity.setPhoneNumberComUse(entity.getPhoneNumberComUse());
		oldEntity.setPhoneNumberComUpdatable(entity.getPhoneNumberComUpdatable());
		oldEntity.setPhoneNumberPsUse(entity.getPhoneNumberPsUse());
		oldEntity.setPhoneNumberPsUpdatable(entity.getPhoneNumberPsUpdatable());
		oldEntity.setUrgentPhoneNumber1Use(entity.getUrgentPhoneNumber1Use());
		oldEntity.setUrgentPhoneNumber1Updatable(entity.getUrgentPhoneNumber1Updatable());
		oldEntity.setUrgentPhoneNumber2Use(entity.getUrgentPhoneNumber2Use());
		oldEntity.setUrgentPhoneNumber2Updatable(entity.getUrgentPhoneNumber2Updatable());
		oldEntity.setDialInNumberUse(entity.getDialInNumberUse());
		oldEntity.setDialInNumberUpdatable(entity.getDialInNumberUpdatable());
		oldEntity.setExtensionNumberUse(entity.getExtensionNumberUse());
		oldEntity.setExtensionNumberUpdatable(entity.getExtensionNumberUpdatable());
		oldEntity.setMailComUse(entity.getMailComUse());
		oldEntity.setMailComUpdatable(entity.getMailComUpdatable());
		oldEntity.setMailPsUse(entity.getMailPsUse());
		oldEntity.setMailPsUpdatable(entity.getMailPsUpdatable());
		oldEntity.setPhoneMailComUse(entity.getPhoneMailComUse());
		oldEntity.setPhoneMailComUpdatable(entity.getPhoneMailComUpdatable());
		oldEntity.setPhoneMailPsUse(entity.getPhoneMailPsUse());
		oldEntity.setPhoneMailPsUpdatable(entity.getPhoneMailPsUpdatable());
		oldEntity.setOtherContact1Name(entity.getOtherContact1Name());
		oldEntity.setOtherContact1Use(entity.getOtherContact1Use());
		oldEntity.setOtherContact2Name(entity.getOtherContact2Name());
		oldEntity.setOtherContact2Use(entity.getOtherContact2Use());
		oldEntity.setOtherContact3Name(entity.getOtherContact3Name());
		oldEntity.setOtherContact3Use(entity.getOtherContact3Use());
		oldEntity.setOtherContact4Name(entity.getOtherContact4Name());
		oldEntity.setOtherContact4Use(entity.getOtherContact4Use());
		oldEntity.setOtherContact5Name(entity.getOtherContact5Name());
		oldEntity.setOtherContact5Use(entity.getOtherContact5Use());
		
		/**
		 * Step persist()
		 */
		this.commandProxy().update(oldEntity);
	}

	@Override
	public Optional<UserInformationUseMethod> findByCId(String cid) {
		return this.queryProxy().query(SELECT_BY_CID, SevmtUserInfoUse.class).setParameter("cId", cid)
				.getSingle(UserInformationUseMethod::createFromMemento);
	}

	private static SevmtUserInfoUse toEntity(UserInformationUseMethod domain) {
		SevmtUserInfoUse entity = new SevmtUserInfoUse();
		domain.setMemento(entity);
		entity.setContractCd(AppContexts.user().contractCode());
		return entity;
	}
}
