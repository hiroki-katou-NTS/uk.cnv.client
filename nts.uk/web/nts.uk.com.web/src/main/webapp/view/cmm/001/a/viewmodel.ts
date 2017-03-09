module cmm001.a {
    export class ScreenModel {
        // data of items list - tree grid
        items: KnockoutObservableArray<Company>;
        item1s: KnockoutObservableArray<Company>;
        columns2: KnockoutObservableArray<any>;
        currentCode: KnockoutObservable<string>;
        firstCode: KnockoutObservable<string>;
        currentCompanyDto: KnockoutObservable<cmm001.a.service.model.CompanyDto>;
        testCompany: KnockoutObservable<cmm001.a.service.model.CompanyDto>;
        currentCodeList: KnockoutObservableArray<any>;
        currentNode: KnockoutObservable<Company>;
        editMode: boolean = true;
        //tabpanel
        tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
        selectedTab: KnockoutObservable<string>;
        //switch
        roundingRules: KnockoutObservableArray<any>;
        selectedRuleCode: any;
        roundingRules1: KnockoutObservableArray<any>;
        selectedRuleCode1: any;
        roundingRules2: KnockoutObservableArray<any>;
        selectedRuleCode2: any;
        roundingRules3: KnockoutObservableArray<any>;
        selectedRuleCode3: any;
        roundingRules4: KnockoutObservableArray<any>;
        selectedRuleCode4: any;
        //        roundingRule1s: KnockoutObservableArray<any>;
        //        selectedRuleCode1: any;
        //combox
        itemList: KnockoutObservableArray<Company>;
        itemName: KnockoutObservable<string>;
        currentCodeCombox: KnockoutObservable<number>
        selectedCode: any;
        isEnable: KnockoutObservable<boolean>;
        isEditable: KnockoutObservable<boolean>;
        //check
        checked1: KnockoutObservable<boolean>;
        checked2: KnockoutObservable<boolean>;
        //search box
        textSearch: string = "";
        companys: KnockoutObservableArray<cmm001.a.service.model.CompanyDto>;
        constructor() {
            let self = this;
            let node: Company;
            self.init();
            self.currentCode.subscribe(function(newValue) {
                console.log(self.items());
                //                let dfd = $.Deferred<any>();
                //                cmm001.a.service.getAllCompanys().done(function(companies) {
                //                    if (companies.length > 0) {
                //                        self.companys(companies);
                if (self.editMode) {
                    self.getDetailCompany(self.companys(), newValue);

                }
                else {
                    self.editMode = true;
                }

                //                    }
                //                    dfd.resolve();
                //                });
            });
            self.selectedCode.subscribe(function(newValue) {

            })
            //            self.checked2.subscribe(function(newValue) {
            //
            //                self.columns2()[2]._initiallyHidden = false;
            //                self.columns2()[2].hidden = false;
            //                // chu y khi code chinh phai lay lai datasource khong se bi thay doi source
            //                self.item1s = ko.observableArray([
            //                    new Company('01', '日通システム株式会社', ''),
            //                    new Company('02', '有限会社日通ベトナム', ''),
            //                    new Company('03', 'UKシステム株式会社', ''),
            //                    new Company('04', '○○株式会社', '<i class="icon icon-close"></i>')
            //                ]);
            //                if (!newValue) {
            //
            //                    for (let i = 0; i < _.size(self.items()); i++) {
            //                        if (self.items()[i].description === '<i class="icon icon-close"></i>') {
            //                            node = self.items()[i];
            //                            self.items()[i] = new Company('', '', '');
            //                            self.items()[i] = new Company(node.code, node.name, '');
            //                           // console.log(self.items()[i]);
            //                            self.currentCode(self.items()[i].code);
            //                        }
            //                    }
            //
            //
            //                } else {
            //                    self.items([]);
            //                    self.items(self.item1s());
            //                    self.currentCode(self.items()[0].code);
            //                }
            //
            //            });
            self.checked1.subscribe(function(newValue) {
                let $grid = $("#single-list");
                var currentColumns = $grid.igGrid("option", "columns");
                var width = $grid.igGrid("option", "width");
                if (newValue) {
                    currentColumns[2].hidden = false;
                    $grid.igGrid("option", "width", "400px");
                } else {
                    currentColumns[2].hidden = true;
                    $grid.igGrid("option", "width", "400px");
                }
                $grid.igGrid("option", "columns", currentColumns);
                self.start(undefined);
            });

        }
        getDetailCompany(items: any, newValue: string): any {
            let self = this;
            let node = new cmm001.a.service.model.CompanyDto();
            _.find(items, function(obj: cmm001.a.service.model.CompanyDto) {
                if (obj.companyCode == newValue) {

                    $(document).ready(function(data) {
                        $("#A_INP_002").attr('disabled', 'true');
                        $("#A_INP_002").attr('readonly', 'true');
                    });
                    if (obj.displayAttribute.toString() === '0') {
                        self.checked2(false);
                    } else {
                        self.checked2(true);
                    }
                    node.companyCode = obj.companyCode;
                    node.companyName = obj.companyName;
                    node.companyNameAbb = obj.companyNameAbb;
                    node.companyNameKana = obj.companyNameKana;
                    node.corporateMyNumber = obj.corporateMyNumber;
                    node.presidentName = obj.presidentName;
                    node.presidentJobTitle = obj.presidentJobTitle;
                    node.address1 = obj.address1;
                    node.address2 = obj.address2;
                    node.addressKana1 = obj.addressKana1;
                    node.addressKana2 = obj.addressKana2;
                    node.telephoneNo = obj.telephoneNo;
                    node.faxNo = obj.faxNo;
                    node.postal = obj.postal;
                    node.use_Jj_Set = obj.use_Jj_Set;
                    node.use_Kt_Set = obj.use_Kt_Set;
                    node.use_Qy_Set = obj.use_Qy_Set;
                    node.depWorkPlaceSet = obj.depWorkPlaceSet;
                    node.termBeginMon = obj.termBeginMon;
                    self.selectedRuleCode(obj.use_Jj_Set);
                    self.selectedRuleCode1(obj.use_Kt_Set);
                    self.selectedRuleCode2(obj.use_Qy_Set);
                    self.selectedRuleCode3(obj.depWorkPlaceSet);
                    self.selectedCode(obj.termBeginMon);
                    self.testCompany(ko.mapping.fromJS(node));
                    self.currentCompanyDto(ko.mapping.fromJS(node));

                }
            });
            return node;
        }
        resetData(): void {
            let self = this;
            self.editMode = false;
            self.currentCode('');
            let companyDto = new cmm001.a.service.model.CompanyDto();
            companyDto.companyCode = "";
            companyDto.companyName = "";
            companyDto.companyNameGlobal = "";
            companyDto.companyNameAbb = "";
            companyDto.companyNameKana = "";
            companyDto.corporateMyNumber = "";
            companyDto.address1 = "";
            companyDto.address2 = "";
            companyDto.addressKana1 = "";
            companyDto.addressKana2 = "";
            companyDto.depWorkPlaceSet = 0;
            companyDto.displayAttribute = 0;
            companyDto.faxNo = "";
            companyDto.postal = "";
            companyDto.presidentName = "";
            companyDto.presidentJobTitle = "";
            companyDto.telephoneNo = '';
            companyDto.termBeginMon = 0;
            companyDto.use_Gr_Set = 0;
            companyDto.use_Kt_Set = 0;
            companyDto.use_Qy_Set = 0;
            companyDto.use_Jj_Set = 0;
            self.currentCompanyDto(ko.mapping.fromJS(companyDto));

        }
        init(): void {
            let self = this;
            self.checked1 = ko.observable(true);
            self.items = ko.observableArray([]);
            self.columns2 = ko.observableArray([
                { headerText: '会社コード', prop: 'code', width: 80 },
                { headerText: '名称', prop: 'name', width: 200 },
                { headerText: '廃止', prop: 'description', width: 50, hidden: false }
            ]);
            self.currentCode = ko.observable('');
            self.firstCode = ko.observable('');
            self.currentCodeList = ko.observableArray(null);
            //tabpanel
            self.tabs = ko.observableArray([
                { id: 'tab-1', title: '会社基本情報', content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true) },
                { id: 'tab-2', title: '会社所在地・連絡先', content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true) },
                { id: 'tab-3', title: 'システム設定', content: '.tab-content-3', enable: ko.observable(true), visible: ko.observable(true) }
            ]);
            self.selectedTab = ko.observable('tab-1');

            //SWITCH
            self.roundingRules = ko.observableArray([
                new RoundingRule("1", '利用する'),
                new RoundingRule('0', '利用しない')
            ]);
            self.selectedRuleCode = ko.observable(self.roundingRules()[1].code);
            self.roundingRules1 = ko.observableArray([
                new RoundingRule("1", '利用する'),
                new RoundingRule('0', '利用しない')
            ]);
            self.selectedRuleCode1 = ko.observable("1");
            self.roundingRules2 = ko.observableArray([
                new RoundingRule("1", '利用する'),
                new RoundingRule('0', '利用しない')
            ]);
            self.selectedRuleCode2 = ko.observable(self.roundingRules2()[1].code);
            self.roundingRules3 = ko.observableArray([
                new RoundingRule("1", '区別する'),
                new RoundingRule('0', '区別しない')
            ]);
            self.selectedRuleCode3 = ko.observable(self.roundingRules3()[0].code);
            //self.selectedRuleCode1 = ko.observable(1);
            //COMBOX 
            self.itemList = ko.observableArray([
                new Company('1', '1月', ''),
                new Company('2', '2月', ''),
                new Company('3', '3月', ''),
                new Company('4', '4月', ''),
                new Company('5', '5月', ''),
                new Company('6', '6月', ''),
                new Company('7', '7月', ''),
                new Company('8', '8月', ''),
                new Company('9', '9月', ''),
                new Company('10', '10月', ''),
                new Company('11', '11月', ''),
                new Company('12', '12月', '')
            ]);
            self.companys = ko.observableArray([]);
            self.itemName = ko.observable('');
            self.currentCodeCombox = ko.observable(4);
            self.selectedCode = ko.observable('4');
            self.isEnable = ko.observable(true);
            self.isEditable = ko.observable(true);
            self.checked2 = ko.observable(true);
            let companyDto = new cmm001.a.service.model.CompanyDto();
            companyDto.companyCode = "0001";
            companyDto.companyName = "samsung";
            companyDto.companyNameGlobal = "1111";
            companyDto.companyNameAbb = "11111";
            companyDto.companyNameKana = "ｻﾝｽﾝ";
            companyDto.corporateMyNumber = "096-093-345";
            companyDto.address1 = "ﾄｼﾊﾞ";
            companyDto.address2 = "nagoya";
            companyDto.addressKana1 = "ﾄｼﾊﾞ";
            companyDto.addressKana2 = "ﾅﾖｶﾞ";
            companyDto.depWorkPlaceSet = 1;
            companyDto.displayAttribute = 2;
            companyDto.faxNo = "11111111";
            companyDto.postal = "045-123-456";
            companyDto.presidentName = "Lan";
            companyDto.presidentJobTitle = "Dir";
            companyDto.telephoneNo = '096-6092-265';
            companyDto.termBeginMon = 2;
            companyDto.use_Gr_Set = 1;
            companyDto.use_Kt_Set = 1;
            companyDto.use_Qy_Set = 1;
            companyDto.use_Jj_Set = 1;
            self.testCompany = ko.observable(ko.mapping.fromJS(companyDto));
            self.currentCompanyDto = ko.observable(ko.mapping.fromJS(companyDto));
        }
        // register company information
        //BTN-002 
        ClickRegister(): any {
            let self = this;
            let dfd = $.Deferred<any>();
            let currentCompany: service.model.CompanyDto;
            currentCompany = ko.toJS(self.currentCompanyDto);
            //console.log( currentCompany);
            //self.currentCompanyDto().companyCode = $("#A_INP_002").val();
            let companyNameGlobal: string;

            //self.currentCompanyDto().address2 = $("#C_INP_003").val();
            // self.currentCompanyDto().companyNameAbb = $("#A_INP_005").val();

            //self.currentCompanyDto().faxNo = $("#C_INP_007").val();

            // self.currentCompanyDto().presidentName = $("#B_INP_002").val()
            //  self.currentCompanyDto().presidentJobTitle = $("#B_INP_003").val();

            let termBeginMon: number;

            self.currentCompanyDto().use_Kt_Set = self.selectedRuleCode1();
            self.currentCompanyDto().use_Jj_Set = self.selectedRuleCode();
            self.currentCompanyDto().use_Qy_Set = self.selectedRuleCode2();
            self.currentCompanyDto().depWorkPlaceSet = self.selectedRuleCode3();
            self.currentCompanyDto().termBeginMon = self.selectedCode();
            let use_Ac_Set: number;
            let use_Gw_Set: number;
            let use_Hc_Set: number;
            let use_Lc_Set: number;
            let use_Bi_Set: number;
            let use_Rs01_Set: number;
            let use_Rs02_Set: number;
            let use_Rs03_Set: number;
            let use_Rs04_Set: number;
            let use_Rs05_Set: number;
            let use_Rs06_Set: number;
            let use_Rs07_Set: number;
            let use_Rs08_Set: number;
            let use_Rs10_Set: number;
            let error: boolean;
            if (nts.uk.text.allHalfKatakana(currentCompany.companyNameKana) === true) {
                $('#A_INP_004').ntsError('clear');
                //self.currentCompanyDto().companyNameKana = $("#A_INP_004").val();
                error = true;

            } else {
                $('#A_INP_004').ntsError('set', 'this text must be half katakana type');


                error = false;
            }
            let error1: boolean;
            if (currentCompany.companyName !== "") {
                $('#A_INP_003').ntsError('clear');
                //self.currentCompanyDto().companyName = $("#A_INP_003").val();
                error1 = true;
            } else {
                $("#A_INP_003").ntsError('set', 'this text must be not null');
                error1 = false;

            }
            let error2: boolean;
            if (currentCompany.address1 != "") {
                $('#C_INP_002').ntsError('clear');
                // self.currentCompanyDto().address1 = $("#C_INP_002").val();
                error2 = true;
            } else {
                $("#C_INP_002").ntsError('set', 'this address must not null');
                error2 = false;
            }
            let error3: boolean;
            //let t1 = $("#C_INP_001").val();
            let t1 = currentCompany.postal;
            let t2 = t1.split("-");
            let text = "";
            for (let i = 0; i < t2.length; i++) {
                text += Number(t2[i]);
            }
            if (Number(text) > 0) {
                $('#C_INP_001').ntsError('clear');
                //self.currentCompanyDto().postal = $("#C_INP_001").val();
                error3 = true;
            } else {
                $("#C_INP_001").ntsError('set', 'this postal is  invalid text');
                error3 = false;

            }

            //let addressKana1;
            let error4: boolean;

            if (nts.uk.text.allHalfKatakana(currentCompany.addressKana1) === true && (currentCompany.addressKana1 === "" || currentCompany.addressKana1 !== "")) {
                $('#C_INP_004').ntsError('clear');
                //self.currentCompanyDto().addressKana1 = $("#C_INP_004").val();
                error4 = true;

            } else {
                $('#C_INP_004').ntsError('set', 'this text must be half katakana type');
                error4 = false;
            }
            let error5: boolean;
            if (nts.uk.text.allHalfKatakana(currentCompany.addressKana2) === true && (currentCompany.addressKana2 === "" || currentCompany.addressKana2 !== "")) {
                $('#C_INP_005').ntsError('clear');
                //self.currentCompanyDto().addressKana2 = $("#C_INP_005").val();
                error5 = true;

            } else {
                $('#C_INP_005').ntsError('set', 'this text must be half katakana type');
                error5 = false;
            }

            let error6: boolean;
            let telNo = currentCompany.telephoneNo;
            let test1 = telNo.split('-');
            let checkTel: boolean;
            for (let i = 0; i < test1.length; i++) {
                if (Number(test1[i])) { checkTel = true; }
                else { checkTel = false; break; }
            }
            if ((telNo === " " || telNo !== "") && checkTel) {
                $('#C_INP_006').ntsError('clear');
                //self.currentCompanyDto().telephoneNo = $("#C_INP_006").val();
                error6 = true;
            } else {
                $("#C_INP_006").ntsError('set', 'this telephone Number is  invalid text');
                error6 = false;
            }
            let error7: boolean;
            let faxNo = currentCompany.faxNo;
            let test2 = faxNo.split('-');
            let checkFax: boolean;
            for (let i = 0; i < test2.length; i++) {
                if (Number(test2[i])) { checkFax = true; }
                else { checkFax = false; break; }
            }
            if (((faxNo === " " || faxNo !== "")) && checkFax) {
                $('#C_INP_007').ntsError('clear');
                //self.currentCompanyDto().faxNo = $("#C_INP_007").val();
                error7 = true;
            } else {
                $("#C_INP_007").ntsError('set', 'this fax Number is  invalid text');
                error7 = false;
            }
            let allerror: boolean;
            allerror = error && error1 && error2 && error3 && error4 && error5 && error6 && error7;
            if (allerror === true) {
                if (currentCompany.companyCode != self.currentCode()) {
                    cmm001.a.service.addData(currentCompany).done(function() {
                        self.start(currentCompany.companyCode);
                        alert("変更された内容が登録されていません。\r\nよろしいですか。");
                    });

                } else {
                    cmm001.a.service.updateData(currentCompany).done(function() {
                        self.start(currentCompany.companyCode);
                        alert("変更された内容が登録されていません。\r\nよろしいですか。");

                    });


                }
            }
        }
        //BTN-003 -Setting cac thong so ban dau
        ClickSetting(): void {
            alert("Settting");

        }
        //BTN-004- Master correction log 
        ClickLog(): void {
            alert("Log");

        }
        //SEL-Btn-001
        ClickSel001(): void {
            alert("Sel001");
        }
        //SEL-Btn-001
        ClickSel002(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred<any>();
            let item: any;
            item = ko.observable(new Company('01', '日通システム株式会社', ''));

            return dfd.promise();

        }
        //SEL-Btn-001
        Browse(): void {
            alert("Browse!");
        }
        start(currentCode: string): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred<any>();
            cmm001.a.service.getAllCompanys().done(function(companies) {
                if (companies.length > 0) {
                    self.companys(companies);
                    self.buildGridDataSource(companies);
                    if (currentCode === undefined) {
                        self.currentCode(self.firstCode());
                        self.getDetailCompany(self.companys, self.firstCode());
                    } else {
                        self.currentCode(currentCode);
                    }
                }
                dfd.resolve();
            });
            return dfd.promise();
        }
        buildGridDataSource(items: Array<cmm001.a.service.model.CompanyDto>): any {
            let self = this;
            self.items = ko.observableArray([]);
            _.forEach(items, function(obj: cmm001.a.service.model.CompanyDto) {
                if (obj.displayAttribute.toString() === "0") {
                    self.items().push(new Company(obj.companyCode.toString(), obj.companyName, ''));
                } else {
                    self.items().push(new Company(obj.companyCode.toString(), obj.companyName, '<i style="margin-left: 15px" class="icon icon-close"></i>'));
                }

            });
            self.firstCode(self.items()[0].code);
            //self.currentCode(self.currentCompanyDto().companyCode);

        }


    }
    export class Company {
        code: string;
        name: string;
        description: string;
        constructor(code: string, name: string, description: string) {
            this.code = code;
            this.name = name;
            this.description = description;
        }
    }
    export class TextEditor {
        value: string;
        constraint: string;
        option: string;
        enable: boolean;
        readonly: boolean;
        constructor(value: string, constraint: string, option: string, enable: boolean, readonly: boolean) {
            this.value = value;
            this.constraint = constraint;
            this.option = option;
            this.enable = enable;
            this.readonly = readonly;
        }
    }
    export class GridList {
        height: number;
        options: KnockoutObservableArray<Company>;
        optionsValue: string;
        columns: KnockoutObservableArray<any>;
        value: string;
        contructor(height: number, options: KnockoutObservableArray<Company>, optionsValue: string, columns: KnockoutObservableArray<any>, value: string) {
            this.height = height;
            this.options = options;
            this.optionsValue = optionsValue;
            this.columns = columns;
            this.value = value;
        }
    }
    export class RoundingRule {
        code: string;
        name: string;
        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }


    }

};
