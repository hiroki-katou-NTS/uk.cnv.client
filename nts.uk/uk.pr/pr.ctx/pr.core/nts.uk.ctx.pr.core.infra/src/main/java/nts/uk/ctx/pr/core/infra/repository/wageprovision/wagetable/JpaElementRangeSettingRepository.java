package nts.uk.ctx.pr.core.infra.repository.wageprovision.wagetable;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.pr.core.dom.wageprovision.wagetable.ElementRangeSetting;
import nts.uk.ctx.pr.core.dom.wageprovision.wagetable.ElementRangeSettingRepository;

/**
 * 
 * @author HungTT
 *
 */

@Stateless
public class JpaElementRangeSettingRepository extends JpaRepository implements ElementRangeSettingRepository {

	@Override
	public List<ElementRangeSetting> getAllElementRangeSetting(List<String> historyIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<ElementRangeSetting> getElementRangeSettingById(String historyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(ElementRangeSetting domain) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(ElementRangeSetting domain) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(String historyId) {
		// TODO Auto-generated method stub
		
	}

}
