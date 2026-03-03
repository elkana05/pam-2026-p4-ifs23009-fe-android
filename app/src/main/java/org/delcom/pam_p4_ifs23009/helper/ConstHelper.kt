package org.delcom.pam_p4_ifs23009.helper

class ConstHelper {
    // Route Names
    enum class RouteNames(val path: String) {
        Home(path = "home"),
        Profile(path = "profile"),
        
        Plants(path = "plants"),
        PlantsAdd(path = "plants/add"),
        PlantsDetail(path = "plants/{plantId}"),
        PlantsEdit(path = "plants/{plantId}/edit"),

        Fishes(path = "fishes"),
        FishesAdd(path = "fishes/add"),
        FishesDetail(path = "fishes/{fishId}"),
        FishesEdit(path = "fishes/{fishId}/edit"),
    }
}
