package hu.kocsisgeri.betterneptun.ui.screen.messages

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import hu.kocsisgeri.betterneptun.databinding.FragmentMessagesBinding
import hu.kocsisgeri.betterneptun.ui.adapter.DiffListAdapter
import hu.kocsisgeri.betterneptun.ui.adapter.cell.InteractionEvent
import hu.kocsisgeri.betterneptun.ui.adapter.cell.NavigationEvent
import hu.kocsisgeri.betterneptun.ui.adapter.cell.ReadMessageEvent
import hu.kocsisgeri.betterneptun.ui.adapter.cell.cellMessageDelegate
import hu.kocsisgeri.betterneptun.ui.model.MessageModel
import hu.kocsisgeri.betterneptun.ui.screen.ComposeFragment
import hu.kocsisgeri.betterneptun.utils.setBackButton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Date

class MessagesFragment : ComposeFragment() {

    private val viewModel: MessagesViewModel by activityViewModel()

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
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        viewModel.listItems.observe(viewLifecycleOwner) { result ->
            listAdapter.updateData(
                result.map {
                    MessageModel(
                        id = it.id,
                        name = it.name,
                        subject = it.subject,
                        isNew = it.isNew,
                        detail = "",
                        date = Date(),
                    )
                }
            )

            binding.progressLayout.isVisible = false
        }
    }

    private fun observeNavigation() {
        events.onEach {
            when (it) {
                is NavigationEvent -> navigator.navigateTo(it.destination)
                is ReadMessageEvent -> {
                    viewModel.readMessage(it.messageId)
                }
            }
        }.launchIn(viewModel.viewModelScope)
    }
}