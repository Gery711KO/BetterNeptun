package hu.kocsisgeri.betterneptun.ui.model

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class CourseModel(val courseId: String): NavKey