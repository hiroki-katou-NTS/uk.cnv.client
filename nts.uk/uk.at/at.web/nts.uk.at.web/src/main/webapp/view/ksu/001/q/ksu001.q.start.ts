module nts.uk.at.view.ksu001.q {
    __viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
        }).then(function() {
            $('[tabindex= 5]').focus();
        });
    });
}