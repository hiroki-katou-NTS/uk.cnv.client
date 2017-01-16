var nts;
(function (nts) {
    var uk;
    (function (uk) {
        var pr;
        (function (pr) {
            var view;
            (function (view) {
                var qmm003;
                (function (qmm003) {
                    var b;
                    (function (b) {
                        var ScreenModel = (function () {
                            function ScreenModel() {
                                var self = this;
                                self.items = ko.observableArray([
                                    new Node('1', '東北', [
                                        new Node('11', '青森県', [
                                            new Node('022012', '青森市', []),
                                            new Node('052019', '秋田市', [])
                                        ]),
                                        new Node('12', '東北', [
                                            new Node('062014', '山形市', [])
                                        ]),
                                        new Node('13', '福島県', [
                                            new Node('062015', '福島市', [])
                                        ])
                                    ]),
                                    new Node('2', '北海道', []),
                                    new Node('3', '東海', []),
                                    new Node('4', '関東', [
                                        new Node('41', '茨城県', [
                                            new Node('062016', '水戸市', []),
                                        ]),
                                        new Node('42', '栃木県', [
                                            new Node('062017', '宇都宮市', [])
                                        ]),
                                        new Node('43', '埼玉県', [
                                            new Node('062019', '川越市', []),
                                            new Node('062020', '熊谷市', []),
                                            new Node('062022', '浦和市', []),
                                        ])
                                    ]),
                                    new Node('5', '東海', [])
                                ]);
                                self.singleSelectedCode = ko.observable(nts.uk.ui.windows.getShared("singleSelectedCode"));
                                console.log(self.singleSelectedCode());
                                self.filteredData = ko.observableArray(nts.uk.util.flatArray(self.items(), "childs"));
                                self.selectedCodes = ko.observableArray([]);
                                self.value = ko.observable(null);
                                self.currentNode = ko.observable(new Node("", "", []));
                                self.singleSelectedCode.subscribe(function (newValue) {
                                    //self.value(newValue);
                                    self.currentNode(self.findByCode(self.filteredData(), newValue));
                                    nts.uk.ui.windows.setShared("currentNode", self.currentNode(), true);
                                });
                            }
                            ScreenModel.prototype.findByCode = function (items, newValue) {
                                var self = this;
                                var _node;
                                _.find(items, function (_obj) {
                                    if (!_node) {
                                        if (_obj.code == newValue) {
                                            _node = _obj;
                                        }
                                    }
                                });
                                return _node;
                            };
                            ;
                            ScreenModel.prototype.ClickButton = function () {
                                //   nts.uk.ui.windows.setShared('singleSelectedCode', self.singleSelectedCode());
                                var self = this;
                                nts.uk.ui.windows.sub.modal('/view/qmm/003/b/index.xhtml', { title: '明細レイアウトの作成＞履歴追加' }).onClosed(function () {
                                    // = nts.uk.ui.windows.getShared("singleSelectedCode");
                                    // self.init1(singleSelectedCode);
                                    nts.uk.ui.windows.setShared('singleSelectedCode', self.singleSelectedCode());
                                });
                                nts.uk.ui.windows.setShared('singleSelectedCode', self.singleSelectedCode());
                            };
                            return ScreenModel;
                        }());
                        b.ScreenModel = ScreenModel;
                        var Node = (function () {
                            function Node(code, name, childs) {
                                var self = this;
                                self.code = code;
                                self.name = name;
                                self.nodeText = self.code + ' ' + self.name;
                                self.childs = childs;
                            }
                            return Node;
                        }());
                        b.Node = Node;
                    })(b = qmm003.b || (qmm003.b = {}));
                })(qmm003 = view.qmm003 || (view.qmm003 = {}));
            })(view = pr.view || (pr.view = {}));
        })(pr = uk.pr || (uk.pr = {}));
    })(uk = nts.uk || (nts.uk = {}));
})(nts || (nts = {}));
;
