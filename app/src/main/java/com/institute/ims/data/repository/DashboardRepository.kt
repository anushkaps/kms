package com.institute.ims.data.repository

import com.institute.ims.data.model.DashboardModuleCard
import com.institute.ims.data.model.DashboardStat
import com.institute.ims.data.model.UserRole

interface DashboardRepository {
    fun getSummaryStats(role: UserRole): List<DashboardStat>
    fun getModuleCards(): List<DashboardModuleCard>
    fun getQuickChipLabels(): List<String>
    fun getOverviewLine(role: UserRole): String
}
