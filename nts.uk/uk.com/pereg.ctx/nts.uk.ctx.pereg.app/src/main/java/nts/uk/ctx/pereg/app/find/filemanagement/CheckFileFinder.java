package nts.uk.ctx.pereg.app.find.filemanagement;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.infra.file.storage.StoredFileStreamService;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.gul.excel.ExcelFileTypeException;
import nts.gul.excel.NtsExcelCell;
import nts.gul.excel.NtsExcelHeader;
import nts.gul.excel.NtsExcelImport;
import nts.gul.excel.NtsExcelReader;
import nts.gul.excel.NtsExcelRow;
import nts.gul.text.StringUtil;
import nts.gul.time.minutesbased.MinutesBasedTimeParser;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHist;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistByEmployee;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistItem;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistRepository;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.pereg.app.find.common.ComboBoxRetrieveFactory;
import nts.uk.ctx.pereg.app.find.layout.dto.EmpMainCategoryDto;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.ActionRole;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.GridEmpBody;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.GridEmpHead;
import nts.uk.ctx.pereg.app.find.person.info.item.DataTypeStateDto;
import nts.uk.ctx.pereg.app.find.person.info.item.DateItemDto;
import nts.uk.ctx.pereg.app.find.person.info.item.ItemTypeStateDto;
import nts.uk.ctx.pereg.app.find.person.info.item.NumericButtonDto;
import nts.uk.ctx.pereg.app.find.person.info.item.NumericItemDto;
import nts.uk.ctx.pereg.app.find.person.info.item.PerInfoItemDefDto;
import nts.uk.ctx.pereg.app.find.person.info.item.PerInfoItemDefForLayoutDto;
import nts.uk.ctx.pereg.app.find.person.info.item.SelectionItemDto;
import nts.uk.ctx.pereg.app.find.person.info.item.SingleItemDto;
import nts.uk.ctx.pereg.app.find.person.info.item.StringItemDto;
import nts.uk.ctx.pereg.app.find.person.info.item.TimeItemDto;
import nts.uk.ctx.pereg.app.find.person.info.item.TimePointItemDto;
import nts.uk.ctx.pereg.app.find.processor.GridPeregProcessor;
import nts.uk.ctx.pereg.app.find.processor.PeregProcessor;
import nts.uk.ctx.pereg.dom.person.info.category.PerInfoCtgByCompanyRepositoty;
import nts.uk.ctx.pereg.dom.person.info.category.PersonInfoCategory;
import nts.uk.ctx.pereg.dom.person.info.item.ItemType;
import nts.uk.ctx.pereg.dom.person.info.singleitem.DataTypeValue;
import nts.uk.ctx.pereg.dom.roles.auth.item.PersonInfoItemAuth;
import nts.uk.ctx.pereg.dom.roles.auth.item.PersonInfoItemAuthRepository;
import nts.uk.ctx.sys.auth.pub.employee.EmployeePublisher;
import nts.uk.ctx.sys.auth.pub.employee.NarrowEmpByReferenceRange;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.security.audittrail.correction.content.DataValueAttribute;
import nts.uk.shr.com.validate.constraint.implement.DateConstraint;
import nts.uk.shr.com.validate.constraint.implement.DateType;
import nts.uk.shr.com.validate.constraint.implement.NumericConstraint;
import nts.uk.shr.com.validate.constraint.implement.StringCharType;
import nts.uk.shr.com.validate.constraint.implement.StringConstraint;
import nts.uk.shr.com.validate.constraint.implement.TimeConstraint;
import nts.uk.shr.com.validate.constraint.implement.TimePointConstraint;
import nts.uk.shr.pereg.app.ComboBoxObject;
import nts.uk.shr.pereg.app.ItemValueType;
import nts.uk.shr.pereg.app.find.PeregEmpInfoQuery;
import nts.uk.shr.pereg.app.find.PeregQueryByListEmp;

@Stateless
public class CheckFileFinder {
	@Inject
	private EmployeeDataMngInfoRepository employeeRepo;
	
	@Inject
	private EmployeePublisher  employeePub;
	
	@Inject
	private StoredFileStreamService fileStreamService;
	
	@Inject
	private GridPeregProcessor gridProcesor;
	
	@Inject 
	private PerInfoCtgByCompanyRepositoty ctgRepo;
	
	@Inject
	private ComboBoxRetrieveFactory comboBoxRetrieveFactory;
	
	@Inject
	private PeregProcessor layoutProcessor;
	
	@Inject
	private AffCompanyHistRepository companyHistRepo;
	
	@Inject
	private PersonInfoItemAuthRepository itemAuthRepo;
	
	private static String header;
	
	private static int index;
	
	private final static List<String> itemSpecialLst = Arrays.asList("IS00003", "IS00004","IS00015","IS00016");
	
	private final static List<String> endDateLst = Arrays.asList("IS00021","IS00027","IS00067","IS00078","IS00083","IS00120","IS00782","IS00256", "IS00789", "IS00842", "IS01017");
	
	//CS00036 special  (#119681)
	private String currentIS00375 = "1";
	private String currentIS00380 = "1";
	
	@SuppressWarnings("unused")
	private static GeneralDate valueStartCode;
	
	private static final String JP_SPACE = "　";
	
	public GridDto processingFile(CheckFileParams params) throws Exception {
		try {
			return processFile(params);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public GridDto processFile(CheckFileParams params) throws Exception {
		try {
			String cid = AppContexts.user().companyId();
			String roleId = AppContexts.user().roles().forPersonalInfo();
			if(roleId == null || roleId =="") return null;
			String contractCd = AppContexts.user().contractCode();
			UpdateMode updateMode = EnumAdaptor.valueOf(params.getModeUpdate(), UpdateMode.class);
			// read file import
			InputStream inputStream = this.fileStreamService.takeOutFromFileId(params.getFileId());
			Optional<PersonInfoCategory> ctgOptional =  this.ctgRepo.getDetailCategoryInfo(cid, params.getCategoryId(), contractCd);
			if(!ctgOptional.isPresent()) throw new BusinessException("category invalid");
			List<String> itemIds = params.getColumnChange().stream()
					.filter(c -> c.getItemTypeState().getItemType() == 2).map(c -> c.getItemId())
					.collect(Collectors.toList());
			List<PersonInfoItemAuth> itemAuths = this.itemAuthRepo.getAllItemAuth(roleId, params.getCategoryId(), itemIds);
			if(CollectionUtil.isEmpty(itemAuths)) { throw new BusinessException("Msg_723");}
			
			// data file
			NtsExcelImport excelReader = NtsExcelReader.read(inputStream);
			// header
			List<NtsExcelHeader> header = excelReader.headers(); 
			List<NtsExcelRow> rows = excelReader.rows();
			List<GridEmpHead> headerDb = this.getHeaderData(ctgOptional.get(), params.getColumnChange());
			Map<String, Periods> peroidByCategory = createPeriod();
			// columns
			List<String> colums = this.getColumsChange(header, headerDb);
			List<EmployeeDataMngInfo> employees = this.getEmployeeIds(rows, params.getSids());
			Periods period = peroidByCategory.get(ctgOptional.get().getCategoryCode().toString());
			//GridDto dto = this.getGridInfo(excelReader, headerDb, ctgOptional.get(), employees, period == null? null: period.periods.get(0).getItem(), updateMode); 
			GridDto dto = this.getGridInfo(excelReader, headerDb, ctgOptional.get(), employees, period, updateMode, itemAuths, params.getBaseDate()); 
			//受入するファイルの列に、メイン画面の「個人情報一覧（A3_001）」に表示している可変列で更新可能な項目が１件でも存在するかチェックする
			//check xem các header của item trong file import có khớp với màn hình A 
			if (colums.size() == 0) {
				throw new BusinessException("Msg_723");
			}
			return dto;
		} catch (ExcelFileTypeException e1) {
			throw e1;
		}
	}
	
	// get ColumnsFixed
	private List<String> getColumsChange(List<NtsExcelHeader> header, List<GridEmpHead> headerReal) throws Exception{
		List<String> colChange = new ArrayList<>();
		header.stream().forEach(c ->{
			NtsExcelCell mainCells = c.getMain();
			if(!mainCells.getValue().getText().equals(TextResource.localize("CPS003_28"))  && !mainCells.getValue().getText().equals(TextResource.localize("CPS003_29") ) && !mainCells.getValue().getText().contains("（コード）")) {
				Optional<GridEmpHead> gridHead = headerReal.stream().filter(head -> head.getItemName().equals( mainCells.getValue().getText())).findFirst();
				if(gridHead.isPresent()) {
					colChange.add( mainCells.getValue().getText());
				}
			}
		});
		return colChange;
	}
	
	//get EmployeeIds
	private List<EmployeeDataMngInfo> getEmployeeIds(List<NtsExcelRow> rows, List<String> sids){
		List<String> employeeIds = new ArrayList<>();
		List<String> employeeCodes = new ArrayList<>();
		String companyId = AppContexts.user().companyId();
		List<EmployeeDataMngInfo> employeeMange = new ArrayList<>();
		List<EmployeeDataMngInfo> result = new ArrayList<>();
		//アルゴリズム「受入社員情報取得処理」を実行する
		rows.stream().forEach(c ->{
			List<NtsExcelCell> cells = c.cells();
			NtsExcelHeader header = cells.get(0).getHeader();
			String nameCol = header.getMain().getValue().getText();
			//Excelファイルから、「社員コード」列を取得する
			if(nameCol.equals(TextResource.localize("CPS003_28"))) {
				String employeeCode = cells.get(0).getValue().getText();
				employeeCodes.add(employeeCode);
			}
		});
		
		if(!employeeCodes.isEmpty()) {
			//「社員コード」列から１行ずつ取得して、ドメインモデル「社員データ管理情報」を取得する 
			employeeMange.addAll(this.employeeRepo.findByListEmployeeCode(companyId, employeeCodes));
			employeeIds.addAll(employeeMange.stream().map(c -> c.getEmployeeId()).collect(Collectors.toList()));
		}
		
		if (!employeeIds.isEmpty()) {
			// 社員リストを参照範囲で絞り込む - RequestList539 (RequestList338)
			Optional<NarrowEmpByReferenceRange> narrow = this.employeePub.findByEmpId(employeeIds, 8, GeneralDate.today());
			// 受入する社員が存在する（ログイン者が操作できる社員として存在する）かチェックする
			if (narrow.isPresent()) {
				if (CollectionUtil.isEmpty(narrow.get().getEmployeeID())) {
					throw new BusinessException("Msg_724");
				} else {
					narrow.get().getEmployeeID().stream().forEach(c -> {
						Optional<EmployeeDataMngInfo> employeeOpt = employeeMange.stream()
								.filter(emp -> emp.getEmployeeId().equals(c.toString())).findFirst();
						if (employeeOpt.isPresent()) {
							result.add(employeeOpt.get());
						}
					});
				}

			} else {
				throw new BusinessException("Msg_724");
			}
		}

		return result;
	}
	
	/**
	 * 	起動時処理
	 * getGridLayout
	 * @return
	 */
	public List<GridEmpHead> getHeaderData(PersonInfoCategory category, List<GridEmpHead> columItemChangeExel) {
		LoginUserContext loginUser = AppContexts.user();
		String contractCd = loginUser.contractCode();
		String roleId = loginUser.roles().forPersonalInfo();
		List<GridEmpHead> headerReal = new ArrayList<>();
		 List<PerInfoItemDefForLayoutDto> i = this.gridProcesor.getPerItemDefForLayout(category, contractCd, roleId);
		 List<GridEmpHead> headers = i.stream().map(m -> new GridEmpHead(m.getId(), m.getDispOrder(), m.getItemCode(), m.getItemParentCode(),
					m.getItemName(), m.getItemTypeState(), m.getIsRequired() == 1, m.getResourceId(),
					m.getLstChildItemDef().stream()
					.sorted(Comparator.comparing(PerInfoItemDefDto::getItemCode, Comparator.naturalOrder()))
							.map(c -> new GridEmpHead(c.getId(), m.getDispOrder(), c.getItemCode(),
									c.getItemParentCode(), c.getItemName(), c.getItemTypeState(),
									c.getIsRequired() == 1, c.getResourceId(), null))
							.collect(Collectors.toList())))
			.sorted(Comparator.comparing(GridEmpHead::getItemOrder, Comparator.naturalOrder()).thenComparing(GridEmpHead::getItemCode, Comparator.naturalOrder()))
			.collect(Collectors.toList());
		 headers.addAll(headers.stream().flatMap(m -> m.getChilds().stream()).collect(Collectors.toList()));
		 columItemChangeExel.stream().forEach(c ->{
			headers.stream().forEach(item ->{
				if((c.getItemId().equals(item.getItemId()))) {
					headerReal.add(item);
					
				}
			});
		});
		return headerReal;
	}
	
	/**
	 * đoc file và lấy ra những item bị ẩn
	 * @param excelReader
	 * @param headerReal
	 * @param category
	 * @param employees
	 * @return
	 */
	private GridDto getGridInfo(NtsExcelImport excelReader, List<GridEmpHead> headerReal, PersonInfoCategory category,
			List<EmployeeDataMngInfo> employees, Periods period, UpdateMode updateMode,
			List<PersonInfoItemAuth> itemAuths, GeneralDate baseDate) {
		String sid = AppContexts.user().employeeId();
		List<ItemRowDto> itemSelfDtos =  new ArrayList<>();
		List<ItemRowDto> itemOtherDtos =  new ArrayList<>();
		boolean isOther = false;
		boolean isSelf = false;
		/* lien quan den bodyData*/
		List<GridEmpHead> headerRemain = new ArrayList<>();
		List<GridEmpHead> header =  new ArrayList<GridEmpHead>();
		header.addAll(headerReal);
		HashMap<String, Object> contraintList = generateContraint(header);
		List<EmployeeRowDto> employeeDtos = new ArrayList<>();
		List<ItemError> itemErrors = new ArrayList<>();
		List<NtsExcelRow> rows = excelReader.rows();
		// đọc dữ liệu từ file import
		//readEmployeeFromFile(category,rows, employees, headerReal, contraintList, headerRemain,  employeeDtos, startCode, itemErrors);
		readEmployeeFromFile(category,rows, employees, headerReal, contraintList, headerRemain,  employeeDtos, period, itemErrors);
		
		List<EmployeeRowDto> result = new ArrayList<>();
		// lấy ra những item bị thiếu không file import ko có trong setting ở màn hình A, set RecordId, set actionRole
		headerReal.removeAll(headerRemain);
		index = 0;
		PeregQueryByListEmp lquery = PeregQueryByListEmp.createQueryLayout(category.getPersonInfoCategoryId(),
				category.getCategoryCode().v(), baseDate,
				employees.stream().map(m -> new PeregEmpInfoQuery(m.getPersonId(), m.getEmployeeId())).collect(Collectors.toList()));
		
		List<EmpMainCategoryDto> layouts = layoutProcessor.getCategoryDetailByListEmp(lquery, true);
		
		Iterator<EmployeeRowDto> itr = employeeDtos.iterator();
		while (itr.hasNext()) {
			EmployeeRowDto pdt = itr.next();
			boolean isSelfAuth = sid.equals(pdt.getEmployeeId());
			List<ItemError> errors = itemErrors.stream().filter(error -> error.getIndex() == index).collect(Collectors.toList());
			// lấy full value của các item
			List<ItemRowDto> itemDtos = new ArrayList<>();
			List<EmpMainCategoryDto> empDtos = layouts.stream().filter(l -> l.getEmployeeId().equals(pdt.getEmployeeId())).collect(Collectors.toList());
			empDtos.stream().forEach(emp  ->{
				List<GridEmpBody> items = emp.getItems();
				// thực hiện lấy những item được hiển thị trên màn hình A mà  ko có trong import 
				//・上書き保存モードの場合、・t/h mode overwrite_Save、				
				//受入した項目のみ更新して、受入してない項目は変更しない update chỉ cac item đa import, cac item chưa import thi ko thay đổi.
				//・新規履歴追加モードの場合、・t/h mode  them lịch sử mới、
				//基準日時点の履歴の情報をベースにして、受入した項目のみ変更、lấy thong tin lịch sử tại thời điểm baseDate lam chuẩn, chỉ thay đổi item đa import、
				//受入していない項目は基準日時点の情報で追加する cac item chưa import thi them thong tin tại thời điểm baseDate
				//※更新権限がない項目は受入（値の変更）をしない。※item ko co quyền update thi ko import（thay đổi value）
				headerReal.stream().forEach(h ->{
					items.stream().forEach(item ->{
						if(h.getItemCode().equals(item.getItemCode())) {
							// trường hợp ghi đè, item ko có trong file import thì sẽ chỉ được view thôi, ko được update giá trị
							int dataType = 0;
							if(h.getItemTypeState().getItemType() == 2) {
								SingleItemDto singleDto = (SingleItemDto) h.getItemTypeState();
								dataType = singleDto.getDataTypeState().getDataTypeValue();
							}
							ItemRowDto dto = new ItemRowDto(h.getItemCode(), h.getItemName(), dataType ,item.getValue(),
									item.getTextValue(), item.getRecordId(),h.getItemOrder(), updateMode == UpdateMode.OVERIDED? ActionRole.VIEW_ONLY: item.getActionRole(),item.getLstComboBoxValue(), false,"","", false);
							itemDtos.add(dto);
						}
					});
				});
				
				if(itemDtos.size() > 0) {
					pdt.getItems().addAll(itemDtos);
				}

				//lấy thông tin recordId và actionRole
				if(items.isEmpty()) {
					pdt.getItems().removeAll(pdt.getItems());
					itr.remove();
				}

				items.stream().forEach(item -> {
					
					pdt.getItems().stream().forEach(itemDto -> {
						
						if (itemDto.getItemCode().equals(item.getItemCode())) {
							
							errors.stream().forEach(error ->{
								
								if(error.getColumnKey().equals(itemDto.getItemCode())) {
									error.setRecordId(item.getRecordId());
								}
								
							});

							
							itemDto.setRecordId(item.getRecordId());
							
							// trường hợp ghi đè, item ko có trong file import thì sẽ chỉ được view thôi, ko được update giá trị
							if(itemDto.getActionRole() == null) {
								itemDto.setActionRole(item.getActionRole() == null? ActionRole.VIEW_ONLY: item.getActionRole());
							}
							
							// trường hợp tạo mới lịch sử, item có trong file import nhưng không có quyền update thì sẽ lấy giá trị từ database
							if(endDateLst.contains(itemDto.getItemCode())&& (item.getActionRole() == ActionRole.VIEW_ONLY || item.getActionRole() == ActionRole.HIDDEN)) {
								itemDto.setValue(item.getValue());
							}
							
							if(category.isHistoryCategory()) {
									if (itemDto.getItemCode().equals(period.getPeriods().get(0).getItem())) {
										period.getPeriods().get(0).setValue(itemDto.getValue());
									}
									
									if (itemDto.getItemCode().equals(period.getPeriods().get(1).getItem())) {
										period.getPeriods().get(1).setValue(itemDto.getValue());
									}
							}
							
							if(itemDto.getDataType() == 0) return; 
							Object valueDb = item.getValue() == null? null: this.convertValue(itemDto.getDataType(), item.getValue().toString());
							Object valueExcel = itemDto.getValue() == null? null: this.convertValue(itemDto.getDataType(), itemDto.getValue().toString());
							
							if(isEqual(valueExcel, valueDb, itemDto.getDataType()) == true) {
								itemDto.setUpdate(false);
								itemDto.setDefValue(itemDto.getValue());
								DataValueAttribute attr = converType(itemDto.getDataType());
								if (itemDto.getDataType() == 6 || itemDto.getDataType() == 7
										|| itemDto.getDataType() == 8) {
									Optional<ComboBoxObject> textOpt = item.getLstComboBoxValue().stream().filter(combo -> combo.getOptionValue().equals(item.getValue() == null? "":itemDto.getValue().toString())).findFirst();
									if(textOpt.isPresent()) {
										itemDto.setTextValue(textOpt.get().getOptionText());
										itemDto.setDefText(textOpt.get().getOptionText());
									}
								} else {
									itemDto.setTextValue(attr.format(valueDb));
									itemDto.setDefText(attr.format(valueDb));
								}
								
							}else {
								itemDto.setUpdate(true);
								itemDto.setDefValue(item.getValue());
								DataValueAttribute attr = converType(itemDto.getDataType());
								if (itemDto.getDataType() == 6 || itemDto.getDataType() == 7
										|| itemDto.getDataType() == 8) {
									Optional<ComboBoxObject> textOpt = item.getLstComboBoxValue().stream().filter(combo -> combo.getOptionValue().equals(itemDto.getValue() == null? "":itemDto.getValue().toString())).findFirst();
									Optional<ComboBoxObject> defTextOpt = item.getLstComboBoxValue().stream().filter(combo -> combo.getOptionValue().equals(item.getValue() == null?"": item.getValue().toString())).findFirst();
									if(textOpt.isPresent()) {
										itemDto.setTextValue(textOpt.get().getOptionText());
									}
									
									if(defTextOpt.isPresent()) {
										itemDto.setDefText(defTextOpt.get().getOptionText());
									}
								} else {
									try {
										itemDto.setDefText(attr.format(valueDb));
										itemDto.setTextValue(attr.format(valueExcel));
									} catch (Exception e) {
										itemDto.setTextValue(valueExcel.toString());
									}
								}
							}
						}
					});
				});		
				
			});
			
			if(category.isHistoryCategory()) {	
				pdt.getItems().stream().forEach(itemDto ->{
					if(itemDto.getItemCode().equals(period.getPeriods().get(0).getItem())) {
						if(period.getPeriods().get(0).getValue() != null && period.getPeriods().get(1).getValue() != null) {
							try {
								GeneralDate start = GeneralDate.fromString(period.getPeriods().get(0).getValue().toString(), "yyyy/MM/dd"); 
								GeneralDate end = GeneralDate.fromString(period.getPeriods().get(1).getValue().toString(), "yyyy/MM/dd"); 
								if(start.after(end)) {
									itemDto.setError(true);
									ItemError error = new ItemError(pdt.getEmployeeId(), itemDto.getRecordId(), index, period.getPeriods().get(0).getItem(), "Msg_861"); 
									itemErrors.add(error);
								}
							}catch(Exception e) {
								System.out.println("invalid format date");
							}
						}
					}
				});
			}
			
			// đếm số item bị lỗi của một employee
			List<ItemRowDto>  itemErrorLst = pdt.getItems().stream().filter(x -> x.isError() == true).collect(Collectors.toList());
			pdt.setNumberOfError(itemErrorLst.size());
			
			pdt.getItems().sort(Comparator.comparing(ItemRowDto::getItemOrder, Comparator.naturalOrder()));
			if (isSelfAuth == true) {
				List<ItemRowDto> itemSelfDtoTmps = pdt.getItems().stream()
						.filter(c -> c.getActionRole() == ActionRole.EDIT).collect(Collectors.toList());
				itemSelfDtos.addAll(itemSelfDtoTmps);
				isSelf = true;
			} else {
				if (itemOtherDtos.isEmpty()) {
					List<ItemRowDto> itemOtherDtoTmps = pdt.getItems().stream()
							.filter(c -> c.getActionRole() == ActionRole.EDIT).collect(Collectors.toList());
					itemOtherDtos.addAll(itemOtherDtoTmps);
					isOther = true;
				}
			}
			result.add(pdt);
			index++;	
		}
		
		if ((itemSelfDtos.isEmpty() && itemOtherDtos.isEmpty()) && ((isOther == false && isSelf == true)
				|| (isOther == true && isSelf == false) || (isOther == true && isSelf == true))) {
			throw new BusinessException("Msg_724");
		}
		
		// sắp xếp lại vị trí item theo số tự lỗi - エクセル受入データを並び替える - エラーの件数　DESC、社員コード　ASC
		Comparator<EmployeeRowDto> compareByName = Comparator
                .comparing(EmployeeRowDto::getNumberOfError, (s1, s2) -> {
                	return s2.compareTo(s1);
                }).thenComparing(EmployeeRowDto::getEmployeeCode);
		result.sort(compareByName);
		//set lại vị trí index sau khi sort
		int index = 0;
		
		for(EmployeeRowDto pdt: result) {
			if(!CollectionUtil.isEmpty(pdt.getItems())) {
				for(ItemError error : itemErrors) {
					if((error.getRecordId() == pdt.getItems().get(0).getRecordId() && error.getSid().equals(pdt.getEmployeeId()))) {
						error.setIndex(index);
					}else if(error.getSid().equals(pdt.getEmployeeId())){
						error.setIndex(index);
					}
				}
			
			}
			index++;
		}
		
		return new GridDto(updateMode.value, header, result, itemErrors);
	}
	
	/**
	 * đọc dữ liệu từ file import
	 * @param category
	 * @param rows
	 * @param employees
	 * @param headerReal
	 * @param contraintList
	 * @param headerRemain
	 * @param employeeDtos
	 */
	private void readEmployeeFromFile(PersonInfoCategory category, List<NtsExcelRow> rows,
			List<EmployeeDataMngInfo> employees, List<GridEmpHead> headerReal, HashMap<String, Object> contraintList,
			List<GridEmpHead> headerRemain, List<EmployeeRowDto> employeeDtos, Periods period, List<ItemError> itemErrors) {
		int index = 0;
		String sid = AppContexts.user().employeeId();
		// get item for self and other (self map key is true)
		HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems = this.layoutProcessor.getPerItemDefForLayout(
				category, AppContexts.user().contractCode(), true, AppContexts.user().roles().forPersonalInfo());
		for(NtsExcelRow row: rows) {
			List<NtsExcelCell> cells = row.cells();		
			EmployeeRowDto employeeDto = new EmployeeRowDto();
			List<ItemRowDto> items = new ArrayList<>();
			for(NtsExcelCell cell: cells) {
				String[] itemChilds = cell.getHeader().getMain().getValue().getText().split("＿");
				String header = cell.getHeader().getMain().getValue().getText();
				String headerTemp = itemChilds.length > 0? itemChilds[itemChilds.length - 1]: header;
				setValueItemDto(category, employees, cell, employeeDto, header, headerTemp,  headerReal, contraintList, items,  headerRemain, period, itemErrors, index, perItems, sid);
			}
			
			employeeDto.setItems(items);
			if(employeeDto.getEmployeeCode()!= null && employeeDto.getEmployeeId() != null) {
				employeeDtos.add(employeeDto);
			}
			index++;
		}

	}
	
	private void setValueItemDto(PersonInfoCategory category, List<EmployeeDataMngInfo> employees, NtsExcelCell cell,

			EmployeeRowDto employeeDto, String header, String headerTemp, List<GridEmpHead> headerReal,

			HashMap<String, Object> contraintList, List<ItemRowDto> items, List<GridEmpHead> headerRemain,

			Periods period, List<ItemError> itemErrors, int index,

			HashMap<Boolean, List<PerInfoItemDefForLayoutDto>> perItems,

			String sid) {

		// lấy emloyeeCode, employeeName
		if (header.equals(TextResource.localize("CPS003_28"))) {

			// employeeId
			Optional<EmployeeDataMngInfo> emp = employees.stream()
					.filter(c -> c.getEmployeeCode().toString().equals(cell.getValue().getText())).findFirst();

			if (!emp.isPresent())
				return;

			employeeDto.setEmployeeCode(cell.getValue() == null ? "" : cell.getValue().getText());

			employeeDto.setEmployeeId(emp.get().getEmployeeId());

			employeeDto.setPersonId(emp.get().getPersonId());

			if (!category.isHistoryCategory()) {

				AffCompanyHist affComHist = this.companyHistRepo
						.getAffCompanyHistoryOfEmployee(emp.get().getEmployeeId());

				AffCompanyHistByEmployee affHistEmp = affComHist.getAffCompanyHistByEmployee(emp.get().getEmployeeId());

				List<AffCompanyHistItem> affHistItemLst = affHistEmp.getLstAffCompanyHistoryItem();

				AffCompanyHistItem affHistItem = affHistItemLst.get(0);

				valueStartCode = affHistItem.start();
			}

		} else if (header.equals(TextResource.localize("CPS003_29"))) {

			employeeDto.setEmployeeName(cell.getValue() == null ? "" : cell.getValue().getText());

		} else {

			boolean isSelf = sid.equals(employeeDto.getEmployeeId());

			List<PerInfoItemDefForLayoutDto> itemAuths = perItems.get(isSelf);

			String value = null;

			if (cell.getValue() != null) {

				switch (cell.getValue().getType()) {

				case TEXT:

					String tmp = cell.getValue().getText();

					value = tmp.contains("-") == true ? tmp.replace("-", "/") : tmp;

					List<String> ddMMyyy = Arrays.asList("dd/MM/yyyy", "dd-MM-yyyy");

					List<String> MMddyyy = Arrays.asList("MM/dd/yyyy", "MM-dd-yyyy");

					Optional<SimpleDateFormat> dateFormat = validateDate(value);

					Optional<DateTimeFormatter> dateTime = validateDateTime(value);

					if (dateFormat.isPresent()) {

						if (ddMMyyy.contains(dateFormat.get().toPattern())) {

							String[] dateSplit = value.split("[-/]");

							if (dateSplit.length == 3) {

								value = GeneralDate.ymd(Integer.valueOf(dateSplit[2]).intValue(),

										Integer.valueOf(dateSplit[1]).intValue(),

										Integer.valueOf(dateSplit[0]).intValue()).toString();
							}

						} else if (MMddyyy.contains(dateFormat.get().toPattern())) {

							String[] dateSplit = value.split("[-/]");

							if (dateSplit.length == 3) {

								value = GeneralDate.ymd(Integer.valueOf(dateSplit[2]).intValue(),

										Integer.valueOf(dateSplit[0]).intValue(),

										Integer.valueOf(dateSplit[1]).intValue()).toString();

							}
						} else {

							value = GeneralDate.fromString(value, "yyyy/MM/dd").toString();

						}
					}

					if (dateTime.isPresent()) {

						List<String> yyyyMMdd = Arrays.asList("yyyyMMdd");

						if (yyyyMMdd.contains("yyyyMMdd")) {

							try {

								value = LocalDate.parse(value, dateTime.get()).toString().replace("-", "/");

							} catch (Exception e) {

								break;

							}

						}

					}

					break;

				case NUMBER:

					value = value == "" ? null : String.valueOf(cell.getValue().getDecimal());

					break;

				case DATE:

					String date = cell.getValue().getDate().toString();

					value = date.contains("-") == true ? date.replace("-", "/") : date;

					break;

				case DATETIME:

					value = cell.getValue().getDateTime().toString();

					break;

				default:
					break;

				}

			}

			// lấy dữ liệu của itemName
			boolean isSelectionCode = headerTemp.contains("（コード）");

			if (isSelectionCode) {

				CheckFileFinder.header = headerTemp.split("（")[0];

			}

			Optional<GridEmpHead> headerGridOpt = headerReal.stream().filter(c -> {

				if (isSelectionCode) {

					return c.getItemName().equals(CheckFileFinder.header);

				} else {

					return c.getItemName().equals(headerTemp);

				}

			}).findFirst();

			if (headerGridOpt.isPresent()) {

				// check quyền cho từng row để xem item nào được hiển thị giá trị
				boolean isAuth = false;

				GridEmpHead headerGrid = headerGridOpt.get();

				for (PerInfoItemDefForLayoutDto ia : itemAuths) {

					if (!ia.getItemCode().equals(headerGrid.getItemCode())) {

						for (PerInfoItemDefForLayoutDto iaChild : ia.getLstChildItemDef()) {

							if (iaChild.getItemCode().equals(headerGrid.getItemCode())) {

								isAuth = true;

								break;
							}
						}

						if (isAuth)
							break;
					} else {

						isAuth = true;
					}
				}

				if (isAuth) {

					Object contraint = contraintList.get(headerGrid.getItemCode());

					// trường hợp lấy ra baseDate theo file import, lấy startDate
					if (category.isHistoryCategory()) {

						if (headerGrid.getItemCode().equals(period.getPeriods().get(0).getItem())) {

							DateConstraint dateContraint = (DateConstraint) contraint;

							Optional<String> error;

							if (dateContraint.getDateType() == DateType.YEARMONTH) {

								String valueCheck = value == null ? "" : value;

								String[] valueSplit = value.split("[/-]");

								valueCheck = valueSplit.length >= 2 ? valueSplit[0] + "/" + valueSplit[1] : valueCheck;

								error = dateContraint.validateString(valueCheck);

								if (error.isPresent()) {

									valueStartCode = GeneralDate.today();

								} else {

									valueStartCode = value == null ? GeneralDate.today()
											: GeneralDate.fromString(valueCheck + "/01", "yyyy/MM/dd");
								}

							} else {

								error = dateContraint.validateString(value == null ? "" : value);

								if (error.isPresent()) {

									valueStartCode = GeneralDate.today();

								} else {

									valueStartCode = value == null ? GeneralDate.today()
											: GeneralDate.fromString(value, "yyyy/MM/dd");

								}

							}

						}
					}

					ItemRowDto empBody = new ItemRowDto();

					if (isSelectionCode) {

						String selectionCode = value == null ? "" : value;

						empBody.setItemCode(headerGrid.getItemCode());

						empBody.setItemName(headerGrid.getItemName());

						empBody.setItemOrder(headerGrid.getItemOrder());

						empBody.setValue(selectionCode);

						SingleItemDto singleDto = (SingleItemDto) headerGrid.getItemTypeState();

						empBody.setDataType(singleDto.getDataTypeState().getDataTypeValue());

						List<ComboBoxObject> comboxLst = this.getComboBox(headerGrid, category,
								employeeDto.getEmployeeId(), empBody, items);

						// thuật toán lấy selectionId, workplaceId, codeName,...
						Optional<ComboBoxObject> combo = comboxLst.stream().filter(c -> {

							if (c.getOptionText().contains(JP_SPACE)) {

								String[] stringSplit = c.getOptionText().split(JP_SPACE);

								if (stringSplit[0].equals(selectionCode)) {

									return true;
								}

								return false;

							} else {

								if (c.getOptionValue().equals(selectionCode)) {

									return true;

								}

								return false;
							}

						}).findFirst();

						empBody.setValue(combo.isPresent() == true ? combo.get().getOptionValue() : "");
						
						//CS00036 special  (#119681)
						if (headerGrid.getItemCode().equals("IS00375")) {
							currentIS00375 = combo.isPresent() ? combo.get().getOptionValue() : "";
						}
						if (headerGrid.getItemCode().equals("IS00380")) {
							currentIS00380 = combo.isPresent() ? combo.get().getOptionValue() : "";
						}

						empBody.setLstComboBoxValue(comboxLst);

						validateCombox(employeeDto.getEmployeeId(), headerGrid, empBody, itemErrors, index, comboxLst);

						items.add(empBody);

					} else {
						if (!headerGrid.getItemName().equals(CheckFileFinder.header)) {

							empBody.setItemCode(headerGrid.getItemCode());

							empBody.setItemName(headerGrid.getItemName());

							empBody.setItemOrder(headerGrid.getItemOrder());

							convertValue(employeeDto.getEmployeeId(), empBody, headerGrid,
									cell.getValue() == null ? null : value, contraint, itemErrors, index);

							empBody.setTextValue(empBody.getValue() == null ? "" : empBody.getValue().toString());

							items.add(empBody);

						}

					}

				}

				headerRemain.add(headerGrid);

			}

		}

	}
	
	

	private void validateCombox(String sid, GridEmpHead headerGrid, ItemRowDto empBody, List<ItemError> itemErrors,
			int index, List<ComboBoxObject> combox) {
		String value =(String) empBody.getValue();
		List<String> comboxKey = combox.stream().map(c -> c.getOptionValue()).collect(Collectors.toList());
		if (headerGrid.isRequired()) {
			if ( value == null || value == "") {
				ItemError error = new ItemError(sid, "", index, headerGrid.getItemCode(), "MsgB_2");
				itemErrors.add(error);
				empBody.setError(true);
			} else {
				if (!comboxKey.contains(value)) {
					ItemError error = new ItemError(sid, "", index, headerGrid.getItemCode(), "MsgB_2");
					itemErrors.add(error);
					empBody.setError(true);
				}
			}
		} else {
			if (!comboxKey.contains(value) && !StringUtil.isNullOrEmpty(value, true)) {
				ItemError error = new ItemError(sid, "", index, headerGrid.getItemCode(), "MsgB_2");
				itemErrors.add(error);
				empBody.setError(true);
			}
		}
	}
	
	/**
	 * getComboBox
	 * @param headerGrid
	 * @param category
	 * @param sid
	 * @param empBody
	 * @param items
	 * @return
	 */
	public List<ComboBoxObject> getComboBox(GridEmpHead headerGrid, PersonInfoCategory category, String sid, ItemRowDto empBody, List<ItemRowDto> items) {
		ItemTypeStateDto dto = headerGrid.getItemTypeState();
		if(dto.getItemType() == ItemType.SINGLE_ITEM.value) {
			SingleItemDto singleDto = (SingleItemDto) dto;
				if (singleDto.getDataTypeState().getDataTypeValue() == DataTypeValue.SELECTION.value
						|| singleDto.getDataTypeState().getDataTypeValue() == DataTypeValue.SELECTION_BUTTON.value
						|| singleDto.getDataTypeState().getDataTypeValue() == DataTypeValue.SELECTION_RADIO.value) {
					boolean isDataType6 = singleDto.getDataTypeState().getDataTypeValue() == DataTypeValue.SELECTION.value?  true: false;
				SelectionItemDto selectionItemDto = (SelectionItemDto) singleDto.getDataTypeState();
				List<ComboBoxObject> combox = this.comboBoxRetrieveFactory.getComboBox(selectionItemDto, sid,
						GeneralDate.today(), headerGrid.isRequired(), category.getPersonEmployeeType(), isDataType6,
						category.getCategoryCode().v(), null, false);
				return combox;
				}
		}
		return new ArrayList<>();
	}
	
	/**
	 * generateContraint
	 * @param headerReal
	 * @return
	 */
	private HashMap<String, Object> generateContraint(List<GridEmpHead> headerReal){
		HashMap<String, Object> contraintList = new HashMap<>();
		
		headerReal.stream().forEach(c ->{
			ItemTypeStateDto itemTypeStateDto = c.getItemTypeState();
			Object obj;
			if(itemTypeStateDto.getItemType() == ItemType.SINGLE_ITEM.value) {
				SingleItemDto singleDto = (SingleItemDto) itemTypeStateDto;
				switch(singleDto.getDataTypeState().getDataTypeValue()) {
				case 1:
					StringItemDto stringDto = (StringItemDto) singleDto.getDataTypeState();
					StringCharType type;
					int length = stringDto.getStringItemLength();
					switch (stringDto.getStringItemType()) {
					case 1:
						type = StringCharType.ANY;
						length = length/2;
						break;
					case 2:
						type = StringCharType.ANY_HALF_WIDTH;
						break;
					case 3:
						type = StringCharType.ALPHA_NUMERIC;
						break;
					case 4:
						type = StringCharType.NUMERIC;
					case 5:
						type = StringCharType.KATAKANA;
						length = length/2;
						break;
					case 6:
						
					case 7:
					default: 
						type = StringCharType.ANY;
						length = length/2;
						break;
					}

					obj = new StringConstraint( 0, type, length);
					contraintList.put(c.getItemCode(), obj);
					break;
				case 2:
					NumericItemDto numericItemDto = (NumericItemDto) singleDto.getDataTypeState();
					double max = Math.pow(10, numericItemDto.getIntegerPart()) - Math.pow(10, -(numericItemDto.getDecimalPart() == null? 0:numericItemDto.getDecimalPart().intValue()));
					double minItem = numericItemDto.getNumericItemMin() == null
							? (numericItemDto.getNumericItemMinus() == 1
									? (numericItemDto.getNumericItemAmount() == 1 ? 0 : -max)
									: 0)
							: numericItemDto.getNumericItemMin().doubleValue();
					double maxItem = numericItemDto.getNumericItemMax() == null? max : numericItemDto.getNumericItemMax().doubleValue();
					obj = new NumericConstraint(0, numericItemDto.getNumericItemMinus() == 0 ? true : false,
							new BigDecimal(minItem), new BigDecimal(maxItem),
							numericItemDto.getIntegerPart(),
							numericItemDto.getDecimalPart() == null ? 0 : numericItemDto.getDecimalPart().intValue());
					contraintList.put(c.getItemCode(), obj);
					break;
					
				case 3:
					DateItemDto dateDto = (DateItemDto) singleDto.getDataTypeState();
					DateType dateType = DateType.DATE;
					switch (dateDto.getDateItemType()) {
					case 1:
						dateType = DateType.DATE;
						break;
					case 2:
						dateType = DateType.YEARMONTH;
						break;
					case 3:
						dateType = DateType.YEAR;
						break;
					default: 
						dateType = DateType.DATE;
						break;
					}
					obj = new DateConstraint(0, dateType);
					contraintList.put(c.getItemCode(), obj);
					break;
				case 4:
					TimeItemDto timeItemDto = (TimeItemDto) singleDto.getDataTypeState();
					obj = new TimeConstraint(0, (int) timeItemDto.getMin(), (int) timeItemDto.getMax());
					contraintList.put(c.getItemCode(), obj);
					break;
					
				case 5:
					TimePointItemDto timePointItemDto = (TimePointItemDto) singleDto.getDataTypeState();
					obj = new TimePointConstraint(0, (int) timePointItemDto.getTimePointItemMin(), (int) timePointItemDto.getTimePointItemMax());
					contraintList.put(c.getItemCode(), obj);
					break;
				case 11:
					NumericButtonDto numbericButtonDto = (NumericButtonDto) singleDto.getDataTypeState();
					obj = new NumericConstraint(0, numbericButtonDto.getNumericItemMinus() == 0 ? true : false,
							numbericButtonDto.getNumericItemMin(), numbericButtonDto.getNumericItemMax(),
							numbericButtonDto.getIntegerPart(),
							numbericButtonDto.getDecimalPart());
					contraintList.put(c.getItemCode(), obj);
					break;
				default: break;
				}
				
			}
		});
		return contraintList;
	}
	
	/**
	 * convertValue kết hợp với validate
	 * @param itemDto
	 * @param gridHead
	 * @param value
	 * @param contraint
	 */
	private void convertValue(String sid, ItemRowDto itemDto, GridEmpHead gridHead, String value, Object contraint, List<ItemError> itemErrors, int index) {
		if (gridHead.getItemTypeState().getItemType() == 2) {
			SingleItemDto singleDto = (SingleItemDto) gridHead.getItemTypeState();
			DataTypeStateDto dataTypeState = (DataTypeStateDto) singleDto.getDataTypeState();
			DataTypeValue itemValueType = EnumAdaptor.valueOf(dataTypeState.getDataTypeValue(), DataTypeValue.class);
			itemDto.setDataType(dataTypeState.getDataTypeValue());
			switch (itemValueType) {
			case STRING:
				itemDto.setValue(value == null ? null : value);
				StringConstraint stringContraint = (StringConstraint) contraint;
				if (gridHead.isRequired()) {
					if (value == null || value =="") {
						ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), "FND_E_REQ_INPUT");
						itemDto.setError(true);
						itemErrors.add(error);
						break;
					} else {
						Optional<String> string = stringContraint.validateString(value.toString());
						if (itemSpecialLst.contains(itemDto.getItemCode())) {
							ItemError errorSpace = validateItemOfCS0002(sid, itemDto, value.toString(), index);
							if (string.isPresent()) {
								itemDto.setError(true);
								ItemError stringError = new ItemError(sid, "", index, itemDto.getItemCode(),
										TextResource.localize(string.get(), Arrays.asList(gridHead.getItemName(),
												String.valueOf(stringContraint.getMaxLenght()))));
								itemErrors.add(stringError);
							} else if (errorSpace != null) {
								itemErrors.add(errorSpace);
							}
							break;
						} else {
							if (string.isPresent()) {
								itemDto.setError(true);
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(),
										TextResource.localize(string.get(), Arrays.asList(gridHead.getItemName(),
										String.valueOf(stringContraint.getMaxLenght()))));
								itemErrors.add(error);
								break;
							}
						}
					}
				} else {
					if (value != null ) {
						Optional<String> string = stringContraint.validateString(value.toString());
						if (itemSpecialLst.contains(itemDto.getItemCode())) {
							if(value.toString() != "") {
								 ItemError errorSpace = validateItemOfCS0002(sid, itemDto, value.toString(),  index);
								if (string.isPresent()) {
									itemDto.setError(true);
									ItemError stringError = new ItemError(sid, "", index, itemDto.getItemCode(),
											TextResource.localize(string.get(), Arrays.asList(gridHead.getItemName(),
													String.valueOf(stringContraint.getMaxLenght()))));
									itemErrors.add(stringError);
								} else if (errorSpace != null) {
									itemErrors.add(errorSpace);
								}
							}
							break;
						} else {
							if (string.isPresent()) {
								itemDto.setError(true);
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(),
										TextResource.localize(string.get(), Arrays.asList(gridHead.getItemName(),
												String.valueOf(stringContraint.getMaxLenght()))));
								itemErrors.add(error);
								break;
							}

						}
					}
				}
				break;
			case NUMERIC:
			case NUMBERIC_BUTTON:
				itemDto.setValue(value == null || value == "" ? null : (isNumeric(value) == true? new BigDecimal(value) : value)); 
				NumericConstraint numberContraint = (NumericConstraint) contraint;
				if (gridHead.isRequired()) {
					if (itemDto.getValue() == null) {
						//CS00036 special  (#119681)
						if (currentIS00375.equals("0") && itemDto.getItemCode().equals("IS00379")) {
							break;
						}
						if (currentIS00380.equals("0") && itemDto.getItemCode().equals("IS00384")) {
							break;
						}
						itemDto.setError(true);
						ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), "FND_E_REQ_INPUT");
						itemErrors.add(error);
						break;
					} else {
						Optional<String> string = numberContraint.validateString(value.toString());
						if (string.isPresent()) {
							itemDto.setError(true);
							if(string.get().equals("MsgB_11")) {
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), 
										TextResource.localize(string.get(), 
												Arrays.asList(gridHead.getItemName(),
												String.valueOf(numberContraint.getMin()), 
												String.valueOf(numberContraint.getMax()),
												String.valueOf(numberContraint.getDecimalPart()))));
								itemErrors.add(error);
								break;
							}else if(string.get().equals("MsgB_8")) {
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), 
										TextResource.localize(string.get(), 
												Arrays.asList(gridHead.getItemName(),
												String.valueOf(numberContraint.getMin()),
												String.valueOf(numberContraint.getMax()))));
								itemErrors.add(error);
								break;
							}else if(string.get().equals("MsgB_12")) {
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), 
										TextResource.localize(string.get(), 
												Arrays.asList(gridHead.getItemName(),
												String.valueOf(numberContraint.getIntegerPart()),
												String.valueOf(numberContraint.getDecimalPart()))));
								itemErrors.add(error);
								break;
							}else if(string.get().equals("MsgB_9")) {
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(),
										TextResource.localize(string.get(),
												Arrays.asList(gridHead.getItemName(),
												String.valueOf(numberContraint.getIntegerPart()))));
								itemErrors.add(error);
								break;
							}else if(string.get().equals("MsgB_13")) {
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(),
										TextResource.localize(string.get(), 
												Arrays.asList(gridHead.getItemName(),
												String.valueOf(numberContraint.getIntegerPart()),
												String.valueOf(numberContraint.getDecimalPart()))));
								itemErrors.add(error);
								break;
							}else if(string.get().equals("MsgB_10")) {
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(),
										TextResource.localize(string.get(), 
												Arrays.asList(gridHead.getItemName(),
												String.valueOf(numberContraint.getIntegerPart()))));
								itemErrors.add(error);
								break;
							}
							break;
						}
						break;
					}
				} else {
					if (itemDto.getValue() != null) {
						Optional<String> string = numberContraint.validateString(value.toString());
						if (string.isPresent()) {
							itemDto.setError(true);
							if(string.get().equals("MsgB_11")) {
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), 
										TextResource.localize(string.get(), 
												Arrays.asList(gridHead.getItemName(),
												String.valueOf(numberContraint.getMin()), 
												String.valueOf(numberContraint.getMax()),
												String.valueOf(numberContraint.getDecimalPart()))));
								itemErrors.add(error);
								break;
							}else if(string.get().equals("MsgB_8")) {
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), 
										TextResource.localize(string.get(), 
												Arrays.asList(gridHead.getItemName(),
												String.valueOf(numberContraint.getMin()),
												String.valueOf(numberContraint.getMax()))));
								itemErrors.add(error);
								break;
							}else if(string.get().equals("MsgB_12")) {
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), 
										TextResource.localize(string.get(), 
												Arrays.asList(gridHead.getItemName(),
												String.valueOf(numberContraint.getIntegerPart()),
												String.valueOf(numberContraint.getDecimalPart()))));
								itemErrors.add(error);
								break;
							}else if(string.get().equals("MsgB_9")) {
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(),
										TextResource.localize(string.get(),
												Arrays.asList(gridHead.getItemName(),
												String.valueOf(numberContraint.getIntegerPart()))));
								itemErrors.add(error);
								break;
							}else if(string.get().equals("MsgB_13")) {
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(),
										TextResource.localize(string.get(), 
												Arrays.asList(gridHead.getItemName(),
												String.valueOf(numberContraint.getIntegerPart()),
												String.valueOf(numberContraint.getDecimalPart()))));
								itemErrors.add(error);
								break;
							}else if(string.get().equals("MsgB_10")) {
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(),
										TextResource.localize(string.get(), 
												Arrays.asList(gridHead.getItemName(),
												String.valueOf(numberContraint.getIntegerPart()))));
								itemErrors.add(error);
								break;
							}
							break;
						}
					}
				}
				break;
			case DATE:
				DateItemDto dateDto = (DateItemDto) dataTypeState;
				itemDto.setValue(value == null || value == "" ? null : value); 
				DateConstraint dateContraint = (DateConstraint) contraint;
				if (gridHead.isRequired()) {
					if (itemDto.getValue() == null) {
						itemDto.setError(true);
						ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), "FND_E_REQ_INPUT");
						itemErrors.add(error);
						break;
					} else {
						if(dateDto.getDateItemType() == 2) {
							if(value.toString() != null) {
								String[] valueSplit = value.toString().split("[-/]");
								String valueComvert = valueSplit.length >= 2? valueSplit[0]+ "/" + valueSplit[1]: value.toString();
								Optional<String> date = dateContraint.validateString(valueComvert);
								if(date.isPresent()) {
									itemDto.setError(true);
									ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), date.get());
									itemErrors.add(error);
									break;
								}
								
							}
							
						}else {
							
							Optional<String>  string = dateContraint.validateString(value.toString());
							if (string.isPresent()) {
								itemDto.setError(true);
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), string.get());
								itemErrors.add(error);
								break;
							}
							
						}

					}
				} else {
					if (itemDto.getValue() != null) {
						if(dateDto.getDateItemType() == 2) {
							if(value.toString() != null) {
								String[] valueSplit = value.toString().split("[-/]");
								String valueComvert = valueSplit.length >= 2 ? valueSplit[0]+ "/" + valueSplit[1]: value.toString();
								Optional<String> date = dateContraint.validateString(valueComvert);
								if(date.isPresent()) {
									itemDto.setError(true);
									ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), date.get());
									itemErrors.add(error);
									break;
								}
								
							}
							
						}else {
							
							Optional<String>  string = dateContraint.validateString(value.toString());
							if (string.isPresent()) {
								itemDto.setError(true);
								ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), string.get());
								itemErrors.add(error);
								break;
							}
							
						}
					}
				}
				break;
			case TIME:
				TimeConstraint timeContraint = (TimeConstraint) contraint;
				if (gridHead.isRequired()) {
					if (value == null || value =="") {
						//CS00036 special  (#119681)
						if (currentIS00375.equals("0") && itemDto.getItemCode().equals("IS01101")) {
							break;
						}
						if (currentIS00380.equals("0") && itemDto.getItemCode().equals("IS01102")) {
							break;
						}
						itemDto.setError(true);
						ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), "FND_E_REQ_INPUT");
						itemErrors.add(error);
						break;
					} else if (value != null  && value != "") {
						String[] stringSplit = value.split(":");
						if(stringSplit.length == 2) {
							if(isNumeric(stringSplit[0]) && isNumeric(stringSplit[1])) {
								itemDto.setValue(new BigDecimal(MinutesBasedTimeParser.parse(value).asDuration()));
							}else {
								itemDto.setValue(value);
							}
						}else {
							itemDto.setValue(value);
						}
						
						Optional<String> string = timeContraint.validateString(value.toString());
						if (string.isPresent()) {
							itemDto.setError(true);
							ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), TextResource.localize(string.get(),
									Arrays.asList(gridHead.getItemName(), convertTime(timeContraint.getMin()), convertTime(timeContraint.getMax()))));
							itemErrors.add(error);
							break;
						}
					}
				} else {
					if (value != null && value != "") {
						String[] stringSplit = value.split(":");
						if(stringSplit.length == 2) {
							if(isNumeric(stringSplit[0]) && isNumeric(stringSplit[1])) {
								itemDto.setValue(new BigDecimal(MinutesBasedTimeParser.parse(value).asDuration()));
							}else {
								itemDto.setValue(value);
							}
						}else {
							itemDto.setValue(value);
						}
						
						Optional<String> string = timeContraint.validateString(value.toString());
						if (string.isPresent()) {
							itemDto.setError(true);
							ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), TextResource.localize(string.get(),
									Arrays.asList(gridHead.getItemName(), convertTime(timeContraint.getMin()), convertTime(timeContraint.getMax()))));
							itemErrors.add(error);
							break;
						}
						
					}
				}
				
				break;
			case TIMEPOINT:
				TimePointConstraint timePointContraint = (TimePointConstraint) contraint;
				if (gridHead.isRequired()) {
					if (value != null && value != "") {
						itemDto.setValue(convertTimepoint(value)); 
						Optional<String> string = timePointContraint.validateString(value);
						if (string.isPresent()) {
							itemDto.setError(true);
							//MsgB_56
							ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(),
									TextResource.localize(string.get(),
											Arrays.asList(gridHead.getItemName(),
													convertStringTimepoint(timePointContraint.getMin()),
													convertStringTimepoint(timePointContraint.getMax()))));
							itemErrors.add(error);
							break;
						}
					}else {
						//MsgB_1
						ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(),
								TextResource.localize("MsgB_1", Arrays.asList(gridHead.getItemName())));
						itemDto.setError(true);
						itemErrors.add(error);
					} 
					
				} else {
					if (value != null && value != "") {
						itemDto.setValue(convertTimepoint(value));
						Optional<String> string = timePointContraint.validateString(value);
						if (string.isPresent()) {
							itemDto.setError(true);
							ItemError error = new ItemError(sid, "", index, itemDto.getItemCode(), 
									TextResource.localize(string.get(),
											Arrays.asList(gridHead.getItemName(),
													convertStringTimepoint(timePointContraint.getMin()),
													convertStringTimepoint(timePointContraint.getMax()))));
							itemErrors.add(error);
							break;
						}
					}
				}
				break;
			case SELECTION:
			case SELECTION_BUTTON:
			case SELECTION_RADIO:
				break;
			default:
				break;
			}
		}

	}
	
	private boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	/**
	 * validate những item đặc biệt của category CS0002
	 * validateItemOfCS0002
	 * @param itemDto
	 * @param value
	*/
	private ItemError validateItemOfCS0002(String sid, ItemRowDto itemDto, String value, int index){
		for (String itemCode : itemSpecialLst) {
			if (itemDto.getItemCode().equals(itemCode)) {
				if (value.startsWith(JP_SPACE) || value.endsWith(JP_SPACE)
						|| !value.contains(JP_SPACE)) {
					itemDto.setError(true);
					return new ItemError(sid, "", index, itemDto.getItemCode(), "Msg_924");
				}
			}
		}
		return null;
	}
	
	/**
	 * danh cho item kieu time - 4
	 * convertTimepoint -> int
	 * @param value
	 * @return
	 */
	private String convertTime(int value) {
		int hours = value/60;
		int minute = (value  - hours*60)%60;
		String valueConvert = hours+":"+ (String.valueOf(minute).length() == 1? "0"+ minute : minute);
		return valueConvert;
	}
	
	/**
	 * danh cho item kieu timepoint - 5
	 * convertTimepoint -> int
	 * @param value
	 * @return
	 */
	private String convertTimepoint(String value) {
		List<String> day = Arrays.asList("当日","前日","翌日");
		boolean checkEquals = false;
		for(int i = 0; i< day.size(); i++) {
			if(value.matches((day.get(i)+"(.*)"))) {
				checkEquals = true;
				String[] e = value.split("日");
				try {
				int minute= MinutesBasedTimeParser.parse(e[1]).asDuration();
				switch(i) {
				case 0:
					return String.valueOf(minute);
				case 1:
					return String.valueOf(-minute);
				case 2:
					return String.valueOf(minute+ 24*60);
				}
				
				}catch(RuntimeException re){
					 return value;
				}
			}
		}
		
		if(checkEquals == false) {
			String[] split  = value.split(":");
			if(split.length == 2) {
				if(isNumeric(split[0]) && isNumeric(split[1])) {
					return  String.valueOf(Integer.valueOf(split[0]).intValue() * 60 + Integer.valueOf(split[1]).intValue());
				}
				
			}
		}
		return value;
	}
	
	private String convertStringTimepoint(int value) {
		int hours = value/60;
		int minute = (value  - hours*60)%60;
		String valueConvert = hours+":"+ (String.valueOf(minute).length() == 1? "0"+ minute : minute);
		String result = "";
		String[] valueSplit = valueConvert.split(":");
		if(valueSplit.length > 0) {
			if(valueSplit.length == 2) {
				if(!isNumeric(valueSplit[0]) || !isNumeric(valueSplit[1])) return valueConvert;
			}
			if(valueSplit.length > 2) {
				 return valueConvert;
			}
			
			if(valueSplit.length == 1) {
				if(!isNumeric(valueSplit[0])) return valueConvert;
			}
			
			int day = hours/24;
			
			if (hours < -1) {
				return "前日" + Math.abs(hours) + ":"+ valueSplit[1];
			}
			if (day == 0) {
				result = "当日" + hours + ":"+ valueSplit[1];
			}
			
			if(day == 1) {
				result = "翌日" + (hours - 24) + ":"+ valueSplit[1];
			}
			
			if(day == 2) {
				result = "翌々日" + (hours - 48) + ":"+ valueSplit[1];
				
			}
		}
		
		return result;
	}
	
	private Object convertValue(int valueType, String value) {
		ItemValueType itemValueType = EnumAdaptor.valueOf(valueType, ItemValueType.class);
		
		if (value == null || value.equals("")) return null;
		
		switch (itemValueType) {
		case STRING:
		case SELECTION: 
		case SELECTION_BUTTON:
		case SELECTION_RADIO:
		case READONLY:
		case READONLY_BUTTON:
		case RELATE_CATEGORY:
			if(value.equals("")) return null;
			return value;
		case TIME:
		case TIMEPOINT:
			try {
				return new Integer(new BigDecimal(value).intValue());
			} catch (Exception e) {
				return value;
			}
		case NUMBERIC_BUTTON:
		case NUMERIC:
			if(isNumeric(value)) {
				return new BigDecimal(value);
			}else {
				return value;
			}
		case DATE:
			List<String> ddMMyyy = Arrays.asList("dd/MM/yyyy","dd-MM-yyyy");
			List<String> MMddyyy = Arrays.asList("MM/dd/yyyy","MM-dd-yyyy");
			try {
				Optional<SimpleDateFormat> dateFormat = validateDate(value);
				
				if (dateFormat.isPresent()) {
					
					if (ddMMyyy.contains(dateFormat.get().toPattern())) {
						
						String[] dateSplit = value.split("[-/]");
						
						if (dateSplit.length == 3) {
							
							return GeneralDate.ymd(Integer.valueOf(dateSplit[2]).intValue(),
									Integer.valueOf(dateSplit[1]).intValue(), Integer.valueOf(dateSplit[0]).intValue());
						} else {
							
							return value;
							
						}
					}
					if (MMddyyy.contains(dateFormat.get().toPattern())) {
						
						String[] dateSplit = value.split("[-/]");
						
						if (dateSplit.length == 3) {
							
							return GeneralDate.ymd(Integer.valueOf(dateSplit[2]).intValue(),
									Integer.valueOf(dateSplit[0]).intValue(), Integer.valueOf(dateSplit[1]).intValue());
						
						} else {
							
							return value;
							
						}
					} else {
						return GeneralDate.fromString(value, "yyyy/MM/dd");
					}
				}
			} catch (Exception e) {
				return value;
			}
		default:
			return null;
		}

	}
	
	private DataValueAttribute converType(int valueType) {
		ItemValueType itemValueType = EnumAdaptor.valueOf(valueType, ItemValueType.class);
		switch (itemValueType) {
		case STRING:
		case SELECTION: 
		case SELECTION_BUTTON:
		case SELECTION_RADIO:
		case READONLY:
		case READONLY_BUTTON:
		case RELATE_CATEGORY:
			return DataValueAttribute.STRING;
		case NUMBERIC_BUTTON:
		case NUMERIC:
			return DataValueAttribute.COUNT;
		case DATE:
			return DataValueAttribute.DATE;
		case TIME:
			return DataValueAttribute.TIME;
		case TIMEPOINT:
			return DataValueAttribute.CLOCK;	
		default:
			return DataValueAttribute.of(-1);

		}
	}
	/**
	 * type = giá trị kiểu 
	 * @param valueExcel
	 * @param valueDb
	 * @param type
	 * @return
	 */
	private boolean isEqual(Object valueExcel, Object valueDb, int type) {
		if(type == 2 || type == 11) {
			boolean ischeckEx = isNumeric(valueExcel == null?"": valueExcel.toString());
			boolean ischeckDb = isNumeric(valueDb == null? "": valueDb.toString());
			if(ischeckEx == true && ischeckDb == true) {
				BigDecimal valEx = (BigDecimal) valueExcel;
				BigDecimal valDb = (BigDecimal) valueDb;
				if(valEx != null) {
					if(valDb == null) return false;
					return valEx.compareTo(valDb) == 0? true: false;
				}else {
					if(valEx == null && valueDb == null){ 
						return true;
					}
					if(valEx == null && valueDb != null) return false;
					return valDb.compareTo(valEx) == 0? true: false;
				}
			}else {
				return false;
			}

		}else {
			if(valueExcel != null) {
				return valueExcel.equals(valueDb);
			}else {
				if(valueExcel == null && valueDb == null){ 
					return true;
				}
				return valueDb.equals(valueExcel);
			}
		}

	}
	
	private Optional<SimpleDateFormat> validateDate(String value) {
		List<SimpleDateFormat> dateFormats = createSimpleDateFormat();
		List<SimpleDateFormat> isDateLst = new ArrayList<>();
		for(SimpleDateFormat dateFormat : dateFormats) {
			try {
				dateFormat.parse(value.trim());
				isDateLst.add(dateFormat);
			} catch (ParseException pe) {
				
			}
			
		}
		if(!isDateLst.isEmpty()) return Optional.ofNullable(isDateLst.get(0));
		return Optional.empty();
	}
	
	private Optional<DateTimeFormatter> validateDateTime(String value) {
		List<DateTimeFormatter> dateFormats = createDateTimeFormatter();
		List<DateTimeFormatter> isDateLst = new ArrayList<>();
		for(DateTimeFormatter dateFormat : dateFormats) {
			try {
			dateFormat.parse(value.trim());
			isDateLst.add(dateFormat);
			}catch(Exception e) {
			}
		}
		if(!isDateLst.isEmpty()) return Optional.ofNullable(isDateLst.get(0));
		return Optional.empty();
	}
	
	private  List<SimpleDateFormat> createSimpleDateFormat(){
		List<SimpleDateFormat> result = new ArrayList<>();
		SimpleDateFormat yMd = new SimpleDateFormat("yyyy/MM/dd");
		yMd.setLenient(false);
		result.add(yMd);
		SimpleDateFormat yMd1 = new SimpleDateFormat("yyyy-MM-dd");
		yMd1.setLenient(false);
		result.add(yMd1);
		SimpleDateFormat dMy = new SimpleDateFormat("dd/MM/yyyy");
		dMy.setLenient(false);
		result.add(dMy);
		SimpleDateFormat dMy1 = new SimpleDateFormat("dd-MM-yyyy");
		dMy1.setLenient(false);
		result.add(dMy1);
		return result;
	}
	
	private  List<DateTimeFormatter> createDateTimeFormatter(){
		List<DateTimeFormatter> result = new ArrayList<>();
		DateTimeFormatter yMd = DateTimeFormatter.ofPattern("yyyyMMdd");
		result.add(yMd);
		DateTimeFormatter mdy = DateTimeFormatter.ofPattern("MMddyyyy");
		result.add(mdy);
		DateTimeFormatter dmy = DateTimeFormatter.ofPattern("ddMMyyyy");
		result.add(dmy);
		return result;
	}
	private Map<String, Periods> createPeriod(){
		Map<String, Periods> aMap = new HashMap<>();
		// 所属会社履歴
		aMap.put("CS00003", new Periods(Arrays.asList(new Period("IS00020", null), new Period("IS00021", null))));
		// 分類１
		aMap.put("CS00004", new Periods(Arrays.asList(new Period("IS00026", null), new Period("IS00027", null))));
		// 雇用
		aMap.put("CS00014", new Periods(Arrays.asList(new Period("IS00066", null), new Period("IS00067", null))));
		// 職位本務
		aMap.put("CS00016", new Periods(Arrays.asList(new Period("IS00077", null), new Period("IS00078", null))));
		// 職場
		aMap.put("CS00017", new Periods(Arrays.asList(new Period("IS00082", null), new Period("IS00083", null))));
		// 休職休業
		aMap.put("CS00018", new Periods(Arrays.asList(new Period("IS00087", null), new Period("IS00088", null))));
		// 短時間勤務
		aMap.put("CS00019", new Periods(Arrays.asList(new Period("IS00102", null), new Period("IS00103", null))));
		// 労働条件
		aMap.put("CS00020", new Periods(Arrays.asList(new Period("IS00119", null), new Period("IS00120", null))));
		// 勤務種別
		aMap.put("CS00021", new Periods(Arrays.asList(new Period("IS00255", null), new Period("IS00256", null))));
		// 労働条件２
		aMap.put("CS00070", new Periods(Arrays.asList(new Period("IS00781", null), new Period("IS00782", null))));
		
		aMap.put("CS00075", new Periods(Arrays.asList(new Period("IS00787", null), new Period("IS00788", null))));
		
		aMap.put("CS00082", new Periods(Arrays.asList(new Period("IS00841", null), new Period("IS00842", null))));
		
		aMap.put("CS00092", new Periods(Arrays.asList(new Period("IS01016", null), new Period("IS01017", null))));
		return aMap;
	}
	
	@AllArgsConstructor
	@Getter
	public class Periods{
		private List<Period> periods;
//		private boolean compare() {
//			if(CollectionUtil.isEmpty(periods)) {
//				return false;
//			}
//			Period start = periods.get(0);
//			Period end = periods.get(1);
//			
//			
//			return false;
//		}
	}
	
	@AllArgsConstructor
	@Getter
	@Setter
	public class Period{
		private String item;
		private Object value;
		public boolean equal(Period other) {
			if (this.item == other.item &&  this.value == other.value) return true;
			if(this.item == other.item ) {
				if(value == null && other.value == null) return true;
//				if(value != null && other.value  == null) {}
			}
//			if (obj == null) return false;
//			if (getClass() != obj.getClass()) return false;
//			return  equals((Object) obj);
			return false;
		}
		
		public boolean equals() {
			
			return true;
		}
	}
	
	
	
	@RequiredArgsConstructor
	public enum UpdateMode {
		OVERIDED(1), ADDHISTORY(2);
		public final int value;

	}

}


