/**
 * 5:11:24 PM Jul 24, 2017
 */
package nts.uk.ctx.at.record.infra.repository.workrecord.erroralarm;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.SneakyThrows;
import lombok.val;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.ErrorAlarmCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.ErAlAttendanceItemCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.ErAlConditionsAttendanceItem;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ConditionAtr;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ConditionType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.AttendanceItemId;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedAmountValue;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedTimeDuration;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedTimesValue;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedTimesValueDay;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.KwrmtErAlWorkRecord;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.KwrmtErAlWorkRecordPK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcmtErAlCondition;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcmtEralApplication;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.ClassificationCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.ctx.at.shared.dom.workrule.businesstype.BusinessTypeCode;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * @author hungnm
 *
 */
@Stateless
public class JpaErrorAlarmWorkRecordRepository extends JpaRepository implements ErrorAlarmWorkRecordRepository {

	private static final String FIND_BY_COMPANY = "SELECT a FROM KwrmtErAlWorkRecord a WHERE a.kwrmtErAlWorkRecordPK.companyId = :companyId ";
	private static final String FIND_BY_ERROR_ALARM_CHECK_ID = "SELECT a FROM KwrmtErAlWorkRecord a WHERE a.kwrmtErAlWorkRecordPK.companyId = :companyId AND a.eralCheckId = :eralCheckId ";
	private static final String FIND_ALL_ER_AL_COMPANY = "SELECT a FROM KwrmtErAlWorkRecord a WHERE a.kwrmtErAlWorkRecordPK.companyId = :companyId "
			+ " AND a.useAtr = 1 AND a.typeAtr IN (0,1)";
	private static final String FIND_BY_COMPANY_AND_USEATR = "SELECT a FROM KwrmtErAlWorkRecord a WHERE a.kwrmtErAlWorkRecordPK.companyId = :companyId AND a.useAtr = :useAtr ";
	private static final String SELECT_ERAL_BY_LIST_CODE = "SELECT s FROM KwrmtErAlWorkRecord s WHERE s.kwrmtErAlWorkRecordPK.errorAlarmCode IN :listCode AND s.kwrmtErAlWorkRecordPK.companyId = :companyId";

	private static final String SELECT_ERAL_BY_LIST_CODE_REMARK = "SELECT s FROM KwrmtErAlWorkRecord s WHERE s.kwrmtErAlWorkRecordPK.errorAlarmCode IN :listCode AND s.kwrmtErAlWorkRecordPK.companyId = :companyId AND s.remarkCancelErrorInput = 1";

	private static final String FIND_MOB_BY_COMPANY = "SELECT a FROM KwrmtErAlWorkRecord a WHERE a.kwrmtErAlWorkRecordPK.companyId = :companyId "
			+ "AND a.typeAtr IN :typeAtrLst";

	@Override
	public Optional<ErrorAlarmWorkRecord> findByCode(String code) {
		Optional<KwrmtErAlWorkRecord> entity = this.queryProxy()
				.find(new KwrmtErAlWorkRecordPK(AppContexts.user().companyId(), code), KwrmtErAlWorkRecord.class);
		return Optional.ofNullable(entity.isPresent() ? KwrmtErAlWorkRecord.toDomain(entity.get()) : null);
	}

	@Override
	public List<ErrorAlarmWorkRecord> getListErrorAlarmWorkRecord(String companyId) {
		List<KwrmtErAlWorkRecord> lstData = this.queryProxy()
				.query(FIND_BY_COMPANY_AND_USEATR, KwrmtErAlWorkRecord.class).setParameter("companyId", companyId)
				.setParameter("useAtr", true).getList();
		return lstData.stream().map(entity -> KwrmtErAlWorkRecord.toDomain(entity)).collect(Collectors.toList());
	}

	@Override
	public void addErrorAlarmWorkRecord(ErrorAlarmWorkRecord domain, ErrorAlarmCondition conditionDomain) {
		domain.setCompanyId(AppContexts.user().companyId());
		KwrmtErAlWorkRecord entity = KwrmtErAlWorkRecord.fromDomain(domain, conditionDomain);
		entity.krcmtErAlCondition.setCompanyId(AppContexts.user().companyId());
		this.commandProxy().insert(entity);
	}

	@Override
	public void updateErrorAlarmWorkRecord(ErrorAlarmWorkRecord domain, ErrorAlarmCondition conditionDomain) {
		KwrmtErAlWorkRecord targetEntity = this.queryProxy()
				.find(new KwrmtErAlWorkRecordPK(domain.getCompanyId(), domain.getCode().v()), KwrmtErAlWorkRecord.class)
				.get();
		domain.setCheckId(targetEntity.eralCheckId);
		// kdw007_ver22
		KwrmtErAlWorkRecord domainAfterConvert = null;
		if (!domain.getFixedAtr()) {
			conditionDomain.setGroupId1(targetEntity.getGroup1Id());
			conditionDomain.setGroupId2(targetEntity.getGroup2Id());
			domainAfterConvert = KwrmtErAlWorkRecord.fromDomain(domain, conditionDomain);
		} else {
			domainAfterConvert = KwrmtErAlWorkRecord.fromDomainFixed(domain, conditionDomain);
		}
		
		
		// targetEntity.eralCheckId = domainAfterConvert.eralCheckId;
		// targetEntity.boldAtr = domainAfterConvert.boldAtr;
		// targetEntity.cancelableAtr = domainAfterConvert.cancelableAtr;
		// targetEntity.cancelRoleId = domainAfterConvert.cancelRoleId;
		// targetEntity.errorAlarmName = domainAfterConvert.errorAlarmName;
		// targetEntity.errorDisplayItem = domainAfterConvert.errorDisplayItem;
		// targetEntity.fixedAtr = domainAfterConvert.fixedAtr;
		// targetEntity.krcmtErAlCondition =
		// domainAfterConvert.krcmtErAlCondition;
		// targetEntity.krcstErAlApplication =
		// domainAfterConvert.krcstErAlApplication;
		// targetEntity.kwrmtErAlWorkRecordPK =
		// domainAfterConvert.kwrmtErAlWorkRecordPK;
		// targetEntity.messageColor = domainAfterConvert.messageColor;
		// targetEntity.typeAtr = domainAfterConvert.typeAtr;
		// targetEntity.useAtr = domainAfterConvert.useAtr;
		// targetEntity.remarkCancelErrorInput =
		// domainAfterConvert.remarkCancelErrorInput;
		// targetEntity.remarkColumnNo = domainAfterConvert.remarkColumnNo;
		this.commandProxy().update(domainAfterConvert);
	}

	@Override
	public void removeErrorAlarmWorkRecord(String code) {
		this.commandProxy().remove(KwrmtErAlWorkRecord.class,
				new KwrmtErAlWorkRecordPK(AppContexts.user().companyId(), code));
	}

	@Override
	public Optional<ErrorAlarmWorkRecord> findByErrorAlamCheckId(String eralCheckId) {
		Optional<KwrmtErAlWorkRecord> entity = this.queryProxy()
				.query(FIND_BY_ERROR_ALARM_CHECK_ID, KwrmtErAlWorkRecord.class)
				.setParameter("companyId", AppContexts.user().companyId()).setParameter("eralCheckId", eralCheckId)
				.getSingle();
		return Optional.ofNullable(entity.isPresent() ? KwrmtErAlWorkRecord.toDomain(entity.get()) : null);
	}

	@Override
	public List<ErrorAlarmWorkRecord> findByListErrorAlamCheckId(List<String> listEralCheckId) {
		List<ErrorAlarmWorkRecord> data = new ArrayList<>();
		for (String eralCheckId : listEralCheckId) {
			Optional<ErrorAlarmWorkRecord> errorAlarmWorkRecord = this.findByErrorAlamCheckId(eralCheckId);
			if (errorAlarmWorkRecord.isPresent()) {
				data.add(errorAlarmWorkRecord.get());
			}
		}
		return data;
	}

	@Override
	public Optional<ErrorAlarmCondition> findConditionByErrorAlamCheckId(String eralCheckId) {
		Optional<KwrmtErAlWorkRecord> entity = this.queryProxy()
				.query(FIND_BY_ERROR_ALARM_CHECK_ID, KwrmtErAlWorkRecord.class)
				.setParameter("companyId", AppContexts.user().companyId()).setParameter("eralCheckId", eralCheckId)
				.getSingle();
		return Optional.ofNullable(entity.isPresent() ? KwrmtErAlWorkRecord.toConditionDomain(entity.get()) : null);
	}

	@Override
	public List<ErrorAlarmCondition> findConditionByListErrorAlamCheckId(List<String> listEralCheckId) {
		List<ErrorAlarmCondition> data = new ArrayList<>();
		for (String eralCheckId : listEralCheckId) {
			Optional<ErrorAlarmCondition> errorAlarmWorkRecord = this.findConditionByErrorAlamCheckId(eralCheckId);
			if (errorAlarmWorkRecord.isPresent()) {
				data.add(errorAlarmWorkRecord.get());
			}
		}
		return data;
	}

	@Override
	public List<ErrorAlarmCondition> getListErrorAlarmCondition(String companyId) {
		List<KwrmtErAlWorkRecord> lstData = this.queryProxy().query(FIND_BY_COMPANY, KwrmtErAlWorkRecord.class)
				.setParameter("companyId", companyId).getList();
		return lstData.stream().map(entity -> KwrmtErAlWorkRecord.toConditionDomain(entity))
				.collect(Collectors.toList());
	}

	@Override
	public List<ErrorAlarmWorkRecord> getListErAlByListCode(String companyId, List<String> listCode) {
		List<ErrorAlarmWorkRecord> datas = new ArrayList<>();
		if (listCode.isEmpty())
			return new ArrayList<ErrorAlarmWorkRecord>();

		CollectionUtil.split(listCode, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
			datas.addAll(this.queryProxy().query(SELECT_ERAL_BY_LIST_CODE, KwrmtErAlWorkRecord.class)
					.setParameter("listCode", subIdList).setParameter("companyId", companyId)
					.getList(c -> KwrmtErAlWorkRecord.toDomain(c)));
		});
		return datas;
	}

	@Override
	public List<ErrorAlarmWorkRecord> getAllErAlCompany(String companyId) {
		return this.queryProxy().query(FIND_ALL_ER_AL_COMPANY, KwrmtErAlWorkRecord.class)
				.setParameter("companyId", companyId).getList(c -> {
					ErrorAlarmWorkRecord record = KwrmtErAlWorkRecord.toDomain(c);
					record.setErrorAlarmCondition(KwrmtErAlWorkRecord.toConditionDomain(c));
					return record;
				});
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<ErrorAlarmWorkRecord> getListErAlByListCodeError(String companyId, List<String> listCode) {
		List<ErrorAlarmWorkRecord> datas = new ArrayList<>();
		if (listCode.isEmpty())
			return new ArrayList<ErrorAlarmWorkRecord>();

		CollectionUtil.split(listCode, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
			String subListCd = NtsStatement.In.createParamsString(subIdList);
			String sql = "SELECT rd.CID, rd.ERROR_ALARM_CD, rd.ERROR_ALARM_NAME, rd.FIXED_ATR, rd.USE_ATR, rd.REMARK_CANCEL_ERR_INP, rd.REMARK_COLUMN_NO, "
					+ "rd.ERAL_ATR, rd.BOLD_ATR, rd.MESSAGE_COLOR, rd.CANCELABLE_ATR, rd.ERROR_DISPLAY_ITEM, rd.ERAL_CHECK_ID, app.APP_TYPE_CD "
					+ "FROM KRCMT_ERAL_DAY_SET rd LEFT JOIN KRCMT_ERAL_APPLICATION app ON rd.CID = app.CID and rd.ERROR_ALARM_CD = app.ERROR_CD "
					+ "WHERE rd.ERROR_ALARM_CD IN (" + subListCd + ") AND rd.CID = ? AND ERAL_ATR = 0";

			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				for (int i = 0; i < subIdList.size(); i++) {
					stmt.setString(i + 1, subIdList.get(i));
				}
				stmt.setString(subIdList.size() + 1, companyId);
				datas.addAll(new NtsResultSet(stmt.executeQuery()).getList(rs -> {
					return ErrorAlarmWorkRecord.createFromJavaType(rs.getString("CID"), rs.getString("ERROR_ALARM_CD"),
							rs.getString("ERROR_ALARM_NAME"), rs.getBoolean("FIXED_ATR"), rs.getBoolean("USE_ATR"),
							rs.getBoolean("REMARK_CANCEL_ERR_INP"), rs.getInt("REMARK_COLUMN_NO"), rs.getInt("ERAL_ATR"),
							rs.getBoolean("BOLD_ATR"), rs.getString("MESSAGE_COLOR"), rs.getBoolean("CANCELABLE_ATR"),
							rs.getInt("ERROR_DISPLAY_ITEM"), rs.getInt("APP_TYPE_CD") == null ? Collections.emptyList() : Arrays.asList(rs.getInt("APP_TYPE_CD")),
							rs.getString("ERAL_CHECK_ID"));
				}));

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		if (datas.isEmpty())
			return new ArrayList<ErrorAlarmWorkRecord>();

		List<ErrorAlarmWorkRecord> results = new ArrayList<>();

		Map<ErrorAlarmWorkRecordCode, List<ErrorAlarmWorkRecord>> map = datas.stream()
				.collect(Collectors.groupingBy(ErrorAlarmWorkRecord::getCode));
		map.entrySet().stream().forEach(x -> {
			ErrorAlarmWorkRecord record = x.getValue().get(0);
			List<Integer> lstApplication = x.getValue().stream().filter(z -> !z.getLstApplication().isEmpty())
					.map(y -> (y.getLstApplication().get(0))).collect(Collectors.toList());
			results.add(new ErrorAlarmWorkRecord(companyId, x.getKey(), record.getName(), record.getFixedAtr(),
					record.getUseAtr(), record.getRemarkCancelErrorInput(), record.getRemarkColumnNo(),
					record.getTypeAtr(), record.getMessage(), record.getCancelableAtr(), record.getErrorDisplayItem(),
					lstApplication, record.getErrorAlarmCheckID()));
		});

		return results;
	}

	@Override
	@SneakyThrows
	public List<ErrorAlarmWorkRecord> getAllErAlCompanyAndUseAtr(String companyId, boolean useAtr) {
		// get data to call KwrmtErAlWorkRecord.toDomain(c)

		Map<String, String> conG1 = new HashMap<>(), conG2 = new HashMap<>();

		Map<String, ErrorAlarmWorkRecord> eRecord = new HashMap<>();
		Map<String, List<WorkTimeCode>> workTimePlan = new HashMap<>();
		Map<String, List<WorkTimeCode>> workTimeActual = new HashMap<>();
		Map<String, List<WorkTypeCode>> workTypePlan = new HashMap<>();
		Map<String, List<WorkTypeCode>> workTypeActual = new HashMap<>();
		Map<String, List<Integer>> appTypes = new HashMap<>();
		Map<String, List<BusinessTypeCode>> businessTypes = new HashMap<>();
		Map<String, List<String>> jobIds = new HashMap<>();
		Map<String, List<EmploymentCode>> employments = new HashMap<>();
		Map<String, List<ClassificationCode>> classifications = new HashMap<>();

		getCodeTypes(companyId, useAtr, workTimePlan, workTimeActual, workTypePlan, workTypeActual, appTypes,
				businessTypes, jobIds, employments, classifications);

		try (PreparedStatement statement = this.connection().prepareStatement(buildJDBCSelectErAlV2())) {
			statement.setString(1, companyId);
			statement.setBoolean(2, useAtr);
			new NtsResultSet(statement.executeQuery()).getList(rs -> {
				String eralID = rs.getString("ERAL_CHECK_ID"), group1Id = rs.getString("ATD_ITEM_CONDITION_GROUP1"),
						group2ID = rs.getString("ATD_ITEM_CONDITION_GROUP2");

				boolean fixedAtr = rs.getBoolean("FIXED_ATR");

				if (group1Id != null) {
					conG1.put(group1Id, eralID);
				}
				if (group2ID != null) {
					conG2.put(group2ID, eralID);
				}
				ErrorAlarmWorkRecord eral = eRecord.get(eralID);
				if (eral == null) {
					String 	erAlCode = rs.getString("ERROR_ALARM_CD"), 
							erAlName = rs.getString("ERROR_ALARM_NAME"),
							mesColor = rs.getString("MESSAGE_COLOR"), 
							mesDisplay = rs.getString("MESSAGE_DISPLAY");

					Integer remarkNo = rs.getInt("REMARK_COLUMN_NO"), 
							erAlAtr = rs.getInt("ERAL_ATR"),
							displayItem = rs.getInt("ERROR_DISPLAY_ITEM"),
							continuousAtr = rs.getInt("CONTINUOUS_PERIOD"),
							filterByBusinessType = rs.getInt("FILTER_BY_BUSINESS_TYPE"),
							filterByJob = rs.getInt("FILTER_BY_JOB_TITLE"),
							filterByEmployment = rs.getInt("FILTER_BY_EMPLOYMENT"),
							filterByClassification = rs.getInt("FILTER_BY_CLASSIFICATION"),
							wtComAtr = rs.getInt("WT_COMPARE_ATR"), 
							useGroup2 = rs.getInt("GROUP2_USE_ATR"),
							whComAtr = rs.getInt("WH_COMPARE_ATR"), 
							useWorkType = rs.getInt("WORKTYPE_USE_ATR"),
							useWorkTypePlan = rs.getInt("WT_PLAN_FILTER_ATR"),
							useWorkTypeActual = rs.getInt("WT_ACTUAL_FILTER_ATR"),
							workTypeOperator = rs.getInt("WT_PLAN_ACTUAL_OPERATOR"),
							useWorkTime = rs.getInt("WORKING_HOURS_USE_ATR"),
							useWorkTimePlan = rs.getInt("WH_PLAN_FILTER_ATR"),
							useWorkTimeActual = rs.getInt("WH_ACTUAL_FILTER_ATR"),
							workTimeOperator = rs.getInt("WH_PLAN_ACTUAL_OPERATOR"),
							groupOperator = rs.getInt("OPERATOR_BETWEEN_GROUPS");
					
					boolean boldAtr = rs.getBoolean("BOLD_ATR"), 
							remarkCanRemove = rs.getBoolean("REMARK_CANCEL_ERR_INP"),
							cancelableAtr = rs.getBoolean("CANCELABLE_ATR");


					eral = ErrorAlarmWorkRecord.createFromJavaType(companyId, erAlCode, erAlName, fixedAtr, useAtr,
							remarkCanRemove, remarkNo, erAlAtr, boldAtr, mesColor, cancelableAtr, displayItem,
							new ArrayList<>(), eralID);

					ErrorAlarmCondition condition = new ErrorAlarmCondition(eralID, mesDisplay);
					condition.setContinuousPeriod(continuousAtr != null ? continuousAtr : 0);

					if (!fixedAtr) {
						// Set AlCheckTargetCondition
						condition.createAlCheckTargetCondition(filterByBusinessType == 1, filterByJob == 1,
								filterByEmployment == 1, filterByClassification == 1, new ArrayList<>(),
								new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

						// Set WorkTypeCondition
						condition.createWorkTypeCondition(useWorkType == 1, wtComAtr == null ? 0 : wtComAtr);
						condition.setWorkType(useWorkTypePlan != null && useWorkTypePlan == 1,
								useWorkTypeActual != null && useWorkTypeActual == 1);
						condition.chooseWorkTypeOperator(workTypeOperator);

						// Set WorkTimeCondtion
						condition.createWorkTimeCondition(useWorkTime == 1, whComAtr == null ? 0 : whComAtr);
						condition.setWorkTime(useWorkTimePlan != null && useWorkTimePlan == 1,
								useWorkTimeActual != null && useWorkTimeActual == 1);
						condition.chooseWorkTimeOperator(workTimeOperator);

						condition.createAttendanceItemCondition(groupOperator, useGroup2 == 1 ? true : false);

						if (appTypes.containsKey(eralID)) {
							eral.getLstApplication().addAll(appTypes.get(eralID));
						}
						if (jobIds.containsKey(eralID)) {
							condition.getCheckTargetCondtion().getLstJobTitleId().addAll(jobIds.get(eralID));
						}
						if (businessTypes.containsKey(eralID)) {
							condition.getCheckTargetCondtion().getLstBusinessTypeCode()
									.addAll(businessTypes.get(eralID));
						}
						if (employments.containsKey(eralID)) {
							condition.getCheckTargetCondtion().getLstEmploymentCode().addAll(employments.get(eralID));
						}
						if (classifications.containsKey(eralID)) {
							condition.getCheckTargetCondtion().getLstClassificationCode()
									.addAll(classifications.get(eralID));
						}
						if (workTimeActual.containsKey(eralID) || workTimePlan.containsKey(eralID)) {
							condition.getWorkTimeCondition().addWorkTime(workTimePlan.get(eralID),
									workTimeActual.get(eralID));
						}
						if (workTypeActual.containsKey(eralID) || workTypePlan.containsKey(eralID)) {
							condition.getWorkTypeCondition().addWorkType(workTypePlan.get(eralID),
									workTypeActual.get(eralID));
						}
					}
					eral.setErrorAlarmCondition(condition);
					eRecord.put(eralID, eral);
				}

				return null;
			});
		}

		getConditionGroupV2(conG1, conG2, eRecord, companyId);

		return eRecord.entrySet().stream().map(c -> {
			return c.getValue().cleanData();
		}).collect(Collectors.toList());
	}

	private void getCodeTypes(String companyId, boolean useAtr, Map<String, List<WorkTimeCode>> workTimePlan,
			Map<String, List<WorkTimeCode>> workTimeActual, Map<String, List<WorkTypeCode>> workTypePlan,
			Map<String, List<WorkTypeCode>> workTypeActual, Map<String, List<Integer>> appTypes,
			Map<String, List<BusinessTypeCode>> businessTypes, Map<String, List<String>> jobIds,
			Map<String, List<EmploymentCode>> employments, Map<String, List<ClassificationCode>> classifications)
			throws SQLException {
		Map<String, WorkTypeCode> workTypeCodes = new HashMap<>();
		Map<String, WorkTimeCode> workTimeCodes = new HashMap<>();
		Map<String, BusinessTypeCode> businessTypeCodes = new HashMap<>();
		Map<String, EmploymentCode> employmentCodes = new HashMap<>();
		Map<String, ClassificationCode> classificationCodes = new HashMap<>();
		try (PreparedStatement statement = this.connection().prepareStatement(buildJDBCSelectErAlApp())) {
			statement.setString(1, companyId);
			statement.setBoolean(2, useAtr);
			new NtsResultSet(statement.executeQuery()).getList(rs -> {
				String eralID = rs.getString(1);
				Integer code = rs.getInt(2);
				if (code != null) {
					if (!appTypes.containsKey(eralID)) {
						appTypes.put(eralID, new ArrayList<>());
					}
					appTypes.get(eralID).add(code);
				}
				return null;
			});
		}
		getType(companyId, useAtr, () -> buildJDBCSelectErAlBusinessType(), businessTypes, (code) -> {
			if (!businessTypeCodes.containsKey(code)) {
				businessTypeCodes.put(code, new BusinessTypeCode(code));
			}
			return businessTypeCodes.get(code);
		});
		getType(companyId, useAtr, () -> buildJDBCSelectErAlCLassification(), classifications, (code) -> {
			if (!classificationCodes.containsKey(code)) {
				classificationCodes.put(code, new ClassificationCode(code));
			}
			return classificationCodes.get(code);
		});
		getType(companyId, useAtr, () -> buildJDBCSelectErAlEmployment(), employments, (code) -> {
			if (!employmentCodes.containsKey(code)) {
				employmentCodes.put(code, new EmploymentCode(code));
			}
			return employmentCodes.get(code);
		});
		getType(companyId, useAtr, () -> buildJDBCSelectErAlJobTittle(), jobIds, (code) -> code);
		getType(companyId, useAtr, () -> buildJDBCSelectErAlWhActual(), workTimeActual, (code) -> {
			if (!workTimeCodes.containsKey(code)) {
				workTimeCodes.put(code, new WorkTimeCode(code));
			}
			return workTimeCodes.get(code);
		});
		getType(companyId, useAtr, () -> buildJDBCSelectErAlWhPlan(), workTimePlan, (code) -> {
			if (!workTimeCodes.containsKey(code)) {
				workTimeCodes.put(code, new WorkTimeCode(code));
			}
			return workTimeCodes.get(code);
		});
		getType(companyId, useAtr, () -> buildJDBCSelectErAlWtActual(), workTypeActual, (code) -> {
			if (!workTypeCodes.containsKey(code)) {
				workTypeCodes.put(code, new WorkTypeCode(code));
			}
			return workTypeCodes.get(code);
		});
		getType(companyId, useAtr, () -> buildJDBCSelectErAlWtPlan(), workTypePlan, (code) -> {
			if (!workTypeCodes.containsKey(code)) {
				workTypeCodes.put(code, new WorkTypeCode(code));
			}
			return workTypeCodes.get(code);
		});
	}

	@SneakyThrows
	private <V> void getType(String companyId, boolean useAtr, Supplier<String> query, Map<String, List<V>> container,
			Function<String, V> converter) {
		try (PreparedStatement statement = this.connection().prepareStatement(query.get())) {
			statement.setString(1, companyId);
			statement.setBoolean(2, useAtr);
			new NtsResultSet(statement.executeQuery()).getList(rs -> {
				String eralID = rs.getString(1), code = rs.getString(2);

				if (code != null) {
					if (!container.containsKey(eralID)) {
						container.put(eralID, new ArrayList<>());
					}
					container.get(eralID).add(converter.apply(code));
				}

				return null;
			});
		}
	}

	private String buildJDBCSelectErAlV2() {
		StringBuilder builder = new StringBuilder(
				"SELECT es.ERAL_CHECK_ID, es.CID, es.ERROR_ALARM_CD, es.ERROR_ALARM_NAME, es.FIXED_ATR, es.USE_ATR, ");
		builder.append(
				" es.REMARK_CANCEL_ERR_INP, es.REMARK_COLUMN_NO, es.ERAL_ATR, es.BOLD_ATR, es.MESSAGE_COLOR, es.CANCELABLE_ATR, es.ERROR_DISPLAY_ITEM,");
		builder.append(
				" ec.MESSAGE_DISPLAY, ec.CONTINUOUS_PERIOD, ec.FILTER_BY_BUSINESS_TYPE, ec.FILTER_BY_JOB_TITLE, ec.FILTER_BY_EMPLOYMENT, ec.FILTER_BY_CLASSIFICATION, ");
		builder.append(
				" ec.ATD_ITEM_CONDITION_GROUP1, ec.WT_ACTUAL_FILTER_ATR, ec.WT_PLAN_ACTUAL_OPERATOR, ec.WH_PLAN_ACTUAL_OPERATOR,");
		builder.append(" ec.ATD_ITEM_CONDITION_GROUP2, ec.WORKTYPE_USE_ATR, ec.WT_COMPARE_ATR, ec.WT_PLAN_FILTER_ATR,");
		builder.append(" ec.WORKING_HOURS_USE_ATR, ec.WH_COMPARE_ATR, ec.WH_PLAN_FILTER_ATR, ec.WH_ACTUAL_FILTER_ATR,");
		builder.append(" ec.OPERATOR_BETWEEN_GROUPS, ec.GROUP2_USE_ATR FROM KRCMT_ERAL_DAY_SET es");
		builder.append(
				" LEFT JOIN KRCMT_ERALST_CND ec ON ec.CID = es.CID AND ec.ERAL_CHECK_ID = es.ERAL_CHECK_ID ");
		builder.append(" WHERE es.CID = ? AND es.USE_ATR = ? ");
		return builder.toString();
	}

	private String buildJDBCSelectErAlApp() {
		StringBuilder builder = new StringBuilder("SELECT es.ERAL_CHECK_ID, ap.APP_TYPE_CD FROM KRCMT_ERAL_DAY_SET es");
		builder.append(" LEFT JOIN KRCMT_ERAL_APPLICATION ap ON ap.CID = es.CID AND es.ERROR_ALARM_CD = ap.ERROR_CD ");
		builder.append(" WHERE es.CID = ? AND es.USE_ATR = ? ");
		return builder.toString();
	}

	private String buildJDBCSelectErAlGet(String table, String field) {
		StringBuilder builder = new StringBuilder("SELECT es.ERAL_CHECK_ID, ap." + field + " FROM KRCMT_ERAL_DAY_SET es");
		builder.append(" LEFT JOIN " + table + " ap ON ap.CID = es.CID AND es.ERAL_CHECK_ID = ap.ERAL_CHECK_ID ");
		builder.append(" WHERE es.CID = ? AND es.USE_ATR = ? ");
		return builder.toString();
	}

	private String buildJDBCSelectErAlBusinessType() {
		return buildJDBCSelectErAlGet("KRCMT_ERAL_BUSINESS_TYPE", "BUSINESS_TYPE_CD");
	}

	private String buildJDBCSelectErAlJobTittle() {
		return buildJDBCSelectErAlGet("KRCMT_ERAL_JOB_TITLE", "JOB_ID");
	}

	private String buildJDBCSelectErAlEmployment() {
		return buildJDBCSelectErAlGet("KRCMT_ERAL_EMPLOYMENT", "EMPTCD");
	}

	private String buildJDBCSelectErAlCLassification() {
		return buildJDBCSelectErAlGet("KRCMT_ERAL_CLASS", "CLSCD");
	}

	private String buildJDBCSelectErAlWtPlan() {
		return buildJDBCSelectErAlGet("KRCMT_ERAL_WKTP_PLAN", "WORKTYPE_CD");
	}

	private String buildJDBCSelectErAlWtActual() {
		return buildJDBCSelectErAlGet("KRCMT_ERAL_WKTP_ACTUAL", "WORKTYPE_CD");
	}

	private String buildJDBCSelectErAlWhPlan() {
		return buildJDBCSelectErAlGet("KRCMT_ERAL_WKTM_PLAN", "WORK_TIME_CD");
	}

	private String buildJDBCSelectErAlWhActual() {
		return buildJDBCSelectErAlGet("KRCMT_ERAL_WKTM_ACTUAL", "WORK_TIME_CD");
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
	private <V> void getConditionGroupV2(Map<String, String> group1Id, Map<String, String> group2Id,
			Map<String, ErrorAlarmWorkRecord> tarCon, String comID) {
		List<String> allId = new ArrayList<>(group1Id.keySet());
		allId.addAll(group2Id.keySet());

		CollectionUtil.split(allId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {

			Map<String, Map<Integer, List<Map<String, Integer>>>> target = new HashMap<>();
			Map<String, Map<Integer, List<Map<String, Integer>>>> singleAtd = new HashMap<>();
			getAtdItem(subList, (s) -> buildJDBCSelectConditionGroupItemTarget(s), target);
			getAtdItem(subList, (s) -> buildJDBCSelectConditionGroupSingle(s), singleAtd);

			try (PreparedStatement statement = this.connection()
					.prepareStatement(buildJDBCSelectConditionGroup(subList))) {
				for (int i = 0; i < subList.size(); i++) {
					statement.setString(1 + i, subList.get(i));
				}
				new NtsResultSet(statement.executeQuery()).getList(rs -> {
					String groupId = rs.getString("CONDITION_GROUP_ID");

					boolean isGroup1 = group1Id.get(groupId) != null;

					Integer conditionAtr = rs.getInt("CONDITION_ATR"),
							conditionOperator = rs.getInt("CONDITION_OPERATOR"),
							conditionType = rs.getInt("CONDITION_TYPE"), useAtr = rs.getInt("USE_ATR"),
							compareSingleType = rs.getInt("CompareSingleType"),
							atdItemConNo = rs.getInt("ATD_ITEM_CON_NO"), compareAtr = rs.getInt("COMPARE_ATR"),
							compareSingleAtr = rs.getInt("eraComSingleAtr"), inputCheck = rs.getInt("INPUT_CHECK");

					Double startV = rs.getDouble("START_VALUE"), endV = rs.getDouble("END_VALUE"),
							fixedV = rs.getDouble("FIXED_VALUE");
					V startValue = null, endValue = null, singleValue = null;

					ErAlConditionsAttendanceItem atc = isGroup1
							? tarCon.get(group1Id.get(groupId)).getErrorAlarmCondition().getAtdItemCondition()
									.getGroup1()
							: tarCon.get(group2Id.get(groupId)).getErrorAlarmCondition().getAtdItemCondition()
									.getGroup2();
					if (atc == null) {
						atc = ErAlConditionsAttendanceItem.init(groupId, conditionOperator);
						if (isGroup1) {
							tarCon.get(group1Id.get(groupId)).getErrorAlarmCondition().getAtdItemCondition()
									.setGroup1(atc);
						} else {
							tarCon.get(group2Id.get(groupId)).getErrorAlarmCondition().getAtdItemCondition()
									.setGroup2(atc);
						}
					}

					ErAlAttendanceItemCondition<V> atdItemConDomain = null;
					Optional<ErAlAttendanceItemCondition<?>> atdItemConDomainOpt = atc.getLstErAlAtdItemCon().stream()
							.filter(atci -> atci.getTargetNO() == atdItemConNo).findFirst();
					if (atdItemConDomainOpt.isPresent()) {
						atdItemConDomain = (ErAlAttendanceItemCondition<V>) atdItemConDomainOpt.get();
					} else {
						atdItemConDomain = new ErAlAttendanceItemCondition<V>(comID, "", atdItemConNo, conditionAtr,
								useAtr == 1 ? true : false, conditionType);
						atc.getLstErAlAtdItemCon().add(atdItemConDomain);
					}

					if (target.containsKey(groupId)) {
						List<Map<String, Integer>> targetItem = target.get(groupId).get(atdItemConNo);
						if (!CollectionUtil.isEmpty(targetItem)) {
							List<Integer> uncountables = targetItem.stream().filter(ti -> ti.get("TARGET_ATR") == 2)
									.map(ti -> ti.get("ATTENDANCE_ITEM_ID")).collect(Collectors.toList());
							// Set Target
							if (conditionAtr == ConditionAtr.TIME_WITH_DAY.value && !uncountables.isEmpty()) {
								atdItemConDomain.setUncountableTarget(uncountables.get(0));
							} else if (conditionAtr != ConditionAtr.TIME_WITH_DAY.value) {
								if (atdItemConDomain.getCountableTarget() == null) {
									atdItemConDomain.setCountableTarget(new ArrayList<>(), new ArrayList<>());
								}
								List<Integer> additions = targetItem.stream().filter(ti -> ti.get("TARGET_ATR") == 0)
										.map(ti -> ti.get("ATTENDANCE_ITEM_ID")).collect(Collectors.toList());
								List<Integer> substractions = targetItem.stream()
										.filter(ti -> ti.get("TARGET_ATR") == 1).map(ti -> ti.get("ATTENDANCE_ITEM_ID"))
										.collect(Collectors.toList());

								atdItemConDomain.getCountableTarget().getAddSubAttendanceItems()
										.getAdditionAttendanceItems().addAll(additions);
								atdItemConDomain.getCountableTarget().getAddSubAttendanceItems()
										.getSubstractionAttendanceItems().addAll(substractions);
							}
						} else {
							return null;
						}
					} else {
						return null;
					}

					// Set Compare
					if (compareAtr != null) {
						if (conditionAtr == ConditionAtr.AMOUNT_VALUE.value) {
							startValue = (V) new CheckedAmountValue(startV.intValue());
							endValue = (V) new CheckedAmountValue(endV.intValue());
						} else if (conditionAtr == ConditionAtr.TIME_DURATION.value) {
							startValue = (V) new CheckedTimeDuration(startV.intValue());
							endValue = (V) new CheckedTimeDuration(endV.intValue());
						} else if (conditionAtr == ConditionAtr.TIME_WITH_DAY.value) {
							startValue = (V) new TimeWithDayAttr(startV.intValue());
							endValue = (V) new TimeWithDayAttr(endV.intValue());
						} else if (conditionAtr == ConditionAtr.TIMES.value) {
							startValue = (V) new CheckedTimesValue(startV.intValue());
							endValue = (V) new CheckedTimesValue(endV.intValue());
						} else if (conditionAtr == ConditionAtr.DAYS.value) {
							startValue = (V) new CheckedTimesValueDay(startV);
							endValue = (V) new CheckedTimesValueDay(endV);
						}
						atdItemConDomain.setCompareRange(compareAtr, startValue, endValue);
					} else if (compareSingleAtr != null) {
						if (compareSingleType == ConditionType.FIXED_VALUE.value) {
							if (conditionAtr == ConditionAtr.AMOUNT_VALUE.value) {
								singleValue = (V) new CheckedAmountValue(fixedV.intValue());
							} else if (conditionAtr == ConditionAtr.TIME_DURATION.value) {
								singleValue = (V) new CheckedTimeDuration(fixedV.intValue());
							} else if (conditionAtr == ConditionAtr.TIME_WITH_DAY.value) {
								singleValue = (V) new TimeWithDayAttr(fixedV.intValue());
							} else if (conditionAtr == ConditionAtr.TIMES.value) {
								singleValue = (V) new CheckedTimesValue(fixedV.intValue());
							} else if (conditionAtr == ConditionAtr.DAYS.value) {
								singleValue = (V) new CheckedTimesValueDay(fixedV);
							}
						} else {
							if (singleAtd.containsKey(groupId) && singleAtd.get(groupId).containsKey(atdItemConNo)
									&& !singleAtd.get(groupId).get(atdItemConNo).isEmpty()) {
								singleValue = (V) new AttendanceItemId(
										singleAtd.get(groupId).get(atdItemConNo).get(0).get("ATTENDANCE_ITEM_ID"));
							}
						}
						atdItemConDomain.setCompareSingleValue(compareSingleAtr, compareSingleType, singleValue);
					} else if (inputCheck != null) {
						atdItemConDomain.setInputCheck(inputCheck);
					}

					return null;
				});

			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		});
	}

	private String buildJDBCSelectConditionGroup(List<String> subList) {
		StringBuilder builder = new StringBuilder(
				"SELECT cg.CONDITION_GROUP_ID, cg.CONDITION_OPERATOR, ic.CONDITION_TYPE, ic.ATD_ITEM_CON_NO, ic.CONDITION_ATR, ic.USE_ATR, ");
		builder.append(" cr.CONDITION_GROUP_ID as compareRanID, cr.COMPARE_ATR, cr.START_VALUE, cr.END_VALUE, ");
		builder.append(
				" cs.COMPARE_ATR as eraComSingleAtr, cs.CONDITION_TYPE as CompareSingleType, sf.FIXED_VALUE, inc.INPUT_CHECK ");
		builder.append(
				" FROM KRCMT_ERALST_CNDEXPIPTCHK cg LEFT JOIN KRCMT_ERALST_CNDGRP ic ON cg.CONDITION_GROUP_ID = ic.CONDITION_GROUP_ID ");
		builder.append(
				" LEFT JOIN KRCMT_ERALST_CNDEXP cr ON ic.CONDITION_GROUP_ID = cr.CONDITION_GROUP_ID AND ic.ATD_ITEM_CON_NO = cr.ATD_ITEM_CON_NO");
		builder.append(
				" LEFT JOIN KRCMT_ERALST_CNDEXPTGT cs ON ic.CONDITION_GROUP_ID = cs.CONDITION_GROUP_ID AND ic.ATD_ITEM_CON_NO = cs.ATD_ITEM_CON_NO");
		builder.append(
				" LEFT JOIN KRCMT_ERALST_CNDEXPSGLFIX sf  ON ic.CONDITION_GROUP_ID = sf.CONDITION_GROUP_ID AND ic.ATD_ITEM_CON_NO = sf.ATD_ITEM_CON_NO ");
		builder.append(
				" LEFT JOIN KRCMT_ERALST_CNDEXPSGL inc ON ic.CONDITION_GROUP_ID = inc.CONDITION_GROUP_ID AND ic.ATD_ITEM_CON_NO = inc.ATD_ITEM_CON_NO ");
		builder.append(" WHERE cg.CONDITION_GROUP_ID IN ( "
				+ subList.stream().map(s -> "?").collect(Collectors.joining(",")) + ")");
		return builder.toString();
	}

	@SneakyThrows
	private void getAtdItem(List<String> subList, Function<List<String>, String> getQuery,
			Map<String, Map<Integer, List<Map<String, Integer>>>> target) {
		try (PreparedStatement statement = this.connection().prepareStatement(getQuery.apply(subList))) {
			for (int i = 0; i < subList.size(); i++) {
				statement.setString(1 + i, subList.get(i));
			}
			new NtsResultSet(statement.executeQuery()).getList(rs -> {
				String groupId = rs.getString("CONDITION_GROUP_ID");
				Integer no = rs.getInt("ATD_ITEM_CON_NO");
				if (!target.containsKey(groupId)) {
					target.put(groupId, new HashMap<>());
				}
				if (!target.get(groupId).containsKey(no)) {
					target.get(groupId).put(no, new ArrayList<>());
				}
				Map<String, Integer> t = new HashMap<>();
				t.put("ATTENDANCE_ITEM_ID", rs.getInt("ATTENDANCE_ITEM_ID"));
				t.put("TARGET_ATR", rs.getInt("TARGET_ATR"));
				target.get(groupId).get(no).add(t);
				return null;
			});
		}
	}

	private String buildJDBCSelectConditionGroupItemTarget(List<String> subList) {
		StringBuilder builder = new StringBuilder(
				"SELECT CONDITION_GROUP_ID, ATD_ITEM_CON_NO, ATTENDANCE_ITEM_ID, TARGET_ATR");
		builder.append(" FROM KRCMT_ERALST_CNDEXPRANGE WHERE CONDITION_GROUP_ID IN ( "
				+ subList.stream().map(s -> "?").collect(Collectors.joining(",")) + ")");
		return builder.toString();
	}

	private String buildJDBCSelectConditionGroupSingle(List<String> subList) {
		StringBuilder builder = new StringBuilder(
				"SELECT CONDITION_GROUP_ID, ATD_ITEM_CON_NO, ATTENDANCE_ITEM_ID, TARGET_ATR");
		builder.append(" FROM KRCMT_ERALST_CNDEXPSGLATD WHERE CONDITION_GROUP_ID IN ( "
				+ subList.stream().map(s -> "?").collect(Collectors.joining(",")) + ")");
		return builder.toString();
	}

	@Override
	public List<ErrorAlarmWorkRecord> getAllErAlCompanyAndUseAtrV2(String companyId, boolean useAtr) {
		StringBuilder builder = new StringBuilder("SELECT a, eac, eaa FROM KwrmtErAlWorkRecord a");
		builder.append(" LEFT JOIN a.krcmtErAlCondition eac ");
		builder.append(" LEFT JOIN a.krcstErAlApplication eaa ");
		builder.append(" WHERE a.kwrmtErAlWorkRecordPK.companyId = :companyId AND a.useAtr = :useAtr ");
		return this.queryProxy().query(builder.toString(), Object[].class).setParameter("companyId", companyId)
				.setParameter("useAtr", useAtr).getList().stream()
				.collect(Collectors.groupingBy(c -> c[0], Collectors.toList())).entrySet().stream().map(e -> {
					KwrmtErAlWorkRecord eralRecord = (KwrmtErAlWorkRecord) e.getKey();
					List<KrcmtEralApplication> eralApp = e.getValue().stream().filter(al -> al[2] != null)
							.map(al -> (KrcmtEralApplication) al[2]).collect(Collectors.toList());
					KrcmtErAlCondition eralCon = e.getValue().stream().filter(al -> al[1] != null).findFirst()
							.map(al -> (KrcmtErAlCondition) al[1]).orElse(null);
					ErrorAlarmWorkRecord record = KwrmtErAlWorkRecord.toDomain(eralRecord, eralApp);
					record.setErrorAlarmCondition(KwrmtErAlWorkRecord.toConditionDomain(eralRecord, eralCon));
					return record;
				}).collect(Collectors.toList());
	}

	@Override
	public List<Map<String, Object>> getErAlByComID(String companyId) {
		StringBuilder builder = new StringBuilder(
				"SELECT a.errorDisplayItem, a.kwrmtErAlWorkRecordPK.errorAlarmCode, a.krcmtErAlCondition");
		builder.append(" FROM KwrmtErAlWorkRecord a");
		builder.append(" WHERE a.kwrmtErAlWorkRecordPK.companyId = :companyId ");
		builder.append(" AND a.useAtr = 1 AND a.typeAtr IN (0,1)");
		builder.append(" AND a.errorDisplayItem IS NOT NULL");
		builder.append(" AND a.krcmtErAlCondition IS NOT NULL");
		return this.queryProxy().query(builder.toString(), Object[].class).setParameter("companyId", companyId)
				.getList(c -> {
					ErrorAlarmCondition codition = KrcmtErAlCondition.toDomain((KrcmtErAlCondition) c[2], companyId,
							c[1].toString());
					Map<String, Object> mapped = new HashMap<>();
					mapped.put("Code", c[1].toString());
					mapped.put("ErrorDisplayItem", (int) c[0]);
					mapped.put("ErrorAlarmCondition", codition);
					return mapped;
				});
	}

	@Override
	public List<ErrorAlarmWorkRecord> getListErrorAlarmWorkRecord(String companyId, int fixed) {
		List<KwrmtErAlWorkRecord> lstData = this.queryProxy()
				.query(FIND_BY_COMPANY + " AND a.fixedAtr = :fixedAtr ", KwrmtErAlWorkRecord.class)
				.setParameter("companyId", companyId).setParameter("fixedAtr", fixed == 1).getList();
		return lstData.stream().map(entity -> {
			ErrorAlarmWorkRecord record = KwrmtErAlWorkRecord.toDomain(entity);
			record.setErrorAlarmCondition(KwrmtErAlWorkRecord.toConditionDomain(entity));
			return record;
		}).collect(Collectors.toList());
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<ErrorAlarmWorkRecord> getListErAlByListCodeRemark(String companyId, Collection<String> listCode) {
		// TODO Auto-generated method stub
		List<ErrorAlarmWorkRecord> datas = new ArrayList<>();
		if (listCode.isEmpty())
			return new ArrayList<ErrorAlarmWorkRecord>();

		CollectionUtil.split(listCode.stream().collect(Collectors.toList()), DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT,
				subIdList -> {
					datas.addAll(this.queryProxy().query(SELECT_ERAL_BY_LIST_CODE_REMARK, KwrmtErAlWorkRecord.class)
							.setParameter("listCode", subIdList).setParameter("companyId", companyId)
							.getList(c -> KwrmtErAlWorkRecord.toDomain(c)));
				});
		return datas;
	}

	@Override
	public List<ErrorAlarmWorkRecord> findMobByCompany(String companyID, List<Integer> typeAtrLst) {
		List<KwrmtErAlWorkRecord> lstData = this.queryProxy().query(FIND_MOB_BY_COMPANY, KwrmtErAlWorkRecord.class)
				.setParameter("companyId", companyID).setParameter("typeAtrLst", typeAtrLst).getList();
		return lstData.stream().map(entity -> KwrmtErAlWorkRecord.toDomain(entity)).collect(Collectors.toList());
	}

	@SneakyThrows
	@Override
	public List<String> checkErrorInList(String companyId, List<String> listCode) {
		if (listCode.isEmpty())
			return listCode;
		List<String> lstResult = new ArrayList<>();
		CollectionUtil.split(listCode, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
			String sql = "select ERROR_ALARM_CD from KRCMT_ERAL_DAY_SET where CID = ? and ERROR_ALARM_CD IN ("
					+ subIdList.stream().map(s -> "?").collect(Collectors.joining(",")) + " ) and ERAL_ATR = ?";
			try (val statement = this.connection().prepareStatement(sql.toString())) {
				statement.setString(1, AppContexts.user().companyId());
				for (int i = 0; i < listCode.size(); i++) {
					statement.setString(i + 2, listCode.get(i));
				}
				statement.setInt(listCode.size() + 2, 0);

				lstResult.addAll(new NtsResultSet(statement.executeQuery()).getList(rec -> {
					return rec.getString("ERROR_ALARM_CD");
				}));
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		});
		return lstResult;
	}	
}
