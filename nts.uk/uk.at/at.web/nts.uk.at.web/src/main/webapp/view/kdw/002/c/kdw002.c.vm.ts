module nts.uk.at.view.kdw002.c {
    import alert = nts.uk.ui.dialog.alert;
    export module viewmodel {
        import href = nts.uk.request.jump;
        import getText = nts.uk.resource.getText;
        import infor = nts.uk.ui.dialog.info;
        export class ScreenModel {
            // A4_1
            listAttdItem: Array<any>;
            dailyServiceTypeControl: KnockoutObservable<DailyAttendanceItemAuth>;
            columns: KnockoutObservableArray<any>;;
            //monthly
            listAttdMonthlyItem: Array<any>;
            // A2_1
            bussinessCodeItems: KnockoutObservableArray<BusinessType>;
            bussinessColumn: KnockoutObservableArray<any>;
            currentRoleId: KnockoutObservable<any>;
            // ?
            txtSearch: KnockoutObservable<string>;
            //isDaily
            isDaily :boolean;
            sideBar :  KnockoutObservable<number>;
            // A3_1
            datasources :KnockoutObservableArray<any>;
            selectedList: any;
            
            listAttFullData  : any;
            listAttFullDataClone  : any;
            // ver7
            roleItems: KnockoutObservableArray<EmployeeRoleDto> = ko.observableArray([]);
            // isNew mode
            isNewMode:  KnockoutObservable<boolean> = ko.observable(false);

            constructor(dataShare:any) {
                var self = this;
                self.bussinessCodeItems = ko.observableArray([]);
                self.listAttdItem = [];
                self.dailyServiceTypeControl = ko.observable(null);
                self.isDaily = dataShare === undefined ? false : dataShare.ShareObject;
                self.sideBar =  ko.observable(1);
                //
                self.datasources = ko.observableArray([]);
                self.selectedList = ko.observableArray([]);
                
                self.listAttFullData = ko.observableArray([]);
                self.listAttFullDataClone = ko.observableArray([]);
                //monthly
                self.listAttdMonthlyItem = [];

                this.bussinessColumn = ko.observableArray([
                    { headerText: 'ID', key: 'roleId', width: 100, hidden: true },
                    { headerText: getText('KDW002_12'), key: 'roleCode', width: 70 },
                    { headerText: getText('KDW002_4'), key: 'roleName', width: 230, formatter: _.escape },
                ]);

                self.currentRoleId = ko.observable('');
                self.currentRoleId.subscribe(roleId => {
                    self.currentRoleId(roleId);
                    _.defer(() => nts.uk.ui.block.invisible());

                    var useHeader = `<input style="vertical-align: middle;" tabindex='-1' type='checkbox' id = 'useCheckAll' onclick='useHeaderChanged(this)'/> `;
                    var youCanChangeItHeader =  `<input style="vertical-align: middle;" tabindex='-1' type='checkbox' id = 'youCanCheckAll' onclick='youCanChangeItHeaderChanged(this)'/> `;
                    var canBeChangedByOthersHeader = `<input style="vertical-align: middle;" tabindex='-1'  type='checkbox' id = 'otherCheckAll' onclick='canBeChangedByOthersHeaderChanged(this)'/> `;
                    // A4_2, A4_3, A4_4, A4_41, A4_8, A4_8.1, A4_9, A4_10, A4_11, A4_16, A4_18
                    self.columns = ko.observableArray([
                        { headerText: '', key: 'itemDailyID', width: 1, hidden: true },
                        { headerText: getText('KDW002_3'), key: 'displayNumber', width: 70, formatter: _.escape },
                        { headerText: getText('KDW002_4'), key: 'itemDailyName', width: 220  , 
										formatter: (keyValue: any) => { return '<div>' + _.escape(keyValue) + '</div>'} },
                        { headerText: useHeader + getText('KDW002_5'), key: 'toUse', width: 120, 
										formatter: (keyValue: any, itemValue: DisplayAndInputControl) => self.getUseTemplate(keyValue, itemValue) },
                        { headerText: youCanChangeItHeader + getText('KDW002_6'), key: 'youCanChangeIt', width: 120, columnCssClass: 'column-style',
										formatter: (keyValue: any, itemValue: DisplayAndInputControl) => self.getYouCanChangeItTemplate(keyValue, itemValue) },
                        { headerText: canBeChangedByOthersHeader + getText('KDW002_7'), key: 'canBeChangedByOthers', width: 165, columnCssClass: 'column-style',
										formatter: (keyValue: any, itemValue: DisplayAndInputControl) => self.getCanBeChangedByOthersTemplate(keyValue, itemValue) },
                        { headerText: '', key: 'userCanUpdateAtr', width: 1, hidden: true },
                    ]);


                    let dfd:any;
                    if(roleId){
                        if(self.isDaily){
                             dfd = self.getDailyAttdItemByRoleID(roleId);

                        }else{
                             dfd = self.getMonthlyAttdItemByRoleID(roleId)
                        }
                    }
                    $.when(dfd).done(
                        (DailyServiceTypeControls) => {
                            
                            $('#useCheckAll').prop('checked', false);
                            $('#youCanCheckAll').prop('checked', false);
                            $('#otherCheckAll').prop('checked', false);
                            if (!nts.uk.util.isNullOrUndefined(DailyServiceTypeControls)) {
                                self.datasources(self.dailyServiceTypeControl().displayAndInput);
                                $("#grid").igGrid({
                                    primaryKey: "itemDailyID",
                                    height: window.innerHeight - 280,
                                    dataSource: ko.mapping.toJSON(self.datasources()),
                                    autoGenerateColumns: false,
                                    //alternateRowStyles: false,
                                    autoAdjustHeight: false,
                                    autoFormat: false,
                                    alternateRowStyles: false,
                                    dataSourceType: "json",
                                    autoCommit: true,
                                    tabIndex: -1,
                                    //virtualization: true,
                                    rowVirtualization: false,
                                  virtualizationMode: "continuous",
//                                    virtualizationMode: "fixed",
									dataRendered: () => {
										nts.uk.ui.block.clear();
									},
									rendered: () => {
								   		nts.uk.ui.block.clear();
								    },
                                    columns: self.columns(),
                                    features: [
                                        {
                                            name: "Updating",
                                            showDoneCancelButtons: false,
                                            enableAddRow: false,
                                            enableDeleteRow: false,
                                            editMode: 'cell',
                                            columnSettings: [
                                                { columnKey: "itemDailyID", hidden: true },
                                                { columnKey: "itemDailyName", readOnly: true },
                                                { columnKey: "toUse", readOnly: true },
                                                { columnKey: "youCanChangeIt", readOnly: true },
                                                { columnKey: "canBeChangedByOthers", readOnly: true },
                                                { columnKey: "userCanUpdateAtr", hidden: true }
                                            ]
                                        },
                                        {
                                            name: "Selection",
                                            mode: "row",
                                            multipleSelection: false,
                                            touchDragSelect: false, // this is true by default
                                            multipleCellSelectOnClick: false
                                            
                                        }
                                    ],
                                    avgRowHeight: "26px"
                                });
                                 $("#grid").setupSearchScroll("igGrid", false);
                                 $("#grid").ntsGridList("setupScrollWhenBinding"); 
                            }
                            let listData = self.dailyServiceTypeControl().displayAndInput;
                            let notUseExist = _.find(listData, function(e){ return !e.toUse });
                            if(nts.uk.util.isNullOrUndefined(notUseExist)){
                                $("#useCheckAll").prop('checked', true);
                            }else{
                                $("#useCheckAll").prop('checked', false);
                            }
                            
                            let notYouCanCheckAll = true;
                            for(let i =0;i<listData.length;i++){
                                if(listData[i].toUse ==true && listData[i].userCanUpdateAtr == 1){
                                    if(!listData[i].youCanChangeIt){
                                        notYouCanCheckAll =false;
                                        break;
                                    }
                                }
                            }
                            $("#youCanCheckAll").prop('checked', notYouCanCheckAll);
                            
                            let notOtherCheckAll = true;
                            for(let i =0;i<listData.length;i++){
                                if(listData[i].toUse ==true && listData[i].userCanUpdateAtr == 1){
                                    if(!listData[i].canBeChangedByOthers){
                                        notOtherCheckAll =false;
                                        break;
                                    }
                                }
                            }
                            $("#otherCheckAll").prop('checked', notOtherCheckAll);
                            
                            //nts.uk.ui.block.clear();
                        }
                    );
                });

                self.txtSearch = ko.observable("");
            }

			getUseTemplate(keyValue: any, itemValue: DisplayAndInputControl) {
				let s = ``;
				s += `<div style="margin-left: -2px; margin-right: -2px; padding-left: 2px; padding-right: 2px; color: transparent;">`;
				if(itemValue.toUse) {
					s+= `<div class="test-div"><input tabindex='-1' type='checkbox' checked `;
				} else {
					s+= `<div class="test-div"><input tabindex='-1' type='checkbox' `;
				}
				s += `onclick='useChanged(this, ${itemValue.itemDailyID},${itemValue.userCanUpdateAtr})' /></div>`;
				s += `</div>`;
				return s;
			}
			
			getYouCanChangeItTemplate(keyValue: any, itemValue: DisplayAndInputControl) {
				let s = ``;
				let disable = !itemValue.userCanUpdateAtr || !itemValue.toUse;
				if(disable) {
					s += `<div style="position: absolute; top: 0; color: transparent; margin-left: -2px;" class="readOnlyColorIsUse">名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名</div>`;
				} else {
					s += `<div style="position: absolute; top: 0; color: transparent; margin-left: -2px;">名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名</div>`;
				}
				s += `<div style="margin-left: -2px; margin-right: -2px; padding-left: 2px; padding-right: 2px; color: transparent; position: inherit;"`;
				if(itemValue.youCanChangeIt) {
					s+= `<div class="test-div"><input tabindex='-1' type='checkbox' checked `;
				} else {
					s+= `<div class="test-div"><input tabindex='-1' type='checkbox' `;
				}
				if(disable) {
					s += ` disabled `;
				} else {
					s += ``;
				}
				s += `onclick='youCanChangeItChanged(this, ${itemValue.itemDailyID})' /></div>`;
				s += `</div>`;
				return s;
			}
			
			getCanBeChangedByOthersTemplate(keyValue: any, itemValue: DisplayAndInputControl) {
				let s = ``;
				let disable = !itemValue.userCanUpdateAtr || !itemValue.toUse;
				if(disable) {
					s += `<div style="position: absolute; top: 0; color: transparent; margin-left: -2px;" class="readOnlyColorIsUse">名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名</div>`;
				} else {
					s += `<div style="position: absolute; top: 0; color: transparent; margin-left: -2px;">名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名</div>`;
				}
				s += `<div style="margin-left: -2px; margin-right: -2px; padding-left: 2px; padding-right: 2px; color: transparent; position: inherit;"`;
				if(itemValue.canBeChangedByOthers) {
					s += `<div class="test-div"><input tabindex='-1' type='checkbox' checked `;
				} else {
					s += `<div class="test-div"><input tabindex='-1' type='checkbox' `;
				}
				if(disable) {
					s += ` disabled `;
				} else {
					s += ``;
				}
				s += `onclick='canBeChangedByOthersChanged(this, ${itemValue.itemDailyID})' /></div>`;
				s += `</div>`;
				return s;
			}
			
			getColumnClass(keyValue: any, itemValue: DisplayAndInputControl) {
				let disable = !itemValue.userCanUpdateAtr || !itemValue.toUse;
				if(disable) {
					return "readOnlyColorIsUse";
				} else {
					return "";
				}
			}
            
            jumpToHome(): void {
                let self = this;
                nts.uk.request.jump("/view/kdw/006/a/index.xhtml");
            }
            
            copy(): void {
                let self = this
                // ver7: get the config role
                let empRole: EmployeeRoleDto = _.find(self.roleItems(), e => e.roleId == self.currentRoleId());
                // isDaily
                if(self.isDaily){
                    service.getDailytRolesByCid().done((roleIDs: Array<any>) => {
                        let param = {
                            code: empRole.roleCode,
                            name: empRole.roleName,
                            targetType: 8,
                            itemListSetting: roleIDs ? roleIDs : [],
                            roleType: 3
                        };
                        console.log(param, 'param');        
                        nts.uk.ui.windows.setShared("CDL023Input", param);
                        nts.uk.ui.windows.sub.modal("com", "/view/cdl/023/a/index.xhtml").onClosed(() => {
                            let data: Array<string>  = nts.uk.ui.windows.getShared("CDL023Output");
                            if(data){
                                let command: any = {};
                                command.roleID = self.currentRoleId();
                                command.destinationList = data;
                                nts.uk.ui.block.invisible();
                                service.copyDailyAttd(command).done(() => {
                                    nts.uk.ui.dialog.info({ messageId: 'Msg_15' }).then(() => {        
                                        
                                    });
                                }).always(() => {
                                    //nts.uk.ui.block.clear();
                                });
                            }                               
                        });
                    });
                } else {
                    service.getMonthlytRolesByCid().done((roleIDs: Array<any>) => {
                        let param = {
                            code: empRole.roleCode,
                            name: empRole.roleName,
                            targetType: 8,
                            itemListSetting: roleIDs ? roleIDs : [],
                            roleType: 3
                        };
                        console.log(param, 'param');        
                        nts.uk.ui.windows.setShared("CDL023Input", param);
                        nts.uk.ui.windows.sub.modal("com", "/view/cdl/023/a/index.xhtml").onClosed(() => {
                            let data: Array<string>  = nts.uk.ui.windows.getShared("CDL023Output");
                            if(data){
                                let command: any = {};
                                command.roleID = self.currentRoleId();
                                command.destinationList = data;
                                nts.uk.ui.block.invisible();
                                service.copyMonthlyAttd(command).done(() => {
                                    nts.uk.ui.dialog.info({ messageId: 'Msg_15' }).then(() => {        

                                    });
                                }).always(() => {
                                    //nts.uk.ui.block.clear();
                                });
                            }                              
                        });
                    });
                }
            }
            


            startPage(): JQueryPromise<any> {
                let self = this,
                    typeInput = TypeInput.DAILY;
                let dfd = $.Deferred();
                 if(!self.isDaily){
                    typeInput = TypeInput.MONTHLY;
                } 
                let dtdGetNameAttItemByType = self.getNameAttItemByType(typeInput);
                $.when(dtdGetNameAttItemByType).done(() => {
                    service.getEmpRole().done(empRoles => {
                        if (!nts.uk.util.isNullOrUndefined(empRoles) && empRoles.length > 0) {
                            self.roleItems(_.sortBy(empRoles, ['roleCode']));
                            let bussinessCodeItems: Array<any> = [];
                            empRoles.forEach((empRole: any)  => {
                                bussinessCodeItems.push(new BusinessType(empRole));
                                //   self.bussinessCodeItems.push(new BusinessType(businessType));
                            });
                            var bTypes = _.sortBy(bussinessCodeItems, 'roleCode');
                            self.bussinessCodeItems(bTypes);
                            var businessTypeCode = bTypes[0].roleId;
                            self.currentRoleId(businessTypeCode);
                        }
                        dfd.resolve();
                        $("#submitDataId").focus();
                    });
                }).always(() => {
                    dfd.resolve();
                })
                
                return dfd.promise();
            }

            getNameAttItemByType(typeInput : number){
                let self = this;
                let dfd = $.Deferred();
                service.getNameAttItemByType(typeInput).done(function(data) {
                    self.listAttFullData(data);
                    dfd.resolve();
                });
                return dfd.promise();
            }

            //get Daily Attd Item By Role ID
            // do something
            getDailyAttdItemByRoleID(roleID: string) {
                let self = this;
                let dfd = $.Deferred();
                let startTime: number = performance.now();
                service.getDailyAttItemNew(roleID).done(function(data) {
                    if (nts.uk.util.isNullOrUndefined(data) || data.length <= 0 || data.every((att: any) => att.authority == null)){
                        self.isNewMode(true);
                    } else {
                        self.isNewMode(false);
                    }
                    
                    console.log("get setting by role: " + (performance.now() - startTime));
                    let listDefault: Array<DisplayAndInputControl> = [];
                    self.listAttFullDataClone(_.cloneDeep(self.listAttFullData()));
                    _.each(self.listAttFullDataClone(), attFullData => {
                        for(let i=0;i<data.length;i++){
                            if(attFullData.attendanceItemId == data[i].attendanceItemId){
                                attFullData.authority = data[i].authority; 
                                break;
                            }    
                        }
                        listDefault.push(DisplayAndInputControl.fromApp(attFullData));
                    });
                    self.dailyServiceTypeControl(
                        new DailyAttendanceItemAuth("", roleID, _.sortBy(listDefault, ['displayNumber']))
                    );
                    console.log("convert object: " + (performance.now() - startTime));
                    dfd.resolve(self.dailyServiceTypeControl());
                });
                return dfd.promise();
            }
            
            //get monthly Attd Item By Role ID
            getMonthlyAttdItemByRoleID(roleID: string) {
                let self = this;
                let dfd = $.Deferred();
                service.getMontlyAttItemNew(roleID).done(function(data) {
                    if (nts.uk.util.isNullOrUndefined(data) || data.length <= 0 || data.every((att: any) => att.authority == null)) {
                        self.isNewMode(true);
                    } else {
                        self.isNewMode(false);
                    }
                    let listDefault: Array<DisplayAndInputControl> = [];
                    self.listAttFullDataClone(_.cloneDeep(self.listAttFullData()));
                    _.each(self.listAttFullDataClone(), attFullData => {
                        for(let i=0;i<data.length;i++){
                            if(attFullData.attendanceItemId == data[i].attendanceItemId){
                                attFullData.authority = data[i].authority;
                                break;
                            }    
                        }
                            
                        listDefault.push(DisplayAndInputControl.fromApp(attFullData));
                    });
                    self.dailyServiceTypeControl(
                        new DailyAttendanceItemAuth("", roleID, _.sortBy(listDefault, ['displayNumber']))
                    );
                    
                    dfd.resolve(self.dailyServiceTypeControl());
                });
                return dfd.promise();
            }

            submitData(): void {
                var self = this;
                var dataSource = $('#grid').data('igGrid').dataSource;
                self.dailyServiceTypeControl().displayAndInput = dataSource.transformedData('afterfilteringandpaging');
                nts.uk.ui.block.invisible();
                if(self.isDaily){
                    service.updateDailyAttdItem(self.dailyServiceTypeControl()).done(x => {
                        nts.uk.ui.dialog.info({ messageId: 'Msg_15' }).then(function() {
                            self.getDailyAttdItemByRoleID(self.dailyServiceTypeControl().authorityDailyId).done(function(){
                                $("#grid").igGrid("destroy")
                                self.currentRoleId(self.dailyServiceTypeControl().authorityDailyId);
                                self.currentRoleId.valueHasMutated();
                            });
                            
                        });
                    }).always(function() {
                        //nts.uk.ui.block.clear();
                    });
                }else{
                    let dataMonthly = dataSource.transformedData('afterfilteringandpaging');
                    let listDisplayMonthly = [];
                    if(dataMonthly.length >0){
                        for(let i = 0;i<dataMonthly.length;i++){
                            listDisplayMonthly.push(
                                new DisplayAndInputMontly(
                                    dataMonthly[i].itemDailyID,
                                    dataMonthly[i].itemDailyName,
                                    dataMonthly[i].displayNumber,
                                    dataMonthly[i].userCanUpdateAtr,
                                    dataMonthly[i].toUse,
                                    dataMonthly[i].canBeChangedByOthers,
                                    dataMonthly[i].youCanChangeIt
                                    )
                                );
                        }
                    }
                    
                    let monthly = new MonthlyAttendanceItemAuth("",self.dailyServiceTypeControl().authorityDailyId,listDisplayMonthly);
                    service.updateMonthlyAttdItem(monthly).done(x => {
                        nts.uk.ui.dialog.info({ messageId: 'Msg_15' }).then(function() {
                            self.getMonthlyAttdItemByRoleID(monthly.authorityMonthlyId).done(function(){
                            ///var temp = $('#grid').igGrid("option","dataSource", self.dailyServiceTypeControl().displayAndInput)
                            $("#grid").igGrid("destroy")
                            self.currentRoleId(monthly.authorityMonthlyId);
                            self.currentRoleId.valueHasMutated();
                            });
                        });
                    }).always(function() {
                        //nts.uk.ui.block.clear();
                    });
                    
                }
            }

            navigateView(): void {
                let self = this;
                var path = "/view/kdw/006/a/index.xhtml";
                href(path);
            }

            searchData(): void {
                var self = this;
                if (self.txtSearch().length === 0) {
                    alert("検索キーワードを入力してください");
                    return;
                }
                var dataSource = $('#grid').data('igGrid').dataSource;
                var filteredData = dataSource.transformedData('afterfilteringandpaging');
                var rowIndexSelected = $('#grid').igGridSelection("selectedRow");
                var index = 0;
                if (typeof (rowIndexSelected) != 'undefined' && rowIndexSelected != null) {
                    if ((rowIndexSelected.index + 1) == filteredData.length) {
                        index = 0;
                    } else {
                        index = rowIndexSelected.index + 1;
                    }
                }
                var keynotExist = true;
                var i;
                var l = filteredData.length;
                for (i = index; i < l; i++) {
                    var rowId = filteredData[i].itemDailyID;
                    var nameValue = filteredData[i].attendanceItemName;
                    var idValue = rowId.toString();

                    if (_.includes(idValue, self.txtSearch()) || _.includes(nameValue, self.txtSearch())) {
                        $('#grid').igGridSelection('selectRow', i);
                        $('#grid').igGrid("virtualScrollTo", i);
                        keynotExist = false;
                        break;
                    }
                }
                if (keynotExist) {
                    for (i = 0; i < index; i++) {
                        var rowId = filteredData[i].itemDailyID;
                        var nameValue = filteredData[i].attendanceItemName;
                        var idValue = rowId.toString();
                        if (_.includes(idValue, self.txtSearch()) || _.includes(nameValue, self.txtSearch())) {
                            $('#grid').igGridSelection('selectRow', i);
                            $('#grid').igGrid("virtualScrollTo", i);
                            keynotExist = false;
                            break;
                        }
                    }
                }
                if (keynotExist) {
                    alert("該当する項目が見つかりませんでした");
                }
            }
        }

        interface IBusinessType {
            roleId: string;
            roleName: string;
            roleCode: string;
        }

        class BusinessType {
            roleId: string;
            roleCode: string;
            roleName: string;
            constructor(param: IBusinessType) {
                let self = this;
                self.roleId = param.roleId;
                self.roleCode = param.roleCode;
                self.roleName = param.roleName;
            }
        }
        interface IDailyServiceTypeControl {
            attendanceItemId: number;
            authorityId: string;
            youCanChangeIt: boolean;
            canBeChangedByOthers: boolean;
            use: boolean
        }

        class DailyServiceTypeControl {
            attendanceItemId: number;
            authorityId: string;
            youCanChangeIt: boolean;
            canBeChangedByOthers: boolean;
            use: boolean
            constructor(param: IDailyServiceTypeControl) {
                let self = this;
                self.attendanceItemId = param.attendanceItemId;
                self.authorityId = param.authorityId;
                self.youCanChangeIt = param.youCanChangeIt;
                self.canBeChangedByOthers = param.canBeChangedByOthers;
                self.use = param.use;
            }
        }
        //class daily
        class DailyAttendanceItemAuth {
            companyID: string;
            authorityDailyId: string;
            displayAndInput: Array<DisplayAndInputControl>;
            constructor(companyID: string, authorityDailyId: string,
                displayAndInput: Array<DisplayAndInputControl>) {
                this.companyID = companyID;
                this.authorityDailyId = authorityDailyId;
                this.displayAndInput = displayAndInput;
            };
        }
        class DisplayAndInputControl {
            itemDailyID: number;
            itemDailyName: string;
            displayName: string;
            displayNumber: number;
            userCanUpdateAtr: number;
            toUse: boolean;
            canBeChangedByOthers: boolean;
            youCanChangeIt: boolean;
            constructor(){
            }
            static fromApp(app) {
                let dto = new DisplayAndInputControl();
                dto.itemDailyID = app.attendanceItemId;
                    dto.itemDailyName = app.attendanceItemName;
                dto.displayNumber = app.attendanceItemDisplayNumber;
                dto.userCanUpdateAtr = app.userCanUpdateAtr;
                if (app.authority != null) {
                    dto.toUse = app.authority.toUse;
                    dto.canBeChangedByOthers = app.authority.canBeChangedByOthers;
                    dto.youCanChangeIt = app.authority.youCanChangeIt;
                } else {
                    dto.toUse = true;
                    dto.canBeChangedByOthers = false;
                    dto.youCanChangeIt = false;
                }
                return dto;
            }
        }
        //monthly
        //
        class MonthlyAttendanceItemAuth {
            companyID: string;
            authorityMonthlyId: string;
            listDisplayAndInputMonthly: Array<DisplayAndInputMontly>;
            constructor(companyID: string, authorityMonthlyId: string,
                listDisplayAndInputMonthly: Array<DisplayAndInputMontly>) {
                this.companyID = companyID;
                this.authorityMonthlyId = authorityMonthlyId;
                this.listDisplayAndInputMonthly = listDisplayAndInputMonthly;
            };
        }
        class DisplayAndInputMontly {
            itemMonthlyId: number;
            itemMontlyName: string;
            displayNumber: number;
            userCanUpdateAtr: number;
            toUse: boolean;
            canBeChangedByOthers: boolean;
            youCanChangeIt: boolean;
            constructor(itemMonthlyId: number,
                itemMontlyName: string,
                displayNumber: number,
                userCanUpdateAtr: number,
                toUse: boolean,
                canBeChangedByOthers: boolean,
                youCanChangeIt: boolean) {
                this.itemMonthlyId = itemMonthlyId;
                this.itemMontlyName = itemMontlyName;
                this.displayNumber = displayNumber;
                this.userCanUpdateAtr = userCanUpdateAtr;
                this.toUse = toUse;
                this.canBeChangedByOthers = canBeChangedByOthers;
                this.youCanChangeIt = youCanChangeIt;

            }

        }
            
        class EmployeeRoleDto {
            roleId: string;
            roleCode: string;
            roleName: string;
            constructor(roleId: string, roleCode: string, roleName: string) {
                this.roleId = roleId;
                this.roleCode = roleCode;
                this.roleName = roleName;
            }
        }

    }
    enum TypeInput {
        
        DAILY = 1,
        
        MONTHLY = 2
        
    }
}
function useChanged(element, rowId, userCanSet) {
    //update lai element
    
    var value = $("#grid").igGrid("getCellValue", rowId, "toUse");
    if ($("#grid").igGridUpdating('isEditing')) {
        $("#grid").igGridUpdating('endEdit', true);
    }
    $("#grid").igGridUpdating("setCellValue", rowId, "toUse", value != true);
    
    let temp = $('#grid').data('igGrid').dataSource._data;
    let notUseExist = _.find(temp, function(e){ return !e.toUse });
    if(nts.uk.util.isNullOrUndefined(notUseExist)){
        $("#useCheckAll").prop('checked', true);
    }else{
        $("#useCheckAll").prop('checked', false);
    }
}


function youCanChangeItChanged(element, rowId) {
    var value = $("#grid").igGrid("getCellValue", rowId, "youCanChangeIt");
    if ($("#grid").igGridUpdating('isEditing')) {
        $("#grid").igGridUpdating('endEdit', true);
    }
    $("#grid").igGridUpdating("setCellValue", rowId, "youCanChangeIt", value != true);
    
    let temp = $('#grid').data('igGrid').dataSource._data;
    let notYouCanCheckAll = true;
    for(let i =0;i<temp.length;i++){
        if(temp[i].toUse ==true && temp[i].userCanUpdateAtr == 1){
            if(!temp[i].youCanChangeIt){
                notYouCanCheckAll =false;
                break;
            }
        }
    }
    $("#youCanCheckAll").prop('checked', notYouCanCheckAll);
    
    
    
   
}
function canBeChangedByOthersChanged(element, rowId) {
    var value = $("#grid").igGrid("getCellValue", rowId, "canBeChangedByOthers");
    if ($("#grid").igGridUpdating('isEditing')) {
        $("#grid").igGridUpdating('endEdit', true);
    }
    $("#grid").igGridUpdating("setCellValue", rowId, "canBeChangedByOthers", value != true);
    
    
    let temp = $('#grid').data('igGrid').dataSource._data;
    let notOtherCheckAll = true;
    for(let i =0;i<temp.length;i++){
        if(temp[i].toUse ==true && temp[i].userCanUpdateAtr == 1){
            if(!temp[i].canBeChangedByOthers){
                notOtherCheckAll =false;
                break;
            }
        }
    }
    $("#otherCheckAll").prop('checked', notOtherCheckAll);
}

function userCanSetChanged(element, rowId) {
    var value = $("#grid").igGrid("getCellValue", rowId, "userCanSet");
    if ($("#grid").igGridUpdating('isEditing')) {
        $("#grid").igGridUpdating('endEdit', true);
    }
    $("#grid").igGridUpdating("setCellValue", rowId, "userCanSet", value != true);
}

function useHeaderChanged(element) {
	nts.uk.ui.block.grayout();
	var dataSource = $('#grid').data('igGrid').dataSource._data;
	if(element.checked) {
		_.forEach(dataSource, item => {
			item.toUse = true;
		});
	} else {
		_.forEach(dataSource, item => {
			item.toUse = false;
		});	
	}
	$("#grid").igGrid("option", "dataSource", dataSource);
}


function youCanChangeItHeaderChanged(element) {
	nts.uk.ui.block.grayout();
	var dataSource = $('#grid').data('igGrid').dataSource._data;
	if(element.checked) {
		_.forEach(dataSource, item => {
			if(item.userCanUpdateAtr==1 && item.toUse) {
				item.youCanChangeIt = true;
			}
		});
	} else {
		_.forEach(dataSource, item => {
			if(item.userCanUpdateAtr==1 && item.toUse) {
				item.youCanChangeIt = false;
			}
		});	
	}
	$("#grid").igGrid("option", "dataSource", dataSource);
}

function canBeChangedByOthersHeaderChanged(element) {
	nts.uk.ui.block.grayout();
	var dataSource = $('#grid').data('igGrid').dataSource._data;
	if(element.checked) {
		_.forEach(dataSource, item => {
			if(item.userCanUpdateAtr==1 && item.toUse) {
				item.canBeChangedByOthers = true;
			}
		});
	} else {
		_.forEach(dataSource, item => {
			if(item.userCanUpdateAtr==1 && item.toUse) {
				item.canBeChangedByOthers = false;
			}
		});	
	}
	$("#grid").igGrid("option", "dataSource", dataSource);
}


