/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.infra.repository.optitem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lombok.SneakyThrows;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.infra.entity.optitem.KrcmtAnyfResultRange;
import nts.uk.ctx.at.record.infra.entity.optitem.KrcmtCalcResultRangePK;
import nts.uk.ctx.at.record.infra.entity.optitem.KrcmtAnyv;
import nts.uk.ctx.at.record.infra.entity.optitem.KrcmtAnyvPK;
import nts.uk.ctx.at.record.infra.entity.optitem.KrcstOptionalItemPK_;
import nts.uk.ctx.at.record.infra.entity.optitem.KrcstOptionalItem_;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemUsageAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.PerformanceAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.export.CalFormulasItemExportData;
import nts.uk.ctx.at.shared.dom.scherec.optitem.export.CalFormulasItemTableExportData;

/**
 * The Class JpaOptionalItemRepository.
 */
@Stateless
public class JpaOptionalItemRepository extends JpaRepository implements OptionalItemRepository {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.OptionalItemRepository#update(nts.uk.ctx
	 * .at.record.dom.optitem.OptionalItem)
	 */
	@Override
	public void update(OptionalItem dom) {

		// Find entity
		KrcmtAnyv entity = this.queryProxy()
				.find(new KrcmtAnyvPK(dom.getCompanyId().v(), dom.getOptionalItemNo().v()),
						KrcmtAnyv.class)
				.get();

		// Update entity
		dom.saveToMemento(new JpaOptionalItemSetMemento(entity));
		this.commandProxy().update(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.OptionalItemRepository#find(java.lang.
	 * String, java.lang.String)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public OptionalItem find(String companyId, Integer optionalItemNo) {
		KrcmtAnyv entity = this.queryProxy()
				.find(new KrcmtAnyvPK(companyId, optionalItemNo), KrcmtAnyv.class).get();

		// Return
		return new OptionalItem(new JpaOptionalItemGetMemento(entity));
	}
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<OptionalItem> findByItemNo(String companyId, Integer optionalItemNo) {
    	Optional<KrcmtAnyv> entity = this.queryProxy()
				.find(new KrcmtAnyvPK(companyId, optionalItemNo), KrcmtAnyv.class);
    	if(entity.isPresent()) {
    		return Optional.of(new OptionalItem(new JpaOptionalItemGetMemento(entity.get())));
    	}
		// Return
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.OptionalItemRepository#findAll(java.lang
	 * .String)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	@SneakyThrows
	public List<OptionalItem> findAll(String companyId) {
		try (val stmt = this.connection()
				.prepareStatement("select * from KRCMT_ANYV KOI LEFT JOIN KRCMT_ANYF_RESULT_RANGE KCRR "
						+ "on KOI.CID = KCRR.CID and KOI.OPTIONAL_ITEM_NO = KCRR.OPTIONAL_ITEM_NO "
						+ "where KOI.CID = ? ORDER BY KOI.OPTIONAL_ITEM_NO ASC")) {
			stmt.setString(1, companyId);

            List<OptionalItem> result = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
				KrcmtAnyv item = new KrcmtAnyv();
				item.setKrcmtAnyvPK(new KrcmtAnyvPK(companyId, rec.getInt("OPTIONAL_ITEM_NO")));
				item.setOptionalItemName(rec.getString("OPTIONAL_ITEM_NAME"));
				item.setOptionalItemAtr(rec.getInt("OPTIONAL_ITEM_ATR"));
				item.setUsageAtr(rec.getInt("USAGE_ATR"));
				item.setCalcAtr(rec.getInt("CALC_ATR"));
				item.setPerformanceAtr(rec.getInt("PERFORMANCE_ATR"));
				item.setEmpConditionAtr(rec.getInt("EMP_CONDITION_ATR"));
				item.setUnitOfOptionalItem(rec.getString("UNIT_OF_OPTIONAL_ITEM"));
				item.setCalcAtr(rec.getInt("CALC_ATR"));
				item.setNote(rec.getString("ITEM_NOTE"));
				item.setDescription(rec.getString("ITEM_DESCRIP"));
				item.setInputCheck(rec.getBoolean("INPUT_WITH_CHECKBOX"));
				KrcmtAnyfResultRange range = new KrcmtAnyfResultRange();
				range.setKrcstCalcResultRangePK(new KrcmtCalcResultRangePK(companyId, rec.getInt("OPTIONAL_ITEM_NO")));
				
				if (rec.getBoolean("UPPER_LIMIT_ATR") != null) {
				    range.setUpperLimitAtr(rec.getBoolean("UPPER_LIMIT_ATR"));
				    
				    range.setUpperDayTimeRange(rec.getInt("UPPER_DAY_TIME_RANGE"));
				    range.setUpperDayNumberRange(rec.getBigDecimal("UPPER_DAY_NUMBER_RANGE"));
				    range.setUpperdayAmountRange(rec.getInt("UPPER_DAY_AMOUNT_RANGE"));
				    range.setUpperMonTimeRange(rec.getInt("UPPER_MON_TIME_RANGE"));
				    range.setUpperMonNumberRange(rec.getBigDecimal("UPPER_MON_NUMBER_RANGE"));
				    range.setUpperMonAmountRange(rec.getInt("UPPER_MON_AMOUNT_RANGE"));
				} else {
				    range.setUpperLimitAtr(false);
				}
				
				if (rec.getBoolean("LOWER_LIMIT_ATR") != null) {
				    range.setLowerLimitAtr(rec.getBoolean("LOWER_LIMIT_ATR"));
				    
				    range.setLowerDayTimeRange(rec.getInt("LOWER_DAY_TIME_RANGE"));
				    range.setLowerDayNumberRange(rec.getBigDecimal("LOWER_DAY_NUMBER_RANGE"));
				    range.setLowerDayAmountRange(rec.getInt("LOWER_DAY_AMOUNT_RANGE"));
				    range.setLowerMonTimeRange(rec.getInt("LOWER_MON_TIME_RANGE"));
				    range.setLowerMonNumberRange(rec.getBigDecimal("LOWER_MON_NUMBER_RANGE"));
				    range.setLowerMonAmountRange(rec.getInt("LOWER_MON_AMOUNT_RANGE"));
				} else {
				    range.setLowerLimitAtr(false);
				}
				range.setTimeItemInputUnit(rec.getInt("INPUT_UNIT_TIME"));
				range.setNumberItemInputUnit(rec.getInt("INPUT_UNIT_NUMBER"));
				range.setAmountItemInputUnit(rec.getInt("INPUT_UNIT_AMOUNT"));

				return new OptionalItem(new JpaOptionalItemGetMemento(item, range));
			});
            return result;
		}catch (Exception e){
			System.out.println("Check error mes KWR :-----------------" + e.getMessage());
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<OptionalItem> findByAtr(String companyId, int atr) {
		// Get entity manager
		EntityManager em = this.getEntityManager();

		// Create builder
		CriteriaBuilder builder = em.getCriteriaBuilder();

		// Create query
		CriteriaQuery<?> cq = builder.createQuery();

		// From table
		Root<KrcmtAnyv> root = cq.from(KrcmtAnyv.class);
		Join<KrcmtAnyv, KrcmtAnyfResultRange> joinRoot = root.join(KrcstOptionalItem_.krcstCalcResultRange,
				JoinType.LEFT);

		List<Predicate> predicateList = new ArrayList<Predicate>();

		// Add where condition
		predicateList.add(builder.equal(root.get(KrcstOptionalItem_.krcmtAnyvPK).get(KrcstOptionalItemPK_.cid),
				companyId));
		predicateList.add(builder.equal(root.get(KrcstOptionalItem_.optionalItemAtr), atr));
		cq.multiselect(root, joinRoot);
		cq.where(predicateList.toArray(new Predicate[] {}));

		// Get results
		@SuppressWarnings("unchecked")
		List<Object[]> results = (List<Object[]>) em.createQuery(cq).getResultList();

		// Check empty
		if (CollectionUtil.isEmpty(results)) {
			return Collections.emptyList();
		}

		// Return
		return results.stream()
				.map(item -> new OptionalItem(
						new JpaOptionalItemGetMemento((KrcmtAnyv) item[0], (KrcmtAnyfResultRange) item[1])))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.OptionalItemRepository#findByListNos(
	 * java.lang.String, java.util.List)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	@SneakyThrows
	public List<OptionalItem> findByListNos(String companyId, List<Integer> optionalitemNos) {
		// Check empty
		if (CollectionUtil.isEmpty(optionalitemNos)) {
			return Collections.emptyList();
		}

		try (val stmt = this.connection()
				.prepareStatement("select * from KRCMT_ANYV KOI LEFT JOIN KRCMT_ANYF_RESULT_RANGE KCRR "
						+ "on KOI.CID = KCRR.CID and KOI.OPTIONAL_ITEM_NO = KCRR.OPTIONAL_ITEM_NO "
						+ "where KOI.CID = ? and KOI.OPTIONAL_ITEM_NO in ("
						+ NtsStatement.In.createParamsString(optionalitemNos)
						+ ") ORDER BY KOI.OPTIONAL_ITEM_NO ASC")) {
			stmt.setString(1, companyId);
			for (int i = 0; i < optionalitemNos.size(); i++) {
				stmt.setInt(i + 2, optionalitemNos.get(i));
			}

			return new NtsResultSet(stmt.executeQuery()).getList(rec -> {
				KrcmtAnyv item = new KrcmtAnyv();
				item.setKrcmtAnyvPK(new KrcmtAnyvPK(companyId, rec.getInt("OPTIONAL_ITEM_NO")));
				item.setOptionalItemName(rec.getString("OPTIONAL_ITEM_NAME"));
				item.setOptionalItemAtr(rec.getInt("OPTIONAL_ITEM_ATR"));
				item.setUsageAtr(rec.getInt("USAGE_ATR"));
				item.setCalcAtr(rec.getInt("CALC_ATR"));
				item.setPerformanceAtr(rec.getInt("PERFORMANCE_ATR"));
				item.setEmpConditionAtr(rec.getInt("EMP_CONDITION_ATR"));
				item.setUnitOfOptionalItem(rec.getString("UNIT_OF_OPTIONAL_ITEM"));
                item.setCalcAtr(rec.getInt("CALC_ATR"));
                item.setNote(rec.getString("ITEM_NOTE"));
                item.setDescription(rec.getString("ITEM_DESCRIP"));
				item.setInputCheck(rec.getBoolean("INPUT_WITH_CHECKBOX"));
				KrcmtAnyfResultRange range = new KrcmtAnyfResultRange();
				range.setKrcstCalcResultRangePK(new KrcmtCalcResultRangePK(companyId, rec.getInt("OPTIONAL_ITEM_NO")));
				if (rec.getBoolean("UPPER_LIMIT_ATR") != null) {
                    range.setUpperLimitAtr(rec.getBoolean("UPPER_LIMIT_ATR"));
                    
                    range.setUpperDayTimeRange(rec.getInt("UPPER_DAY_TIME_RANGE"));
                    range.setUpperDayNumberRange(rec.getBigDecimal("UPPER_DAY_NUMBER_RANGE"));
                    range.setUpperdayAmountRange(rec.getInt("UPPER_DAY_AMOUNT_RANGE"));
                    range.setUpperMonTimeRange(rec.getInt("UPPER_MON_TIME_RANGE"));
                    range.setUpperMonNumberRange(rec.getBigDecimal("UPPER_MON_NUMBER_RANGE"));
                    range.setUpperMonAmountRange(rec.getInt("UPPER_MON_AMOUNT_RANGE"));
                } else {
                    range.setUpperLimitAtr(false);
                }
                
                if (rec.getBoolean("LOWER_LIMIT_ATR") != null) {
                    range.setLowerLimitAtr(rec.getBoolean("LOWER_LIMIT_ATR"));
                    
                    range.setLowerDayTimeRange(rec.getInt("LOWER_DAY_TIME_RANGE"));
                    range.setLowerDayNumberRange(rec.getBigDecimal("LOWER_DAY_NUMBER_RANGE"));
                    range.setLowerDayAmountRange(rec.getInt("LOWER_DAY_AMOUNT_RANGE"));
                    range.setLowerMonTimeRange(rec.getInt("LOWER_MON_TIME_RANGE"));
                    range.setLowerMonNumberRange(rec.getBigDecimal("LOWER_MON_NUMBER_RANGE"));
                    range.setLowerMonAmountRange(rec.getInt("LOWER_MON_AMOUNT_RANGE"));
                } else {
                    range.setLowerLimitAtr(false);
                }
                range.setTimeItemInputUnit(rec.getInt("INPUT_UNIT_TIME"));
                range.setNumberItemInputUnit(rec.getInt("INPUT_UNIT_NUMBER"));
                range.setAmountItemInputUnit(rec.getInt("INPUT_UNIT_AMOUNT"));

				return new OptionalItem(new JpaOptionalItemGetMemento(item, range));
			});
		} catch (Exception e){
			System.out.println("Check error mes KWR:-----------------------" + e.getMessage());
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.OptionalItemRepository#findByAtr(java.
	 * lang.String, nts.uk.ctx.at.record.dom.optitem.OptionalItemAtr)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<OptionalItem> findByAtr(String companyId, OptionalItemAtr atr) {
		// Get entity manager
		EntityManager em = this.getEntityManager();

		// Create builder
		CriteriaBuilder builder = em.getCriteriaBuilder();

		// Create query
		CriteriaQuery<?> cq = builder.createQuery();

		// From table
		Root<KrcmtAnyv> root = cq.from(KrcmtAnyv.class);
		Join<KrcmtAnyv, KrcmtAnyfResultRange> joinRoot = root.join(KrcstOptionalItem_.krcstCalcResultRange,
				JoinType.LEFT);

		List<Predicate> predicateList = new ArrayList<Predicate>();

		// Add where condition
		predicateList.add(builder.equal(root.get(KrcstOptionalItem_.krcmtAnyvPK).get(KrcstOptionalItemPK_.cid),
				companyId));
		predicateList.add(builder.equal(root.get(KrcstOptionalItem_.optionalItemAtr), atr.value));
		cq.multiselect(root, joinRoot);
		cq.where(predicateList.toArray(new Predicate[] {}));

		// Get results
		@SuppressWarnings("unchecked")
		List<Object[]> results = (List<Object[]>) em.createQuery(cq).getResultList();

		// Check empty
		if (CollectionUtil.isEmpty(results)) {
			return Collections.emptyList();
		}

		// Return
		return results.stream()
				.map(item -> new OptionalItem(
						new JpaOptionalItemGetMemento((KrcmtAnyv) item[0], (KrcmtAnyfResultRange) item[1])))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemRepository#
	 * findByPerformanceAtr(java.lang.String,
	 * nts.uk.ctx.at.record.dom.optitem.PerformanceAtr)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<OptionalItem> findByPerformanceAtr(String companyId, PerformanceAtr atr) {
		// Get entity manager
		EntityManager em = this.getEntityManager();

		// Create builder
		CriteriaBuilder builder = em.getCriteriaBuilder();

		// Create query
		CriteriaQuery<?> cq = builder.createQuery();

		// From table
		Root<KrcmtAnyv> root = cq.from(KrcmtAnyv.class);
		Join<KrcmtAnyv, KrcmtAnyfResultRange> joinRoot = root.join(KrcstOptionalItem_.krcstCalcResultRange,
				JoinType.LEFT);

		List<Predicate> predicateList = new ArrayList<Predicate>();

		// Add where condition
		predicateList.add(builder.equal(root.get(KrcstOptionalItem_.krcmtAnyvPK).get(KrcstOptionalItemPK_.cid),
				companyId));
		predicateList.add(builder.equal(root.get(KrcstOptionalItem_.performanceAtr), atr.value));
		cq.multiselect(root, joinRoot);
		cq.where(predicateList.toArray(new Predicate[] {}));

		// Get results
		@SuppressWarnings("unchecked")
		List<Object[]> results = (List<Object[]>) em.createQuery(cq).getResultList();

		// Check empty
		if (CollectionUtil.isEmpty(results)) {
			return Collections.emptyList();
		}

		// Return
		return results.stream()
				.map(item -> new OptionalItem(
						new JpaOptionalItemGetMemento((KrcmtAnyv) item[0], (KrcmtAnyfResultRange) item[1])))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.optitem.OptionalItemRepository#
	 * findByPerformanceAtr(java.lang.String,
	 * nts.uk.ctx.at.record.dom.optitem.PerformanceAtr)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<OptionalItem> findUsedByPerformanceAtr(String companyId, PerformanceAtr atr) {
		// Get entity manager
		EntityManager em = this.getEntityManager();

		// Create builder
		CriteriaBuilder builder = em.getCriteriaBuilder();

		// Create query
		CriteriaQuery<Tuple> cq = builder.createQuery(Tuple.class);

		// From table
		Root<KrcmtAnyv> root = cq.from(KrcmtAnyv.class);
		Join<KrcmtAnyv, KrcmtAnyfResultRange> join = root.join(KrcstOptionalItem_.krcstCalcResultRange,
				JoinType.LEFT);

		List<Predicate> predicateList = new ArrayList<Predicate>();

		// Add where condition
		predicateList.add(builder.equal(root.get(KrcstOptionalItem_.krcmtAnyvPK).get(KrcstOptionalItemPK_.cid),
				companyId));
		predicateList.add(builder.equal(root.get(KrcstOptionalItem_.performanceAtr), atr.value));
		predicateList.add(builder.equal(root.get(KrcstOptionalItem_.usageAtr), OptionalItemUsageAtr.USE.value));
		cq.where(predicateList.toArray(new Predicate[] {}));

		cq = cq.multiselect(root, join);
		List<Tuple> result = em.createQuery(cq.distinct(true)).getResultList();

		// Get results
		return result.stream()
				.map(c -> new OptionalItem(
						new JpaOptionalItemGetMemento((KrcmtAnyv) c.get(0), (KrcmtAnyfResultRange) c.get(1))))
				.collect(Collectors.toList());
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Map<Integer, OptionalItemAtr> findOptionalTypeBy(String companyId, PerformanceAtr atr) {
		// Get entity manager
		EntityManager em = this.getEntityManager();

		// Create builder
		CriteriaBuilder builder = em.getCriteriaBuilder();

		// Create query
		CriteriaQuery<Tuple> cq = builder.createTupleQuery();

		// From table
		Root<KrcmtAnyv> root = cq.from(KrcmtAnyv.class);

		List<Predicate> predicateList = new ArrayList<Predicate>();

		// Add where condition
		predicateList.add(builder.equal(root.get(KrcstOptionalItem_.krcmtAnyvPK).get(KrcstOptionalItemPK_.cid),
				companyId));
		predicateList.add(builder.equal(root.get(KrcstOptionalItem_.performanceAtr), atr.value));
		cq.where(predicateList.toArray(new Predicate[] {}));

		// Get NO and optional attr only
		cq.multiselect(root.get(KrcstOptionalItem_.krcmtAnyvPK).get(KrcstOptionalItemPK_.optionalItemNo),
				root.get(KrcstOptionalItem_.optionalItemAtr));

		// Get results
		List<Tuple> results = em.createQuery(cq).getResultList();

		// Check empty
		if (CollectionUtil.isEmpty(results)) {
			return new HashMap<>();
		}

		// Return
		return results.stream().collect(Collectors.toMap(r -> (Integer) r.get(0),
				r -> EnumAdaptor.valueOf((Integer) r.get(1), OptionalItemAtr.class)));
	}

	@Override
	public List<CalFormulasItemExportData> findAllData(String companyId) {
		// TODO Auto-generated method stub
		return null;
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	@SneakyThrows
	public List<CalFormulasItemExportData> findAllCalFormulasItem(String companyId, String languageId) {
		try (val stmt = this.connection().prepareStatement(
				"SELECT ec.EMP_CD , oi.OPTIONAL_ITEM_NO, oi.OPTIONAL_ITEM_NAME, oi.EMP_CONDITION_ATR FROM "
						+ "KRCMT_ANYV oi " + "LEFT JOIN KRCMT_ANYF_RESULT_RANGE rr ON oi.CID = rr.CID "
						+ "AND oi.OPTIONAL_ITEM_NO = rr.OPTIONAL_ITEM_NO "
						+ "LEFT JOIN KRCMT_ANYF_COND_EMP ec ON rr.OPTIONAL_ITEM_NO = ec.OPTIONAL_ITEM_NO AND rr.CID = ec.CID"
						+ "where oi.CID = ? AND ec.EMP_CD IS NOT NULL ORDER BY oi.OPTIONAL_ITEM_NO ASC")) {
			stmt.setString(1, companyId);
			return new NtsResultSet(stmt.executeQuery()).getList(rec -> {
				return toReportData(rec);
			});
		} 

	}

	private CalFormulasItemExportData toReportData(NtsResultRecord rec) {
		Map<String, String> empConditions = new HashMap<>();
		empConditions.put(rec.getString("EMP_CD"), rec.getString("EMP_APPL_ATR"));
		CalFormulasItemExportData item = new CalFormulasItemExportData(
				rec.getString("OPTIONAL_ITEM_NO"),
				rec.getString("OPTIONAL_ITEM_NAME"), 
				rec.getString("EMP_CONDITION_ATR"), 
				empConditions);
		return item;
	}

	// Export Data table
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	@SneakyThrows
	public List<CalFormulasItemTableExportData> findAllCalFormulasTableItem(String companyId, String languageId) {
		try (val stmt = this.connection().prepareStatement(
				"SELECT "
				+"oi.OPTIONAL_ITEM_NO, oi.OPTIONAL_ITEM_NAME, oif.SYMBOL, fd.DISPORDER "
				+", fr.ROUNDING_ATR "
				+", oi.PERFORMANCE_ATR "
				+", oi.EMP_CONDITION_ATR, oi.PERFORMANCE_ATR, oif.FORMULA_ID, fd.DISPORDER "
				+", oif.FORMULA_ATR, oif.FORMULA_NAME, oif.CALC_ATR "
				+", fr.NUMBER_ROUNDING, fr.TIME_ROUNDING, fr.AMOUNT_ROUNDING, fr.NUMBER_ROUNDING_UNIT, fr.TIME_ROUNDING_UNIT, fr.AMOUNT_ROUNDING_UNIT "
				+", cis.MINUS_SEGMENT, cis.OPERATOR "
				+", fs.LEFT_FORMULA_ITEM_ID, fs.LEFT_SET_METHOD, fs.LEFT_INPUT_VAL, fs.RIGHT_FORMULA_ITEM_ID, fs.RIGHT_SET_METHOD, fs.RIGHT_INPUT_VAL "
				+"FROM "
				+"KRCMT_ANYV oi "
				+"INNER JOIN KRCMT_ANYF oif ON oi.CID = oif.CID AND oi.OPTIONAL_ITEM_NO = oif.OPTIONAL_ITEM_NO "
				+"INNER JOIN KRCMT_ANYF_SORT fd ON oif.OPTIONAL_ITEM_NO = fd.OPTIONAL_ITEM_NO AND oif.CID = fd.CID AND oif.FORMULA_ID = fd.FORMULA_ID " 
				+"INNER JOIN KRCMT_ANYF_ROUND fr ON fd.FORMULA_ID = fr.FORMULA_ID and fd.OPTIONAL_ITEM_NO = fr.OPTIONAL_ITEM_NO "
				+"INNER JOIN KRCMT_ANYF_ITEM_SELECT cis ON fd.FORMULA_ID = cis.FORMULA_ID AND fd.OPTIONAL_ITEM_NO = cis.OPTIONAL_ITEM_NO "
				+"LEFT JOIN KRCMT_ANYF_DETAIL fs ON (fr.FORMULA_ID = fs.LEFT_FORMULA_ITEM_ID  OR fr.FORMULA_ID = fs.RIGHT_FORMULA_ITEM_ID) "
				+"WHERE oi.CID = ? "
				)) {
			stmt.setString(1, companyId);
			return new NtsResultSet(stmt.executeQuery()).getList(rec -> {
				return toReportDataTable(rec);
			});
		}

	}

	private CalFormulasItemTableExportData toReportDataTable(NtsResultRecord rec) {
		return null;
	}
	/*
	 * private CalFormulasItemExportData toReportData(Object[] entity, String
	 * langId) { // TODO Auto-generated method stub return new
	 * CalFormulasItemExportData(
	 * 
	 * ); }
	 */

}
