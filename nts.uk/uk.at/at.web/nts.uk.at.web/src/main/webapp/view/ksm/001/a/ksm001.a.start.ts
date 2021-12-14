module nts.uk.at.view.ksm001.a {
    __viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function(res) {
            __viewContext.bind(res);
            $('#comboTargetYear').focus();
            screenModel.initNextTabFeature();
        });

    });
}