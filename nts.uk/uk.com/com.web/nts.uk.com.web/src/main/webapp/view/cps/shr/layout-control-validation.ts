module nts.layout {
    import ajax = nts.uk.request.ajax;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;

    export const validate = {
        removeDoubleLine: (items: Array<any>) => {
            let maps = _(items)
                .map((x, i) => (x.layoutItemType == IT_CLA_TYPE.SPER) ? i : -1)
                .filter(x => x != -1).value();

            _.each(maps, (t, i) => {
                if (maps[i + 1] == t + 1) {
                    _.remove(items, (m: any) => {
                        let item: any = ko.unwrap(items)[maps[i + 1]];
                        return item && item.layoutItemType == IT_CLA_TYPE.SPER && item.layoutID == m.layoutID;
                    });
                }
            });
        }
    }

    class constraint {
        lstCls: Array<any> = [];

        constructor(lstCls: Array<any>) {
            let self = this;

            self.lstCls = lstCls;
        }

        find = (categoryCode: string, subscribeCode: string): IFindData => {
            let self = this,
                controls: Array<any> = _(self.lstCls).filter(x => _.has(x, "items") && _.isFunction(x.items)).map(x => x.items()).flatten().flatten().value(),
                subscribe: any = _.find(controls, (x: any) => x.categoryCode.indexOf(categoryCode) > -1 && x.itemCode == subscribeCode);

            if (subscribe) {
                return <IFindData>{
                    data: subscribe,
                    ctrl: $('#' + subscribe.itemDefId.replace(/[-_]/g, ''))
                };
            }

            return null;
        };

        finds = (categoryCode: string, subscribesCode: Array<string>): Array<IFindData> => {
            if (!_.isArray(subscribesCode)) {
                throw "[subscribesCode] isn't an array!";
            }

            let self = this,
                controls: Array<any> = _(self.lstCls).filter(x => _.has(x, "items") && _.isFunction(x.items)).map(x => x.items()).flatten().flatten().value(),
                subscribes: Array<any> = _.filter(controls, (x: any) => x.categoryCode.indexOf(categoryCode) > -1 && (subscribesCode || []).indexOf(x.itemCode) > -1);

            return subscribes.map(x => {
                return <IFindData>{
                    data: x,
                    ctrl: $('#' + x.itemDefId.replace(/[-_]/g, ''))
                };
            });
        };

        findChilds = (categoryCode: string, parentCode: string): Array<IFindData> => {
            let self = this,
                controls: Array<any> = _(self.lstCls).filter(x => _.has(x, "items") && _.isFunction(x.items)).map(x => x.items()).flatten().flatten().value(),
                subscribes: Array<any> = _.filter(controls, (x: any) => x.categoryCode.indexOf(categoryCode) > -1 && x.itemParentCode == parentCode);

            return subscribes.map(x => {
                return <IFindData>{
                    data: x,
                    ctrl: $('#' + x.itemDefId.replace(/[-_]/g, ''))
                };
            });
        };
    }

    const fetch = {
        check_start_end: (param: ICheckParam) => ajax(`ctx/pereg/person/common/checkStartEnd`, param),
        check_multi_time: (param: ICheckParam) => ajax(`ctx/pereg/person/common/checkMultiTime`, param)
    }

    export class validation {
        finder: IFinder = undefined;
        constructor(lstCls: Array<any>) {
            let self = this;
            self.finder = new constraint(lstCls);

            self.radio();
            self.button();
            self.combobox();
        }

        radio = () => {
            let self = this,
                finder = self.finder,
                CS00020_IS00248: IFindData = finder.find('CS00020', 'IS00248'),
                CS00020_IS00121: IFindData = finder.find('CS00020', 'IS00121');

            if (CS00020_IS00248) {
                CS00020_IS00248.data.value.subscribe(x => {
                    let ctrls: Array<IFindData> = finder.findChilds(CS00020_IS00248.data.categoryCode, CS00020_IS00248.data.itemParentCode);

                    _.each(ctrls, c => {
                        if (c.data.itemCode != CS00020_IS00248.data.itemCode) {
                            c.data.editable(x == 1);
                            c.data.readonly(x != 1);
                        }
                    });
                });
            }

            if (CS00020_IS00121) {
                CS00020_IS00121.data.value.subscribe(x => {
                    let ctrls: Array<IFindData> = finder.findChilds(CS00020_IS00248.data.categoryCode, CS00020_IS00248.data.itemParentCode);

                    _.each(ctrls, c => {
                        if (c.data.itemCode != CS00020_IS00248.data.itemCode) {
                            c.data.editable(x == 1);
                        }
                    });
                });
            }
        };

        button = () => {
            let self = this,
                finder = self.finder,
                CS00020_IS00130: IFindData = finder.find('CS00020', 'IS00130'),

                CS00020_IS00131: IFindData = finder.find('CS00020', 'IS00131'),
                CS00020_IS00133: IFindData = finder.find('CS00020', 'IS00133'),
                CS00020_IS00134: IFindData = finder.find('CS00020', 'IS00134'),
                CS00020_IS00136: IFindData = finder.find('CS00020', 'IS00136'),
                CS00020_IS00139: IFindData = finder.find('CS00020', 'IS00139'),
                CS00020_IS00140: IFindData = finder.find('CS00020', 'IS00140'),
                CS00020_IS00142: IFindData = finder.find('CS00020', 'IS00142'),
                CS00020_IS00143: IFindData = finder.find('CS00020', 'IS00143'),
                CS00020_IS00145: IFindData = finder.find('CS00020', 'IS00145'),
                CS00020_IS00146: IFindData = finder.find('CS00020', 'IS00146'),


                CS00020_IS00238: IFindData = finder.find('CS00020', 'IS00238'),
                CS00020_IS00239: IFindData = finder.find('CS00020', 'IS00239'),

                CS00020_IS00241: IFindData = finder.find('CS00020', 'IS00241'),
                CS00020_IS00242: IFindData = finder.find('CS00020', 'IS00242'),
                CS00020_IS00244: IFindData = finder.find('CS00020', 'IS00244'),
                CS00020_IS00245: IFindData = finder.find('CS00020', 'IS00245'),
 
                CS00020_IS00148: IFindData = finder.find('CS00020', 'IS00148'),
                CS00020_IS00149: IFindData = finder.find('CS00020', 'IS00149'),
                CS00020_IS00151: IFindData = finder.find('CS00020', 'IS00151'),
                CS00020_IS00152: IFindData = finder.find('CS00020', 'IS00152'),
                CS00020_IS00154: IFindData = finder.find('CS00020', 'IS00154'),
                CS00020_IS00155: IFindData = finder.find('CS00020', 'IS00155'),

                CS00020_IS00157: IFindData = finder.find('CS00020', 'IS00157'),
                CS00020_IS00158: IFindData = finder.find('CS00020', 'IS00158'),
                CS00020_IS00166: IFindData = finder.find('CS00020', 'IS00166'),
                CS00020_IS00167: IFindData = finder.find('CS00020', 'IS00167'),
                CS00020_IS00175: IFindData = finder.find('CS00020', 'IS00175'),
                CS00020_IS00176: IFindData = finder.find('CS00020', 'IS00176'),
                CS00020_IS00184: IFindData = finder.find('CS00020', 'IS00184'),
                CS00020_IS00185: IFindData = finder.find('CS00020', 'IS00185'),

                CS00020_IS00187: IFindData = finder.find('CS00020', 'IS00187'),
                CS00020_IS00188: IFindData = finder.find('CS00020', 'IS00188'),
                CS00020_IS00190: IFindData = finder.find('CS00020', 'IS00190'),
                CS00020_IS00191: IFindData = finder.find('CS00020', 'IS00191'),
                
                CS00020_IS00193: IFindData = finder.find('CS00020', 'IS00193'),
                CS00020_IS00194: IFindData = finder.find('CS00020', 'IS00194'),
                CS00020_IS00196: IFindData = finder.find('CS00020', 'IS00196'),
                CS00020_IS00197: IFindData = finder.find('CS00020', 'IS00197'),
                CS00020_IS00199: IFindData = finder.find('CS00020', 'IS00199'),
                CS00020_IS00200: IFindData = finder.find('CS00020', 'IS00200'),

                CS00020_IS00202: IFindData = finder.find('CS00020', 'IS00202'),
                CS00020_IS00203: IFindData = finder.find('CS00020', 'IS00203'),
                CS00020_IS00211: IFindData = finder.find('CS00020', 'IS00211'),
                CS00020_IS00212: IFindData = finder.find('CS00020', 'IS00212'),
                CS00020_IS00220: IFindData = finder.find('CS00020', 'IS00220'),
                CS00020_IS00221: IFindData = finder.find('CS00020', 'IS00221'),


                CS00020_IS00128: IFindData = finder.find('CS00020', 'IS00128');

                CS00020_IS00223: IFindData = finder.find('CS00020', 'IS00223'),
                CS00020_IS00224: IFindData = finder.find('CS00020', 'IS00224'),
                CS00020_IS00226: IFindData = finder.find('CS00020', 'IS00226'),
                CS00020_IS00227: IFindData = finder.find('CS00020', 'IS00227');

            if (CS00020_IS00130 || CS00020_IS00131) {

                $(`#${CS00020_IS00130 && CS00020_IS00130.ctrl.attr('id')}, #${CS00020_IS00131 && CS00020_IS00131.ctrl.attr('id')}`)
                    .on('click', () => {
                        let _finder = finder,
                            lstComboBoxValue = CS00020_IS00130.data.lstComboBoxValue,
                            selectedWorkTypeCode = CS00020_IS00130 ? CS00020_IS00130.data.value() || "" : "",
                            selectedWorkTimeCode = CS00020_IS00131 ? CS00020_IS00131.data.value() || "" : "";

                        setShared('parentCodes', {
                            workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                            selectedWorkTypeCode: selectedWorkTypeCode,
                            workTimeCodes: [],
                            selectedWorkTimeCode: selectedWorkTimeCode
                        }, true);

                        modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                            var childData: IChildData = getShared('childData');
                            if (!childData) {
                                CS00020_IS00130.data.value(undefined);
                                CS00020_IS00131.data.value(undefined);
                            } else {
                                if (CS00020_IS00130) {
                                    CS00020_IS00130.data.value(childData.selectedWorkTypeCode);
                                }
                                if (CS00020_IS00131) {
                                    CS00020_IS00131.data.value(childData.selectedWorkTimeCode);
                                }
                                if (childData.first) {
                                    if (CS00020_IS00133) {
                                        CS00020_IS00133.data.value(childData.first.start);
                                    }
                                    if (CS00020_IS00134) {
                                        CS00020_IS00134.data.value(childData.first.end);
                                    }
                                }
                                if (childData.second) {
                                    if (CS00020_IS00136) {
                                        CS00020_IS00136.data.value(childData.second.start);
                                    }
                                    if (CS00020_IS00137) {
                                        CS00020_IS00137.data.value(childData.second.end);
                                    }
                                }
                            }
                        });
                    });
            };
            //BUTTON IS00128
            if (CS00020_IS00128) {
                CS00020_IS00128.ctrl.on('click', () => {
                    let lstComboBoxValue = CS00020_IS00128.data.lstComboBoxValue,
                        selectedWorkTypeCode = CS00020_IS00128 ? CS00020_IS00128.data.value() || "" : "";

                    setShared('parentCodes', {
                        workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                        selectedWorkTypeCode: selectedWorkTypeCode,
                        workTimeCodes: [],
                        selectedWorkTimeCode: ""
                    }, true);
                    modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                        var childData: IChildData = getShared('childData');
                        if (!childData) {
                            CS00020_IS00130.data.value(undefined);
                        } else {
                            if (CS00020_IS00130) {
                                CS00020_IS00130.data.value(childData.selectedWorkTypeCode);
                            }
                        }
                    });

                });

            }
            //BUTTON IS00139 ,IS00140
            if (CS00020_IS00139 || CS00020_IS00140) {

                $(`${CS00020_IS00139 && CS00020_IS00139.ctrl.attr('id')}, ${CS00020_IS00140 && CS00020_IS00140.ctrl.attr('id')}`)
                    .on('click', () => {
                        let _finder = finder,
                            lstComboBoxValue = CS00020_IS00139.data.lstComboBoxValue,
                            selectedWorkTypeCode = CS00020_IS00139 ? CS00020_IS00139.data.value() || "" : "",
                            selectedWorkTimeCode = CS00020_IS00140 ? CS00020_IS00140.data.value() || "" : "";

                        setShared('parentCodes', {
                            workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                            selectedWorkTypeCode: selectedWorkTypeCode,
                            workTimeCodes: [],
                            selectedWorkTimeCode: selectedWorkTimeCode
                        }, true);

                        modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                            var childData: IChildData = getShared('childData');
                            if (!childData) {
                                CS00020_IS00139.data.value(undefined);
                                CS00020_IS00140.data.value(undefined);
                            } else {
                                if (CS00020_IS00139) {
                                    CS00020_IS00139.data.value(childData.selectedWorkTypeCode);
                                }
                                if (CS00020_IS00140) {
                                    CS00020_IS00140.data.value(childData.selectedWorkTimeCode);
                                }
                                if (childData.first) {
                                    if (CS00020_IS00142) {
                                        CS00020_IS00142.data.value(childData.first.start);
                                    }
                                    if (CS00020_IS00143) {
                                        CS00020_IS00143.data.value(childData.first.end);
                                    }
                                }
                                if (childData.second) {
                                    if (CS00020_IS00145) {
                                        CS00020_IS00145.data.value(childData.second.start);
                                    }
                                    if (CS00020_IS00146) {
                                        CS00020_IS00146.data.value(childData.second.end);
                                    }
                                }
                            }
                        });
                    });
            };


            

            if (CS00020_IS00238 || CS00020_IS00239) {

                $(`#${CS00020_IS00238 && CS00020_IS00238.ctrl.attr('id')}, #${CS00020_IS00239 && CS00020_IS00239.ctrl.attr('id')}`)
                    .on('click', () => {
                        let _finder = finder,
                            lstComboBoxValue = CS00020_IS00238.data.lstComboBoxValue,
                            selectedWorkTypeCode = CS00020_IS00238 ? CS00020_IS00238.data.value() || "" : "",
                            selectedWorkTimeCode = CS00020_IS00239 ? CS00020_IS00239.data.value() || "" : "";

                        setShared('parentCodes', {
                            workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                            selectedWorkTypeCode: selectedWorkTypeCode,
                            workTimeCodes: [],
                            selectedWorkTimeCode: selectedWorkTimeCode
                        }, true);

                        modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                            var childData: IChildData = getShared('childData');
                            if (!childData) {
                                CS00020_IS00238.data.value(undefined);
                                CS00020_IS00239.data.value(undefined);
                            } else {
                                if (CS00020_IS00238) {
                                    CS00020_IS00238.data.value(childData.selectedWorkTypeCode);
                                }
                                if (CS00020_IS00239) {
                                    CS00020_IS00239.data.value(childData.selectedWorkTimeCode);
                                }
                                if (childData.first) {
                                    if (CS00020_IS00241) {
                                        CS00020_IS00241.data.value(childData.first.start);
                                    }
                                    if (CS00020_IS00242) {
                                        CS00020_IS00242.data.value(childData.first.end);
                                    }
                                }
                                if (childData.second) {
                                    if (CS00020_IS00244) {
                                        CS00020_IS00244.data.value(childData.second.start);
                                    }
                                    if (CS00020_IS00245) {
                                        CS00020_IS00245.data.value(childData.second.end);
                                    }
                                }
                            }
                        });
                    });
            };
            

            if (CS00020_IS00184 || CS00020_IS00185) {

                $(`#${CS00020_IS00184 && CS00020_IS00184.ctrl.attr('id')}, #${CS00020_IS00185 && CS00020_IS00185.ctrl.attr('id')}`)
                    .on('click', () => {
                        let _finder = finder,
                            lstComboBoxValue = CS00020_IS00184.data.lstComboBoxValue,
                            selectedWorkTypeCode = CS00020_IS00184 ? CS00020_IS00184.data.value() || "" : "",
                            selectedWorkTimeCode = CS00020_IS00185 ? CS00020_IS00185.data.value() || "" : "";

                        setShared('parentCodes', {
                            workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                            selectedWorkTypeCode: selectedWorkTypeCode,
                            workTimeCodes: [],
                            selectedWorkTimeCode: selectedWorkTimeCode
                        }, true);

                        modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                            var childData: IChildData = getShared('childData');
                            if (!childData) {
                                CS00020_IS00184.data.value(undefined);
                                CS00020_IS00185.data.value(undefined);
                            } else {
                                if (CS00020_IS00184) {
                                    CS00020_IS00184.data.value(childData.selectedWorkTypeCode);
                                }
                                if (CS00020_IS00185) {
                                    CS00020_IS00185.data.value(childData.selectedWorkTimeCode);
                                }
                                if (childData.first) {
                                    if (CS00020_IS00187) {
                                        CS00020_IS00187.data.value(childData.first.start);
                                    }
                                    if (CS00020_IS00188) {
                                        CS00020_IS00188.data.value(childData.first.end);
                                    }
                                }
                                if (childData.second) {
                                    if (CS00020_IS00190) {
                                        CS00020_IS00190.data.value(childData.second.start);
                                    }
                                    if (CS00020_IS00191) {
                                        CS00020_IS00191.data.value(childData.second.end);
                                    }
                                }
                            }
                        });
                    });
            };
        

          if (CS00020_IS00220 || CS00020_IS00221) {

                $(`#${CS00020_IS00220 && CS00020_IS00220.ctrl.attr('id')}, #${CS00020_IS00221 && CS00020_IS00221.ctrl.attr('id')}`)
                    .on('click', () => {
                        let _finder = finder,
                            lstComboBoxValue = CS00020_IS00220.data.lstComboBoxValue,
                            selectedWorkTypeCode = CS00020_IS00220 ? CS00020_IS00220.data.value() || "" : "",
                            selectedWorkTimeCode = CS00020_IS00221 ? CS00020_IS00221.data.value() || "" : "";

                        setShared('parentCodes', {
                            workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                            selectedWorkTypeCode: selectedWorkTypeCode,
                            workTimeCodes: [],
                            selectedWorkTimeCode: selectedWorkTimeCode
                        }, true);

                        modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                            var childData: IChildData = getShared('childData');
                            if (!childData) {
                                CS00020_IS00220.data.value(undefined);
                                CS00020_IS00221.data.value(undefined);
                            } else {
                                if (CS00020_IS00220) {
                                    CS00020_IS00220.data.value(childData.selectedWorkTypeCode);
                                }
                                if (CS00020_IS00221) {
                                    CS00020_IS00221.data.value(childData.selectedWorkTimeCode);
                                }
                                if (childData.first) {
                                    if (CS00020_IS00223) {
                                        CS00020_IS00223.data.value(childData.first.start);
                                    }
                                    if (CS00020_IS00224) {
                                        CS00020_IS00224.data.value(childData.first.end);
                                    }
                                }
                                if (childData.second) {
                                    if (CS00020_IS00226) {
                                        CS00020_IS00226.data.value(childData.second.start);
                                    }
                                    if (CS00020_IS00227) {
                                        CS00020_IS00227.data.value(childData.second.end);
                                    }
                                }
                            }
                        });
                    });
            };


            if (CS00020_IS00148 || CS00020_IS00149) {

                $(`#${CS00020_IS00148 && CS00020_IS00148.ctrl.attr('id')}, #${CS00020_IS00149 && CS00020_IS00149.ctrl.attr('id')}`)
                    .on('click', () => {
                        let _finder = finder,
                            lstComboBoxValue = CS00020_IS00148.data.lstComboBoxValue,
                            selectedWorkTypeCode = CS00020_IS00148 ? CS00020_IS00148.data.value() || "" : "",
                            selectedWorkTimeCode = CS00020_IS00149 ? CS00020_IS00149.data.value() || "" : "";

                        setShared('parentCodes', {
                            workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                            selectedWorkTypeCode: selectedWorkTypeCode,
                            workTimeCodes: [],
                            selectedWorkTimeCode: selectedWorkTimeCode
                        }, true);

                        modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                            var childData: IChildData = getShared('childData');
                            if (!childData) {
                                CS00020_IS00148.data.value(undefined);
                                CS00020_IS00149.data.value(undefined);
                            } else {
                                if (CS00020_IS00148) {
                                    CS00020_IS00148.data.value(childData.selectedWorkTypeCode);
                                }
                                if (CS00020_IS00149) {
                                    CS00020_IS00149.data.value(childData.selectedWorkTimeCode);
                                }
                                if (childData.first) {
                                    if (CS00020_IS00151) {
                                        CS00020_IS00151.data.value(childData.first.start);
                                    }
                                    if (CS00020_IS00152) {
                                        CS00020_IS00152.data.value(childData.first.end);
                                    }
                                }
                                if (childData.second) {
                                    if (CS00020_IS00154) {
                                        CS00020_IS00154.data.value(childData.second.start);
                                    }
                                    if (CS00020_IS00155) {
                                        CS00020_IS00155.data.value(childData.second.end);
                                    }
                                }
                            }
                        });
                    });
            };



            if (CS00020_IS00193 || CS00020_IS00194) {

                $(`#${CS00020_IS00193 && CS00020_IS00193.ctrl.attr('id')}, #${CS00020_IS00194 && CS00020_IS00194.ctrl.attr('id')}`)
                    .on('click', () => {
                        let _finder = finder,
                            lstComboBoxValue = CS00020_IS00193.data.lstComboBoxValue,
                            selectedWorkTypeCode = CS00020_IS00193 ? CS00020_IS00193.data.value() || "" : "",
                            selectedWorkTimeCode = CS00020_IS00194 ? CS00020_IS00194.data.value() || "" : "";

                        setShared('parentCodes', {
                            workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                            selectedWorkTypeCode: selectedWorkTypeCode,
                            workTimeCodes: [],
                            selectedWorkTimeCode: selectedWorkTimeCode
                        }, true);

                        modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                            var childData: IChildData = getShared('childData');
                            if (!childData) {
                                CS00020_IS00193.data.value(undefined);
                                CS00020_IS00194.data.value(undefined);
                            } else {
                                if (CS00020_IS00193) {
                                    CS00020_IS00193.data.value(childData.selectedWorkTypeCode);
                                }
                                if (CS00020_IS00194) {
                                    CS00020_IS00194.data.value(childData.selectedWorkTimeCode);
                                }
                                if (childData.first) {
                                    if (CS00020_IS00196) {
                                        CS00020_IS00196.data.value(childData.first.start);
                                    }
                                    if (CS00020_IS00197) {
                                        CS00020_IS00197.data.value(childData.first.end);
                                    }
                                }
                                if (childData.second) {
                                    if (CS00020_IS00199) {
                                        CS00020_IS00199.data.value(childData.second.start);
                                    }
                                    if (CS00020_IS00200) {
                                        CS00020_IS00200.data.value(childData.second.end);
                                    }
                                }
                            }
                        });
                    });
            };


            if (CS00020_IS00202) {
                CS00020_IS00202.ctrl.on('click', () => {
                    let _finder = finder,
                        lstComboBoxValue = CS00020_IS00202.data.lstComboBoxValue,
                        selectedWorkTypeCode = CS00020_IS00202.data.value() || "";

                    setShared('parentCodes', {
                        workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                        selectedWorkTypeCode: selectedWorkTypeCode,
                        workTimeCodes: "",
                        selectedWorkTimeCode: ""
                    }, true);

                    modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                        var childData: IChildData = getShared('childData');
                        if (!childData) {
                            CS00020_IS00202.data.value(undefined);
                        } else {
                            CS00020_IS00202.data.value(childData.selectedWorkTypeCode);
                        }
                    });
                });
            }

            if (CS00020_IS00203) {
                CS00020_IS00203.ctrl.on('click', () => {
                    let _finder = finder,
                        lstComboBoxValue = CS00020_IS00203.data.lstComboBoxValue,
                        selectedWorkTypeCode = CS00020_IS00203.data.value() || "";

                    setShared('parentCodes', {
                        workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                        selectedWorkTypeCode: selectedWorkTypeCode,
                        workTimeCodes: "",
                        selectedWorkTimeCode: ""
                    }, true);

                    modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                        var childData: IChildData = getShared('childData');
                        if (!childData) {
                            CS00020_IS00203.data.value(undefined);
                        } else {
                            CS00020_IS00203.data.value(childData.selectedWorkTypeCode);
                        }
                    });
                });
            }

            if (CS00020_IS00211) {
                CS00020_IS00211.ctrl.on('click', () => {
                    let _finder = finder,
                        lstComboBoxValue = CS00020_IS00211.data.lstComboBoxValue,
                        selectedWorkTypeCode = CS00020_IS00211.data.value() || "";

                    setShared('parentCodes', {
                        workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                        selectedWorkTypeCode: selectedWorkTypeCode,
                        workTimeCodes: "",
                        selectedWorkTimeCode: ""
                    }, true);

                    modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                        var childData: IChildData = getShared('childData');
                        if (!childData) {
                            CS00020_IS00211.data.value(undefined);
                        } else {
                            CS00020_IS00211.data.value(childData.selectedWorkTypeCode);
                        }
                    });
                });
            }

            if (CS00020_IS00212) {
                CS00020_IS00212.ctrl.on('click', () => {
                    let _finder = finder,
                        lstComboBoxValue = CS00020_IS00212.data.lstComboBoxValue,
                        selectedWorkTypeCode = CS00020_IS00212.data.value() || "";

                    setShared('parentCodes', {
                        workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                        selectedWorkTypeCode: selectedWorkTypeCode,
                        workTimeCodes: "",
                        selectedWorkTimeCode: ""
                    }, true);

                    modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                        var childData: IChildData = getShared('childData');
                        if (!childData) {
                            CS00020_IS00212.data.value(undefined);
                        } else {
                            CS00020_IS00212.data.value(childData.selectedWorkTypeCode);
                        }
                    });
                });
            }


            if (CS00020_IS00157) {
                CS00020_IS00157.ctrl.on('click', () => {
                    let _finder = finder,
                        lstComboBoxValue = CS00020_IS00157.data.lstComboBoxValue,
                        selectedWorkTypeCode = CS00020_IS00157.data.value() || "";

                    setShared('parentCodes', {
                        workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                        selectedWorkTypeCode: selectedWorkTypeCode,
                        workTimeCodes: "",
                        selectedWorkTimeCode: ""
                    }, true);

                    modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                        var childData: IChildData = getShared('childData');
                        if (!childData) {
                            CS00020_IS00157.data.value(undefined);
                        } else {
                            CS00020_IS00157.data.value(childData.selectedWorkTypeCode);
                        }
                    });
                });
            }
            if (CS00020_IS00158) {
                CS00020_IS00158.ctrl.on('click', () => {
                    let _finder = finder,
                        lstComboBoxValue = CS00020_IS00158.data.lstComboBoxValue,
                        selectedWorkTypeCode = CS00020_IS00158.data.value() || "";

                    setShared('parentCodes', {
                        workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                        selectedWorkTypeCode: selectedWorkTypeCode,
                        workTimeCodes: "",
                        selectedWorkTimeCode: ""
                    }, true);

                    modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                        var childData: IChildData = getShared('childData');
                        if (!childData) {
                            CS00020_IS00158.data.value(undefined);
                        } else {
                            CS00020_IS00158.data.value(childData.selectedWorkTypeCode);
                        }
                    });
                });
            }

            if (CS00020_IS00166) {
                CS00020_IS00166.ctrl.on('click', () => {
                    let _finder = finder,
                        lstComboBoxValue = CS00020_IS00166.data.lstComboBoxValue,
                        selectedWorkTypeCode = CS00020_IS00166.data.value() || "";

                    setShared('parentCodes', {
                        workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                        selectedWorkTypeCode: selectedWorkTypeCode,
                        workTimeCodes: "",
                        selectedWorkTimeCode: ""
                    }, true);

                    modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                        var childData: IChildData = getShared('childData');
                        if (!childData) {
                            CS00020_IS00166.data.value(undefined);
                        } else {
                            CS00020_IS00166.data.value(childData.selectedWorkTypeCode);
                        }
                    });
                });
            }
            if (CS00020_IS00167) {
                CS00020_IS00167.ctrl.on('click', () => {
                    let _finder = finder,
                        lstComboBoxValue = CS00020_IS00167.data.lstComboBoxValue,
                        selectedWorkTypeCode = CS00020_IS00167.data.value() || "";

                    setShared('parentCodes', {
                        workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                        selectedWorkTypeCode: selectedWorkTypeCode,
                        workTimeCodes: "",
                        selectedWorkTimeCode: ""
                    }, true);

                    modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                        var childData: IChildData = getShared('childData');
                        if (!childData) {
                            CS00020_IS00167.data.value(undefined);
                        } else {
                            CS00020_IS00167.data.value(childData.selectedWorkTypeCode);
                        }
                    });
                });
            }

            if (CS00020_IS00175) {
                CS00020_IS00175.ctrl.on('click', () => {
                    let _finder = finder,
                        lstComboBoxValue = CS00020_IS00175.data.lstComboBoxValue,
                        selectedWorkTypeCode = CS00020_IS00175.data.value() || "";

                    setShared('parentCodes', {
                        workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                        selectedWorkTypeCode: selectedWorkTypeCode,
                        workTimeCodes: "",
                        selectedWorkTimeCode: ""
                    }, true);

                    modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                        var childData: IChildData = getShared('childData');
                        if (!childData) {
                            CS00020_IS00175.data.value(undefined);
                        } else {
                            CS00020_IS00175.data.value(childData.selectedWorkTypeCode);
                        }
                    });
                });
            }
            if (CS00020_IS00176) {
                CS00020_IS00176.ctrl.on('click', () => {
                    let _finder = finder,
                        lstComboBoxValue = CS00020_IS00176.data.lstComboBoxValue,
                        selectedWorkTypeCode = CS00020_IS00176.data.value() || "";

                    setShared('parentCodes', {
                        workTypeCodes: _.map(lstComboBoxValue, x => x.optionValue),
                        selectedWorkTypeCode: selectedWorkTypeCode,
                        workTimeCodes: "",
                        selectedWorkTimeCode: ""
                    }, true);

                    modal('at', '/view/kdl/003/a/index.xhtml').onClosed(() => {
                        var childData: IChildData = getShared('childData');
                        if (!childData) {
                            CS00020_IS00176.data.value(undefined);
                        } else {
                            CS00020_IS00176.data.value(childData.selectedWorkTypeCode);
                        }
                    });
                });
            }


        };

        combobox = () => {
        };
    }


    // define ITEM_CLASSIFICATION_TYPE
    enum IT_CLA_TYPE {
        ITEM = <any>"ITEM", // single item
        LIST = <any>"LIST", // list item
        SPER = <any>"SeparatorLine" // line item
    }

    interface IValidation {
        radio: () => void;
        button: () => void;
        combobox: () => void;
    }

    interface IFinder {
        find: (categoryCode: string, subscribeCode: string) => IFindData;
        finds: (categoryCode: string, subscribesCode: Array<string>) => Array<IFindData>;
        findChilds: (categoryCode: string, parentCode: string) => Array<IFindData>;
    }

    interface IFindData {
        ctrl: JQuery;
        data: IItemData
    }

    interface IItemData {
        value: KnockoutObservable<any>;
        editable: KnockoutObservable<boolean>;
        readonly: KnockoutObservable<boolean>;
        categoryCode: string;
        itemCode: string;
        lstComboBoxValue: Array<any>;
        itemParentCode?: string;
    }

    interface IComboboxItem {
        optionText: string;
        optionValue: string;
    }

    interface IParentCodes {
        workTypeCodes: string;
        selectedWorkTypeCode: string;
        workTimeCodes: string;
        selectedWorkTimeCode: string
    }

    interface IChildData {
        selectedWorkTypeCode: string;
        selectedWorkTypeName: string;
        selectedWorkTimeCode: string;
        selectedWorkTimeName: string;
        first: IDateRange;
        second: IDateRange;
    }

    interface IDateRange {
        start: number;
        end: number;
    }

    interface ICheckParam {
        workTimeCode?: string;
    }
} 