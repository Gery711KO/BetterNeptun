package hu.kocsisgeri.betterneptun.ui.navigation.destination

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class CourseDetailDestination(val courseId: String): NavKey
