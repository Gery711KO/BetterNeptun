package hu.kocsisgeri.betterneptun.ui.adapter.cell

import android.annotation.SuppressLint
import androidx.navigation.NavDirections
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.databinding.CellMessageBinding
import hu.kocsisgeri.betterneptun.ui.adapter.ListItem
import hu.kocsisgeri.betterneptun.ui.messages.MessagesFragmentDirections
import hu.kocsisgeri.betterneptun.ui.model.MessageModel
import kotlinx.coroutines.flow.MutableSharedFlow
import java.text.SimpleDateFormat
import java.util.*

fun cellMessageDelegate(event: MutableSharedFlow<NavigationEvent>) =
    adapterDelegateViewBinding<MessageModel, ListItem, CellMessageBinding>(
        viewBinding = { layoutInflater, parent ->
            CellMessageBinding.inflate(layoutInflater, parent, false)
        },
        block = {
            binding.currentUserInfo.setOnClickListener {
                event.tryEmit(NavigationEvent(MessagesFragmentDirections.toMessageDetail(item)))
            }

            bind {
                if (item.isNew) {
                    binding.currentUserInfoCard.strokeColor =
                        context.getColor(R.color.base_button_bg)
                    binding.currentUserInfoCard.strokeWidth = 10
                } else {
                    binding.currentUserInfoCard.strokeColor =
                        context.getColor(android.R.color.transparent)
                    binding.currentUserInfoCard.strokeWidth = 0
                }
                binding.senderName.text = item.name
                binding.subject.text = item.subject
                binding.sentDate.text = item.date.toDateString()
            }
        }
    )


const val ONLY_DATE_FORMAT = "yyyy.MM.dd. HH:mm:ss"

@SuppressLint("SimpleDateFormat")
fun Date.toDateString(): String {
    return SimpleDateFormat(ONLY_DATE_FORMAT).format(this)
}

data class NavigationEvent(val navDirections: NavDirections)