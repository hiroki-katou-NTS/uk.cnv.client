module nts.uk.at.view.ksm001.f {
    __viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function(res) {
            __viewContext.bind(screenModel);
            $('#first-color-picker').focus();
        });
    });
}