package nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday;

import java.util.List;
import java.util.Optional;

/**
 * 公休付与残数データ
 * @author Hop.NT
 *
 */
public interface PublicHolidayRemainRepository{
	
	Optional<PublicHolidayRemain> get(String sid);
	
	void add(PublicHolidayRemain domain);
	
	void addAll(List<PublicHolidayRemain> domains);
	
	void update(PublicHolidayRemain domain);
	
	void updateAll(List<PublicHolidayRemain> domains);
	
	void delete(String sid);
	List<PublicHolidayRemain> getAll(String cid, List<String> sids);
}
