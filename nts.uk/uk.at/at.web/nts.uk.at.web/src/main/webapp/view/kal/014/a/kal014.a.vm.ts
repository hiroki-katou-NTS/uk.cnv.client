module nts.uk.at.kal014.a {

    import common=nts.uk.at.kal014.common;
    const PATH_API = {
        GET_ALL_SETTING: 'at/record/alarmwrkp/screen/getAll',
        GET_ALL_CTG: 'at/record/alarmwrkp/screen/getAllCtg',
        GET_SETTING_BYCODE: 'alarmworkplace/patternsetting/getPatternSetting',
        REGISTER: 'alarmworkplace/patternsetting/register',
        ADD: 'alarmworkplace/patternsetting/add',
        DELETE: 'alarmworkplace/patternsetting/delete',
        GET_ROLENAME: "ctx/sys/auth/role/get/rolename/by/roleids"

    }

    const SCREEN = {
        B: 'B',
        C: 'C'
    }

    @bean()
    export class Kal014AViewModel extends ko.ViewModel {

        backButon: string = "/view/kal/012/a/index.xhtml";
        gridItems: KnockoutObservableArray<GridItem> = ko.observableArray([]);
        currentCode: KnockoutObservable<string> = ko.observable(null);
        selectedExecutePermission: KnockoutObservable<any>;
        tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
        selectedTab: KnockoutObservable<string>;
        isNewMode: KnockoutObservable<boolean>;
        itemsSwap: KnockoutObservableArray<AlarmCheckCategoryList>;
        listAllCtgCode: Array<AlarmCheckCategoryList> = [];
        columns: KnockoutObservableArray<nts.uk.ui.NtsGridListColumn>;
        currentCodeListSwap: KnockoutObservableArray<any>;
        roundingRules: KnockoutObservableArray<any>;
        selectedRuleCode: any;
        workPalceCategory: any;
        alarmPatterSet : KnockoutObservable<AlarmPatternObject>
            = ko.observable(new AlarmPatternObject(null,null,
            new WkpAlarmPermissionSettingDto(null),null,[]));
        listRoleID: KnockoutObservableArray<string> = ko.observableArray([]);

        constructor(props: any) {
            super();
            const vm = this;
            vm.workPalceCategory = common.WORKPLACE_CATAGORY;
            vm.tabs = ko.observableArray([
                {
                    id: 'tab-1',
                    title: vm.$i18n('KAL014_17'),
                    content: '.tab-content-1',
                    enable: ko.observable(true),
                    visible: ko.observable(true)
                },
                {
                    id: 'tab-2',
                    title: vm.$i18n('KAL014_18'),
                    content: '.tab-content-2',
                    enable: ko.observable(true),
                    visible: ko.observable(true)
                },
                {
                    id: 'tab-3',
                    title: vm.$i18n('KAL014_19'),
                    content: '.tab-content-3',
                    enable: ko.observable(true),
                    visible: ko.observable(true)
                }
            ]);
            vm.selectedTab = ko.observable('tab-1');
            vm.itemsSwap = ko.observableArray([]);
            vm.columns = ko.observableArray([
                {headerText: vm.$i18n('KAL014_22'), key: 'key', hidden: true},
                {headerText: vm.$i18n('KAL014_22'), key: 'cssClass', hidden: true},
                {headerText: vm.$i18n('KAL014_22'), key: 'categoryName',
                    template: "<span class='${cssClass}'>${categoryName}</span>", width: 150},
                {headerText: vm.$i18n('KAL014_23'), key: 'code',
                    template: "<span class='${cssClass}'>${code}</span>", width: 50},
                {headerText: vm.$i18n('KAL014_24'), key: 'name',
                    template: "<span class='${cssClass}'>${name}</span>", width: 150}
            ]);
            vm.currentCodeListSwap = ko.observableArray([]);
            vm.roundingRules = ko.observableArray([
                {code: 1, name: vm.$i18n('KAL014_30')},
                {code: 0, name: vm.$i18n('KAL014_31')}
            ]);
            vm.selectedRuleCode = ko.observable(null);
            vm.isNewMode = ko.observable(true);
            vm.selectedExecutePermission = ko.observable("");
            $("#fixed-table").ntsFixedTable({height: 301, width: 817});

            vm.getAllSettingData(null).done(()=>{
                vm.itemsSwap( _.cloneDeep(vm.listAllCtgCode));
            }).fail( (err: any) =>{
                vm.$dialog.error(err);
            });
        }

        created() {
            const vm = this;
            vm.currentCode.subscribe((code:any)=>{
                if (_.isNil(code)){
                    return;
                }
                $("#swap-list-grid2").igGridSelection("clearSelection");
                $("#swap-list-grid1").igGridSelection("clearSelection");
                vm.getSelectedData(code).done(()=>{
                    // reset swap list
                    vm.itemsSwap.removeAll();
                    vm.itemsSwap(_.cloneDeep(vm.listAllCtgCode));

                    $("#A3_3").focus();
                }).fail( (err: any) =>{
                    vm.$dialog.error(err);
                });
            });

            vm.alarmPatterSet().allCtgCdSelected.subscribe(()=>{
                vm.$errors("clear","#swap-list-grid2_container");
                let listCtgdu = _.map(vm.alarmPatterSet().allCtgCdSelected(),
                        i=> new ItemModel(i.category,i.categoryName)),
                    listCtg: Array<ItemModel> =_.uniqWith(listCtgdu,_.isEqual ),
                    listCtgOld: Array<number> = _.map(vm.alarmPatterSet().checkConList(),i=>i.alarmCategory()),
                    newCtg = _.filter(listCtg, item => listCtgOld.indexOf(item.code) === -1),
                    updateCtg = _.filter( _.map(listCtg,i=>i.code), item => listCtgOld.indexOf(item) != -1),
                    removeOld = _.filter(listCtgOld, item => _.map(listCtg,i=>i.code).indexOf(item) === -1),
                    newCtgList: Array<WkpCheckConditionDto> = [];

                // Update category
                if (updateCtg.length >0 || removeOld.length > 0) {
                    let newResult = _.filter(vm.alarmPatterSet().checkConList(),
                        item => updateCtg.indexOf(item.alarmCategory()) != -1
                            && removeOld.indexOf(item.alarmCategory()) == -1);
                    newResult.forEach((ctg: WkpCheckConditionDto)=>{
                        let listSelectedCode = _.map(_.filter(vm.alarmPatterSet().allCtgCdSelected(),
                            i =>i.category === ctg.alarmCategory()   ),ctgCd => ctgCd.code);
                        ctg.updateSelectedCd(listSelectedCode);

                    });
                    vm.alarmPatterSet().updateConditionCtg(newResult);
                }

                //  add category
                newCtg.forEach((ctg: ItemModel)=>{
                    let listSelectedCode = _.map(_.filter(vm.alarmPatterSet().allCtgCdSelected(),
                        i =>i.category === ctg.code   ),ctgCd => ctgCd.code);
                    let defautData : IWkpCheckConditionDto = { alarmCategory: ctg.code, alarmCtgName: ctg.name,
                        checkConditionCodes: listSelectedCode, extractionDaily: null,listExtractionMonthly: null,singleMonth: null};
                    newCtgList.push(vm.createDataforCategory(defautData));
                });
                if (newCtgList.length > 0){
                    vm.alarmPatterSet().addConditionCtg(newCtgList);
                }


            });

            vm.alarmPatterSet().alarmPerSet().authSetting.subscribe((value: number)=>{
                if (value == 0){
                    vm.alarmPatterSet().alarmPerSet().updateRoleIds([]);
                }
            })

            vm.alarmPatterSet().alarmPerSet().roleIds.subscribe((value: Array<string>) =>{
                vm.$ajax("com",PATH_API.GET_ROLENAME,vm.alarmPatterSet().alarmPerSet().roleIds())
                    .done(function(listRole: Array<RoleDto>) {
                        vm.alarmPatterSet().alarmPerSet().setRoleName(_.map(listRole,i=>i.name));
                });
            })
        }

        getAllSettingData(selectedCd?: string): JQueryPromise<any>{
            const vm = this;
            let dfd = $.Deferred();
            $.when(vm.$ajax(PATH_API.GET_ALL_SETTING),vm.$ajax(PATH_API.GET_ALL_CTG))
                .done((data: Array<IAlarmPatternSet>, ctg: Array<IAlarmCheckCategoryList>) =>{
                    // Set all value of category
                    if (!_.isEmpty(ctg)) {
                        let allCtgCd = _.map(ctg, i => new AlarmCheckCategoryList(i));
                        vm.listAllCtgCode = _.sortBy(allCtgCd, ['category', 'code']);
                    } else{
                        vm.listAllCtgCode = [];
                    }

                    if (!data || data.length <= 0){
                        vm.gridItems([]);
                        // Set to New Mode
                        vm.clickNewButton();
                        dfd.resolve();
                        return;
                    }
                    // Set to update mode
                    vm.isNewMode(false);
                    vm.gridItems( _.map(data, x=> new GridItem(x.alarmPatternCD, x.alarmPatternName)));
                    // In case of update mode select on updated item
                    if (selectedCd){
                        vm.currentCode(selectedCd);
                    } else {
                        // In case of init screen select on first item
                        vm.currentCode(vm.gridItems()[0].code);
                    }
                    vm.currentCode.valueHasMutated();
                    dfd.resolve();
            }).fail( (err: any) =>{
                vm.$dialog.error(err);
                dfd.reject();
            });


            return dfd.promise();

        }

        getSelectedData(alarmCd: string): JQueryPromise<any>{
            const vm = this;
            let dfd = $.Deferred();
            // Get Data of select item
            if (_.isNil(alarmCd)){
                return;
            }
            vm.$errors("clear");
            vm.$blockui("show");
            vm.$ajax(PATH_API.GET_SETTING_BYCODE,{patternCode: alarmCd})
                .done((data: IAlarmPatternObject)=>{
                    if (!data){
                        vm.getAllSettingData().done(()=>{
                            dfd.resolve();
                            return;
                        });
                    }
                    vm.isNewMode(false);

                    vm.alarmPatterSet().alarmPerSet().update(data.alarmPerSet);

                    let checkCon: Array<WkpCheckConditionDto> = [];
                    _.forEach(data.checkConList, (value)=>{
                        checkCon.push(vm.createDataforCategory(value));
                    });

                    let listSelected: Array<AlarmCheckCategoryList> = _.flatMap(data.checkConList,
                        (value: any )=>{
                        let item: Array<AlarmCheckCategoryList>  = _.filter(vm.listAllCtgCode,
                                i=>i.category == value.alarmCategory
                                    && value.checkConditionCodes.indexOf(i.code) != -1);
                        let deletedItems = _.filter(value.checkConditionCodes,
                            (code: string) => _.map(item,item => item.code).indexOf(code) == - 1);

                        _.forEach(deletedItems, (deletedItem: string) =>{
                            let param: IAlarmCheckCategoryList = {category: value.alarmCategory, categoryName: value.alarmCtgName,
                                code: deletedItem,name: vm.$i18n("KAL014_56"), cssClass: 'red-color'};
                            item.push(new AlarmCheckCategoryList(param));
                        });
                        return item;
                    });

                    vm.alarmPatterSet().update(data.alarmPatternCD,data.alarmPatternName,checkCon,_.sortBy(listSelected,
                            ['code','categoryName']));
                    dfd.resolve();
                })
                .fail((error: any)=>{
                    vm.$dialog.error(error);
                    dfd.reject();
                }).always(()=>{
                    vm.$blockui("hide");
            });

            return dfd.promise();
        }

        createDataforCategory(data: IWkpCheckConditionDto): WkpCheckConditionDto{
            const vm = this;
            let extracDai: WkpExtractionPeriodDailyDto,
                extracSingMon: WkpSingleMonthDto,
                listExtracMon: WkpExtractionPeriodMonthlyDto;
            switch (data.alarmCategory) {
                // 月次
                case vm.workPalceCategory.MONTHLY:
                    extracSingMon = new WkpSingleMonthDto(data.singleMonth);
                    break

                // マスタチェック(基本)
                case vm.workPalceCategory.MASTER_CHECK_BASIC:
                // マスタチェック(職場)
                case vm.workPalceCategory.MASTER_CHECK_WORKPLACE:
                    listExtracMon = new WkpExtractionPeriodMonthlyDto(data.listExtractionMonthly);
                    break;

                //マスタチェック(日次)
                case vm.workPalceCategory.MASTER_CHECK_DAILY:
                // スケジュール／日次
                case vm.workPalceCategory.SCHEDULE_DAILY:
                // 申請承認
                case vm.workPalceCategory.APPLICATION_APPROVAL:
                    extracDai = new WkpExtractionPeriodDailyDto(data.extractionDaily);
                    break
            }
            return new WkpCheckConditionDto(data.alarmCategory,data.alarmCtgName,data.checkConditionCodes,
                extracDai, listExtracMon,extracSingMon,
                vm.getExtractionPeriod(data.alarmCategory,data.checkConditionCodes,
                    extracDai,listExtracMon,extracSingMon));
        }

        /***
         * getExtractionPeriod
         * @param {number} alarmCategory
         * @param {Array<string>} checkConditionCodes
         * @param {nts.uk.at.kal014.a.WkpExtractionPeriodDailyDto} extractionDaily
         * @param {nts.uk.at.kal014.a.WkpExtractionPeriodMonthlyDto} listExtractionMonthly
         * @param {nts.uk.at.kal014.a.WkpSingleMonthDto} singleMonth
         * @returns {string}
         */
        getExtractionPeriod(alarmCategory: number, checkConditionCodes: Array<string>,
                            extractionDaily: WkpExtractionPeriodDailyDto,listExtractionMonthly: WkpExtractionPeriodMonthlyDto,
                            singleMonth: WkpSingleMonthDto): string {
            //   カテゴリ　＝＝　月次の場合
            // 　・抽出期間　＝　単月
            //             カテゴリ　＝＝　マスタチェック（基本）OR　マスタチェック（職場）の場合
            // 　・抽出期間　＝　抽出期間（月単位）
            // カテゴリ　＝＝　マスタチェック（日次）OR　スケジュール/日次　OR　申請承認　の場合
            // 　・抽出期間　＝　抽出期間（日単位）
            const vm = this;
            let extractionText = "";
            switch (alarmCategory) {
                // マスタチェック(基本)
                // マスタチェック(職場)
                case vm.workPalceCategory.MASTER_CHECK_BASIC:
                case vm.workPalceCategory.MASTER_CHECK_WORKPLACE:
                    extractionText = vm.buildExtracMon(listExtractionMonthly);
                    break;
                //マスタチェック(日次)
                case vm.workPalceCategory.MASTER_CHECK_DAILY:
                // スケジュール／日次
                case vm.workPalceCategory.SCHEDULE_DAILY:
                case vm.workPalceCategory.APPLICATION_APPROVAL:
                    extractionText = vm.buildExtracDay(extractionDaily);
                    break
                // 月次
                case vm.workPalceCategory.MONTHLY:
                    extractionText = vm.getMonthValue(singleMonth);
                    break
                // 申請承認
            }
            return extractionText;
        }


        buildExtracMon(data: WkpExtractionPeriodMonthlyDto): string {
            const vm = this;

            let start = "";
            if (data.strCurrentMonth()) {
                start = "当月";
            } else {
                start = data.strMonth() + "ヶ月"
                    + (data.strPreviousAtr() == 0? "前" : "先");
            }
            let end = "";
            if (data.endCurrentMonth()) {
                end = "当月";
            } else {
                end = data.endMonth() + "ヶ月"
                    + (data.endPreviousAtr() == 0? "前" : "先");
            }

            return start + " "+ vm.$i18n('KAL014_42') + " " +  end;
        }
        /**
         * This function is responsible for getting month value
         *
         * @param month
         * @return string
         * */
        getMonthValue(month: WkpSingleMonthDto): string {
            let result = "";
            if (month.curentMonth()) {
                result = "当月";
            } else {
                result = month.monthNo() + "ヶ月"
                    + (month.monthPrevious() == 0? "前" : "先");
            }
            return result;
        }

        buildExtracDay(data: WkpExtractionPeriodDailyDto): string {
            const vm = this;
            let start = "";
            if (data.strSpecify() == StartSpecify.MONTH) {
                if (data.strCurrentMonth()) {
                    // 当月の締め開始日
                    start = "当月の" + vm.$i18n('KAL014_48');
                } else {
                    start = data.strMonth() + "ヶ月"
                        + (data.strPreviousMonth() == 0? "前" : "先") + "の"+ vm.$i18n('KAL014_48');
                }
            } else{
                start = vm.$i18n('KAL014_44')
                    + data.strDay()+  vm.$i18n('KAL014_46') + (data.strPreviousDay() == 0? "前" : "後");
            }

            let end = "";
            if (data.endSpecify() == EndSpecify.MONTH) {
                if (data.endCurrentMonth()) {
                    // 当月の締め終了日
                    end = "当月の" + vm.$i18n('KAL014_53');
                } else {
                    end = data.endMonth() + "ヶ月"
                        + (data.endPreviousMonth() == 0? "前" : "先") + "の" +vm.$i18n('KAL014_53');
                }
            } else{
                end = vm.$i18n('KAL014_44')
                    + data.endDay() +  vm.$i18n('KAL014_46') + (data.endPreviousDay() == 0?"前" : "後");
            }

            return start + " "+ vm.$i18n('KAL014_42') + " " + end;
        }
        /**
         * This function is responsible to click per item action and take decision which screen will open
         * @param item
         * @return type void
         *
         * */
        clickTableItem(item: WkpCheckConditionDto) {
            const vm = this;
            if (item.alarmCategory() === vm.workPalceCategory.MASTER_CHECK_BASIC
                || item.alarmCategory()=== vm.workPalceCategory.MASTER_CHECK_WORKPLACE
                || item.alarmCategory() === vm.workPalceCategory.MONTHLY) {
                vm.openScreen(item, SCREEN.B);
            } else {
                vm.openScreen(item, SCREEN.C);
            }
        }

        /**
         * This function is responsible open modal KAL014 B, C
         * @param item
         * @param type <screen type B and C>
         * @return type void
         *
         * */
        openScreen(item: any, type: any) {
            const vm = this;
            let modalPath = type === SCREEN.B ? '/view/kal/014/b/index.xhtml' : '/view/kal/014/c/index.xhtml';

            vm.$window.modal(modalPath, ko.toJS(item))
                .then((result: any) => {
              
                    if (_.isEmpty(result)){
                        return;
                    }
                    let element = _.find(vm.alarmPatterSet().checkConList(), ((item)=>{
                        return item.alarmCategory() == result.shareData.alarmCategory;
                    }));

                    switch (result.shareData.alarmCategory) {
                        // マスタチェック(基本)
                        // マスタチェック(職場)
                        case vm.workPalceCategory.MASTER_CHECK_BASIC:
                        case vm.workPalceCategory.MASTER_CHECK_WORKPLACE:
                            element.listExtractionMonthly().updateStartEnd(result.shareData.strMonth, result.shareData.endMonth);
                            element.updateDisplayTxt(vm.buildExtracMon(element.listExtractionMonthly()));
                            break;
                        case vm.workPalceCategory.MASTER_CHECK_DAILY:
                        case vm.workPalceCategory.SCHEDULE_DAILY:
                        case vm.workPalceCategory.APPLICATION_APPROVAL:
                            element.extractionDaily().updateExtractionPeriodDaily(result.shareData.strSpecify,result.shareData.strMonth,result.shareData.strPreviousDay,result.shareData.strDay,result.shareData.strPreviousMonth,
                                result.shareData.endSpecify,result.shareData.endMonth,result.shareData.endPreviousDay,result.shareData.endDay,result.shareData.endPreviousMonth);
                            element.updateDisplayTxt(vm.buildExtracDay(element.extractionDaily()));
                            break
                        // 月次
                        case vm.workPalceCategory.MONTHLY:
                            element.singleMonth().updateStrMonth(result.shareData.strMonth);
                            element.updateDisplayTxt(vm.getMonthValue(element.singleMonth()));
                            break
                    }
                });
        }

        /**
         * This function is responsible to make screen the new mode
         * @return type void
         *
         * */
        clickNewButton() {
            const vm = this;
            vm.isNewMode(true);
            vm.$errors("clear", ".nts-editor", "button").then(() => {
                vm.initScreen().done(()=>{
                    vm.cleanInput();
                    $("#A3_2").focus();
                });

            });
        }

        /**
         * This function is responsible clean input data
         * @return type void
         *
         * */
        initScreen():JQueryPromise<any>{
            const vm = this;
            let dfd = $.Deferred();
            vm.alarmPatterSet().update(null,null,[],[]);
            vm.alarmPatterSet().alarmPerSet().update(null);
            vm.currentCode(null);
            dfd.resolve();

            return dfd.promise();
        }
        cleanInput() {
            const vm = this;
            vm.itemsSwap.removeAll();
            vm.itemsSwap(_.cloneDeep(vm.listAllCtgCode));

        }

        /**
         * This function is responsible to register data
         * @return type void
         *
         * */
        clickRegister() {
            const vm = this;
            // Validate logic
            if (_.find(vm.alarmPatterSet().allCtgCdSelected(), { 'cssClass': 'red-color' })) {
                vm.$dialog.error({ messageId: "Msg_817" });
                return;
            }
            vm.$validate(".nts-input",".nts-editor", ".ntsControl").then((valid: boolean) => {
                if (!valid){
                    return;
                }
                vm.$blockui("show")
                let param = ko.toJS(vm.alarmPatterSet());
                vm.$ajax(vm.isNewMode() ? PATH_API.ADD : PATH_API.REGISTER,param).done(()=>{
                    vm.$dialog.info({ messageId: 'Msg_15' }).done(()=>{
                        vm.isNewMode(false);
                        vm.getAllSettingData(param.alarmPatternCD).done(()=>{
                            $("#A3_3").focus();
                        });
                    })
                }).fail((res: any) => {
                    if (res.messageId == "Msg_3"){
                        vm.$errors('#A3_2', 'Msg_3');
                    } else if (res.messageId == "Msg_811"){
                        vm.$errors('#swap-list-grid2_container', 'Msg_811');
                    } else {
                        vm.$dialog.error(res);
                    }
                }).always(() => vm.$blockui("hide"));
            });
        }

        /**
         * This function is responsible to delete data
         * @return type void
         *
         * */
        clickDeleteButton() {
            const vm = this;
            vm.$dialog.confirm({ messageId: "Msg_18" }).then((result: 'no' | 'yes' | 'cancel') => {
                if (result === 'yes') {
                    vm.$blockui("grayout");
                    vm.$ajax(PATH_API.DELETE,{code: vm.currentCode()}).done(() => {
                        vm.$dialog.info({ messageId: "Msg_16" }).then(() => {
                            // Find new selected item
                            let index = _.findIndex(vm.gridItems(), item => item.code == vm.currentCode()),
                                newCurrentCode: string = null;
                            if ( vm.gridItems().length < 2){
                                newCurrentCode = null;
                            }else if (index == (vm.gridItems().length -1 )){
                                newCurrentCode = vm.gridItems()[index -1 ].code;
                            }  else {
                                newCurrentCode = vm.gridItems()[index + 1].code;
                            }
                            vm.getAllSettingData(newCurrentCode);
                        });
                    }).fail((res: any) => {
                        vm.$dialog.error(res);
                    }).always(() => vm.$blockui("hide"));
                }
            })

        }

        /**
         * This function is responsible to configuration action
         * @return type void
         *
         * */
        clickConfiguration(vm: any) {
            //const vm = this;
            let param = {
                currentCode: vm.alarmPerSet().roleIds(),
                roleType: 3,
                multiple: true
            };
            nts.uk.ui.windows.setShared("paramCdl025", param);
            nts.uk.ui.windows.sub.modal("com", "/view/cdl/025/index.xhtml").onClosed(() => {
                let data: Array<string> = nts.uk.ui.windows.getShared("dataCdl025");
                if (!nts.uk.util.isNullOrUndefined(data))
                    vm.alarmPerSet().updateRoleIds(data);
            });
        }
    }

    class ItemModel {
        code: number;
        name: string;

        constructor(code: number, name: string) {
            this.code = code;
            this.name = name;
        }
    }


    interface IAlarmPatternSet{
        /**
         * アラームリストパターンコード
         */
        alarmPatternCD: string;
        /**
         * アラームリストパターン名称
         */
        alarmPatternName: string;
    }

    class GridItem {
        code: string
        name: string;

        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }

    class AlarmPatternObject{
        // アラームリストパターンコード
        alarmPatternCD: KnockoutObservable<string> = ko.observable(null);
        // アラームリストパターン名称
        alarmPatternName: KnockoutObservable<string> = ko.observable(null);
        // アラームリスト権限設定
        alarmPerSet: KnockoutObservable<WkpAlarmPermissionSettingDto> = ko.observable(null);
        // チェック条件
        checkConList: KnockoutObservableArray<WkpCheckConditionDto> = ko.observableArray(null);
        allCtgCdSelected: KnockoutObservableArray<AlarmCheckCategoryList>  = ko.observableArray(null);

        constructor(alarmPatternCD:string,alarmPatternName: string, alarmPerSet: WkpAlarmPermissionSettingDto, checkConList: Array<WkpCheckConditionDto>,
        allCtgCdSelected: Array<AlarmCheckCategoryList>){
            this.alarmPatternCD(alarmPatternCD);
            this.alarmPatternName(alarmPatternName);
            this.alarmPerSet(alarmPerSet);
            this.checkConList(_.isEmpty(checkConList) ? [] : _.sortBy(checkConList,i=>i.alarmCategory()));
            this.allCtgCdSelected(allCtgCdSelected || []);

        }

        update(alarmPatternCD:string,alarmPatternName: string, checkConList: Array<WkpCheckConditionDto>,
                    allCtgCdSelected: Array<AlarmCheckCategoryList>){
            this.alarmPatternCD(alarmPatternCD);
            this.alarmPatternName(alarmPatternName);
            this.checkConList(_.isEmpty(checkConList) ? [] :_.sortBy(checkConList,i=>i.alarmCategory()));
            this.allCtgCdSelected(allCtgCdSelected || []);

        }


        // Add condition
        addConditionCtg(newCtg: Array<WkpCheckConditionDto>){
            let fullCtg: Array<WkpCheckConditionDto> = ko.unwrap(this.checkConList);
            newCtg.forEach((item: WkpCheckConditionDto)=>{
                fullCtg.push(item);
            })

            this.checkConList(_.sortBy(fullCtg,i=>i.alarmCategory()));
        }

        // Update condition
        updateConditionCtg(newCtg: Array<WkpCheckConditionDto>){
            this.checkConList(_.sortBy(newCtg,i=>i.alarmCategory()));
        }

    }

    class WkpAlarmPermissionSettingDto {
        authSetting: KnockoutObservable<number>= ko.observable(0);
        roleIds: KnockoutObservableArray<string>= ko.observableArray(null);
        roleIdDis: KnockoutObservable<string>= ko.observable(null);
        constructor(data: IWkpAlarmPermissionSettingDto){
            this.authSetting(_.isNil(data) ? 0 : data.authSetting);
            this.roleIds(_.isNil(data) ? [] : data.roleIds);
        }

        update(data: IWkpAlarmPermissionSettingDto){
            this.authSetting(_.isNil(data) ? 0 : data.authSetting);
            this.roleIds(_.isNil(data) ? [] : data.roleIds);
            if (_.isNil(data)){
                this.roleIdDis("");
            }
        }

        updateRoleIds(data: Array<string>){
            this.roleIds(_.isNil(data) ? [] : data);
        }
        setRoleName(name: Array<string>){
            this.roleIdDis(_.isEmpty(name) ? "" :_.join(name, '、'))
        }
    }

    class WkpCheckConditionDto {
        alarmCategory: KnockoutObservable<number>= ko.observable(null);
        alarmCtgName: KnockoutObservable<string>= ko.observable(null);
        checkConditionCodes: KnockoutObservableArray<string>= ko.observableArray(null);
        displayText: KnockoutObservable<string> = ko.observable(null);
        extractionDaily: KnockoutObservable<WkpExtractionPeriodDailyDto>= ko.observable(null);
        listExtractionMonthly: KnockoutObservable<WkpExtractionPeriodMonthlyDto>= ko.observable(null);
        singleMonth: KnockoutObservable<WkpSingleMonthDto>= ko.observable(null);

        constructor(alarmCategory: number, alarmCtgName: string, checkConditionCodes: Array<string>,
                    extractionDaily: WkpExtractionPeriodDailyDto,listExtractionMonthly: WkpExtractionPeriodMonthlyDto,
                    singleMonth: WkpSingleMonthDto, displayText: string) {
            this.alarmCategory(alarmCategory);
            this.alarmCtgName(alarmCtgName);
            this.checkConditionCodes(checkConditionCodes);
            this.extractionDaily(extractionDaily);
            this.listExtractionMonthly(listExtractionMonthly);
            this.singleMonth(singleMonth);
            this.displayText(displayText)
        }

        updateDisplayTxt(display: string){
            this.displayText(display);
        }

        updateSelectedCd(selectedCodes: Array<string>){
            this.checkConditionCodes(selectedCodes);
        }

    }
    class WkpExtractionPeriodDailyDto {

        //Start date
        strSpecify: KnockoutObservable<number>= ko.observable(null);
        strPreviousMonth?: KnockoutObservable<number>= ko.observable(null);
        strMonth?: KnockoutObservable<number>= ko.observable(null);
        strCurrentMonth?: KnockoutObservable<boolean>= ko.observable(null);
        strPreviousDay?: KnockoutObservable<number>= ko.observable(null);
        strMakeToDay?: KnockoutObservable<boolean>= ko.observable(null);
        strDay?: KnockoutObservable<number>= ko.observable(null);

        //End date
        endSpecify: KnockoutObservable<number>= ko.observable(null);
        endPreviousDay?: KnockoutObservable<number>= ko.observable(null);
        endMakeToDay?: KnockoutObservable<boolean>= ko.observable(null);
        endDay?: KnockoutObservable<number>= ko.observable(null);
        endPreviousMonth?:KnockoutObservable<number>= ko.observable(null);
        endCurrentMonth?: KnockoutObservable<boolean>= ko.observable(null);
        endMonth?:KnockoutObservable<number>= ko.observable(null);

        constructor(data: IWkpExtractionPeriodDailyDto){
            this.strSpecify(_.isNil(data)? StartSpecify.MONTH :data.strSpecify);
            this.strPreviousMonth(_.isNil(data)? PreviousClassification.BEFORE :data.strPreviousMonth);
            this.strMonth((_.isNil(data) || data.strCurrentMonth )? 0 : data.strMonth);
            this.strCurrentMonth(_.isNil(data)? true :data.strCurrentMonth);
            this.strPreviousDay(_.isNil(data)? null :data.strPreviousDay);
            this.strMakeToDay(_.isNil(data)? null: data.strDay == 0);
            this.strDay(_.isNil(data)? null :data.strDay);

            this.endSpecify(_.isNil(data)? EndSpecify.MONTH  :data.endSpecify);
            this.endPreviousDay(_.isNil(data)? null :data.endPreviousDay);
            this.endMonth((_.isNil(data) || data.endCurrentMonth) ? 0 :data.endMonth);
            this.endCurrentMonth(_.isNil(data)? true :data.endCurrentMonth);
            this.endPreviousMonth(_.isNil(data)? PreviousClassification.BEFORE :data.endPreviousMonth);
            this.endDay(_.isNil(data)? null :data.endDay);
            this.endMakeToDay(_.isNil(data) ? null : data.endDay == 0);
        }

        updateExtractionPeriodDaily(strSpecify: number,strMonth: number,strPreviousDay: number, strDay: number,strPreviousMonth: number,
                                    endSpecify: number,endMonth: number,endPreviousDay:number,  endDay: number,
                                    endPreviousMonth: number){
            this.strSpecify(strSpecify);
            this.strMonth(strMonth);
            this.strCurrentMonth(strMonth == 0);
            this.strPreviousDay(strPreviousDay);
            this.strMakeToDay(strDay == 0);
            this.strDay(strDay);
            this.strPreviousMonth(strPreviousMonth);

            this.endSpecify(endSpecify);
            this.endMonth(endMonth);
            this.endCurrentMonth(endMonth == 0);
            this.endPreviousDay(endPreviousDay);
            this.endDay(endDay);
            this.endMakeToDay(endDay == 0);
            this.endPreviousMonth(endPreviousMonth);

        }
    }

     class WkpExtractionPeriodMonthlyDto {

        //Start month
        strSpecify: KnockoutObservable<number>= ko.observable(null);
        strMonth?:KnockoutObservable<number>= ko.observable(null);
        strCurrentMonth?: KnockoutObservable<boolean>= ko.observable(null);
        strPreviousAtr?: KnockoutObservable<number>= ko.observable(null);
        yearType?:KnockoutObservable<number>= ko.observable(null);
        designatedMonth?: KnockoutObservable<number>= ko.observable(null);

        //End Month
        endSpecify: KnockoutObservable<number>= ko.observable(null);
        extractFromStartMonth: KnockoutObservable<number>= ko.observable(null);
        endMonth?: KnockoutObservable<number>= ko.observable(null);
        endCurrentMonth?: KnockoutObservable<boolean>= ko.observable(null);
        endPreviousAtr?: KnockoutObservable<number> = ko.observable(null);

        constructor(data: IWkpExtractionPeriodMonthlyDto){
                // 「締め開始月を指定する」固定
                this.strSpecify(_.isNil(data)? SpecifyStartMonth.DESIGNATE_CLOSE_START_MONTH : data.strSpecify);
                this.strMonth(_.isNil(data)? 0 :data.strMonth);
                this.strCurrentMonth(_.isNil(data)? true :data.strCurrentMonth);
                // 「前」固定
                this.strPreviousAtr(_.isNil(data)? PreviousClassification.BEFORE :data.strPreviousAtr);
                this.yearType(_.isNil(data)? null :data.yearType);
                this.designatedMonth(_.isNil(data)? null :data.designatedMonth);

                // 「締め開始月を指定する」固定
                this.endSpecify(_.isNil(data)? SpecifyEndMonth.DESIGNATE_CLOSE_START_MONTH :data.endSpecify);
                this.extractFromStartMonth(_.isNil(data)? null :data.extractFromStartMonth);
                this.endMonth(_.isNil(data)? 0 :data.endMonth);
                this.endCurrentMonth(_.isNil(data)? true :data.endCurrentMonth);
                // 「前」固定
                this.endPreviousAtr(_.isNil(data)? PreviousClassification.BEFORE :data.endPreviousAtr);
        }

        updateStartEnd(strMonth: number, endMonth: number){
            this.strMonth(strMonth);
            this.strCurrentMonth(strMonth == 0);
            this.endMonth(endMonth);
            this.endCurrentMonth(endMonth == 0);
        }
    }
    class WkpSingleMonthDto {

        /** 前・先区分  */
        monthPrevious: KnockoutObservable<number>= ko.observable(null);
        /** 月数 */
        monthNo: KnockoutObservable<number>= ko.observable(null);
        /** 当月とする */
        curentMonth: KnockoutObservable<boolean>= ko.observable(true);

        constructor(data: IWkpSingleMonthDto){
            this.monthPrevious(_.isNil(data) ? PreviousClassification.BEFORE : data.monthPrevious);
            this.monthNo(_.isNil(data) ? 0 : data.monthNo);
            this.curentMonth(_.isNil(data) ? true : data.curentMonth);
        }

        updateStrMonth(strMonth: number){
            this.monthNo(strMonth);
            this.curentMonth(strMonth == 0);
        }
    }

    // Interface
    interface IAlarmPatternObject{
        /**
         * アラームリストパターンコード
         */
        alarmPatternCD: string;
        /**
         * アラームリストパターン名称
         */
        alarmPatternName: string;
        /**
         * アラームリスト権限設定
         */
        alarmPerSet: IWkpAlarmPermissionSettingDto;
        /**
         * チェック条件
         */
        checkConList: Array<IWkpCheckConditionDto> ;
    }

    interface IWkpAlarmPermissionSettingDto {
        authSetting: number;
        roleIds: Array<string>;
    }

    interface IWkpCheckConditionDto {
        alarmCategory: number;
        alarmCtgName: string;
        checkConditionCodes: Array<string>;
        extractionDaily: IWkpExtractionPeriodDailyDto;
        listExtractionMonthly: IWkpExtractionPeriodMonthlyDto;
        singleMonth: IWkpSingleMonthDto;
    }
    interface IWkpExtractionPeriodDailyDto {

        //Start date
        strSpecify: number;
        strPreviousMonth?: number;
        strMonth?: number;
        strCurrentMonth?: boolean;
        strPreviousDay?: number;
        strMakeToDay?: boolean;
        strDay?: number;

        //End date
        endSpecify: number;
        endPreviousDay?: number;
        endMakeToDay?: boolean;
        endDay?:number;
        endPreviousMonth?:number;
        endCurrentMonth?: boolean;
        endMonth?:number;
    }
    interface IWkpExtractionPeriodMonthlyDto {

        //Start month
        strSpecify: number;
        strMonth?:number;
        strCurrentMonth?: boolean;
        strPreviousAtr?: number;
        yearType?:number;
        designatedMonth?: number;

        //End Month
        endSpecify: number;
        extractFromStartMonth: number;
        endMonth?: number;
        endCurrentMonth?: boolean;
        endPreviousAtr?: number ;
    }
    interface IWkpSingleMonthDto {

        /** 前・先区分  */
        monthPrevious: number;
        /** 月数 */
        monthNo: number;
        /** 当月とする */
        curentMonth: boolean;
    }


    interface IAlarmCheckCategoryList {
        // カテゴリ
        category: number;
        categoryName: string;
        // アラームチェック条件コード
        code: string;
        // 名称
        name: string;
        cssClass:string;
    }

    class AlarmCheckCategoryList {
        key :string;
        // カテゴリ
        category: number;

        categoryName: string;
        // アラームチェック条件コード
        code: string;
        // 名称
        name: string;

        cssClass: string = "";

        constructor (data: IAlarmCheckCategoryList) {
            this.key = data.category + "-"+data.code;
            this.category = data.category;
            this.categoryName = data.categoryName;
            this.code = data.code;
            this.name = data.name;
            this.cssClass = data.cssClass;
        }
    }

    interface RoleDto {
            // Id
        roleId: string;
            // コード
        roleCode: string;
            // ロール種類
        roleType: number;
            // 参照範囲
        employeeReferenceRange: number;
            // ロール名称
        name: string;
            // 契約コード
        contractCode: string;
            // 担当区分
        assignAtr: number;
            // 会社ID
        companyId: string;
    }

    export enum StartSpecify{
        // 実行日からの日数を指定する
        DAYS = 0,
        // 締め日を指定する
        MONTH = 1
    }

    export enum EndSpecify{
        // 実行日からの日数を指定する
        DAYS = 0,
        // 締め日を指定する
        MONTH = 1
    }

    export enum SpecifyStartMonth{
        // 締め開始月を指定する
        DESIGNATE_CLOSE_START_MONTH = 1,
        // 固定の月度を指定する
        SPECIFY_FIXED_MOON_DEGREE = 2
    }

    export enum SpecifyEndMonth{
        // 締め開始月を指定する
        DESIGNATE_CLOSE_START_MONTH = 1,
        // 固定の月度を指定する
        SPECIFY_FIXED_MOON_DEGREE = 2
    }
    export enum PreviousClassification {

        /*** 前 */
        BEFORE = 0,
        /*** 先*/
        AHEAD= 1
    }
}
