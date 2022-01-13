module nts.uk.at.view.kdl009.a {

    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    import block = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;
    import alertError = nts.uk.ui.dialog.alertError;
	import formatById = nts.uk.time.format.byId;

    export module viewmodel {
        export class ScreenModel {
            listEmpId : any;
			itemList : any;
			selectCombobox : KnockoutObservable<any>;
			dataOneEmp : any;
			listDataInfo : KnockoutObservableArray<RemainNumberDetailedInfoDto>;
			employeeNameSelect : KnockoutObservable<string>;
			
			// search
			items: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
			searchText: KnockoutObservable<string> = ko.observable('');

			//kcp
			employeeList: KnockoutObservableArray<UnitModel> = ko.observableArray<UnitModel>([]);
			listComponentOption: any = [];
			selectedCode: KnockoutObservable<string>;
			multiSelectedCode: KnockoutObservableArray<string>;
			isShowAlreadySet: KnockoutObservable<boolean>;
			alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
			isDialog: KnockoutObservable<boolean>;
			isShowNoSelectRow: KnockoutObservable<boolean>;
			isMultiSelect: KnockoutObservable<boolean>;
			isShowWorkPlaceName: KnockoutObservable<boolean>;
			isShowSelectAllButton: KnockoutObservable<boolean>;
			disableSelection: KnockoutObservable<boolean>;
			
			listEmployeeImport: any = [];
			//data table top
			managementCheck: KnockoutObservable<number> =  ko.observable(0);
			currentRemainNumberSelect: KnockoutObservable<string> =  ko.observable("");
			expiredWithinMonthSelect: KnockoutObservable<string> =  ko.observable("");
			dayCloseDeadlineSelect: KnockoutObservable<string> =  ko.observable("");
			
			listDataFull : any;
            constructor(data: any) {
				let self = this;
				self.listEmpId = nts.uk.ui.windows.getShared('KDL009_DATA');
				
				self.itemList = ko.observableArray([
		            new ItemModel('0', getText('KDL009_39')),
		            new ItemModel('1', getText('KDL009_40'))
		        ]);
				self.selectcombobox = ko.observable(0);
				self.dataOneEmp = ko.observable(null);
				self.listDataInfo = ko.observableArray([]);
				self.listDataFull = ko.observableArray([]);
				self.employeeNameSelect = ko.observable("");
				
				self.selectedCode = ko.observable('1');
				self.multiSelectedCode = ko.observableArray(['0', '1', '4']);
				self.isShowAlreadySet = ko.observable(false);
				self.alreadySettingList = ko.observableArray([
					{ code: '1', isAlreadySetting: true },
					{ code: '2', isAlreadySetting: true }
				]);
				self.isDialog = ko.observable(false);
				self.isShowNoSelectRow = ko.observable(false);
				self.isMultiSelect = ko.observable(false);
				self.isShowWorkPlaceName = ko.observable(false);
				self.isShowSelectAllButton = ko.observable(false);
				self.disableSelection = ko.observable(false);
				self.listComponentOption = {
					isShowAlreadySet: self.isShowAlreadySet(),
					isMultiSelect: false,
					listType: 4,
					employeeInputList: self.employeeList,
					selectType: 3,
					selectedCode: self.selectedCode,
					isDialog: self.isDialog(),
					isShowNoSelectRow: self.isShowNoSelectRow(),
					alreadySettingList: self.alreadySettingList,
					isShowWorkPlaceName: self.isShowWorkPlaceName(),
					isShowSelectAllButton: self.isShowSelectAllButton(),
					disableSelection: self.disableSelection(),
					maxRows: 15,
					tabindex: -1

				};
				if (self.listEmpId.length > 1) {
					$("#area-right").show();
				} else {
					$("#area-right").hide();
				}
				$('#kcp005').ntsListComponent(self.listComponentOption);

			
				self.listComponentOption.selectedCode.subscribe((value: any) => {
					let name: any = _.filter(self.employeeList(), (x: any) => {
						return _.isEqual(x.code, value);
					});
					self.selectcombobox('0');
					self.employeeNameSelect(name[0].code + "  " + name[0].name);
					let empId = _.filter(self.listEmployeeImport, (x) => {
						return _.isEqual(x.employeeCode, value)})[0].employeeId;
					self.getHolidayConfByEmp(empId);
				});
				
				self.selectcombobox.subscribe(v =>{
					if (v == null) return;
					self.changeCombobox(v);
				});
				
				
			}
			
			public changeCombobox(value){
				let self = this;
				if(value == '0'){
					self.listDataInfo(self.listDataFull());
				}else{
					let data = _.filter(self.listDataInfo(), (x) => {
						return _.isEqual(x.digestionStatus.substring(0,2), "残り");
					});
					self.listDataInfo(data);
				}
			}
			

            startPage(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                block.grayout();
                $.when(self.getStartHolidayConf(self.listEmpId)).done(function() {
	
					
                    block.clear();
                    dfd.resolve();
                });

                return dfd.promise();
            }

			findData(data: any) {
				let self = this;
				
				let text = $("input.ntsSearchBox.nts-editor.ntsSearchBox_Component").val()
				if (text == "") {
					nts.uk.ui.dialog.info({ messageId: "MsgB_24" });
				} else {
					let lstFil = _.filter(self.employeeList(), (z: any) => {
						return _.includes(z.code, text);
					});
	
					if (lstFil.length > 0) {              
						let index = _.findIndex(lstFil, (z : any) => _.isEqual(z.code,self.listComponentOption.selectedCode()));
                      	if (index == -1 || index == lstFil.length - 1){
                          	self.listComponentOption.selectedCode(lstFil[0].code);
                      	} else {
                          	self.listComponentOption.selectedCode(lstFil[index + 1].code);
                     	}
						let scroll: any = _.findIndex(self.employeeList(), (z: any) => _.isEqual(z.code, self.listComponentOption.selectedCode())),
						id = _.filter($("table > tbody > tr > td > div"), (x: any) => {
							return _.includes(x.id, "_scrollContainer") && !_.includes(x.id, "single-list");
						})
					$("#" + id[0].id).scrollTop(scroll * 24);
					} else {
						nts.uk.ui.dialog.info({ messageId: "MsgB_25" });
					}
				}
			}
			
			cancel() {
				let self = this;
				nts.uk.ui.windows.close();
			}
			public getStartHolidayConf(listEmp): any {
                let self = this;
                let dfd = $.Deferred();
				service.getStartHolidayConf(listEmp).done((data: any) => {
					self.dataOneEmp(data);
					self.listEmployeeImport = data.listEmployeeImport;
					_.forEach(data.listEmployeeImport, (a: any, ind) => {
						self.employeeList.push({ id: ind, code: a.employeeCode, name: a.employeeName })
					});
					dfd.resolve();
                }).fail(function (res: any) {
                    alertError({ messageId: "" });
                });
                return dfd.promise();
            }

			public getHolidayConfByEmp(emp): any {
                let self = this;
                let dfd = $.Deferred();
				service.getHolidayConfByEmp(emp).done((data: any) => {
					self.dataOneEmp().remainNumConfirmDto =data;
					self.listDataInfo([]);
					self.currentRemainNumberSelect(data.currentRemainNumber);
					self.expiredWithinMonthSelect(data.expiredWithinMonth);
					self.dayCloseDeadlineSelect(data.dayCloseDeadline);
					self.managementCheck(data.management);
					if(data !=null && data.detailRemainingNumbers.length>0){
						let temp = _.map(data.detailRemainingNumbers, (a: any) => new RemainNumberDetailedInfoDto(a));
						self.listDataInfo(temp);
						//lấy dữ liệu quá khứ cuối cùng để bôi đen border
						if(self.listDataInfo().length>1){
							for(let i = 1;i<self.listDataInfo().length;i++){
								if(self.listDataInfo()[i-1].isFuture){
									break;
								}
								if(self.listDataInfo()[i].isFuture ){
									self.listDataInfo()[i].isLastPast(true);
									break;
								}
							}
							
						}
						self.listDataFull(self.listDataInfo());
					}
	                //xóa index kcp005
	               	let id = _.filter($("#kcp005 > div > div  "), (x) => {
								return _.includes(x.id, "container") && ! _.includes(x.id, "single-list");
						});
					$("#" + id[0].id).attr('tabindex', -1);
					
					dfd.resolve();
                }).fail(function (res: any) {
                    alertError({ messageId: "" });
                });
                return dfd.promise();
            }

			public changeData(){
				let self = this;
				let listDigestionDate = _.map(_.filter(self.listDataInfo(), (item) => {
					return item.accrualDate != "" && item.digestionDateStatus != "";
				}), a => a.digestionDate.substring(0,10));
				 _.remove(self.listDataInfo(), function(n) {
				  	return listDigestionDate.contains(n.digestionDate.substring(0,10)) && n.accrualDate == "";
				});
			}
        }
    }

	export class ItemModel {
		code: number;
		name: string;
		displayName?: string;
		constructor(code: number, name: string, displayName?: string) {
			this.code = code;
			this.name = name;
			this.displayName = displayName;
		}
	}
	
	
	export interface UnitModel {
		id?: string;
		code: string;
		name?: string;
		workplaceName?: string;
		isAlreadySetting?: boolean;
		optionalColumn?: any;
	}
	
	export class RemainNumberDetailedInfoDto {
        //期限日
		deadline : string;
		//期限日状況
		dueDateStatus : string;
		//消化一覧[0].消化数
		digestionCount : string;
		//消化一覧[0].消化日
		digestionDate : string;
		//消化一覧[0].消化日状況
		digestionDateStatus : string;
		//消化状況
		digestionStatus : string;
		//発生数
		numberOccurrences : string;
		//発生日
		accrualDate : string;
		//発生日状況
		occurrenceDateStatus : string;

		//消化一覧
		listDigestionItem : Array<DigestionItem>;
		
		isFuture : boolean;
		isLastPast: KnockoutObservable<boolean> = ko.observable(false);
		
        constructor(data :any ) {
            let self = this;
			self.listDigestionItem = [];
            self.deadline = data.deadline;
            self.dueDateStatus = data.dueDateStatus;
			if(data.listDigestion.length>0){
				self.digestionCount = data.listDigestion[0].digestionCount;
				self.digestionDate = data.listDigestion[0].digestionDate;
				self.digestionDateStatus = data.listDigestion[0].digestionDateStatus;
				if(data.listDigestion.length>1){
					let listDigestionItem = [];
					for(let i = 1;i<data.listDigestion.length;i++){
						listDigestionItem.push(new DigestionItem(data.listDigestion[i]));
					}
					self.listDigestionItem = listDigestionItem;
				}
			}else{
				self.digestionCount = "";
				self.digestionDate = "";
				self.digestionDateStatus = "";
			}
			
			self.digestionStatus = data.digestionStatus;
			self.numberOccurrences = data.numberOccurrences;
			self.accrualDate = data.accrualDate;
			self.occurrenceDateStatus = data.occurrenceDateStatus;
			
			if(_.isEqual(self.digestionStatus.substring(0,2), "残り")){
				self.dueDateStatus = data.dueDateStatus;
				self.deadline = data.deadline;
			}else{
				self.dueDateStatus = " ";
				self.deadline = " ";
			}
			
			
			if(self.digestionDateStatus == "予" || self.occurrenceDateStatus == "予"){
				self.isFuture = true;
			}else{
				self.isFuture = false;
        	}
    	}
	}
	export class DigestionItem {
		//消化数
		digestionCount : string;
		//消化日
		digestionDate : string;
		//消化日状況
		digestionDateStatus : string;
		constructor(data :any ) {
			let self = this;
			self.digestionCount = data.digestionCount;
			self.digestionDate = data.digestionDate;
			self.digestionDateStatus = data.digestionDateStatus;
		}
	}
}