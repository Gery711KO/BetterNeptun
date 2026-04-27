package hu.kocsisgeri.betterneptun.ui.adapter.cell

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.databinding.CellSubjectBinding
import hu.kocsisgeri.betterneptun.ui.adapter.ListItem
import hu.kocsisgeri.betterneptun.ui.model.InteractionEvent
import hu.kocsisgeri.betterneptun.domain.model.Subject
import hu.kocsisgeri.betterneptun.utils.setSafeOnClickListener
import kotlinx.coroutines.flow.MutableSharedFlow

@SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
fun cellSubjectDelegate(event: MutableSharedFlow<InteractionEvent>) =
    adapterDelegateViewBinding<Subject, ListItem, CellSubjectBinding>(
        viewBinding = { layoutInflater, parent ->
            CellSubjectBinding.inflate(layoutInflater, parent, false)
        },
        block = {
            binding.subjectRoot.setSafeOnClickListener(250) {
                binding.detailsRoot.isVisible = !binding.detailsRoot.isVisible
                binding.openable.animate().rotationBy(
                    if (binding.detailsRoot.isVisible) -180f else 180f
                ).apply {
                    duration = 200
                }.start()
            }
            bind {
                binding.subjectCode.text = item.subjectCode
                binding.subjectName.text = item.subjectName
                binding.requirementValue.text = item.subjectRequirement
                binding.credits.text = "Kredit:\t\t${item.subjectCredit}"
                if (item.isCompleted) {
                    binding.state.setImageDrawable(context.getDrawable(R.drawable.ic_passed))
                }
            }
        }
    )