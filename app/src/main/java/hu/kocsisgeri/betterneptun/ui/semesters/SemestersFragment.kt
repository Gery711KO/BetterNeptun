package hu.kocsisgeri.betterneptun.ui.semesters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.google.android.material.tabs.TabLayout
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.databinding.FragmentSemestersBinding
import hu.kocsisgeri.betterneptun.utils.setBackButton
import kotlinx.android.synthetic.main.fragment_semesters.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SemestersFragment : Fragment() {

    private val viewModel: SemestersViewModel by viewModel()
    private lateinit var binding: FragmentSemestersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSemestersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButton(binding.backButton)
        setupTabNavigation()
        setChartContent()
    }

    private fun setupTabNavigation() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.chart.isVisible = true
                        binding.chart.animateY(1000)
                    }
                    else -> binding.chart.isVisible = false
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setChartContent() {
        setChartTheme()
        viewModel.credits.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResult.Error -> {}
                is ApiResult.Progress -> {
                    binding.loading.isVisible = true
                }
                is ApiResult.Success -> {
                    binding.loading.isVisible = false
                    binding.chart.apply {
                        this.data = it.data
                        this.barData.setValueTextColor(context.getColor(R.color.base_text_color))
                        animateY(1000)
                    }
                }
            }
        }
    }

    private fun setChartTheme() {
        binding.chart.apply {
            chart.description.isEnabled = false
            chart.setPinchZoom(false)
            chart.setDrawBarShadow(false)
            chart.setDrawGridBackground(false)

            val l = chart.legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            l.orientation = Legend.LegendOrientation.VERTICAL
            l.textColor = requireContext().getColor(R.color.base_text_color)
            l.setDrawInside(true)
            l.yOffset = 10f
            l.xOffset = 10f
            l.yEntrySpace = 0f
            l.textSize = 10f

            val xAxis = chart.xAxis
            xAxis.granularity = 1f
            xAxis.setCenterAxisLabels(true)
            xAxis.textColor = requireContext().getColor(R.color.base_text_color)

            val leftAxis = chart.axisLeft
            leftAxis.textColor = requireContext().getColor(R.color.base_text_color)
            leftAxis.valueFormatter = LargeValueFormatter()
            leftAxis.setDrawGridLines(false)
            leftAxis.spaceTop = 25f
            leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


            chart.axisRight.isEnabled = false
        }
    }
}