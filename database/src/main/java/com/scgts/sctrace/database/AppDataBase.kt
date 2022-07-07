package com.scgts.sctrace.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.scgts.sctrace.database.converter.*
import com.scgts.sctrace.database.dao.*
import com.scgts.sctrace.database.model.*

@Database(
    entities = [
        TaskEntity::class,
        AssetEntity::class,
        TraceEventEntity::class,
        UserEntity::class,
        UserProjectRoleEntity::class,
        ExpectedAmountEntity::class,
        ConditionEntity::class,
        FacilityEntity::class,
        RackLocationEntity::class,
        ProjectEntity::class,
        ProjectConditionEntity::class,
        MiscellaneousQueueEntity::class,
        ProjectFacilityEntity::class
    ],
    version = 71,
    exportSchema = true
)
@TypeConverters(
    ListTypeConverters::class,
    DateTimeConverter::class,
    SubmitStatusConverter::class,
    FacilityTypeConverter::class,
    TaskStatusConverter::class,
    TaskTypeForFilteringConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tasksDao(): TasksDao
    abstract fun assetsDao(): AssetsDao
    abstract fun traceEventsDao(): TraceEventsDao
    abstract fun userDao(): UserDao
    abstract fun userProjectRolesDao(): UserProjectRolesDao
    abstract fun expectedAmountsDao(): ExpectedAmountsDao
    abstract fun conditionsDao(): ConditionsDao
    abstract fun facilitiesDao(): FacilitiesDao
    abstract fun rackLocationsDao(): RackLocationsDao
    abstract fun projectsDao(): ProjectsDao
    abstract fun projectConditionsDao(): ProjectConditionsDao
    abstract fun miscellaneousQueueDao(): MiscellaneousQueueDao
    abstract fun projectFacilitiesDao(): ProjectFacilitiesDao

    companion object {

        fun init(context: Context, name: String): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                name
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        //TODO: how to this with koin
//        fun destroyInstance() {
//            if(instance != null) instance = null
//        }
    }
}
