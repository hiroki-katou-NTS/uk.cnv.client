/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.com.view.cas012.b {
    import block = nts.uk.ui.block;
    import isNullOrEmpty = nts.uk.util.isNullOrEmpty;
    import isNullOrUndefined = nts.uk.util.isNullOrUndefined;
    var format = nts.uk.text.format;
    const API = {
        getEmployeeList:"screen/com/cas012/b/get-employee-info/{0}"
    };
    @bean()
    class ViewModel extends ko.ViewModel {
        companyId: KnockoutObservable<string> = ko.observable('');
        loginCid: KnockoutObservable<string> = ko.observable('');
        companyCode: KnockoutObservable<string>;
        companyName: KnockoutObservable<string>;
        //B1_2
        itemList: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        listCompany: KnockoutObservableArray<ItemModel> = ko.observableArray([]);

        selectedCode: KnockoutObservable<string>;
        isEnable: KnockoutObservable<boolean>;
        isEditable: KnockoutObservable<boolean>;
        isRequired: KnockoutObservable<boolean>;
        // start declare KCP005
        listComponentOption: any;
        selectedCodeKCP: KnockoutObservable<string>;
        multiSelectedCode: KnockoutObservableArray<string>;
        isShowAlreadySet: KnockoutObservable<boolean>;
        alreadySettingPersonal: KnockoutObservableArray<UnitAlreadySettingModel>;
        isDialog: KnockoutObservable<boolean>;
        isShowNoSelectRow: KnockoutObservable<boolean>;
        isMultiSelect: KnockoutObservable<boolean>;
        isShowWorkPlaceName: KnockoutObservable<boolean>;
        isShowSelectAllButton: KnockoutObservable<boolean>;
        disableSelection : KnockoutObservable<boolean>;
        employeeList: KnockoutObservableArray<UnitModel>;
        baseDate: KnockoutObservable<Date>;
        employInfors: KnockoutObservableArray<any> = ko.observableArray([]);
        listEmployee: KnockoutObservableArray<EmployInfor> = ko.observableArray([]);
        optionalColumnDatasource: KnockoutObservableArray<any> = ko.observableArray([]);

        constructor(params: any) {
            super();
            let vm = this;
            vm.companyCode = ko.observable('');
            vm.companyName = ko.observable('');
            //B1_2
            vm.isEnable = ko.observable(true);
            vm.isEditable = ko.observable(true);
            vm.isRequired = ko.observable(true);
            // KCP005
            vm.baseDate = ko.observable(new Date());
            vm.selectedCodeKCP = ko.observable('');
            vm.multiSelectedCode = ko.observableArray();
            vm.isShowAlreadySet = ko.observable(false);
            vm.alreadySettingPersonal = ko.observableArray([]);
            vm.isDialog = ko.observable(false);
            vm.isShowNoSelectRow = ko.observable(false);
            vm.isMultiSelect = ko.observable(false);
            vm.isShowWorkPlaceName = ko.observable(false);
            vm.isShowSelectAllButton = ko.observable(false);
            vm.disableSelection = ko.observable(false);
            vm.employeeList = ko.observableArray<UnitModel>([]);
            let cidLogin = vm.$user.companyId;
            vm.loginCid(cidLogin);
            vm.companyId.subscribe((cid) => {
                 if (cid){
                vm.getListEmployee(cid);
                 }
            });
            vm.getListCompany();
            vm.KCP005_load()
        }
        created() {
        }
        mounted() {
            let vm = this;
            vm.setFocus();
        }
        setFocus() {
            $('#combo-box2').focus();
        }
        getListEmployee(cid : string):JQueryPromise<any>{
            let vm = this,
                dfd = $.Deferred();
            block.invisible();
            let emps : any = [];
            let job : any = [];
            let _path = format(API.getEmployeeList,cid);
            vm.$ajax('com',_path ).done((data) => {
                if(!isNullOrEmpty(data)){
                    for (let i =0; i<data.length;i++) {
                        let item = data[i];
                        emps.push({
                            id: item.employeeId,
                            code: item.employeeCode,
                            name: item.businessName,
                            affiliationName: item.workplaceName
                        });
                        job.push({
                            empId: item.employeeCode,
                            content: item.jobTitleName
                        })
                    }

                }
                vm.optionalColumnDatasource(job);
                vm.employInfors(emps);
                vm.listEmployee(data);
            }).always(()=>{
                block.clear()
            }).fail(()=>{
                vm.backToTopPage();
                dfd.reject();
            });
            return dfd.promise();
        }
        getListCompany(){
            let vm = this,
                dataFromScreenA: any = nts.uk.ui.windows.getShared("cid_from_a");
            if(!isNullOrUndefined(dataFromScreenA)){
                let companys : ItemModel[] = [];
                let listCompany = dataFromScreenA.listCompany;
                for (let i =0; i<listCompany.length;i++) {
                    let item = listCompany[i];
                    companys.push( new ItemModel(item.companyCode,item.companyName,item.companyId) )
                }
                vm.listCompany(companys);
                if(!isNullOrEmpty(companys)){
                    vm.companyId(companys[0].id);
                }
            }
        }
        KCP005_load() {
            let vm = this;
            vm.baseDate = ko.observable(new Date());
            vm.isShowAlreadySet = ko.observable(false);
            vm.alreadySettingPersonal = ko.observableArray([]);
            vm.isDialog = ko.observable(false);
            vm.isShowNoSelectRow = ko.observable(false);
            vm.isMultiSelect = ko.observable(false);
            vm.isShowWorkPlaceName = ko.observable(false);
            vm.isShowSelectAllButton = ko.observable(false);
            vm.disableSelection = ko.observable(false);
            vm.listComponentOption = {
                isShowAlreadySet: false,
                isMultiSelect: false,
                listType: ListType.EMPLOYEE,
                employeeInputList: vm.employInfors,
                selectType: SelectType.SELECT_ALL,
                selectedCode: vm.multiSelectedCode,
                isDialog: false,
                alreadySettingList: vm.alreadySettingPersonal,
                isShowWorkPlaceName: true,
                isShowSelectAllButton: false,
                showOptionalColumn: true,
                optionalColumnName: nts.uk.resource.getText("CAS012_27"),
                optionalColumnDatasource: vm.optionalColumnDatasource,
                maxWidth: 520,
                maxRows: 15,
            };
            vm.multiSelectedCode.subscribe((e) => {
                let employee = _.find(vm.listEmployee(),(i)=>i.employeeCode == e.toString());
                let company = _.find(vm.listCompany(),(i)=>i.id == vm.companyId());
                if(!isNullOrUndefined(employee)){
                    nts.uk.ui.windows.setShared("employeeInf", employee);
                }
                if(!isNullOrUndefined(company)){
                    nts.uk.ui.windows.setShared("cid", company);
                }
            });
            $('#kcp005').ntsListComponent(vm.listComponentOption)
        }
        backToTopPage() {
            nts.uk.request.jump("/view/ccg/008/a/index.xhtml");
        }
        decision(){
            let vm = this;
            if(isNullOrEmpty(vm.multiSelectedCode())){
                nts.uk.ui.dialog.alertError({
                    messageId: "Msg_218",
                    messageParams: [nts.uk.resource.getText("KCP005_1")]
                });
            }else {
                nts.uk.ui.windows.close();
            }
        }
        cancel_Dialog(): any {
            nts.uk.ui.windows.setShared("employeeInf", null);
            nts.uk.ui.windows.setShared("cid", null);
            nts.uk.ui.windows.close();
        }
    }
    class ItemModel {
        code: string;
        name: string;
        id?: string;
        constructor(code: string, name: string, id: string) {
            this.code = code;
            this.name = name;
            this.id = id;
        }
    }
    export class ListType {
        static EMPLOYMENT = 1;
        static Classification = 2;
        static JOB_TITLE = 3;
        static EMPLOYEE = 4;
    }

    export interface UnitModel {
        code: string;
        name?: string;
        id?: string;
    }
    export interface EmployInfor {
        personId:string;
        userId : string;
        employeeId : string;
        employeeCode : string;
        businessName: string;
        jobTitleID : string;
        jobTitleCode: string;
        jobTitleName: string;
        workplaceId : string;
        workplaceCode: string;
        workplaceName : string;
        wkpDisplayName : string;
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
}