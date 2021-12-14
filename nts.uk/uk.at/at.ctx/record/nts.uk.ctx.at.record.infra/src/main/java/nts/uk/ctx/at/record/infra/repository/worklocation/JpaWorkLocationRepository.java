package nts.uk.ctx.at.record.infra.repository.worklocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.query.TypedQueryWrapper;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocation;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocationRepository;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkplacePossible;
import nts.uk.ctx.at.record.infra.entity.worklocation.KrcmtIP4Address;
import nts.uk.ctx.at.record.infra.entity.worklocation.KrcmtIP4AddressPK;
import nts.uk.ctx.at.record.infra.entity.worklocation.KrcmtWorkLocation;
import nts.uk.ctx.at.record.infra.entity.worklocation.KrcmtWorkplacePossible;
import nts.uk.ctx.at.record.infra.entity.worklocation.KrcmtWorkplacePossiblePK;
import nts.uk.ctx.at.record.infra.entity.worklocation.KwlmtWorkLocationPK;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.net.Ipv4Address;

@Stateless
/**
 * 
 * @author hieult
 *
 */
public class JpaWorkLocationRepository extends JpaRepository implements WorkLocationRepository {

	private static final String SELECT = "SELECT c FROM KrcmtWorkLocation c";
	private static final String SELECT_SINGLE = "SELECT c FROM KrcmtWorkLocation c WHERE c.kwlmtWorkLocationPK.contractCode = :contractCode AND c.kwlmtWorkLocationPK.workLocationCD = :workLocationCD";
	private static final String SELECT_ALL_BY_COMPANY = SELECT
			+ " WHERE c.kwlmtWorkLocationPK.contractCode = :contractCode order by c.kwlmtWorkLocationPK.workLocationCD asc";
	private static final String SELECT_CODE_AND_NAME = "SELECT c.kwlmtWorkLocationPK.workLocationCD, c.workLocationName FROM KrcmtWorkLocation c"
			+ " WHERE c.kwlmtWorkLocationPK.contractCode = :contractCode AND c.kwlmtWorkLocationPK.workLocationCD IN :workLocationCDs";
	private static final String SELECT_BY_CODES = "SELECT c FROM KrcmtWorkLocation c WHERE c.kwlmtWorkLocationPK.contractCode = :contractCode";

	private static final String SELECT_IPADDRESS_CODE_ORDERBY = "SELECT c FROM KrcmtIP4Address c"
			+ " WHERE c.krcmtIP4AddressPK.contractCode = :contractCode "
			+ " AND c.krcmtIP4AddressPK.workLocationCD = :workLocationCD "
			+ " ORDER BY c.krcmtIP4AddressPK.net1, c.krcmtIP4AddressPK.net2, c.krcmtIP4AddressPK.host1, c.krcmtIP4AddressPK.host2 ";

	private static final String SELECT_IPADDRESS_BY_START_END_IP = "SELECT c FROM KrcmtIP4Address c"
			+ " WHERE c.krcmtIP4AddressPK.contractCode = :contractCode " + " AND c.krcmtIP4AddressPK.net1 = :net1 "
			+ " AND c.krcmtIP4AddressPK.net2 = :net2 " + " AND c.krcmtIP4AddressPK.host1 = :host1 "
			+ " AND c.krcmtIP4AddressPK.host2 BETWEEN :host2 AND :endIP ";

	private static final String SELECT_IPADDRESS = "SELECT c FROM KrcmtIP4Address c"
			+ " WHERE c.krcmtIP4AddressPK.contractCode = :contractCode " + " AND c.krcmtIP4AddressPK.net1 = :net1 "
			+ " AND c.krcmtIP4AddressPK.net2 = :net2 " + " AND c.krcmtIP4AddressPK.host1 = :host1 "
			+ " AND c.krcmtIP4AddressPK.host2 = :host2 ";

	private static final String SELECT_BY_WORKPLACES = SELECT
			+ " WHERE c.kwlmtWorkLocationPK.contractCode = :contractCode"
			+ " AND c.krcmtWorkplacePossible.krcmtWorkplacePossiblePK.cid = :cid"
			+ " AND c.krcmtWorkplacePossible.workplaceId = :workplaceId";

	private static final String SELECT_BY_WORKPLACE = SELECT
			+ " INNER JOIN KrcmtWorkplacePossible p on p.krcmtWorkplacePossiblePK.workLocationCD = c.kwlmtWorkLocationPK.workLocationCD"
			+ " WHERE c.kwlmtWorkLocationPK.contractCode = :contractCode"
			+ " AND c.kwlmtWorkLocationPK.workLocationCD = :workLocationCD"
			+ " AND p.krcmtWorkplacePossiblePK.cid = :cid";

	@Override
	public List<WorkLocation> findAll(String contractCode) {
		List<WorkLocation> test = this.queryProxy().query(SELECT_ALL_BY_COMPANY, KrcmtWorkLocation.class)
				.setParameter("contractCode", contractCode).getList(c -> c.toDomain());
		return test;
	}

	@Override
	public Optional<WorkLocation> findByCode(String contractCode, String workPlaceCD) {
		Optional<WorkLocation> test = this.queryProxy().query(SELECT_SINGLE, KrcmtWorkLocation.class)
				.setParameter("contractCode", contractCode).setParameter("workLocationCD", workPlaceCD)
				.getSingle(c -> c.toDomain());
		return test;
	}

	@Override
	public Map<String, String> getNameByCode(String contractCode, List<String> listWorkLocationCd) {
		if (listWorkLocationCd.isEmpty()) {
			return new HashMap<String, String>();
		}
		return this.queryProxy().query(SELECT_CODE_AND_NAME, Object[].class).setParameter("contractCode", contractCode)
				.setParameter("workLocationCDs", listWorkLocationCd).getList().stream()
				.collect(Collectors.toMap(s -> String.valueOf(s[0]), s -> String.valueOf(s[1])));
	}

	@Override
	public List<WorkLocation> findByCodes(String contractCode, List<String> codes) {
		String queryString = SELECT_BY_CODES;

		TypedQueryWrapper<KrcmtWorkLocation> query = null;
		if (codes == null || codes.isEmpty()) {
			query = this.queryProxy().query(queryString, KrcmtWorkLocation.class).setParameter("contractCode",
					contractCode);
		} else {
			queryString += " AND c.kwlmtWorkLocationPK.workLocationCD IN :workLocationCD";
			query = this.queryProxy().query(queryString, KrcmtWorkLocation.class)
					.setParameter("contractCode", contractCode).setParameter("workLocationCD", codes);
		}

		List<WorkLocation> result = query.getList(c -> c.toDomain());
		return result;
	}

	@Override
	public void insertWorkLocation(WorkLocation workLocation) {
		this.commandProxy().insert(KrcmtWorkLocation.toEntity(workLocation));

	}

	@Override
	public void updateWorkLocation(WorkLocation workLocation) {
		Optional<KrcmtWorkLocation> oldData = this.queryProxy().query(SELECT_SINGLE, KrcmtWorkLocation.class)
				.setParameter("contractCode", workLocation.getContractCode().v())
				.setParameter("workLocationCD", workLocation.getWorkLocationCD().v()).getSingle();
		if (oldData.isPresent()) {
			KrcmtWorkLocation newData = KrcmtWorkLocation.toEntity(workLocation);
			oldData.get().workLocationName = newData.workLocationName;
			oldData.get().radius = newData.radius;
			oldData.get().latitude = newData.latitude;
			oldData.get().longitude = newData.longitude;
			
			if (newData.krcmtWorkplacePossible != null) {
				oldData.get().krcmtWorkplacePossible = new KrcmtWorkplacePossible(
						new KrcmtWorkplacePossiblePK(newData.krcmtWorkplacePossible.krcmtWorkplacePossiblePK.contractCode,
								newData.krcmtWorkplacePossible.krcmtWorkplacePossiblePK.workLocationCD,
								newData.krcmtWorkplacePossible.krcmtWorkplacePossiblePK.cid),
						workLocation.getWorkplace().map(m -> m.getWorkpalceId()).orElse(null));
			}
			
			oldData.get().krcmtIP4Address = newData.krcmtIP4Address;
			this.commandProxy().update(oldData.get());

			if (newData.krcmtWorkplacePossible != null) {
				String toDeleteData = "DELETE FROM KrcmtWorkplacePossible e" + " WHERE "
						+ "e.krcmtWorkplacePossiblePK.contractCode = :contractCode "
						+ " AND e.krcmtWorkplacePossiblePK.workLocationCD = :workLocationCD "
						+ " AND e.krcmtWorkplacePossiblePK.cid = :cid ";
				this.getEntityManager().createQuery(toDeleteData)
						.setParameter("contractCode", oldData.get().kwlmtWorkLocationPK.contractCode)
						.setParameter("workLocationCD", oldData.get().kwlmtWorkLocationPK.workLocationCD)
						.setParameter("cid", AppContexts.user().companyId()).executeUpdate();
				this.commandProxy().insert(newData.krcmtWorkplacePossible);
			}
		}

	}

	@Override
	public void deleteWorkLocation(String contractCode, String workLocationCD) {
		Optional<WorkLocation> data = this.findByCode(contractCode, workLocationCD);
		if (data.isPresent()) {
			this.commandProxy().remove(KrcmtWorkLocation.class, new KwlmtWorkLocationPK(contractCode, workLocationCD));
		}
	}

	@Override
	public List<Ipv4Address> getIPAddressSettings(String contractCode, String workLocationCD) {
		List<Ipv4Address> datas = this.queryProxy().query(SELECT_IPADDRESS_CODE_ORDERBY, KrcmtIP4Address.class)
				.setParameter("contractCode", contractCode).setParameter("workLocationCD", workLocationCD)
				.getList(c -> c.toDomain());
		return datas;
	}

	@Override
	public List<Ipv4Address> getIPAddressByStartEndIP(String contractCode, int net1, int net2, int host1, int host2,
			int endIP) {
		List<Ipv4Address> datas = this.queryProxy().query(SELECT_IPADDRESS_BY_START_END_IP, KrcmtIP4Address.class)
				.setParameter("contractCode", contractCode).setParameter("net1", net1).setParameter("net2", net2)
				.setParameter("host1", host1).setParameter("host2", host2).setParameter("endIP", endIP)
				.getList(c -> c.toDomain());
		return datas;
	}

	@Override
	public List<Ipv4Address> getIPAddressByIP(String contractCode, int net1, int net2, int host1, int host2) {
		List<Ipv4Address> datas = this.queryProxy().query(SELECT_IPADDRESS, KrcmtIP4Address.class)
				.setParameter("contractCode", contractCode).setParameter("net1", net1).setParameter("net2", net2)
				.setParameter("host1", host1).setParameter("host2", host2).getList(c -> c.toDomain());
		return datas;
	}

	@Override
	public void deleteByIP(String contractCode, String workLocationCD, int net1, int net2, int host1, int host2) {
		this.commandProxy().remove(KrcmtIP4Address.class,
				new KrcmtIP4AddressPK(contractCode, workLocationCD, net1, net2, host1, host2));
	}

	@Override
	public void insertListIP(String contractCode, String workLocationCD, List<Ipv4Address> listIpv4Address) {
		if (!listIpv4Address.isEmpty()) {
			this.commandProxy().insertAll(listIpv4Address.stream()
					.map(c -> KrcmtIP4Address.toEntity(contractCode, workLocationCD, c)).collect(Collectors.toList()));
		}

	}

	@Override
	public List<WorkLocation> findByWorkPlace(String contractCode, String cid, String workPlaceId) {
		List<WorkLocation> result = this.queryProxy().query(SELECT_BY_WORKPLACES, KrcmtWorkLocation.class)
				.setParameter("contractCode", contractCode).setParameter("cid", cid)
				.setParameter("workplaceId", workPlaceId).getList(c -> c.toDomain());

		return result;
	}

	@Override
	public void deleteByWorkLocationCd(String contractCode, String workLocationCD, String cid) {
		this.commandProxy().remove(KrcmtWorkplacePossible.class,
				new KrcmtWorkplacePossiblePK(contractCode, workLocationCD, cid));
	}

	@Override
	public Optional<WorkLocation> findByWorkLocationCd(String contractCode, String cid, String workLocationCD) {
		String SELECT = "SELECT c FROM KrcmtWorkplacePossible c where c.krcmtWorkplacePossiblePK.cid = :cid and c.krcmtWorkplacePossiblePK.workLocationCD = :workLocationCD";
		Optional<WorkLocation> result = this.queryProxy().query(SELECT_BY_WORKPLACE, KrcmtWorkLocation.class)
				.setParameter("contractCode", contractCode).setParameter("workLocationCD", workLocationCD)
				.setParameter("cid", cid).getSingle(c -> c.toDomain());
		
		Optional<WorkplacePossible> workplacePossible = this.queryProxy().query(SELECT, KrcmtWorkplacePossible.class)
				.setParameter("cid", cid).setParameter("workLocationCD", workLocationCD).getSingle(c -> c.toDomain());
		
		if (workplacePossible.isPresent()) {
			result.get().setWorkplace(workplacePossible);
		}
		
		return result;
	}

}
