package org.delcom.pam_p4_ifs23009.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.delcom.pam_p4_ifs23009.network.plants.service.IPlantAppContainer
import org.delcom.pam_p4_ifs23009.network.plants.service.IPlantRepository
import org.delcom.pam_p4_ifs23009.network.plants.service.PlantAppContainer
import org.delcom.pam_p4_ifs23009.network.plants.service.PlantRepository

@Module
@InstallIn(SingletonComponent::class)
object PlantModule {
    @Provides
    fun providePlantContainer(): IPlantAppContainer {
        return PlantAppContainer()
    }

    @Provides
    fun providePlantRepository(container: IPlantAppContainer): IPlantRepository {
        return container.plantRepository
    }
}