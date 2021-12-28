/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />
module nts.uk.com.view.ccg015.h {
  import ComponentModel = nts.uk.com.view.ccg025.a.component.viewmodel.ComponentModel;
  
  const API = {
    get: 'at/auth/workplace/initdisplayperiod/get',
    getByCid: 'at/auth/workplace/initdisplayperiod/get-by-cid',
    save: 'at/auth/workplace/initdisplayperiod/save',
    delete: 'at/auth/workplace/initdisplayperiod/delete',
  }

  @bean()
  export class ScreenModel extends ko.ViewModel {
    numberOfDays: KnockoutObservableArray<{name: string, value: number}> = ko.observableArray([]);
    selectedNumberOfDay: KnockoutObservable<any> = ko.observable(0);

    roleName: KnockoutObservable<string> = ko.observable('');
    isNewMode: KnockoutObservable<boolean> = ko.observable(true);
    alreadyRoles: string[] = [];
    component: KnockoutObservable<ComponentModel> = ko.observable(new ComponentModel({ 
      roleType: 3, //就業
      multiple: false,
      isAlreadySetting: true,
      rows: 10,
      tabindex: 4,
      onDialog: true,
    }));
    currentRoleId: KnockoutComputed<string>;

    created() {
      const vm = this;
      for (let i = 0; i <= 31; i++) {
        if (i === 0) {
          vm.numberOfDays.push({name: vm.$i18n('CCG015_103'), value: i});
          continue;
        }

        vm.numberOfDays.push({name: vm.$i18n('CCG015_104', [i.toString()]), value: i});
      }
      vm.currentRoleId = ko.computed(() => {
        return vm.component().currentRoleId();
      });
    }

    mounted() {
      const vm = this;
      vm.currentRoleId.subscribe((value: any) => {
        vm.getData();
        const role = _.find(vm.component().listRole(), role => role.roleId === value);
        if (_.isNil(role)) {
          vm.roleName('');
          return;
        }

        vm.roleName(`${role.roleCode} ${role.roleName}`);
      });

      vm.getAlreadySetting(true);
    }

    getAlreadySetting(isStart?: boolean) {
      const vm = this;
      const roleAtr = ko.unwrap<number>(vm.component().roleClassification);
      const selectedRole = ko.unwrap<string>(vm.component().currentRoleId);
      vm.$ajax('at', API.getByCid)
        .then((result: InitDisplayPeriodSwitchSet[]) => {
          if (!result) {
            vm.alreadyRoles = [];
            return;
          }
          vm.alreadyRoles = _.map(result, x => x.roleID);
          
        })
        .then(() => {
          vm.component(new ComponentModel({
            roleType: 3, //就業
            multiple: false,
            isAlreadySetting: true,
            rows: 10,
            tabindex: 4,
            onDialog: true,
            roleAtr,
            alreadySetList: vm.alreadyRoles,
          }));
          vm.component().displayRoleClassification(true);
          return vm.component().startPage(selectedRole);
        })
        .then(() => $('#ccg015_h10 .multi-list_container').focus());
    }

    getData() {
      const vm = this;
      vm
        .$blockui('grayout')
        .then(() => vm.$ajax('at', API.get, vm.currentRoleId()))
        .then((response: InitDisplayPeriodSwitchSet) => {
          if (!response) {
            vm.selectedNumberOfDay(0);
            vm.isNewMode(true);
          }
          else {
            vm.isNewMode(false);
            vm.selectedNumberOfDay(response.day);
          }
        })
        .always(() => vm.$blockui('clear'));
    }

    onSave() {
      const vm = this;
      const command = {
        companyId: __viewContext.user.companyId,
        roleId: vm.currentRoleId(),
        day: vm.selectedNumberOfDay(),
      };
      vm
        .$blockui('grayout')
        .then(() => vm.$ajax('at', API.save, command))
        .then(() => vm.getAlreadySetting())
        .then(() => vm.$dialog.info({messageId: 'Msg_15'}))
        .then(() => vm.getData())
        .always(() => vm.$blockui('clear'));
    }

    onDelete() {
      const vm = this;
      const command = {
        companyId: __viewContext.user.companyId,
        roleId: vm.currentRoleId(),
      };
      vm
        .$blockui('grayout')
        .then(() => vm.$dialog.confirm({messageId: 'Msg_18'}))
        .then((response: 'yes' | 'no') => {
          if (response === 'yes') {
            vm
              .$ajax('at', API.delete, command)
              .then(() => vm.getAlreadySetting())
              .then(() => vm.$dialog.info({messageId: 'Msg_16'}))
              .then(() => vm.getData());
          }
        })
        .always(() => vm.$blockui('clear'));
    }

    close() {
      const vm = this;
      vm.$window.close();
    }
  }

  interface InitDisplayPeriodSwitchSet {
    companyID: string;
    roleID: string;
  	day: number;
  }
}