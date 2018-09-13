module nts.uk.at.view.kdw001.c {
    import getText = nts.uk.resource.getText;
    import Ccg001ReturnedData = nts.uk.com.view.ccg.share.ccg.service.model.Ccg001ReturnedData;
    import EmployeeSearchDto = nts.uk.com.view.ccg.share.ccg.service.model.EmployeeSearchDto;
    import GroupOption = nts.uk.com.view.ccg.share.ccg.service.model.GroupOption;

    export module viewmodel {
        export class ScreenModel {

            //Declare screenName flag to forward screen B or screen C
            screenName: string;

            //Declare kcp005 list properties
            listComponentOption: any;
            selectedCode: KnockoutObservable<string>;
            multiSelectedCode: KnockoutObservableArray<string>;
            isShowAlreadySet: KnockoutObservable<boolean>;
            alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
            isDialog: KnockoutObservable<boolean>;
            isShowNoSelectRow: KnockoutObservable<boolean>;
            isMultiSelect: KnockoutObservable<boolean>;
            isShowWorkPlaceName: KnockoutObservable<boolean>;
            isShowSelectAllButton: KnockoutObservable<boolean>;
            employeeList: KnockoutObservableArray<UnitModel>;

            //Declare time range input
            enable: KnockoutObservable<boolean>;
            required: KnockoutObservable<boolean>;
            dateValue: KnockoutObservable<any>;
            startDateString: KnockoutObservable<string>;
            endDateString: KnockoutObservable<string>;
            // startDate for validate
            startDateValidate: KnockoutObservable<string>;

            //Declare employee filter component
            ccg001ComponentOption: any;
            showinfoSelectedEmployee: KnockoutObservable<boolean>;
            // Options
            baseDate: KnockoutObservable<Date>;
            selectedEmployee: KnockoutObservableArray<EmployeeSearchDto>;

            //close period
            periodStartDate: any;

            closureId: KnockoutObservable<any> = ko.observable(1);

            constructor() {

                var self = this;

                //Get screenName value from a screen
                __viewContext.transferred.ifPresent(data => {
                    self.screenName = data.screenName;
                });

                //Init kcp005 properties
                self.baseDate = ko.observable(new Date());
                self.selectedCode = ko.observable(null);
                self.multiSelectedCode = ko.observableArray([]);
                self.isShowAlreadySet = ko.observable(false);
                self.alreadySettingList = ko.observableArray([
                    { code: '1', isAlreadySetting: true },
                    { code: '2', isAlreadySetting: true }
                ]);
                self.isDialog = ko.observable(true);
                self.isShowNoSelectRow = ko.observable(false);
                self.isMultiSelect = ko.observable(false);
                self.isShowWorkPlaceName = ko.observable(true);
                self.isShowSelectAllButton = ko.observable(true);

                this.employeeList = ko.observableArray<UnitModel>([]);

                self.listComponentOption = {
                    isShowAlreadySet: self.isShowAlreadySet(),
                    isMultiSelect: true,
                    listType: ListType.EMPLOYEE,
                    employeeInputList: self.employeeList,
                    selectType: SelectType.SELECT_BY_SELECTED_CODE,
                    selectedCode: self.multiSelectedCode,
                    isDialog: self.isDialog(),
                    isShowNoSelectRow: self.isShowNoSelectRow(),
                    alreadySettingList: self.alreadySettingList,
                    isShowWorkPlaceName: self.isShowWorkPlaceName(),
                    isShowSelectAllButton: false
                };



                //Init time range input
                self.enable = ko.observable(true);
                self.required = ko.observable(true);

                let today = new Date;
                self.dateValue = ko.observable({});
                self.dateValue().startDate = "2017/11/08";
                self.dateValue().endDate = today;
                self.startDateValidate = ko.observable("");


                //var closureID = __viewContext.transferred.value.closureID;
                //self.closureId = ko.observable(1);
                let closureID = '1';
                service.findPeriodById(Number(closureID)).done((data) => {
                    self.startDateValidate = data.startDate;
                    self.periodStartDate = data.startDate.toString();
                    self.dateValue().startDate = data.startDate.toString();
                    self.dateValue().endDate = data.endDate.toString();
                    self.dateValue.valueHasMutated();
                    self.reloadCcg001();
                    $('#ccgcomponent').focus();
                    $('#ccgcomponent').ntsGroupComponent(self.ccg001ComponentOption);
                }).always(() => {
                    self.startDateString = ko.observable("");
                    self.endDateString = ko.observable("");

                    self.startDateString.subscribe(function(value) {
                        self.dateValue().startDate = value;
                        self.dateValue.valueHasMutated();
                    });

                    self.endDateString.subscribe(function(value) {
                        self.dateValue().endDate = value;
                        self.dateValue.valueHasMutated();
                    });
                    self.reloadCcg001();
                    $('#ccgcomponent').focus();
                    $('#ccgcomponent').ntsGroupComponent(self.ccg001ComponentOption);
                });

                self.closureId.subscribe(function(value) {
                    service.findPeriodById(Number(value)).done((data) => {
                        self.startDateValidate = data.startDate;
                        self.periodStartDate = data.startDate.toString();
                        self.dateValue().startDate = data.startDate.toString();
                        self.dateValue().endDate = data.endDate.toString();
                        self.dateValue.valueHasMutated();
//                        self.reloadCcg001();
//                        $('#ccgcomponent').focus();
//                        $('#ccgcomponent').ntsGroupComponent(self.ccg001ComponentOption);
                    }).always(() => {
                        self.startDateString = ko.observable("");
                        self.endDateString = ko.observable("");

                        self.startDateString.subscribe(function(value) {
                            self.dateValue().startDate = value;
                            self.dateValue.valueHasMutated();
                        });

                        self.endDateString.subscribe(function(value) {
                            self.dateValue().endDate = value;
                            self.dateValue.valueHasMutated();
                        });
//                        self.reloadCcg001();
//                        $('#ccgcomponent').focus();
//                        $('#ccgcomponent').ntsGroupComponent(self.ccg001ComponentOption);
                    });
                });
                
//                if(self.dateValue().startDate != "2017/11/08" && self.dateValue().endDate != today){
//                    
//                    self.dateValue.subscribe(function(value){
//                        self.reloadCcg001();
//                        $('#ccgcomponent').focus();
//                        $('#ccgcomponent').ntsGroupComponent(self.ccg001ComponentOption);
//                    });
//                }

                //                self.dateValue.subscribe(function(value){
                //                    if(self.startDateValidate() != "" && (value.startDate < self.startDateValidate())) {
                //                        $('#daterangepicker').ntsError('set', {messageId:"Msg_1349"});
                //                    } else {
                //                        $('#daterangepicker').ntsError('clear');   
                //                    }
                //                });


                //Init employee filter component
                self.selectedEmployee = ko.observableArray([]);
                self.showinfoSelectedEmployee = ko.observable(false);
                self.baseDate = ko.observable(new Date());



            }

            public reloadCcg001(): void {
                let self = this;
                if ($('.ccg-sample-has-error').ntsError('hasError')) {
                    return;
                }
                //                if (!self.showBaseDate() && !self.showClosure() && !self.showPeriod()){
                //                    nts.uk.ui.dialog.alertError("Base Date or Closure or Period must be shown!" );
                //                    return;
                //                }
                self.ccg001ComponentOption = {
                    /** Common properties */
                    systemType: 2, // ã·ã¹ã åºå
                    showEmployeeSelection: false, // æ¤ç´¢ã¿ã¤ã
                    showQuickSearchTab: true, // ã¯ã¤ã¯æ¤ç´¢
                    showAdvancedSearchTab: true, // è©³ç´°æ¤ç´¢
                    showBaseDate: false, // åºæºæ¥å©ç¨
                    showClosure: true, // å°±æ¥­ç·ãæ¥å©ç¨
                    showAllClosure: false, // å¨ç·ãè¡¨ç¤º
                    showPeriod: true, // å¯¾è±¡æéå©ç¨
                    periodFormatYM: false, // å¯¾è±¡æéç²¾åº¦

                    /** Required parameter */
                    baseDate: moment().toISOString(), // åºæºæ¥
                    periodStartDate: self.dateValue().startDate,
                    periodEndDate: self.dateValue().endDate,
                    inService: true, // å¨è·åºå
                    leaveOfAbsence: true, // ä¼èåºå
                    closed: true, // ä¼æ¥­åºå
                    retirement: false, // éè·åºå

                    /** Quick search tab options */
                    showAllReferableEmployee: true, // åçå¯è½ãªç¤¾å¡ãã¹ã¦
                    showOnlyMe: true, // èªå ã
                    showSameWorkplace: true, // åãè·å ´ã®ç¤¾å¡
                    showSameWorkplaceAndChild: true, // åãè·å ´ã¨ããéä¸ãç¤¾å¡

                    /** Advanced search properties */
                    showEmployment: false, // é¨æ¡ä»¶
                    showWorkplace: true, // è·å ´æ¡ä»¶
                    showClassification: true, // å¡æ¡ä»¶
                    showJobTitle: true, // è·ä½æ¡ä»¶
                    showWorktype: true, // å¤ç¨®æ¡ä»¶
                    isMutipleCheck: true, // é¸æã¢ã¼ã

                    /** Return data */
                    returnDataFromCcg001: function(data: Ccg001ReturnedData) {
                        self.showinfoSelectedEmployee(true);
                        self.selectedEmployee(data.listEmployee);
                        self.closureId(data.closureId);

                        //Convert list Object from server to view model list
                        let items = _.map(data.listEmployee, item => {
                            return new UnitModel(item);
                        });
                        self.employeeList(items);

                        //Fix bug 42, bug 43
                        let selectList = _.map(data.listEmployee, item => {
                            return item.employeeCode;
                        });
                        self.multiSelectedCode(selectList);
                    }
                }

                // Start component
                //                $('#ccgcomponent').ntsGroupComponent(self.ccg001ComponentOption);
            }

            opendScreenBorJ() {
                let self = this;
                var closureID = '1';
                if (self.dateValue().startDate < self.startDateValidate) {
                    //                    $('#daterangepicker  input[id$=-startInput],#daterangepicker  input[id$=-endInput]'').ntsError('clear');
                    nts.uk.ui.dialog.alertError({ messageId: "Msg_1349" });
                    return;
                }
                if (!nts.uk.ui.errors.hasError()) {
                    service.findPeriodById(Number(self.closureId())).done((data) => {
                        if (data) {
                            let listEmpSelected = self.listComponentOption.selectedCode();
                            if (listEmpSelected == undefined || listEmpSelected.length <= 0) {
                                nts.uk.ui.dialog.alertError({ messageId: "Msg_206" });
                                return;
                            }
                            let startDateS = self.dateValue().startDate.split("/");
                            let endDateS = self.dateValue().endDate.split("/");
                            let startDate = new Date(startDateS[0], startDateS[1], startDateS[2]);
                            let endDate = new Date(endDateS[0], endDateS[1], endDateS[2]);
                            let startDate_unixtime = parseInt(startDate.getTime() / 1000);
                            let endDate_unixtime = parseInt(endDate.getTime() / 1000);
                            var timeDifference = endDate_unixtime - startDate_unixtime;
                            var timeDifferenceInHours = timeDifference / 60 / 60;
                            var timeDifferenceInDays = timeDifferenceInHours / 24;

                            if (timeDifferenceInDays > 31) {
                                nts.uk.ui.dialog.confirm('å¯¾è±¡æéããæãè¶ã¦ã¾ããããããã§ãã).ifYes(() => {
                                    let yearPeriodStartDate = self.periodStartDate.split("/")[0];
                                    let monthPeriodStartDate = self.periodStartDate.split("/")[1];
                                    let dayPeriodStartDate = self.periodStartDate.split("/")[2];
                                    let yearStartDate = Number(self.dateValue().startDate.split("/")[0]);
                                    let monthStartDate = Number(self.dateValue().startDate.split("/")[1]);
                                    let dayStartDate = Number(self.dateValue().startDate.split("/")[2]);
                                    if (yearStartDate < yearPeriodStartDate || monthStartDate < monthPeriodStartDate || dayStartDate < dayPeriodStartDate) {
                                        nts.uk.ui.dialog.alertError('ç·ãåçéããéå»ã®æ¥ä»ãæ®ã§ãã¾ãã');
                                        return;
                                    }


                                    let listEmpSelectedId = [];
                                    _.forEach(self.selectedEmployee(), function(value) {
                                        if (_.includes(listEmpSelected, value.employeeCode)) {
                                            listEmpSelectedId.push(value.employeeId);
                                        }
                                    });




                                    __viewContext["viewmodel"].params.setParamsScreenC({
                                        closureID: self.closureId(),
                                        lstEmployeeID: listEmpSelectedId,
                                        periodStartDate: self.dateValue().startDate,
                                        periodEndDate: self.dateValue().endDate
                                    });
                                    $("#wizard").ntsWizard("next").done(function() {
                                        $('#checkBox1').focus();
                                    });

                                })

                            } else {
                                let monthNow = data.month; // thieu thang hien tai cua  domain ç·ã
                                let monthStartDate = Number(self.dateValue().startDate.split("/")[1]);
                                if (monthStartDate < monthNow) {
                                    nts.uk.ui.dialog.alertError('ç·ãåçéããéå»ã®æ¥ä»ãæ®ã§ãã¾ãã');
                                    return;
                                }

                                let listEmpSelectedId = [];
                                _.forEach(self.selectedEmployee(), function(value) {
                                    if (_.includes(listEmpSelected, value.employeeCode)) {
                                        listEmpSelectedId.push(value.employeeId);
                                    }
                                });

                                __viewContext["viewmodel"].params.setParamsScreenC({
                                    closureID: self.closureId(),
                                    lstEmployeeID: listEmpSelectedId,
                                    periodStartDate: self.dateValue().startDate,
                                    periodEndDate: self.dateValue().endDate
                                });
                                $("#wizard").ntsWizard("next").done(function() {
                                    $('#checkBox1').focus();
                                });

                            }
                        }
                    });
                }//end if

            }

            start() {
                var self = this;
                $('#ccgcomponent').focus();
                $('#ccgcomponent').ntsGroupComponent(self.ccg001ComponentOption);
                $('#component-items-list').ntsListComponent(self.listComponentOption);
                $('#ccgcomponent').attr('tabindex',1);
                $("#com-kcp-searchbox *").attr('tabindex', -1);
                $("table").attr('tabindex', 4);
                $('#ccg001-btn-search-drawer').focus();
            }

        }

        //Object for kcp005
        export class ListType {
            static EMPLOYMENT = 1;
            static Classification = 2;
            static JOB_TITLE = 3;
            static EMPLOYEE = 4;
        }

        export class UnitModel {
            code: string;
            name: string;
            workplaceName: string;
            isAlreadySetting: boolean;
            constructor(x: EmployeeSearchDto) {
                let self = this;
                if (x) {
                    self.code = x.employeeCode;
                    self.name = x.employeeName;
                    self.workplaceName = x.workplaceName;
                    self.isAlreadySetting = false;
                } else {
                    self.code = "";
                    self.name = "";
                    self.workplaceName = "";
                    self.isAlreadySetting = false;
                }
            }
        }

        export class SelectType {
            static SELECT_BY_SELECTED_CODE = 1;
            static SELECT_ALL = 2;
            static SELECT_FIRST_ITEM = 3;
            static NO_SELECT = 4;
        }

        export interface UnitAlreadySettingModel {
            code: string;
            isAlreadySetting: boolean;
        }

        //Object for filter component        
        export interface GroupOption {
            baseDate?: KnockoutObservable<Date>;
            // ã¯ã¤ã¯æ¤ç´¢ã¿ã
            isQuickSearchTab: boolean;
            // åçå¯è½ãªç¤¾å¡ãã¹ã¦
            isAllReferableEmployee: boolean;
            //èªå ã
            isOnlyMe: boolean;
            //ããªãé¨éã®ç¤¾å¡
            isEmployeeOfWorkplace: boolean;
            //ããªãï¼éä¸é¨éã®ç¤¾å¡
            isEmployeeWorkplaceFollow: boolean;


            // è©³ç´°æ¤ç´¢ã¿ã
            isAdvancedSearchTab: boolean;
            //è¤°é¸æ
            isMutipleCheck: boolean;

            //ç¤¾å¡æ®ã¿ã¤ãor å¨ç¤¾å¡ã¿ã¤ã
            isSelectAllEmployee: boolean;

            onSearchAllClicked: (data: EmployeeSearchDto[]) => void;

            onSearchOnlyClicked: (data: EmployeeSearchDto) => void;

            onSearchOfWorkplaceClicked: (data: EmployeeSearchDto[]) => void;

            onSearchWorkplaceChildClicked: (data: EmployeeSearchDto[]) => void;

            onApplyEmployee: (data: EmployeeSearchDto[]) => void;
        }

        export interface EmployeeSearchDto {
            employeeId: string;

            employeeCode: string;

            employeeName: string;

            workplaceCode: string;

            workplaceId: string;

            workplaceName: string;
        }

    }
}
