package hu.kocsisgeri.betterneptun.ui.adapter.cell

import android.annotation.SuppressLint
import androidx.navigation3.runtime.NavKey
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import hu.kocsisgeri.betterneptun.R
import hu.kocsisgeri.betterneptun.databinding.CellMessageBinding
import hu.kocsisgeri.betterneptun.ui.adapter.ListItem
import hu.kocsisgeri.betterneptun.ui.model.MessageModel
import hu.kocsisgeri.betterneptun.ui.navigation.destination.MessageDetailDestination
import kotlinx.coroutines.flow.MutableSharedFlow
import java.text.SimpleDateFormat
import java.util.*

fun cellMessageDelegate(event: MutableSharedFlow<InteractionEvent>) =
    adapterDelegateViewBinding<MessageModel, ListItem, CellMessageBinding>(
        viewBinding = { layoutInflater, parent ->
            CellMessageBinding.inflate(layoutInflater, parent, false)
        },
        block = {
            binding.currentUserInfo.setOnClickListener {
                event.tryEmit(ReadMessageEvent(item.id))
                event.tryEmit(NavigationEvent(MessageDetailDestination(item.name))) // TODO
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


interface InteractionEvent
data class NavigationEvent(val destination: NavKey): InteractionEvent
data class ReadMessageEvent(val messageId: Int) : InteractionEvent