<template>
<div class="ksus01a">
    <div class="card card-label">
        <!-- A1_1 -->
        <div class="card-header uk-bg-green" style="height: 6.5vh;">
            <div id="year-month-component" class="flex-center-vert" style="border-color: #E0F59E;">
                <!-- A1_1_1 -->
                <span v-on:click="changeYearMonth(false)">
                    <i style="padding-top: 1vw;" class="fas fa-arrow-alt-circle-left large-icon"></i>
                </span>
                <!-- A1_1_2 -->
                <nts-year-month
                    v-bind:showTitle="false"
                    v-model="yearMonth"
                    name="対象月"
                    v-on:input="watchYearMonth"
                />
                <!-- A1_1_3 -->
                <span v-on:click="changeYearMonth(true)">
                    <i style="padding-top: 1vw;" class="fas fa-arrow-alt-circle-right large-icon"></i>
                </span>
            </div>
            <div class="flex-center-vert">
                <!-- A1_1_4 -->
                <span 
                    id="backCurrentMonth"
                    v-on:click="backCurrentMonth()"
                    class="uk-bg-green"
                >
                    {{ "KSUS01_2" | i18n }}
                    <i class="fas fa-undo" style="margin-left: 2vw;"></i>
                </span>
                <!-- A1_1_5 -->
                <span v-on:click="openKSUS01B()">
                    <i style="padding-top: 1vw;" class="fas fa-info-circle large-icon"></i>
                </span>
            </div>
        </div>

        <!-- Calendar -->
        <div id="calendar" class="card-body">
            <div class="date-header-container">
                <!-- A1_2 -->
                <div
                    v-for="(item, index) in dateHeaderList"
                    v-bind:key="index"
                    :value="index"
                    v-bind:class="setHeaderColor(item.weekDayIndex)"
                >
                    <!-- A1_2_1 -->
                    <div>{{item.headerName}}</div>
                </div>
            </div>

            <div class="date-cell-container">
                <!-- A1_3 -->
                <div
                    v-for="(item, index) in dateCellList"
                    v-bind:key="index"
                    :value="index"
                    v-bind:class="setCellColor(item)"
                    v-on:click="showDetail(true, item)"
                    v-on:touchstart="handleTouchStart"
                    v-on:touchend="handleTouchEnd"
                    v-show="rowFocus == null || rowFocus == item.rowNumber"
                >
                    <div class="date-cell-content">
                        <div class="date-cell-top">
                            <!-- A1_3_1 -->
                            <span v-if="today == item.date" class="uk-bg-schedule-that-day" style="border-radius: 50%; height: 4vw; min-width: 3vw; padding-right: 0.25vw">
                                {{item.formatedDate}}
                            </span>
                            <span v-else>{{item.formatedDate}}</span>
                            <!-- A1_3_2 -->
                            <span v-show="workDesiredOpAtr && item.displayData.workDesireStatus != undefined && item.displayData.workDesireStatus != 0">
                                <i class="far fa-star uk-text-holiday" v-if="item.displayData.workDesireStatus == 1"></i>
                                <i class="far fa-star uk-text-attendance" v-else></i>
                            </span>
                        </div>
                        <!-- A1_3_3 -->
                        <div v-show="item.isActive" style="text-align: center; margin: 3.5vh 0">
                            <span v-if="item.displayData.workScheduleAtr >= 1 && item.displayData.workScheduleAtr <= 3 && item.displayData.workScheduleName.length > 0" 
                                v-bind:style="item.workScheduleStyle + 'width: 100%; padding: 0.25em 0;'"
                            >
                                <!-- <span class="uk-text-holiday" v-if="item.displayData.workScheduleAtr == 0">{{item.displayData.workScheduleName}}</span> -->
                                <span class="uk-text-half-day-work" v-if="item.displayData.workScheduleAtr == 1 || item.displayData.workScheduleAtr == 2">{{item.displayData.workScheduleName}}</span>
                                <span class="uk-text-attendance" v-else-if="item.displayData.workScheduleAtr == 3">{{item.displayData.workScheduleName}}</span>
                            </span>
                            <span v-else-if="item.displayData.workScheduleAtr >= 0 && item.displayData.workScheduleAtr <= 3 && (!item.displayData.workScheduleName || item.displayData.workScheduleName.length <= 0)" class="uk-bg-gray" v-bind:style="item.workScheduleStyle + 'width: 100%; padding: 0.25em 0;'">
                                <span style='color: black;'>{{ "KSUS01_21" | i18n }}</span>
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- A3 -->
    <div id="detail-component" class="card card-label" v-show="isDetailShow">
        <!-- A3_1 -->
        <div class="card-header uk-bg-choice-row" v-on:click="showDetail(false)">
            <span style="font-weight: bold;">{{detailCell && detailCell.formatedLongMdwDate}}</span>
            <i class="fas fa-sort-down large-icon"></i>
            <!-- an invisible span to center the icon with flex -->
            <span style="font-weight: bold; visibility: hidden">{{detailCell && detailCell.formatedLongMdwDate}}</span>
        </div>
        
        <div class="card-body" >
            <div class="detail-spacing">
                <!-- A4_1 -->
                <span><i class="far fa-calendar-alt"></i></span>
                <!-- A4_2 -->
                <span style="color: #999">{{ "KSUS01_10" | i18n }}</span>
            </div>
            <div class="detail-spacing detail-indent">
                <!-- A4_3 -->
                <span v-if="detailCell && detailCell.displayData.workScheduleName && detailCell.displayData.workScheduleName.length > 0" 
                    v-bind:style="detailCell && detailCell.workScheduleStyle + ' font-size: 4.5vw;'">
                    <span style="margin: 0;" class="uk-text-holiday" v-if="detailCell && (detailCell.displayData.workScheduleAtr == 0 || detailCell.displayData.workScheduleAtr == undefined)">
                        {{detailCell && detailCell.displayData.workScheduleName}}
                    </span>
                    <span style="margin: 0;" class="uk-text-half-day-work" v-if="detailCell && (detailCell.displayData.workScheduleAtr == 1 || detailCell.displayData.workScheduleAtr == 2)">
                        {{detailCell && detailCell.displayData.workScheduleName}}
                    </span>
                    <span style="margin: 0;" class="uk-text-attendance" v-if="detailCell && detailCell.displayData.workScheduleAtr == 3">
                        {{detailCell && detailCell.displayData.workScheduleName}}
                    </span>
                </span>
                <span v-else class="uk-bg-gray" v-bind:style="detailCell && detailCell.workScheduleStyle + ' font-size: 4.5vw;'">
                    <span style='margin: 0; color: black;'>{{ "KSUS01_21" | i18n }}</span>
                </span>
                <!-- A4_4 A4_5-->
                <span 
                    v-for="(item, index) in detailCell && detailCell.displayData.workScheduleTimeZone" 
                    v-bind:key="index"
                    :value="index"
                    style="font-weight: bold;"
                >
                    {{item.attendanceStamp}}～{{item.leaveStamp}}
                </span>
            </div>
            <div class="detail-spacing" v-show="detailCell && (detailCell.displayData.workScheduleAtr != undefined && detailCell.displayData.workScheduleAtr != 0)">
                <!-- A4_6 -->
                <span><i class="fa fa-user-alt"></i></span>
                <!-- A4_7 -->
                <span style="color: #999">{{ "KSUS01_11" | i18n }}</span>
            </div>
            <div class="detail-spacing detail-indent" v-show="detailCell && (detailCell.displayData.workScheduleAtr != undefined && detailCell.displayData.workScheduleAtr != 0)">
                <!-- A4_8 -->
                <span style="font-weight: bold;">
                    {{detailCell && detailCell.displayData.otherStaffs}}
                </span>
            </div>
            <!-- A4_9 -->
            <hr/>
            <div class="detail-spacing">
                <!-- A4_10 -->
                <span><i class="far fa-star"></i></span>
                <!-- A4_11 -->
                <span style="color: #999">{{ "KSUS01_12" | i18n }}</span>
            </div>
            <div v-if="workDesireInputMode == null" class="detail-spacing detail-indent">
                <div>
                    <span style="font-weight: bold;">{{ "KSUS01_22" | i18n }}</span>
                </div>
            </div>
            <!-- <div v-else-if="detailCell && detailCell.displayData.listWorkDesire.length == 0" class="detail-spacing detail-indent">
                <div>
                    <span style="font-weight: bold;">{{ "KSUS01_22" | i18n }}</span>
                </div>
            </div> -->
            <div v-else-if="workDesireInputMode == 0">
                <div class="detail-spacing detail-indent" >
                    <span
                        style="padding: 0.25em 1em; font-weight: bold; border-radius: 0.25rem; display: inline-block;"
                        class="uk-bg-schedule-sunday">
                        <span style="margin: 0" class="uk-text-red">
                            {{ "KSUS01_14" | i18n }}
                        </span>
                    </span>
                </div>
            </div>
            <div v-else-if="workDesireInputMode == 1">
                <div class="detail-spacing detail-indent" 
                    v-for="(item, index) in detailCell && detailCell.displayData.listWorkDesire" 
                    v-bind:key="index"
                    :value="index"
                >
                    <div>
                        <!-- A4_12 -->
                        <span v-bind:style="item.workDesireStyle">
                            <span style="margin: 0" class="uk-text-holiday" v-if="item.workDesireAtr == 0">
                                {{item.workDesireName}}
                            </span>
                            <span style="margin: 0" class="uk-text-half-day-work" v-if="item.workDesireAtr == 1 || item.workDesireAtr == 2">
                                {{item.workDesireName}}
                            </span>
                            <span style="margin: 0" class="uk-text-attendance" v-if="item.workDesireAtr == 3">
                                {{item.workDesireName}}
                            </span>
                        </span>
                        <!-- <span style="padding: 0.25em 1em; font-weight: bold; border-radius: 0.25rem; background-color: red">
                            勤務希望
                        </span> -->
                        <!-- A4_13 A4_14 -->
                        <span 
                            v-for="(timeZone, timeZoneIndex) in item.workDesireTimeZone" 
                            v-bind:key="timeZoneIndex"
                            :value="timeZoneIndex"
                            style="font-weight: bold;"
                        >
                            {{timeZone.start}}～{{timeZone.end}}
                        </span>
                    </div>
                </div>
            </div>
            <div class="detail-spacing">
                <!-- A4_15 -->
                <span><i class="far fa-sticky-note"></i></span>
                <!-- A4_16 -->
                <span style="color: #999">{{ "KSUS01_13" | i18n }}</span>
            </div>
            <div class="detail-spacing detail-indent">
                <!-- A4_17 -->
                <nts-text-area
                    v-model="memo"
                    v-bind:disabled="true"
                    v-bind:showTitle="false"
                    v-bind:inlineTitle="false"/>
            </div>
        </div>
    </div>
</div>
</template>