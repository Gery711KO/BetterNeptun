package hu.kocsisgeri.betterneptun.domain.api.datasource

import com.google.gson.Gson
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.domain.api.APIService
import hu.kocsisgeri.betterneptun.domain.api.dto.*
import hu.kocsisgeri.betterneptun.domain.api.network.CustomCookieJar
import hu.kocsisgeri.betterneptun.domain.api.network.NetworkResponse
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.ui.model.*
import hu.kocsisgeri.betterneptun.utils.getDoubleValue
import hu.kocsisgeri.betterneptun.utils.getIntValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import timber.log.Timber
import java.lang.Exception
import java.util.*
import kotlin.coroutines.CoroutineContext

class NetworkDataSourceImpl(
    private val api: APIService,
    private val cookieJar: CustomCookieJar
) : NetworkDataSource, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    override suspend fun initiateLogin(userData: LoginRequestDto): NetworkResponse<LoginResponseDto, String> =
        withContext(coroutineContext) {
            val loginCheck = api.checkLogin(userData)
            api.checkTry(userData)
            api.checkPopUp(PopupDto())
            loginCheck
        }

    override suspend fun getLogin(): NetworkResponse<String, String> {
        return api.getLogin()
    }

    override suspend fun checkLogin(userData: LoginRequestDto): NetworkResponse<LoginResponseDto, String> {
        return api.checkLogin(userData)
    }

    override suspend fun checkTry(userData: LoginRequestDto): NetworkResponse<LoginResponseDto, String> {
        return api.checkTry(userData)
    }

    override suspend fun checkPopUp(popupDto: PopupDto): NetworkResponse<String, String> {
        return api.checkPopUp(popupDto)
    }

    override suspend fun getMessages(neptunUser: NeptunUser): NetworkResponse<MessageResponseDto, String> {
        return api.fetchMessages(neptunUser)
    }

    override suspend fun getTrainings(neptunUser: NeptunUser): NetworkResponse<TrainingResponseDto, String> {
        return api.fetchUserData(neptunUser)
    }

    override suspend fun getAddedCourses(neptunUser: NeptunUser): NetworkResponse<AddedSubjectsResponseDto, String> {
        return api.fetchAddedSubjects(
            AddedSubjectRequest(
                neptunUser.UserLogin,
                neptunUser.Password,
                -1
            )
        )
    }

    override suspend fun getMarkBookData(neptunUser: NeptunUser): NetworkResponse<MarkBookDataResponseDto, String> {
        return api.fetchMarkBookData(
            MarkBookRequest(
                neptunUser.UserLogin,
                neptunUser.Password
            )
        )
    }

    override suspend fun getData(): ApiResult<StudentData> = withContext(Dispatchers.IO) {
        try {
            val doc = Jsoup.connect("https://neptun.uni-obuda.hu/hallgato/main.aspx")
                .cookies(cookieJar.getCookies().associate {
                    it.name to it.value
                }).get()
            val dataText =
                doc.getElementsByAttributeValue("id", "upTraining").first()?.child(2)?.text()
                    ?.split("-")
            ApiResult.Success(
                StudentData(
                    name = dataText?.get(0)?.trim(),
                    neptun = dataText?.get(1)?.trim(),
                )
            )
        } catch (ex: Exception) {
            ApiResult.Error("Network error")
        }
    }

    override suspend fun getCourses(): ApiResult<CourseResponseDto> = withContext(Dispatchers.IO) {
        try {
            val doc = Jsoup.connect("https://neptun.uni-obuda.hu/hallgato/TimeTableHandler.ashx")
                .cookies(cookieJar.getCookies().associate {
                    it.name to it.value
                }).requestBody(
                    Gson().toJson(
                        CourseRequestDto().copy(
                            startdate = "/Date(${(Date().time - THREE_MONTHS_IN_MILLIS)})/",
                            enddate = "/Date(${(Date().time + THREE_MONTHS_IN_MILLIS)})/"
                        )
                    )
                ).post()
            Gson().fromJson(doc.body().text(), CourseResponseDto::class.java)
                .let { ApiResult.Success(it) }
        } catch (ex: Exception) {
            ApiResult.Error("Network error")
        }
    }

    override suspend fun getAverages(): List<SemesterModel> {
        try {
            val doc =
                Jsoup.connect("https://neptun.uni-obuda.hu/hallgato/main.aspx?ismenuclick=true&ctrl=0205")
                    .cookies(cookieJar.getCookies().associate {
                        it.name to it.value
                    }).get()
            val data = doc.getElementsByAttributeValue("class", "scrollablebody")
                .first()?.children()?.filter { it.hasAttr("ri") }

            return data?.map {
                SemesterModel(
                    semesterTitle = it.child(1).text(),
                    status = it.child(2).text(),
                    moneyStatus = it.child(3).text(),
                    semesterFulfilledCredits = it.child(4).getIntValue(),
                    semesterTakenCredits = it.child(5).getIntValue(),
                    allFulfilledCredits = it.child(6).getIntValue(),
                    allTakenCredits = it.child(7).getIntValue(),
                    normalAverage = it.child(8).getDoubleValue(),
                    commutativeAverage = it.child(9).getDoubleValue(),
                )
            }?.map { model ->
                val sort = model.semesterTitle.split("/").map { it.toInt() }
                model.copy(
                    index = sort.sum()
                )
            }?.sortedBy { it.index }?: listOf()
        } catch (ex: Exception) {
            Timber.e(ex)
            return listOf()
        }
    }

    override suspend fun markMessageAsRead(messageReader: MessageReader): NetworkResponse<NeptunUser, String> {
        return api.markMessageAsRead(messageReader)
    }

    companion object {
        const val THREE_MONTHS_IN_MILLIS = 7862400000
    }
}