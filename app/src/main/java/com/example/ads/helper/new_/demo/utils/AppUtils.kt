package com.example.ads.helper.new_.demo.utils

import com.example.ads.helper.new_.demo.BuildConfig

private data class SkuData(
    val applicationId: String = "",
    val lifeTimeSku: String = "",
    val weeklySku: String = "",
    val monthlySku: String = "",
    val yearlySku: String = "",
    val revenueCatID: String = "",
)

const val CONTACTS_BACKUP_APPLICATION_ID = "com.contacts.number.add.sim.phone.recent.details"
const val CONTACTS_BACKUP_LIFE_TIME_SKU = "com.vasu.contacts.lifetime"
const val CONTACTS_BACKUP_WEEKLY_SKU = "com.vasu.contacts.weekly"
const val CONTACTS_BACKUP_MONTHLY_SKU = "com.contacts.backup.monthly"
const val CONTACTS_BACKUP_YEARLY_SKU = "com.contacts.backup.yearly"

const val GPS_MAP_CAMERA_APPLICATION_ID = "com.gps.photo.geo.capture.location.map.time.stamp"
const val GPS_MAP_CAMERA_LIFE_TIME_SKU = "com.gps.photo.geo.capture.lifetime"
const val GPS_MAP_CAMERA_WEEKLY_SKU = ""
const val GPS_MAP_CAMERA_MONTHLY_SKU = "com.gps.photo.geo.capture.monthly"
const val GPS_MAP_CAMERA_YEARLY_SKU = "com.gps.photo.geo.capture.yearly"


private val SKU_KEY_LIST: ArrayList<SkuData> = arrayListOf(
    SkuData(
        applicationId = CONTACTS_BACKUP_APPLICATION_ID,
        lifeTimeSku = CONTACTS_BACKUP_LIFE_TIME_SKU,
        weeklySku = CONTACTS_BACKUP_WEEKLY_SKU,
        monthlySku = CONTACTS_BACKUP_MONTHLY_SKU,
        yearlySku = CONTACTS_BACKUP_YEARLY_SKU,
//        revenueCatID = "",
    ),
    SkuData(
        applicationId = GPS_MAP_CAMERA_APPLICATION_ID,
        lifeTimeSku = GPS_MAP_CAMERA_LIFE_TIME_SKU,
        weeklySku = GPS_MAP_CAMERA_WEEKLY_SKU,
        monthlySku = GPS_MAP_CAMERA_MONTHLY_SKU,
        yearlySku = GPS_MAP_CAMERA_YEARLY_SKU,
//        revenueCatID = "",
    ),
    SkuData(
        applicationId = "com.voice.gps.navigation.map.location.route",
        revenueCatID = "goog_dlPfUeMWkUyljBCupwuSBIwcEMb",
//        monthlySku = "com.voicegps.test_1"
    ),
    SkuData(
        applicationId = "com.voice.changer.sound.effects.girl.call",
        revenueCatID = "goog_idqTpAMCtLoJjqLwuZpuFpwexhA",
    ),
    SkuData(
        applicationId = "com.vasu.ads.helper.demo",
        monthlySku = "vasu.ads.helper.month.test.1",
        weeklySku = "vasu.ads.helper.week.test.1",
        yearlySku = "vasu.ads.helper.year.test.1",
        lifeTimeSku = "vasu.ads.helper.year.test.one",
//        revenueCatID = "goog_VfuepcREdgEARZAcfgLVStSMMDp",
    ),
)


val LIFE_TIME_SKU = SKU_KEY_LIST.find { it.applicationId == BuildConfig.APPLICATION_ID }?.lifeTimeSku ?: ""
val WEEKLY_SKU = SKU_KEY_LIST.find { it.applicationId == BuildConfig.APPLICATION_ID }?.weeklySku ?: ""
val MONTHLY_SKU = SKU_KEY_LIST.find { it.applicationId == BuildConfig.APPLICATION_ID }?.monthlySku ?: ""
val YEARLY_SKU = SKU_KEY_LIST.find { it.applicationId == BuildConfig.APPLICATION_ID }?.yearlySku ?: ""
val REVENUE_CAT_ID = SKU_KEY_LIST.find { it.applicationId == BuildConfig.APPLICATION_ID }?.revenueCatID ?: ""