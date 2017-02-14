var nts;
(function (nts) {
    var uk;
    (function (uk) {
        var pr;
        (function (pr) {
            var view;
            (function (view) {
                var qmm008;
                (function (qmm008) {
                    var c;
                    (function (c) {
                        var service;
                        (function (service) {
                            var servicePath = {
                                getOfficeDetailData: "pr/insurance/social/find",
                                updateOffice: "pr/insurance/social/update",
                                regiterOffice: "pr/insurance/social/register",
                            };
                            function getOfficeItemDetail(code) {
                                var dfd = $.Deferred();
                                var findPath = servicePath.getOfficeDetailData + "/" + code;
                                nts.uk.request.ajax(findPath).done(function (data) {
                                    dfd.resolve(data);
                                });
                                return dfd.promise();
                            }
                            service.getOfficeItemDetail = getOfficeItemDetail;
                            function register(data) {
                                return nts.uk.request.ajax(servicePath.regiterOffice, data);
                            }
                            service.register = register;
                            function update(data) {
                                return nts.uk.request.ajax(servicePath.updateOffice, data);
                            }
                            service.update = update;
                            var model;
                            (function (model) {
                                var finder;
                                (function (finder) {
                                    var ChooseOption = (function () {
                                        function ChooseOption() {
                                        }
                                        return ChooseOption;
                                    }());
                                    finder.ChooseOption = ChooseOption;
                                    var OfficeItemDto = (function () {
                                        function OfficeItemDto(companyCode, code, name, shortName, picName, picPosition, potalCode, prefecture, address1st, address2nd, kanaAddress1st, kanaAddress2nd, phoneNumber, healthInsuOfficeRefCode1st, healthInsuOfficeRefCode2nd, pensionOfficeRefCode1st, pensionOfficeRefCode2nd, welfarePensionFundCode, officePensionFundCode, healthInsuCityCode, healthInsuOfficeSign, pensionCityCode, pensionOfficeSign, healthInsuOfficeCode, healthInsuAssoCode, memo) {
                                            this.companyCode = companyCode;
                                            this.code = code;
                                            this.name = name;
                                            this.shortName = shortName;
                                            this.picName = picName;
                                            this.picPosition = picPosition;
                                            this, potalCode = potalCode;
                                            this.prefecture = prefecture;
                                            this.address1st = address1st;
                                            this.address2nd = address2nd;
                                            this.kanaAddress1st = kanaAddress1st;
                                            this.kanaAddress2nd = kanaAddress2nd;
                                            this.phoneNumber = phoneNumber;
                                            this.healthInsuOfficeRefCode1st = healthInsuOfficeRefCode1st;
                                            this.healthInsuOfficeRefCode2nd = healthInsuOfficeRefCode2nd;
                                            this.pensionOfficeRefCode1st = pensionOfficeRefCode1st;
                                            this.pensionOfficeRefCode2nd = pensionOfficeRefCode2nd;
                                            this.welfarePensionFundCode = welfarePensionFundCode;
                                            this.officePensionFundCode = officePensionFundCode;
                                            this.healthInsuCityCode = healthInsuCityCode;
                                            this.healthInsuOfficeSign = healthInsuOfficeSign;
                                            this.pensionCityCode = pensionCityCode;
                                            this.pensionOfficeSign = pensionOfficeSign;
                                            this.healthInsuOfficeCode = healthInsuOfficeCode;
                                            this.healthInsuAssoCode = healthInsuAssoCode;
                                        }
                                        return OfficeItemDto;
                                    }());
                                    finder.OfficeItemDto = OfficeItemDto;
                                })(finder = model.finder || (model.finder = {}));
                            })(model = service.model || (service.model = {}));
                        })(service = c.service || (c.service = {}));
                    })(c = qmm008.c || (qmm008.c = {}));
                })(qmm008 = view.qmm008 || (view.qmm008 = {}));
            })(view = pr.view || (pr.view = {}));
        })(pr = uk.pr || (uk.pr = {}));
    })(uk = nts.uk || (nts.uk = {}));
})(nts || (nts = {}));
