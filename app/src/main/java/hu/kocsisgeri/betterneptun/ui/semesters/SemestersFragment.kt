package hu.kocsisgeri.betterneptun.ui.semesters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.google.android.material.tabs.TabLayout
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.databinding.FragmentSemestersBinding
import hu.kocsisgeri.betterneptun.utils.setBackButton
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
        setBarChartContent()
    }

    private fun setupTabNavigation() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.barChart.isVisible = tab?.position == 0
                binding.lineChart.isVisible = tab?.position == 1
                when (tab?.position) {
                    0 -> {
                        binding.barChart.animateY(1000)
                    }
                    1 -> {
                        binding.lineChart.animateY(1000)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setBarChartContent() {
        setChartTheme()
        viewModel.credits.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResult.Error -> {}
                is ApiResult.Progress -> {
                    binding.loading.isVisible = true
                }
                is ApiResult.Success -> {
                    binding.loading.isVisible = false
                    binding.barChart.apply {
                        this.data = it.data
                        this.barData.setValueTextColor(context.getColor(R.color.base_text_color))
                        this.barData.setValueTextSize(10f)
                        this.barData.setValueFormatter(
                            LargeValueFormatter()
                        )
                        animateY(1000)
                    }
                }
            }
        }

        viewModel.averages.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResult.Error -> {}
                is ApiResult.Progress -> {
                    binding.loading.isVisible = true
                }
                is ApiResult.Success -> {
                    binding.loading.isVisible = false
                    binding.lineChart.apply {
                        this.data = it.data
                        this.lineData.setValueTextColor(context.getColor(R.color.base_text_color))
                        this.lineData.setValueTextSize(10f)
                        this.lineData.setValueFormatter(
                            DefaultAxisValueFormatter(2)
                        )
                    }
                }
            }
        }
    }

    private fun setChartTheme() {
        binding.barChart.apply {
            isDoubleTapToZoomEnabled = false
            description.isEnabled = false
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)


            val ll1 = LimitLine(30f, "")
            ll1.lineWidth = 1f
            ll1.lineColor = context.getColor(R.color.base_text_color)
            ll1.enableDashedLine(10f, 10f, 0f)
            ll1.textColor = context.getColor(R.color.base_text_color)
            ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
            ll1.textSize = 10f

            // draw limit lines behind data instead of on top
            axisLeft.setDrawLimitLinesBehindData(true);
            xAxis.setDrawLimitLinesBehindData(true);

            // add limit lines
            axisLeft.addLimitLine(ll1);


            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.textColor = requireContext().getColor(R.color.base_text_color)
            legend.setDrawInside(true)
            legend.yOffset = 10f
            legend.xOffset = 10f
            legend.yEntrySpace = 0f
            legend.textSize = 10f
            legend.form = Legend.LegendForm.SQUARE

            xAxis.granularity = 1f
            xAxis.setCenterAxisLabels(true)
            xAxis.textColor = requireContext().getColor(R.color.base_text_color)

            val leftAxis = axisLeft
            leftAxis.textColor = requireContext().getColor(R.color.base_text_color)
            leftAxis.valueFormatter = LargeValueFormatter()
            leftAxis.setDrawGridLines(false)
            leftAxis.spaceTop = 25f
            leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


            axisRight.isEnabled = false
        }

        binding.lineChart.apply {
            isDoubleTapToZoomEnabled = false
            description.isEnabled = false
            setPinchZoom(false)
            setDrawGridBackground(false)

            val ll1 = LimitLine(5f, "5")
            ll1.lineWidth = 1f
            ll1.enableDashedLine(10f, 10f, 0f)
            ll1.labelPosition = LimitLabelPosition.LEFT_TOP
            ll1.textColor = context.getColor(R.color.base_text_color)
            ll1.lineColor = context.getColor(R.color.base_text_color)
            ll1.textSize = 10f


            // draw limit lines behind data instead of on top
            axisLeft.setDrawLimitLinesBehindData(true);
            xAxis.setDrawLimitLinesBehindData(true);

            // add limit lines
            axisLeft.addLimitLine(ll1);

            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.textColor = requireContext().getColor(R.color.base_text_color)
            legend.setDrawInside(true)
            legend.yOffset = 10f
            legend.xOffset = 10f
            legend.yEntrySpace = 0f
            legend.textSize = 10f
            legend.form = Legend.LegendForm.LINE

            xAxis.granularity = 1f
            xAxis.setCenterAxisLabels(true)
            xAxis.textColor = requireContext().getColor(R.color.base_text_color)

            val leftAxis = axisLeft
            leftAxis.textColor = requireContext().getColor(R.color.base_text_color)
            leftAxis.valueFormatter = LargeValueFormatter()
            leftAxis.setDrawGridLines(false)
            leftAxis.spaceTop = 25f
            leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


            axisRight.isEnabled = false
        }
    }
}