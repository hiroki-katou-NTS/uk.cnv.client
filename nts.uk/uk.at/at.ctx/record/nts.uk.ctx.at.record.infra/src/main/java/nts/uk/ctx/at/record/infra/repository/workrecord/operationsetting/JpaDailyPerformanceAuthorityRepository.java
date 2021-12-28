/**
 *
 */
package nts.uk.ctx.at.record.infra.repository.workrecord.operationsetting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.authormanage.DailyPerformAuthorRepo;
import nts.uk.ctx.at.record.dom.workrecord.authormanage.DailyPerformanceAuthority;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtAttendanceAut;
import nts.uk.ctx.at.record.dom.workrecord.authormanage.DailyPerformanceFunctionNo;
import nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.KrcmtDaiPerformanceAutPk;

/**
 * @author danpv
 *
 */
@Stateless
public class JpaDailyPerformanceAuthorityRepository extends JpaRepository
		implements DailyPerformAuthorRepo {

	private static final String GET_DAI_PER_AUTH_WITH_ROLE = "SELECT da FROM KrcmtAttendanceAut da WHERE da.pk.roleId =:roleId";
	
	private static final String GET_DAI_PER_AUTH_BY_CID = "SELECT da FROM KrcmtAttendanceAut da WHERE da.pk.companyId =:companyId";
	
	private static final String GET_DAI_PER_AUTH_BY_CID_AND_ROLE = GET_DAI_PER_AUTH_BY_CID + " AND da.pk.roleId =:roleId";

	private static final String GET_DAI_PER_AUTH_WITH_ROLE_AND_FUNCTION_NO = "SELECT da FROM KrcmtAttendanceAut da"
			+ "	WHERE da.pk.roleId = :roleId"
			+ "		AND da.pk.functionNo = :functionNo"
			+ "		AND da.availability = :availability";

	@Override
	public List<DailyPerformanceAuthority> get(String roleId) {
		List<KrcmtAttendanceAut> entities = this.queryProxy()
				.query(GET_DAI_PER_AUTH_WITH_ROLE, KrcmtAttendanceAut.class)
				.setParameter("roleId", roleId).getList();
		List<DailyPerformanceAuthority> results = new ArrayList<>();
		entities.forEach(ent -> {
			BigDecimal avaiBigDecimal = ent.availability;
			boolean availability = false;
			if (avaiBigDecimal.intValue() == 1) {
				availability = true;
			}
			results.add(new DailyPerformanceAuthority(ent.pk.companyId, roleId, ent.pk.functionNo, availability));
		});
		return results;
	}
	
	@Override
	public List<DailyPerformanceAuthority> findByCid(String cid) {
		List<KrcmtAttendanceAut> entities = this.queryProxy()
				.query(GET_DAI_PER_AUTH_BY_CID, KrcmtAttendanceAut.class)
				.setParameter("companyId", cid).getList();
		List<DailyPerformanceAuthority> results = new ArrayList<>();
		entities.forEach(ent -> {
			BigDecimal avaiBigDecimal = ent.availability;
			boolean availability = false;
			if (avaiBigDecimal.intValue() == 1) {
				availability = true;
			}
			results.add(new DailyPerformanceAuthority(ent.pk.companyId, ent.pk.roleId, ent.pk.functionNo, availability));
		});
		return results;
	}
	
	@Override
	public List<DailyPerformanceAuthority> findByCidAndRole(String cid, String roleId) {
		List<KrcmtAttendanceAut> entities = this.queryProxy()
				.query(GET_DAI_PER_AUTH_BY_CID_AND_ROLE, KrcmtAttendanceAut.class)
				.setParameter("companyId", cid)
				.setParameter("roleId", roleId)
				.getList();
		List<DailyPerformanceAuthority> results = new ArrayList<>();
		entities.forEach(ent -> {
			BigDecimal avaiBigDecimal = ent.availability;
			boolean availability = false;
			if (avaiBigDecimal.intValue() == 1) {
				availability = true;
			}
			results.add(new DailyPerformanceAuthority(ent.pk.companyId, ent.pk.roleId, ent.pk.functionNo, availability));
		});
		return results;
	}

	@Override
	public void save(DailyPerformanceAuthority daiPerAuthority) {
		KrcmtDaiPerformanceAutPk primaryKey = new KrcmtDaiPerformanceAutPk(daiPerAuthority.getCompanyId(), daiPerAuthority.getRoleID(),
				daiPerAuthority.getFunctionNo().v());
		Optional<KrcmtAttendanceAut> daiPerAthrOptional = this.queryProxy().find(primaryKey,
				KrcmtAttendanceAut.class);
		if (daiPerAthrOptional.isPresent()) {
			KrcmtAttendanceAut entity = daiPerAthrOptional.get();
			entity.availability = bigDecimalValue(daiPerAuthority.isAvailability());
			this.commandProxy().update(entity);
		} else {
			KrcmtAttendanceAut entity = new KrcmtAttendanceAut();
			entity.pk = primaryKey;
			entity.availability = bigDecimalValue(daiPerAuthority.isAvailability());
			this.commandProxy().insert(entity);
		}
	}

	private BigDecimal bigDecimalValue(boolean check) {
		if (check) {
			return new BigDecimal(1);
		} else {
			return new BigDecimal(0);
		}
	}

	/**
	 * ログイン社員の就業帳票の権限を取得する
	 * 
	 * @param roleId ロールID
	 * @param functionNo: 機能NO
	 * @param available: 利用できる
	 * @return
	 */
	@Override
	public boolean getAuthorityOfEmployee(String roleId, DailyPerformanceFunctionNo functionNo, boolean available) {
		//	ドメインモデル「勤務実績の権限」を取得する
		Optional<KrcmtAttendanceAut> oKrcmtDaiPerformanceAut = this.queryProxy()
				.query(GET_DAI_PER_AUTH_WITH_ROLE_AND_FUNCTION_NO, KrcmtAttendanceAut.class)
				.setParameter("roleId", roleId)
				.setParameter("functionNo", functionNo.v())
				.setParameter("availability", this.bigDecimalValue(available))
				.getSingle();

		//	ドメインモデル「勤務実績の権限」が取得できなければ、
		//	自由設定区分は「false：使用しない」として取り扱うこと。
		return oKrcmtDaiPerformanceAut.isPresent();
	}

	@Override
	public void copy(String companyId, List<DailyPerformanceAuthority> sourceData, List<String> targetRoleList) {
		//INPUT．「複写先リスト」をループする
		targetRoleList.forEach(roleId -> {
			//複写先のドメインモデル「勤務実績の権限」を削除する
			List<KrcmtAttendanceAut> deleteEntities = this.queryProxy()
					.query(GET_DAI_PER_AUTH_BY_CID_AND_ROLE, KrcmtAttendanceAut.class)
					.setParameter("companyId", companyId)
					.setParameter("roleId", roleId)
					.getList();
			this.commandProxy().removeAll(deleteEntities);
			this.getEntityManager().flush();
			
			//ドメインモデル「勤務実績の権限」を新規登録する
			List<DailyPerformanceAuthority> newDatas = sourceData.stream()
					.map(data -> {
						return new DailyPerformanceAuthority(companyId, roleId, data.getFunctionNo().v(), data.isAvailability());
					})
					.collect(Collectors.toList());
			newDatas.forEach(data -> {
				this.save(data);
			});
		});
	}

}
