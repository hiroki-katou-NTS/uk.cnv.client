package nts.uk.ctx.at.aggregation.infra.repository.scheduletable.outputsetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.ctx.at.aggregation.dom.scheduletable.OneRowOutputItem;
import nts.uk.ctx.at.aggregation.dom.scheduletable.OutputItem;
import nts.uk.ctx.at.aggregation.dom.scheduletable.OutputSettingCode;
import nts.uk.ctx.at.aggregation.dom.scheduletable.OutputSettingName;
import nts.uk.ctx.at.aggregation.dom.scheduletable.ScheduleTableAttendanceItem;
import nts.uk.ctx.at.aggregation.dom.scheduletable.ScheduleTableOutputSetting;
import nts.uk.ctx.at.aggregation.dom.scheduletable.ScheduleTableOutputSettingRepository;
import nts.uk.ctx.at.aggregation.dom.scheduletable.ScheduleTablePersonalInfoItem;
import nts.uk.ctx.at.aggregation.infra.entity.scheduletable.outputsetting.KagmtRptSchedule;
import nts.uk.ctx.at.aggregation.infra.entity.scheduletable.outputsetting.KagmtRptScheduleItem;
import nts.uk.ctx.at.aggregation.infra.entity.scheduletable.outputsetting.KagmtRptScheduleItemPk;
import nts.uk.ctx.at.aggregation.infra.entity.scheduletable.outputsetting.KagmtRptSchedulePk;
import nts.uk.ctx.at.aggregation.infra.entity.scheduletable.outputsetting.KagmtRptScheduleTallyByPerson;
import nts.uk.ctx.at.aggregation.infra.entity.scheduletable.outputsetting.KagmtRptScheduleTallyByPersonPk;
import nts.uk.ctx.at.aggregation.infra.entity.scheduletable.outputsetting.KagmtRptScheduleTallyByWkp;
import nts.uk.ctx.at.aggregation.infra.entity.scheduletable.outputsetting.KagmtRptScheduleTallyByWkpPk;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 	
 * @author quytb
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaScheduleTableOutputSettingItemRepository extends JpaRepository
		implements ScheduleTableOutputSettingRepository {
	
	private static final String sql1 = "SELECT a FROM KagmtRptSchedule a WHERE a.pk.companyId = :companyId AND a.pk.code = :code";
	private static final String sql2 = "SELECT a FROM KagmtRptScheduleItem a WHERE a.pk.companyId = :companyId AND a.pk.code = :code";	
	private static final String sql3 = "SELECT a FROM KagmtRptScheduleTallyByPerson a WHERE a.pk.companyId = :companyId AND a.pk.code = :code ORDER BY a.displayOrder";
	private static final String sql4 = "SELECT a FROM KagmtRptScheduleTallyByWkp a WHERE a.pk.companyId = :companyId AND a.pk.code = :code ORDER BY a.displayOrder";

	private ScheduleTableOutputSetting toDomainFromEntity(KagmtRptSchedule kagmtRptSchedule,
			List<KagmtRptScheduleItem> kagmtRptScheduleItems, List<KagmtRptScheduleTallyByPerson> kagmtRptScheduleTallyByPersons,
			List<KagmtRptScheduleTallyByWkp> kagmtRptScheduleTallyByWkps) {
		List<OneRowOutputItem> oneRowOutputItems = new ArrayList<OneRowOutputItem>();
		List<PersonalCounterCategory> personalCounterCategories = new ArrayList<PersonalCounterCategory>();
		List<WorkplaceCounterCategory> workplaceCounterCategories = new ArrayList<WorkplaceCounterCategory>();
		if(kagmtRptScheduleTallyByPersons != null && !kagmtRptScheduleTallyByPersons.isEmpty()) {
			kagmtRptScheduleTallyByPersons.stream().forEach(x -> {
				if(x.pk.categoryNo != null) {
					personalCounterCategories.add(PersonalCounterCategory.of(x.pk.categoryNo));
				}	
			});
		}		
		
		if(kagmtRptScheduleTallyByWkps != null && !kagmtRptScheduleTallyByWkps.isEmpty()) {
			kagmtRptScheduleTallyByWkps.stream().forEach(x -> {
				if(x.pk.categoryNo != null) {
					workplaceCounterCategories.add(WorkplaceCounterCategory.of(x.pk.categoryNo));
				}	
			});			
		}
		
		kagmtRptScheduleItems.stream().forEach(x -> {
			if (x.personalItem == null && x.additionalPersonalItem == null && x.attendanceItem == null) {
				oneRowOutputItems.add(OneRowOutputItem.create(
						Optional.of(EnumAdaptor.valueOf(0, ScheduleTablePersonalInfoItem.class)),
						Optional.of(EnumAdaptor.valueOf(0, ScheduleTablePersonalInfoItem.class)),
						Optional.of(EnumAdaptor.valueOf(0, ScheduleTableAttendanceItem.class))));
			} else {
				OneRowOutputItem item = OneRowOutputItem.create(
						Optional.ofNullable(x.personalItem != null ? EnumAdaptor.valueOf(x.personalItem, ScheduleTablePersonalInfoItem.class) : null),
						Optional.ofNullable(x.additionalPersonalItem != null ? EnumAdaptor.valueOf(x.additionalPersonalItem, ScheduleTablePersonalInfoItem.class): null),
						Optional.ofNullable(x.attendanceItem != null ? EnumAdaptor.valueOf(x.attendanceItem, ScheduleTableAttendanceItem.class): null));
				oneRowOutputItems.add(item);
			}			
		});
		
		OutputItem outputItem = new OutputItem(NotUseAtr.valueOf(kagmtRptSchedule.additionalColumUseAtr),
				NotUseAtr.valueOf(kagmtRptSchedule.shiftBackColorUseAtr), NotUseAtr.valueOf(kagmtRptSchedule.recordDispAtr),
				oneRowOutputItems);
	
		return ScheduleTableOutputSetting.create(new OutputSettingCode(kagmtRptSchedule.pk.code), new OutputSettingName(kagmtRptSchedule.name), outputItem,
				workplaceCounterCategories, personalCounterCategories);
	}
	
	
	private List<ScheduleTableOutputSetting> toListDomainFromEntity(List<KagmtRptSchedule> kagmtRptSchedules,
			List<KagmtRptScheduleItem> kagmtRptScheduleItems, List<KagmtRptScheduleTallyByPerson> kagmtRptScheduleTallyByPersons,
			List<KagmtRptScheduleTallyByWkp> kagmtRptScheduleTallyByWkps) {
		
		List<ScheduleTableOutputSetting> results = new ArrayList<>();
		
		Map<String, List<KagmtRptScheduleItem>> listKagmtRptScheduleItem = kagmtRptScheduleItems.stream()
				.collect(Collectors.groupingBy(KagmtRptScheduleItem::getCode));	
		
		Map<String, List<KagmtRptScheduleTallyByPerson>> listKagmtRptScheduleTallyByPerson = kagmtRptScheduleTallyByPersons.stream()
				.collect(Collectors.groupingBy(KagmtRptScheduleTallyByPerson::getCode));	
		
		
		Map<String, List<KagmtRptScheduleTallyByWkp>> listKagmtRptScheduleTallyByWkp = kagmtRptScheduleTallyByWkps.stream()
				.collect(Collectors.groupingBy(KagmtRptScheduleTallyByWkp::getCode));	
		
		kagmtRptSchedules.stream().forEach(x -> {
			results.add(this.toDomainFromEntity(x, listKagmtRptScheduleItem.get(x.pk.code),
					listKagmtRptScheduleTallyByPerson.get(x.pk.code), listKagmtRptScheduleTallyByWkp.get(x.pk.code)));
		});		
		return results;
	}	

	private KagmtRptSchedule toKagmtRptScheduleEntity(ScheduleTableOutputSetting domain) {
		return new KagmtRptSchedule(new KagmtRptSchedulePk(AppContexts.user().companyId(), domain.getCode().v()),
				domain.getName().v(), domain.getOutputItem().getAdditionalColumnUseAtr().value,
				domain.getOutputItem().getShiftBackgroundColorUseAtr().value,
				domain.getOutputItem().getDailyDataDisplayAtr().value);
	}

	private List<KagmtRptScheduleItem> toKagmtRptScheduleItemEntity(ScheduleTableOutputSetting domain) {
		List<KagmtRptScheduleItem> results = new ArrayList<KagmtRptScheduleItem>();
		List<OneRowOutputItem> list = domain.getOutputItem().getDetails();
		int idx = 1;
		for(int i = 0; i < list.size(); i++) {
			results.add(new KagmtRptScheduleItem(
					new KagmtRptScheduleItemPk(AppContexts.user().companyId(), domain.getCode().v(),
							idx++),
					list.get(i).getPersonalInfo().isPresent() ?list.get(i).getPersonalInfo().get().value : null, 
					list.get(i).getAdditionalInfo().isPresent() ? list.get(i).getAdditionalInfo().get().value : null,
					list.get(i).getAttendanceItem().isPresent() ? list.get(i).getAttendanceItem().get().value : null));
		}

		return results;
	}

	private List<KagmtRptScheduleTallyByPerson> toKagmtRptScheduleTallyByPersonEntity(
			ScheduleTableOutputSetting domain) {
		List<KagmtRptScheduleTallyByPerson> results = new ArrayList<>();
		String companyId = AppContexts.user().companyId();
		List<PersonalCounterCategory> list = domain.getPersonalCounterCategories();
		if(!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				results.add(new KagmtRptScheduleTallyByPerson(
						new KagmtRptScheduleTallyByPersonPk(companyId, domain.getCode().v(), list.get(i).value),
						i
				));
			}
		}		
		return results;
	}

	private List<KagmtRptScheduleTallyByWkp> toKagmtRptScheduleTallyByWkpEntity(ScheduleTableOutputSetting domain) {
		List<KagmtRptScheduleTallyByWkp> results = new ArrayList<>();
		String companyId = AppContexts.user().companyId();
		List<WorkplaceCounterCategory> list = domain.getWorkplaceCounterCategories();
		if(!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				results.add(new KagmtRptScheduleTallyByWkp(
						new KagmtRptScheduleTallyByWkpPk(companyId, domain.getCode().v(), list.get(i).value),
						i
				));
			}
		}		
		return results;
	}

	@Override
	public void insert(String companyId, ScheduleTableOutputSetting domain) {
		this.commandProxy().insert(this.toKagmtRptScheduleEntity(domain));
		this.commandProxy().insertAll(this.toKagmtRptScheduleItemEntity(domain));
		
		if(!this.toKagmtRptScheduleTallyByPersonEntity(domain).isEmpty()) {
			this.commandProxy().insertAll(this.toKagmtRptScheduleTallyByPersonEntity(domain));
		}
		
		if(!this.toKagmtRptScheduleTallyByWkpEntity(domain).isEmpty()) {
			this.commandProxy().insertAll(this.toKagmtRptScheduleTallyByWkpEntity(domain));
		}		
	}

	@Override
	public void update(String companyId, ScheduleTableOutputSetting domain) {		
		Optional<KagmtRptSchedule> results1 = this.queryProxy().query(sql1, KagmtRptSchedule.class).setParameter("companyId", companyId)
				.setParameter("code", domain.getCode().v()).getSingle();
		if(results1.isPresent()) {
			results1.get().name =domain.getName().v();
			results1.get().additionalColumUseAtr = domain.getOutputItem().getAdditionalColumnUseAtr().value;
			results1.get().shiftBackColorUseAtr = domain.getOutputItem().getShiftBackgroundColorUseAtr().value;
			results1.get().recordDispAtr = domain.getOutputItem().getDailyDataDisplayAtr().value;
		}
	
		List<KagmtRptScheduleItem> results2 = this.queryProxy().query(sql2, KagmtRptScheduleItem.class).setParameter("companyId", companyId)
				.setParameter("code", domain.getCode().v()).getList();
		if(!results2.isEmpty()) {
			this.commandProxy().removeAll(results2);
		}
		
		List<KagmtRptScheduleTallyByPerson> results3 = this.queryProxy().query(sql3, KagmtRptScheduleTallyByPerson.class).setParameter("companyId", companyId)
				.setParameter("code", domain.getCode().v()).getList();		
		if(!results3.isEmpty()) {
			this.commandProxy().removeAll(results3);
		}
		
		List<KagmtRptScheduleTallyByWkp> results4 = this.queryProxy().query(sql4, KagmtRptScheduleTallyByWkp.class).setParameter("companyId", companyId)
				.setParameter("code", domain.getCode().v()).getList();
		if(!results4.isEmpty()) {
			this.commandProxy().removeAll(results4);
		}
		
		Optional<ScheduleTableOutputSetting> kagmtRptSchedule = this.get(companyId, domain.getCode());
		if (kagmtRptSchedule.isPresent()) {			
			this.commandProxy().updateAll(this.toKagmtRptScheduleItemEntity(domain));	
			this.commandProxy().updateAll(this.toKagmtRptScheduleTallyByPersonEntity(domain));
			this.commandProxy().updateAll(this.toKagmtRptScheduleTallyByWkpEntity(domain));
		}
		this.commandProxy().update(results1.get());
	}

	@Override
	public void delete(String companyId, OutputSettingCode code) {		
		Optional<KagmtRptSchedule> results1 = this.queryProxy().query(sql1, KagmtRptSchedule.class).setParameter("companyId", companyId)
				.setParameter("code", code).getSingle();
		
		List<KagmtRptScheduleItem> results2 = this.queryProxy().query(sql2, KagmtRptScheduleItem.class).setParameter("companyId", companyId)
				.setParameter("code", code).getList();
		
		List<KagmtRptScheduleTallyByPerson> results3 = this.queryProxy().query(sql3, KagmtRptScheduleTallyByPerson.class).setParameter("companyId", companyId)
				.setParameter("code", code).getList();
		
		List<KagmtRptScheduleTallyByWkp> results4 = this.queryProxy().query(sql4, KagmtRptScheduleTallyByWkp.class).setParameter("companyId", companyId)
				.setParameter("code", code).getList();
		
		if(results1.isPresent()) {
			this.commandProxy().remove(results1.get());
		}		
		if(!results2.isEmpty()) {
			this.commandProxy().removeAll(results2);
		}
		
		if(!results3.isEmpty()) {
			this.commandProxy().removeAll(results3);
		}
		
		if(!results4.isEmpty()) {
			this.commandProxy().removeAll(results4);
		}
	}

	@Override
	public Optional<ScheduleTableOutputSetting> get(String companyId, OutputSettingCode code) {	
		String sql = sql2 + " ORDER BY a.pk.rowNo ASC ";	
		
		Optional<KagmtRptSchedule> results1 = this.queryProxy().query(sql1, KagmtRptSchedule.class).setParameter("companyId", companyId)
				.setParameter("code", code).getSingle();
		
		List<KagmtRptScheduleItem> kagmtRptScheduleItems = this.queryProxy().query(sql, KagmtRptScheduleItem.class).setParameter("companyId", companyId)
				.setParameter("code", code).getList();
		
		List<KagmtRptScheduleTallyByPerson> kagmtRptScheduleTallyByPersons = this.queryProxy().query(sql3, KagmtRptScheduleTallyByPerson.class).setParameter("companyId", companyId)
				.setParameter("code", code).getList();
		
		List<KagmtRptScheduleTallyByWkp> kagmtRptScheduleTallyByWkps = this.queryProxy().query(sql4, KagmtRptScheduleTallyByWkp.class).setParameter("companyId", companyId)
				.setParameter("code", code).getList();
		
		if(results1.isPresent()) {
			KagmtRptSchedule kagmtRptSchedule = results1.get();
			return Optional.of(this.toDomainFromEntity(kagmtRptSchedule, kagmtRptScheduleItems, kagmtRptScheduleTallyByPersons, kagmtRptScheduleTallyByWkps));
		} else {
			return Optional.empty();
		}		
	}
	@Override
	public List<ScheduleTableOutputSetting> getList(String companyId) {
		String query1 = "SELECT a FROM KagmtRptSchedule a WHERE a.pk.companyId = :companyId";
		String query2 = "SELECT a FROM KagmtRptScheduleItem a WHERE a.pk.companyId = :companyId ORDER BY a.pk.rowNo ASC";	
		String query3 = "SELECT a FROM KagmtRptScheduleTallyByPerson a WHERE a.pk.companyId = :companyId ORDER BY a.displayOrder";
		String query4 = "SELECT a FROM KagmtRptScheduleTallyByWkp a WHERE a.pk.companyId = :companyId ORDER BY a.displayOrder";
		
		List<KagmtRptSchedule> kagmtRptSchedules = this.queryProxy().query(query1, KagmtRptSchedule.class).setParameter("companyId", companyId).getList();
		
		List<KagmtRptScheduleItem> kagmtRptScheduleItems = this.queryProxy().query(query2, KagmtRptScheduleItem.class).setParameter("companyId", companyId).getList();
		
		List<KagmtRptScheduleTallyByPerson> kagmtRptScheduleTallyByPersons = this.queryProxy().query(query3, KagmtRptScheduleTallyByPerson.class).setParameter("companyId", companyId).getList();
		
		List<KagmtRptScheduleTallyByWkp> kagmtRptScheduleTallyByWkps = this.queryProxy().query(query4, KagmtRptScheduleTallyByWkp.class).setParameter("companyId", companyId).getList();
	
		return this.toListDomainFromEntity(kagmtRptSchedules, kagmtRptScheduleItems, kagmtRptScheduleTallyByPersons, kagmtRptScheduleTallyByWkps);
	}

	@Override
	public boolean exists(String companyId, OutputSettingCode code) {
		Optional<ScheduleTableOutputSetting> domainOpt = this.get(companyId, code);
		return domainOpt.isPresent() ? true : false;
	}

}
