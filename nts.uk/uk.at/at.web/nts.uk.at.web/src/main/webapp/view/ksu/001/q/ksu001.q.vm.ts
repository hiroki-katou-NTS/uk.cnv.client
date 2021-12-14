/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.at.view.ksu001.q {
	import blockUI = nts.uk.ui.block;
	import getShared = nts.uk.ui.windows.getShared;
	import setShared = nts.uk.ui.windows.setShared;
	import InitialStartupScreenResultDto = service.model.InitialStartupScreenResultDto;

	export module viewmodel {
		export class ScreenModel {
			organizationName: KnockoutObservable<string> = ko.observable('');
			selectItemCode: KnockoutObservable<string> = ko.observable('');
			externalBudgetItems: KnockoutObservableArray<string> = ko.observableArray([]);
			externalBudgetModel: KnockoutObservable<ExternalBudgetModel>;
			targetData: any = {};
			budgetData: any = {};
			listperiods: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
			selectedPeriods: KnockoutObservable<string>;
			labelQ32: KnockoutObservable<string> = ko.observable('');
			columns: KnockoutObservableArray<any>;
			EndStatus: string;
			getDaysArray: any;
			daylist: any;
			arrayDate: any;
			listBudgetDaily: any = {};
			numbereditor: any = {};
			yearmontheditor: any;
			currencyeditor: any;
			listperiodsTemp: any = [];
			check: KnockoutObservable<boolean>;
			check1: KnockoutObservable<boolean>;
			check2: KnockoutObservable<boolean>;
			check3: KnockoutObservable<boolean>;
			constructor() {
				var self = this;
				self.selectedPeriods = ko.observable("");
				self.externalBudgetModel = ko.observable(new ExternalBudgetModel());
				let target = getShared("target");
				let period = getShared("period");
				self.targetData.unit = target.unit;
				self.targetData.id = target.id;
				self.targetData.endDate = period.endDate;
				self.budgetData.unit = target.unit;
				self.budgetData.id = target.id;
				self.budgetData.startDate = period.startDate;
				self.budgetData.endDate = period.endDate;
				self.EndStatus = 'cancel';
				self.check = ko.observable(false);
				self.check1 = ko.observable(false);
				self.check2 = ko.observable(false);
				self.check3 = ko.observable(false);
				self.selectItemCode.subscribe(function(codeEB) {
					if (codeEB) {
						self.budgetData.itemCode = codeEB;
						self.listperiodsTemp = [];						
						self.arrayDate.forEach((x) => {
							self.listperiodsTemp.push(new ItemModel(x, ''));							
						});		
						let size = self.arrayDate.length ;
						while (size < 10){
							self.listperiodsTemp.push(new ItemModel('', ''));
							size++;
						}			
						self.listperiods.removeAll();
						blockUI.invisible();
						self.loadFindBudgetDaily(self.budgetData).done(() => {		
							nts.uk.ui.errors.clearAll();			
							self.listperiods(self.listperiodsTemp);
							var externalBudget = self.externalBudgetModel().externalBudgetItems().filter(x => { return x.code === codeEB })[0].attribute;
							switch (externalBudget) {
								case "時間":
									self.labelQ32(nts.uk.resource.getText('KSU001_3707'));
									self.check(true);
									self.check1(false);
									self.check2(false);
									self.check3(false);
									break;
								case "金額":
									self.labelQ32(nts.uk.resource.getText('KSU001_3708'));
									self.check(false);
									self.check1(true);
									self.check2(false);
									self.check3(false);
									break;
								case "人数":
									self.labelQ32(nts.uk.resource.getText('KSU001_3709'));
									self.check(false);
									self.check1(false);
									self.check2(true);
									self.check3(false);
									break;
								case "数値":
									self.labelQ32(nts.uk.resource.getText('KSU001_3710'));
									self.check(false);
									self.check1(false);
									self.check2(false);
									self.check3(true);
									break;
							};
							
							// $("table tbody tr td:nth-child(1)").css("background-color", "#D9D9D9");
							$("table tbody tr td:nth-child(1):contains(土)").css("background-color", "#8bd8ff");
							$("table tbody tr td:nth-child(1):contains(日)").css("background-color", "#fabf8f");
							$("table tbody tr td:nth-child(1)").css("color", "#404040");
							$("table tbody tr td:nth-child(1):contains(土)").css("color", "#0000ff");
							$("table tbody tr td:nth-child(1):contains(日)").css("color", "#ff0000");
						}).always(function(){
							blockUI.clear();
							nts.uk.ui.errors.clearAll();
						});
					}
				});

				self.getDaysArray = function(start, end) {					
					let arr = [];
					// let datePlus = new Date(end);		
					for (let dt = new Date(start); dt <= end; dt.setDate(dt.getDate() + 1)) {
						arr.push(new Date(dt));
					}					
					return arr;
				};

				self.daylist = self.getDaysArray(new Date(period.startDate), new Date(period.endDate));
				self.arrayDate = self.daylist.map((v) => {
					return moment(v).format("YYYY/MM/DD") + '  (' + moment(v).format('dd') + ')';
				});

				self.numbereditor = {
					option: new nts.uk.ui.option.NumberEditorOption({
						grouplength: 3,
						decimallength: 0
					}),
				}
				self.yearmontheditor = {
					option: ko.mapping.fromJS(new nts.uk.ui.option.TimeEditorOption({
						inputFormat: 'time'
					})),
				};
				self.currencyeditor = {
					option: new nts.uk.ui.option.CurrencyEditorOption({
						grouplength: 3,
						currencyposition:"right",	
						currencyformat: "JPY"
					}),
				};

			}
			// External budget actual amount
			private loadFindBudgetDaily(data: any): JQueryPromise<void> {
				let self = this;
				var dfd = $.Deferred();							
				service.findBudgetDaily(data).done(function(items: any) {
					if (items) {						
						self.listBudgetDaily = items;
						items.forEach(item => {
							// self.listperiods.push(item);
							self.listperiodsTemp.map((x) => {
								if (x.date().slice(0, 10) == item.date) {
									return x.value(item.value.toString());
								}
							})
						});
					}

					dfd.resolve();
				}).always(function(){
					self.clearError();
				});
				return dfd.promise();
			}

			public startPage(): JQueryPromise<void> {
				let self = this;
				var dfd = $.Deferred();		
				blockUI.invisible();		
				service.findExtBudget(self.targetData).done(function(ExtBudget: any) {
					if (ExtBudget.externalBudgetItems.length == 0) {
						nts.uk.ui.dialog.error({ messageId: "Msg_1917" }).then(function(){
							self.closeDialog();
						});						
					} else {
						self.externalBudgetModel().updateData(ExtBudget);
						self.organizationName(ExtBudget.orgName);
						self.selectItemCode(self.externalBudgetModel().externalBudgetItems()[0].code);
					}
					blockUI.clear();
					dfd.resolve();
				});
				blockUI.clear();
				return dfd.promise();
			}

			public closeDialog(): void {
				let self = this;
				setShared('EndStatus', self.EndStatus);
				nts.uk.ui.windows.close();
			}

			public register() {
				let self = this;			
				let command: any = {};
				let requestBudget = {};
				if(self.validateAll()){
					return;
				}	
				command.unit = self.targetData.unit;
				command.id = self.targetData.id;
				command.itemCode = self.selectItemCode();
				var dateValues = [];				
				self.listperiods().forEach((x) => {
					if(x.date() != ''){
						dateValues.push({
							date: x.date().slice(0, 10),
							value: x.value()
						});
					}					
				});
				command.dateAndValues = dateValues;				
				command.type = self.labelQ32();

				requestBudget.id = command.id;
				requestBudget.unit = command.unit;
				requestBudget.itemCode = command.itemCode;
				requestBudget.startDate = dateValues[0].date;
				requestBudget.endDate = dateValues[dateValues.length -1].date;
				service.register(command).done(() => {
					blockUI.invisible();
					nts.uk.ui.dialog.info({ messageId: "Msg_15" });	
					self.loadFindBudgetDaily(requestBudget);
					self.EndStatus = 'update';					
					blockUI.clear();					
				}).always (function() {
					self.clearError();
					blockUI.clear();
				});
			}

			private validateAll(): boolean {
				$('#extBudgetTime').ntsEditor('validate');
				$('#extBudgetMoney').ntsEditor('validate');
				$('#extBudgetNumberPerson').ntsEditor('validate');
				$('#extBudgetNumericalVal').ntsEditor('validate');
                if (nts.uk.ui.errors.hasError()) {                    
                    return true;
                }
                return false;
            }

			private clearError(): void {
                $('#extBudgetTime').ntsError('clear');
                $('#extBudgetMoney').ntsError('clear');
				$('#extBudgetNumberPerson').ntsError('clear');
				$('#extBudgetNumericalVal').ntsError('clear');
				$(".nts-input").ntsError("clear");
            }
		}

		export class ExternalBudgetModel {
			orgName: KnockoutObservable<string>;
			externalBudgetItems: KnockoutObservableArray<any>;

			constructor() {
				this.orgName = ko.observable('');
				this.externalBudgetItems = ko.observableArray([]);
			}
			updateData(dto: InitialStartupScreenResultDto) {
				this.orgName(dto.orgName);
				this.externalBudgetItems(dto.externalBudgetItems);
			}
		}
		export class ItemModel {
			date: KnockoutObservable<string> = ko.observable('');
			value: KnockoutObservable<string> = ko.observable('');
			constructor(date: string, value: string) {
				this.date(date);
				this.value(value);
			}
		}
	}
}