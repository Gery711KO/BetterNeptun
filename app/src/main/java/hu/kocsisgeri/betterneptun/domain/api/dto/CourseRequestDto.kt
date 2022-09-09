package hu.kocsisgeri.betterneptun.domain.api.dto

import java.io.Serializable

data class CourseRequestDto(
    val TimeTableID: String? = "upFunction_c_common_timetable_upParent_tabOrarend_ctl00_up_timeTablePerson_upMaxLimit_personTimetable_upTimeTable_TimeTable1",
    val method: String? = "list",
    val viewtype: String? = "week",
    val startdate: String? = "/Date(1661760000000)/",
    val enddate: String? = "/Date(1664755199000)/",
    val showTypes: List<Boolean>? = listOf(
        true, false, false, false, false, false,
        false, false, false, false, false, false,
    ),
    val isNormalPost: Boolean? = false,
    val timetableform: String? = "0",
    val SzuloOrarend: Boolean? = false
)

data class CourseResponseDto(

    var dynamicViewString: String? = null,
    var end: String? = null,
    var error: String? = null,
    var events: ArrayList<TimelineEventDto> = arrayListOf(),
    var isDynamicView: Boolean? = null,
    var issort: Boolean? = null,
    var start: String? = null,
    var viewtype: String? = null

) : Serializable

data class TimelineEventDto(

    var allday: Int? = null,
    var attend: String? = null,
    var editable: Int? = null,
    var enddate: String? = null,
    var id: Int? = null,
    var iskiemelt: Boolean? = null,
    var location: String? = null,
    var morethanonedayevent: Int? = null,
    var recurringevent: Int? = null,
    var startdate: String? = null,
    var theme: Int? = null,
    var title: String? = null,
    var faculty: String? = null

)