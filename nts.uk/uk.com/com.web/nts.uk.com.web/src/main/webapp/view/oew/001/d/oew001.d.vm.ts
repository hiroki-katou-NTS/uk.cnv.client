/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.oew001.d {

  import model = oew001.share.model;

  const API = {
    getEquipmentClsList : "query/equipment/classificationmaster/getAll"
  };

  @bean()
  export class ScreenModel extends ko.ViewModel {

    equipmentClsList: KnockoutObservableArray<EquipmentClassificationDto> = ko.observableArray([]);
    selectedEquipmentClsCode: KnockoutObservable<string> = ko.observable(null);
    columns: KnockoutObservableArray<any> = ko.observableArray([]);
    enableSave: KnockoutObservable<boolean>;
    isOpenFromA: boolean;

    created(param: any) {
      const vm = this;
      vm.columns([
        { headerText: vm.$i18n("OEW001_50"), key: 'code', width: 100 },
        { headerText: vm.$i18n("OEW001_51"), key: 'name', width: 200 },
      ]);
      vm.enableSave = ko.computed(() => vm.equipmentClsList().length !== 0 && !!vm.selectedEquipmentClsCode());
      vm.selectedEquipmentClsCode(param.equipmentClsCode);
      vm.isOpenFromA = param.isOpenFromA;
    }

    mounted() {
      const vm = this;
      vm.$blockui("grayout");
      vm.$ajax(API.getEquipmentClsList)
        .then(result => {
          vm.equipmentClsList(result);
          if (!vm.isOpenFromA) {
            vm.equipmentClsList.unshift({
              code: model.constants.SELECT_ALL_CODE,
              name: vm.$i18n("OEW001_71")
            });
          }
        })
        .then(() => {
          if (vm.equipmentClsList().length > 0 && !vm.selectedEquipmentClsCode()) {
            vm.selectedEquipmentClsCode(vm.equipmentClsList()[0].code);
          }
        })
        .always(() => {
          $("#D2_container").focus();
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