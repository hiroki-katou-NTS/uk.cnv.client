<template>
  <div>
    <div>
      <!----><!---->
      <div
        role=""
        class="table-container mx-n3 mt-n2"
        style="margin-top: -8px !important"
      >
        <div class="table-header" style="height: 35px">
          <table class="table table-bordered m-0 table-sm table-custom">
            <tr
              class="uk-bg-green"
              style="
                border-bottom-width: 1px !important;
                border-bottom: 1px;
                border-color: #e0f59e !important;
                height: 32px;
                width: 100%;
              "
            >
              <td style="width: 35%; border: 0px solid #dee2e6 !important">
                <div>
                  <i
                    v-click="openPrevMonth"
                    class="fas fa-arrow-alt-circle-left"
                  ></i>
                </div>
              </td>
              <td style="border: 0px solid #dee2e6 !important">
                <div style="margin-top: -4px; width: 127px">
                  <nts-year-month  id="yearmonth-cmm"
                    v-bind:showTitle="false"
                    v-model="yearMonth"
                    name="対象月"
                  />
                </div>
              </td>
              <td style="width: 35%; border: 0px solid #dee2e6 !important">
                <div style="float: right">
                  <i
                    v-click="openNextMonth"
                    class="fas fa-arrow-alt-circle-right"
                  ></i>
                </div>
              </td>
            </tr>
          </table>
        </div>
        <div
          class="table-body"
          style="
            margin-bottom: -1px;
            margin-top: -1px;
            height: auto;
            overflow: hidden;
          "
        >
          <div style="text-align: right"><!----><!----><!----></div>
          <table id="table-data" class="table table-bordered m-0 table-sm table-custom">
            <tbody>
              <tr style="">
                <td
                  class="uk-bg-schedule-sunday"
                  style="width: 51px; height: 18px;text-align: center; color:#FF0000;font-weight: bold;font-size: 2vh;"
                >
                  {{'KSUS02_3' | i18n}}
                </td>
                <td class="uk-bg-disable" style="width: 51px; height: 18px;text-align: center; color:#404040;font-weight: bold;font-size: 2vh;">
                  {{'KSUS02_4' | i18n}}
                </td>
                <td class="uk-bg-disable" style="width: 51px;text-align: center; color:#404040;font-weight: bold;font-size: 2vh;">{{'KSUS02_5' | i18n}}</td>
                <td class="uk-bg-disable" style="width: 51px;text-align: center; color:#404040;font-weight: bold;font-size: 2vh;">{{'KSUS02_6' | i18n}}</td>
                <td class="uk-bg-disable" style="width: 51px;text-align: center; color:#404040;font-weight: bold;font-size: 2vh;">{{'KSUS02_7' | i18n}}</td>
                <td class="uk-bg-disable" style="width: 51px;text-align: center; color:#404040;font-weight: bold;font-size: 2vh;">{{'KSUS02_8' | i18n}}</td>
                <td
                  id="d-1"
                  class="uk-bg-schedule-saturday"
                  style="width: 51px;text-align: center; color:#0000FF;font-weight: bold;font-size: 2vh;"
                >
                  {{'KSUS02_9' | i18n}}
                </td>
              </tr>

              <tr>
                <td
                  v-for="(item, index) in listDataDisplay.slice(0, 7)"
                  v-bind:key="index"
                  v-on="{ click: item.disable || !item.canUpdateCell? () =>{}: cellFocus }"
                  :id="item.id"
                  :class="item.disable ? 'uk-bg-white-smoke w-51' : !item.canUpdateCell || !isCurrentMonth ? 'uk-bg-silver w-51': 'w-51'"
                >
                  <div
                    v-if="item.disable == true"
                    class="uk-bg-white-smoke cell-data"
                  ></div>
                  <div v-else class="cell-data">
                    <div>
                      <span v-html="item.dateDisplay"></span>
                      <span v-if="item.showMemo == true"
                        ><i class="far fa-sticky-note memo-css"></i
                      ></span>
                    </div>
                    <div style="text-align: center">
                      <span
                        v-if="item.canUpdateCell &&
                          item.workAvailabilityOfOneDayDto.workAvaiByHolidayDto
                            .assignmentMethod == 0
                        "
                        class="form-control select-el cell-style"
                        >{{'KSUS02_18' | i18n}}</span
                      >
                    </div>
                  </div>
                </td>
              </tr>
              <tr class="swipeCalendar">
                <td
                  v-for="(item, index) in listDataDisplay.slice(7, 14)"
                  v-bind:key="index"
                  v-on="{ click: item.disable || !item.canUpdateCell? () =>{}: cellFocus }"
                  :id="item.id"
                  :class="item.disable ? 'uk-bg-white-smoke w-51' : !item.canUpdateCell || !isCurrentMonth ? 'uk-bg-silver w-51': 'w-51'"
                >
                  <div
                    v-if="item.disable == true"
                    class="uk-bg-white-smoke cell-data"
                  ></div>
                  <div v-else class="cell-data">
                    <div>
                      <span v-html="item.dateDisplay"></span>
                      <span v-if="item.showMemo == true"
                        ><i class="far fa-sticky-note memo-css"></i
                      ></span>
                    </div>
                    <div style="text-align: center">
                      <span 
                        v-if=" item.canUpdateCell &&
                          item.workAvailabilityOfOneDayDto.workAvaiByHolidayDto
                            .assignmentMethod == 0
                        "
                        class="form-control select-el cell-style"
                        >{{'KSUS02_18' | i18n}}</span
                      >
                    </div>
                  </div>
                </td>
              </tr>
              <tr class="swipeCalendar">
                <td 
                  v-for="(item, index) in listDataDisplay.slice(14, 21)"
                  v-bind:key="index"
                  v-on="{ click: item.disable || !item.canUpdateCell? () =>{}: cellFocus }"
                  :id="item.id"
                  :class="item.disable ? 'uk-bg-white-smoke w-51' : !item.canUpdateCell || !isCurrentMonth ? 'uk-bg-silver w-51': 'w-51'"
                >
                  <div
                    v-if="item.disable == true"
                    class="uk-bg-white-smoke cell-data"
                  ></div>
                  <div v-else class="cell-data">
                    <div>
                      <span v-html="item.dateDisplay"></span>
                      <span v-if="item.showMemo == true"
                        ><i class="far fa-sticky-note memo-css"></i
                      ></span>
                    </div>
                    <div style="text-align: center">
                      <span
                        v-if=" item.canUpdateCell &&
                          item.workAvailabilityOfOneDayDto.workAvaiByHolidayDto
                            .assignmentMethod == 0
                        "
                        class="form-control select-el cell-style"
                        >{{'KSUS02_18' | i18n}}</span
                      >
                    </div>
                  </div>
                </td>
              </tr>
              <tr class="swipeCalendar">
                <td
                  v-for="(item, index) in listDataDisplay.slice(21, 28)"
                  v-bind:key="index"
                  v-on="{ click: item.disable || !item.canUpdateCell? () =>{}: cellFocus }"
                  :id="item.id"
                  :class="item.disable ? 'uk-bg-white-smoke w-51' : !item.canUpdateCell || !isCurrentMonth ? 'uk-bg-silver w-51': 'w-51'"
                >
                  <div
                    v-if="item.disable == true"
                    class="uk-bg-white-smoke cell-data"
                  ></div>
                  <div v-else class="cell-data">
                    <div>
                      <span v-html="item.dateDisplay"></span>
                      <span v-if="item.showMemo == true"
                        ><i class="far fa-sticky-note memo-css"></i
                      ></span>
                    </div>
                    <div style="text-align: center">
                      <span
                        v-if="  item.canUpdateCell &&
                          item.workAvailabilityOfOneDayDto.workAvaiByHolidayDto
                            .assignmentMethod == 0
                        "
                        class="form-control select-el cell-style"
                        >{{'KSUS02_18' | i18n}}</span
                      >
                    </div>
                  </div>
                </td>
              </tr>
              <tr class="swipeCalendar">
                <td
                  v-for="(item, index) in listDataDisplay.slice(28, 35)"
                  v-bind:key="index"
                  v-on="{ click: item.disable || !item.canUpdateCell? () =>{}: cellFocus }"
                  :id="item.id"
                  :class="item.disable ? 'uk-bg-white-smoke w-51' : !item.canUpdateCell || !isCurrentMonth ? 'uk-bg-silver w-51': 'w-51'"
                >
                  <div
                    v-if="item.disable == true"
                    class="uk-bg-white-smoke cell-data"
                  ></div>
                  <div v-else class="cell-data">
                    <div>
                      <span v-html="item.dateDisplay"></span>
                      <span v-if="item.showMemo == true"
                        ><i class="far fa-sticky-note memo-css"></i
                      ></span>
                    </div>
                    <div style="text-align: center">
                      <span
                        v-if="  item.canUpdateCell &&
                          item.workAvailabilityOfOneDayDto.workAvaiByHolidayDto
                            .assignmentMethod == 0
                        "
                        class="form-control select-el cell-style"
                        >{{'KSUS02_18' | i18n}}</span
                      >
                    </div>
                  </div>
                </td>
              </tr>
              <tr class="swipeCalendar">
                <td style="border-bottom: 2px solid #dee2e6;"
                  v-for="(item, index) in listDataDisplay.slice(35, 42)"
                  v-bind:key="index"
                  v-on="{ click: item.disable || !item.canUpdateCell? () =>{}: cellFocus }"
                  :id="item.id"
                  :class="item.disable ? 'uk-bg-white-smoke w-51' : !item.canUpdateCell || !isCurrentMonth ? 'uk-bg-silver w-51': 'w-51'"
                >
                  <div
                    v-if="item.disable == true"
                    class="uk-bg-white-smoke cell-data"
                  ></div>
                  <div v-else class="cell-data">
                    <div>
                      <span v-html="item.dateDisplay"></span>
                      <span v-if="item.showMemo == true"
                        ><i class="far fa-sticky-note memo-css"></i
                      ></span>
                    </div>
                    <div style="text-align: center">
                      <span
                        v-if="  item.canUpdateCell &&
                          item.workAvailabilityOfOneDayDto.workAvaiByHolidayDto
                            .assignmentMethod == 0
                        "
                        class="form-control select-el cell-style"
                        >{{'KSUS02_18' | i18n}}</span
                      >
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div
          style="
            position: absolute;
            top: 121px;
            font-size: 12px;
            float: left;
            font-weight: bold;
          "
        ></div>
      </div>

    </div>

    <div class="hidden-popup" style="position: absolute; top: 0">
      <span
        class="form-control holiday"
        style="
          font-size: 17px;
          width: 52px;
          height: 28px;
          color: red;
          background-color: #fabf8f;
          padding: 0;
          margin: auto;
          margin-top: 5px;
          font-weight: bold;
          border: 1px solid #ced4da;
          text-align: center;
          float: none;
        "
        >休日</span
      >
    </div>

    <div v-show="showPopup"
      class="nts-help-button-image"
      style="background-color: pink"
    >
      <ul class="list-group" style="color: white">
        <li
          v-click="memoInput"
          class="list-group-item"
          style="background-color: pink; padding: 9px"
        >
          <i class="fas fa-sticky-note" style="margin-right: 5px"
            >{{'KSUS02_13' | i18n}}</i
          >
        </li>
        <li
          v-click="clearAll"
          class="list-group-item"
          style="background-color: pink; padding: 9px"
        >
          <i class="fas fa-trash-alt" style="margin-right: 5px">{{'KSUS02_14' | i18n}}</i>
        </li>
      </ul>
      <span v-show="isCaretLeft" class="caret-helpbutton caret-bottom"></span>
      <span v-show="endRight" class="caret-helpbutton caret-bottom1"></span>
      <span v-show="endLeft" class="caret-helpbutton caret-bottom2"></span>
    </div>
    <div class="modal-confirm" v-show="showConfirmDialog">
      <div class="modal show">
        <div
          class="modal-dialog modal-md modal-popup modal-info modal-dialog-centered modal-danger type-confirm"
        >
          <div class="modal-content uk-bg-silver">
            <h5
              class="card-header"
              style="padding: 5px; background-color: rgba(117, 202, 235, 0.33)"
              >
              <span id="monthday" style="font-weight: bold; margin-left: 6px"
                ></span
              >
              </h5
            >
            <div
              style="
                padding: 6px;
                background-color: rgba(117, 202, 235, 0.08);
                height: 186px;
              "
              class="modal-body"
            >
              <div class="card2 card show" style="height: 166px;" >
                <div class="card-header uk-bg-accordion">
                  <button
                    style="color: black"
                    class="btn btn-link"
                    type="button"
                  >
                    <i class="far fa-sticky-note"></i
                    ><span class="small-header" style="margin-left: 9px"
                      >{{'KSUS02_11' | i18n}}</span
                    >
                  </button>
                </div>
                <div>
                  <div class="card-body">
                      <div v-if="isCurrentMonth == true" >
                      <nts-text-area
                        id="text-area-1"
                        v-model="memoCurent"
                        rows="5"
                        v-bind:placeholder="$i18n('KSUS02_15')"
                        class="form-control"
                        style="height: 120px;margin: 0px;padding: 0px;"
                      />
                      </div>
                      <div v-else>
                        <nts-text-area
                        disabled
                        id="text-area-1"
                        v-model="memoCurent"
                        rows="5"
                        v-bind:placeholder="$i18n('KSUS02_15')"
                        class="form-control"
                        style="height: 120px;margin: 0px;padding: 0px;"
                      />
                      </div>
                      
                  </div>
                </div>
              </div>
            </div>
            <div v-if="isCurrentMonth == true" class="modal-footer text-right" style="">
              <button v-click="setMemo" class="btn btn-link" style="">
                {{'KSUS02_16' | i18n}}
              </button>
              <button v-click="closePopup" class="btn btn-link text-secondary">
                {{'KSUS02_17' | i18n}}
              </button>
            </div>
            <div v-else class="modal-footer text-right" style="">
              <button v-click="closePopup" class="btn btn-link text-secondary">
                {{'KSUS02_21' | i18n}}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>