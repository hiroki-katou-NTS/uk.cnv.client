package repository.person.info.setting.user;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.transaction.Transactional;

import entity.person.info.setting.user.BpsstUserSetting;
import entity.person.info.setting.user.BpsstUserSettingPk;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.person.dom.person.info.setting.user.UserSetting;
import nts.uk.ctx.bs.person.dom.person.info.setting.user.UserSettingRepository;

@Stateless
@Transactional
public class JpaUserSetting extends JpaRepository implements UserSettingRepository{

	private static final String CHECK_USER_SETTING_EXIST = "SELECT COUNT(i.BpsstUserSettingPk.employeeId) FROM BpsstUserSetting i"
			+ " WHERE i.BpsstUserSettingPk.employeeId = :employeeId ";
	
	private BpsstUserSetting createObject(UserSetting userSetting){
		BpsstUserSettingPk pk = new BpsstUserSettingPk(userSetting.getEmployeeId());
		BpsstUserSetting entity = new BpsstUserSetting(pk, userSetting.getEmpCodeValType().value, userSetting.getCardNoValType().value,
				userSetting.getRecentRegType().value, userSetting.getEmpCodeLetter(), userSetting.getCardNoLetter());
		return entity;
	}
	
	@Override
	public void createUserSetting(UserSetting userSetting) {
		BpsstUserSetting entity = this.createObject(userSetting);
		getEntityManager().persist(entity);
		
	}

	@Override
	public void updateUserSetting(UserSetting userSetting) {
		BpsstUserSetting entity = this.createObject(userSetting);
		this.commandProxy().update(entity);
	}

	@Override
	public boolean checkUserSettingExist(String employeeId) {
		Optional<Long> a = this.queryProxy().query(CHECK_USER_SETTING_EXIST, Long.class)
				.setParameter("employeeId", employeeId).getSingle();
		int count = a.isPresent() ? a.get().intValue() : 0;
		return count > 0;
	}

}
