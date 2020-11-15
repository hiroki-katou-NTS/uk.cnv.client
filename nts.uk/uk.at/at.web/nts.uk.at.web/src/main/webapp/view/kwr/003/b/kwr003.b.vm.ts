/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.at.view.kwr003.b {
  const NUM_ROWS = 10;
  const KWR003_B_INPUT = 'KWR003_WORK_STATUS_DATA';
  const KWR003_B_OUTPUT = 'KWR003WORK_STATUS_RETURN';
  const KWR003_C_INPUT = 'KWR003_C_DATA';
  const KWR003_C_OUTPUT = 'KWR003_C_RETURN';

  const PATH = {
    getSettingListWorkStatus: 'at/function/kwr/003/a/listworkstatus',
    getSettingLitsWorkStatusDetails: 'at/function/kwr/003/b/detailworkstatus',
    checkDailyAuthor: 'at/function/kwr/003/a/checkdailyauthor',
    deleteSettingItemDetails: 'at/function/kwr/003/b/delete',
    createSettingItemDetails: 'at/function/kwr/003/b/create',
    updateSettingItemDetails: 'at/function/kwr/003/b/update',
    getFormInfo: 'at/screen/kwr/003/b/getinfor'
  };

  @bean()
  class ViewModel extends ko.ViewModel {

    settingListItems: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
    columns: KnockoutObservableArray<any>;
    currentCode: KnockoutObservable<any>;
    currentCodeList: KnockoutObservable<string> = ko.observable(null);
    currentSettingCodeList: KnockoutObservableArray<any>;
    settingRules: KnockoutObservableArray<any>;
    attendance: KnockoutObservable<any> = ko.observable(null);
    attendanceCode: KnockoutObservable<string> = ko.observable(null);
    attendanceName: KnockoutObservable<string> = ko.observable(null);
    settingCategory: KnockoutObservable<number> = ko.observable(0);
    settingId: KnockoutObservable<string> = ko.observable(null);
    settingListItemsDetails: KnockoutObservableArray<SettingForPrint> = ko.observableArray([]);

    isSelectAll: KnockoutObservable<boolean> = ko.observable(false);
    isEnableAddButton: KnockoutObservable<boolean> = ko.observable(false);
    isEnableAttendanceCode: KnockoutObservable<boolean> = ko.observable(false);
    isEnableDeleteButton: KnockoutObservable<boolean> = ko.observable(false);
    isEnableDuplicateButton: KnockoutObservable<boolean> = ko.observable(false);
    isNewMode: KnockoutObservable<boolean> = ko.observable(false);

    //KDL 047, 048
    shareParam = new SharedParams();

    position: KnockoutObservable<number> = ko.observable(1);
    attendanceItemName: KnockoutObservable<string> = ko.observable(null);
    columnIndex: KnockoutObservable<number> = ko.observable(1);
    isDisplayItemName: KnockoutObservable<boolean> = ko.observable(true);
    isDisplayTitle: KnockoutObservable<boolean> = ko.observable(true);
    isEnableComboBox: KnockoutObservable<number> = ko.observable(2);
    isEnableTextEditor: KnockoutObservable<number> = ko.observable(2);
    comboSelected: KnockoutObservable<any> = ko.observable(null);
    tableSelected: KnockoutObservable<any> = ko.observable(null);


    workStatusTableOutputItem: KnockoutObservable<any> = ko.observable(null);
    diligenceProjects: KnockoutObservableArray<DiligenceProject> = ko.observableArray([]);

    constructor(params: any) {
      super();

      const vm = this;

      //get output info
      vm.getWorkStatusTableOutput();

      vm.getSettingList();

      vm.currentCodeList.subscribe((newValue: any) => {
        nts.uk.ui.errors.clearAll();                
        if( !newValue ) return;
        vm.getSettingListForPrint(newValue);
        vm.getSettingListItemsDetails();
      });

      vm.settingRules = ko.observableArray([
        { code: 1, name: vm.$i18n('KWR003_217') },
        { code: 2, name: vm.$i18n('KWR003_218') }
      ]);

      vm.settingListItemsDetails.subscribe((newList) => {
        if (!newList || newList.length <= 0) {
          vm.isSelectAll(false);
          return;
        }
        //Check if all the values in the settingListItemsDetails array are true:
        let isSelectedAll: any = vm.settingListItemsDetails().every(item => item.isChecked() === true);
        //there is least one item which is not checked
        if (isSelectedAll === false) isSelectedAll = null;
        vm.isSelectAll(isSelectedAll);
      });

      // subscribe isSelectAll
      vm.isSelectAll.subscribe(newValue => {
        vm.selectAllChange(newValue);
      });

      //KDL 047, 048      
      vm.shareParam.titleLine.displayFlag = vm.isDisplayTitle();
      vm.shareParam.titleLine.layoutCode = vm.attendanceCode();
      vm.shareParam.titleLine.layoutName = vm.attendanceName();

      const positionText = vm.position() === 1 ? "上" : "下";
      vm.shareParam.titleLine.directText = vm.$i18n('KWR002_131') + vm.columnIndex() + vm.$i18n('KWR002_132') + positionText + vm.$i18n('KWR002_133');
      vm.shareParam.itemNameLine.displayFlag = vm.isDisplayItemName();
      vm.shareParam.itemNameLine.displayInputCategory = vm.isEnableTextEditor();
      vm.shareParam.itemNameLine.name = vm.attendanceItemName();
      vm.shareParam.attribute.selectionCategory = vm.isEnableComboBox();
      vm.shareParam.attribute.selected = vm.comboSelected();
      vm.shareParam.selectedTime = vm.tableSelected();

      vm.shareParam.attribute.attributeList = [
        new AttendaceType(1, vm.$i18n('KWR002_141')),
        new AttendaceType(2, vm.$i18n('KWR002_142')),
        new AttendaceType(3, vm.$i18n('KWR002_143')),
        /* new AttendaceType(4, vm.$i18n('KWR002_180')),
        new AttendaceType(5, vm.$i18n('KWR002_181')),
        new AttendaceType(6, vm.$i18n('KWR002_182')),
        new AttendaceType(7, vm.$i18n('KWR002_183')) */
      ]

      vm.shareParam.attendanceItems = vm.diligenceProjects();
      vm.shareParam.diligenceProjectList = vm.diligenceProjects();

    }

    created(params: any) {
      const vm = this;
    }

    mounted() {
      const vm = this;
      if (!!navigator.userAgent.match(/Trident.*rv\:11\./))
        $("#multiGridList").ntsFixedTable({ height: 368 });
      else
        $("#multiGridList").ntsFixedTable({ height: 370 });
    }

    addRowItem(newRow?: SettingForPrint) {
      let vm = this,
        row: SettingForPrint = newRow;

      if (!newRow) {
        let lastItem: any = _.last(vm.settingListItemsDetails());
        let id = lastItem ? lastItem.id : 1;
        row = new SettingForPrint(id, null, 0, null, false);
      }

      row.isChecked.subscribe((value: boolean) => {
        vm.settingListItemsDetails.valueHasMutated();
      });

      row.setting.subscribe((value) => {
        console.log(value);
        //vm.settingListItemsDetails.valueHasMutated();
      });

      vm.settingListItemsDetails.push(row);
    }

    addNewRow() {
      const vm = this;

      nts.uk.ui.errors.clearAll();
      vm.currentCodeList(null);

      vm.createDefaultSettingDetails();

      vm.isEnableDuplicateButton(false);
      vm.isEnableDeleteButton(false);

      vm.attendanceCode(null);
      vm.attendanceName(null);
      vm.isEnableAttendanceCode(true);
      vm.isNewMode(true);
      $('#KWR003_B42').focus();
    }

    /**
     * Registers setting
     */
    registerSetting() {
      const vm = this;

      let params: any = {
        code: '',
        name: '',
        settingListItemsDetails: vm.settingListItemsDetails()
      };

      if (vm.isNewMode()) {
        //コードが重複しているため、登録できません。 Msg_1753        
        let checkExist = _.find(vm.settingListItems(), ['code', _.trim(vm.attendanceCode())]);
        if (!_.isNil(checkExist)) {
          vm.$dialog.error({ messageId: 'Msg_1753' }).then(() => {
            $('#KWR003_B42').focus();
          });
          return;
        }
      } else { //update model
        //出力項目が削除されています。 ＃Msg_1903
        //get detail before update
        /* let temp = vm.settingListItemsDetails();
        temp = _.filter(temp, (x) => x.id !== 1);
        if (temp.length !== vm.settingListItemsDetails().length) {
          vm.$dialog.error({ messageId: 'Msg_1903' }).then(() => {
            $('#btnB11').focus();
          });
          return;
        } */
      }

      vm.settingAttendance();
      //register or update
      vm.addNewOrUpdate();

      //sort by name with desc
      let listItemsDetails: Array<any> = [];
      listItemsDetails = vm.orderListItemsByField(vm.settingListItemsDetails());
      vm.createListItemAfterSorted(listItemsDetails);
      //change to update status
      vm.isNewMode(false);

    }

    settingAttendance() {
      const vm = this;
      let returnAttendance: AttendanceItem = {
        code: vm.attendanceCode(),
        name: vm.attendanceName(),
        status: vm.isNewMode() ? 1 : 0, // 0: Update, 1: Addnew, 2: Remove
        settingCategory: vm.settingCategory()
      };
      vm.attendance(returnAttendance);
    }

    addNewOrUpdate() {
      const vm = this;

      let params: DataOutputDto = new DataOutputDto(),
        outputItemList: Array<OutputItemList> = [],
        attribute: Attribute = new Attribute();
      console.log(vm.settingListItemsDetails());
      _.forEach(vm.settingListItemsDetails(), (item) => {
        if (item.name() && item.selectedTimeList().length > 0) {
          let outputItem = new OutputItemList();
          outputItem.rank = item.id;
          outputItem.name = item.name();
          outputItem.printTargetFlag = item.isChecked();
          outputItem.independentCalClassic = item.setting();
          outputItem.itemDetailAtt = item.selected;
          outputItem.dailyMonthlyClassic = 1; //日次

          let outputItemDetails: Array<Attribute> = [];
          _.forEach(item.selectedTimeList(), (o) => {
            outputItemDetails.push({
              operator: (o.operator) ? (o.operator == '+' ? 1 : 2) : null,
              attendanceItemId: o.itemId
            });
          });
          outputItem.selectedAttItemList = outputItemDetails;

          outputItemList.push(outputItem);
        }
      });

      params.code = vm.attendanceCode();
      params.name = vm.attendanceName();
      params.settingId = vm.settingId();
      params.settingCategory = vm.settingCategory();
      params.outputItemList = outputItemList;

      console.log(params);

      let url = vm.isNewMode() ? PATH.createSettingItemDetails : PATH.updateSettingItemDetails;

      vm.$blockui('show');
      vm.$ajax(url, params).done(() => {
        vm.loadSettingList({ standOrFree: vm.settingCategory(), code: vm.attendanceCode() });
        vm.$dialog.info({ messageId: 'Msg_15' }).then(() => vm.$blockui('hide'));
      })
        .always(() => vm.$blockui('hide')).fail();
    }

    /**
     * Orders list items by field
     * @param [listItemsDetails] 
     * @param [field] 
     * @param [sort_type] 
     * @returns  
     */
    orderListItemsByField(listItemsDetails?: Array<any>, field: string = 'name', sort_type: string = 'desc') {
      let newListItemsDetails: Array<any> = [];
      _.forEach(listItemsDetails, (row, index) => {
        let temp = {
          id: row.id,
          isChecked: row.isChecked(),
          name: row.name(),
          setting: row.setting(),
          selectionItem: row.selectionItem(),
          selectedItemList: row.selectedTimeList(),
          selected: row.selected,
          selectedTime: row.selectedTime
        };

        newListItemsDetails.push(temp);
      });

      newListItemsDetails = _.orderBy(newListItemsDetails, [field], [sort_type]);

      return newListItemsDetails;
    }

    /**
     * Create list item after sorted
     * @param [listItemsDetails] 
     */
    createListItemAfterSorted(listItemsDetails?: Array<any>) {
      const vm = this;

      vm.settingListItemsDetails([]);
      _.forEach(listItemsDetails, (x: any) => {
        let newItem: SettingForPrint = new SettingForPrint(
          x.id, 
          x.name, 
          x.setting,
          x.selectionItem, 
          x.isChecked,
          x.selectedItemList,
          x.selected,
          x.selectedTime
          );
        vm.addRowItem(newItem);
      });
    }

    /**
     * Detele setting
     */
    deteleSetting() {
      const vm = this;

      let returnAttendance: AttendanceItem = {};
      returnAttendance.code = vm.attendanceCode();
      returnAttendance.status = 2; //Deleted
      vm.attendance(returnAttendance);

      //vm.settingAttendance();
      /*       
      vm.$blockui('show');

      const params = {
        code: vm.attendanceCode() //該当する設定ID
      };

      vm.$dialog.confirm({ messageId: 'Msg_18' }).then((answer: string) => {
        if (answer === 'yes') {
          vm.$ajax(PATH.deleteSettingItemDetails, params)
            .done(() => {
              vm.$dialog.info({ messageId: 'Msg_16' }).then(() => {
                let returnAttendance: AttendanceItem= {};

                returnAttendance.code = vm.attendanceCode();
                returnAttendance.status = 2; //Deleted

                vm.attendance(returnAttendance);
                vm.$blockui('hide');
              })
            })
            .always(() => {
              vm.$blockui('hide');
            })
            .fail((error) => {

            });
        }
      }); */
    }

    /**
     * Deletes setting item
     * @returns  
     */
    deleteSettingItem() {
      const vm = this;
      //get all items that will be remove
      let listCheckedItems: Array<any> = vm.settingListItemsDetails().filter((row) => row.isChecked() === true);
      if (listCheckedItems.length <= 0) return;

      //get all items that will be not remove
      let listNotCheckedItems: Array<any> = vm.settingListItemsDetails().filter((row) => row.isChecked() === false);
      vm.settingListItemsDetails(listNotCheckedItems);
    }
    /**
     * Duplicate Setting
     * */
    /**
     * Shows dialog C
     */
    showDialogC() {
      const vm = this;      

      let selectedObj = _.find(vm.settingListItems(), (x) => x.code === vm.currentCodeList());
      let params = {
        code: selectedObj.code,
        name: selectedObj.name,
        settingCategory: vm.settingCategory(),
        settingId: selectedObj.id //複製元の設定ID 
      }

      vm.$window.storage(KWR003_C_INPUT, params).then(() => {
        vm.$window.modal('/view/kwr/003/c/index.xhtml').then(() => {
          vm.$window.storage(KWR003_C_OUTPUT).then((data) => {
            if (_.isNil(data)) {
              return;
            }

            let duplicateItem = _.find(vm.settingListItems(), (x) => x.code === data.code);
            if (!_.isNil(duplicateItem)) {
              vm.$dialog.error({ messageId: 'Msg_1903' }).then(() => { });
              return;
            }

            vm.settingListItems.push(data);
            vm.currentCodeList(data.code);
          });
        });
      });

      $('#KWR003_B43').focus();
    }

    /**
     * Close dialog
     */
    closeDialog() {
      const vm = this;
      vm.$window.storage(KWR003_B_OUTPUT, vm.attendance());
      vm.$window.close();
    }

    /**
     * Get setting list items details
     */
    getSettingListItemsDetails() {
      const vm = this;

      let selectedObj = _.find(vm.settingListItems(), (x) => x.code === vm.currentCodeList());
      //get details the work table status
      let beginItems = 0;
      let listDaily = vm.diligenceProjects();
      
      vm.$blockui('show');

      vm.$ajax(PATH.getSettingLitsWorkStatusDetails, { settingId: selectedObj.id }).done((data) => {
        if (!_.isNil(data) && data.outputItemList.length > 0) {
          console.log(data);
          //total output item which registered
          beginItems = data.outputItemList.length;

          //order by list
          let listItemsDetails: Array<SettingForPrint> = [];
          _.forEach(data.outputItemList, (x, index: number) => {
            let dataItemsWithOperation: Array<selectedItemList> = [];

            //remove duplicate if yes
            let attendanceItemList = _.filter(x.attendanceItemList, (element: any, index, self) => {
              return index === _.findIndex(self, (d: any) => element.attendanceItemId === d.attendanceItemId);
            });

            //get selected items: 選択項目
            _.forEach(attendanceItemList, (element: any) => {
              let findObj = _.find(listDaily, (listItem: any) => listItem.attendanceItemId === element.attendanceItemId);
              if (findObj) {
                dataItemsWithOperation.push({
                  itemId: element.attendanceItemId,
                  indicatesNumber: element.attendanceItemId,
                  name: findObj.name,
                  operator: (element.operatorName === 'ADDITION') ? '+' : '-'
                });
              }
            });

            let newItem: SettingForPrint = new SettingForPrint(
              index + 1,
              x.name,
              x.independentCalClassic,
              vm.createDataSelection(dataItemsWithOperation),
              x.printTargetFlag,
              dataItemsWithOperation,
              x.itemDetailAtt);

            listItemsDetails.push(newItem);
            //vm.addRowItem(newItem);
          });

          //re-order the list
          listItemsDetails = vm.orderListItemsByField(listItemsDetails);          
          vm.createListItemAfterSorted(listItemsDetails);
        }

        //if beginItems is less then NUM_ROWS | 10
        for (let i = beginItems; i < NUM_ROWS; i++) {
          let newItem: SettingForPrint = new SettingForPrint(i + 1, null, 1, null, false);
          vm.addRowItem(newItem);
        }

        vm.$blockui('hide');

      }).fail(() => vm.$blockui('hide')).always(() => vm.$blockui('hide'));
    }

    /**
     * Creatse default setting details
     */
    createDefaultSettingDetails() {
      const vm = this;
      //clear
      vm.settingListItemsDetails([])
      for (let i = 0; i < NUM_ROWS; i++) {
        let newItem = new SettingForPrint(i + 1, null, 1, null, false);
        vm.addRowItem(newItem);
      }
    }
    /**
     * Creates data selection
     * @param selectedItemList 
     * @returns  
     */
    createDataSelection(selectedItemList: Array<any>) {
      let vm = this,
        dataSelection: string = '',
        selectionItem: Array<string> = [];

      _.forEach(selectedItemList, (item, index: number) => {
        if (index === 0 && item.operator.substring(0, 1) === '+') {
          selectionItem.push(item.name);
        } else {
          selectionItem.push(item.operator + ' ' + item.name);
        }
      });

      if (selectionItem.length > 0) {
        dataSelection = _.join(selectionItem, ' ');
        if (dataSelection.length > 20) {
          dataSelection = dataSelection.substring(0, 19) + vm.$i18n('KWR003_219');
        }
      }

      return dataSelection;
    }

    getSettingListForPrint(code: string) {
      const vm = this;
      if (!_.isNil(code)) {
        let selectedObj = _.find(vm.settingListItems(), (x: any) => x.code === code);
        if (!_.isNil(selectedObj)) {
          vm.attendanceCode(selectedObj.code);
          vm.attendanceName(selectedObj.name);
          //KDL 047, 048
          vm.shareParam.titleLine.layoutCode = vm.attendanceCode();
          vm.shareParam.titleLine.layoutName = vm.attendanceName();

          vm.isEnableAttendanceCode(false);
          vm.isEnableAddButton(true);
          vm.isEnableDeleteButton(true);
          vm.isEnableDuplicateButton(true);
          vm.isNewMode(false);
          vm.settingAttendance();
        }
      }

      $('#KWR003_B43').focus();
    }

    getWorkStatusTableOutput() {
      const vm = this;
      vm.$blockui('show');

      vm.workStatusTableOutputItem = ko.observable({ listDaily: [], listMonthly: [] });
      vm.$ajax(PATH.getFormInfo, { formNumberDisplay: 6 }).done((result) => {
        //vm.workStatusTableOutputItem(result);
        if (result && result.listDaily) {
          _.forEach(result.listDaily, (item) => {
            vm.diligenceProjects.push(new DiligenceProject(
              item.attendanceItemId,
              item.attendanceItemName,
              item.attributes,
              item.attendanceItemDisplayNumber
            )
            );
          });
        }
        vm.$blockui('hide');
      }).always(() => vm.$blockui('hide'));
    }

    /**
     * Gets setting listwork status
     * @param type 
     * 定型選択	: 0
     * 自由設定 : 1
     */
    getSettingListWorkStatus(type: number): any {
      const vm = this;
      let listWorkStatus: Array<ItemModel> = [];
      //定型選択		
      vm.$ajax(PATH.getSettingListWorkStatus, { setting: type }).then((data) => {
        console.log(data);
        if (!_.isNil(data)) {
          _.forEach(data, (item) => {
            listWorkStatus.push(new ItemModel(item.settingDisplayCode, item.settingName, item.settingId));
          });
        }
        return listWorkStatus;
      });
    }

    getSettingList() {
      const vm = this;
      vm.$window.storage(KWR003_B_INPUT).then((data: any) => {
        if (!data) return;
        vm.settingCategory(data.standOrFree);
        vm.settingId(data.settingId);        
        vm.loadSettingList(data);
      });
    }

    loadSettingList(params: any) {
      const vm = this;
      let listWorkStatus: Array<any> = [];
      vm.$ajax(PATH.getSettingListWorkStatus, { setting: params.standOrFree }).then((data) => {
        if (!_.isNil(data)) {
          _.forEach(data, (item) => {
            listWorkStatus.push(new ItemModel(item.settingDisplayCode, item.settingName, item.settingId));
          });
        }

        //sort by code with asc
        vm.settingListItems([]);
        listWorkStatus = _.orderBy(listWorkStatus, ['code'], ['asc']);
        vm.settingListItems(listWorkStatus);

        let code = !_.isNil(data) ? params.code : null;
        if (vm.settingListItems().length > 0) {
          let firstItem: any = _.head(vm.settingListItems());
          if (!code) code = firstItem.code;
        }

        vm.currentCodeList(code);
        //vm.getSettingListForPrint(code);
        //vm.getSettingListItemsDetails();
      });
    }
    /**
     * Opens dialog KDL
     * @param data 
     */
    openDialogKDL(data: SettingForPrint) {
      const vm = this;

      if (data.setting() === 2)
        vm.openDialogKDL048(data);
      else
        vm.openDialogKDL047(data);
    }
    /**
     * Opens dialog kdl047
     * @param row 
     */
    openDialogKDL047(row: any) {
      const vm = this;

      vm.shareParam.itemNameLine.name = row.name();
      vm.shareParam.attribute.selected = row.selected; //setting Category
      vm.shareParam.selectedTime = row.selectedTime;

      nts.uk.ui.windows.setShared('attendanceItem', vm.shareParam, true);
      nts.uk.ui.windows.sub.modal('/view/kdl/047/a/index.xhtml').onClosed(() => {
        const attendanceItem = nts.uk.ui.windows.getShared('attendanceRecordExport');

        if (_.isNil(attendanceItem)) {
          return;
        }

        if (nts.uk.ui.errors.hasError()) nts.uk.ui.errors.clearAll();
        let index = _.findIndex(vm.settingListItemsDetails(), (o: any) => { return o.id === row.id; });
        if (attendanceItem.attendanceItemName) {
          vm.settingListItemsDetails()[index].name(attendanceItem.attendanceItemName);
          $('#textName' + vm.settingListItemsDetails()[index].id).ntsError('clear');
        }

        let findAttendanceName = _.find(vm.shareParam.attendanceItems, (x: any) => {
          return x.attendanceItemId === parseInt(attendanceItem.attendanceId);
        });

        //選択項目
        if (!_.isNil(findAttendanceName)) {
          vm.settingListItemsDetails()[index].selectionItem(findAttendanceName.attendanceItemName);

          let listItem: selectedItemList = {};
          listItem.itemId = parseInt(attendanceItem.attendanceId);
          listItem.name = findAttendanceName.attendanceItemName;
          listItem.operator = '+'; //+
          vm.settingListItemsDetails()[index].selectedTimeList.push(listItem);

          vm.settingListItemsDetails()[index].selected = attendanceItem.attribute;
          vm.settingListItemsDetails()[index].selectedTime = attendanceItem.attendanceId;
          
          if (!attendanceItem.attendanceItemName) {
            $('#textName' + row.id).ntsError('set', { messageId: "MsgB_1" });
            $('#textName' + row.id).focus();
          }
        } else {
          vm.settingListItemsDetails()[index].name(null);
          vm.settingListItemsDetails()[index].selectionItem(null);
          vm.settingListItemsDetails()[index].selectedTimeList([]);
          vm.settingListItemsDetails()[index].selectedTime = -1;
          vm.settingListItemsDetails()[index].selected = 0;
        }
      });
    }

    /**
     * Opens dialog kdl048
     * @param row 
     */
    openDialogKDL048(row: any) {
      let vm = this,
        selectionItem: Array<string> = [];

      vm.shareParam.attribute.attributeList = [
        new AttendaceType(4, vm.$i18n('KWR002_180')),
        new AttendaceType(5, vm.$i18n('KWR002_181')),
        //new AttendaceType(6, vm.$i18n('KWR002_182')),
        new AttendaceType(7, vm.$i18n('KWR002_183'))
      ]

      vm.shareParam.itemNameLine.name = row.name();
      vm.shareParam.attribute.selected = row.selected; //setting Category

      if (!_.isNil(row.selectedTimeList())) {
        vm.shareParam.selectedTimeList = row.selectedTimeList();
      }

      nts.uk.ui.windows.setShared('attendanceItem', vm.shareParam, true);
      nts.uk.ui.windows.sub.modal('/view/kdl/048/index.xhtml').onClosed(() => {
        const attendanceItem = nts.uk.ui.windows.getShared('attendanceRecordExport');

        if (!attendanceItem) return;
        let index = _.findIndex(vm.settingListItemsDetails(), (o: any) => { return o.id === row.id; });
        if (attendanceItem.selectedTimeList.length > 0) {
          //clear error on input
          if (nts.uk.ui.errors.hasError()) nts.uk.ui.errors.clearAll();
          let dataSelection: string = vm.createDataSelection(attendanceItem.selectedTimeList);
          console.log(index);
          if (index > -1) {
            vm.settingListItemsDetails()[index].name(attendanceItem.itemNameLine.name);
            vm.settingListItemsDetails()[index].selectionItem(dataSelection);
            vm.settingListItemsDetails()[index].selectedTimeList(attendanceItem.selectedTimeList);
            vm.settingListItemsDetails()[index].selected = attendanceItem.attribute.selected;
            if (!attendanceItem.itemNameLine.name) {
              $('#textName' + row.id).ntsError('set', { messageId: "MsgB_1" });
              $('#textName' + row.id).focus();
            }
          }
        } else {
          vm.settingListItemsDetails()[index].name(null);
          vm.settingListItemsDetails()[index].selectionItem(null);
          vm.settingListItemsDetails()[index].selectedTimeList([]);
          vm.settingListItemsDetails()[index].selectedTime = -1;
          vm.settingListItemsDetails()[index].selected = 0;
        }
      });
    }


    selectAllChange(newValue: boolean) {
      const vm = this;

      if (newValue === null) return;

      _.forEach(vm.settingListItemsDetails(), (row, index) => {
        row.isChecked(newValue);
      })
    }
  }

  //=================================================================
  export interface AttendanceItem {
    code?: string;
    name?: string;
    status?: number;
    settingCategory?: number;
  }

  export class ItemModel {
    id: string;
    code: string;
    name: string;
    constructor(code?: string, name?: string, id?: string) {
      this.code = code;
      this.name = name;
      this.id = id;
    }
  }

  export class SettingForPrint {
    id: number;
    isChecked: KnockoutObservable<boolean> = ko.observable(false);
    name: KnockoutObservable<string> = ko.observable(null);
    setting: KnockoutObservable<number> = ko.observable(1); // 1 単独 | 2	計算 
    selected: number = 0;
    selectedTime: number = -1;
    selectionItem: KnockoutObservable<string> = ko.observable(null);
    selectedTimeList: KnockoutObservableArray<selectedItemList> = ko.observableArray([]);

    constructor(
      id?: number,
      name?: string,
      setting?: number,
      selectionItem?: string,
      checked?: boolean,
      selectedTimeList?: Array<selectedItemList>,
      selected: number = 0,
      selectedTime: number = -1) {
      this.name(name || '');
      this.setting(setting);
      this.isChecked(checked || false);
      this.selectionItem(selectionItem || ''); //display
      this.id = id;
      this.selectedTimeList(selectedTimeList || []);
      this.selected = selected;
      this.selectedTime = selectedTime;
    }
  }

  export class Model {
    code: string;
    name: string;
    settingForPrint: Array<SettingForPrint>;
    constructor(code?: string, name?: string, settings?: Array<SettingForPrint>) {
      this.code = code;
      this.name = name;
      this.settingForPrint = settings;
    }
  }

  export interface selectedItemList {
    itemId?: number;
    operator?: string | number;
    name?: string;
    indicatesNumber?: number
  }
  //KDL 047, 048
  // Display object mock
  export class SharedParams {
    // タイトル行
    titleLine: TitleLineObject = new TitleLineObject();
    // 項目名行
    itemNameLine: ItemNameLineObject = new ItemNameLineObject();
    // 属性
    attribute: AttributeObject = new AttributeObject();
    // List<勤怠項目>KDL 048
    diligenceProjectList: DiligenceProject[] = [];
    // List<勤怠項目> KDL 047
    attendanceItems: DiligenceProject[] = [];
    // List<選択済み勤怠項目>
    selectedTimeList: selectedItemListParam[] = [];
    // 選択済み勤怠項目ID
    selectedTime: number;
  }
  export class selectedItemListParam {
    // 項目ID
    itemId: any | null = null;
    // 演算子
    operator: String | null = null;
    name: string | null = null;
    indicatesNumber: any | null = null;
    constructor(itemId: any, operator: String, name?: string, indicatesNumber?: any) {
      this.itemId = itemId;
      this.operator = operator;
      this.name = name;
      this.indicatesNumber = indicatesNumber;
    }
  }

  export class TitleLineObject {
    // 表示フラグ
    displayFlag: boolean = false;
    // 出力項目コード
    layoutCode: String | null = null;
    // 出力項目名
    layoutName: String | null = null;
    // コメント
    directText: String | null = null;
  }

  export class ItemNameLineObject {
    // 表示フラグ
    displayFlag: boolean = false;
    // 表示入力区分
    displayInputCategory: number = 1;
    // 名称
    name: String | null = null;
  }

  export class AttributeObject {
    // 選択区分
    selectionCategory: number = 2;
    // List<属性>
    attributeList: AttendaceType[] = [];
    // 選択済み
    selected: number = 1;

    constructor(init?: Partial<AttributeObject>) {
      $.extend(this, init);
    }
  }

  export class AttendaceType {
    attendanceTypeCode: number;
    attendanceTypeName: string;
    constructor(attendanceTypeCode: number, attendanceTypeName: string) {
      this.attendanceTypeCode = attendanceTypeCode;
      this.attendanceTypeName = attendanceTypeName;
    }
  }

  export class DiligenceProject {
    attendanceItemId: any;
    attendanceItemName: any;
    attributes: any;
    displayNumbers: any;
    //48
    // ID
    id: any;
    // 名称
    name: any;
    // 属性
    //attributes: any;
    // 表示番号
    indicatesNumber: any;
    constructor(id: any, name: any, attributes: any, indicatesNumber: any) {
      this.attendanceItemId = id;
      this.attendanceItemName = name;
      this.attributes = attributes;
      this.displayNumbers = indicatesNumber;
      //48
      this.id = id;
      this.name = name;
      //this.attributes = attributes;
      this.indicatesNumber = indicatesNumber;
    }
  }

  //save
  export class DataOutputDto {
    settingId?: string;
    code: string;
    name: string;
    settingCategory: number; // 0 定型選択 || 1 自由設定
    outputItemList: Array<any>;

    constructor(init?: Partial<DataOutputDto>) {
      $.extend(this, init);
    }
  }

  export class OutputItemList {
    rank: number; //項目の順位
    name: string; //名称
    printTargetFlag: boolean; //印刷対象フラグ
    independentCalClassic: number; //単独計算区分 //1 	単独 || 2	計算
    dailyMonthlyClassic: number; //日次月次区分 1	日次 || //2 月次
    itemDetailAtt: number; //項目詳細の属性
    selectedAttItemList: Array<any>; // 選択勤怠項目リスト

    constructor(init?: Partial<OutputItemList>) {
      $.extend(this, init);
    }
  }

  export class Attribute {
    operator: number | string; //演算子 1	加算 || 2	減算 || null
    attendanceItemId: number; //勤怠項目ID

    constructor(init?: Partial<Attribute>) {
      $.extend(this, init);
    }
  }
}