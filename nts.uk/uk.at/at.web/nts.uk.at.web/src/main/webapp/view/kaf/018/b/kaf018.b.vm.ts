/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kaf018.b.viewmodel {
	import character = nts.uk.characteristics;
	import InitDisplayOfApprovalStatus = nts.uk.at.view.kaf018.a.viewmodel.InitDisplayOfApprovalStatus;
	import DisplayWorkplace = nts.uk.at.view.kaf018.a.viewmodel.DisplayWorkplace;
	import ClosureItem = nts.uk.at.view.kaf018.a.viewmodel.ClosureItem;
	import KAF018DParam = nts.uk.at.view.kaf018.d.viewmodel.KAF018DParam;
	import KAF018FParam = nts.uk.at.view.kaf018.f.viewmodel.KAF018FParam;
	import KAF018HParam = nts.uk.at.view.kaf018.h.viewmodel.KAF018HParam;
	import ApprovalStatusMailType = kaf018.share.model.ApprovalStatusMailType;
	import KAF018CParam = nts.uk.at.view.kaf018.c.viewmodel.KAF018CParam;
	
	@bean()
	class Kaf018BViewModel extends ko.ViewModel {
		params: KAF018BParam = null;
		appNameLst: Array<any> = [];
		closureItem: ClosureItem;
		startDate: string;
		endDate: string;
		dataSource: Array<ApprSttExecutionDto> = [];
		selectWorkplaceInfo: Array<DisplayWorkplace> = [];
		initDisplayOfApprovalStatus: InitDisplayOfApprovalStatus = {
			// ページング行数
			numberOfPage: 100,
			// ユーザーID
			userID: '',
			// 会社ID
			companyID: __viewContext.user.companyId,
			// 月別実績の本人確認・上長承認の状況を表示する
			confirmAndApprovalMonthFlg: false,
			// 就業確定の状況を表示する
			confirmEmploymentFlg: false,
			// 申請の承認状況を表示する
			applicationApprovalFlg: false,
			// 日別実績の本人確認・上長承認の状況を表示する
			confirmAndApprovalDailyFlg: false
		};
		pageData: Array<string> = [];
		
		created(params: KAF018BParam) {
			const vm = this;
			vm.params = params;
			vm.$blockui('show');
			vm.initDisplayOfApprovalStatus = params.initDisplayOfApprovalStatus;
			vm.appNameLst = params.appNameLst;
			vm.closureItem = params.closureItem;
			vm.startDate = params.startDate;
			vm.endDate = params.endDate;
			vm.selectWorkplaceInfo = params.selectWorkplaceInfo;
			vm.dataSource = _.map(_.sortBy(vm.selectWorkplaceInfo, 'hierarchyCode'), x => {
				return {
					wkpID: x.id,
					wkpCD: x.code,
					wkpName: x.name,
					hierarchyCode: x.hierarchyCode,
					empPeriodLst: [],
					level: x.level,
					countEmp: null,
					countUnApprApp: null,
					countUnConfirmDay: null,
					countUnApprDay: null,
					countUnConfirmMonth: null,
					countUnApprMonth: null,
					displayConfirm: null,
					confirmPerson: null,
					date: null,
				} 
			});
			$("#bGrid").css('visibility','hidden');
			vm.createIggrid(params.useSet);
		}
		
		loadData(initFlg: boolean) {
			const vm = this;
			vm.$blockui('show');
			let closureId = vm.closureItem.closureId,
				processingYm = vm.closureItem.processingYm,
				startDate = vm.startDate,
				endDate = vm.endDate,
				wkpInfoLst = _.filter(vm.selectWorkplaceInfo, o => _.includes(vm.pageData, o.id)),
				initDisplayOfApprovalStatus = vm.initDisplayOfApprovalStatus,
				employmentCDLst = vm.params.employmentCDLst,
				apprSttComfirmSet = vm.params.useSet,
				wsParam = { closureId, processingYm, startDate, endDate, wkpInfoLst, initDisplayOfApprovalStatus, employmentCDLst, apprSttComfirmSet };
			vm.$ajax('at', API.getStatusExecution, wsParam)
			.then((data: Array<ApprSttExecutionDto>) => {
				_.forEach(vm.dataSource, x => {
					let exist = _.find(data, y => y.wkpID == x.wkpID);
					if(exist) {
						x.wkpCD = exist.wkpCD;
						x.wkpName = exist.wkpName;
						x.empPeriodLst = exist.empPeriodLst;
						x.countEmp = exist.countEmp;
						x.countUnApprApp = exist.countUnApprApp;
						x.countUnConfirmDay = exist.countUnConfirmDay;
						x.countUnApprDay = exist.countUnApprDay;
						x.countUnConfirmMonth = exist.countUnConfirmMonth;
						x.countUnApprMonth = exist.countUnApprMonth;
						x.displayConfirm = exist.displayConfirm;
						x.confirmPerson = exist.confirmPerson;
						x.date = exist.date;
					}
				});
				$("#bGrid").igGrid("option", "dataSource", vm.dataSource);
				$("#bGrid").css('visibility','visible');
				if(initFlg) {
					$(".ui-iggrid").focus();	
				}
			});
		}
		
		createIggrid(useSet: any) {
			const vm = this;
			$("#bGrid").igGrid({
				// width: window.innerWidth - 24 < 1000 ? 1000 : window.innerWidth - 24,
				height: window.innerHeight - 150,
				dataSource: vm.dataSource,
				primaryKey: 'wkpID',
				primaryKeyDataType: 'string',
				rowVirtualization: true,
				virtualization: true,
				virtualizationMode: 'continuous',
				enter: 'right',
				autoFitWindow: false,
				hidePrimaryKey: true,
				avgRowHeight: 25,
				cellClick: (evt: any, ui: any) => {
					vm.cellGridClick(evt, ui); 
				},
				dataRendered: () => {
					vm.$nextTick(() => {
						vm.$blockui('hide');
					});
				},
				rendered: () => {
					if($("#bGrid").css('visibility')=='hidden'){
						vm.$nextTick(() => {
							vm.$blockui('show');
							character.restore('InitDisplayOfApprovalStatus').then((obj: InitDisplayOfApprovalStatus) => {
								if(obj) {
									vm.initDisplayOfApprovalStatus = obj;
									$("#bGrid").igGridPaging("option", "pageSize", vm.initDisplayOfApprovalStatus.numberOfPage);
									if(!vm.initDisplayOfApprovalStatus.applicationApprovalFlg) {
										$("#bGrid").igGrid("hideColumn", "countUnApprApp");
									}
									if(!useSet.usePersonConfirm || !vm.initDisplayOfApprovalStatus.confirmAndApprovalDailyFlg) {
										$("#bGrid").igGrid("hideColumn", "countUnConfirmDay");
									}
									if(!useSet.useBossConfirm || !vm.initDisplayOfApprovalStatus.confirmAndApprovalDailyFlg) {
										$("#bGrid").igGrid("hideColumn", "countUnApprDay");
									}
									if(!useSet.monthlyIdentityConfirm || !vm.initDisplayOfApprovalStatus.confirmAndApprovalMonthFlg) {
										$("#bGrid").igGrid("hideColumn", "countUnConfirmMonth");	
									}
									if(!useSet.monthlyConfirm || !vm.initDisplayOfApprovalStatus.confirmAndApprovalMonthFlg) {
										$("#bGrid").igGrid("hideColumn", "countUnApprMonth");
									}
									if(!useSet.employmentConfirm || !vm.initDisplayOfApprovalStatus.confirmEmploymentFlg) {
										$("#bGrid").igGrid("hideColumn", "displayConfirm");
										$("#bGrid").igGrid("hideColumn", "confirmPerson");
										$("#bGrid").igGrid("hideColumn", "date");
									}
									vm.getPageData();
									vm.loadData(true);
								}
							});
						});
					} else {
						vm.$nextTick(() => {
							vm.$blockui('hide');
						});
					}
			    },
				columns: [
					{ 
						headerText: '', 
						key: 'wkpID', 
						dataType: 'string',
						width: '50px',
						hidden: true
					},
					{ 
						headerText: vm.$i18n('KAF018_331'), 
						key: 'wkpID',
						dataType: 'string',
						width: '550px',
						headerCssClass: 'kaf018-b-header-wkpName',
						columnCssClass: 'kaf018-b-column-wkpName',
						formatter: (key: string) => vm.getWkpInfo(key),
					},
					{ 
						headerText: vm.$i18n('KAF018_332'), 
						key: 'countEmp', 
						dataType: 'number', 
						width: '50px', 
						headerCssClass: 'kaf018-b-header-countEmp',
						columnCssClass: 'kaf018-b-column-countEmp',
						formatter: (key: number) => {
							if(!key) {
								return "";
							}
							return key;
						},
					},
					{ 
						headerText: '<p style="text-align: center">' + vm.$i18n('KAF018_333') + '</p>' + vm.createButtonHtml(0), 
						key: 'countUnApprApp', 
						dataType: 'number', 
						width: '80px',
						headerCssClass: 'kaf018-b-header-countUnApprApp',
						columnCssClass: 'kaf018-b-column-count',
						formatter: (key: number) => {
							if(!key) {
								return "";
							}
							return key;
						},
					},
					{ 
						headerText: vm.$i18n('KAF018_334'),
						group: [
							{ 
								headerText: '<p style="text-align: center">' + vm.$i18n('KAF018_337') + '</p>' + vm.createButtonHtml(1), 
								key: 'countUnConfirmDay', 
								dataType: 'number', 
								width: '78px', 
								columnCssClass: 'kaf018-b-column-count',
								formatter: (key: number) => {
									if(!key) {
										return "";
									}
									return key;
								},
							},
							{ 
								headerText: '<p style="text-align: center">' + vm.$i18n('KAF018_338') + '</p>' + vm.createButtonHtml(2), 
								key: 'countUnApprDay', 
								dataType: 'number', 
								width: '78px', 
								columnCssClass: 'kaf018-b-column-count',
								formatter: (key: number) => {
									if(!key) {
										return "";
									}
									return key;
								},
							}
						]
					},
					{ 
						headerText: vm.$i18n('KAF018_335'),
						group: [
							{ 
								headerText: '<p style="text-align: center">' + vm.$i18n('KAF018_339') + '</p>' + vm.createButtonHtml(5), 
								key: 'countUnConfirmMonth', 
								dataType: 'number', 
								width: '78px', 
								columnCssClass: 'kaf018-b-column-count',
								formatter: (key: number) => {
									if(!key) {
										return "";
									}
									return key;
								},
							},
							{ 
								headerText: '<p style="text-align: center">' + vm.$i18n('KAF018_340') + '</p>' + vm.createButtonHtml(3), 
								key: 'countUnApprMonth', 
								dataType: 'number', 
								width: '78px', 
								columnCssClass: 'kaf018-b-column-count',
								formatter: (key: number) => {
									if(!key) {
										return "";
									}
									return key;
								},
							}
						]
					},
					{ 
						headerText: vm.$i18n('KAF018_336'),
						group: [
							{ 
								headerText: '<p style="text-align: center">' + vm.$i18n('KAF018_341') + '</p>' + vm.createButtonHtml(4), 
								key: 'displayConfirm', 
								dataType: 'boolean', 
								width: '78px', 
								columnCssClass: 'kaf018-b-column-count',
								formatter: (key: boolean, object: any) => {
									if(!object.countEmp) {
										return "";
									}
									if(key) {
										return vm.$i18n('KAF018_345');	
									}
									return vm.$i18n('KAF018_344');	
								},
							},
							{ 
								headerText: '<p style="text-align: center">' + vm.$i18n('KAF018_342') + '</p>', 
								key: 'confirmPerson', 
								dataType: 'string', 
								width: '150px', 
								headerCssClass: 'kaf018-b-header-emp-confirm',
								formatter: (key: string) => {
									if(!key) {
										return "";
									}
									return key;
								},
							},
							{ 
								headerText: '<p style="text-align: center">' + vm.$i18n('KAF018_343') + '</p>', 
								key: 'date', 
								dataType: 'string', 
								width: '100px', 
								headerCssClass: 'kaf018-b-header-emp-confirm',
								formatter: (key: any) => {
									if(!key) {
										return "";
									}
									return moment(key).format('YYYY/MM/DD');
								},
							}
						]
					}	
				],
				features: [
					{
						name: 'MultiColumnHeaders'
					},
					{
						name: "Paging",
						defaultDropDownWidth: 80,
						pageCountLimit : 1,
						pageSize: 100,
						pageSizeList: [
							vm.$i18n('KAF018_327'), 
							vm.$i18n('KAF018_328'), 
							vm.$i18n('KAF018_329'), 
							vm.$i18n('KAF018_330')
						],
						locale: {
							pagerRecordsLabelTooltip: "Current records",
							pageSizeDropDownLabel: vm.$i18n('KAF018_325'),
							pageSizeDropDownTrailingLabel: ""
						},
						pageIndexChanged: () => {
							$(".ui-iggrid").focus();
							vm.getPageData();
							vm.loadData(false);	
						},
						pageSizeChanged: () => {
							vm.initDisplayOfApprovalStatus.numberOfPage = $("#bGrid").igGridPaging("option", "pageSize");
							character.save('InitDisplayOfApprovalStatus', vm.initDisplayOfApprovalStatus).then(() => {
								$(".ui-iggrid").focus();
								vm.getPageData();
								vm.loadData(false);	
							});
						}
					},
				],
			});
		}
		
		getPageData() {
			const vm = this;
			vm.pageData = _.map($('#bGrid').igGrid("allRows"), (o: any) => o.getAttribute("data-id"));
		}
		
		createButtonHtml(mailType: ApprovalStatusMailType) {
			return `<button class="kaf018-b-mailButton" data-bind="click: function() { $vm.buttonMailAction(` + mailType + `); }, text: $i18n('KAF018_346')"></button>`;
		}
		
		getWkpInfo(key: string) {
			const vm = this;
			let currentWkpInfo = _.find(vm.dataSource, o => o.wkpID==key);
			let displayWkpInfo = '';
			for(let i = 1; i < currentWkpInfo.level; i++) {
				displayWkpInfo += '<div style="width: 20px; display: inline-block;"></div>';
			}
			displayWkpInfo += '<div style="display: inline-block;">' + currentWkpInfo.wkpCD + '</div>';
			displayWkpInfo += '<div style="display: inline-block;"><span style="margin-left: 20px;">' + currentWkpInfo.wkpName + '</span></div>';
			return displayWkpInfo;
		}
		
		cellGridClick(evt: any, ui: any) {
			const vm = this;
			if(ui.colKey=="countUnApprApp") {
				let countUnApprApp = _.find(vm.dataSource, o => o.wkpID == ui.rowKey).countUnApprApp;
				if(!countUnApprApp) {
					return;	
				}
				let closureItem = vm.closureItem,
					startDate = vm.startDate,
					endDate = vm.endDate,
					apprSttExeDtoLst = _.filter(vm.dataSource, o => {
						let countUnApprApp = o.countUnApprApp ? true : false;
						return countUnApprApp && _.includes(vm.pageData, o.wkpID);
					}),
					currentWkpID = ui.rowKey,
					appNameLst: Array<any> = vm.appNameLst,
					dParam: KAF018DParam = { closureItem, startDate, endDate, apprSttExeDtoLst, currentWkpID, appNameLst };
				vm.$window.modal('/view/kaf/018/d/index.xhtml', dParam);
			}
			
			if(ui.colKey=="countUnConfirmDay" || ui.colKey=="countUnApprDay" || ui.colKey=="countUnConfirmMonth" || ui.colKey=="countUnApprMonth") {
				let currentWkp = _.find(vm.dataSource, o => o.wkpID == ui.rowKey);
				if(!(currentWkp.countUnConfirmDay || currentWkp.countUnApprDay || currentWkp.countUnConfirmMonth || currentWkp.countUnApprMonth)) {
					return;	
				}
				let closureItem = vm.closureItem,
					startDate = vm.startDate,
					endDate = vm.endDate,
					apprSttExeDtoLst = _.filter(vm.dataSource, o => {
						let count = (o.countUnConfirmDay || o.countUnApprDay || o.countUnConfirmMonth || o.countUnApprMonth) ? true : false;
						return count && _.includes(vm.pageData, o.wkpID);
					}),
					currentWkpID = ui.rowKey,
					apprSttComfirmSet = vm.params.useSet,
					fParam: KAF018FParam = { closureItem, startDate, endDate, apprSttExeDtoLst, currentWkpID, apprSttComfirmSet };
				vm.$window.modal('/view/kaf/018/f/index.xhtml', fParam);
			}
			if(ui.colKey=="displayConfirm") {
				let currentWkp = _.find(vm.dataSource, o => o.wkpID == ui.rowKey);
				if(!currentWkp.countEmp) {
					return;	
				}
				let closureItem = vm.closureItem,
					startDate = vm.startDate,
					endDate = vm.endDate,
					wkpInfo = _.find(vm.dataSource, o => o.wkpID==ui.rowKey),
					displayConfirm = wkpInfo.displayConfirm,
					confirmEmp = wkpInfo.confirmPerson,
					confirmDate = wkpInfo.date,
					hParam: KAF018HParam = { closureItem, startDate, endDate, wkpInfo, displayConfirm, confirmEmp, confirmDate };
				vm.$window.modal('/view/kaf/018/h/index.xhtml', hParam).then((result: any) => {
					if(result) {
						if(result.isActiveConfirm) {
							let closureId = closureItem.closureId,
								processingYm = closureItem.processingYm,
								wkpInfoLst = [_.find(vm.selectWorkplaceInfo, o => o.id==ui.rowKey)],
								afterConfirmParam = {closureId, processingYm, wkpInfoLst};
							vm.$blockui('show');
							vm.$ajax('at', API.getEmploymentConfirmInfoAfter, afterConfirmParam).then((afterConfirmData: any) => {
								_.forEach(vm.dataSource, x => {
									if(x.wkpID!=ui.rowKey) {
										return;	
									}
									if(afterConfirmData) {
										x.displayConfirm = true;
										x.confirmPerson = _.keys(afterConfirmData)[0];
										x.date = _.values(afterConfirmData)[0].toString();
									} else {
										x.displayConfirm = false;
										x.confirmPerson = '';
										x.date = '';
									}
								});
								$("#bGrid").igGrid("option", "dataSource", vm.dataSource);
							});
						}
					}	
				});
			}
		}
		
		buttonMailAction(mailTypeParam: ApprovalStatusMailType) {
			const vm = this;
			let height = window.innerHeight;
			if(window.innerHeight > 820) {
				height = 820
			}
			if(window.innerHeight < 600) {
				height = 600;
			}
			let dialogSize = { width: 900, height: height },
				existData = _.chain(vm.dataSource).filter(o => {
					switch(mailTypeParam) {
						case ApprovalStatusMailType.APP_APPROVAL_UNAPPROVED:
							return o.countUnApprApp;
						case ApprovalStatusMailType.DAILY_UNCONFIRM_BY_PRINCIPAL:
							return o.countUnConfirmDay;
						case ApprovalStatusMailType.DAILY_UNCONFIRM_BY_CONFIRMER:
							return o.countUnApprDay;
						case ApprovalStatusMailType.MONTHLY_UNCONFIRM_BY_PRINCIPAL:
							return o.countUnConfirmMonth;
						case ApprovalStatusMailType.MONTHLY_UNCONFIRM_BY_CONFIRMER:
							return o.countUnApprMonth;
						case ApprovalStatusMailType.WORK_CONFIRMATION:
							return o.countEmp > 0 && !o.displayConfirm;
						default:
							return false;
					}
				}).map(o => o.wkpID).value(),
				mailType = mailTypeParam,
				closureItem = vm.closureItem,
				startDate = vm.startDate,
				endDate = vm.endDate,
				selectWorkplaceInfo = _.filter(vm.selectWorkplaceInfo, o => _.includes(existData, o.id)),
				employmentCDLst = vm.params.employmentCDLst,
				cParam: KAF018CParam = { mailType, closureItem, startDate, endDate, selectWorkplaceInfo, employmentCDLst };
			vm.$window.modal('/view/kaf/018/c/index.xhtml', cParam, dialogSize);
		}
		
		goBackA() {
			const vm = this;
			vm.params.initDisplayOfApprovalStatus = vm.initDisplayOfApprovalStatus;
			vm.$jump('/view/kaf/018/a/index.xhtml', vm.params);
		}
	}
	
	export interface KAF018BParam {
		closureItem: ClosureItem;
		startDate: string;
		endDate: string;
		selectWorkplaceInfo: Array<DisplayWorkplace>;
		appNameLst: Array<any>;
		useSet: any;
		initDisplayOfApprovalStatus: InitDisplayOfApprovalStatus;
		employmentCDLst: Array<string>;
	}

	export interface ApprSttExecutionDto {
		wkpID: string;
		wkpCD: string;
		wkpName: string;
		hierarchyCode: string;
		empPeriodLst: Array<any>;
		level: number;
		countEmp: number;
		countUnApprApp: number;
		countUnConfirmDay: number;
		countUnApprDay: number;
		countUnConfirmMonth: number;
		countUnApprMonth: number;
		displayConfirm: boolean;
		confirmPerson: string;
		date: string;
	}	

	const API = {
		getStatusExecution: "at/request/application/approvalstatus/getStatusExecution",
		getEmploymentConfirmInfoAfter: "at/request/application/approvalstatus/getEmploymentConfirmInfoAfter"
	}
}