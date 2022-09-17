package hu.kocsisgeri.betterneptun.ui.adapter.cell

import android.annotation.SuppressLint
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import hu.kocsisgeri.betterneptun.databinding.CellSubjectBinding
import hu.kocsisgeri.betterneptun.ui.adapter.ListItem
import hu.kocsisgeri.betterneptun.ui.model.MarkBookDataModel
import kotlinx.coroutines.flow.MutableSharedFlow

@SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
fun cellSubjectDelegate(event: MutableSharedFlow<InteractionEvent>) =
    adapterDelegateViewBinding<MarkBookDataModel, ListItem, CellSubjectBinding>(
        viewBinding = { layoutInflater, parent ->
            CellSubjectBinding.inflate(layoutInflater, parent, false)
        },
        block = {
            bind {
                binding.subjectCode.text = item.subjectCode
                binding.subjectName.text = item.subjectName
                binding.credits.text = "Kredit:\t\t${item.subjectCredit}"
                item.state.res?.let {
                    binding.state.setImageDrawable(context.getDrawable(it))
                }?: binding.state.setImageDrawable(null)
            }
        }
    )