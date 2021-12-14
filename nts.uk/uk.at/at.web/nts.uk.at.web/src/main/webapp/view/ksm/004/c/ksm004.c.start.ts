module nts.uk.at.view.ksm004.c {
    __viewContext.ready(function() {
        var screenModel = new nts.uk.at.view.ksm004.c.viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
        });
    });
}