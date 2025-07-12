package com.example.crudapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudapp.data.model.User
import com.example.crudapp.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeUsers()

        binding.fabAdd.setOnClickListener {
            showUserDialog(null)
        }
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter(
            onEdit = { user -> showUserDialog(user) },
            onDelete = { user -> viewModel.deleteUser(user) }
        )
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter
    }

    private fun observeUsers() {
        viewModel.users.observe(this) { users ->
            adapter.submitList(users)
        }
    }

    private fun showUserDialog(user: User?) {
        val dialogBinding = com.example.crudapp.databinding.DialogUserBinding.inflate(layoutInflater)
        dialogBinding.etName.setText(user?.name ?: "")
        dialogBinding.etEmail.setText(user?.email ?: "")

        MaterialAlertDialogBuilder(this)
            .setTitle(if (user == null) "Add User" else "Edit User")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val name = dialogBinding.etName.text.toString().trim()
                val email = dialogBinding.etEmail.text.toString().trim()
                if (name.isNotEmpty() && email.isNotEmpty()) {
                    val newUser = User(
                        id = user?.id ?: "",
                        name = name,
                        email = email
                    )
                    if (user == null) {
                        viewModel.addUser(newUser)
                    } else {
                        viewModel.updateUser(newUser)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}