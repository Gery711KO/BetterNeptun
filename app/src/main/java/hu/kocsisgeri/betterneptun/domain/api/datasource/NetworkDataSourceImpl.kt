package hu.kocsisgeri.betterneptun.domain.api.datasource

import com.google.gson.Gson
import hu.kocsisgeri.betterneptun.domain.api.APIService
import hu.kocsisgeri.betterneptun.domain.api.dto.*
import hu.kocsisgeri.betterneptun.domain.api.network.CustomCookieJar
import hu.kocsisgeri.betterneptun.domain.api.network.NetworkResponse
import hu.kocsisgeri.betterneptun.domain.model.StudentData
import hu.kocsisgeri.betterneptun.ui.model.NeptunUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
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

    override suspend fun getData(): StudentData = withContext(Dispatchers.IO) {
        val doc = Jsoup.connect("https://neptun.uni-obuda.hu/hallgato/main.aspx")
            .cookies(cookieJar.getCookies().associate {
                it.name to it.value
            }).get()
        val dataText = doc.getElementsByAttributeValue("id", "upTraining").first()?.child(2)?.text()
            ?.split("-")
        val unread = doc.getElementsByAttributeValue("id", "_lnkInbox").first()?.text()
        StudentData(
            name = dataText?.get(0)?.trim(),
            neptun = dataText?.get(1)?.trim(),
            unreadMessages = if (unread?.contains("(") == true) {
                unread.split("(")?.get(1)?.removeSuffix(")")
            } else {
                "0"
            },
        )
    }

    override suspend fun getCourses(): CourseResponseDto {
        val gson = Gson()
        val doc = Jsoup.connect("https://neptun.uni-obuda.hu/hallgato/TimeTableHandler.ashx")
            .cookies(cookieJar.getCookies().associate {
                it.name to it.value
            }).requestBody(
                gson.toJson(
                    CourseRequestDto().copy(
                        startdate = "/Date(${(Date().time - THREE_MONTHS_IN_MILLIS)})/",
                        enddate = "/Date(${(Date().time + THREE_MONTHS_IN_MILLIS)})/"
                    )
                )
            ).post()
        return gson.fromJson(doc.body().text(), CourseResponseDto::class.java)
    }

    companion object {
        const val THREE_MONTHS_IN_MILLIS = 7862400000
    }
}