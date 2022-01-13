package nts.uk.screen.at.app.monthlyperformance.correction.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.task.AsyncTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.command.monthly.MonthlyRecordWorkCommand;
import nts.uk.ctx.at.record.app.find.monthly.finder.MonthlyRecordWorkFinder;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyRecordWorkDto;
import nts.uk.ctx.at.record.app.find.monthly.root.common.ClosureDateDto;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.dtos.EmpPerformMonthParamImport;
import nts.uk.ctx.at.record.dom.approvalmanagement.dailyperformance.algorithm.RegisterDayApproval;
import nts.uk.ctx.at.record.dom.monthly.MonthlyRecordTransactionService;
import nts.uk.ctx.at.record.dom.monthly.TimeOfMonthlyRepository;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.IdentityProcessUseSet;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.month.algorithm.ParamRegisterConfirmMonth;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.month.algorithm.RegisterConfirmationMonth;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.month.algorithm.SelfConfirm;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.repository.IdentityProcessUseSetRepository;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.AggregateMonthlyRecordServiceProc;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.IntegrationOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeName;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.DataDialogWithTypeProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.ParamDialog;
import nts.uk.screen.at.app.dailyperformance.correction.dto.type.TypeLink;
import nts.uk.screen.at.app.monthlyperformance.audittrail.MonthlyCorrectionLogCommand;
import nts.uk.screen.at.app.monthlyperformance.audittrail.MonthlyCorrectionLogCommandHandler;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.EditStateOfMonthlyPerformanceDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPItemCheckBox;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPItemDetail;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPItemParent;
import nts.uk.screen.at.app.monthlyperformance.correction.query.MonthlyModifyQuery;
import nts.uk.screen.at.app.monthlyperformance.correction.query.MonthlyModifyResult;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
/**
 * 
 * @author sonnh1
 *
 */
@Stateless
public class MonModifyCommandFacade {
	
	@Inject
	private MonthModifyCommandFacade monthModifyCommandFacade;

	@Inject
	private MonthlyPerformanceCorrectionUpdateCommand monthlyPerformanceCorrectionUpdateCommand;
	
	@Inject
	private RegisterConfirmationMonth registerConfirmationMonth;
	
	@Inject
	private MonthlyCorrectionLogCommandHandler handlerLog;
	
	@Inject
	private MonthlyRecordWorkFinder finder;

	@Inject
	private RegisterDayApproval registerDayApproval;
	
	@Inject
	private TimeOfMonthlyRepository timeOfMonth;

	@Inject
	private IdentityProcessUseSetRepository identityProcessUseSetRepository;
	
	@Inject
	private MonthlyRecordTransactionService monthRecordTransaction;
	
	@Inject
	private DataDialogWithTypeProcessor dataDialogWithTypeProcessor;
	
	@Inject
	private OptionalItemRepository optionalMasterRepo;
	
	private final static List<Integer> ITEM_ID_ALL = AttendanceItemIdContainer.getIds(AttendanceItemType.MONTHLY_ITEM)
			.stream().map(x -> x.getItemId()).collect(Collectors.toList());
	
	public List<OutputRegisterKMW003> insertItemDomain(MPItemParent dataParent) {
		YearMonth ym = new YearMonth(dataParent.getYearMonth());
		
//		dataParent.getMPItemDetails().stream().forEach(d -> {
//			long dbVer = timeOfMonth.getVer(d.getEmployeeId(), ym, dataParent.getClosureId(), 
//					dataParent.getClosureDate().getClosureDay(), dataParent.getClosureDate().getLastDayOfMonth());
//			if(dbVer != d.getVersion()){
//				throw new OptimisticLockException(I18NText.getText("Msg_1528"));
//			}
//		});
		getWplPosId(dataParent);
		Map<String, List<MPItemDetail>> mapItemDetail = dataParent.getMPItemDetails().stream()
				.collect(Collectors.groupingBy(x -> x.getEmployeeId()));
		List<MonthlyModifyQuery> listQuery = new ArrayList<>();
		// insert value
		mapItemDetail.entrySet().forEach(item -> {
			List<MPItemDetail> rowDatas = item.getValue();
			MonthlyModifyQuery query = new MonthlyModifyQuery(rowDatas.stream().map(x -> {
				return ItemValue.builder().itemId(x.getItemId()).layout(x.getLayoutCode()).value(x.getValue()).withPath("");
			}).collect(Collectors.toList()), dataParent.getYearMonth(), item.getKey(), dataParent.getClosureId(),
					dataParent.getClosureDate());
			
			query.setVersion(dataParent.getDataLock().stream().filter(l -> l.getEmployeeId().equals(item.getKey())).findFirst().get().getVersion());
			
			listQuery.add(query);
		});
		// lay data truoc khi update de so sanh voi data sau khi update
		List<MonthlyRecordWorkDto> oldDtos = getDtoFromQuery(listQuery); 
		
		// clone array do khi chay qua monthModifyCommandFacade.handleUpdate() thi data bi thay doi gia tri
		Map<Integer, OptionalItem> optionalMaster = optionalMasterRepo
				.findAll(AppContexts.user().companyId()).stream()
				.collect(Collectors.toMap(c -> c.getOptionalItemNo().v(), c -> c));
		List<MonthlyRecordWorkDto> oldDtosClone = new ArrayList<>();
		oldDtos.stream().forEach(x -> {
			IntegrationOfMonthly integrationOfMonthly = x.toDomain(x.getEmployeeId(), x.getYearMonth(), x.getClosureID(), x.getClosureDate());
			MonthlyRecordWorkDto dto = MonthlyRecordWorkDto.fromDtoWithOptional(integrationOfMonthly, optionalMaster);
			oldDtosClone.add(dto);
		});
		List<MonthlyModifyResult> dataOld = AttendanceItemUtil.toItemValues(oldDtos, ITEM_ID_ALL, AttendanceItemUtil.AttendanceItemType.MONTHLY_ITEM)
				.entrySet().stream().map(record -> {
					return MonthlyModifyResult.builder().items(record.getValue())
							.employeeId(record.getKey().getEmployeeId()).yearMonth(record.getKey().getYearMonth().v())
							.closureId(record.getKey().getClosureID()).closureDate(record.getKey().getClosureDate())
							.workDatePeriod(record.getKey().getAttendanceTime().getDatePeriod().toDomain())
							.version(record.getKey().getAffiliation().getVersion()).completed();
				}).collect(Collectors.toList());
		monthModifyCommandFacade.createMultiCommand(listQuery, oldDtosClone);
		
		List<MonthlyRecordWorkDto> dataAfterCal = new ArrayList<>();
		oldDtosClone.stream().forEach(x -> {
			IntegrationOfMonthly integrationOfMonthly = x.toDomain(x.getEmployeeId(), x.getYearMonth(), x.getClosureID(), x.getClosureDate());
			if(integrationOfMonthly.getAttendanceTime().isPresent()) {
				AttendanceTimeOfMonthly attendanceTimeOfMonthly = AggregateMonthlyRecordServiceProc.recalcAttendanceTime(integrationOfMonthly.getAttendanceTime().get());
				integrationOfMonthly.setAttendanceTime(Optional.of(attendanceTimeOfMonthly));
			}
			MonthlyRecordWorkDto dto = MonthlyRecordWorkDto.fromDtoWithOptional(integrationOfMonthly, optionalMaster);
			dataAfterCal.add(dto);
		});
		
		List<MonthlyModifyResult> dataNew = AttendanceItemUtil.toItemValues(dataAfterCal, ITEM_ID_ALL, AttendanceItemUtil.AttendanceItemType.MONTHLY_ITEM)
				.entrySet().stream().map(record -> {
					return MonthlyModifyResult.builder().items(record.getValue())
							.employeeId(record.getKey().getEmployeeId()).yearMonth(record.getKey().getYearMonth().v())
							.closureId(record.getKey().getClosureID()).closureDate(record.getKey().getClosureDate())
							.workDatePeriod(record.getKey().getAttendanceTime().getDatePeriod().toDomain())
							.version(record.getKey().getAffiliation().getVersion()).completed();
				}).collect(Collectors.toList());
		
		
		List<OutputRegisterKMW003> listChange = new ArrayList<>();
		dataOld.stream().forEach(x ->{
			List<Integer> listItemEditCell = dataParent.getMPItemDetails().stream().filter(i->i.getEmployeeId().equals(x.getEmployeeId())).map(c->c.getItemId()).collect(Collectors.toList());
			Optional<MonthlyModifyResult> newItem = dataNew.stream()
					.filter(c -> x.getEmployeeId().equals(c.getEmployeeId())
							&& x.getYearMonth().equals(c.getYearMonth()) && x.getClosureId() == c.getClosureId())
					.findFirst();
			if (newItem.isPresent()) {
				List<ItemValue> listItemValue =  getListCal(x.getItems(), newItem.get().getItems(), listItemEditCell);
				listChange.add(new OutputRegisterKMW003(x.getEmployeeId(), listItemValue));
			}
			
		});
		
		
		List<MonthlyRecordWorkCommand> listCommand =monthModifyCommandFacade.createMultiCommand(listQuery, dataAfterCal);
		monthModifyCommandFacade.handleUpdate(listCommand);
		
		
		
		
		// insert edit state
		dataParent.getMPItemDetails().forEach(item -> {
			ClosureDateDto closureDate = dataParent.getClosureDate();
			EditStateOfMonthlyPerformanceDto editStateOfMonthlyPerformanceDto = new EditStateOfMonthlyPerformanceDto(
					item.getEmployeeId(), new Integer(item.getItemId()),
					new DatePeriod(dataParent.getStartDate(), dataParent.getEndDate()),
					dataParent.getYearMonth().intValue(), dataParent.getClosureId(),
					new nts.uk.screen.at.app.monthlyperformance.correction.dto.ClosureDateDto(
							closureDate.getClosureDay().intValue(),
							closureDate.getLastDayOfMonth().booleanValue() ? 1 : 0),
					new Integer(0));
			this.monthlyPerformanceCorrectionUpdateCommand.handleAddOrUpdate(editStateOfMonthlyPerformanceDto);
		});
		
		// dang ki xac nhan ban than
		this.insertSign(dataParent, ym);
		
		approval(dataParent, ym);
		
		dataParent.getDataLock().stream().forEach(lock -> {
			if(!listQuery.stream().filter(c -> lock.getEmployeeId().equals(c.getEmployeeId())).findFirst().isPresent()) {
				monthRecordTransaction.updated(lock.getEmployeeId(), ym, dataParent.getClosureId(), 
						dataParent.getClosureDate().getClosureDay(), dataParent.getClosureDate().getLastDayOfMonth(), lock.getVersion());
			}
		});
		
		// add correction log
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		AsyncTask task = AsyncTask.builder().withContexts().keepsTrack(false).threadName(this.getClass().getName())
				.build(() -> {
					List<MonthlyRecordWorkDto> newDtos = createDtoNews(listQuery, dataAfterCal);
					//List<MonthlyRecordWorkDto> newDtos = getDtoFromQuery(listQuery); 
					// lay lai data sau khi update de so sanh voi data truoc khi update
					handlerLog.handle(new MonthlyCorrectionLogCommand(oldDtos, newDtos, listQuery, dataParent.getEndDate()));
				});
		executorService.submit(task);
		return listChange;
	}
	
	private List<ItemValue> getListCal(List<ItemValue> itemOlds,List<ItemValue> itemsNew,List<Integer> itemsEditCell) {
		List<ItemValue> result = new ArrayList<>();
		
		val mapItemNew = itemsNew.stream().collect(Collectors.toMap(x -> x.getItemId(), x -> x));
		itemOlds.stream().forEach(x -> {
			val itemNew = mapItemNew.get(x.getItemId());
			// TimeWithDay
			if(x.getValueType().isInteger() || x.getValueType().isDouble()) {
				if ((itemNew.getValue() == null && x.getValue() != null && Double.parseDouble(x.getValue()) == 0)
						   || (x.getValue() == null && itemNew.getValue() != null && Double.parseDouble(itemNew.getValue()) == 0) 
						   || (itemNew.getValue() != null && x.getValue() != null && Double.parseDouble(x.getValue()) == 0 && Double.parseDouble(itemNew.getValue()) == 0)) {
					itemNew.value(0);
					x.value(0);
				}
			}
            if(x.getValue() != null && itemNew.getValue() != null) {
            	if(!x.getValue().equals(itemNew.getValue())) result.add(itemNew);
            }else if (!x.equals(itemNew)) {
				result.add(itemNew);
			}
		});
		return result.stream().filter(c-> !itemsEditCell.contains(c.getItemId())).collect(Collectors.toList());
	}

	private void approval(MPItemParent dataParent, YearMonth ym) {
		
		List<EmpPerformMonthParamImport> listRegister = new ArrayList<>();
		List<EmpPerformMonthParamImport> listRemove = new ArrayList<>();
		for(MPItemCheckBox mpi :dataParent.getDataCheckApproval()) { //loc trang thai approve de dang ki
			EmpPerformMonthParamImport p = new EmpPerformMonthParamImport(ym, dataParent.getClosureId(),
					dataParent.getClosureDate().toDomain(), dataParent.getEndDate(), mpi.getEmployeeId());
			if(mpi.isValue()) {
				listRegister.add(p);
			}else {
				listRemove.add(p);
			}
			timeOfMonth.verShouldUp(mpi.getEmployeeId(), ym, dataParent.getClosureId(), 
									dataParent.getClosureDate().getClosureDay(),
									dataParent.getClosureDate().getLastDayOfMonth());
		}
		// remove approval	- no. 529	
		this.removeMonApproval(listRemove);
		// insert approval - no. 528
		this.insertApproval(listRegister);
	}
	
	private void insertSign(MPItemParent mPItemParent, YearMonth ym) {
		List<MPItemCheckBox> dataCheckSign = mPItemParent.getDataCheckSign();
		if(dataCheckSign.isEmpty()) return;
		IdentityProcessUseSet identity = identityProcessUseSetRepository.findByKey(AppContexts.user().companyId()).orElse(null);
		if(!identity.isUseIdentityOfMonth()){
			return;
		}
		List<SelfConfirm> selfConfirm = new ArrayList<>();
		ClosureDateDto closureDate = mPItemParent.getClosureDate();
		dataCheckSign.stream().forEach(x -> {
			selfConfirm.add(new SelfConfirm(x.getEmployeeId(), x.isValue()));
		});
		ParamRegisterConfirmMonth param = new ParamRegisterConfirmMonth(ym, selfConfirm,
				mPItemParent.getClosureId(), new ClosureDate(closureDate.getClosureDay(),
						closureDate.getLastDayOfMonth()), GeneralDate.today());
		
		registerConfirmationMonth.registerConfirmationMonth(param);
		dataCheckSign.stream().forEach(x -> {
			timeOfMonth.verShouldUp(x.getEmployeeId(), param.getYearMonth(), param.getClosureId(), 
					param.getClosureDate().getClosureDay().v(),
					param.getClosureDate().getLastDayOfMonth());
		});
		
	}

//	private void insertApproval(List<MPItemCheckBox> dataCheckApproval) {
//		if(dataCheckApproval.isEmpty()) return;
//		ParamDayApproval param = new ParamDayApproval(AppContexts.user().employeeId(),
//				dataCheckApproval.stream()
//						.map(x -> new ContentApproval(x.getDate(), x.isValue(), x.getEmployeeId(), x.isFlagRemoveAll()))
//						.collect(Collectors.toList()));
//		registerDayApproval.registerDayApproval(param);
//	}
	
	private List<MonthlyRecordWorkDto> getDtoFromQuery(List<MonthlyModifyQuery> query) {
		Set<String> emps = new HashSet<>();
		Set<YearMonth> yearmonth = new HashSet<>();
		query.stream().forEach(q -> {
			emps.add(q.getEmployeeId());
			yearmonth.add(new YearMonth(q.getYearMonth()));
		});
		List<MonthlyRecordWorkDto> values = finder.find(emps, yearmonth);
		List<MonthlyRecordWorkDto> listDtos = new ArrayList<>();
		values.forEach(v -> {
			MonthlyModifyQuery q = query.stream().filter(qr -> {
				return qr.getClosureId() == v.getClosureID() && qr.getEmployeeId().equals(v.getEmployeeId())
						&& v.yearMonth().compareTo(qr.getYearMonth()) == 0 && v.getClosureDate().equals(qr.getClosureDate());
			}).findFirst().orElse(null);
			if (q != null) {
				listDtos.add(v);
			}
		});
		return listDtos;
	}
	
	private void insertApproval(List<EmpPerformMonthParamImport> dataCheckApprovals) {
		if(dataCheckApprovals.isEmpty()) return;
		registerDayApproval.registerMonApproval(AppContexts.user().employeeId(), dataCheckApprovals);
	}

	public void removeMonApproval(List<EmpPerformMonthParamImport> dataCheckApprovals) {
		if(dataCheckApprovals.isEmpty()) return;
		registerDayApproval.removeMonApproval(AppContexts.user().employeeId(), dataCheckApprovals);
	}
	
	private List<MonthlyRecordWorkDto> createDtoNews(List<MonthlyModifyQuery> query,List<MonthlyRecordWorkDto> values) {
		Set<String> emps = new HashSet<>();
		Set<YearMonth> yearmonth = new HashSet<>();
		query.stream().forEach(q -> {
			emps.add(q.getEmployeeId());
			yearmonth.add(new YearMonth(q.getYearMonth()));
		});
		//List<MonthlyRecordWorkDto> oldValues = finder.find(emps, yearmonth);
		return values.stream().map(v -> {
			MonthlyModifyQuery q = query.stream().filter(qr -> {
				return qr.getClosureId() == v.getClosureID() && qr.getEmployeeId().equals(v.getEmployeeId())
						&& v.yearMonth().compareTo(qr.getYearMonth()) == 0 && v.getClosureDate().equals(qr.getClosureDate());
			}).findFirst().orElse(null);
			if(q == null){
				return null;
			}
			IntegrationOfMonthly domain = v.toDomain(v.employeeId(), v.yearMonth(), v.getClosureID(), v.getClosureDate());
			MonthlyRecordWorkDto dtoNew = MonthlyRecordWorkDto.fromOnlyAttTime(domain);
			MonthlyRecordWorkDto dto = AttendanceItemUtil.fromItemValues(dtoNew, q.getItems(), AttendanceItemType.MONTHLY_ITEM);
			return dto;
		}).filter(v -> v != null).collect(Collectors.toList());
	}
	
	private void getWplPosId(MPItemParent mPItemParent) {
		// map id -> code possition and workplace
		mPItemParent.getMPItemDetails().stream().map(itemEdit -> {
			if (itemEdit.getItemId() == 193) {
				CodeName codeName = dataDialogWithTypeProcessor.getTypeDialog(TypeLink.POSSITION.value,
						new ParamDialog(mPItemParent.getStartDate(), itemEdit.getValue()));
				itemEdit.setValue(codeName == null ? null : codeName.getId());
				return itemEdit;
			} else if (itemEdit.getItemId() == 198) {
				CodeName codeName = dataDialogWithTypeProcessor.getTypeDialog(TypeLink.POSSITION.value,
						new ParamDialog(mPItemParent.getEndDate(), itemEdit.getValue()));
				itemEdit.setValue(codeName == null ? null : codeName.getId());
				return itemEdit;
			} else if (itemEdit.getItemId() == 194) {
				CodeName codeName = dataDialogWithTypeProcessor.getTypeDialog(TypeLink.WORKPLACE.value,
						new ParamDialog(mPItemParent.getStartDate(), itemEdit.getValue()));
				itemEdit.setValue(codeName == null ? null : codeName.getId());
				return itemEdit;
			} else if (itemEdit.getItemId() == 199) {
				CodeName codeName = dataDialogWithTypeProcessor.getTypeDialog(TypeLink.WORKPLACE.value,
						new ParamDialog(mPItemParent.getEndDate(), itemEdit.getValue()));
				itemEdit.setValue(codeName == null ? null : codeName.getId());
				return itemEdit;
			}
			return itemEdit;
		}).collect(Collectors.toList());
	}
	
}
