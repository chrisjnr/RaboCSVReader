package com.example.rabocsvreader.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rabocsvreader.databinding.ItemProfileViewBinding
import com.example.rabocsvreader.ui.models.Person

internal class ProfileAdapter(
    private val items: MutableList<Person> = mutableListOf()
) :
    RecyclerView.Adapter<ProfileAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemProfileViewBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size


    inner class Holder(private val binding: ItemProfileViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: Person) = with(binding) {
            Glide.with(root.context).load(person.avatar).into(
                ivUserImage
            )
            // TODO: format both
            tvDob.text = person.dob
            tvFullName.text = "${person.firstName} ${person.surname}"
            tvIssueNo.text = person.issueCount.toString()

        }
    }


    fun updateList(list: List<Person>) {
        val startIndex = items.size
        items.addAll(list)
        notifyItemRangeInserted(startIndex, list.size)
    }
}