module qmm003.a.viewmodel {
    export class ScreenModel {
        // data of items list - tree grid
        items: KnockoutObservableArray<Node>;
        item1s: any;
        singleSelectedCode: KnockoutObservable<string>;
        headers: any;
        // curentNode: any;
        currentNode: KnockoutObservable<Node>;
        labelSubsub: any; // show label sub sub of root
        selectedCode: KnockoutObservable<string>;
        isEnable: KnockoutObservable<boolean>;
        isEditable: KnockoutObservable<boolean>;
        editMode: boolean = true; // true là mode thêm mới, false là mode sửa 
        filteredData: KnockoutObservableArray<Node> = ko.observableArray([]);
        selectedCodes: KnockoutObservableArray<any>;
        mode: KnockoutObservable<boolean>;
        testNode = [];
        nodeRegionPrefectures: KnockoutObservableArray<Node> = ko.observableArray([]);
        japanLocation: Array<service.model.RegionObject> = [];
        precfecture: Array<Node> = [];
        itemPrefecture: KnockoutObservableArray<Node> = ko.observableArray([]);
        residentalTaxList: KnockoutObservableArray<service.model.ResidentialTax> = ko.observableArray([]);
        currentResidential: KnockoutObservable<service.model.ResidentialTax>;
        currentResi: KnockoutObservable<service.model.ResidentialTax>;
        residentialReportCode: KnockoutObservable<string> = ko.observable("");
        constructor() {
            let self = this;
            self.init();
            self.selectedCodes = ko.observableArray([]);
            self.singleSelectedCode.subscribe(function(newChange) {
                if (self.editMode) {
                    let currentNode: Node;
                    currentNode = self.findByCode(self.filteredData(), newChange);
                    self.currentNode(ko.mapping.fromJS(currentNode));
                    self.findPrefectureByResiTax(newChange);

                    self.currentResi(ko.mapping.fromJS(self.findResidentialByCode(self.residentalTaxList(), newChange)));
                    self.residentialReportCode(self.currentResi().resiTaxReportCode);
                    self.currentResidential(ko.mapping.fromJS(self.currentResi()));

                } else {
                    self.editMode = true;
                }
            });
        }
        
        // find Node By code (singleSelectedCode)
        findByCode(items: Array<Node>, newValue: string): Node {
            let self = this;
            let node: Node;
            _.find(items, function(obj: Node) {
                if (!node) {
                    if (obj.code == newValue) {
                        node = obj;
                        $(document).ready(function(data) {
                            $("#A_INP_002").attr('disabled', 'true');
                            $("#A_INP_002").attr('readonly', 'true');
                        });
                    }
                }
            });

            return node;
        };
        
        // find  Node by resiTaxCode
        findResidentialByCode(items: Array<service.model.ResidentialTax>, newValue: string): service.model.ResidentialTax {
            let self = this;
            let objResi: service.model.ResidentialTax;
            _.find(items, function(obj: service.model.ResidentialTax) {
                if (!objResi) {
                    if (obj.resiTaxCode == newValue) {
                        objResi = obj;
                    }
                }
            });

            return objResi;
        };
        // find Node by name
        findByName(items: Array<Node>, name: string): Node {
            let self = this;
            let node: Node;
            _.find(items, function(obj: Node) {
                if (!node) {
                    if (obj.name === name) {
                        node = obj;
                    }
                }
            });
            return node;
        }
        //  set selectedcode by prefectureCode
        findPrefectureByResiTax(code: string): void {
            let self = this;
            _.each(self.items(), function(objRegion: Node) {
                _.each(objRegion.childs, function(objPrefecture: Node) {
                    _.each(objPrefecture.childs, function(obj: Node) {
                        if (obj.code === code) {
                            self.selectedCode(objPrefecture.code);
                        }
                    });
                });
            });

        }
        // create array prefecture from japan location
        buildPrefectureArray(): void {
            let self = this;
            _.map(self.japanLocation, function(region: service.model.RegionObject) {
                _.each(region.prefectures, function(objPrefecture: service.model.PrefectureObject) {
                    return self.precfecture.push(new Node(objPrefecture.prefectureCode, objPrefecture.prefectureName, []));
                });
            });

        }
        
        // reset Data
        resetData(): void {
            let self = this;
            self.editMode = false;
            self.currentNode(ko.mapping.fromJS(new Node('', '', [])));
            let node = new service.model.ResidentialTax();
            node.companyCode = '';
            node.resiTaxCode = '';
            node.resiTaxAutonomy = '';
            node.prefectureCode = '';
            node.resiTaxReportCode = '';
            node.registeredName = '';
            node.companyAccountNo = '';
            node.companySpecifiedNo = '';
            node.cordinatePostalCode = '';
            node.cordinatePostOffice = '';
            node.memo = '';
            self.currentResi(node);
            self.currentResidential(ko.mapping.fromJS(self.currentResi()));
            self.singleSelectedCode("");
            self.selectedCode("");
        }
        // function not use
        search(): void {

            let inputSearch = $("#search").find("input.ntsSearchBox").val();
            if (inputSearch === '') {
                $('#search').ntsError('set', 'inputSearch が入力されていません。');
            } else {
                $('#search').ntsError('clear');
            }

            // errror search
            let error: boolean;
            _.find(this.filteredData(), function(obj: Node) {
                if (obj.code !== inputSearch) {
                    error = true;
                }
            });
            if (error = true) {
                $('#search').ntsError('set', '対象データがありません。');
            } else {
                $('#search').ntsError('clear');
            }
        }
        
        // init menu
        init(): void {
            let self = this;
            // data of treegrid
            self.items = ko.observableArray([]);
            self.mode = ko.observable(null);
            let node : Node;
            self.currentNode = ko.observable(ko.mapping.fromJS(new Node('022012', '青森市', [])));
            self.isEnable = ko.observable(true);
            self.isEditable = ko.observable(true);
            self.selectedCode = ko.observable("11");
            self.singleSelectedCode = ko.observable('022012');
            let objResi = new service.model.ResidentialTax();
            objResi.companyCode ='a';
            objResi.resiTaxCode = 'a';
            objResi.resiTaxAutonomy = 'a';
            objResi.prefectureCode = '42';
            objResi.resiTaxReportCode = '062014';
            objResi.registeredName = 'aaa';
            objResi.companyAccountNo = 'b';
            objResi.companySpecifiedNo = 'cccccc';
            objResi.cordinatePostalCode = '11111';
            objResi.cordinatePostOffice = 'bbbbb';
            objResi.memo = 'sssssssssssssssss';
            self.currentResi = ko.observable((objResi));
            self.currentResidential = ko.observable(ko.mapping.fromJS(self.currentResi()));
        }
        openBDialog() {
            let self = this;
            let singleSelectedCode: any;
            let currentNode: Node;
            nts.uk.ui.windows.setShared('singleSelectedCode', self.singleSelectedCode(), true);
            nts.uk.ui.windows.sub.modeless('/view/qmm/003/b/index.xhtml', { title: '住民税納付先の登録＞住民税納付先一覧', dialogClass: "no-close" }).onClosed(function(): any {
                singleSelectedCode = nts.uk.ui.windows.getShared("singleSelectedCode");
                currentNode = nts.uk.ui.windows.getShared("currentNode");
                console.log(currentNode);
                self.editMode = false;
                self.singleSelectedCode(singleSelectedCode);
                self.currentNode(ko.mapping.fromJS(currentNode));
                //console.log(ko.mapping.fromJS(self.currentNode()));
            });
        }
        openCDialog() {
            let self = this;
            let currentNode: Node;
            nts.uk.ui.windows.sub.modeless("/view/qmm/003/c/index.xhtml", { title: '住民税納付先の登録＞住民税報告先一覧', dialogClass: "no-close" }).onClosed(function(): any {
                currentNode = nts.uk.ui.windows.getShared('currentNode');
                self.residentialReportCode(currentNode.nodeText);
//                self.currentResi().resiTaxReportCode = ko.mapping.fromJS(currentNode.nodeText);
//                console.log(self.residentialReportCode());
//                console.log(self.currentResi().resiTaxReportCode);

            });
        }
        openDDialog() {
            let self = this;
            let items: any;
            nts.uk.ui.windows.sub.modeless("/view/qmm/003/d/index.xhtml", { title: '住民税納付先の登録　＞　一括削除', dialogClass: "no-close" }).onClosed(function(): any {
                items = nts.uk.ui.windows.getShared('items');
                self.items([]);
                self.items(items);
            });
        }
        openEDialog() {

            let self = this;
            let labelSubsub: any;
            nts.uk.ui.windows.sub.modeless("/view/qmm/003/e/index.xhtml", { title: '住民税納付先の登録＞納付先の統合', dialogClass: "no-close" }).onClosed(function(): any {
                labelSubsub = nts.uk.ui.windows.getShared('labelSubsub');
                self.labelSubsub(labelSubsub);
                console.log(labelSubsub);
            });
        }
        //11.初期データ取得処理 11. Initial data acquisition processing
        start(): JQueryPromise<any> {
            var dfd = $.Deferred<any>();
            let self = this;
            (qmm003.a.service.getResidentialTax()).done(function(data: Array<qmm003.a.service.model.ResidentialTax>) {
                if (data.length > 0) {
                    self.mode(true); // true, update mode 
                    self.residentalTaxList(data);
                    (qmm003.a.service.getRegionPrefecture()).done(function(locationData: Array<service.model.RegionObject>) {
                        self.japanLocation = locationData;
                        self.buildPrefectureArray();
                        self.itemPrefecture(self.precfecture);
                        self.buildResidentalTaxTree();
                        let node: Array<Node> = [];
                        node = nts.uk.util.flatArray(self.nodeRegionPrefectures(), "childs");
                        self.filteredData(node);
                        self.items(self.nodeRegionPrefectures());
                    });


                } else {
                    self.mode(false)// false, new mode
                }

                dfd.resolve();

            }).fail(function(res) {

            });

            return dfd.promise();
        }

        buildResidentalTaxTree() {
            let self = this;
            var child = [];
            let i = 0;
            _.each(self.residentalTaxList(), function(objResi: qmm003.a.service.model.ResidentialTax) {
                _.each(self.japanLocation, function(objRegion: service.model.RegionObject) {
                    let cout: boolean = false;
                    let coutPre: boolean = false;
                    _.each(objRegion.prefectures, function(objPrefecture: service.model.PrefectureObject) {
                        if (objPrefecture.prefectureCode === objResi.prefectureCode) {
                            _.each(self.nodeRegionPrefectures(), function(obj: Node) {
                                if (obj.code === objRegion.regionCode) {
                                    _.each(obj.childs, function(objChild: Node) {
                                        if (objChild.code === objPrefecture.prefectureCode) {
                                            objChild.childs.push(new Node(objResi.resiTaxCode, objResi.resiTaxAutonomy, []));
                                            coutPre = true;
                                        }
                                    });
                                    if (coutPre === false) {
                                        obj.childs.push(new Node(objPrefecture.prefectureCode, objPrefecture.prefectureName, [new Node(objResi.resiTaxCode, objResi.resiTaxAutonomy, [])]));
                                    }
                                    cout = true;
                                }
                            });
                            if (cout === false) {
                                let chi = [];
                                self.nodeRegionPrefectures.push(new Node(objRegion.regionCode, objRegion.regionName, [new Node(objPrefecture.prefectureCode, objPrefecture.prefectureName, [new Node(objResi.resiTaxCode, objResi.resiTaxAutonomy, [])])]));
                            }
                        }
                    });
                });

            });
        }

        ClickRegister(): void {
            let self = this;
            let objResi = new service.model.ResidentialTax();
            let node = new  Node('','',[]);
            node = ko.toJS(self.currentNode());
            console.log(node.name);
            console.log(node.code);
            objResi = ko.toJS(self.currentResidential());
            objResi.resiTaxCode = node.code;
            objResi.resiTaxAutonomy = node.name;
            objResi.prefectureCode = self.selectedCode();
            objResi.resiTaxReportCode = self.residentialReportCode();
            console.log(objResi);
            qmm003.a.service.addResidential(objResi).done(function() {
                self.items([]);
                self.nodeRegionPrefectures([]);
                self.start();
                self.singleSelectedCode(objResi.resiTaxCode);

            });



        }



    }
    export class Node {
        code: string;
        name: string;
        nodeText: string;
        childs: any;
        constructor(code: string, name: string, childs: Array<Node>) {
            let self = this;
            self.code = code;
            self.name = name;
            self.nodeText = self.code + ' ' + self.name;
            self.childs = childs;

        }
    }
};
