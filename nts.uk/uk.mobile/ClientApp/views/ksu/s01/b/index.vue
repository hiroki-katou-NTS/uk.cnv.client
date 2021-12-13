<template>
<div class="ksus01b">
    <!-- B1_1 -->
    <span class="bold">{{startDate}}～{{endDate}}</span>
    <!-- B1_1_1 -->
    <hr/>
    <div class="left-container">
        <!-- B1_2 -->
        <i class="fas fa-clock large-icon"></i>
        <!-- B1_3 -->
        <span class="bold">{{ "KSUS01_16" | i18n }}</span>
    </div>
    <div class="right-container">
        <!-- B1_4 -->
        <span class="xxx-large blue bold">{{totalWorkingTime}}</span>
        <!-- B1_5 -->
        <span class="bold blue">{{ "KSUS01_17" | i18n }}</span>
        <!-- B1_5_1 -->
        <span v-if="totalWorkingTimeMinutes > 0" class="xxx-large blue bold">{{totalWorkingTimeMinutes}}</span>
        <!-- B1_5_2 -->
        <span v-if="totalWorkingTimeMinutes > 0" class="bold blue">{{ "KSUS01_23" | i18n }}</span>
    </div>
    <div class="left-container">
        <!-- B1_6 -->
        <i class="fas fa-yen-sign large-icon"></i>
        <!-- B1_7 -->
        <span class="bold">{{ "KSUS01_18" | i18n }}</span>
    </div>
    <div class="right-container">
        <!-- B1_8 -->
        <span class="xxx-large blue bold">{{formatNumberSeparated(estimatedSalaryMonthly)}}</span>
        <!-- B1_9 -->
        <span class="bold blue">{{ "KSUS01_19" | i18n }}</span>
    </div>
    <!-- B1_10 -->
    <hr/>
    <div style="center-container">
        <!-- B1_11 -->
        <i class="fas fa-coins large-icon"></i>
        <!-- B1_12 -->
        <span class="bold">{{ "KSUS01_20" | i18n }}</span>
    </div>
    <div class="center-container">
        <!-- B1_13 -->
        <span class="xxx-large blue bold">{{formatNumberSeparated(estimatedSalaryCumulative)}}</span>
        <!-- B1_14 -->
        <span class="bold blue">{{ "KSUS01_19" | i18n }}</span>
    </div>
</div>
</template>