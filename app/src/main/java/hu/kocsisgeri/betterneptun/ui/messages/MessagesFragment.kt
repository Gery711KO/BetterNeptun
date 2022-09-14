package hu.kocsisgeri.betterneptun.ui.messages

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.data.dao.ApiResult
import hu.kocsisgeri.betterneptun.databinding.FragmentMessagesBinding
import hu.kocsisgeri.betterneptun.ui.adapter.DiffListAdapter
import hu.kocsisgeri.betterneptun.ui.adapter.cell.*
import hu.kocsisgeri.betterneptun.utils.NotifyingLinearLayoutManager
import hu.kocsisgeri.betterneptun.utils.setBackButton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class MessagesFragment : Fragment() {

    private val viewModel: MessagesViewModel by viewModel()
    private lateinit var binding: FragmentMessagesBinding
    private val events = MutableSharedFlow<InteractionEvent>(1, 50)

    private val listAdapter = DiffListAdapter(cellMessageDelegate(events))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setList()
        setBackButton(binding.backButton)
        observeNavigation()
    }

    @SuppressLint("SetTextI18n")
    private fun setList() {
        binding.messageList.apply {
            layoutManager = NotifyingLinearLayoutManager(context)
            adapter = listAdapter
        }

        viewModel.listItems.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    listAdapter.updateData(result.data.map { it.mapToModel() })
                    binding.progressLayout.isVisible = false
                }
                is ApiResult.Error -> {
                    // do something with error
                }
                is ApiResult.Progress -> {
                    binding.progressLayout.isVisible = true
                    binding.progress.progress = result.percentage
                    binding.progressLabel.text = "Üzenetek betöltése (${result.percentage}%)"
                }
            }
        }

        binding.messageList.setOnScrollChangeListener { _, _, _, _, _ ->
            if (isLastVisible()) {
                //viewModel.fetch()
            }
        }

        (binding.messageList.layoutManager as NotifyingLinearLayoutManager).mCallback =
            object : NotifyingLinearLayoutManager.OnLayoutCompleteCallback {
                override fun onLayoutComplete() {
                    binding.messageList.scrollToPosition(viewModel.getCurrentPosition())
                }
            }
    }

    private fun isLastVisible(): Boolean {
        val layoutManager = binding.messageList.layoutManager as LinearLayoutManager
        val pos = layoutManager.findLastCompletelyVisibleItemPosition()
        val numItems: Int = listAdapter.itemCount
        return pos >= numItems - 1
    }

    private fun observeNavigation() {
        events.onEach {
            when (it) {
                is NavigationEvent -> {
                    if (findNavController().currentDestination?.id == R.id.messagesFragment)
                        findNavController().navigate(it.navDirections)
                }
                is ReadMessageEvent -> {
                    viewModel.readMessage(it.messageId)
                }
            }
        }.launchIn(viewModel.viewModelScope)
    }
}