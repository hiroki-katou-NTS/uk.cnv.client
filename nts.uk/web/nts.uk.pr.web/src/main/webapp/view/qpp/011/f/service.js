var qpp011;
(function (qpp011) {
    var f;
    (function (f) {
        var service;
        (function (service) {
            var paths = {
                findAllResidential: "pr/core/residential/findallresidential",
                getlistLocation: "pr/core/residential/getlistLocation",
            };
            /**
             * Get list payment date processing.
             */
            function findAllResidential() {
                var dfd = $.Deferred();
                nts.uk.request.ajax(paths.findAllResidential)
                    .done(function (res) {
                    dfd.resolve(res);
                })
                    .fail(function (res) {
                    dfd.reject(res);
                });
                return dfd.promise();
            }
            service.findAllResidential = findAllResidential;
            function getlistLocation() {
                var dfd = $.Deferred();
                nts.uk.request.ajax(paths.getlistLocation)
                    .done(function (res) {
                    dfd.resolve(res);
                })
                    .fail(function (res) {
                    dfd.reject(res);
                });
                return dfd.promise();
            }
            service.getlistLocation = getlistLocation;
        })(service = f.service || (f.service = {}));
    })(f = qpp011.f || (qpp011.f = {}));
})(qpp011 || (qpp011 = {}));
