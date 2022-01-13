 /// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kaf018.d.viewmodel {
	import ApprSttExecutionDto = nts.uk.at.view.kaf018.b.viewmodel.ApprSttExecutionDto;
	import ClosureItem = nts.uk.at.view.kaf018.a.viewmodel.ClosureItem;
	import KAF018EParam = nts.uk.at.view.kaf018.e.viewmodel.KAF018EParam;
	
	@bean()
	class Kaf018DViewModel extends ko.ViewModel {
		appNameLst: Array<any> = [];
		closureItem: ClosureItem;
		startDate: string;
		endDate: string;
		apprSttExeDtoLst: Array<ApprSttExecutionDto> = [];
		currentApprSttExeDto: KnockoutObservable<ApprSttExecutionDto> = ko.observable(null);
		headers: Array<any> = [];
		columns: Array<any> = [];
		dataSource: Array<EmpInfo> = [];
		enableBack: KnockoutObservable<boolean> = ko.pureComputed(() => {
			const vm = this;
			let index = _.indexOf(vm.apprSttExeDtoLst, vm.currentApprSttExeDto())
			if(index > 0) {
				return true;
			} else {
				return false;
			}	
		});
		enableNext: KnockoutObservable<boolean> = ko.pureComputed(() => {
			const vm = this;
			let index = _.indexOf(vm.apprSttExeDtoLst, vm.currentApprSttExeDto());
			if(index < vm.apprSttExeDtoLst.length-1) {
				return true;
			} else {
				return false;
			}
		});
		
		created(params: KAF018DParam) {
			const vm = this;
			vm.$blockui('show');
			vm.appNameLst = params.appNameLst;
			vm.closureItem = params.closureItem;
			vm.startDate = params.startDate;
			vm.endDate = params.endDate;
			vm.apprSttExeDtoLst = params.apprSttExeDtoLst;
			vm.currentApprSttExeDto(_.find(params.apprSttExeDtoLst, o => o.wkpID == params.currentWkpID));
			vm.columns.push(
				{ 
					headerText: '', 
					key: 'empID', 
					width: 1,
					dataType: 'string',
					hidden: true
				},
				{ 
					headerText: vm.$i18n('KAF018_373'),
					key: 'empName',
					width: 400,
					headerCssClass: 'kaf018-d-header-empName',
					formatter: (key: string, object: EmpInfo) =>  {
						return vm.getDispEmpName(object.empID);
					}
				}
			);
			let dateRangeNumber = moment(vm.endDate,'YYYY/MM/DD').diff(moment(vm.startDate,'YYYY/MM/DD'), 'days');
			for(let i = 0; i <= dateRangeNumber; i++) {
				if( i < dateRangeNumber) {
					vm.columns.push(
						{ 
							headerText: moment(moment(vm.startDate,'YYYY/MM/DD').add(i, 'd')).date(),
							headerCssClass: vm.getHeaderCss(i),
							group: [
								{ 
									headerText: moment(moment(vm.startDate,'YYYY/MM/DD').add(i, 'd')).format('ddd'),
									key: 'dateInfoLst',
									width: '60px',
									headerCssClass: vm.getHeaderCss(i),
									columnCssClass: 'kaf018-d-column-date',
									formatter: (value: any) => vm.getStatusByDay(value, i)
								}
							]
						}
					);	
				} else {
					vm.columns.push(
						{ 
							headerText: moment(moment(vm.startDate,'YYYY/MM/DD').add(i, 'd')).date(),
							headerCssClass: vm.getHeaderCss(i),
							group: [
								{ 
									headerText: moment(moment(vm.startDate,'YYYY/MM/DD').add(i, 'd')).format('ddd'),
									key: 'dateInfoLst',
									width: '77px',
									headerCssClass: vm.getHeaderCss(i),
									columnCssClass: 'kaf018-d-column-date',
									formatter: (value: any) => vm.getStatusByDay(value, i)
								}
							]
						}
					);	
				}

			}
			let empID = '',
				empCD = '',
				empName = '',
				dateInfoLst: Array<DateInfo> = [];
			for(let j = 0; j <= dateRangeNumber; j++) {
				dateInfoLst.push({
					date: moment(moment(vm.startDate,'YYYY/MM/DD').add(j, 'd')).format('YYYY/MM/DD'),
					status: ''
				});
			}
			vm.dataSource.push({ empID, empCD, empName, dateInfoLst });
			$("#dGrid").css('visibility','hidden');
			vm.createIggrid();
			vm.refreshDataSource();
			$(window).resize(() => {
				let topRange = document.getElementById('dGrid').getBoundingClientRect().top,
					bottomRange = document.getElementById('functions-area-bottom').getBoundingClientRect().height,
					height = window.innerHeight - topRange - bottomRange - 10;
				$("#dGrid").igGrid("option", "height", height + "px");
				$("#dGrid").igGrid("option", "width", window.innerWidth - 40 + "px");
			});
		}
		
		mounted() {
			let topRange = document.getElementById('dGrid').getBoundingClientRect().top,
				bottomRange = document.getElementById('functions-area-bottom').getBoundingClientRect().height,
				height = window.innerHeight - topRange - bottomRange - 10;
			$("#dGrid").igGrid("option", "height", height + "px");
		}
		
		getDispEmpName(value: string) {
			const vm = this;
			let	empInfo: EmpInfo = _.find(vm.dataSource, o => o.empID==value);
			return '<span class="kaf018-d-column-empName">' + empInfo.empCD + '</span>' + '　　　　' + '<span class="kaf018-d-column-empName">' + empInfo.empName + '</span>';
		}
		
		getHeaderCss(value: any) {
			const vm = this;
			let dayOfWeek: number = parseInt(moment(moment(vm.startDate,'YYYY/MM/DD').add(value, 'd')).format('e'));
			if(dayOfWeek==6) {
				return 'kaf018-d-header-saturday';
			}
			if(dayOfWeek==0) {
				return 'kaf018-d-header-sunday';
			}
			return 'kaf018-d-header-date';
		}
		
		getStatusByDay(value: Array<DateInfo>, i: number) {
			const vm = this;
			let key = moment(moment(vm.startDate,'YYYY/MM/DD').add(i, 'd')).format('YYYY/MM/DD'),
				itemValue = _.find(value, o => o.date == key);
			if(itemValue) {
				return itemValue.status;
			}
			return '';
		}
		
		createIggrid() {
			const vm = this;
			$("#dGrid").igGrid({
				height: 527,
				width: window.innerWidth - 40,
				dataSource: vm.dataSource,
				primaryKey: 'empID',
				primaryKeyDataType: 'string',
				virtualization: true,
				virtualizationMode: 'continuous',
				dataRendered: () => {
					vm.$nextTick(() => {
						vm.$blockui('hide');
					});
				},
				rendered: () => {
			   		if($("#dGrid").css('visibility')=='hidden'){
						vm.$nextTick(() => {
							vm.$blockui('show');
							$('#kaf018-d-cancel-btn').focus();
						});
					} else {
						vm.$nextTick(() => {
							vm.$blockui('hide');
						});
					}
			    },
				cellClick: (evt: any, ui: any) => {
					vm.cellGridClick(evt, ui); 
				},
				columns: vm.columns,
				features: [
					{
						name: 'MultiColumnHeaders'
					},
					{
						name: 'ColumnFixing', 
						fixingDirection: 'left',
						showFixButtons: false,
						columnSettings: [
											{ columnKey: 'empName', isFixed: true }
										]
					}
				],
			});
		}
		
		cellGridClick(evt: any, ui: any) {
			const vm = this;
			if(ui.colKey=="empName") {
				let empInfoLst = vm.dataSource,
					startDate = vm.startDate,
					endDate = vm.endDate,
					currentEmpID = ui.rowKey,
					appNameLst: Array<any> = vm.appNameLst,
					eParam: KAF018EParam = { empInfoLst, startDate, endDate, currentEmpID, appNameLst };
				vm.$window.modal('/view/kaf/018/e/index.xhtml', eParam);
			}
		}
		
		close() {
			const vm = this;
			vm.$window.close({});
		}
		
		back() {
			const vm = this;
			let index = _.indexOf(vm.apprSttExeDtoLst, vm.currentApprSttExeDto());
			if (index > 0) {
				vm.currentApprSttExeDto(vm.apprSttExeDtoLst[index - 1]);
				vm.refreshDataSource();
			}
		}

		next() {
			const vm = this;
			let index = _.indexOf(vm.apprSttExeDtoLst, vm.currentApprSttExeDto());
			if (index < (vm.apprSttExeDtoLst.length-1)) {
				vm.currentApprSttExeDto(vm.apprSttExeDtoLst[index + 1]);
				vm.refreshDataSource();
			}
		}
		
		refreshDataSource() {
			const vm = this;
			let wkpID = vm.currentApprSttExeDto().wkpID,
				startDate = vm.startDate,
				endDate = vm.endDate,
				empPeriodLst = vm.currentApprSttExeDto().empPeriodLst,
				wsParam = { wkpID, startDate, endDate, empPeriodLst };
			vm.$blockui('show');
			vm.$ajax(API.getApprSttStartByEmp, wsParam).done((data) => {
				vm.dataSource = data;
				$("#dGrid").igGrid("option", "dataSource", vm.dataSource);
				$("#dGrid").css('visibility','visible');
			});
		}
	}
	
	export interface KAF018DParam {
		closureItem: ClosureItem;
		startDate: string;
		endDate: string;
		apprSttExeDtoLst: Array<ApprSttExecutionDto>;
		currentWkpID: string;
		appNameLst: Array<any>;
	}
	
	export interface EmpInfo {
		empID: string;
		empCD: string;
		empName: string;
		dateInfoLst: Array<DateInfo>;
	}
	
	interface DateInfo {
		date: string;
		status: string;
	}

	const API = {
		getApprSttStartByEmp: "at/request/application/approvalstatus/getApprSttStartByEmp",
	}
}