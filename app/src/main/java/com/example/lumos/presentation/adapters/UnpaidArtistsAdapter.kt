package com.example.lumos.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lumos.R
import com.example.lumos.presentation.viewModels.ManagementViewModel

//class UnpaidArtistsAdapter(
//    private val onMarkPaid: (Artist) -> Unit
//) : ListAdapter<Artist, UnpaidArtistsAdapter.ArtistViewHolder>(ArtistDiffCallback()) {
class UnpaidArtistsAdapter(
    private val onMarkPaid: (ManagementViewModel.ArtistWithUnpaid) -> Unit
) : ListAdapter<ManagementViewModel.ArtistWithUnpaid, UnpaidArtistsAdapter.ArtistViewHolder>(
    ArtistDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_unpaid_artist, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

//    inner class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val nameTextView: TextView = itemView.findViewById(R.id.artist_name)
//        private val balanceTextView: TextView = itemView.findViewById(R.id.artist_balance)
//        private val markPaidButton: Button = itemView.findViewById(R.id.mark_paid_button)
//
//        fun bind(artist: Artist) {
//            nameTextView.text = "${artist.firstName} ${artist.lastName}"
//            balanceTextView.text = "Баланс: ${artist.balance} руб."
//
//            markPaidButton.setOnClickListener {
//                onMarkPaid(artist)
//            }
//        }
//    }
//
//    class ArtistDiffCallback : DiffUtil.ItemCallback<Artist>() {
//        override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
//            return oldItem == newItem
//        }
//    }
inner class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val nameTextView: TextView = itemView.findViewById(R.id.artist_name)
    private val balanceTextView: TextView = itemView.findViewById(R.id.artist_balance)
    private val markPaidButton: Button = itemView.findViewById(R.id.mark_paid_button)

    fun bind(artistWithUnpaid: ManagementViewModel.ArtistWithUnpaid) {
        nameTextView.text = "${artistWithUnpaid.artist.firstName} ${artistWithUnpaid.artist.lastName}"
        balanceTextView.text = "Невыплачено: ${artistWithUnpaid.unpaidAmount} руб. (${artistWithUnpaid.unpaidEarnings.size} заказов)"

        markPaidButton.setOnClickListener {
            onMarkPaid(artistWithUnpaid)
        }
    }
}

    class ArtistDiffCallback : DiffUtil.ItemCallback<ManagementViewModel.ArtistWithUnpaid>() {
        override fun areItemsTheSame(
            oldItem: ManagementViewModel.ArtistWithUnpaid,
            newItem: ManagementViewModel.ArtistWithUnpaid
        ): Boolean {
            return oldItem.artist.id == newItem.artist.id
        }

        override fun areContentsTheSame(
            oldItem: ManagementViewModel.ArtistWithUnpaid,
            newItem: ManagementViewModel.ArtistWithUnpaid
        ): Boolean {
            return oldItem == newItem
        }
    }
}