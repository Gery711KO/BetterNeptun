package hu.kocsisgeri.betterneptun.ui.messages.detail_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import hu.kocsisgeri.betterneptun.databinding.FragmentMessageDetailBinding
import hu.kocsisgeri.betterneptun.ui.adapter.cell.toDateString
import hu.kocsisgeri.betterneptun.utils.setBackButton
import hu.kocsisgeri.betterneptun.utils.setTextAndAddClickableLinks

class MessageDetailFragment : Fragment() {

    private lateinit var binding: FragmentMessageDetailBinding
    private val args by navArgs<MessageDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButton(binding.backButton)
        setData()
    }

    private fun setData() {
        binding.detail.setTextAndAddClickableLinks(args.message?.detail, requireContext(), findNavController())
        binding.subject.text = args.message?.subject
        binding.sender.text = args.message?.name
        binding.date.text = args.message?.date?.toDateString()

        binding.subjectTitle.text = args.message?.subject
    }
}