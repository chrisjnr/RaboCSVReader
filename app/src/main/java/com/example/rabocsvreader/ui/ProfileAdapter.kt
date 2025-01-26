package com.example.rabocsvreader.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.utils.formatLocalDateToString
import com.example.rabocsvreader.R
import com.example.rabocsvreader.databinding.ItemProfileViewBinding
import com.example.rabocsvreader.ui.models.Person

internal class ProfileAdapter(
) : ListAdapter<Person, ProfileAdapter.Holder>(
        PersonItemDiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemProfileViewBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }



    inner class Holder(private val binding: ItemProfileViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: Person) = with(binding) {
            Glide.with(root.context).load(person.avatar).into(
                ivUserImage
            )
            tvDob.text = formatLocalDateToString(person.dob)
            tvFullName.text = root.context.getString(R.string.main_screen_full_name, person.firstName, person.surname)
            tvIssueNo.text = person.issueCount.toString()

        }
    }


    object PersonItemDiffCallback :
        DiffUtil.ItemCallback<Person>() {
        override fun areItemsTheSame(
            oldItem: Person,
            newItem: Person
        ): Boolean {
            return oldItem.firstName == newItem.firstName
                    && oldItem.surname == newItem.surname
                    && oldItem.dob == newItem.dob
                    && oldItem.avatar == newItem.avatar
                    && oldItem.issueCount == newItem.issueCount
        }

        override fun areContentsTheSame(
            oldItem: Person,
            newItem: Person
        ): Boolean {
            return oldItem.toString() == newItem.toString()
        }
    }
}