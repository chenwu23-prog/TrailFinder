package com.example.trailfinder

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trailfinder.data.repository.BadgeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.trailfinder.ui.profile.BadgeAdapter
import androidx.core.net.toUri

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var usernameEditText: EditText
    private lateinit var avatarImageView: ImageView
    private lateinit var saveButton: Button

    private lateinit var badgeRepository: BadgeRepository

    private lateinit var badgeAdapter: BadgeAdapter

    private val userId = 1

    private var currentUser: UserEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        usernameEditText = view.findViewById(R.id.profileUsername)
        avatarImageView = view.findViewById(R.id.profileAvatar)
        saveButton = view.findViewById(R.id.saveProfileButton)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = AppDatabase.getDatabase(requireContext())
        val userDao = db.userDao()

        viewLifecycleOwner.lifecycleScope.launch {
            // 🟢 Load existing user (if any)
            currentUser = withContext(Dispatchers.IO) {
                userDao.getUser()
            }

            // 🟢 If no user yet, create one-time guest record
            if (currentUser == null) {
                currentUser = UserEntity(username = "Guest")
                withContext(Dispatchers.IO) { userDao.insertUser(currentUser!!) }
            }

            // 🟢 Update UI
            usernameEditText.setText(currentUser?.username ?: "Guest")
            currentUser?.avatarUri?.let { avatarImageView.setImageURI(it.toUri()) }

            // 🟢 Save button handler
            saveButton.setOnClickListener {
                val updatedUser = currentUser?.copy(
                    username = usernameEditText.text.toString(),
                    avatarUri = currentUser?.avatarUri
                )

                if (updatedUser != null) {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        userDao.insertUser(updatedUser)
                    }

                    currentUser = updatedUser
                    Toast.makeText(requireContext(), "Profile saved!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            badgeAdapter = BadgeAdapter()

            val recycler = view.findViewById<RecyclerView>(R.id.badgeRecyclerView)
            recycler.layoutManager = LinearLayoutManager(requireContext())
            recycler.adapter = badgeAdapter

            viewLifecycleOwner.lifecycleScope.launch {
                val badges = badgeRepository.getUserBadges(userId)
                badgeAdapter.setItems(badges)
            }
        }
    }
}