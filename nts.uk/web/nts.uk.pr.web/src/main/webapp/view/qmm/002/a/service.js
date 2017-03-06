var nts;
(function (nts) {
    var uk;
    (function (uk) {
        var pr;
        (function (pr) {
            var view;
            (function (view) {
                var qmm002_1;
                (function (qmm002_1) {
                    var a;
                    (function (a) {
                        var service;
                        (function (service) {
                            var paths = {
                                getBankList: "basic/system/bank/find/all",
                                addBranchList: "basic/system/bank/branch/add",
                                updateBranchList: "basic/system/bank/branch/update",
                                removeBranch: "basic/system/bank/branch/remove",
                                removeBank: "basic/system/bank/remove"
                            };
                            function getBankList() {
                                var dfd = $.Deferred();
                                nts.uk.request.ajax("com", paths.getBankList)
                                    .done(function (res) {
                                    dfd.resolve(res);
                                })
                                    .fail(function (res) {
                                    dfd.reject(res);
                                });
                                return dfd.promise();
                            }
                            service.getBankList = getBankList;
                            function addBank(isCreated, bankInfo) {
                                var dfd = $.Deferred();
                                var path = isCreated ? paths.addBranchList : paths.updateBranchList;
                                nts.uk.request.ajax("com", path, bankInfo)
                                    .done(function (res) {
                                    dfd.resolve(res);
                                })
                                    .fail(function (res) {
                                    dfd.reject(res);
                                });
                                return dfd.promise();
                            }
                            service.addBank = addBank;
                            function removeBank(isParentNode, bankCode, branchCode) {
                                var dfd = $.Deferred();
                                var path = isParentNode ? paths.removeBank : paths.removeBranch;
                                var obj = {
                                    bankCode: bankCode,
                                    branchCode: branchCode
                                };
                                nts.uk.request.ajax("com", path, obj)
                                    .done(function (res) {
                                    dfd.resolve(res);
                                })
                                    .fail(function (res) {
                                    dfd.reject(res);
                                });
                                return dfd.promise();
                            }
                            service.removeBank = removeBank;
                        })(service = a.service || (a.service = {}));
                    })(a = qmm002_1.a || (qmm002_1.a = {}));
                })(qmm002_1 = view.qmm002_1 || (view.qmm002_1 = {}));
            })(view = pr.view || (pr.view = {}));
        })(pr = uk.pr || (uk.pr = {}));
    })(uk = nts.uk || (nts.uk = {}));
})(nts || (nts = {}));
