package nts.uk.ctx.at.record.dom.workrecord.workrecord;

import java.util.List;
import java.util.Optional;

import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;

/**
 * 就業確定Repository		
 * @author chungnt
 *
 */

public interface EmploymentConfirmedRepository {

	/**
	 * 	[1]  insert(就業確定)
	 * @param domain
	 */
	public void insert(EmploymentConfirmed domain);

	/**
	 *  [2]  delete(就業確定)
	 * @param domain
	 */
	public void delete(EmploymentConfirmed domain);

	/**
	 * [3]  取得する
	 * @param companyId
	 * @param workplaceId
	 * @param closureId
	 * @param processYM
	 * @return
	 */
	public Optional<EmploymentConfirmed> get(String companyId, String workplaceId, ClosureId closureId, YearMonth processYM);
	
	/**
	 * 	[4]  取得する
	 * @param companyId
	 * @param workplaceId
	 * @param closureId
	 * @param processYM
	 * @return
	 */
	public List<EmploymentConfirmed> get(String companyId, List<String> workplaceId, ClosureId closureId, YearMonth processYM);
	
	public List<EmploymentConfirmed> getListByCompany(String companyId);
}
