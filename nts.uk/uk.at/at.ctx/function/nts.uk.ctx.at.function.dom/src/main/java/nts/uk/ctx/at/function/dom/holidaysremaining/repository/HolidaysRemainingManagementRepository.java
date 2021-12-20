package nts.uk.ctx.at.function.dom.holidaysremaining.repository;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.function.dom.holidaysremaining.HolidaysRemainingManagement;

public interface HolidaysRemainingManagementRepository {

	List<HolidaysRemainingManagement> getHolidayManagerLogByCompanyId(String companyId);

	Optional<HolidaysRemainingManagement> getHolidayManagerByLayOutId(String layOutId);

	Optional<HolidaysRemainingManagement> getHolidayManagerByCidAndExecCd(String companyID, String code);

	void insert(HolidaysRemainingManagement domain);

	void update(HolidaysRemainingManagement domain);

	void remove(String companyId, String code);

	void remove(String layoutId);
}
