/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.annualpaidleave;

import java.sql.PreparedStatement;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.SneakyThrows;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingRepository;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.annualpaidleave.KshmtHdpaidSet;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.annualpaidleave.KshmtHdpaidSetMng;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.annualpaidleave.KshmtHdpaidTimeSet;

/**
 * The Class JpaAnnualPaidLeaveSettingRepository.
 */
@Stateless
public class JpaAnnualPaidLeaveSettingRepository extends JpaRepository implements AnnualPaidLeaveSettingRepository {

    /*
     * (non-Javadoc)
     *
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
     * AnnualPaidLeaveSettingRepository#add(nts.uk.ctx.at.shared.dom.vacation.
     * setting.annualpaidleave.AnnualPaidLeaveSetting)
     */
    @Override
    public void add(AnnualPaidLeaveSetting setting) {
        KshmtHdpaidSet v = this.toEntity(setting);
        this.commandProxy().insert(v);
    }

    /*
     * (non-Javadoc)
     *
     * @see nts.uk.ctx.pr.core.dom.vacation.setting.annualpaidleave.
     * AnnualPaidLeaveSettingRepository#update(nts.uk.ctx.pr.core.dom.vacation.
     * setting.annualpaidleave.AnnualPaidLeaveSetting)
     */
    @Override
    public void update(AnnualPaidLeaveSetting setting) {
        this.commandProxy().update(this.toEntity(setting));
    }

    /*
     * (non-Javadoc)
     * @see nts.uk.ctx.pr.core.dom.vacation.setting.annualpaidleave.
     * AnnualPaidLeaveSettingRepository#findByCompanyId(java.lang.String)
     */
    
    @SneakyThrows
    @Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public AnnualPaidLeaveSetting findByCompanyId(String companyId) {

		String sqlJdbc = "SELECT *, KTAS.ROUND_PROC_CLA KTASROUND_PRO_CLA,"
    	//String sqlJdbc = "SELECT *,"
				+ " KMAS.HALF_ROUND_PROC KMASHALF_ROUND_PROC, KMAS.SCHEDULD_WORKING_DAYS KMASSCHEDULD_WORKING_DAYS, "
				+ " KTAS.TIME_OF_DAY_REFERENCE KTASTIME_OF_DAY_REFERENCE, KTAS.UNIFORM_TIME KTASUNIFORM_TIME, KTAS.CONTRACT_TIME_ROUND KTASCONTRACT_TIME_ROUND,"
				+ " KTAS.TIME_MANAGE_ATR KTASTIME_MANAGE_ATR, KTAS.TIME_UNIT KTASTIME_UNIT, KTAS.TIME_MAX_DAY_MANAGE_ATR KTASTIME_MAX_DAY_MANAGE_ATR, "
				+ " KTAS.TIME_MAX_DAY_REFERENCE KTASTIME_MAX_DAY_REFERENCE, KTAS.TIME_MAX_DAY_UNIF_COMP KTASTIME_MAX_DAY_UNIF_COMP"
				+ " FROM KSHMT_HDPAID_SET KAPL "
				+ "LEFT JOIN KSHMT_HDPAID_SET_MNG KMAS ON KAPL.CID = KMAS.CID "
				+ "LEFT JOIN KSHMT_HDPAID_TIME_SET KTAS ON KAPL.CID = KTAS.CID "
				+ "WHERE KAPL.CID = ?";

		try (PreparedStatement stmt = this.connection().prepareStatement(sqlJdbc)) {

			stmt.setString(1, companyId);

			Optional<KshmtHdpaidSet> result = new NtsResultSet(stmt.executeQuery())
					.getSingle(rec -> {
						KshmtHdpaidSetMng kmamtMngAnnualSet = new KshmtHdpaidSetMng();
						kmamtMngAnnualSet.setCid(rec.getString("CID"));
						kmamtMngAnnualSet.setHalfManageAtr(rec.getInt("HALF_MANAGE_ATR"));
						kmamtMngAnnualSet.setHalfMaxReference(rec.getInt("HALF_MAX_REFERENCE"));
						kmamtMngAnnualSet
								.setHalfMaxUniformComp(rec.getInt("HALF_MAX_UNIFORM_COMP"));
						kmamtMngAnnualSet.setRetentionYear(rec.getInt("RETENTION_YEAR"));
						kmamtMngAnnualSet.setHalfRoundProc(rec.getInt("KMASHALF_ROUND_PROC"));
						kmamtMngAnnualSet.setScheduleWorkingDays(rec.getDouble("KMASSCHEDULD_WORKING_DAYS"));
//						kmamtMngAnnualSet.setRemainingMaxDay(rec.getDouble("REMAINING_MAX_DAY"));
//						kmamtMngAnnualSet
//								.setNextGrantDayDispAtr(rec.getInt("NEXT_GRANT_DAY_DISP_ATR"));
//						kmamtMngAnnualSet
//								.setRemainingNumDispAtr(rec.getInt("REMAINING_NUM_DISP_ATR"));
//						kmamtMngAnnualSet.setYearlyOfDays(rec.getDouble("YEARLY_OF_DAYS"));
//						kmamtMngAnnualSet.setRoundProcessCla(rec.getInt("KMASROUND_PRO_CLA"));
						KshmtHdpaidTimeSet ktvmtTimeVacationSet = new KshmtHdpaidTimeSet();
						ktvmtTimeVacationSet.setCid(rec.getString("CID"));
						ktvmtTimeVacationSet.setRoundProcessCla(rec.getBoolean("KTASROUND_PRO_CLA"));
						ktvmtTimeVacationSet.setTimeOfDayRef(rec.getInt("KTASTIME_OF_DAY_REFERENCE"));
						ktvmtTimeVacationSet.setUniformTime(rec.getInt("KTASUNIFORM_TIME"));
						ktvmtTimeVacationSet.setContractTimeRound(rec.getInt("KTASCONTRACT_TIME_ROUND"));
						ktvmtTimeVacationSet.setTimeManageAtr(rec.getInt("KTASTIME_MANAGE_ATR"));
						ktvmtTimeVacationSet.setTimeUnit(rec.getInt("KTASTIME_UNIT"));
						ktvmtTimeVacationSet.setTimeMaxDayManageAtr(rec.getInt("KTASTIME_MAX_DAY_MANAGE_ATR"));
						ktvmtTimeVacationSet.setTimeMaxDayReference(rec.getInt("KTASTIME_MAX_DAY_REFERENCE"));
						ktvmtTimeVacationSet.setTimeMaxDayUnifComp(rec.getInt("KTASTIME_MAX_DAY_UNIF_COMP"));
						
						KshmtHdpaidSet entity = new KshmtHdpaidSet();
						entity.setCid(rec.getString("CID"));
						entity.setPriorityType(rec.getInt("PRIORITY_TYPE"));
						entity.setManageAtr(rec.getInt("MANAGE_ATR"));
						entity.setKmamtMngAnnualSet(kmamtMngAnnualSet);
						entity.setKtvmtTimeVacationSet(ktvmtTimeVacationSet);
						return entity;
					});

			if (!result.isPresent()) {
				return null;
			}

			return new AnnualPaidLeaveSetting(
					new JpaAnnualPaidLeaveSettingGetMemento(result.get()));
		}
	}

    /**
     * To entity.
     *
     * @param setting the setting
     * @return the kalmt annual paid leave
     */
    private KshmtHdpaidSet toEntity(AnnualPaidLeaveSetting setting) {
        Optional<KshmtHdpaidSet> optinal = this.queryProxy().find(setting.getCompanyId(),
                KshmtHdpaidSet.class);
        KshmtHdpaidSet entity = null;
        if (optinal.isPresent()) {
            entity = optinal.get();
        } else {
            entity = new KshmtHdpaidSet();
        }
        setting.saveToMemento(new JpaAnnualPaidLeaveSettingSetMemento(entity));
        return entity;
    }

	@Override
	public void copyMasterData(String sourceCid, String targetCid, boolean isReplace) {
		// TODO Auto-generated method stub

	}

}
