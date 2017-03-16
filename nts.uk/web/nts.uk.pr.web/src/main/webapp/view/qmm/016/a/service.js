var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var nts;
(function (nts) {
    var uk;
    (function (uk) {
        var pr;
        (function (pr) {
            var view;
            (function (view) {
                var qmm016;
                (function (qmm016) {
                    var service;
                    (function (service) {
                        var path = {
                            loadHistoryByUuid: 'pr/proto/wagetable/find/{0}'
                        };
                        var Service = (function (_super) {
                            __extends(Service, _super);
                            function Service() {
                                _super.apply(this, arguments);
                            }
                            Service.prototype.loadHistoryByUuid = function (uuid) {
                                return nts.uk.request.ajax(nts.uk.text.format(path.loadHistoryByUuid, uuid));
                            };
                            return Service;
                        }(view.base.simplehistory.service.BaseService));
                        service.Service = Service;
                        service.instance = new Service({
                            historyMasterPath: 'pr/proto/wagetable/masterhistory',
                            createHisotyPath: 'pr/proto/wagetable/history/create',
                            deleteHistoryPath: 'pr/proto/wagetable/history/delete',
                            updateHistoryStartPath: 'pr/proto/wagetable/history/update/start'
                        });
                    })(service = qmm016.service || (qmm016.service = {}));
                    var model;
                    (function (model) {
                        model.allDemension = [
                            { code: 0, name: '一次元', isCertification: false, isAttendance: false },
                            { code: 1, name: '二次元', isCertification: false, isAttendance: false },
                            { code: 2, name: '三次元', isCertification: false, isAttendance: false },
                            { code: 3, name: '資格', isCertification: true, isAttendance: false },
                            { code: 4, name: '精皆勤手当', isCertification: false, isAttendance: true }
                        ];
                        model.normalDemension = _.filter(model.allDemension, function (di) { return !di.isCertification && !di.isAttendance; });
                        model.specialDemension = _.filter(model.allDemension, function (di) { return di.isCertification || di.isAttendance; });
                        model.demensionMap = new Array();
                        _.forEach(model.allDemension, function (de) {
                            model.demensionMap[de.code] = de;
                        });
                    })(model = qmm016.model || (qmm016.model = {}));
                })(qmm016 = view.qmm016 || (view.qmm016 = {}));
            })(view = pr.view || (pr.view = {}));
        })(pr = uk.pr || (uk.pr = {}));
    })(uk = nts.uk || (nts.uk = {}));
})(nts || (nts = {}));
//# sourceMappingURL=service.js.map