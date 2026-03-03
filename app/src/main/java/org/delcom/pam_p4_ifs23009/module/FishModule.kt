package org.delcom.pam_p4_ifs23009.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.delcom.pam_p4_ifs23009.network.fish.service.FishAppContainer
import org.delcom.pam_p4_ifs23009.network.fish.service.IFishAppContainer
import org.delcom.pam_p4_ifs23009.network.fish.service.IFishRepository

@Module
@InstallIn(SingletonComponent::class)
object FishModule {
    @Provides
    fun provideFishContainer(): IFishAppContainer {
        return FishAppContainer()
    }

    @Provides
    fun provideFishRepository(container: IFishAppContainer): IFishRepository {
        return container.fishRepository
    }
}
