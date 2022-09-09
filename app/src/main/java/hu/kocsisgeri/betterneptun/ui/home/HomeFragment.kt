package hu.kocsisgeri.betterneptun.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import hu.kocsisgeri.betterneptun.data.repository.course.CourseRepo
import hu.kocsisgeri.betterneptun.databinding.FragmentHomeBinding
import hu.kocsisgeri.betterneptun.utils.getCourseDateString
import hu.kocsisgeri.betterneptun.utils.setButtonNavigation
import hu.kocsisgeri.betterneptun.utils.showToastOnClick
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()

    private val args by navArgs<HomeFragmentArgs>()

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserData()
        setButtons()
        fetchNewDataOnAppStart(args.currentUser?.maxPage)
    }

    @SuppressLint("SetTextI18n")
    private fun setUserData() {
        binding.studentNeptun.text = args.currentUser?.neptun
        binding.studentName.text = args.currentUser?.name
        binding.studentUnread.text = "${args.currentUser?.unreadMessages} olvasatlan üzenet"
    }

    @SuppressLint("SetTextI18n")
    private fun fetchNewDataOnAppStart(maxPage: Int?) {
        if (!viewModel.isLoggedIn.value) {
            maxPage?.let {
                viewModel.fetchMessages(it)
            }?: viewModel.fetchMessages(1)
        }

        CourseRepo.sorted.distinctUntilChanged().observe(viewLifecycleOwner) {
            binding.courseLoading.isVisible = false
            binding.nextCourseLabel.text = "Következő óra"
            binding.nextCourseTitle.text = it.title.trimStart()
            binding.nextCourseLocation.text = it.location.trim()
            binding.nextCourseDate.text = it.startTime.getCourseDateString()
            binding.nextCourseStart.text =
                "${it.startTime.hour}:${if (it.startTime.minute < 10){"0" + it.startTime.minute} else it.startTime.minute}"
            binding.nextCourseEnd.text =
                "${it.endTime.hour}:${if (it.endTime.minute < 10) { "0" + it.endTime.minute} else it.endTime.minute}"
            binding.line.background.setTint(it.color)
        }
    }

    private fun setButtons() {
        setButtonNavigation(binding.settingsButtonCard, HomeFragmentDirections.toSettings())
        setButtonNavigation(binding.messageButtonCard, HomeFragmentDirections.toMessages())
        setButtonNavigation(binding.calendarButtonCard, HomeFragmentDirections.toCalendar())

        showToastOnClick(binding.coursesButtonCard, "Fejlesztés alatt!!")
        showToastOnClick(binding.examsButtonCard, "Fejlesztés alatt!!")
        showToastOnClick(binding.semestersButtonCard, "Fejlesztés alatt!!")
        showToastOnClick(binding.scheduleButtonCard, "Fejlesztés alatt!!")
    }

}