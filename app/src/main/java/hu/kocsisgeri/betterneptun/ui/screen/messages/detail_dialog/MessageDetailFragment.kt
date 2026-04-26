package hu.kocsisgeri.betterneptun.ui.screen.messages.detail_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.kocsisgeri.betterneptun.databinding.FragmentMessageDetailBinding
import hu.kocsisgeri.betterneptun.ui.screen.ComposeFragment
import hu.kocsisgeri.betterneptun.ui.screen.messages.MessagesViewModel
import hu.kocsisgeri.betterneptun.utils.setTextAndAddClickableLinks
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MessageDetailFragment : ComposeFragment() {

    private val viewModel: MessagesViewModel by activityViewModel()

    private lateinit var binding: FragmentMessageDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        closeButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearMessageDetail()
    }

    private fun closeButton() {
        binding.closeButton.setOnClickListener {
           navigator.navigateBack()
        }
    }

    private fun setData() {
        viewModel.messageDetail.observe(viewLifecycleOwner) {
            it?.let { message ->
                binding.detail.setTextAndAddClickableLinks(
                    markdown = message.posts.first().htmlText,
                    requireContext(),
                    navigator
                )
                binding.subject.text = message.subject
                binding.sender.text = message.sender
                binding.date.text = message.date
            }
        }
    }
}