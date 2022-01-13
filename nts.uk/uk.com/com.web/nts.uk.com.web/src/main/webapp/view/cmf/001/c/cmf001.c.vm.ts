/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
/// <reference path="../../../../lib/nittsu/nts.uk.com.web.nittsu.bundles.d.ts" />

module nts.uk.com.cmf001.c {

    let datasource = {
        importableItem: {
            init() {
                return (<any> ko).mapping.fromJS({
                    name: null,
                    type: null,
                    constraintName: null,
                    constraint: null,
                    required: null,
                });
            }
        },

        itemMapping: {
            init() {
                let res = {
                    source: null,
                    settingCode: null,
                    domainId: null,
                    itemNo: null,
                    type: null,
                    revisingValue: {
                        usePadding: null,
                        paddingLength: null,
                        paddingMethod: null,
                        useCodeConvert: null,
                        codeConvert: {
                            importWithoutSetting: null,
                            convertDetailsText: "",
                        },
                        isDecimalization: null,
                        decimalizationLength: null,
                        timeHourlySegment: null,
                        timeBaseNumber: null,
                        timeDelimiter: null,
                        timeRounding: null,
                        dateFormat: null,
                    },
                };

                return (<any> ko).mapping.fromJS(res);
            },

        }
    }

    let ICON_CONFIGURED = nts.uk.request.resolvePath(
        __viewContext.rootPath + "../" + (<any> nts).uk.request.WEB_APP_NAME.comjs
         + "/lib/nittsu/ui/style/stylesheets/images/icons/numbered/78.png");

    function renderConfiguredIcon(configured) {
        if (configured === "true") {
            return '<div class="icon-configured"><span id="icon-configured" style="'
                + 'background-image: url(\'' + ICON_CONFIGURED + '\');"></span></div>';
        } else {
            return '';
        }
    }

    @bean()
    export class ViewModel extends ko.ViewModel {

        settingCode: string;
    
    	domainId: int;

        items: KnockoutObservableArray<any> = ko.observableArray([]);

        itemsColumns: KnockoutObservableArray<any> = ko.observableArray([
            { headerText: "NO", key: "itemNo", width: 0, hidden: true },
            { headerText: "名称", key: "name", width: 200 },
            {
                headerText: "",
                key: "alreadyDetail",
                width: 40,
                formatter: renderConfiguredIcon
            },
        ]);

        mappingSource = [
            { code: "CSV", name: "CSV" },
            { code: "固定値", name: "固定値" },
        ];

        selectedItemNo: KnockoutObservable<number> = ko.observable(null);

        isItemSelected = ko.computed(() => !util.isNullOrEmpty(this.selectedItemNo()));

        currentItem: any = ko.observable();
        
        backUrl: string;

        constructor() {
            super();

            this.settingCode = __viewContext.transferred.get().settingCode;
            this.domainId = __viewContext.transferred.get().domainId;
            this.backUrl = __viewContext.transferred.get().screenId === 'cmf001f'
            	? '../f/index.xhtml'
            	: '../b/index.xhtml';

            this.currentItem({
                def: datasource.importableItem.init(),
                selectedMappingType: ko.observable(null),
                csvMapping: datasource.itemMapping.init(),
                fixedMapping: ko.observable(),
            });

            this.selectedItemNo.subscribe(v => this.itemSelected(v));
        }

        mounted() {
            this.loadSetting().done(() => {
                $("body").removeClass("ko-cloak");
            });
        }

        loadSetting() {
            let dfd = $.Deferred();

            let path = "exio/input/setting/find/" + this.settingCode + "/" + this.domainId;
            this.$ajax(path).done(res => {
                this.items.removeAll();
                res.layouts.forEach(layout => {
                    this.items.push(layout);
                });

                dfd.resolve();
            });

            return dfd.promise();
        }

        itemSelected(itemNo) {

            let selectedItem = this.items().filter(e => e.itemNo == itemNo)[0];

            let current = this.currentItem();
            current.def.name(selectedItem.name);
            current.def.type(selectedItem.type);
            current.selectedMappingType(selectedItem.source);

            let fixedValue = selectedItem.fixedValue;
            if (selectedItem.type === 'TIME_POINT' || selectedItem.type === 'TIME_DURATION') {
                fixedValue = Number(fixedValue || 0);
            }
            current.fixedMapping(fixedValue);

            this.loadImportableItem();
            this.loadReviseItem();
			this.checkError();
        }

        loadImportableItem() {

            let path = "/screen/com/cmf/cmf001/importable-item"
                + "/" + this.settingCode
                + "/" + this.domainId
                + "/" + this.selectedItemNo();
            
            this.$ajax(path).done(res => {

                this.currentItem().def.required(res.required);

                if (res.constraint !== null) {
                    if (res.constraint.charType === null) {
                        res.constraint.charType = undefined; // util.getConstraintMesはundefined前提の設計なので
                    }
                    __viewContext.primitiveValueConstraints[res.constraint.name] = res.constraint;
                    let constraintText = (<any> util).getConstraintMes(res.constraint.name) + "　";
                    if (res.constraint.domainType === "Enum") {
                        constraintText = "整数 " + constraintText;
                    }
                    this.currentItem().def.constraintName(res.constraint.name);
                    this.currentItem().def.constraint(constraintText);
                } else {
                    this.currentItem().def.constraintName(null);
                    this.currentItem().def.constraint("");
                }

            });
        }

        loadReviseItem() {

            let path = "/exio/input/setting/assembly/revise/reviseitem/get"
                + "/" + this.settingCode
                + "/" + this.domainId
                + "/" + this.selectedItemNo();

            this.$ajax(path).done(res => {
                let mapping = this.currentItem().csvMapping;

                let r = mapping.revisingValue;
                r.usePadding(null);
                r.paddingLength(null);
                r.paddingMethod(null);
                r.useCodeConvert(null);
                r.codeConvert.importWithoutSetting(null);
                r.codeConvert.convertDetailsText("");
                r.isDecimalization(null);
                r.decimalizationLength(null);
                r.timeHourlySegment(null);
                r.timeBaseNumber(null);
                r.timeDelimiter(null);
                r.timeRounding(null);
                r.dateFormat(null);
                
                (<any> ko).mapping.fromJS(res.revisingValue, {}, mapping.revisingValue);

                if (res.revisingValue && res.revisingValue.codeConvert) {
                    mapping.revisingValue.codeConvert.convertDetailsText(
                        res.revisingValue.codeConvert.details
                            .map(d => d.before + "," + d.after)
                            .join("\n")
                    );
                } else {
                    mapping.revisingValue.codeConvert.convertDetailsText("");
                }
                if (!res.revisingValue || res.revisingValue.timeHourlySegment === undefined){
                    mapping.revisingValue.timeHourlySegment(1);
                }
                if (!res.revisingValue || res.revisingValue.timeBaseNumber === undefined){
                    mapping.revisingValue.timeBaseNumber(0);
                }
                if (!res.revisingValue || res.revisingValue.timeDelimiter === undefined){
                    mapping.revisingValue.timeRounding(0);
                }
                if (!res.revisingValue || res.revisingValue.timeRounding === undefined){
                    mapping.revisingValue.timeRounding(3);
                }

            });
        }

		checkError(){
			nts.uk.ui.errors.clearAll()
			$('.check-target').ntsError('check');
		}

        canSave = ko.computed(() => this.$errors.length === 0 && this.isItemSelected());

		save() {
            
			this.checkError();
			if(!nts.uk.ui.errors.hasError()){
                let path = "/screen/com/cmf/cmf001/save";

                let item = this.currentItem();

                let fixedValue = item.fixedMapping();
                if (item.def.type() === 'DATE' && fixedValue) {
                    let splitValue = fixedValue.split("T");
                    if(splitValue.length === 0){
                        fixedValue = "";
                    }else{
                        fixedValue = splitValue[0].replace(/-/g, "");
                    }
                }

                let s = item.csvMapping.revisingValue;
                let revisingValue = (<any> ko).mapping.toJS({
                    usePadding: s.usePadding,
                    paddingLength: s.paddingLength,
                    paddingMethod: s.paddingMethod,
                    useCodeConvert: s.useCodeConvert,
                    codeConvert: s.codeConvert,
                    isDecimalization: s.isDecimalization,
                    decimalizationLength: s.decimalizationLength,
                    timeHourlySegment: s.timeHourlySegment,
                    timeBaseNumber: s.timeBaseNumber,
                    timeDelimiter: s.timeDelimiter,
                    timeRounding: s.timeRounding,
                    dateFormat: s.dateFormat,
                });

                if (revisingValue.codeConvert) {
                    let convertDetailsText = revisingValue.codeConvert.convertDetailsText;
                    if(convertDetailsText){
                        revisingValue.codeConvert.details = convertDetailsText
                            .split("\n")
                            .map(l => {
                                let p = l.split(",");
                                if (p.length !== 2) return null;
                                return { before: p[0], after: p[1] };
                            })
                            .filter(d => d !== null);
                    }else{
                        revisingValue.codeConvert.details = [];
                    }
                }

                let command = {
                    settingCode: this.settingCode,
                    domainId: this.domainId,
                    itemNo: this.selectedItemNo(),
                    mappingSource: item.selectedMappingType(),
                    fixedValue: fixedValue,
                    revisingValue: revisingValue,
                };

                this.$ajax(path, command).done(res => {
                    ui.dialog.info({ messageId: "Msg_15" });
                    this.loadSetting();
                });
            }
        }

        canDeleteSetting = ko.computed(() => this.isItemSelected());

        deleteSetting() {
            let path = "/screen/com/cmf/cmf001/delete";
            let itemNo = this.selectedItemNo();
            let command = {
                settingCode: this.settingCode,
                domainId: this.domainId,
                itemNo: itemNo,
            };

            ui.dialog.confirm({ messageId: "Msg_18" }).ifYes(() => {
                this.$ajax(path, command).done(() => {
                    ui.dialog.info({ messageId: "Msg_16" });
                    this.loadSetting();
                    this.itemSelected(itemNo);
                });
            });
        }
        
        clickBackButton() {
			let self = this;
			request.jump(self.backUrl, {
				settingCode: self.settingCode,
				domainId: self.domainId
			});
        }
    }

}