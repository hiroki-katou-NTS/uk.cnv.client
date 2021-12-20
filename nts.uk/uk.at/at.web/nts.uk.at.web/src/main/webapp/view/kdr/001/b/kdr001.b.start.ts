module nts.uk.at.view.kdr001.b {
    __viewContext.ready(function() {
        let screenModel = new viewmodel.ScreenModel();
        screenModel.start().done(function(self) {
            __viewContext.bind(screenModel);
            // setTimeout(self.setFocus(),10000);

            setTimeout(function(){
                if (self.isNewMode()) {
                    $('#holidayCode').focus();
                } else {
                    $('#holidayName').focus();
                }
              }, 200);
            setTimeout(function() {self.setSpecialHolidayStyle(); }, 200);
        });
    });
}

