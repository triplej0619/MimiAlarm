package com.mimi.mimialarm

import com.mimi.mimialarm.core.infrastructure.AlarmManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import com.nhaarman.mockito_kotlin.mock

/**
 * Created by MihyeLee on 2017. 8. 9..
 */
@Module
class TestModule {
    @Provides
    fun provideAlarmManager(): AlarmManager = mock()
}