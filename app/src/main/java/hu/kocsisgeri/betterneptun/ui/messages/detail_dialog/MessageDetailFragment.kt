package hu.kocsisgeri.betterneptun.ui.messages.detail_dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.databinding.FragmentMessageDetailBinding
import hu.kocsisgeri.betterneptun.ui.adapter.cell.toDateString
import hu.kocsisgeri.betterneptun.ui.main.MainActivity
import hu.kocsisgeri.betterneptun.utils.setBackButton
import hu.kocsisgeri.betterneptun.utils.setTextAndAddClickableLinks
import timber.log.Timber

class MessageDetailFragment : DialogFragment() {

    private lateinit var binding: FragmentMessageDetailBinding
    private val args by navArgs<MessageDetailFragmentArgs>()

    override fun getTheme(): Int = R.style.AppDialogTheme

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
        openAnimation()
        closeButton()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                closeAnimation()
            }
        }
    }

    private fun openAnimation() {
        binding.animationRoot.scaleX = 0.2f
        binding.animationRoot.scaleY = 0.2f
        binding.animationRoot.animate().scaleX(1f).scaleY(1f).apply {
            duration = 300
        }.start()
    }

    private fun closeButton() {
        binding.closeButton.setOnClickListener {
           closeAnimation()
        }
    }

    private fun closeAnimation() {
        binding.animationRoot.animate().scaleX(0.0f).scaleY(0.0f).apply {
            duration = 300
        }.withEndAction { findNavController().popBackStack() }.start()
    }

    private fun setData() {
        binding.detail.setTextAndAddClickableLinks(args.message?.detail, requireContext(), findNavController())
        binding.subject.text = args.message?.subject
        binding.sender.text = args.message?.name
        binding.date.text = args.message?.date?.toDateString()
    }
}