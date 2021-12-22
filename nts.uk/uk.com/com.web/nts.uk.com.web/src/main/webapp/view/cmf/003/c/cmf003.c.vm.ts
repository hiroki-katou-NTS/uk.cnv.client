/* module nts.uk.com.view.cmf003.c {
    import getText = nts.uk.resource.getText;
    import close = nts.uk.ui.windows.close;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    import alertError = nts.uk.ui.dialog.alertError;
    export module viewmodel {
        export class ScreenModel {

            // category
            categoriesDefault: KnockoutObservableArray<ItemCategory>;
            categoriesSelected: KnockoutObservableArray<ItemCategory>;
            columns: KnockoutObservableArray<nts.uk.ui.NtsGridListColumn>;
            currentCateSelected: KnockoutObservableArray<ItemSystemType>;

            // systemType
            systemTypes: KnockoutObservableArray<number>;
            selectedCode: KnockoutObservable<string> = ko.observable(null);
            currentItem: KnockoutObservable<ItemSystemType>;
            isEnable: KnockoutObservable<boolean>;
            isEditable: KnockoutObservable<boolean>;
            headerCodeCategories: string = getText("CMF003_65");
            headerNameCategories: string = getText("CMF003_66");

            systemtypeFromB: KnockoutObservable<ItemSystemType>;
            categoriesFromB: KnockoutObservable<any>;
            constructor() {

                var self = this;
                let categoriesFB = getShared('CMF003_B_CATEGORIES');
                let systemtypeFB = getShared('CMF003_B_SYSTEMTYPE');
                self.currentCateSelected = ko.observableArray([]);
                var systemIdSelected;
                self.systemTypes = ko.observableArray([]);
                service.getSysTypes().done(function(data: Array<any>) {
                    if (data && data.length) {
                        _.forOwn(data, function(index) {
                            self.systemTypes.push(new ItemSystemType(index.type + '', index.name));
                        });

                        if (systemtypeFB != undefined) { 
                            systemIdSelected = systemtypeFB.code; 
                        } else { 
                            systemIdSelected = self.systemTypes()[0].code; 
                        }
                        self.selectedCode(systemIdSelected);
                    } else {

                    }

                }).fail(function(error) {
                    alertError(error);

                }).always(() => {

                });

                self.isEnable = ko.observable(true);
                self.isEditable = ko.observable(true);
                self.categoriesDefault = ko.observableArray([]);
                self.selectedCode.subscribe(value => {
                    if (value && value.length > 0) {
                    self.currentItem = _.find(self.systemTypes(), a => a.code === value);
                        service.getConditionList(parseInt(self.selectedCode())).done(function(data: Array<any>) {
                            
                            data = _.sortBy(data, ["categoryId"]);
                            if (systemtypeFB != undefined) {
                                _.forOwn(categoriesFB, function(index) {
                                     _.remove(data, function (e) {
                                            return e.categoryId == index.categoryId;
                                        });
                                });
                                self.currentCateSelected(categoriesFB);
                           }
                            self.categoriesDefault(data);
                            $("#swap-list-grid1 tr:first-child").focus();
                            
                        }).fail(function(error) {
                            alertError(error);
                        }).always(() => {
                            _.defer(() => {
                                $("#grd_Condition tr:first-child").focus();
                            });
                        });
                    }
                });

                self.columns = ko.observableArray([
                    { headerText: self.headerCodeCategories, key: 'categoryId', width: 70 },
                    { headerText: self.headerNameCategories, key: 'categoryName', width: 250 }
                ]);


                self.categoriesSelected = self.currentCateSelected;

            }

            remove() {
                let self = this;
                self.categoriesDefault.shift();
            }

            closeUp() {
                close();
            }

            submit() {
                let self = this;
                if (self.currentCateSelected().length == 0) {
                    alertError({ messageId: "Msg_577" });
                } else {
                    setShared("CMF003_C_CATEGORIES", JSON.stringify(self.currentCateSelected()));
                    setShared("CMF003_C_SYSTEMTYPE", self.currentItem);
                    close();
                }
            }

        }
    }

    class ItemCategory {
        schelperSystem: number;
        categoryId: string;
        categoryName: string;
        possibilitySystem: number;
        storedProcedureSpecified: number;
        timeStore: number;
        otherCompanyCls: number;
        attendanceSystem: number;
        recoveryStorageRange: number;
        paymentAvailability: number;
        storageRangeSaved: number;
        constructor(schelperSystem: number, categoryId: string, categoryName: string, possibilitySystem: number,
            storedProcedureSpecified: number, timeStore: number, otherCompanyCls: number, attendanceSystem: number,
            recoveryStorageRange: number, paymentAvailability: number, storageRangeSaved: number) {
            this.schelperSystem = schelperSystem;
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.possibilitySystem = possibilitySystem;
            this.storedProcedureSpecified = storedProcedureSpecified;
            this.timeStore = timeStore;
            this.otherCompanyCls = otherCompanyCls;
            this.attendanceSystem = attendanceSystem;
            this.recoveryStorageRange = recoveryStorageRange;
            this.paymentAvailability = paymentAvailability;
            this.storageRangeSaved = storageRangeSaved;
        }
    }

    class ItemSystemType {
        code: string;
        name: string;
        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }



} */

module nts.uk.com.view.cmf003.c {
  import getText = nts.uk.resource.getText;
  import TextEditorOption = nts.uk.ui.option.TextEditorOption;
  import NtsGridListColumn = nts.uk.ui.NtsGridListColumn;

  @bean()
  export class ViewModel extends ko.ViewModel {
    screenMode: KnockoutObservable<number> = ko.observable();
    isNewMode: KnockoutObservable<boolean> = ko.computed(() => {
      const vm = this;
      return vm.screenMode() === ScreenMode.NEW;
    });

    //Pattern list
    patternList: KnockoutObservableArray<Pattern> = ko.observableArray([]);
    selectedPatternCode: KnockoutObservable<string> = ko.observable('');
    patternColumns: KnockoutObservableArray<any> = ko.observableArray([
      { headerText: getText('CMF003_23'), key: 'displayCode', width: 75 },
      { headerText: getText('CMF003_632'), key: 'patternName', width: 250 }
    ]);

    //Pattern
    codeValue: KnockoutObservable<string> = ko.observable('');
    nameValue: KnockoutObservable<string> = ko.observable('');

    options: TextEditorOption = new TextEditorOption({
      width: '150px'
    });

    //Category list
    readonly categoriesDefault: KnockoutObservableArray<Category> = ko.observableArray([]);
    categoriesFiltered: KnockoutObservableArray<Category> = ko.observableArray([]);
    leftColumns: KnockoutObservableArray<NtsGridListColumn> = ko.observableArray([
      { headerText: '', key: 'id', hidden: true },
      { headerText: getText('CMF003_65'), key: 'categoryId', width: 70 },
      { headerText: getText('CMF003_66'), key: 'displayName', width: 250 },
    ]);
    rightColumns: KnockoutObservableArray<NtsGridListColumn> = ko.observableArray([
      { headerText: '', key: 'id', hidden: true },
      { headerText: getText('CMF003_65'), key: 'categoryId', width: 70 },
      { headerText: getText('CMF003_66'), key: 'displayName', width: 250 },
      { headerText: getText('CMF003_636'), key: 'retentionPeriod', width: 80 }
    ]);
    currentCateSelected: KnockoutObservableArray<Category> = ko.observableArray([]);
    systemTypes: KnockoutObservableArray<ItemModel> = ko.observableArray([
      new ItemModel(0, ' '),
    ]);
    selectedSystemType: KnockoutObservable<number> = ko.observable(0);

    //Auto execution
    saveFormatChecked: KnockoutObservable<boolean> = ko.observable(false);
    saveFormatEnabled: KnockoutObservable<boolean> = ko.observable(true);
    disableMoveButton: KnockoutObservable<boolean> = ko.observable(false);
    usePasswordChecked: KnockoutObservable<boolean> = ko.observable(false);
    targetYearDD: KnockoutObservableArray<ItemModel> = ko.observableArray([
      new ItemModel(1, getText('CMF003_405')),
      new ItemModel(2, getText('CMF003_406')),
      new ItemModel(3, getText('CMF003_407'))
    ]);
    targetMonthDD: KnockoutObservableArray<ItemModel> = ko.observableArray([
      new ItemModel(1, getText('CMF003_408')),
      new ItemModel(2, getText('CMF003_409')),
      new ItemModel(3, getText('CMF003_410')),
      new ItemModel(4, getText('CMF003_411')),
      new ItemModel(5, getText('CMF003_412')),
      new ItemModel(6, getText('CMF003_413')),
      new ItemModel(7, getText('CMF003_414')),
      new ItemModel(8, getText('CMF003_415')),
      new ItemModel(9, getText('CMF003_416')),
      new ItemModel(10, getText('CMF003_417')),
      new ItemModel(11, getText('CMF003_418')),
      new ItemModel(12, getText('CMF003_419'))
    ]);
    selectedDailyTargetYear: KnockoutObservable<number> = ko.observable(1);
    selectedDailyTargetMonth: KnockoutObservable<number> = ko.observable(1);
    selectedMonthlyTargetYear: KnockoutObservable<number> = ko.observable(1);
    selectedMonthlyTargetMonth: KnockoutObservable<number> = ko.observable(1);
    selectedAnnualTargetYear: KnockoutObservable<number> = ko.observable(1);
    password: KnockoutObservable<string> = ko.observable('');
    confirmPassword: KnockoutObservable<string> = ko.observable('');
    isCheckboxActive: KnockoutObservable<boolean> = ko.observable(false);
    explanation: KnockoutObservable<string> = ko.observable('');

    mounted() {
      const vm = this;
      vm.usePasswordChecked.subscribe(value => {
        if (value) {
          $(".password-input").trigger("validate");
        } else {
          $('.password-input').ntsError('clear');
        }
      });

      vm.selectedSystemType.subscribe(value => {
        let chain = _.chain(vm.categoriesDefault())
                    .filter(item => !_.find(vm.currentCateSelected(), { categoryId: item.categoryId, systemType: item.systemType })); // Filter out selected categories
        if (Number(value) !== 0) {
          chain = chain.filter({ systemType: Number(value) - 1 });  // Filter only selected systemType (if needed)
        }
        vm.categoriesFiltered(chain.sortBy("categoryId").value())   // Sort categories by categoryId asc
        vm.categoriesFiltered.valueHasMutated();
      });

      vm.usePasswordChecked.subscribe(value => {
        if (!value) {
          vm.password('');
          vm.confirmPassword('');
        }
      });

      vm.selectedPatternCode.subscribe(value => {
        const pattern = vm.getPatternById(value);
        if (pattern) {
          vm.selectPattern(pattern.code, pattern.patternClassification);
        }
      });

      vm.screenMode.subscribe(value => {
        if (Number(value) === ScreenMode.NEW) {
          vm.selectedPatternCode('');
          $("#C7_2").focus();
        } else {
          $("#C7_4").focus();
        }
      });

      vm.initDisplay();
    }

    private initDisplay() {
      const vm = this;
      vm.screenMode(ScreenMode.NEW);
      vm.$blockui("grayout");
      service.initDisplay()
        .then((res) => {
          vm.checkInCharge(res.pic);
          let patternArr: Pattern[] = [];
          _.map(res.patterns, (x: any) => {
            let p = new Pattern();
            p.code = x.patternCode;
            p.patternName = x.patternName;
            p.patternClassification = x.patternClassification;
            p.displayCode = x.patternClassification + x.patternCode;
            patternArr.push(p);
          });
            
          let arr: Category[] = [];
          _.map(res.categories, (x: any) => {
            let c = vm.convertToCategory(x);
            arr.push(c);
          });
          vm.categoriesDefault(arr);

          if(patternArr.length > 0){
              vm.patternList(patternArr);
              vm.patternList(_.orderBy(vm.patternList(), ['patternClassification', 'code'], ['desc', 'asc']));
              vm.selectedPatternCode(vm.patternList()[0].displayCode);
          }    
            
          _.forEach(vm.categoriesDefault(), item => vm.categoriesFiltered().push(item));
          vm.categoriesFiltered.valueHasMutated();

        }).always(() => {
          vm.$blockui("clear");
          // Move C1_4 into position
          $("#C1_4").appendTo("#C2-search-area");
        })
        .fail(err => vm.$dialog.error({ messageId: err.messageId }));
    }

    public refreshNew() {
      const vm = this;
      vm.screenMode(ScreenMode.NEW);
      vm.selectedAnnualTargetYear(vm.getDefaultItem(vm.targetYearDD()).code);
      vm.selectedDailyTargetYear(vm.getDefaultItem(vm.targetYearDD()).code);
      vm.selectedMonthlyTargetYear(vm.getDefaultItem(vm.targetYearDD()).code);
      vm.selectedDailyTargetMonth(vm.getDefaultItem(vm.targetMonthDD()).code);
      vm.selectedMonthlyTargetMonth(vm.getDefaultItem(vm.targetMonthDD()).code);
      vm.codeValue('');
      vm.nameValue('');
      vm.selectedSystemType(0);
      vm.saveFormatChecked(false);
      vm.usePasswordChecked(false);
      vm.password('');
      vm.confirmPassword('');
      vm.explanation('');
      vm.categoriesFiltered([]);
      _.forEach(vm.categoriesDefault(), item => vm.categoriesFiltered().push(item));
      vm.currentCateSelected([]);
      vm.disableMoveButton(false);
      vm.saveFormatEnabled(true);
      vm.$errors("clear");
    }

    public register() {
      const vm = this;
      if (vm.validateBeforeRegister()) {
        vm.$blockui("grayout");

        let param: any = {};
        param.screenMode = Number(vm.screenMode());
        param.patternCode = vm.codeValue();
        param.patternName = vm.nameValue();
        param.categoriesMaster = vm.currentCateSelected();
        param.idenSurveyArch = vm.saveFormatChecked();
        param.dailyReferYear = vm.selectedDailyTargetYear();
        param.dailyReferMonth = vm.selectedDailyTargetMonth();
        param.monthlyReferMonth = vm.selectedMonthlyTargetMonth();
        param.monthlyReferYear = vm.selectedMonthlyTargetYear();
        param.annualReferYear = vm.selectedAnnualTargetYear();
        param.patternCompressionPwd = vm.password();
        param.withoutPassword = Boolean(vm.usePasswordChecked()) ? 1 : 0;
        param.patternSuppleExplanation = vm.explanation();

        service.addPattern(param).then(() => {
          vm.$dialog.info({ messageId: "Msg_15" });
          if (vm.screenMode() === ScreenMode.NEW) {
            let pattern: Pattern = new Pattern();
            pattern.code = param.patternCode;
            pattern.patternClassification = 0;
            pattern.patternName = param.patternName;
            pattern.displayCode = pattern.patternClassification + pattern.code;
            vm.patternList.push(pattern);
            vm.patternList(_.orderBy(vm.patternList(), ['patternClassification', 'code'], ['desc', 'asc']));
            vm.selectPattern(pattern.code, pattern.patternClassification);

            if (vm.selectedPatternCode() === '') {
              vm.selectedPatternCode(pattern.displayCode);
            }
          } else {
            _.find(vm.patternList(), { 'code': param.patternCode }).patternName = param.patternName;
            vm.patternList.valueHasMutated();
          }
        }).fail((err) => {
          vm.$dialog.error({ messageId: 'Msg_3' });
        })
          .always(() => {
            vm.$blockui("clear");
          })
      }
    }

    public duplicate() {
      const vm = this;
      vm.screenMode(ScreenMode.NEW);
      vm.saveFormatEnabled(true);
      vm.codeValue('');
      vm.nameValue('');
    }

    public deletePattern() {
      const vm = this;
      /**
       * 確認メッセージ（Msg_18）を表示する
       */
      vm.$dialog.confirm({ messageId: "Msg_18" }).then((result: 'no' | 'yes' | 'cancel') => {
        /**
         * 「いいえ」（ID：Msg_36）をクリック
         */
        if (result === 'no') {
          /**
           * 終了状態：削除処理をキャンセル
           */
          return;
        }
        /**
         * 「はい」（ID：Msg_35）をクリック
         */
        if (result === 'yes') {
          /**
           * 終了状態：削除処理を実行
           */
          let param: any = {};
          vm.$blockui("grayout");
          const pattern = vm.getPatternById(vm.selectedPatternCode());
          param.patternCode = pattern.code;
          param.patternClassification = pattern.patternClassification;

          service.deletePattern(param).then(() => {
            const index: number = vm.patternList().indexOf(pattern);
            if (index > -1) {
              vm.patternList().splice(index, 1);
              vm.patternList.valueHasMutated();
              vm.selectedPatternCode('');
              if (vm.patternList().length === 0) {
                vm.refreshNew();
              } else if (vm.patternList().length === index) {
                vm.selectPatternByIndex(index - 1);
              } else {
                vm.selectPatternByIndex(index);
              }
            }
            vm.$dialog.info({ messageId: "Msg_16" });
          }).always(() => {
            vm.$blockui("clear");
          });
        }
      });
    }

    public selectPattern(patternCode: string, patternClassification: number) {
      const vm = this;
      vm.$blockui("grayout");
      let param: any = {};
      param.patternCode = patternCode;
      param.patternClassification = patternClassification;
      param.categories = vm.categoriesDefault();
      service.selectPattern(param).then((res) => {
        vm.screenMode(ScreenMode.UPDATE);
        vm.screenMode.valueHasMutated();
        if (res.pattern) {
          const pattern: any = res.pattern;
          vm.codeValue(pattern.patternCode);
          vm.nameValue(pattern.patternName);
          vm.categoriesFiltered([]);
          let arr: Category[] = [];
          _.forEach(res.selectableCategories, c => {
            let category = vm.convertToCategory(c);
            arr.push(category);
          });
          vm.categoriesFiltered(arr);
          arr = [];
          vm.currentCateSelected([]);
          _.forEach(res.pattern.selectCategories, c => {
            let category = vm.convertToCategory(c);
            arr.push(category);
          });
          vm.currentCateSelected(arr);
          vm.saveFormatChecked(pattern.idenSurveyArch === 1);
          vm.saveFormatEnabled(pattern.patternClassification === 0);
          vm.disableMoveButton(pattern.patternClassification === 1);
          vm.selectedDailyTargetMonth(vm.getReferValue(pattern.dailyReferMonth));
          vm.selectedDailyTargetYear(vm.getReferValue(pattern.dailyReferYear));
          vm.selectedMonthlyTargetMonth(vm.getReferValue(pattern.monthlyReferMonth));
          vm.selectedMonthlyTargetYear(vm.getReferValue(pattern.monthlyReferYear));
          vm.selectedAnnualTargetYear(vm.getReferValue(pattern.annualReferYear));
          vm.usePasswordChecked(pattern.withoutPassword === 1);
          vm.password(pattern.patternCompressionPwd);
          vm.confirmPassword(pattern.patternCompressionPwd);
          vm.explanation(pattern.patternSuppleExplanation);
          vm.selectedSystemType(0);
        }

        //revalidate
        vm.$errors("clear");
      }).always(() => {
        vm.$blockui("clear");
      });
    }

    private selectPatternByIndex(index: number) {
      const vm = this;
      const pattern: Pattern = vm.patternList()[index];
      if (pattern) {
        if (vm.selectedPatternCode() === '') {
          vm.selectedPatternCode(pattern.displayCode);
        }
        vm.selectPattern(pattern.code, pattern.patternClassification);
      }
    }

    private getDefaultItem(arr: ItemModel[]): ItemModel {
      return arr[0];
    }

    private getReferValue(value: any): number {
      return value ?? 1;
    }

    private getPatternById(id: string): Pattern {
      const vm = this;
      return _.filter(vm.patternList(), p => p.code === id.substring(1)).pop();
    }

    private convertToCategory(c: any): Category {
      const vm = this;
      let category = new Category();
      category.categoryId = c.categoryId;
      category.categoryName = c.categoryName;
      category.retentionPeriod = getText(c.retentionPeriod);
      category.systemType = c.systemType;
      category.patternClassification = c.patternClassification;
      category.displayName = c.categoryName + nts.uk.text.format(getText('CMF003_634'), vm.getSystemText(c.systemType));
      return category;
    }

    private checkInCharge(pic: LoginPersionInCharge) {
      const vm = this;
      if (pic.personnel)
        vm.systemTypes.push(new ItemModel(1, getText('CMF003_400')));
      if (pic.attendance)
        vm.systemTypes.push(new ItemModel(2, getText('CMF003_401')));
      if (pic.payroll)
        vm.systemTypes.push(new ItemModel(3, getText('CMF003_402')));
      if (pic.officeHelper)
        vm.systemTypes.push(new ItemModel(4, getText('CMF003_403')));
    }

    private getSystemText(type: number): string {
      switch (type) {
        case 0: return getText('Enum_SystemType_PERSON_SYSTEM');
        case 1: return getText('Enum_SystemType_ATTENDANCE_SYSTEM');
        case 2: return getText('Enum_SystemType_PAYROLL_SYSTEM');
        case 3: return getText('Enum_SystemType_OFFICE_HELPER');
      }
    }

    private validateBeforeRegister(): boolean {
      const vm = this;
      $("#C7_2 input").trigger("validate");
      $("#C7_4 input").trigger("validate");

      if (vm.usePasswordChecked()) {
        $("#C9_3 input").trigger("validate");
        $("#C9_6 input").trigger("validate");
        if (!nts.uk.ui.errors.hasError()) {
          if (vm.password() !== vm.confirmPassword()) {
            vm.$dialog.error({ messageId: 'Msg_566' });
            return false;
          }
        }
      }

      if (Number(vm.currentCateSelected().length) === 0) {
        vm.$dialog.error({ messageId: 'Msg_577' });
        return false;
      }

      if (nts.uk.ui.errors.hasError()) {
        return false;
      }
      return true;
    }

    public checkAfterMoveLeft(toRight: any, oldSource: any, newI: any) {
      const vm = nts.uk.ui._viewModel.content;
      vm.selectedSystemType.valueHasMutated();
    }

    public checkAfterMoveRight(toRight: any, oldSource: any, newI: any) {
      const vm = nts.uk.ui._viewModel.content;
      vm.currentCateSelected(_.orderBy(vm.currentCateSelected(), ["categoryId"], ["asc"]));
      vm.currentCateSelected.valueHasMutated();
    }
  }

  export class Pattern {
    code: string;
    patternName: string;
    patternClassification: number;
    displayCode?: string;
  }

  export class Category {
    categoryId: string;
    categoryName: string;
    retentionPeriod: string;
    systemType: number;
    patternClassification?: number;
    id?: string = nts.uk.util.randomId();
    displayName: string;
  }

  export class ItemModel {
    code: number;
    name: string;

    constructor(code: number, name: string) {
      this.code = code;
      this.name = name;
    }
  }

  export interface LoginPersionInCharge {
    attendance: boolean;
    employeeInfo: boolean;
    officeHelper: boolean;
    payroll: boolean;
    personnel: boolean;
  }

  export class ScreenMode {
    static NEW = 0;
    static UPDATE = 1;
  }
}