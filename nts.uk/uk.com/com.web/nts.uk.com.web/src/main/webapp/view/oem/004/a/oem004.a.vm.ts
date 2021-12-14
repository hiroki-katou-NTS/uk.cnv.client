/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />
module nts.uk.com.view.oem004.a {

  const API = {
    getEquipmentList: 'query/equipment/classificationmaster/getAll',
    insetEquipment: 'ctx/office/equipment/classificationmaster/insert',
    updateEquipment: 'ctx/office/equipment/classificationmaster/update',
    deleteEquipment: 'com/screen/oem004/delete',
  };

  @bean()
  export class ViewModel extends ko.ViewModel {
    tableColumns: KnockoutObservableArray<any> = ko.observableArray([
      { headerText: this.$i18n('KDL047_6'), prop: 'code', width: 100 },
      { headerText: this.$i18n('KDL047_7'), prop: 'name', width: 250 }
    ]);
    equipmentList: KnockoutObservableArray<EquipmentClassification> = ko.observableArray([]);
    selectedCode: KnockoutObservable<string> = ko.observable(null);

    code: KnockoutObservable<string> = ko.observable('');
    name: KnockoutObservable<string> = ko.observable('');

    isNewMode: KnockoutObservable<boolean> = ko.observable(true);

    mounted() {
      const vm = this;
      vm.$nextTick(() => {
        vm.selectedCode.subscribe(val => {
          vm.displaySelectedItem();
          if (val) {
            vm.isNewMode(false)
          }
        })
			});

      vm.startScreen();
    }

    startScreen() {
      const vm = this;
      vm
        .$blockui('grayout')
        .then(() => vm.getEquipmentList())
        .then(() => {
          if (_.isEmpty(vm.equipmentList())) {
            $('#equipment-code').focus();
            vm.isNewMode(true);
            return;
          }

          vm.selectedCode(vm.equipmentList()[0].code);
          vm.displaySelectedItem();
          vm.isNewMode(false);
        })
        .always(() => vm.$blockui('clear'))
    }

    displaySelectedItem() {
      const vm = this;
      vm.$errors('clear');
      const selectedItem = _.find(vm.equipmentList(), item => item.code === vm.selectedCode());
      if (!selectedItem) {
        vm.code('');
        vm.name('');
        $('#equipment-code').focus();
        return;
      };

      vm.code(selectedItem.code);
      vm.name(selectedItem.name);
      $('#equipment-name').focus();
    }
    
    /**
     * 設備分類一覧を取得する
     */
    getEquipmentList(): JQueryPromise<any> {
      const vm = this, dfd = $.Deferred();
      vm
        .$ajax('com', API.getEquipmentList)
        .then((response: EquipmentClassification[]) => {
          const dataList = _.orderBy(response, ['code'], ['asc']);
          vm.equipmentList(dataList);
          dfd.resolve();
        })
        .fail(() => dfd.reject())
      return dfd.promise();
    }

    changeToNewMode() {
      const vm = this;
      vm.isNewMode(true);
      vm.selectedCode(null);
    }

    clickRegister() {
      const vm = this;
      vm.$validate().then(valid => {
        if (!valid) return;

        if (vm.isNewMode()) vm.createEquipmentCls();
        if (!vm.isNewMode()) vm.updateEquipmentCls();
      })
    }

    /**
     * 設備分類を新規登録する
     */
    createEquipmentCls() {
      const vm = this;
      vm
        .$blockui('grayout')
        .then(() => vm.$ajax('com', API.insetEquipment, { code: vm.code(), name: vm.name() }))
        .then(() => vm.$dialog.info({ messageId: 'Msg_15' }))
        .then(() => vm.getEquipmentList())
        .then(() => {
          vm.selectedCode(vm.code());
          vm.displaySelectedItem();
          vm.isNewMode(false);
        })
        .fail(err => vm.$dialog.error(err)
          .then(() => {
            $('#equipment-code').focus();
            $('#equipment-code').ntsError('set', err)
          })
        )
        .always(() => vm.$blockui('clear'))
    }

    /**
     * 更新モードの場合
     */
    updateEquipmentCls() {
      const vm = this;
      vm
        .$blockui('grayout')
        .then(() => vm.$ajax('com', API.updateEquipment, { code: vm.code(), name: vm.name() }))
        .then(() => vm.$dialog.info({ messageId: 'Msg_15' }))
        .then(() => vm.getEquipmentList())
        .then(() => {
          vm.selectedCode(vm.code());
          vm.displaySelectedItem();
        })
        .always(() => vm.$blockui('clear'));
    }

    clickDelete() {
      const vm = this;
      let index = -1;
      vm
        .$blockui('grayout')
        .then(() => vm.$dialog.confirm({ messageId: "Msg_18" }))
        .then((result: 'no' | 'yes' | 'cancel') => {
          if (result !== 'yes') return;
          index = _.findIndex(vm.equipmentList(), item => item.code === vm.selectedCode());
          return vm.$ajax('com', API.deleteEquipment, vm.code())
            .then(() => vm.$dialog.info({ messageId: 'Msg_16' }))
        })
        .then(() => vm.getEquipmentList())
        .then(() => vm.setSelectedAfterDelete(index))
        .fail(error => vm.$dialog.error(error))
        .always(() => vm.$blockui('clear'));
    }

    setSelectedAfterDelete(index: number) {
      const vm = this;
      const dataLength = vm.equipmentList().length;
      if (dataLength === 0) {
        vm.isNewMode(true);
        vm.selectedCode(null);
        return;
      }

      if (index >= dataLength) index = dataLength - 1

      const selected = vm.equipmentList()[index];
      if (selected) {
        vm.selectedCode(selected.code);
      }
    }

    clickExport() {
      const vm = this;
      vm.$blockui('grayout');
      const program = nts.uk.ui._viewModel.kiban.programName().split(" ");
      let domainType = "OEM004設備分類の登録";
      if (program.length > 1) {
        program.shift();
        domainType = domainType + program.join(" ");
      }
      nts.uk.request
        .exportFile('/masterlist/report/print', {
          domainId: 'EquipmentClassification',
          domainType: domainType,
          languageId: __viewContext.user.selectedLanguage.basicLanguageId,
          reportType: 0,
        })
        .fail(err => vm.$dialog.alert({ messageId: err.messageId }))
        .always(() => vm.$blockui('clear'));
    }

  }

  class EquipmentClassification {
    code: string;
    name: string;

    constructor(init?: EquipmentClassification) {
      $.extend(this, init);
    }
  }
}
