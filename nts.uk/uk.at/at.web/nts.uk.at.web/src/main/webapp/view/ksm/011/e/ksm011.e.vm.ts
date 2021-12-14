/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.at.view.ksm011.e {

  import ccg = nts.uk.com.view.ccg025.a;
  import model = nts.uk.com.view.ccg025.a.component.model;

  import ccg026 = nts.uk.com.view.ccg026;
  import ROLE_TYPE = ccg026.component.ROLE_TYPE;
  import IPermision = ccg026.component.IPermision;


  const fetch = {
    getRoleInfor: 'screen/at/ksm/ksm011/e/getpermission/',
    saveRoleSetting: 'screen/at/ksm/ksm011/e/register',
  };
  
  @bean()
  class ViewModel extends ko.ViewModel {
    switchItems: KnockoutObservableArray<any>;
    tabs: KnockoutObservableArray<NtsTabPanelModel>;
    selectedTab: KnockoutObservable<string>;
    basicFunctionControl: KnockoutObservable<number> = ko.observable(0);
    daysList: KnockoutObservableArray<any> = ko.observableArray([]);
    dateSelected: KnockoutObservable<number> = ko.observable(31);

    //ccg025    
    componentViewmodel: ccg.component.viewmodel.ComponentModel;
    listRole: KnockoutObservableArray<model.Role> = ko.observableArray([]);
    roleName: KnockoutObservable<string> = ko.observable(null);

    //ccg026    
    roleId: KnockoutObservable<string> = ko.observable(null);// role id    
    roleType: KnockoutObservable<ROLE_TYPE> = ko.observable(ROLE_TYPE.COMPANY_MANAGER);
    roleType1: KnockoutObservable<ROLE_TYPE> = ko.observable(ROLE_TYPE.PERSONAL_INFO);
    roleType2: KnockoutObservable<ROLE_TYPE> = ko.observable(ROLE_TYPE.PERSONAL_INFO);
    
    permissionCommon : KnockoutObservableArray<IPermision> = ko.observableArray([]);
    permissionByWorkplace : KnockoutObservableArray<IPermision> = ko.observableArray([]);
    permissionByIndividual : KnockoutObservableArray<IPermision> = ko.observableArray([]);
    data: any;

    constructor(params: any) {
      super();
      const vm = this;

      vm.tabs = ko.observableArray([
        { id: 'tab-1', title: vm.$i18n('KSM011_96'), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true) },
        { id: 'tab-2', title: vm.$i18n('KSM011_97'), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true) },
        { id: 'tab-3', title: vm.$i18n('KSM011_98'), content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(true) }
      ]);
      vm.selectedTab = ko.observable('tab-1');

      vm.switchItems = ko.observableArray([
        { code: 1, name: vm.$i18n('KSM011_21') },
        { code: 0, name: vm.$i18n('KSM011_22') }
      ]);

      vm.getDayList();

      vm.initialCCG025026();

    }

    created(params: any) {
      const vm = this;
      vm.selectedTab.subscribe((value) => {
        switch(value) {
          case "tab-1": 
            vm.rebindCcg026("permissionCommon")
              .$nextTick(() => vm.permissionCommon.valueHasMutated());
            break;
          case "tab-2": 
            vm.rebindCcg026("permissionByWorkplace")
              .$nextTick(() => vm.permissionByWorkplace.valueHasMutated());
            break;
          case "tab-3": 
            vm.rebindCcg026("permissionByIndividual")
              .$nextTick(() => vm.permissionByIndividual.valueHasMutated());
            break;
        }
      });
    }

    mounted() {
      const vm = this;
        $("#tab-panel li")[0].focus();
    }

    private rebindCcg026(id: string): this {
      const vm = this;
      let element = $(`#${id}`)[0];
      ko.cleanNode(element);
      ko.applyBindings(vm, element);
      return vm;
    }

    saveData() {
      const vm = this;
      vm.$blockui("show");
      const data = {
          roleId: vm.roleId(),
          useAtr: vm.basicFunctionControl(),
          deadLineDay: vm.dateSelected(),
          scheModifyCtrlCommons: vm.permissionCommon().map((i: any) => ({
              functionNo: i.functionNo,
              available: i.available ? 1 : 0
          })),
          scheModifyByWorkplaces: vm.permissionByWorkplace().map((i: any) => ({
              functionNo: i.functionNo,
              available: i.available ? 1 : 0
          })),
          scheModifyByPersons: vm.permissionByIndividual().map((i: any) => ({
              functionNo: i.functionNo,
              available: i.available ? 1 : 0
          }))
      };
        vm.$ajax(fetch.saveRoleSetting, data).done((data) => {
            vm.$dialog.info({ messageId: 'Msg_15'});
        }).fail(error => {
            vm.$dialog.error(error);
        }).always(() => {
            vm.$blockui("hide");
        });
    }

    getDayList() {
      const vm = this;
      vm.daysList = ko.observableArray([]);
      let days = [];
      for (let day = 0; day <= 31; day++) {
        days.push({ day: day, name: day + vm.$i18n('KSM011_105') });
      }
      // days.push({ day: 31, name: vm.$i18n('KSM011_106') });
      vm.daysList(days);
    }

    getDataCCG025(): JQueryPromise<any> {
      let vm = this;
      let dfd = $.Deferred();
      vm.componentViewmodel.startPage().done(() => {
        vm.listRole(vm.componentViewmodel.listRole());
      });
      dfd.resolve();
      return dfd.promise();
    }

    findRole(roleId?: string) {
      const vm = this;

      let role = _.find(vm.listRole(), (x) => { return x.roleId === roleId; });
      if (role) vm.roleName(role.roleName);
    }

    
    initialCCG025026() {

      const vm = this;
      //ccg025
      vm.componentViewmodel = new ccg.component.viewmodel.ComponentModel({
        roleType: 3,
        multiple: false,
        isAlreadySetting: false
      });

      vm.getDataCCG025().done(() => { });
      vm.componentViewmodel.currentCode.subscribe((roleId) => {
        if (vm.listRole().length <= 0) vm.listRole(vm.componentViewmodel.listRole());

        vm.roleId(roleId);
        vm.findRole(roleId);
      });

      vm.roleId.subscribe((newRoleId) => {
        if (!_.isEmpty(newRoleId)) vm.getRoleInfor(newRoleId);
        vm.selectedTab('tab-1');
      });       
    }

    getRoleInfor(roleId: string) {
      const vm = this;
      vm.$blockui("show");
      vm.$ajax(fetch.getRoleInfor + roleId).done((data) => {
          if (data) {
            vm.data = data;
            vm.basicFunctionControl(data.useAtr || 0);
            vm.dateSelected(data.deadLineDay || 0);
            vm.convertToPermissionList(data);
          }
      }).fail(error => {
        vm.$dialog.error(error);
      }).always(() => {
        vm.$blockui("hide");
      });
    }

    private convertToPermissionList(data: any): void {
      const vm = this;
      const checkedCommon = data.scheModifyCommon.scheModifyAuthCtrlCommons
        .filter((i: any) => i.available)
        .map((i: any) => i.functionNo);
      vm.permissionCommon(data.scheModifyCommon.scheModifyFuncCommons.map((i: any) => ({
        available: checkedCommon.indexOf(i.functionNo) >= 0,
        description: i.explanation,
        functionName: i.name,
        functionNo: i.functionNo,
        orderNumber: i.displayOrder
      })));

      const checkedWorkplace = data.scheModifyByWorkplace.scheModifyAuthCtrlByWkp
        .filter((i: any) => i.available)
        .map((i: any) => i.functionNo);
      vm.permissionByWorkplace(data.scheModifyByWorkplace.scheModifyFuncByWorkplaces.map((i: any) => ({
        available: checkedWorkplace.indexOf(i.functionNo) >= 0,
        description: i.explanation,
        functionName: i.name,
        functionNo: i.functionNo,
        orderNumber: i.displayOrder
      })));

      const checkedPerson = data.scheduleModifyByPerson.scheModifyAuthCtrlByPersons
        .filter((i: any) => i.available)
        .map((i: any) => i.functionNo);
      vm.permissionByIndividual(data.scheduleModifyByPerson.scheModifyFuncByPersons.map((i: any) => ({
        available: checkedPerson.indexOf(i.functionNo) >= 0,
        description: i.explanation,
        functionName: i.name,
        functionNo: i.functionNo,
        orderNumber: i.displayOrder
      })));
    }
  }
}