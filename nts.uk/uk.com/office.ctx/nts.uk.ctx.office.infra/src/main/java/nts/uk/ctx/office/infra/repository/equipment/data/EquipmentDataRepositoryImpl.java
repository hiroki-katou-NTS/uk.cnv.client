package nts.uk.ctx.office.infra.repository.equipment.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.text.StringUtil;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.ItemClassification;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationCode;
import nts.uk.ctx.office.dom.equipment.data.ActualItemUsageValue;
import nts.uk.ctx.office.dom.equipment.data.EquipmentData;
import nts.uk.ctx.office.dom.equipment.data.EquipmentDataRepository;
import nts.uk.ctx.office.dom.equipment.data.ResultData;
import nts.uk.ctx.office.dom.equipment.information.EquipmentCode;
import nts.uk.ctx.office.infra.entity.equipment.data.OfidtEquipmentDayAtd;
import nts.uk.ctx.office.infra.entity.equipment.data.OfidtEquipmentDayAtdPK;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class EquipmentDataRepositoryImpl extends JpaRepository implements EquipmentDataRepository {

	// Maximum number of value for each item type
	private static final int MAXIMUM_VALUE = 9;
	private static final List<Integer> TEXT_ITEM_NOS = Arrays.asList(1, 2, 3);
	private static final List<Integer> NUMBER_ITEM_NOS = Arrays.asList(4, 5, 6);
	private static final List<Integer> TIME_ITEM_NOS = Arrays.asList(7, 8, 9);
	
	private static final String SELECT_ALL = "SELECT t FROM OfidtEquipmentDayAtd t ";
	private static final String AND_WITHIN_PERIOD = "AND t.useDate >= :startDate AND t.useDate <= :endDate ";
	private static final String SELECT_BY_EQ_CD_AND_PERIOD = SELECT_ALL
			+ "WHERE t.companyId = :cid AND t.equipmentCode = :equipmentCode "
			+ AND_WITHIN_PERIOD
			+ "ORDER BY t.useDate, t.pk.inputDate ASC";
	private static final String SELECT_BY_EQ_CLS_CD_AND_EQ_CD_AND_PERIOD = SELECT_ALL
			+ "WHERE t.companyId = :cid AND t.equipmentClsCode = :equipmentClsCode AND t.equipmentCode = :equipmentCode "
			+ AND_WITHIN_PERIOD
			+ "ORDER BY t.useDate, t.pk.inputDate ASC";
	private static final String SELECT_BY_INFO = SELECT_ALL
			+ "WHERE t.companyId = :cid AND t.equipmentCode = :equipmentCode AND t.pk.sid = :sid "
			+ "AND t.useDate = :useDate AND t.pk.inputDate = :inputDate";
	private static final String SELECT_BY_EQ_CLS_CD_AND_PERIOD = SELECT_ALL
			+ "WHERE t.companyId = :cid AND t.equipmentClsCode = :equipmentClsCode "
			+ AND_WITHIN_PERIOD;
	private static final String SELECT_BY_PERIOD = SELECT_ALL
			+ "WHERE t.companyId = :cid "
			+ AND_WITHIN_PERIOD;
	
	@Override
	public void insert(EquipmentData domain) {
		this.commandProxy().insert(this.toEntity(domain));
	}

	@Override
	public void update(EquipmentData domain) {
		this.commandProxy().update(this.toEntity(domain));
	}

	@Override
	public void delete(String cid, String sid, GeneralDateTime inputDate) {
		this.commandProxy().remove(OfidtEquipmentDayAtd.class, new OfidtEquipmentDayAtdPK(sid, inputDate));
	}

	@Override
	public List<EquipmentData> findByEquipmentCodeAndPeriod(String cid, String equipmentCode, DatePeriod period) {
		return this.queryProxy().query(SELECT_BY_EQ_CD_AND_PERIOD, OfidtEquipmentDayAtd.class)
				.setParameter("cid", cid)
				.setParameter("equipmentCode", equipmentCode)
				.setParameter("startDate", period.start())
				.setParameter("endDate", period.end())
				.getList(this::toDomain);
	}

	@Override
	public List<EquipmentData> findByEquipmentClsCodeAndEquipmentCodeAndPeriod(String cid, String equipmentClsCode,
			String equipmentCode, DatePeriod period) {
		return this.queryProxy().query(SELECT_BY_EQ_CLS_CD_AND_EQ_CD_AND_PERIOD, OfidtEquipmentDayAtd.class)
				.setParameter("cid", cid)
				.setParameter("equipmentClsCode", equipmentClsCode)
				.setParameter("equipmentCode", equipmentCode)
				.setParameter("startDate", period.start())
				.setParameter("endDate", period.end())
				.getList(this::toDomain);
	}

	@Override
	public Optional<EquipmentData> findByUsageInfo(String cid, String equipmentCode, GeneralDate useDate,
			String sid, GeneralDateTime inputDate) {
		return this.queryProxy().query(SELECT_BY_INFO, OfidtEquipmentDayAtd.class)
				.setParameter("cid", cid)
				.setParameter("equipmentCode", equipmentCode)
				.setParameter("sid", sid)
				.setParameter("useDate", useDate)
				.setParameter("inputDate", inputDate)
				.getSingle(this::toDomain);
	}

	@Override
	public List<EquipmentData> findByEquipmentClsCodeAndPeriod(String cid, String equipmentClsCode, DatePeriod period) {
		return this.queryProxy().query(SELECT_BY_EQ_CLS_CD_AND_PERIOD, OfidtEquipmentDayAtd.class)
				.setParameter("cid", cid)
				.setParameter("equipmentClsCode", equipmentClsCode)
				.setParameter("startDate", period.start())
				.setParameter("endDate", period.end())
				.getList(this::toDomain);
	}

	@Override
	public List<EquipmentData> findByPeriod(String cid, DatePeriod period) {
		return this.queryProxy().query(SELECT_BY_PERIOD, OfidtEquipmentDayAtd.class)
				.setParameter("cid", cid)
				.setParameter("startDate", period.start())
				.setParameter("endDate", period.end())
				.getList(this::toDomain);
	}

	@Override
	public List<EquipmentData> findByPeriodAndOptionalInput(String cid, DatePeriod period,
			Optional<String> optEquipmentCode, Optional<String> optEquipmentClsCode) {
		if (optEquipmentCode.isPresent()) {
			if (optEquipmentClsCode.isPresent()) {
				// return　[5] Get*()
				return this.findByEquipmentClsCodeAndEquipmentCodeAndPeriod(cid, optEquipmentClsCode.get(),
						optEquipmentCode.get(), period);
			} else {
				// return　[4] Get*()
				return this.findByEquipmentCodeAndPeriod(cid, optEquipmentCode.get(), period);
			}
		} else {
			if (optEquipmentClsCode.isPresent()) {
				// return　[7] Get*()
				return this.findByEquipmentClsCodeAndPeriod(cid, optEquipmentClsCode.get(), period);
			} else {
				// return　[8] Get*()
				return this.findByPeriod(cid, period);
			}
		}
	}

	private EquipmentData toDomain(OfidtEquipmentDayAtd entity) {
		List<ResultData> itemDatas = new ArrayList<>();
		for (ItemClassification itemCls : ItemClassification.values()) {
			String type = itemCls.toString().toLowerCase();
			for (int i = 1; i <= MAXIMUM_VALUE; i++) {
				if (!this.isWithinItemNos(itemCls, i)) {
					continue;
				}
				Field f = this.getField(type, i);
				Optional<ActualItemUsageValue> actualValue;
				try {
					actualValue = Optional.ofNullable(f.get(entity)).map(String::valueOf).map(ActualItemUsageValue::new);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					actualValue = Optional.empty();
				}
				itemDatas.add(new ResultData(new EquipmentItemNo(String.valueOf(i)), itemCls, actualValue));
			}
		}
		return new EquipmentData(entity.getPk().getInputDate(), entity.getUseDate(), entity.getPk().getSid(),
				new EquipmentCode(entity.getEquipmentCode()),
				new EquipmentClassificationCode(entity.getEquipmentClsCode()), itemDatas);
	}
	
	private OfidtEquipmentDayAtd toEntity(EquipmentData domain) {
		OfidtEquipmentDayAtdPK pk = new OfidtEquipmentDayAtdPK(domain.getSid(), domain.getInputDate());
		OfidtEquipmentDayAtd entity = new OfidtEquipmentDayAtd();
		entity.setPk(pk);
		entity.setCompanyId(AppContexts.user().companyId());
		entity.setContractCd(AppContexts.user().contractCode());
		entity.setEquipmentClsCode(domain.getEquipmentClassificationCode().v());
		entity.setEquipmentCode(domain.getEquipmentCode().v());
		entity.setUseDate(domain.getUseDate());
		
		domain.getResultDatas().forEach(itemData -> {
			String type = itemData.getItemClassification().toString().toLowerCase();
			Optional<String> optActualValue = itemData.getActualValue().map(ActualItemUsageValue::v);
			if (optActualValue.isPresent() && StringUtil.isNullOrEmpty(optActualValue.get(), true)) {
				optActualValue = Optional.empty();
			}
			Field f = this.getField(type, Integer.valueOf(itemData.getItemNo().v()));
			try {
				switch (itemData.getItemClassification()) {
				case TEXT:
					f.set(entity, optActualValue.orElse(null));
					break;
				case NUMBER:
				case TIME:
					f.set(entity, optActualValue.map(Integer::valueOf).orElse(null));
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return entity;
	}

	private Field getField(String type, int index) {
		Class<OfidtEquipmentDayAtd> c = OfidtEquipmentDayAtd.class;
		String fieldName = type + "Value" + index;
		try {
			Field f = c.getDeclaredField(fieldName);
			f.setAccessible(true);
			return f;
		} catch (SecurityException | NoSuchFieldException e) {
			return null;
		}
	}
	
	private boolean isWithinItemNos(ItemClassification itemClassification, int index) {
		switch (itemClassification) {
		case TEXT:
			return TEXT_ITEM_NOS.contains(index);
		case NUMBER:
			return NUMBER_ITEM_NOS.contains(index);
		case TIME:
			return TIME_ITEM_NOS.contains(index);
		}
		return false;
	}
}
