package hu.kocsisgeri.betterneptun.ui.messages.detail_dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.databinding.FragmentMessageDetailBinding
import hu.kocsisgeri.betterneptun.ui.adapter.cell.toDateString
import hu.kocsisgeri.betterneptun.utils.setTextAndAddClickableLinks

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
        setBackgroundClick()
        setStatusAndNavbarTransparency()
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
            duration = 200
        }.start()
    }

    private fun closeButton() {
        binding.closeButton.setOnClickListener {
           closeAnimation()
        }
    }

    private fun closeAnimation() {
        binding.animationRoot.animate().scaleX(0.0f).scaleY(0.0f).apply {
            duration = 200
        }.withEndAction { findNavController().popBackStack() }.start()
    }

    private fun setData() {
        binding.detail.setTextAndAddClickableLinks(args.message?.detail, requireContext(), findNavController())
        binding.subject.text = args.message?.subject
        binding.sender.text = args.message?.name
        binding.date.text = args.message?.date?.toDateString()
    }

    private fun setBackgroundClick() {
        binding.background.isClickable = true
        binding.background.setOnClickListener {
            closeAnimation()
        }
    }
}

val DialogFragment.window: Window? get() = dialog?.window

fun DialogFragment.setStatusAndNavbarTransparency() {
    if (Build.VERSION.SDK_INT == 30) {
        window?.run {
            window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_FULLSCREEN
        }

    } else {
        window?.run {
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }
}