module nts.uk.at.view.kdl051.a {

    import getShared = nts.uk.ui.windows.getShared;
    import setShared = nts.uk.ui.windows.setShared;
    import block = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;
    import alertError = nts.uk.ui.dialog.alertError;
	import formatById = nts.uk.time.format.byId;

    export module viewmodel {
        export class ScreenModel {
            listEmpId : any;
			dataOneEmp : any;
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
			managementCheck: KnockoutObservable<number> =  ko.observable(2);
			currentRemainNumberSelect: KnockoutObservable<string> =  ko.observable("");
			expiredWithinMonthSelect: KnockoutObservable<string> =  ko.observable("");
			dayCloseDeadlineSelect: KnockoutObservable<string> =  ko.observable("");
			
			//table bottom
			items: KnockoutObservableArray<DigestionDetailsDto>;
        	columns: KnockoutObservableArray<NtsGridListColumn>;
			currentCode: KnockoutObservable<any>;
			
			listDataFull : any;
            constructor(data: any) {
				let self = this;
				self.listEmpId = nts.uk.ui.windows.getShared('KDL051A_PARAM');
				
				
				self.dataOneEmp = ko.observable(null);
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
					self.employeeNameSelect(name[0].code + "　" + name[0].name);
					let empId = _.filter(self.listEmployeeImport, (x) => {
						return _.isEqual(x.employeeCode, value)})[0].employeeId;
					self.getDeitalInfoNursingByEmp(empId);
				});
				
				
				//table bottom
				self.items = ko.observableArray([]);
            	self.currentCode = ko.observable();
                // self.items.push(new DigestionDetailsDtoTest("0.5日","2021/09/23（木）　" ,'')); 
				// self.items.push(new DigestionDetailsDtoTest("1.0日","2021/09/24（金）　" ,''));
				// self.items.push(new DigestionDetailsDtoTest("1.0日","2021/09/26（日）　" ,''));
	            // self.items.push(new DigestionDetailsDtoTest("0.5日","2021/10/05（火）　" ,'予'));
				// self.items.push(new DigestionDetailsDtoTest("1.0日","2021/10/20（水）　" ,'予'));
	            
	            self.columns = ko.observableArray([
					{ headerText: '', key: 'digestionStatus', width: 200,hidden: true } ,
	                { headerText: getText('KDL051_25'), key: 'digestionDate', width: 200,formatter: function (digestionDate, record) {
                        return "<div style='margin-left: 5px;display: flex;'><div style='width: 20px;' >"+record.digestionStatus.toString()+"</div> <div style='width: 155px;float:right;'> " + digestionDate + " </div></div>";   
                	} }, 
	                { headerText: getText('KDL051_26'), key: 'numberOfUse', width: 100,formatter: v => {
                    	return '<div style="margin-left: 10px;">' + v + '</div>';
                    } } 
	            ]); 
				
			}

            startPage(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                block.grayout();
                self.getChildNursingLeave(self.listEmpId).done(function() {
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
			public getChildNursingLeave(listEmp): JQueryPromise<any>  {
                let self = this;
                let dfd = $.Deferred();
				service.getChildNursingLeave(listEmp).done((data: any) => {
					self.dataOneEmp(data);
					self.listEmployeeImport = data.lstEmployee;
					_.forEach(data.lstEmployee, (a: any, ind) => {
						self.employeeList.push({ id: ind, code: a.employeeCode, name: a.employeeName })
					});
					
					dfd.resolve();
                }).fail(function (res: any) {
                    alertError({ messageId: "" });
                });
                return dfd.promise();
            }

			public getDeitalInfoNursingByEmp(emp) : JQueryPromise<any>  {
                let self = this;
                let dfd = $.Deferred();
				self.managementCheck(2);
				service.getDeitalInfoNursingByEmp(emp).done((data: any) => {
					self.dataOneEmp(data);
					self.items([]);
					self.managementCheck(data.managementSection?1:0);
					let temp = _.sortBy(data.listDigestionDetails, [function(o) { return o.digestionDate; }]);
					_.forEach(temp, (a: any) => {
						self.items.push(new DigestionDetailsDto(a));
					});
					
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
	
	export class DigestionDetailsDto {
        //使用数
		numberOfUse : string;
		//消化日
		digestionDate : string;
		//消化状況
		digestionStatus : string;
		
        constructor(data :any ) {
            let self = this;
            self.numberOfUse = data.numberOfUse;
		    self.digestionDate = data.digestionDate;
			self.digestionStatus = data.digestionStatus;
    	}
	}
	
	export class DigestionDetailsDtoTest {
        //使用数
		numberOfUse : string;
		//消化日
		digestionDate : string;
		//消化状況
		digestionStatus : string;
		
        constructor(numberOfUse : string,
					digestionDate : string,
					digestionStatus : string ) {
            let self = this;
            self.numberOfUse = numberOfUse;
		    self.digestionDate = digestionDate;
			self.digestionStatus = digestionStatus;
    	}
	}
}