package nts.uk.ctx.at.shared.infra.repository.employeeworkway.medicalworkstyle;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.LicenseClassification;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.NurseClassifiCode;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.NurseClassifiName;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.NurseClassification;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.NurseClassificationRepository;
import nts.uk.ctx.at.shared.infra.entity.employeeworkway.medicalworkstyle.KscmtNurseLicense;
import nts.uk.ctx.at.shared.infra.entity.employeeworkway.medicalworkstyle.KscmtNurseLicensePK;
import nts.uk.shr.infra.data.jdbc.JDBCUtil;

/**
 * @author ThanhNX
 * 看護区分Repository
 */
@Stateless
public class JpaNurseClassificationRepository extends JpaRepository implements NurseClassificationRepository {

	private static String FIND_WITH_COMPANYID = "select a from KscmtNurseLicense a where a.kscmtNurseLicensePK.companyId = :companyId ORDER BY a.kscmtNurseLicensePK.code ";

	//[1] 会社の看護区分リストを取得する
	@Override
	public List<NurseClassification> getListCompanyNurseCategory(String companyId) {
		return this.queryProxy().query(FIND_WITH_COMPANYID, KscmtNurseLicense.class)
				.setParameter("companyId", companyId).getList(x -> toDomain(x));
	}

	//[2] 指定する看護区分を取得する
	@Override
	public Optional<NurseClassification> getSpecifiNurseCategory(String companyId, String code) {
		return this.queryProxy().find(new KscmtNurseLicensePK(companyId, code), KscmtNurseLicense.class)
				.map(x -> toDomain(x));
	}

	//[3] insert(看護区分）
	@Override
	public void insert(NurseClassification nurseClassification) {
		this.commandProxy().insert(toEntity(nurseClassification));
	}

	//[4] update(看護区分）
	@Override
	public void update(NurseClassification nurseClassification) {
		String sqlQuery = "UPDATE KSCMT_NURSE_LICENSE SET NAME = ? , LICENSE_ATR = ?, IS_OFFICE_WORK = ?  WHERE CID = ? AND CD = ? ";
		try (PreparedStatement ps = this.connection().prepareStatement(JDBCUtil.toUpdateWithCommonField(sqlQuery))) {
			ps.setString(1, nurseClassification.getNurseClassifiName().v());
			ps.setInt(2, nurseClassification.getLicense().value);
			ps.setInt(3, nurseClassification.isOfficeWorker() ? 1 : 0);

			ps.setString(4, nurseClassification.getCompanyId().v());
			ps.setString(5, nurseClassification.getNurseClassifiCode().v());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		//this.commandProxy().update(toEntity(nurseClassification));
	}

	//[5] delete(会社ID，看護区分コード）
	@Override
	public void delete(String companyId, String code) {
		this.commandProxy().remove(KscmtNurseLicense.class, new KscmtNurseLicensePK(companyId, code));

	}

	private NurseClassification toDomain(KscmtNurseLicense ent) {
		return new NurseClassification(new CompanyId(ent.getKscmtNurseLicensePK().getCompanyId()),
				new NurseClassifiCode(ent.getKscmtNurseLicensePK().getCode()), new NurseClassifiName(ent.getName()),
				LicenseClassification.valueOf(ent.getLicenseAtr()), ent.isOfficeWork());
	}

	private KscmtNurseLicense toEntity(NurseClassification domain) {
		return new KscmtNurseLicense(
				new KscmtNurseLicensePK(domain.getCompanyId().v(), domain.getNurseClassifiCode().v()),
				domain.getNurseClassifiName().v(), domain.getLicense().value, domain.isOfficeWorker());
	}

}
