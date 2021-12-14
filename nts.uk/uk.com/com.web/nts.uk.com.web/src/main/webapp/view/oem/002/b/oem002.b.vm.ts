/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.oem002.b {

  const API = {
    getEquipmentClsList : "com/screen/oem002/getClassificationList"
  };

  @bean()
  export class ScreenModel extends ko.ViewModel {

    equipmentClsList: KnockoutObservableArray<EquipmentClassificationDto> = ko.observableArray([]);
    selectedEquipmentClsCode: KnockoutObservable<string> = ko.observable(null);
    columns: KnockoutObservableArray<any> = ko.observableArray([]);
    enableSave: KnockoutObservable<boolean>;

    created(param: string) {
      const vm = this;
      vm.columns([
        { headerText: vm.$i18n("OEM002_23"), key: 'code', width: 100 },
        { headerText: vm.$i18n("OEM002_24"), key: 'name', width: 200 },
      ]);
      vm.enableSave = ko.computed(() => vm.equipmentClsList().length !== 0 && !!vm.selectedEquipmentClsCode());
      vm.selectedEquipmentClsCode(param);
    }

    mounted() {
      const vm = this;
      vm.$blockui("grayout");
      vm.$ajax(API.getEquipmentClsList)
        .then(result => vm.equipmentClsList(result))
        .then(() => {
          if (vm.equipmentClsList().length > 0 && !vm.selectedEquipmentClsCode()) {
            vm.selectedEquipmentClsCode(vm.equipmentClsList()[0].code);
          }
        })
        .always(() => {
          $("#B1_2_container").focus();
          vm.$blockui("clear");
        });
    }

    public closeDialog() {
      const vm = this;
      vm.$window.close({
        code: vm.selectedEquipmentClsCode(),
        name: _.find(vm.equipmentClsList(), { "code": vm.selectedEquipmentClsCode() }).name
      });
    }

    public cancel() {
      const vm = this;
      vm.$window.close();
    }
  }

  export class EquipmentClassificationDto {
    /**
     * コード
     */
     code: string;
    
     /**
      * 名称
      */
     name: string;
  }
}