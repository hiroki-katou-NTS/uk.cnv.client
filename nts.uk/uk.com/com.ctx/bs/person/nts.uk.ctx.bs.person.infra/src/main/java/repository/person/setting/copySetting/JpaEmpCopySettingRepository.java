package repository.person.setting.copySetting;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.person.dom.person.info.setting.copySetting.EmpCopySetting;
import nts.uk.ctx.bs.person.dom.person.setting.copySetting.EmpCopySettingRepository;

@Stateless
public class JpaEmpCopySettingRepository extends JpaRepository implements EmpCopySettingRepository {

	@Override
	public Optional<EmpCopySetting> find(String companyId) {
		
		return null;
	}

}
