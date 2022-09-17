package hu.kocsisgeri.betterneptun.ui.subjects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.databinding.FragmentSubjectsBinding
import hu.kocsisgeri.betterneptun.ui.adapter.DiffListAdapter
import hu.kocsisgeri.betterneptun.ui.adapter.cell.InteractionEvent
import hu.kocsisgeri.betterneptun.ui.adapter.cell.cellSubjectDelegate
import hu.kocsisgeri.betterneptun.utils.setBackButton
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubjectsFragment : Fragment() {

    private val viewModel: SubjectsViewModel by viewModel()
    private lateinit var binding: FragmentSubjectsBinding
    private val events = MutableSharedFlow<InteractionEvent>(1, 50)


    private val listAdapter = DiffListAdapter(cellSubjectDelegate(events))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButton(binding.backButton)
        setData()
    }

    private fun setData() {
        binding.subjectList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        viewModel.listItems.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResult.Error -> {}
                is ApiResult.Progress -> {
                    binding.courseLoading.isVisible = true
                }
                is ApiResult.Success -> {
                    listAdapter.updateData(it.data)
                    binding.courseLoading.isVisible = false
                }
            }
        }
    }
}