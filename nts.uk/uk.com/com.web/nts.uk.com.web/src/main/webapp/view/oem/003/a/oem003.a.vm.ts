/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />
module nts.uk.com.view.oem003.a {

  const API = {
    register: 'com/screen/oem003/insert',
    findSettings: 'com/screen/oem003/findSettings',
  };

  const COMMA = '、';

  @bean()
  export class ViewModel extends ko.ViewModel {
    equipmentList: KnockoutObservableArray<any> = ko.observableArray([]);
    checkAll: KnockoutObservable<boolean> = ko.observable(false);

    dataTables: KnockoutObservableArray<DataTable> = ko.observableArray([]);
    formTitle: KnockoutObservable<string> = ko.observable(this.$i18n('OEM003_25'));
    enableButtonAdd: KnockoutComputed<boolean> = ko.computed(() => this.dataTables().length !== 9);
    $focus: any = null;

    created() {
      const vm = this;
      vm.checkAll.subscribe(value => {
        const checkData = _.forEach(vm.dataTables(), item => {
          item.selected(value);
        });
        vm.dataTables(checkData);
      });
    }

    mounted() {
      const vm = this;
      vm.startPage();
    }

    startPage() {
      const vm = this;
      vm
        .$blockui('grayout')
        .then(() => vm.$ajax('com', API.findSettings))
        .then((response: EquipmentUsageSetting) => {
          vm.toDataDisplay(response);
          $('#form-title').focus();
        })
        .always(() => vm.$blockui('clear'));
    }

    clickAdd() {
      const vm = this;
      const dataLength = vm.dataTables().length;
      // Max length is 9
      if (dataLength >= 9) return;

      const order = dataLength + 1;
      const data = vm.dataTables();
      data.push(vm.defaultDataTable(order));
      vm.dataTables(data);
      $(`.data-${order} .A4_D2`).focus();
    }

    clickExport() {
      const vm = this;
      if (_.isEmpty(vm.dataTables())) {
        vm.$dialog.error({ messageId: 'Msg_37' });
        return;
      }
      vm.$blockui('grayout');
      const program = nts.uk.ui._viewModel.kiban.programName().split(' ');
      let domainType = 'OEM003設備利用実績の設定';
      if (program.length > 1) {
        program.shift();
        domainType = domainType + program.join(' ');
      }
      nts.uk.request
        .exportFile('/masterlist/report/print', {
          domainId: 'EquipmentUsageSettings',
          domainType: domainType,
          languageId: __viewContext.user.selectedLanguage.basicLanguageId,
          reportType: 0,
        })
        .fail(err => vm.$dialog.error({ messageId: err.messageId }))
        .always(() => {
          vm.$blockui('clear');
          if (!_.isNil(vm.$focus) && !_.isEmpty(vm.dataTables))
            vm.$focus.focus();
        });
    }

    removeData(delOrder: KnockoutObservable<number>) {
      const vm = this;
      let deleteOrder = 0;
      // filter to remove data
      const filterArr = _.filter(vm.dataTables(), (item, index) => {
        if (item.order() !== delOrder())
          return true
        
          deleteOrder = index + 1;
        item.$errors('clear');
        return false;
      });

      // set new order for data
      filterArr.map((item, index) => {
        const order = index + 1;
        item.order(order);
        item.index = order;
      });

      vm.dataTables(filterArr);
      _.forEach(vm.dataTables(), data => vm.validateItem(data, data.itemNo()));

      if (deleteOrder > 0 && deleteOrder <= vm.dataTables().length) {
        $(`.data-${deleteOrder} input[tabindex="6"]`).focus();
      }

      if (deleteOrder > 0 && deleteOrder > vm.dataTables().length) {
        $(`.data-${deleteOrder - 1} input[tabindex="6"]`).focus();
      }
    }

    toOrderBy(type: 'before' | 'after') {
      const vm = this;
      // Clone data list of table
      const data = _.cloneDeep(vm.dataTables());

      // Get old position top of first selected item
      const firstSelected = _.find(vm.dataTables(), i => i.checked()).order();
      const $container = $('.ui-iggrid-scrolldiv');
      const $scrollTo = $(`.data-${firstSelected}`);
      const oldOffsetTop = $scrollTo.offset().top;
      const newOffsetTop = type === 'before'
        ? oldOffsetTop - $scrollTo.height()
        : oldOffsetTop + $scrollTo.height();

      const move = (fromOrder: number, toOrder: number) => {
        const from = fromOrder - 1;
        const to = toOrder - 1;

        if (!data[to] || data[to].checked())
          return;

        // remove `from` item and store it
        const f = data.splice(from, 1)[0];
        // insert stored item into position `to`
        data.splice(to, 0, f);
      }

      // Change the order of data list
      _.map(
        // If type is before, map the data tables, ifnot map the reverse of data table
        type === 'before' ? vm.dataTables() : vm.dataTables().reverse(),
        item => {
          if (item.checked() && type === 'before') {
            item.toBefore();
            move(item.index, item.order())
          }

          if (item.checked() && type === 'after') {
            item.toAfter();
            move(item.index, item.order())
          }
        }
      );

      // Apply new order
      _.map(data, (item, index) => {
        item.order(index + 1);
        item.index = index + 1;
      });
      
      vm.dataTables(data);
      vm.setFocusEvent();

      this.$nextTick(() => {
        $container.scrollTop(newOffsetTop - $container.offset().top + $container.scrollTop());
      });
    }

    beforeRegisted(): JQueryPromise<any> {
      const vm = this, dfd = $.Deferred();
      let isErrors = false;
      const data = ko.unwrap<DataTable>(vm.dataTables);
      if (_.isEmpty(data)) {
        vm.$dialog.error({ messageId: 'Msg_2219' });
        dfd.reject();
      }

      // Error lines cause of empty item type
      vm.$validate(".item-type").then(result => {
        if (!result) {
          dfd.reject();
        }
      });

      // Error lines cause of min and max
      const minmaxEL: any[] = [];
      // Error lines cause of item no
      let itemNoEL: any[] = [];
      _.map(data, (item) => {
        // If min greater than max, add to error list
        if ((item.isNumeric() || item.isTime()) && Number(item.min()) > Number(item.max())) {
          minmaxEL.push(item.order());
        }
        if (!_.includes(itemNoEL, item.order())) {
          const errorLines = vm.checkItemNo(item.itemNo());
          itemNoEL = _.concat(itemNoEL, errorLines);

          // If has more than 1 lines duplicate item no, show error
          if (errorLines.length > 1) {
            isErrors = true;
            const messageParams = [errorLines.join(COMMA)];
            const selector = `.data-${item.order()}`
            const msg = { messageId: 'Msg_2218', messageParams }
            vm.$errors(selector, msg);
          }

          if (errorLines.length <= 1) {
            item.itemTypeError(false);
          }
        }
      });

      if (!_.isEmpty(minmaxEL)) {
        isErrors = true;
        const messageParams = [minmaxEL.join(COMMA)];
        vm.$dialog.error({ messageId: 'Msg_2220', messageParams });
      }

      if (isErrors) dfd.reject();
      if (!isErrors) dfd.resolve();
      return dfd.promise();
    }

    clickRegister() {
      const vm = this;
      vm
        .beforeRegisted()
        .then(() => {
          const command: EquipmentUsageSetting = new EquipmentUsageSetting(
            __viewContext.user.companyId,
            vm.formTitle(),
            vm.dataTables()
          );
    
          vm
            .$validate()
            .then((valid) => {
              if (!valid) return;
              vm
                .$blockui('grayout')
                .then(() => vm.$ajax('com', API.register, command))
                .then(() => vm.$dialog.info({ messageId: 'Msg_15' }))
                .always(() => {
                  vm.$blockui('clear');
                  if (!_.isNil(vm.$focus) && !_.isEmpty(vm.dataTables))
                    vm.$focus.focus();
                })
            });
        });
    }

    defaultDataTable(order: number): DataTable {
      const vm = this;
      const data = new DataTable('', 6, null, null, null, null, '', false, '', order);
      vm.subscribeItemNo(data);
      return data;
    }

    toDataDisplay({ itemSettings, formatSetting, formSetting }: EquipmentUsageSetting) {
      const vm = this;
      const merged = _.reduce(itemSettings, (arr, e) => {
        arr.push(
          $.extend({}, e, _.find(formatSetting.itemDisplaySettings, a => a.itemNo === e.itemNo))
        );
        return arr;
      }, []);

      const dataTables = _.map(merged, item => {
        const data = new DataTable(
          item.items.itemName,
          item.displayWidth,
          item.itemNo,
          item.inputControl.minimum,
          item.inputControl.maximum,
          item.inputControl.digitsNo,
          item.items.unit,
          item.inputControl.require,
          item.items.memo,
          item.displayOrder
        );
        vm.subscribeItemNo(data);
        return data;
      });

      if (formSetting) {
        vm.formTitle(formSetting.title);
      }
      vm.dataTables(dataTables);
      vm.setFocusEvent();
    }

    setFocusEvent() {
      const vm = this;
      $('.ui-iggrid-scrolldiv table.data-table tr td *').focusin((e) => {
        vm.$focus = e.target;
      })
    }

    subscribeItemNo(data: DataTable) {
      const vm = this;
      data.itemNo.subscribe(() => {
        data.min(null);
        data.max(null);
        data.digitsNo(null);
        data.unit('');
        data.$errors('clear');
      }, 'beforeChange');

      data.itemNo.subscribe(value => vm.validateItem(data, value));
    }

    validateItem(data: DataTable, value: number) {
      const vm = this;
      _.forEach(vm.dataTables(), i => i.itemTypeError(false));
        vm
          .$errors('clear', [
            '.data-1', '.data-2', '.data-3',
            '.data-4', '.data-5', '.data-6',
            '.data-7', '.data-8', '.data-9',
          ])
          .then(() => data.$validate())
          .then(() => {
            const errorLines = vm.checkItemNo(value);
            if (errorLines.length <= 1) {
              data.itemTypeError(false)
              return;
            }

            const messageParams = [errorLines.join(COMMA)];
            const selector = `.data-${data.order()}`
            const msg = { messageId: 'Msg_2218', messageParams }
            vm.$errors(selector, msg);
          });
    }

    checkItemNo(value: number): number[] {
      const vm = this;
      const finder = _.filter(vm.dataTables(), item => {
        if (item.itemNo() != value) {
          return false;
        }
          
        if (item.itemNo() == value) {
          item.itemTypeError(true);
          return true;
        }

        return false;
      });

      return _.map(finder, i => i.order());
    }

  }

  class DataTable extends ko.ViewModel {
    id: string;
    checked: KnockoutObservable<boolean> = ko.observable(false);
    itemName: KnockoutObservable<string>;
    displayWidth: KnockoutObservable<number>;
    itemNo: KnockoutObservable<number>;
    min: KnockoutObservable<number>;
    max: KnockoutObservable<number>;
    digitsNo: KnockoutObservable<number>;
    unit: KnockoutObservable<string>;
    require: KnockoutObservable<boolean>;
    memo: KnockoutObservable<string>;
    order: KnockoutObservable<number>;
    index: number;

    itemTypes: KnockoutObservableArray<any> = ko.observableArray([
      { itemNo: 1, text: this.$i18n('OEM003_26') },
      { itemNo: 2, text: this.$i18n('OEM003_27') },
      { itemNo: 3, text: this.$i18n('OEM003_28') },
      { itemNo: 4, text: this.$i18n('OEM003_29') },
      { itemNo: 5, text: this.$i18n('OEM003_30') },
      { itemNo: 6, text: this.$i18n('OEM003_31') },
      { itemNo: 7, text: this.$i18n('OEM003_32') },
      { itemNo: 8, text: this.$i18n('OEM003_33') },
      { itemNo: 9, text: this.$i18n('OEM003_34') },
    ]);

    isCharacter: KnockoutComputed<boolean>;
    isNumeric: KnockoutComputed<boolean>;
    isTime: KnockoutComputed<boolean>;
    isCharOrNumber: KnockoutComputed<boolean>;
    isItemTypeNull: KnockoutComputed<boolean>;
    itemTypeError: KnockoutObservable<boolean> = ko.observable(false);
    css: KnockoutComputed<any>;

    constructor(
      itemName: string, displayWidth: number,
      itemNo: number, min: number, max: number, digitsNo: number,
      unit: string, require: boolean, memo: string, order: number
    ) {
      super();
      const vm = this;
      vm.id = nts.uk.util.randomId();
      vm.itemName = ko.observable(itemName);
      vm.displayWidth = ko.observable(displayWidth);
      vm.itemNo = ko.observable(itemNo);
      vm.min = ko.observable(min);
      vm.max = ko.observable(max);
      vm.digitsNo = ko.observable(digitsNo);
      vm.unit = ko.observable(unit);
      vm.require = ko.observable(require);
      vm.memo = ko.observable(memo);
      vm.order = ko.observable(order);

      vm.isCharacter = ko.computed(() => 1 <= this.itemNo() && this.itemNo() <= 3);
      vm.isNumeric = ko.computed(() => 4 <= this.itemNo() && this.itemNo() <= 6);
      vm.isTime = ko.computed(() => 7 <= this.itemNo() && this.itemNo() <= 9);
      vm.isCharOrNumber = ko.computed(() => vm.isCharacter() || vm.isNumeric());
      vm.isItemTypeNull = ko.computed(() => _.isNull(vm.itemNo()));
      vm.css = ko.computed(() => {
        const data = `data-${vm.order()}`;
        return {
          [data]: true,
          'active': vm.checked()
        }
      });
      vm.index = order;
    }

    toAfter() {
      const vm = this;
      if (vm.order() >= 9)
        return;

      vm.order(vm.order() + 1);
    }

    toBefore() {
      const vm = this;
      if (vm.order() <= 1)
        return;

      vm.order(vm.order() - 1);
    }

    selected(selected: boolean) {
      const vm = this;
      vm.checked(selected);
    }

  }

  enum ItemClassification {
    TEXT = 0,
    NUMBER = 1,
    TIME = 2,
  }

  class EquipmentUsageSetting {
    // 設備帳票設定
    formSetting: {
      cid: string,
      title: string
    };

    // 設備利用実績の項目設定
    itemSettings: {
      cid: string, itemNo: string,
      inputControl: {
        itemCls: number,
        require: boolean,
        digitsNo: number,
        maximum: number,
        minimum: number,
      }, 
      items: {
        itemName: string,
        unit: string,
        memo: string,
      },
    }[];

    // 設備の実績入力フォーマット設定
    formatSetting: {
      cid: string,
      itemDisplaySettings: {
        displayWidth: number,
        displayOrder: number,
        itemNo: string
      }[],
    };

    constructor(cid: string, title: string, dataTables: DataTable[]) {
      const vm = this;
      vm.formSetting = { cid, title };
      const itemSettings: any[] = [];
      const itemDisplaySettings: any[] = [];
      _.forEach(dataTables, item => {
        itemDisplaySettings.push({
          displayWidth: item.displayWidth(),
          displayOrder: item.order(),
          itemNo: item.itemNo(),
        });

        itemSettings.push({
          cid, itemNo: item.itemNo(),
          inputControl: {
            itemCls: item.isCharacter()
              ? ItemClassification.TEXT
              : item.isNumeric()
                ? ItemClassification.NUMBER
                : ItemClassification.TIME,
            require: item.require(),
            digitsNo: item.digitsNo(),
            maximum: item.max(),
            minimum: item.min(),
          }, 
          items: {
            itemName: item.itemName(),
            unit: item.unit(),
            memo: item.memo(),
          },
        })
      });
      
      vm.itemSettings = itemSettings;
      vm.formatSetting = { cid, itemDisplaySettings };
    }
  }
}
