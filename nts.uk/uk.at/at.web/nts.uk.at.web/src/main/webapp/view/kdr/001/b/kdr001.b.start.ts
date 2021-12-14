module nts.uk.at.view.kdr001.b {
    __viewContext.ready(function() {
        let screenModel = new viewmodel.ScreenModel();
        screenModel.start().done(function(self) {
            __viewContext.bind(screenModel);
            self.setFocus();
            setTimeout(function() {self.setSpecialHolidayStyle(); }, 200);
        });
    });
}

