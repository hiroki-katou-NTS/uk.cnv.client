package nts.uk.ctx.bs.employee.infra.repository.tempabsence;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.transaction.Transactional;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsItemRepository;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsenceHisItem;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.AfterChildbirth;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.AnyLeave;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.CareHoliday;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.ChildCareHoliday;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.Leave;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.MidweekClosure;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.SickLeave;
import nts.uk.ctx.bs.employee.infra.entity.temporaryabsence.BsymtTempAbsHistItem;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@Transactional
public class JpaTempAbsItem extends JpaRepository implements TempAbsItemRepository {
	
	private static final String GET_BY_SID_DATE = "SELECT hi FROM BsymtTempAbsHistItem hi"
			+ " INNER JOIN BsymtTempAbsHist h ON h.histId = hi.histId"
			+ " WHERE h.sid = :sid AND h.startDate <= :standardDate AND h.endDate >= :standardDate";
	
	private static final String GET_BY_SIDS_DATE = "SELECT hi FROM BsymtTempAbsHistItem hi"
			+ " INNER JOIN BsymtTempAbsHist h ON h.histId = hi.histId"
			+ " WHERE h.sid IN :sids AND h.startDate <= :standardDate AND h.endDate >= :standardDate";
	
//	private static final String GET_BY_HISTORYID_LIST = "SELECT hi FROM BsymtTempAbsHistItem hi"
//			+ " WHERE hi.histId IN :histIds";
	
	@Override
	public Optional<TempAbsenceHisItem> getItemByHitoryID(String historyId) {
		Optional<BsymtTempAbsHistItem> option = this.queryProxy().find(historyId, BsymtTempAbsHistItem.class);
		if (option.isPresent()) {
			return Optional.of(toDomain(option.get()));
		}
		return Optional.empty();
	}
	
	@Override
	public Optional<TempAbsenceHisItem> getByEmpIdAndStandardDate(String employeeId, GeneralDate standardDate) {
		Optional<BsymtTempAbsHistItem> optionData = this.queryProxy().query(GET_BY_SID_DATE, BsymtTempAbsHistItem.class)
				.setParameter("sid", employeeId).setParameter("standardDate", standardDate).getSingle();
		if ( optionData.isPresent()) {
			return Optional.of(toDomain(optionData.get()));
		}
		return Optional.empty();
	}
	
	@Override
	public List<TempAbsenceHisItem> getByEmpIdsAndStandardDate(List<String> employeeIds,
			GeneralDate standardDate) {
		
		List<BsymtTempAbsHistItem> resultList = new ArrayList<>();
		
		// Split employeeId List if size of employeeId List is greater than 1000
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			List<BsymtTempAbsHistItem> optionDatas = this.queryProxy()
					.query(GET_BY_SIDS_DATE, BsymtTempAbsHistItem.class)
					.setParameter("sids", subList).setParameter("standardDate", standardDate)
					.getList();
			resultList.addAll(optionDatas);
		});

		return resultList.parallelStream().map(item -> toDomain(item))
				.collect(Collectors.toList());
	}
	
	private TempAbsenceHisItem toDomain(BsymtTempAbsHistItem ent) {
		Boolean multiple = ent.multiple == null ? null : ent.multiple == 1;
		Boolean sameFamily = ent.sameFamily == null ? null : ent.sameFamily == 1;
		Boolean spouseIsLeave = ent.spouseIsLeave == null ? null : ent.spouseIsLeave == 1;
		return TempAbsenceHisItem.createTempAbsenceHisItem(ent.tempAbsFrameNo, ent.histId, ent.sid, ent.remarks,
				ent.soInsPayCategory, multiple, ent.familyMemberId, sameFamily, ent.childType, ent.createDate,
				spouseIsLeave, ent.sameFamilyDays);
	}

	@Override
	public void add(TempAbsenceHisItem domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	@Override
	public void update(TempAbsenceHisItem domain) {
		Optional<BsymtTempAbsHistItem> tempAbs = this.queryProxy().find(domain.getHistoryId(),
				BsymtTempAbsHistItem.class);

		if (!tempAbs.isPresent()) {
			throw new RuntimeException("invalid TempAbsenceHisItem");
		}
		updateEntity(domain, tempAbs.get());

		this.commandProxy().update(tempAbs.get());
	}

	@Override
	public void delete(String histId) {
		Optional<BsymtTempAbsHistItem> tempAbs = this.queryProxy().find(histId, BsymtTempAbsHistItem.class);

		if (!tempAbs.isPresent()) {
			throw new RuntimeException("invalid TempAbsenceHisItem");
		}

		this.commandProxy().remove(BsymtTempAbsHistItem.class, histId);
	}

	/**
	 * Covert from domain to entity
	 * 
	 * @param domain
	 * @return
	 */
	private BsymtTempAbsHistItem toEntity(TempAbsenceHisItem domain) {
		int tempAbsenceFrNo = domain.getTempAbsenceFrNo().v().intValue();
		switch (tempAbsenceFrNo) {
		case 1:
			Leave leave = (Leave) domain;
			return new BsymtTempAbsHistItem(leave.getHistoryId(), leave.getEmployeeId(),
					tempAbsenceFrNo, leave.getRemarks() != null? leave.getRemarks().v():null, leave.getSoInsPayCategory());
		case 2:
			MidweekClosure midweek = (MidweekClosure) domain;
			return new BsymtTempAbsHistItem(midweek.getHistoryId(), midweek.getEmployeeId(),
					tempAbsenceFrNo, midweek.getRemarks() != null? midweek.getRemarks().v():null, midweek.getSoInsPayCategory(),
					midweek.getMultiple());
		case 3:
			AfterChildbirth childBirth = (AfterChildbirth) domain;
			return new BsymtTempAbsHistItem(childBirth.getHistoryId(), childBirth.getEmployeeId(),
					tempAbsenceFrNo, childBirth.getRemarks() != null? childBirth.getRemarks().v():null,
					childBirth.getSoInsPayCategory(),childBirth.getFamilyMemberId());
		case 4:
			ChildCareHoliday childCare = (ChildCareHoliday) domain;
			return new BsymtTempAbsHistItem(childCare.getHistoryId(), childCare.getEmployeeId(),
					tempAbsenceFrNo,childCare.getRemarks() != null? childCare.getRemarks().v():null, childCare.getSoInsPayCategory(), childCare.getSameFamily(), childCare.getChildType(),childCare.getFamilyMemberId(), 
					childCare.getCreateDate(), childCare.getSpouseIsLeave());
		case 5:
			CareHoliday careLeave = (CareHoliday) domain;
			return new BsymtTempAbsHistItem(careLeave.getHistoryId(), careLeave.getEmployeeId(),
					tempAbsenceFrNo,careLeave.getRemarks() != null? careLeave.getRemarks().v():null, careLeave.getSoInsPayCategory(), careLeave.getSameFamily() ,
					careLeave.getSameFamilyDays(), careLeave.getFamilyMemberId());
		case 6:
			SickLeave sickLeave = (SickLeave) domain;
			return new BsymtTempAbsHistItem(sickLeave.getHistoryId(), sickLeave.getEmployeeId(),
					tempAbsenceFrNo, sickLeave.getRemarks() != null? sickLeave.getRemarks().v():null, sickLeave.getSoInsPayCategory());
		case 7:
		case 8:
		case 9:
		case 10:
			AnyLeave anyLeave = (AnyLeave) domain;
			return new BsymtTempAbsHistItem(anyLeave.getHistoryId(), anyLeave.getEmployeeId(),
					tempAbsenceFrNo,anyLeave.getRemarks() != null? anyLeave.getRemarks().v():null, anyLeave.getSoInsPayCategory());
		default:
			return null;
		}

	}

	/**
	 * Update entity from domain
	 * 
	 * @param domain
	 * @return
	 */
	private void updateEntity(TempAbsenceHisItem domain, BsymtTempAbsHistItem entity) {
		// Common value
//		entity.histId = domain.getHistoryId();
		entity.tempAbsFrameNo = domain.getTempAbsenceFrNo().v().intValue();
		entity.remarks = domain.getRemarks().v();
		entity.soInsPayCategory = domain.getSoInsPayCategory();

		switch (domain.getTempAbsenceFrNo().v().intValue()) {
		case 1:
			break;
		case 2:
			MidweekClosure midweek = (MidweekClosure) domain;
			if (midweek.getMultiple() != null){
				entity.multiple = midweek.getMultiple() ? 1 : 0;
			}else {
				entity.multiple = null;
			}
			break;
		case 3:
			AfterChildbirth childBirth = (AfterChildbirth) domain;
			entity.familyMemberId = childBirth.getFamilyMemberId();
			break;
		case 4:
			ChildCareHoliday childCare = (ChildCareHoliday) domain;
			if (childCare.getSameFamily() != null){
				entity.sameFamily = childCare.getSameFamily() ? 1 : 0;
			} else {
				entity.sameFamily = null;
			}
			entity.childType = childCare.getChildType();
			entity.familyMemberId = childCare.getFamilyMemberId();
			entity.createDate = childCare.getCreateDate();
			if (childCare.getSpouseIsLeave() != null){
				entity.spouseIsLeave = childCare.getSpouseIsLeave() ? 1 : 0;
			} else {
				entity.spouseIsLeave = null;
			}
			break;
		case 5:
			CareHoliday careLeave = (CareHoliday) domain;
			if (careLeave.getSameFamily() != null){
				entity.sameFamily = careLeave.getSameFamily() ? 1 : 0;
			} else {
				entity.sameFamily = null;
			}
			entity.sameFamilyDays = careLeave.getSameFamilyDays();
			entity.familyMemberId = careLeave.getFamilyMemberId();
			break;
		case 6:
			break;
		case 7:
		case 8:
		case 9:
		case 10:
			break;
		default:
		}

	}
	
	//fix sửa thành jdbc -> tăng tốc độ truy vấn
	@Override
	public List<TempAbsenceHisItem> getItemByHitoryIdList(List<String> historyIds) {
		if (historyIds.isEmpty()) {
			return new ArrayList<>();
		}
		// ResultList
		List<BsymtTempAbsHistItem> entities = new ArrayList<>();

		// Split historyIds List if size of historyIds List is greater than 1000
		CollectionUtil.split(historyIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			String sql = "SELECT * FROM BSYMT_TEMP_ABS_HIST_ITEM WHERE HIST_ID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(i + 1, subList.get(i));
				}
				List<BsymtTempAbsHistItem> tempHistItemLst = new NtsResultSet(stmt.executeQuery()).getList(r -> {
					BsymtTempAbsHistItem history = new BsymtTempAbsHistItem(r.getString("HIST_ID"), r.getString("SID"),
							r.getInt("TEMP_ABS_FRAME_NO"), r.getString("REMARKS"), r.getInt("SO_INS_PAY_CATEGORY"),
							r.getInt("MULTIPLE"), r.getString("FAMILY_MEMBER_ID"), r.getInt("SAME_FAMILY"),
							r.getInt("CHILD_TYPE"), r.getGeneralDate("CREATE_DATE"), r.getInt("SPOUSE_IS_LEAVE"),
							r.getInt("SAME_FAMILY_DAYS"));
					return history;
				}).stream().collect(Collectors.toList());
				entities.addAll(tempHistItemLst);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});

		return entities.stream().map(x -> toDomain(x)).collect(Collectors.toList());
	}

	@Override
	public void addAll(List<TempAbsenceHisItem> domains) {
		String SQL = "INSERT INTO BSYMT_TEMP_ABS_HIST_ITEM (INS_DATE, INS_CCD , INS_SCD , INS_PG,"
				+ " UPD_DATE , UPD_CCD , UPD_SCD , UPD_PG," 
				+ " CONTRACT_CD, HIST_ID, SID, TEMP_ABS_FRAME_NO,"
				+ " REMARKS, SO_INS_PAY_CATEGORY";
		
		String contractCode = AppContexts.user().contractCode();
		String insCcd = AppContexts.user().companyCode();
		String insScd = AppContexts.user().employeeCode();
		String insPg = AppContexts.programId();
		
		String updCcd = insCcd;
		String updScd = insScd;
		String updPg = insPg;
		StringBuilder sb = new StringBuilder();
		
		domains.stream().forEach(c ->{
			int tempAbsenceFrNo = c.getTempAbsenceFrNo().v().intValue();
			switch(tempAbsenceFrNo) {
			case 1:
				String sql = SQL + ")" + " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
						+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
						+ " CONTRACT_CD_VAL, HIST_ID_VAL, SID_VAL, TEMP_ABS_FRAME_NO_VAL, REMARKS_VAL, SO_INS_PAY_CATEGORY_VAL); ";
					Leave leave = (Leave) c;
					sql = sql.replace("INS_DATE_VAL", "'" + GeneralDateTime.now() + "'");
					sql = sql.replace("INS_CCD_VAL", "'" + insCcd + "'");
					sql = sql.replace("INS_SCD_VAL", "'" + insScd + "'");
					sql = sql.replace("INS_PG_VAL", "'" + insPg + "'");

					sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
					sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
					sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
					sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");
					
					sql = sql.replace("CONTRACT_CD_VAL", "'" + contractCode + "'");
					sql = sql.replace("HIST_ID_VAL", "'" + leave.getHistoryId() + "'");
					sql = sql.replace("SID_VAL", "'" + leave.getEmployeeId() + "'");
					sql = sql.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
					sql = sql.replace("REMARKS_VAL", c.getRemarks()== null? "null" : "'" + leave.getRemarks() + "'");
					sql = sql.replace("SO_INS_PAY_CATEGORY_VAL", leave.getSoInsPayCategory() == null? "null": "" +  leave.getSoInsPayCategory().intValue() + "");
					sb.append(sql);
				break;
			case 2:
				String sql1 = SQL +  ", MULTIPLE) "+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
						+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
						+ " CONTRACT_CD_VAL, HIST_ID_VAL, SID_VAL, TEMP_ABS_FRAME_NO_VAL, REMARKS_VAL, SO_INS_PAY_CATEGORY_VAL, MULTIPLE_VAL); ";
					MidweekClosure midweek = (MidweekClosure) c;
					sql1 = sql1.replace("INS_DATE_VAL", "'" + GeneralDateTime.now() + "'");
					sql1 = sql1.replace("INS_CCD_VAL", "'" + insCcd + "'");
					sql1 = sql1.replace("INS_SCD_VAL", "'" + insScd + "'");
					sql1 = sql1.replace("INS_PG_VAL", "'" + insPg + "'");

					sql1 = sql1.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
					sql1 = sql1.replace("UPD_CCD_VAL", "'" + updCcd + "'");
					sql1 = sql1.replace("UPD_SCD_VAL", "'" + updScd + "'");
					sql1 = sql1.replace("UPD_PG_VAL", "'" + updPg + "'");
					
					sql1 = sql1.replace("CONTRACT_CD_VAL", "'" + contractCode + "'");
					sql1 = sql1.replace("HIST_ID_VAL", "'" + midweek.getHistoryId() + "'");
					sql1 = sql1.replace("SID_VAL", "'" + midweek.getEmployeeId() + "'");
					sql1 = sql1.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
					sql1 = sql1.replace("REMARKS_VAL", midweek.getRemarks()== null? "null" : "'" + midweek.getRemarks() + "'");
					sql1 = sql1.replace("SO_INS_PAY_CATEGORY_VAL", midweek.getSoInsPayCategory() == null? "null": "" +  midweek.getSoInsPayCategory().intValue() + "");
					sql1 = sql1.replace("MULTIPLE_VAL", midweek.getMultiple() == null? "null": midweek.getMultiple().booleanValue() == true? "1": "0");
					sb.append(sql1);
				break;
			case 3:
				String sql2 = SQL  + ", FAMILY_MEMBER_ID)"+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
						+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
						+ " CONTRACT_CD_VAL, HIST_ID_VAL, SID_VAL, TEMP_ABS_FRAME_NO_VAL, REMARKS_VAL, SO_INS_PAY_CATEGORY_VAL, FAMILY_MEMBER_ID_VAL); ";
					AfterChildbirth childBirth = (AfterChildbirth) c;
					sql2 = sql2.replace("INS_DATE_VAL", "'" + GeneralDateTime.now() + "'");
					sql2 = sql2.replace("INS_CCD_VAL", "'" + insCcd + "'");
					sql2 = sql2.replace("INS_SCD_VAL", "'" + insScd + "'");
					sql2 = sql2.replace("INS_PG_VAL", "'" + insPg + "'");

					sql2 = sql2.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
					sql2 = sql2.replace("UPD_CCD_VAL", "'" + updCcd + "'");
					sql2 = sql2.replace("UPD_SCD_VAL", "'" + updScd + "'");
					sql2 = sql2.replace("UPD_PG_VAL", "'" + updPg + "'");
					
					sql2 = sql2.replace("CONTRACT_CD_VAL", "'" + contractCode + "'");
					sql2 = sql2.replace("HIST_ID_VAL", "'" + childBirth.getHistoryId() + "'");
					sql2 = sql2.replace("SID_VAL", "'" + childBirth.getEmployeeId() + "'");
					sql2 = sql2.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
					sql2 = sql2.replace("REMARKS_VAL", childBirth.getRemarks()== null? "null" : "'" + childBirth.getRemarks() + "'");
					sql2 = sql2.replace("SO_INS_PAY_CATEGORY_VAL", childBirth.getSoInsPayCategory() == null? "null": "" +  childBirth.getSoInsPayCategory().intValue() + "");
					sql2 = sql2.replace("FAMILY_MEMBER_ID_VAL", childBirth.getFamilyMemberId() == null? "null": "'"+ childBirth.getFamilyMemberId()+ "'");
					sb.append(sql2);
				break;
			case 4:
				String sql3 = SQL  + ", CREATE_DATE, SPOUSE_IS_LEAVE)"+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
						+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
						+ " CONTRACT_CD_VAL, HIST_ID_VAL, SID_VAL, TEMP_ABS_FRAME_NO_VAL, REMARKS_VAL, SO_INS_PAY_CATEGORY_VAL, CREATE_DATE_VAL, SPOUSE_IS_LEAVE_VAL); ";
					ChildCareHoliday childCare = (ChildCareHoliday) c;
					sql3 = sql3.replace("INS_DATE_VAL", "'" + GeneralDateTime.now() + "'");
					sql3 = sql3.replace("INS_CCD_VAL", "'" + insCcd + "'");
					sql3 = sql3.replace("INS_SCD_VAL", "'" + insScd + "'");
					sql3 = sql3.replace("INS_PG_VAL", "'" + insPg + "'");

					sql3 = sql3.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
					sql3 = sql3.replace("UPD_CCD_VAL", "'" + updCcd + "'");
					sql3 = sql3.replace("UPD_SCD_VAL", "'" + updScd + "'");
					sql3 = sql3.replace("UPD_PG_VAL", "'" + updPg + "'");
					
					sql3 = sql3.replace("CONTRACT_CD_VAL", "'" + contractCode + "'");
					sql3 = sql3.replace("HIST_ID_VAL", "'" + childCare.getHistoryId() + "'");
					sql3 = sql3.replace("SID_VAL", "'" + childCare.getEmployeeId() + "'");
					sql3 = sql3.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
					sql3 = sql3.replace("REMARKS_VAL", childCare.getRemarks()== null? "null" : "'" + childCare.getRemarks() + "'");
					sql3 = sql3.replace("SO_INS_PAY_CATEGORY_VAL", childCare.getSoInsPayCategory() == null? "null": "" +  childCare.getSoInsPayCategory().intValue() + "");
					sql3 = sql3.replace("CREATE_DATE_VAL", childCare.getCreateDate() == null? "null": "'"+ childCare.getCreateDate()+ "'");
					sql3 = sql3.replace("SPOUSE_IS_LEAVE_VAL", childCare.getSpouseIsLeave() == null? "null":  childCare.getSpouseIsLeave().booleanValue() == true? "1": "0");
					sb.append(sql3);
				break;
			case 5:
				String sql4 = SQL  +", SAME_FAMILY, SAME_FAMILY_DAYS, FAMILY_MEMBER_ID)"+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
						+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
						+ " CONTRACT_CD_VAL, HIST_ID_VAL, SID_VAL, TEMP_ABS_FRAME_NO_VAL, REMARKS_VAL, SO_INS_PAY_CATEGORY_VAL, SAME_FAMILY_VAL, SAME_FAMILY_DAYS_VAL, FAMILY_MEMBER_ID_VAL); ";
					CareHoliday careLeave = (CareHoliday) c;
					sql4 = sql4.replace("INS_DATE_VAL", "'" + GeneralDateTime.now() + "'");
					sql4 = sql4.replace("INS_CCD_VAL", "'" + insCcd + "'");
					sql4 = sql4.replace("INS_SCD_VAL", "'" + insScd + "'");
					sql4 = sql4.replace("INS_PG_VAL", "'" + insPg + "'");

					sql4 = sql4.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
					sql4 = sql4.replace("UPD_CCD_VAL", "'" + updCcd + "'");
					sql4 = sql4.replace("UPD_SCD_VAL", "'" + updScd + "'");
					sql4 = sql4.replace("UPD_PG_VAL", "'" + updPg + "'");
					
					sql4 = sql4.replace("CONTRACT_CD_VAL", "'" + contractCode + "'");
					sql4 = sql4.replace("HIST_ID_VAL", "'" + careLeave.getHistoryId() + "'");
					sql4 = sql4.replace("SID_VAL", "'" + careLeave.getEmployeeId() + "'");
					sql4 = sql4.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
					sql4 = sql4.replace("REMARKS_VAL", careLeave.getRemarks()== null? "null" : "'" + careLeave.getRemarks() + "'");
					sql4 = sql4.replace("SO_INS_PAY_CATEGORY_VAL", careLeave.getSoInsPayCategory() == null? "null": "" +  careLeave.getSoInsPayCategory().intValue() + "");
					sql4 = sql4.replace("SAME_FAMILY_VAL", careLeave.getSameFamily() == null? "null": careLeave.getSameFamily().booleanValue() == true? "1":"0");
					sql4 = sql4.replace("SAME_FAMILY_DAYS_VAL", careLeave.getSameFamilyDays() == null? "null": ""+ careLeave.getSameFamilyDays().intValue()+ "");
					sql4 = sql4.replace("FAMILY_MEMBER_ID_VAL", careLeave.getFamilyMemberId() == null? "null": "'" + careLeave.getFamilyMemberId() + "'");
					sb.append(sql4);
				break;
			case 6:
				String sql5 = SQL + ")"+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
						+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
						+ " CONTRACT_CD_VAL, HIST_ID_VAL, SID_VAL, TEMP_ABS_FRAME_NO_VAL, REMARKS_VAL, SO_INS_PAY_CATEGORY_VAL); ";
				SickLeave sickLeave = (SickLeave) c;
				sql5 = sql5.replace("INS_DATE_VAL", "'" + GeneralDateTime.now() + "'");
				sql5 = sql5.replace("INS_CCD_VAL", "'" + insCcd + "'");
				sql5 = sql5.replace("INS_SCD_VAL", "'" + insScd + "'");
				sql5 = sql5.replace("INS_PG_VAL", "'" + insPg + "'");

				sql5 = sql5.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
				sql5 = sql5.replace("UPD_CCD_VAL", "'" + updCcd + "'");
				sql5 = sql5.replace("UPD_SCD_VAL", "'" + updScd + "'");
				sql5 = sql5.replace("UPD_PG_VAL", "'" + updPg + "'");
				
				sql5 = sql5.replace("CONTRACT_CD_VAL", "'" + contractCode + "'");
				sql5 = sql5.replace("HIST_ID_VAL", "'" + sickLeave.getHistoryId() + "'");
				sql5 = sql5.replace("SID_VAL", "'" + sickLeave.getEmployeeId() + "'");
				sql5 = sql5.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
				sql5 = sql5.replace("REMARKS_VAL", sickLeave.getRemarks()== null? "null" : "'" + sickLeave.getRemarks() + "'");
				sql5 = sql5.replace("SO_INS_PAY_CATEGORY_VAL", sickLeave.getSoInsPayCategory() == null? "null": "" +  sickLeave.getSoInsPayCategory().intValue() + "");
	
				sb.append(sql5);
				break;
			case 7:
			case 8:
			case 9:
			case 10:
				String sql6 = SQL  + ")"+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
						+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
						+ " CONTRACT_CD_VAL, HIST_ID_VAL, SID_VAL, TEMP_ABS_FRAME_NO_VAL, REMARKS_VAL, SO_INS_PAY_CATEGORY_VAL); ";
				AnyLeave anyLeave = (AnyLeave) c;
				sql6 = sql6.replace("INS_DATE_VAL", "'" + GeneralDateTime.now() + "'");
				sql6 = sql6.replace("INS_CCD_VAL", "'" + insCcd + "'");
				sql6 = sql6.replace("INS_SCD_VAL", "'" + insScd + "'");
				sql6 = sql6.replace("INS_PG_VAL", "'" + insPg + "'");

				sql6 = sql6.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
				sql6 = sql6.replace("UPD_CCD_VAL", "'" + updCcd + "'");
				sql6 = sql6.replace("UPD_SCD_VAL", "'" + updScd + "'");
				sql6 = sql6.replace("UPD_PG_VAL", "'" + updPg + "'");
				
				sql6 = sql6.replace("CONTRACT_CD_VAL", "'" + contractCode + "'");
				sql6 = sql6.replace("HIST_ID_VAL", "'" + anyLeave.getHistoryId() + "'");
				sql6 = sql6.replace("SID_VAL", "'" + anyLeave.getEmployeeId() + "'");
				sql6 = sql6.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
				sql6 = sql6.replace("REMARKS_VAL", anyLeave.getRemarks()== null? "null" : "'" + anyLeave.getRemarks() + "'");
				sql6 = sql6.replace("SO_INS_PAY_CATEGORY_VAL", anyLeave.getSoInsPayCategory() == null? "null": "" +  anyLeave.getSoInsPayCategory().intValue() + "");
	
				sb.append(sql6);
				break;
			default:
				break;			
			}
		});
		
		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
	}

	@Override
	public void updateAll(List<TempAbsenceHisItem> domains) {
		String SQL = "UPDATE BSYMT_TEMP_ABS_HIST_ITEM SET UPD_DATE = UPD_DATE_VAL, UPD_CCD = UPD_CCD_VAL, UPD_SCD = UPD_SCD_VAL, UPD_PG = UPD_PG_VAL, TEMP_ABS_FRAME_NO = TEMP_ABS_FRAME_NO_VAL, ";
		String _WHERE =" WHERE HIST_ID = HIST_ID_VAL AND SID = SID_VAL ;";
		String updCcd = AppContexts.user().companyCode();
		String updScd = AppContexts.user().employeeCode();
		String updPg =  AppContexts.programId();
		StringBuilder sb = new StringBuilder();
		//int tempAbsenceFrNo = tempAbsenceHisItem.getTempAbsenceFrNo().v().intValue();
		domains.stream().forEach(c -> {
			String sql = "";
			int tempAbsenceFrNo = c.getTempAbsenceFrNo().v().intValue();
			switch (tempAbsenceFrNo) {
			case 1:
				sql = SQL + "REMARKS = REMARKS_VAL, SO_INS_PAY_CATEGORY = SO_INS_PAY_CATEGORY_VAL " + _WHERE;
				Leave leave = (Leave) c;
				sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
				sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
				sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
				sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");

				sql = sql.replace("HIST_ID_VAL", "'" + leave.getHistoryId() + "'");
				sql = sql.replace("SID_VAL", "'" + leave.getEmployeeId() + "'");
				sql = sql.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
				sql = sql.replace("REMARKS_VAL", c.getRemarks() == null ? "null" : "'" + leave.getRemarks() + "'");
				sql = sql.replace("SO_INS_PAY_CATEGORY_VAL", leave.getSoInsPayCategory() == null ? "null"
						: "" + leave.getSoInsPayCategory().intValue() + "");
				sb.append(sql);
				break;

			case 2:
				sql = SQL
						+ "REMARKS = REMARKS_VAL, SO_INS_PAY_CATEGORY = SO_INS_PAY_CATEGORY_VAL, MULTIPLE = MULTIPLE_VAL "
						+ _WHERE;
				MidweekClosure midweek = (MidweekClosure) c;
				sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
				sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
				sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
				sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");

				sql = sql.replace("HIST_ID_VAL", "'" + midweek.getHistoryId() + "'");
				sql = sql.replace("SID_VAL", "'" + midweek.getEmployeeId() + "'");
				sql = sql.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
				sql = sql.replace("REMARKS_VAL",
						midweek.getRemarks() == null ? "null" : "'" + midweek.getRemarks() + "'");
				sql = sql.replace("SO_INS_PAY_CATEGORY_VAL", midweek.getSoInsPayCategory() == null ? "null"
						: "" + midweek.getSoInsPayCategory().intValue() + "");
				sql = sql.replace("MULTIPLE_VAL", midweek.getMultiple() == null ? "null"
						: midweek.getMultiple().booleanValue() == true ? "1" : "0");
				sb.append(sql);
				break;

			case 3:
				sql = SQL
						+ "REMARKS = REMARKS_VAL, SO_INS_PAY_CATEGORY = SO_INS_PAY_CATEGORY_VAL, FAMILY_MEMBER_ID = FAMILY_MEMBER_ID_VAL "
						+ _WHERE;
				AfterChildbirth childBirth = (AfterChildbirth) c;

				sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
				sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
				sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
				sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");

				sql = sql.replace("HIST_ID_VAL", "'" + childBirth.getHistoryId() + "'");
				sql = sql.replace("SID_VAL", "'" + childBirth.getEmployeeId() + "'");
				sql = sql.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
				sql = sql.replace("REMARKS_VAL",
						childBirth.getRemarks() == null ? "null" : "'" + childBirth.getRemarks() + "'");
				sql = sql.replace("SO_INS_PAY_CATEGORY_VAL", childBirth.getSoInsPayCategory() == null ? "null"
						: "" + childBirth.getSoInsPayCategory().intValue() + "");
				sql = sql.replace("FAMILY_MEMBER_ID_VAL",
						childBirth.getFamilyMemberId() == null ? "null" : "'" + childBirth.getFamilyMemberId() + "'");
				sb.append(sql);
				break;
			case 4:
				sql = SQL
						+ "REMARKS = REMARKS_VAL, SO_INS_PAY_CATEGORY = SO_INS_PAY_CATEGORY_VAL, CREATE_DATE = CREATE_DATE_VAL, SPOUSE_IS_LEAVE = SPOUSE_IS_LEAVE_VAL "
						+ _WHERE;
				ChildCareHoliday childCare = (ChildCareHoliday) c;
				sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
				sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
				sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
				sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");

				sql = sql.replace("HIST_ID_VAL", "'" + childCare.getHistoryId() + "'");
				sql = sql.replace("SID_VAL", "'" + childCare.getEmployeeId() + "'");
				sql = sql.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
				sql = sql.replace("REMARKS_VAL",
						childCare.getRemarks() == null ? "null" : "'" + childCare.getRemarks() + "'");
				sql = sql.replace("SO_INS_PAY_CATEGORY_VAL", childCare.getSoInsPayCategory() == null ? "null"
						: "" + childCare.getSoInsPayCategory().intValue() + "");
				sql = sql.replace("CREATE_DATE_VAL",
						childCare.getCreateDate() == null ? "null" : "'" + childCare.getCreateDate() + "'");
				sql = sql.replace("SPOUSE_IS_LEAVE_VAL", childCare.getSpouseIsLeave() == null ? "null"
						: childCare.getSpouseIsLeave().booleanValue() == true ? "1" : "0");
				sb.append(sql);
				break;
			case 5:
				sql = SQL
						+ "REMARKS = REMARKS_VAL, SO_INS_PAY_CATEGORY = SO_INS_PAY_CATEGORY_VAL, SAME_FAMILY = SAME_FAMILY_VAL, SAME_FAMILY_DAYS = SAME_FAMILY_DAYS_VAL, FAMILY_MEMBER_ID = FAMILY_MEMBER_ID_VAL "
						+ _WHERE;
				CareHoliday careLeave = (CareHoliday) c;
				sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
				sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
				sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
				sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");

				sql = sql.replace("HIST_ID_VAL", "'" + careLeave.getHistoryId() + "'");
				sql = sql.replace("SID_VAL", "'" + careLeave.getEmployeeId() + "'");
				sql = sql.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
				sql = sql.replace("REMARKS_VAL",
						careLeave.getRemarks() == null ? "null" : "'" + careLeave.getRemarks() + "'");
				sql = sql.replace("SO_INS_PAY_CATEGORY_VAL", careLeave.getSoInsPayCategory() == null ? "null"
						: "" + careLeave.getSoInsPayCategory().intValue() + "");
				sql = sql.replace("SAME_FAMILY_VAL", careLeave.getSameFamily() == null ? "null"
						: careLeave.getSameFamily().booleanValue() == true ? "1" : "0");
				sql = sql.replace("SAME_FAMILY_DAYS_VAL", careLeave.getSameFamilyDays() == null ? "null"
						: "" + careLeave.getSameFamilyDays().intValue() + "");
				sql = sql.replace("FAMILY_MEMBER_ID_VAL",
						careLeave.getFamilyMemberId() == null ? "null" : "'" + careLeave.getFamilyMemberId() + "'");
				sb.append(sql);
				break;
			case 6:
				sql = SQL + "REMARKS = REMARKS_VAL, SO_INS_PAY_CATEGORY = SO_INS_PAY_CATEGORY_VAL " + _WHERE;
				SickLeave sickLeave = (SickLeave) c;

				sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
				sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
				sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
				sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");

				sql = sql.replace("HIST_ID_VAL", "'" + sickLeave.getHistoryId() + "'");
				sql = sql.replace("SID_VAL", "'" + sickLeave.getEmployeeId() + "'");
				sql = sql.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
				sql = sql.replace("REMARKS_VAL",
						sickLeave.getRemarks() == null ? "null" : "'" + sickLeave.getRemarks() + "'");
				sql = sql.replace("SO_INS_PAY_CATEGORY_VAL", sickLeave.getSoInsPayCategory() == null ? "null"
						: "" + sickLeave.getSoInsPayCategory().intValue() + "");

				sb.append(sql);
				break;
			case 7:
			case 8:
			case 9:
			case 10:
				sql = SQL + "REMARKS = REMARKS_VAL, SO_INS_PAY_CATEGORY = SO_INS_PAY_CATEGORY_VAL " + _WHERE;
				AnyLeave anyLeave = (AnyLeave) c;
				sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
				sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
				sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
				sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");

				sql = sql.replace("HIST_ID_VAL", "'" + anyLeave.getHistoryId() + "'");
				sql = sql.replace("SID_VAL", "'" + anyLeave.getEmployeeId() + "'");
				sql = sql.replace("TEMP_ABS_FRAME_NO_VAL", "" + tempAbsenceFrNo + "");
				sql = sql.replace("REMARKS_VAL",
						anyLeave.getRemarks() == null ? "null" : "'" + anyLeave.getRemarks() + "'");
				sql = sql.replace("SO_INS_PAY_CATEGORY_VAL", anyLeave.getSoInsPayCategory() == null ? "null"
						: "" + anyLeave.getSoInsPayCategory().intValue() + "");
				sb.append(sql);
				break;
			default:
				break;
			}
		});

		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}

}
