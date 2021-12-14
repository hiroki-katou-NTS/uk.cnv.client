 /// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kaf018.a.viewmodel {
	import character = nts.uk.characteristics;
	import KAF018BParam = nts.uk.at.view.kaf018.b.viewmodel.KAF018BParam;
	
	@bean()
	class Kaf018AViewModel extends ko.ViewModel {
		appNameLst: Array<any> = [];
		closureLst: KnockoutObservableArray<ClosureItem> = ko.observableArray([]);
		selectedClosureId: KnockoutObservable<number> = ko.observable(0);
		dateValue: KnockoutObservable<any> = ko.observable({});
		applicationApprovalFlg: CheckBoxValue = null;
		confirmAndApprovalDailyFlg: CheckBoxValue = null;
		confirmAndApprovalMonthFlg: CheckBoxValue = null;
		confirmEmploymentFlg: CheckBoxValue = null;
		useSet: any = null;
		initDisplayOfApprovalStatus: InitDisplayOfApprovalStatus = {
			// ページング行数
			numberOfPage: 100,
			// ユーザーID
			userID: __viewContext.user.employeeId,
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
		fullWorkplaceInfo: Array<DisplayWorkplace> = [];
		employmentCDLst: Array<string> = [];
		treeGrid: any;
		multiSelectedWorkplaceId: KnockoutObservableArray<string> = ko.observableArray([]);
		baseDate: KnockoutObservable<Date> = ko.observable(new Date());
		displayUpdMailTmpBtn: boolean = false;
		
		created(params: KAF018BParam) {
			const vm = this;
			vm.displayUpdMailTmpBtn = __viewContext.user.role.isInCharge.attendance;
			vm.applicationApprovalFlg = new CheckBoxValue(false, true, true, vm.$i18n('KAF018_318'));
			vm.confirmAndApprovalDailyFlg = new CheckBoxValue(false, true, true, '');
			vm.confirmAndApprovalMonthFlg = new CheckBoxValue(false, true, true, '');
			vm.confirmEmploymentFlg = new CheckBoxValue(false, true, true, vm.$i18n('KAF018_321'));
			vm.treeGrid = {
				isShowAlreadySet: false,
				isMultipleUse: false,
				isMultiSelect: true,
				treeType: 1,
				selectedId: vm.multiSelectedWorkplaceId,
				baseDate: vm.baseDate,
				selectType: 1,
				isShowSelectButton: true,
				isDialog: true,
				showIcon: true,
				alreadySettingList: ko.observableArray([]),
				maxRows: 15,
				tabindex: 1,
				systemType: 2
			};
			vm.dateValue.subscribe(value => {
				vm.baseDate(new Date(value.endDate));
			});
			vm.applicationApprovalFlg.value.subscribe(value => vm.initDisplayOfApprovalStatus.applicationApprovalFlg = value);
			vm.confirmAndApprovalDailyFlg.value.subscribe(value => vm.initDisplayOfApprovalStatus.confirmAndApprovalDailyFlg = value);
			vm.confirmAndApprovalMonthFlg.value.subscribe(value => vm.initDisplayOfApprovalStatus.confirmAndApprovalMonthFlg = value);
			vm.confirmEmploymentFlg.value.subscribe(value => vm.initDisplayOfApprovalStatus.confirmEmploymentFlg = value);
			vm.$blockui('show');
			vm.$ajax(API.getUseSetting).then((useSetResult: any) => {
				if(useSetResult) {
					if(useSetResult.usePersonConfirm && useSetResult.useBossConfirm) {
						vm.confirmAndApprovalDailyFlg.text(vm.$i18n('KAF018_552'));
					} else if(useSetResult.usePersonConfirm){
						vm.confirmAndApprovalDailyFlg.text(vm.$i18n('KAF018_553'));
					} else {
						vm.confirmAndApprovalDailyFlg.text(vm.$i18n('KAF018_554'));
					}
					if(useSetResult.monthlyIdentityConfirm && useSetResult.monthlyConfirm) {
						vm.confirmAndApprovalMonthFlg.text(vm.$i18n('KAF018_555'));		
					} else if(useSetResult.monthlyIdentityConfirm){
						vm.confirmAndApprovalMonthFlg.text(vm.$i18n('KAF018_556'));
					} else {
						vm.confirmAndApprovalMonthFlg.text(vm.$i18n('KAF018_557'));
					}
					if((useSetResult.usePersonConfirm) || (useSetResult.useBossConfirm)) {
						vm.confirmAndApprovalDailyFlg.display(true);
						vm.confirmAndApprovalDailyFlg.enable(true);
					} else {
						vm.confirmAndApprovalDailyFlg.display(false);
						vm.confirmAndApprovalDailyFlg.enable(false);
					}
					if((useSetResult.monthlyIdentityConfirm) || (useSetResult.monthlyConfirm)) {
						vm.confirmAndApprovalMonthFlg.display(true);
						vm.confirmAndApprovalMonthFlg.enable(true);
					} else {
						vm.confirmAndApprovalMonthFlg.display(false);
						vm.confirmAndApprovalMonthFlg.enable(false);
					}
					if(useSetResult.employmentConfirm) {
						vm.confirmEmploymentFlg.display(true);
						vm.confirmEmploymentFlg.enable(true);
					} else {
						vm.confirmEmploymentFlg.display(false);
						vm.confirmEmploymentFlg.enable(false);
					}
				}
				vm.useSet = useSetResult;
				return character.restore('InitDisplayOfApprovalStatus');
			}).then((obj: InitDisplayOfApprovalStatus) => {
				if(obj) {
					if(obj.applicationApprovalFlg) {
						vm.applicationApprovalFlg.value(true);
					} else {
						vm.applicationApprovalFlg.value(false);
					}
					if(obj.confirmAndApprovalDailyFlg) {
						vm.confirmAndApprovalDailyFlg.value(true);
					} else {
						vm.confirmAndApprovalDailyFlg.value(false);
					}
					if(obj.confirmAndApprovalMonthFlg) {
						vm.confirmAndApprovalMonthFlg.value(true);
					} else {
						vm.confirmAndApprovalMonthFlg.value(false);
					}
					if(obj.confirmEmploymentFlg) {
						vm.confirmEmploymentFlg.value(true);
					} else {
						vm.confirmEmploymentFlg.value(false);
					}
					vm.initDisplayOfApprovalStatus = obj;
				} else {
					vm.applicationApprovalFlg.value(true);
					vm.confirmAndApprovalDailyFlg.value(true);
					vm.confirmAndApprovalMonthFlg.value(true);
					vm.confirmEmploymentFlg.value(true);
				}
				return vm.$ajax(API.getAppNameInAppList);
			}).then((appNameLst: any) => {
				vm.appNameLst = appNameLst;
				return $.Deferred((dfd) => {
					return character.restore(__viewContext.user.employeeId + __viewContext.user.companyId).then((restoreData: any) => {
						let selectedClosureIdCache = null;
						if(restoreData)	{
							selectedClosureIdCache = restoreData.employmentInfo.selectedClosureId;
						}
						dfd.resolve(selectedClosureIdCache);
					});
				});
			}).then((data: any) => {
				return vm.$ajax(API.getApprovalStatusActivation, {selectClosureId: data});
			}).then((data: any) => {
				return $.Deferred((dfd) => {
					vm.closureLst(_.map(data.closureList, (o: any) => {
						return new ClosureItem(
							o.closureHistories[0].closureId, 
							o.closureHistories[0].closeName, 
							o.closureMonth,
							o.closureHistories[0].closureDate.closureDay,
							o.closureHistories[0].closureDate.lastDayOfMonth);
					}));
					vm.employmentCDLst = data.listEmploymentCD;
					if(!_.isEmpty(params)) {
						vm.multiSelectedWorkplaceId(_.map(params.selectWorkplaceInfo, o => o.id));
						vm.selectedClosureId(params.closureItem.closureId);
						vm.dateValue().startDate = params.startDate;
						vm.dateValue().endDate = params.endDate;
						vm.dateValue.valueHasMutated();
						if(params.initDisplayOfApprovalStatus.applicationApprovalFlg) {
							vm.applicationApprovalFlg.value(true);
						} else {
							vm.applicationApprovalFlg.value(false);
						}
						if(params.initDisplayOfApprovalStatus.confirmAndApprovalDailyFlg) {
							vm.confirmAndApprovalDailyFlg.value(true);
						} else {
							vm.confirmAndApprovalDailyFlg.value(false);
						}
						if(params.initDisplayOfApprovalStatus.confirmAndApprovalMonthFlg) {
							vm.confirmAndApprovalMonthFlg.value(true);
						} else {
							vm.confirmAndApprovalMonthFlg.value(false);
						}
						if(params.initDisplayOfApprovalStatus.confirmEmploymentFlg) {
							vm.confirmEmploymentFlg.value(true);
						} else {
							vm.confirmEmploymentFlg.value(false);
						}
						vm.initDisplayOfApprovalStatus = params.initDisplayOfApprovalStatus;
						dfd.resolve();
					} else {
						return character.restore(__viewContext.user.employeeId + __viewContext.user.companyId).then((restoreData: any) => {
							if(restoreData)	{
								vm.selectedClosureId(restoreData.employmentInfo.selectedClosureId);	
							} else {
								vm.selectedClosureId(_.head(vm.closureLst()).closureId);	
							}
							vm.dateValue().startDate = data.startDate;
							vm.dateValue().endDate = data.endDate;
							vm.dateValue.valueHasMutated();
							dfd.resolve();
						})
					}	
				});
			}).then(() => {
				vm.initDisplayOfApprovalStatus.companyID = __viewContext.user.companyId;
				vm.initDisplayOfApprovalStatus.userID = __viewContext.user.employeeId;
				return character.save('InitDisplayOfApprovalStatus', vm.initDisplayOfApprovalStatus);
			}).then(() => {
				return $('#tree-grid').ntsTreeComponent(vm.treeGrid).done(() => {
					vm.fullWorkplaceInfo = vm.flattenWkpTree(_.cloneDeep($('#tree-grid').getDataList()));
					$('#multiple-tree-grid-tree-grid').igTreeGrid("option", "height", "392px");
					vm.selectedClosureId.subscribe((value) => {
						vm.$blockui('show');
						vm.$ajax(`${API.changeClosure}/${value}`).then((changeDateData) => {
							vm.employmentCDLst = changeDateData.listEmploymentCD;
							vm.dateValue().startDate = changeDateData.startDate;
							vm.dateValue().endDate = changeDateData.endDate;
							vm.dateValue.valueHasMutated();	
							$('#tree-grid').ntsTreeComponent(vm.treeGrid).done(() => {
								vm.fullWorkplaceInfo = vm.flattenWkpTree(_.cloneDeep($('#tree-grid').getDataList()));
								$('#multiple-tree-grid-tree-grid').igTreeGrid("option", "height", "392px");
								$('#tree-grid').focusTreeGridComponent();
								vm.$blockui('hide');
							});
						});
					});
				});
			}).then(() => {
				vm.$blockui('hide');
				$('#tree-grid').focusTreeGridComponent();
			});
		}
		
		extraction() {
			const vm = this;
			// 画面情報を保持する。（再表示した際に使用するため）
			
			// アルゴリズム「職場選択チェック」を実行する(thực hiện thuật toán 「職場選択チェック」)
			// 共通部品「KCP004_職場リスト」より選択職場を取得する(get workplace chọn từ commonComponent「KCP004_職場リスト」)
			if(_.isEmpty(vm.multiSelectedWorkplaceId())) {
				// メッセージ（Msg_786）を表示する(hiển thị message（Msg_786）)
				vm.$dialog.error({ messageId: 'Msg_786' });
				return;
			}
			// アルゴリズム「項目選択チェック」を実行する
			// 画面の項目選択チェックボックスを判別する
			if(!(vm.applicationApprovalFlg.value() || vm.confirmAndApprovalDailyFlg.value() || vm.confirmAndApprovalMonthFlg.value() || vm.confirmEmploymentFlg.value())) {
				// メッセージ（Msg_1764）を表示する(hiển thị message（Msg_1764）)
				vm.$dialog.error({ messageId: 'Msg_1764' });
				return;
			}
			// Ｂ画面(承認・確認状況の照会)を実行する
			character.save('InitDisplayOfApprovalStatus', vm.initDisplayOfApprovalStatus).then(() => {
				vm.fullWorkplaceInfo = vm.flattenWkpTree(_.cloneDeep($('#tree-grid').getDataList()));
				let closureItem = _.find(vm.closureLst(), o => o.closureId == vm.selectedClosureId()),
					startDate = vm.dateValue().startDate,
					endDate = vm.dateValue().endDate,
					selectWorkplaceInfo: Array<DisplayWorkplace> = _.chain(vm.fullWorkplaceInfo)
																	.filter((o: DisplayWorkplace) => _.includes(vm.multiSelectedWorkplaceId(), o.id))
																	.sortBy('hierarchyCode').value(),
					appNameLst: Array<any> = vm.appNameLst,
					useSet = vm.useSet,
					initDisplayOfApprovalStatus = vm.initDisplayOfApprovalStatus,
					employmentCDLst = vm.employmentCDLst,
					bParam: KAF018BParam = { closureItem, startDate, endDate, selectWorkplaceInfo, appNameLst, useSet, initDisplayOfApprovalStatus, employmentCDLst };
				vm.$jump("/view/kaf/018/b/index.xhtml", bParam);	
			});
		}

		emailSetting() {
			const vm = this;
			let height = screen.availHeight;
			if(screen.availHeight > 475) {
				height = 475
			}
			if(screen.availHeight < 400) {
				height = 400;
			}
			let dialogSize = {
				width: 910,
				height: height
			}
			vm.$window.modal('/view/kaf/018/i/index.xhtml', {}, dialogSize);
		}
		
		flattenWkpTree(wkpTree: Array<DisplayWorkplace>) {
            return wkpTree.reduce((wkp, x) => {
                wkp = wkp.concat(x);
                if (x.children && x.children.length > 0) {
                    wkp = wkp.concat(this.flattenWkpTree(x.children));
                    x.children = [];
                }
                return wkp;
            }, []);
        }
	}

	export class ClosureItem {
		closureId: number; 
		closureName: string;
		processingYm: number;
		closureDay: number;
		lastDayOfMonth: boolean;
		
		constructor(closureId: number, closureName: string, processingYm: number, closureDay: number, lastDayOfMonth: boolean) {
			this.closureId = closureId;
			this.closureName = closureName;
			this.processingYm = processingYm;
			this.closureDay = closureDay;
			this.lastDayOfMonth = lastDayOfMonth;
		}
	}
	
	// 承認状況照会の初期表示
	export class InitDisplayOfApprovalStatus {
		// ページング行数
		numberOfPage: number;
		// ユーザーID
		userID: string;
		// 会社ID
		companyID: string;
		// 月別実績の本人確認・上長承認の状況を表示する
		confirmAndApprovalMonthFlg: boolean;
		// 就業確定の状況を表示する
		confirmEmploymentFlg: boolean;
		// 申請の承認状況を表示する
		applicationApprovalFlg: boolean;
		// 日別実績の本人確認・上長承認の状況を表示する
		confirmAndApprovalDailyFlg: boolean;
	}
	
	export interface DisplayWorkplace {
		code: string;
		id: string;
		name: string;
		hierarchyCode: string;
		level: number;
		children: Array<DisplayWorkplace>;
	}
	
	export class CheckBoxValue {
		value: KnockoutObservable<boolean>;
		display: KnockoutObservable<boolean>;
		enable: KnockoutObservable<boolean>;
		text: KnockoutObservable<string>;
		constructor(value: boolean, display: boolean, enable: boolean, text: string) {
			this.value = ko.observable(value);
			this.display = ko.observable(display);
			this.enable = ko.observable(enable);
			this.text = ko.observable(text);
		}
	}

	const API = {
		getAppNameInAppList: "at/request/application/screen/applist/getAppNameInAppList",
		getApprovalStatusActivation: "at/request/application/approvalstatus/getApprovalStatusActivation",
		getUseSetting: "at/record/application/realitystatus/getUseSetting",
		changeClosure: "at/request/application/approvalstatus/changeClosure"
	}
}